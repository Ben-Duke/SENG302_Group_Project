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
                    img1.setAttribute('id', i+1);
                    img1.src = path + albumData[i];
                    img1.class = "hover-shadow";
                    img1.addEventListener('click', openModal);
                    img1.addEventListener('click', currentSlide(i+1));
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

// MDB Lightbox Init
$(function () {
$("#mdb-lightbox-ui").load("mdb-addons/mdb-lightbox-ui.html");
});


// Open the Modal
function openModal() {
  document.getElementById("myModal").style.display = "block";
}

// Close the Modal
function closeModal() {
  document.getElementById("myModal").style.display = "none";
}

var slideIndex = 1;
showSlides(slideIndex);

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
  var dots = document.getElementsByClassName("demo");
  var captionText = document.getElementById("caption");
  if (n > slides.length) {slideIndex = 1}
  if (n < 1) {slideIndex = slides.length}
  for (i = 0; i < slides.length; i++) {
    slides[i].style.display = "none";
  }
  for (i = 0; i < dots.length; i++) {
    dots[i].className = dots[i].className.replace(" active", "");
  }
  slides[slideIndex-1].style.display = "block";
  dots[slideIndex-1].className += " active";
  captionText.innerHTML = dots[slideIndex-1].alt;
}