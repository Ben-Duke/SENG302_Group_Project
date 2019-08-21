/**
 * Function to search for albums.
 * Updates the rows of photos with album titles matching the search term
 */
function searchAlbum(){
    // Declare variables
    var input, filter, ul, li, a, i, txtValue;
    input = document.getElementById('searchAlbums');
    filter = input.value.toUpperCase();
    ul = document.getElementById("albumList");
    li = ul.getElementsByClassName('album');
    console.log(li.length);

    // Loop through all list items, and hide those who don't match the search query
    for (i = 0; i < li.length; i++) {
        a = li[i].getElementsByClassName("panel-default")[0].getElementsByClassName("panel-heading")[0].getElementsByClassName("panel-title")[0];
        txtValue = a.textContent || a.innerText;
        if (txtValue.toUpperCase().indexOf(filter) > -1) {
            li[i].style.display = "";
        } else {
            li[i].style.display = "none";
        }
    }
}

$('#photo-upload').click(function (eve){
    let searchBar = document.getElementById("album-search-photo");
    let privateInput = document.getElementById("private").checked;
    let filePath = document.getElementById("photoUpload");
    if (searchBar.value === null || searchBar.value === "") {
        document.getElementById("photoAlbumMessage").style.display = "block";
    } else {
        var formData = new FormData();
        formData.append('picture', filePath.files[0]);
        formData.append('private', privateInput);
        formData.append('album', searchBar.value);
        var token = $('input[name="csrfToken"]').attr('value');
        $.ajaxSetup({
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Csrf-Token', token);
            }
        });
        $.ajax({
            type: 'POST',
            processData: false,
            contentType: false,
            url: '/users/home/photo',
            data: formData,
            success: function (data, textStatus, xhr) {
                if (xhr.status == 200) {
                    window.location = '/users/albums'
                } else {
                    window.location = '/users/albums'
                }
            }
        })
    }
});
