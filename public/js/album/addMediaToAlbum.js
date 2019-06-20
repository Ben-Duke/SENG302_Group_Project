function addMediaToAlbum(url, mediaIds) {

    console.log(mediaIds);

    var token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });

    $.ajax({
        url: url,
        method: "PUT",
        data: JSON.stringify({
            mediaIds: mediaIds
        }),
        contentType: 'application/json',
        success: function(res) {
            console.log("Success!");
        }
    });

}