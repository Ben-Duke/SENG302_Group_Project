var slideIndex = 1;
var currentSlideNo = 1;
var albumData = null;


function setDeletePhotoListener(albumData, i) {
    document.getElementById('deletePhotoBtn').addEventListener('click', () => {
        const mediaId = albumData[i]["mediaId"];
        deletePhotoRequest(mediaId);
    });
}

function setDestinationLinkListener(albumData, i) {
    function destinationLinkListener() {
        const mediaId = albumData[i]["mediaId"];
        openDestinationModal(mediaId);
    }
    const original = document.getElementById('linkDestinationBtn');
    const clone = original.cloneNode(true);
    original.parentNode.replaceChild(clone, original);
    clone.addEventListener('click', destinationLinkListener)
}

function setPrivacyListener(setPrivacy, mediaId) {
    const privacyBtn = document.getElementById('privacyBtn');
    privacyBtn.addEventListener('click', () => {
        if(setPrivacy) {
            privacyBtn.innerHTML = "Make Public";
            document.querySelector('div[data-mediaId="'+mediaId+'"]').setAttribute("data-privacy", false);
        }
        else {
            privacyBtn.innerHTML = "Make Private";
            document.querySelector('div[data-mediaId="'+mediaId+'"]').setAttribute("data-privacy", true);}
    })
}


function setSlideListeners(i) {
    const dataset = document.getElementById('myModal').dataset;
    const isOwner = dataset.isowner;
    const albumId = dataset.album;
    const hidePrivate = !isOwner;

    $.ajax({
        type: 'GET',
        url: '/users/albums/get/' + hidePrivate + '/' + albumId,
        contentType: 'application/json',
        success: (albumData) => {
            let setPrivacy;
            setDeletePhotoListener(albumData, i);
            setDestinationLinkListener(albumData, i);

            const mediaId = albumData[i]["mediaId"];

            if(albumData[i]["isMediaPublic"]) {setPrivacy=0;}
            else {setPrivacy=1;}

            $.ajax({
                type: 'GET',
                url: '/users/home/photoPrivacy/' + mediaId + '/' + setPrivacy,
                contentType: 'application/json',
                success: () => {
                    setPrivacyListener(setPrivacy, mediaId)
                }
            });
        }
    });
}

function setProfilePicture() {

}


/**
 * Function to search for albums.
 * Updates the rows of photos with album titles matching the search term
 */
function getAlbum(userId, albumId, isOwner){
    // Declare variables
    var hidePrivate;
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

//[{"mediaId":1,"url":"card.PNG","isMediaPublic":true,"isProfile":false,"profile":false,"isProfilePhoto":false,"unusedUserPhotoFileName":"1_card.PNG","urlWithPath":"C:\\Users\\Priyesh\\IdeaProjects\\team-800-newnull1/card.PNG","isPublic":true,"mediaPublic":true},{"mediaId":2,"url":"Capture.PNG","isMediaPublic":false,"isProfile":false,"profile":false,"isProfilePhoto":false,"unusedUserPhotoFileName":"1_Capture.PNG","urlWithPath":"C:\\Users\\Priyesh\\IdeaProjects\\team-800-newnull1/Capture.PNG","isPublic":false,"mediaPublic":false},{"mediaId":3,"url":"1_elegant-christmas-background_23-2147722745.jpg","isMediaPublic":true,"isProfile":false,"profile":false,"isProfilePhoto":false,"unusedUserPhotoFileName":"1_1_elegant-christmas-background_23-2147722745.jpg","urlWithPath":"C:\\Users\\Priyesh\\IdeaProjects\\team-800-newnull1/1_elegant-christmas-background_23-2147722745.jpg","isPublic":true,"mediaPublic":true},{"mediaId":4,"url":"1_shop-grand-opening-poster.jpg","isMediaPublic":true,"isProfile":false,"profile":false,"isProfilePhoto":false,"unusedUserPhotoFileName":"1_1_shop-grand-opening-poster.jpg","urlWithPath":"C:\\Users\\Priyesh\\IdeaProjects\\team-800-newnull2/1_shop-grand-opening-poster.jpg","isPublic":true,"mediaPublic":true},{"mediaId":5,"url":"1_InvalidCountryBug.png","isMediaPublic":true,"isProfile":false,"profile":false,"isProfilePhoto":false,"unusedUserPhotoFileName":"1_1_InvalidCountryBug.png","urlWithPath":"C:\\Users\\Priyesh\\IdeaProjects\\team-800-newnull2/1_InvalidCountryBug.png","isPublic":true,"mediaPublic":true}]

async function addAlbum(albumData) {
    var path = "/users/home/servePicture/";
    for (let i=0; i<albumData.length; i++) {
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
    img1.setAttribute("data-mediaId", albumData[i]["mediaId"]);
    img1.classList.add("hover-shadow");
    img1.addEventListener('click', () => {
        openModal();
        currentSlide(i+1);
        setSlideListeners(i)
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

function openDestinationModal(mediaId) {
    console.log("dest modal opened");
    // document.getElementById('destination-modal').style.display="block";
    getDestData(mediaId);
}

function getDestData(mediaId) {
    $.ajax({
        type: 'GET',
        url: '/users/destinations/getalljson',
        contentType: 'application/json',
        success: (destData) => {
            loadDestTable(destData, mediaId)
        }
    });
}

function loadDestTable(destData, mediaId) {
    for (let destination of destData) {
        const publicTable = document.getElementById('public-dest-tbody');
        const privateTable = doucment.getElementById('private-dest-tbody');
        if (destination.isPublic) {
            addDestRow(publicTable, destination, mediaId);
        } else {
            addDestRow(privateTable, destination, mediaId)
        }
    }
}

function addDestRow(table, destination, mediaId) {
    const row = document.createElement("TR");

    const name = document.createElement("TH");
    name.setAttribute('scope', 'row');
    name.innerText = destination.destName;
    row.appendChild(name);

    const type = document.createElement("TD");
    type.innerText = destination.destType;
    row.appendChild(type);

    const country = document.createElement("TD");
    country.innerText = destination.country;
    row.appendChild(country);

    const district = document.createElement("TD");
    district.innerText = destination.district;
    row.appendChild(district);

    const linkButton = document.createElement('BUTTON');
    linkButton.setAttribute('class', 'btn btn-primary');
    linkButton.innerText = 'Link to destination';
    linkButton.addEventListener('click', linkDestination(destination.id, mediaId));

    const unlinkButton = document.createElement('BUTTON');
    unlinkButton.setAttribute('class', 'btn btn-danger');
    unlinkButton.innerText = 'Unlink from destination';
    unlinkButton.addEventListener('click', unlinkDestination(destination.id, mediaId));

    const div = document.createElement('DIV');
    div.appendChild(linkButton);
    div.appendChild(unlinkButton);
    row.appendChild(div);
}


function linkDestination(destId, mediaId) {
    console.log("linked " + destId + " to " + mediaId);
}

function unlinkDestination(destId, mediaId) {
    console.log("unlinked " + destId + " from " + mediaId);
}

// Next/previous controls
function plusSlides(n) {
    setSlideListeners(slideIndex);
    showSlides(slideIndex += n);
}

// Thumbnail image controls
function currentSlide(n) {
  showSlides(slideIndex = n);
}

function showSlides(n) {
  var i;
  var slides = document.getElementsByClassName("mySlides");
//  var captionText = document.getElementById("caption");
  if (n > slides.length) {slideIndex = 1}
  if (n < 1) {slideIndex = slides.length}
  for (i = 0; i < slides.length; i++) {
    slides[i].style.display = "none";
  }
  slides[slideIndex-1].style.display = "block";
  if(slides[slideIndex-1].getAttribute("data-privacy") == "true") {
    document.getElementById("privacyBtn").innerHTML = "Make Private";
  } else if (slides[slideIndex-1].getAttribute("data-privacy") == "false"){
    document.getElementById("privacyBtn").innerHTML = "Make Public";
  }

}



/**
 * New set profile picture from existing picture function. Makes use of the cropper.
 * @param photoId
 */
function setProfilePictureRequest(photoId){
    $("#myModal").modal('hide');
    let isExistingPhoto = true;
    let photoIdToEdit = photoId;
    $('#addProfilePhoto').modal('show');
}


/**
 *
 */
$('#addProfilePhoto').on('show.bs.modal', function (e) {
    if(isExistingPhoto == true){
        $("#myModal").hide();
        var output = document.getElementById('change-profile-pic');
        output.src = "/users/home/serveDestPicture/" + photoIdToEdit;
        $("#selectProfileInput").hide();
    }
});

/**
 *
 */
$('#addProfilePhoto').on('shown.bs.modal', function (e) {
    var isExistingPhoto;
    if(isExistingPhoto == true){
        isExistingPhoto = false;
        $.ajax({
            type: 'GET',
            url: '/users/photos/' + photoIdToEdit,
            success: function(data){
                let filename = data["url"];
                $('#change-profile-pic').cropper("destroy");

                var $previews = $('.preview');
                $('#change-profile-pic').cropper({
                    movable: false,
                    autoCropArea: 1,
                    aspectRatio: 1,
                    ready: function(e){

                        //DO NOT DELETE THIS SET TIMEOUT
                        // setTimeout(function(){
                        //     $('#change-profile-pic').cropper('crop');
                        //     croppedCanvas = $('#change-profile-pic').cropper('getCroppedCanvas');
                        // }, 1);

                        let cropBoxElements = document.getElementsByClassName('cropper-face cropper-move');
                        let cropBoxElement = cropBoxElements[0];
                        let cropBoxMoveEvent = new Event('crop');
                        cropBoxElement.dispatchEvent(cropBoxMoveEvent);

                        },
                    crop: function (e) {
                        var imageData = $(this).cropper('getImageData');
                        var croppedCanvas = $(this).cropper('getCroppedCanvas');
                        $('.preview').html('<img src="' + croppedCanvas.toDataURL() + '" class="thumb-lg img-circle" style="width:100px;height:100px;">');
                        var previewAspectRatio = e.width / e.height;
                        $previews.each(function (){
                            var $preview = $(this);
                            var previewWidth = $preview.width();
                            var previewHeight = previewWidth / previewAspectRatio;
                            var imageScaledRatio = e.width / previewWidth;
                            $preview.height(previewHeight).find('img').css({
                                width: imageData.naturalWidth / imageScaledRatio,
                                height: imageData.naturalHeight / imageScaledRatio,
                                marginLeft: -e.x / imageScaledRatio,
                                marginTop: -e.y / imageScaledRatio
                            });
                        });
                    }
                });
            }
        });
    }
});


/**
 * This function is called when the user clicks the upload button to upload the cropped canvas image to the database.
 * Sends an AJAX post request to the backend with the photo's information to store the photo within the database.
 * The cropped image will be used as the user's profile picture.
 */
$('#save-profile').click(function (eve){


    /*
        WARNING
        WARNING
        WARNING
        WARNING

        This solves the bug where the crop box needs to move a little before the image can
        be successfully uploaded.

        We do not know why this works.

        Please do not touch.
     */
    let cropper = $("#change-profile-pic").data('cropper');
    cropper.scale(1);
    /*
        WARNING END
        WARNING END
        WARNING END
        WARNING END
     */



    eve.preventDefault();
    var formData = new FormData();
    croppedCanvas.toBlob(function (blob) {
        formData.append('picture', blob, filename);
        formData.append('album', "Profile Pictures");
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
            url: '/users/home/profilePicture',
            data: formData,
            success: function (data, textStatus, xhr) {
                if (xhr.status == 200) {
                    $("#selectProfileInput").show();
                    window.location = '/users/home'
                } else {
                    window.location = '/users/home'
                }
            }
        })
    });
});

function showMessage(message) {
    const x = document.getElementById("snackbar");

    // Add the "show" class to DIV
    x.className = "show";
    x.innerText = message;
    // After 3 seconds, remove the show class from DIV
    setTimeout(function(){
        x.className = x.className.replace("show", "");
    }, 3000);
}

function deletePhotoFromUI(mediaId) {
    var slides = document.getElementsByClassName("mySlides");
    var size = slides.length;
    for (var i=0; i < slides.length; i++) {
        if (slides[i].getAttribute('data-mediaId') == mediaId) {
            slides[i].remove();
            document.querySelectorAll("img[data-mediaId='" + mediaId + "']")[0].remove();
            var wellStyle = document.getElementById('emptyAlbumMessage').getAttribute("style");
            wellStyle += "display: none;"
            document.getElementById('emptyAlbumMessage').setAttribute("style", wellStyle);
            size-=1;
        }
    }
    if (size > 0) {plusSlides(1);}
    else
    {
        closeModal();
        var wellStyle = document.getElementById('emptyAlbumMessage').getAttribute("style");
        wellStyle += "display: block;"
        document.getElementById('emptyAlbumMessage').setAttribute("style", wellStyle);
    }
    showMessage("Deleted the photo successfully!");
}

/**
 * This function makes a request to the server to delete a photo with the passed id.
 * @param url
 * @param photoid
 * @param imageId
 */
function deletePhotoRequest(photoId){
    var url = '/users/home/deletePicture/' + photoId + '/false';
    var token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
        url: url,
        method: "Delete",
        data: JSON.stringify({
            photoid: '"' + photoId + '"',
            response: false
        }),
        headers: {
            'Content-Type': 'application/json'
        },
        success: function(res){
                    deletePhotoFromUI(photoId);
                },
        error: function(res){
            if(res.responseText === "Is profile picture ask user"){
                $(document.getElementById('myModal')).modal('hide');
                $(document.getElementById('confirmDeleteProfilePhotoModal')).modal('show');

                document.getElementById('yesDeleteProfilePhoto').onclick =
                    function(){
                        $.ajax({
                            url: '/users/unlinkAndDeletePicture/'+photoId,
                            method: "Delete",
                            success:function(res) {
                                deletePhotoFromUI(photoId);
                            },
                            error:function(res){
                                console.log(res.responseText);
                            }
                        });
                        $(document.getElementById('myModal')).modal('show')
                    };
                document.getElementById('noCloseDeleteProfilePhotoButton').onclick =
                    function(){
                        $(document.getElementById('myModal')).modal('show')
                    };

                $('#confirmDeletePhotoModal').on('hidden.bs.modal', function () {
                    $(document.getElementById('myModal')).modal('show');
                })

            }
             else if(res.responseText === "Failed to delete image"){
                 $(document.getElementById('myModal')).modal('hide');
                 $(document.getElementById('confirmDeletePhotoModal')).modal('show');

                 document.getElementById('yesDeletePhoto').onclick =
                     function(){
                         $.ajax({
                             url: '/users/unlinkAndDeletePicture/'+photoId,
                             method: "Delete",
                             success: function (res) {
                                deletePhotoFromUI(photoId);
                             },
                             error: function (res) {
                                 console.log(JSON.stringify(res));
                             }
                         })
                         $(document.getElementById('myModal')).modal('show')
                     };
                 document.getElementById('noCloseDeletePhotoButton').onclick =
                     function(){
                         $(document.getElementById('myModal')).modal('show')
                 };

                 $('#confirmDeletePhotoModal').on('hidden.bs.modal', function () {
                     $(document.getElementById('myModal')).modal('show');
                 })

             }
        }

    })
}