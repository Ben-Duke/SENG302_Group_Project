var user;

$(document).keydown('undo_redo', function(e) {
    if ((e.ctrlKey && e.keyCode === 90) && e.shiftKey !== true) {
        undoRedoRequest("undo")
    } else if (e.ctrlKey && e.shiftKey && e.keyCode === 90) {
        undoRedoRequest("redo")
    };
});

$('#undoButton').click( function(e) {
    undoRedoRequest("undo")
});

$('#redoButton').click( function(e) {
    undoRedoRequest("redo")
});

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

