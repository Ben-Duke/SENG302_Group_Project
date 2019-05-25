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
            location.reload();
        },
        error: function(xhr, textStatus, errorThrown){
            console.log(xhr.status + " " + textStatus + " " + errorThrown);
        }
    })
};

