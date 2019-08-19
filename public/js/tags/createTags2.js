/**
 * Exists to provide a second trip editor component when there needs to be two on the same page.
 * This means that if createTags.js is edited, this file also needs to be changed accordingly.
 * It's a pretty big band aid fix but there's no workaround unless we re-engineered the reusable component to
 * work with classes instead of IDs. I had a go at that but it got confusing very fast - Gavin
 */
var toAddTagList2 = new Set();
// let taggableId;
// let taggableType;


initialise2();

function initialise2() {
    addTagAddTagListeners2();
    updateExistingTagLabels2();
}


/**
 * Adds the tags that belong to the tagged item to labels
 */
function updateExistingTagLabels2() {
    let dataset = document.getElementById('tag-list2').dataset;
    let taggableType = dataset.taggabletype;
    let taggableId = dataset.taggableid;
    removeExistingTagLabels2();
    if (taggableId !== "") {
        sendGetTagsRequest2(taggableId, taggableType);
    }
}

/**
 * Hides the tag editor
 */
function hideTagEditor2() {
    document.getElementById("tag-container2").style.visibility = 'hidden';
}

/**
 * Shows the tag editor
 */
function showTagEditor2() {
    document.getElementById("tag-container2").style.visibility = 'visible';
}

/**
 * Removes all existing tag labels in preparation for refreshing them
 */
function removeExistingTagLabels2() {
    toAddTagList2 = new Set();
    const tagList = document.getElementById("tag-line2");
    const tagInput = document.getElementById('tag-add2');
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
function sendGetTagsRequest2(taggableId, taggableType) {
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
            addTagLabel2(tag.name, taggableId, taggableType)
        }
    }).fail((xhr, textStatus, errorThrown) => {
        console.log(xhr.status + " " + textStatus + " " + errorThrown);
    });
}

/**
 * Adds listeners to the search bar to search the database and redirect to tags page if needed
 */
function addTagAddTagListeners2() {
    const addInput = document.getElementById("tag-add2");
    const addList = document.getElementById("tag-add-results2");
    document.getElementById('tag-list2').addEventListener('tagChange', updateExistingTagLabels2);
    addInput.addEventListener('input', (e) => {

        if (e.constructor.name !== 'InputEvent') {
            // then this is a selection, not user input
            addTag2(addInput.value);

        } else if (e.data === ' ' || e.data === '"') {
            extractAndAddTag2(addInput.value)
        }
        while (addList.firstChild) {
            addList.removeChild(addList.firstChild);
        }
        const query = addInput.value;
        if (query) {
            searchAddTags2(query);
        }
    });
    addInput.addEventListener('keyup', e => {
        if (e.key === 'Enter') {
            extractAndAddTag2(addInput.value);
        }
    });
}

/**
 * Extracts the tag name from user input and adds the tag to the database
 * @param inputVal the user inputted data
 */
function extractAndAddTag2(inputVal) {
    inputVal = inputVal.trim(); //Take off white space

    let numOfQuotes = (inputVal.match(/"/g) || []).length;

    if (numOfQuotes === 0) {
        addTag2(inputVal);
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

        addTag2(tag);
    }
}

/**
 * Adds a tag to the database
 * @param name the name of the tag
 */
function addTag2(name) {

    let dataset = document.getElementById('tag-list2').dataset;
    let taggableType = dataset.taggabletype;
    let taggableId = dataset.taggableid;
    sendAddTagRequest2(name, taggableId, taggableType);
}

/**
 * Adds a tag label to the display
 * @param name the name of the tag being displayed
 */
function addTagLabel2(name, taggableId, taggableType) {
    let tagList = document.getElementById("tag-line2");
    let newTag = document.createElement("span");
    let newText = document.createElement("span");
    let newRemove = document.createElement("a");
    let newIcon = document.createElement("i");
    let input = document.getElementById("tag-add2");
    input.value = "";

    newTag.className = "tag label label-info";
    newTag.id = name;
    newText.innerHTML = name;
    newIcon.className = "remove glyphicon glyphicon-remove-sign glyphicon-white";
    newRemove.onclick = function() {
        tagList.removeChild(newTag);
        toAddTagList2.delete(name.toLowerCase());
        removeTagFromItem2(name, taggableId, taggableType);
    };
    newRemove.appendChild(newIcon);
    newTag.appendChild(newText);
    newTag.appendChild(newRemove);
    if(!toAddTagList2.has(name.toLowerCase())) {
        toAddTagList2.add(name.toLowerCase());
        tagList.appendChild(newTag);
    }
}

/**
 * Removes a tag from the item. If there is no item it clears all of the pending tags of a user
 * @param name the name of the tag to clear
 */
function removeTagFromItem2(name, taggableId, taggableType) {
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
function sendAddTagRequest2(name, taggableId, taggableType) {
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
        addTagLabel2(name, taggableType, taggableId)
    }).fail((xhr, textStatus, errorThrown) => {
        console.log(xhr.status + " " + textStatus + " " + errorThrown);
    });
}

/**
 * Search the database for the tag and if it succeeds adds these tags to the datalist on the search bar
 * @param query the tag to search for
 */
function searchAddTags2(query) {
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
        addTagsToAddList2(result)
    }).fail((xhr, textStatus, errorThrown) => {
        console.log(xhr.status + " " + textStatus + " " + errorThrown);
});
}



/**
 * Adds a list of tags as the datalist for the search bar
 * @param result the result of the http request to find tags
 */
function addTagsToAddList2(result) {
    const list = document.getElementById('tag-add-results2');
    for (let tag of result) {
        const tagSelection = document.createElement('OPTION');
        tagSelection.text = tag.name;

        list.appendChild(tagSelection);
    }
}