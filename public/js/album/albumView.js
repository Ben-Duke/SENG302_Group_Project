var slideIndex = 1;
var albumData = null;

function setProfilePicture() {

}

function deletePhoto(mediaId) {

}

function changePrivacy(userId, albumId, isOwner) {
    if(isOwner) {hidePrivate = false;}
    else {hidePrivate = true}
    $.ajax({
            type: 'GET',
            url: '/users/albums/get/' + hidePrivate + '/' + albumId,
            contentType: 'application/json',
            success: (albumData) => {
                    var mediaId = albumData[slideIndex-1]["mediaId"];
                    if(albumData[slideIndex-1]["isMediaPublic"]==true) {setPrivacy=0;}
                    else {setPrivacy=1;}
                    $.ajax({
                           type: 'GET',
                           url: '/users/home/photoPrivacy/' + mediaId + '/' + setPrivacy,
                           contentType: 'application/json',
                           success: () => {
                                if(setPrivacy==0) {document.getElementById("privacyBtn").innerHTML = "Make Private";}
                                else if(setPrivacy==1) {document.getElementById("privacyBtn").innerHTML = "Make Public";}
                           },
                           error: () => {
                                if(setPrivacy==1) {document.getElementById("privacyBtn").innerHTML = "Make Private";}
                                else if(setPrivacy==0) {document.getElementById("privacyBtn").innerHTML = "Make Public";}
                           }

                    });
            }
    });
}

function linkToDestination(mediaId) {

}

/**
 * Function to search for albums.
 * Updates the rows of photos with album titles matching the search term
 */
function getAlbum(userId, albumId, isOwner){
    // Declare variables
    var col1, col2, col3, col4, path, hidePrivate;
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
                    addAlbum(albumData)
                }
            });
}

[{"mediaId":1,"url":"card.PNG","isMediaPublic":true,"isProfile":false,"profile":false,"isProfilePhoto":false,"unusedUserPhotoFileName":"1_card.PNG","urlWithPath":"C:\\Users\\Priyesh\\IdeaProjects\\team-800-newnull1/card.PNG","isPublic":true,"mediaPublic":true},{"mediaId":2,"url":"Capture.PNG","isMediaPublic":false,"isProfile":false,"profile":false,"isProfilePhoto":false,"unusedUserPhotoFileName":"1_Capture.PNG","urlWithPath":"C:\\Users\\Priyesh\\IdeaProjects\\team-800-newnull1/Capture.PNG","isPublic":false,"mediaPublic":false},{"mediaId":3,"url":"1_elegant-christmas-background_23-2147722745.jpg","isMediaPublic":true,"isProfile":false,"profile":false,"isProfilePhoto":false,"unusedUserPhotoFileName":"1_1_elegant-christmas-background_23-2147722745.jpg","urlWithPath":"C:\\Users\\Priyesh\\IdeaProjects\\team-800-newnull1/1_elegant-christmas-background_23-2147722745.jpg","isPublic":true,"mediaPublic":true},{"mediaId":4,"url":"1_shop-grand-opening-poster.jpg","isMediaPublic":true,"isProfile":false,"profile":false,"isProfilePhoto":false,"unusedUserPhotoFileName":"1_1_shop-grand-opening-poster.jpg","urlWithPath":"C:\\Users\\Priyesh\\IdeaProjects\\team-800-newnull2/1_shop-grand-opening-poster.jpg","isPublic":true,"mediaPublic":true},{"mediaId":5,"url":"1_InvalidCountryBug.png","isMediaPublic":true,"isProfile":false,"profile":false,"isProfilePhoto":false,"unusedUserPhotoFileName":"1_1_InvalidCountryBug.png","urlWithPath":"C:\\Users\\Priyesh\\IdeaProjects\\team-800-newnull2/1_InvalidCountryBug.png","isPublic":true,"mediaPublic":true}]

async function addAlbum(albumData) {
    path = "/users/home/servePicture/";
    for (i=0; i<albumData.length; i++) {
        await displayGrid(i, albumData, path);
        await displaySlides(i, albumData, path);
    }
    showSlides(slideIndex);
}

async function displayGrid(i, albumData, path) {
    var url = albumData[i]["urlWithPath"];
    var img1 = document.createElement("img");
    img1.src = path + encodeURIComponent(url);
    img1.setAttribute("data-id", i);
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

async function displaySlides(i, albumData, path) {
    var url = albumData[i]["urlWithPath"];
    var mediaId = albumData[i]["mediaId"];
    var lightBox = document.getElementById("lightbox-modal");
    var mySlidesDiv = document.createElement("div");
    mySlidesDiv.classList.add("mySlides");
    mySlidesDiv.setAttribute("data-privacy", albumData[i]["isMediaPublic"]);
    mySlidesDiv.setAttribute("data-mediaId", mediaId);
    var img1 = document.createElement("img");
    img1.setAttribute("id", "img"+(i+1));
    img1.classList.add("center-block");
    img1.src = path + encodeURIComponent(url);
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
  slides[slideIndex-1].style.display = "block";
  if(slides[slideIndex-1].getAttribute("data-privacy") == true) {
    document.getElementById("privacyBtn").innerHTML = "Make Public";
  } else {
    document.getElementById("privacyBtn").innerHTML = "Make Private";
  }

}