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

function getPhotoCaption(photoId) {
    $.ajax({
        type: 'GET',
        url: '/users/photos/'+ photoId +'/caption',
        success:function(res){
            const captionInput = document.getElementById("captionInput-" + photoId);

            captionInput.value = res;
        },
        error: function(xhr, textStatus, errorThrown){
            console.log(xhr.status + " " + textStatus + " " + errorThrown);

        }
    })
}

/**
 * Used to edit a caption, shows all button required to edit a caption,
 * calls getPhotoCaption to get the caption for the photo so the user
 * can edit it.
 * @param photoId
 */
function editCaption(photoId) {
    getPhotoCaption(photoId);
    const captionInput = document.getElementById("captionInput-" + photoId);
    const saveButton = document.getElementById("cancelCaptionButton-" + photoId);
    const cancelButton = document.getElementById("saveCaptionButton-" + photoId);
    const editButton = document.getElementById("editCaptionButton-" + photoId);

    toggleDisplay(captionInput);
    toggleDisplay(saveButton);
    toggleDisplay(cancelButton);
    toggleDisplay(editButton);
}

/**
 * Hides the caption edit inputs and brings the edit caption button back
 * @param photoId
 */
function cancelEditCaption(photoId) {
    const captionInput = document.getElementById("captionInput-" + photoId);
    const saveButton = document.getElementById("cancelCaptionButton-" + photoId);
    const cancelButton = document.getElementById("saveCaptionButton-" + photoId);
    const editButton = document.getElementById("editCaptionButton-" + photoId);

    toggleDisplay(captionInput);
    toggleDisplay(saveButton);
    toggleDisplay(cancelButton);
    toggleDisplay(editButton);
}

function submitEditCaption(photoId) {
    const captionInput = document.getElementById("captionInput-" + photoId);
    const captionInputText = captionInput.value;

    $.ajax({
        type: 'PUT',
        url: '/users/photos/'+ photoId +'/caption',
        data: JSON.stringify({
            caption: captionInputText
        }),
        headers: {
            'Content-Type': 'application/json'
        },
        success:function(){
            document.getElementById("caption-" + photoId).innerText = captionInputText;
            cancelEditCaption(photoId);
        },
        error: function(xhr, textStatus, errorThrown){
            console.log(xhr.status + " " + textStatus + " " + errorThrown);
        }
    })



}
