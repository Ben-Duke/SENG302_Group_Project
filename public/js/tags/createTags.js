var user;
var toAddTagList = new Array(new Set(), new Set());
// let taggableId;
// let taggableType;


initialise();

function initialise() {
    addTagAddTagListeners();
    updateExistingTagLabels();
}


/**
 * Adds the tags that belong to the tagged item to labels
 */
function updateExistingTagLabels() {
    let tagLists = document.getElementsByClassName('tag-list');
    for (let i = 0; i < tagLists.length; i++) {
        let dataset = tagLists[i].dataset;
        let taggableType = dataset.taggabletype;
        let taggableId = dataset.taggableid;
        removeExistingTagLabels();
        if (taggableId !== "") {
            sendGetTagsRequest(taggableId, taggableType);
        }
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
 * Removes all existing tag labels in preparation for refreshing them
 */
function removeExistingTagLabels() {
    toAddTagList[0] = new Set();
    toAddTagList[1] = new Set();
    const tagList = document.getElementsByClassName("tag-line");
    const tagInput = document.getElementsByClassName('tag-add');
    for (let i = 0; i < tagList.length; i++) {
        while (tagList[i].firstChild) {
            tagList[i].removeChild(tagList[i].firstChild)
        }
        if(tagInput[i] != null) {
            tagList[i].appendChild(tagInput[i]);
        }
    }
}

function clearTagCreator() {
    // const tagList = document.getElementsByClassName("tag-list");
    // tagList.setAttribute("data-taggableId", null);
    // tagList.dispatchEvent(new Event('tagChange'));
}

/**
 * Gets all tags for a given item and adds them to the display
 * @param taggableId the id of the item
 * @param taggableType the type of the item
 */
function sendGetTagsRequest(taggableId, taggableType) {
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
    document.getElementsByClassName('tag-list')[0].addEventListener('tagChange', updateExistingTagLabels);

    const addInput = document.getElementsByClassName("tag-add");
    const addList = document.getElementsByClassName("tag-add-results");
    for (let i = 0; i < addInput.length; i++) {
        addInput[i].addEventListener('input', (e) => {

            if (e.constructor.name !== 'InputEvent') {
                // then this is a selection, not user input
                addTag(addInput[i].value);

            } else if (e.data === ' ' || e.data === '"') {
                extractAndAddTag(addInput[i].value)
            }
            while (addList[i].firstChild) {
                addList[i].removeChild(addList[i].firstChild);
            }
            const query = addInput[i].value;
            if (query) {
                searchAddTags(query);
            }
        });
        addInput[i].addEventListener('keyup', e => {
            if (e.key === 'Enter') {
                extractAndAddTag(addInput[i].value);
            }
        });
    }
}

/**
 * Extracts the tag name from user input and adds the tag to the database
 * @param inputVal the user inputted data
 */
function extractAndAddTag(inputVal) {
    inputVal = inputVal.trim(); //Take off white space

    let numOfQuotes = (inputVal.match(/"/g) || []).length;

    if (numOfQuotes === 0) {
        addTag(inputVal);
    } else if (numOfQuotes === 2) {
        inputVal = inputVal.replace(/"/g, ""); //Takes of quotes

        console.log(inputVal);

        let words = inputVal.split(" ");


        let tag = "";
        for (let i in words) {
            console.log(words[i]);
            tag += words[i] + ' ';
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

    let dataset = document.getElementsByClassName('tag-list')[0].dataset;
    let taggableType = dataset.taggabletype;
    let taggableId = dataset.taggableid;
    sendAddTagRequest(name, taggableType, taggableId);
}

/**
 * Adds a tag label to the display
 * @param name the name of the tag being displayed
 */
function addTagLabel(name) {
    let tagList = document.getElementsByClassName("tag-line");
    let newTag = document.createElement("span");
    let newText = document.createElement("span");
    let newRemove = document.createElement("a");
    let newIcon = document.createElement("i");
    let input = document.getElementsByClassName("tag-add");
    for (let i = 0; i < input.length; i++) {
        input[i].value = "";

        newTag.className = "tag label label-info";
        newTag.id = name;
        newText.innerHTML = name;
        newIcon.className = "remove glyphicon glyphicon-remove-sign glyphicon-white";
        newRemove.onclick = function () {
            tagList[i].removeChild(newTag);
            toAddTagList[i].delete(name.toLowerCase());
            removeTagFromItem(name);
        };
        newRemove.appendChild(newIcon);
        newTag.appendChild(newText);
        newTag.appendChild(newRemove);
        if (!toAddTagList[i].has(name.toLowerCase())) {
            toAddTagList[i].add(name.toLowerCase());
            tagList[i]  .appendChild(newTag);
        }
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