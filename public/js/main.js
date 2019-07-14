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
    var x = document.getElementById("snackbar");

    // Add the "show" class to DIV
    x.className = "show";
    x.innerText = message + " " + action;
    // After 3 seconds, remove the show class from DIV
    setTimeout(function(){
        x.className = x.className.replace("show", "");
    }, 3000);
};

/**
 * Requests the photo caption and sets the captionInput to match that if it exists.
 * @param photoId the id of the photo to get the caption from
 */
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
 * Toggles the visibility of elements relating to captions.
 * Hides the edit caption button only if the caotion exists
 * @param photoId the id of the photo to toggle displays of
 */
function toggleCaptionDisplays(photoId) {
    const caption = document.getElementById("caption-" + photoId);
    const captionInput = document.getElementById("captionInput-" + photoId);
    const editCaptionBtn = document.getElementById("editCaptionButton-" + photoId);

    if (captionInput.value) {
        editCaptionBtn.style.display = 'none'
    } else {
        editCaptionBtn.style.display = 'block'
    }

    toggleDisplay(caption);
    toggleDisplay(captionInput);
}

/**
 * Used to edit a caption, shows all button required to edit a caption,
 * calls getPhotoCaption to get the caption for the photo so the user
 * can edit it.
 * @param photoId
 */
function editCaption(photoId) {
    getPhotoCaption(photoId);
    toggleCaptionDisplays(photoId);
    document.getElementById("captionInput-" + photoId).focus();
    document.getElementById("editCaptionButton-" + photoId).style.display = 'none';
}

/**
 * Sends a request to change the photo caption and toggles appropriate displays
 * @param caption the new caption
 * @param photoId the id of the photo to change the caption of
 */
function submitEditCaption(caption, photoId) {
    const errorMessage = document.getElementById(`lengthErrorMessage-${photoId}`);

    if (caption.length > 255) {
        errorMessage.style.display = 'block';
        return;
    }

    // Remove error message
    errorMessage.style.display = 'none';

    $.ajax({
        type: 'PUT',
        url: '/users/photos/'+ photoId +'/caption',
        data: JSON.stringify({
            caption: caption
        }),
        headers: {
            'Content-Type': 'application/json'
        },
        success:function(){
            document.getElementById("caption-" + photoId).innerText = caption;
            toggleCaptionDisplays(photoId);
        },
        error: function(xhr, textStatus, errorThrown){
            console.log(xhr.status + " " + textStatus + " " + errorThrown);
        }
    });
}
