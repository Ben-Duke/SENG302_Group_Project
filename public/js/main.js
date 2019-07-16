var user;

addTagSearchTagListeners();

function addTagSearchTagListeners() {
    try {
        const searchBar = document.getElementById("tag-search");
        const datalist = document.getElementById("tag-results");
        searchBar.addEventListener('input', () => {
            while (datalist.firstChild) {
                datalist.removeChild(datalist.firstChild);
            }
            const query = searchBar.value;
            if (query) {
                searchTags(query);
            }
        });
        searchBar.addEventListener('keydown', e => {
            if (e.key === 'Enter') {
                window.location.href = "/tags/" + searchBar.value
            }
        })
    } catch (err) {
        //do nothing. Just to avoid errors if the correct page is not loaded
    }
}

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

function addTagsToDataList(result) {
    const list = document.getElementById('tag-results');
    list.addEventListener('click', () => {
        console.log("yeet")
    })
    for (let tag of result) {
        const tagSelection = document.createElement('OPTION');
        tagSelection.value = tag.name;
        tagSelection.addEventListener('click', () => {
            console.log('haha yeet');
            console.log(tag)
        });
        list.appendChild(tagSelection);
        // const link = document.createElement("A");
        // link.href = "/tags/" + tag.tagId;
        // link.appendChild(document.createTextNode(tag.name));
        // list.appendChild(link);
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
    };
});

/**
 * Sets the onclick method for navigation bar undo button
 * Completes given undo request
 */
$('#undoButton').click( function(e) {
    undoRedoRequest("undo")
});

/**
 * Sets the onclick method for navigation bar redo button
 * Completes given redo request
 */
$('#redoButton').click( function(e) {
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
            sessionStorage.setItem(url, res);
            window.location.reload();
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
    var x = document.getElementById("snackbar");

    // Add the "show" class to DIV
    x.className = "show";
    x.innerText = message + " " + action;
    // After 3 seconds, remove the show class from DIV
    setTimeout(function(){
        x.className = x.className.replace("show", "");
    }, 3000);
};


