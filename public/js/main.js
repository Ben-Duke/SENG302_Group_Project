var user;

/**
 * Sets the global keyboard shortcuts for undo and redo
 * Completes given undo or redo request
 */
$(document).keydown('undo_redo', function(e) {
    if ((e.ctrlKey && e.keyCode === 90) && e.shiftKey !== true) {
        undoRedoRequest("undo")
    } else if (e.ctrlKey && e.shiftKey && e.keyCode === 90) {
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
    var token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function (xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
        type: 'PUT',
        url: '/' + url,
        success:function(res, textStatus, xhr){
            sessionStorage.reloadAfterPageLoad = url;
            window.location.reload();
        },
        error: function(xhr, textStatus, errorThrown){
            console.log(xhr.status + " " + textStatus + " " + errorThrown);
        }
    })
};


/**
 * Check if undo/redo request as been sent after location reload to show undo/redo message
 * Sets session storage to false after
 */
$(function () {
        if ( sessionStorage.reloadAfterPageLoad !== false) {
            if (sessionStorage.reloadAfterPageLoad === "undo") {
                showMessage("undone")
            } else if (sessionStorage.reloadAfterPageLoad === "redo") {
                showMessage("redone")
            }
            sessionStorage.reloadAfterPageLoad = false;
        }
    }
);

/**
 * Shows notification message for given undo/redo request
 * @param message Either Undo or Redo
 */
function showMessage(message) {
    var x = document.getElementById("snackbar");

    // Add the "show" class to DIV
    x.className = "show";
    x.innerText = message;
    console.log(x.innerText);
    // After 3 seconds, remove the show class from DIV
    setTimeout(function(){
        x.className = x.className.replace("show", "");
    }, 3000);
};

