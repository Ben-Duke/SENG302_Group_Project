var user;

addTagSearchTagListeners();

/**
 * Adds listeners to the search bar to search the database and redirect to tags page if needed
 */
function addTagSearchTagListeners() {
    function redirectToTagPage(tag, datalist) {
        for (let dataTag of datalist.options) {
            if (dataTag.value.toUpperCase() === tag.toUpperCase()) {
                window.location.href = "/tags/display/" + tag.toLowerCase();
            }
        }
    }

    try {
        const searchBar = document.getElementById("tag-search");
        const datalist = document.getElementById("tag-results");
        searchBar.addEventListener('input', (e) => {
            if (e.constructor.name !== 'InputEvent') {
                // then this is a selection, not user input
                redirectToTagPage(searchBar.value, datalist)
            }
            while (datalist.firstChild) {
                datalist.removeChild(datalist.firstChild);
            }
            const query = searchBar.value;
            if (query) {
                searchTags(query);
            }
        });
        searchBar.addEventListener('keyup', e => {
            if (e.key === 'Enter') {
                redirectToTagPage(searchBar.value, datalist);
            }
        })
    } catch (err) {
        //do nothing. Just to avoid errors if the correct page is not loaded
    }
}

/**
 * Search the database for the tag and if it succeeds adds these tags to the datalist on the search bar
 * @param query the tag to search for
 */
function searchTags(query) {
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
        addTagsToDataList(result)
    }).fail((xhr, textStatus, errorThrown) => {
        console.log(xhr.status + " " + textStatus + " " + errorThrown);
    });
}

/**
 * Adds a list of tags as the datalist for the search bar
 * @param result the result of the http request to find tags
 */
function addTagsToDataList(result) {
    const list = document.getElementById('tag-results');
    for (let tag of result) {
        const tagSelection = document.createElement('OPTION');
        tagSelection.text = tag.name;

        list.appendChild(tagSelection);
    }
}

/**
 * Sets the global keyboard shortcuts for undo and redo
 * Completes given undo or redo request
 */
$(document).keydown('undo_redo', function(e) {
    if (e.ctrlKey && e.keyCode === 90) {
        undoRedoRequest("undo")
    } else if (e.ctrlKey && e.keyCode === 89) {
        undoRedoRequest("redo")
    }
});

/**
 * Sets the onclick method for navigation bar undo button
 * Completes given undo request
 */
$('#undoButton').click( function() {
    undoRedoRequest("undo")
});

/**
 * Sets the onclick method for navigation bar redo button
 * Completes given redo request
 */
$('#redoButton').click( function() {
    undoRedoRequest("redo")
});


/**
 * Sends request to UndoRedoController which completes the given undo/redo request
 * @param url Either Undo or Redo
 */
function undoRedoRequest(url){
    $.ajax({
        type: 'PUT',
        url: '/' + url,

        success:function(res){
            if(res.status !== 204) {
                sessionStorage.setItem(url, res);
                window.location.reload();
            }
        },
        error: function(xhr, textStatus, errorThrown){
            console.log(xhr.status + " " + textStatus + " " + errorThrown);
        }
    })
}


/**
 * Check if undo/redo request as been sent after location reload to show undo/redo message
 * Sets session storage to false after
 */
$(function () {
    if (sessionStorage !== false) {
            if (sessionStorage.getItem("undo")) {
                showMessage(sessionStorage.getItem("undo"), "undone")
            } else if (sessionStorage.getItem("redo")) {
                showMessage(sessionStorage.getItem("redo"), "redone")
            }
            sessionStorage.clear()
        }
    }
);

/**
 * Shows notification message for given undo/redo request
 * @param message Either Undo or Redo
 */
function showMessage(message, action) {
    const x = document.getElementById("snackbar");

    // Add the "show" class to DIV
    x.className = "show";
    x.innerText = message + " " + action;
    // After 3 seconds, remove the show class from DIV
    setTimeout(function(){
        x.className = x.className.replace("show", "");
    }, 3000);
}
