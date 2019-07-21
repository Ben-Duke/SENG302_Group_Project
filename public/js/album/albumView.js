/**
 * Function to search for albums.
 * Updates the rows of photos with album titles matching the search term
 */
function getAlbum(userId, albumId, isOwner){
    // Declare variables
    var col1, col2, col3, col4, album, path, hidePrivate;
    path = "/assets/images/user_photos/user_" + userId + "/";
    col1 = document.getElementById('col1');
    col2 = document.getElementById('col2');
    col3 = document.getElementById('col3');
    col4 = document.getElementById('col4');
    if(isOwner) {hidePrivate = false;}
    else {hidePrivate = true}
    $.ajax({
            type: 'GET',
            url: '/users/albums/get/' + hidePrivate + '/' + albumId,
            contentType: 'application/json',
            success: function(albumData){
                for (i=0; i<albumData.length; i++) {
                    var img1 = document.createElement("img");
                    img1.src = path + albumData[i];
                    if (i%4==0) {
                        document.getElementById('col1').appendChild(img1);
                    } else if (i%4==1){
                        document.getElementById('col2').appendChild(img1);
                    } else if (i%4==2){
                        document.getElementById('col3').appendChild(img1);
                    } else if (i%4==3){
                        document.getElementById('col4').appendChild(img1);
                    }
                }
            }
    });
    console.log("done");
}

