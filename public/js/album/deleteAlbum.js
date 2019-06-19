
function deleteAlbum(url) {
    var token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });

    $.ajax({
        url: url,
        method: "DELETE",
        contentType: 'application/json',
        success: function(res) {
            console.log("Success!");
        }
    });

}