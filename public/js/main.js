var user;

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

addCaptionInputListeners();

/**
 * Adds listeners to each caption input such that when they lose focus the caption is saved
 */
function addCaptionInputListeners() {
    const captionInputs = document.getElementsByClassName("captionInput");
    for (let i=0; i < captionInputs.length; i++) {
        const input = captionInputs.item(i);
        input.addEventListener('blur', function() {
            const photoId = input.id.split("-")[1];
            const caption = input.value;
            submitEditCaption(caption, photoId)
        });
        input.addEventListener('keydown', function (e) {
            if (e.key === 'Enter') {
                input.blur();
            }
        });
    }
}

hideEditButtons();

/**
 * Hides the add caption buttons tha should not be displayed on load page.
 * The buttons are only shown if the caption is empty
 */
function hideEditButtons() {
    const editButtons = document.getElementsByClassName("captionButton");
    for (let i=0; i < editButtons.length; i++) {
        const button = editButtons.item(i);
        const photoId = button.id.split("-")[1];
        getPhotoCaption(photoId);
        const caption = document.getElementById("caption-" + photoId).innerText;
        if(caption){
            button.style.display = "none";
        }
    }
}


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
    const x = document.getElementById("snackbar");

    // Add the "show" class to DIV
    x.className = "show";
    x.innerText = message + " " + action;
    // After 3 seconds, remove the show class from DIV
    setTimeout(function(){
        x.className = x.className.replace("show", "");
    }, 3000);
}
