var slideIndex = 1;

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
            success: (albumData) => {
                    addAlbum(albumData, path)
                }
            });
}

async function addAlbum(albumData, path) {
    for (i=0; i<albumData.length; i++) {
        await displayGrid(i, albumData[i], path);
        await displaySlides(albumData[i], path);
    }
    showSlides(slideIndex);
}

async function displayGrid(i, url, path) {
    var img1 = document.createElement("img");
    img1.src = path + url;
    img1.classList.add("hover-shadow");
    img1.addEventListener('click', openModal);
    img1.addEventListener('click', () => {
        currentSlide(i+1)
    });
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

async function displaySlides(url, path) {
    var lightBox = document.getElementById("lightbox-modal");
    var mySlidesDiv = document.createElement("div");
    mySlidesDiv.classList.add("mySlides");
    var img1 = document.createElement("img");
    img1.setAttribute('id', "img-" + (i+1));
    img1.classList.add("center-block");
    img1.src = path + url;
    mySlidesDiv.appendChild(img1);
    lightBox.appendChild(mySlidesDiv);
}


// Open the Modal
function openModal() {
  document.getElementById("myModal").style.display = "block";
}

// Close the Modal
function closeModal() {
  document.getElementById("myModal").style.display = "none";
}



// Next/previous controls
function plusSlides(n) {
  showSlides(slideIndex += n);
}

// Thumbnail image controls
function currentSlide(n) {
  showSlides(slideIndex = n);
}

function showSlides(n) {
  var i;
  var slides = document.getElementsByClassName("mySlides");
  var captionText = document.getElementById("caption");
  if (n > slides.length) {slideIndex = 1}
  if (n < 1) {slideIndex = slides.length}
  for (i = 0; i < slides.length; i++) {
    slides[i].style.display = "none";
  }
  console.log(slides)
  slides[slideIndex-1].style.display = "block";

}