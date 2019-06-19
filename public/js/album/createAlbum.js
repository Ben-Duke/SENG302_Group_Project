
function createAlbum(url, title, mediaId) {

    var token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });

    $.ajax({
        url: "/users/albums/create",
        method: "POST",
        data: JSON.stringify({
            title: title,
            mediaId: mediaId
        }),
        contentType: 'application/json',
        success: function(res) {
            console.log("Success!");
        }
    });

}