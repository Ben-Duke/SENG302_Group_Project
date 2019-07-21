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

