function createAlbum(url, title, mediaId) {

    var token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });

    $.ajax({
        url: url,
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

function addOrRemoveMediaToAlbum(url, mediaIds) {

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