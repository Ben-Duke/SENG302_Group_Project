var user;
var toAddTagList = new Set();

addTagAddTagListeners();

/**
 * Adds listeners to the search bar to search the database and redirect to tags page if needed
 */
function addTagAddTagListeners() {
    function redirectToTagPage(tag, datalist) {
        for (let dataTag of datalist.options) {
            if (dataTag.value.toUpperCase() === tag.toUpperCase()) {
                window.location.href = "/tags/" + tag.toLowerCase();
            }
        }
    }

    try {
        const addInput = document.getElementById("tag-add");
        const addList = document.getElementById("tag-add-results");
        addInput.addEventListener('input', (e) => {

            if (e.constructor.name !== 'InputEvent') {
                // then this is a selection, not user input
                addTag(addInput.value);

            } else if (e.data === ' ' || e.data === '"') {
                extractAndAddTag(addInput.value)
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
                extractAndAddTag(addInput.value);
            }
        });

    } catch (err) {
        //do nothing. Just to avoid errors if the correct page is not loaded
    }
}

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

function addTag(name) {
    let dataset = document.getElementById('tag-list').dataset;
    let taggableType = dataset.taggabletype;
    let taggableId = dataset.taggableid;
    sendAddTagRequest(name, taggableType, taggableId);
}

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
    newText.innerHTML = name;
    newIcon.className = "remove glyphicon glyphicon-remove-sign glyphicon-white";
    newRemove.onclick = function() {
        tagList.removeChild(newTag);
        toAddTagList.delete(name.toLowerCase());
        removeTagFromItem(name);
    };
    newRemove.appendChild(newIcon);
    newTag.appendChild(newText);
    newTag.appendChild(newRemove);
    if(!toAddTagList.has(name.toLowerCase())) {
        toAddTagList.add(name.toLowerCase());
        tagList.appendChild(newTag);
    }
}

function removeTagFromItem(name) {
    let dataset = document.getElementById('tag-list').dataset;
    let taggableType = dataset.taggabletype;
    let taggableId = dataset.taggableid;
    if (taggableId !== "") {
        $.ajax({
            type: 'DELETE',
            url: `/tags/${taggableId}`,
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
    } else {

    }
}

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

function addExistingTag(name) {

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