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
            addTag(addInput.value);
        }
    })
    } catch (err) {
        //do nothing. Just to avoid errors if the correct page is not loaded
    }
}

function addTag(name) {
    let tagList = document.getElementById("tag-list");
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
     }
    newRemove.appendChild(newIcon);
    newTag.appendChild(newText);
    newTag.appendChild(newRemove);
    if(!toAddTagList.has(name.toLowerCase())) {
        toAddTagList.add(name.toLowerCase());
        tagList.appendChild(newTag);
    }

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