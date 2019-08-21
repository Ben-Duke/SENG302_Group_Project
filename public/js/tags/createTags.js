var user;
var toAddTagList = new Set();
let taggableId;
let taggableType;

initialise();

function initialise() {
    addTagAddTagListeners();
    initaliseTaggableIdAndTaggableType();
    updateExistingTagLabels();
}

/**
 * Initialises the TaggableId and TaggableType to the parameters set when the Tag Editor was constructed.
 */
function initaliseTaggableIdAndTaggableType() {
    let dataset = document.getElementById('tag-list').dataset;
    taggableType = dataset.taggabletype;
    taggableId = dataset.taggableid;
}

/**
 * Adds the tags that belong to the tagged item to labels
 */
function updateExistingTagLabels() {
    removeExistingTagLabels();
    if (taggableId !== "") {
        sendGetTagsRequest();
    }
}

/**
 * Hides the tag editor
 */
function hideTagEditor() {
    document.getElementById("tag-container").style.visibility = 'hidden';
}

/**
 * Shows the tag editor
 */
function showTagEditor() {
    document.getElementById("tag-container").style.visibility = 'visible';
}

/**
 * Sets the ID of the taggable model to be tagged by the tag editor
 */
function changeTaggableModel(newTaggableId, newTaggableType) {
    taggableId = newTaggableId;
    taggableType = newTaggableType;
    updateExistingTagLabels();
}

/**
 * Removes all existing tag labels in preparation for refreshing them
 */
function removeExistingTagLabels() {
    toAddTagList = new Set();
    const tagList = document.getElementById("tag-line");
    const tagInput = document.getElementById('tag-add');
    while (tagList.firstChild) {
        tagList.removeChild(tagList.firstChild)
    }
    tagList.appendChild(tagInput);
}

/**
 * Gets all tags for a given item and adds them to the display
 * @param taggableId the id of the item
 * @param taggableType the type of the item
 */
function sendGetTagsRequest() {
    const url = `/tags/get/${taggableId}`;
    $.ajax({
        type: 'PUT',
        url: url,
        data: JSON.stringify({
            taggableType: taggableType
        }),
        headers: {
            'Content-Type': 'application/json'
        }
    }).done((tags) => {
        for (let tag of tags) {
            addTagLabel(tag.name)
        }
    }).fail((xhr, textStatus, errorThrown) => {
        console.log(xhr.status + " " + textStatus + " " + errorThrown);
    });
}

/**
 * Adds listeners to the search bar to search the database and redirect to tags page if needed
 */
function addTagAddTagListeners() {
    const allowedQuotes = ['"', '\''];      // the characters which a multi-word tag can be enclosed in

    const addInput = document.getElementById("tag-add");
    const addList = document.getElementById("tag-add-results");
    document.getElementById('tag-list').addEventListener('tagChange', updateExistingTagLabels);
    addInput.addEventListener('input', (e) => {

        if (e.constructor.name !== 'InputEvent') {
            // then this is a selection, not user input
            addTag(addInput.value);

        } else if (e.data === ' ' || allowedQuotes.includes(e.data)) {
            extractAndAddTag(addInput.value, allowedQuotes)
        }
        while (addList.firstChild) {
            addList.removeChild(addList.firstChild);
        }
        const query = addInput.value;
        if (query) {
            searchAddTags(query);
        }
    });
    addInput.addEventListener('keyup', e => {
        if (e.key === 'Enter') {
            extractAndAddTag(addInput.value, allowedQuotes);
        }
    });
}

/**
 * Extracts the tag name from user input and adds the tag to the database
 * @param inputVal the user inputted data
 * @param allowedQuotes list of legal enclosing characters for a multi-word tag
 */
function extractAndAddTag(inputVal, allowedQuotes) {
    inputVal = inputVal.trim(); //Take off white space

    // Get type of quote used (first character
    const firstChar = inputVal[0];  // will be undefined for empty input

    if (!allowedQuotes.includes(firstChar)) { // one word tag
        addTag(inputVal);
    } else {    // starts with a quote
        // check if the input ends with the same quote it started with
        if (!inputVal.endsWith(firstChar)) {
            return  // enclosing quotes do not match
        }

        // remove quotes and add tag
        inputVal = inputVal.slice(1, inputVal.length - 1);  // remove the first and last chars (the quotes)

        let words = inputVal.split(" ");
        let tag = "";

        for (let word of words) {
            if (word) {
                tag += word + ' ';
            }
        }

        tag = tag.trim();
        addTag(tag);
    }
}

/**
 * Adds a tag to the database
 * @param name the name of the tag
 */
function addTag(name) {
    sendAddTagRequest(name, taggableType, taggableId);
}

/**
 * Adds a tag label to the display
 * @param name the name of the tag being displayed
 */
function addTagLabel(name) {
    let tagList = document.getElementById("tag-line");
    let newTag = document.createElement("span");
    let newText = document.createElement("span");
    let newRemove = document.createElement("a");
    let newIcon = document.createElement("i");
    let input = document.getElementById("tag-add");

    input.value = "";

    newTag.className = "tag label label-info";
    newTag.id = name;
    newText.innerHTML = `<a style="color: black">${name}</a>`;
    newIcon.className = "remove glyphicon glyphicon-remove-sign glyphicon-white";

    newRemove.onclick = function() {
        tagList.removeChild(newTag);
        toAddTagList.delete(name.toLowerCase());
        removeTagFromItem(name);
    };
    newText.onclick = function() {
        window.location.href = '/tags/display/' + name.toLowerCase();
    };
    newRemove.appendChild(newIcon);
    newTag.appendChild(newText);
    newTag.appendChild(newRemove);
    if(!toAddTagList.has(name.toLowerCase())) {
        toAddTagList.add(name.toLowerCase());
        tagList.appendChild(newTag);
    }
}

/**
 * Removes a tag from the item. If there is no item it clears all of the pending tags of a user
 * @param name the name of the tag to clear
 */
function removeTagFromItem(name) {
    let url;
    taggableId === "" ? url = '/tags' : url = `/tags/${taggableId}`;
    $.ajax({
        type: 'DELETE',
        url: url,
        data: JSON.stringify({
            tag: name,
            taggableType: taggableType
        }),
        headers: {
            'Content-Type': 'application/json'
        }
    }).fail((xhr, textStatus, errorThrown) => {
        console.log(xhr.status + " " + textStatus + " " + errorThrown);
    });

}

/**
 * Sends a request to add a tag to an item
 * @param name the name of the tag
 * @param taggableType the type of the item
 * @param taggableId the id of the item
 */
function sendAddTagRequest(name, taggableType, taggableId) {
    let url;
    taggableId === "" ? url = '/tags' :  url = `/tags/${taggableId}`;
    $.ajax({
        type: 'PUT',
        url: url,
        data: JSON.stringify({
            tag: name,
            taggableType: taggableType
        }),
        headers: {
            'Content-Type': 'application/json'
        }
    }).done(() => {
        addTagLabel(name)
    }).fail((xhr, textStatus, errorThrown) => {
        console.log(xhr.status + " " + textStatus + " " + errorThrown);
    });
}

/**
 * Search the database for the tag and if it succeeds adds these tags to the datalist on the search bar
 * @param query the tag to search for
 */
function searchAddTags(query) {
    $.ajax({
        type: 'PUT',
        url: '/tags/search',
        data: JSON.stringify({
            search: query
        }),
        headers: {
            'Content-Type': 'application/json'
        }
    }).done((result) => {
        addTagsToAddList(result)
    }).fail((xhr, textStatus, errorThrown) => {
        console.log(xhr.status + " " + textStatus + " " + errorThrown);
});
}

/**
 * Search the database for the tag and if it succeeds adds these tags to the datalist on the search bar
 * @param query the tag to search for
 */
function searchAddTags(query) {
    $.ajax({
        type: 'PUT',
        url: '/tags/search',
        data: JSON.stringify({
            search: query
        }),
        headers: {
            'Content-Type': 'application/json'
        }
    }).done((result) => {
        addTagsToAddList(result)
    }).fail((xhr, textStatus, errorThrown) => {
        console.log(xhr.status + " " + textStatus + " " + errorThrown);
});
}



/**
 * Adds a list of tags as the datalist for the search bar
 * @param result the result of the http request to find tags
 */
function addTagsToAddList(result) {
    const list = document.getElementById('tag-add-results');
    for (let tag of result) {
        const tagSelection = document.createElement('OPTION');
        tagSelection.text = tag.name;

        list.appendChild(tagSelection);
    }
}