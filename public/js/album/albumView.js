var slideIndex = 1;
var currentSlideNo = 1;
var albumData = null;
var photoIdToEdit;

moveAlbumSearch();


function moveAlbumSearch() {
      const searchBar = document.getElementById("album-search-move");
      let oldAlbumId = document.getElementById("existingAlbumId").innerText;
      searchBar.addEventListener('input', (e) => {
          if (e.constructor.name !== 'InputEvent') {
                let title = searchBar.value;
                console.log(title);
                $.ajax({
                                type: 'GET',
                                url: '/users/albums/getFromTitle/' + title,
                                contentType: 'application/json',
                                success: (newAlbumId) => {
                                        console.log(newAlbumId)
                                        moveBetweenAlbums(oldAlbumId, newAlbumId);
                                    }
                                });


          }
      });
}

function moveBetweenAlbums(oldAlbumId, newAlbumId) {

  var hidePrivate = false;
      $.ajax({
              type: 'GET',
              url: '/users/albums/get/' + hidePrivate + '/' + oldAlbumId,
              contentType: 'application/json',
              success: (albumData) => {
                      console.log(albumData);
                      console.log(slideIndex);
                      let mediaId = albumData[slideIndex-1]["mediaId"];
                      var token =  $('input[name="csrfToken"]').attr('value');
                      $.ajaxSetup({
                          beforeSend: function(xhr) {
                              xhr.setRequestHeader('Csrf-Token', token);
                          }
                          });
                          $.ajax({
                              url: "/users/albums/move_media/" + newAlbumId,
                              method: "PUT",
                              data: JSON.stringify({
                                  mediaIds: [mediaId]
                              }),
                              headers: {
                                  'Content-Type': 'application/json'
                              },
                              success:function(res, textStatus, xhr){
                                  if (xhr.status == 200) {
                                     window.location = oldAlbumId
                                  }
                              }
                          })
              }
      });


}





/**
 * Sets listener for the delete button on the current slide
 * @param albumData the data of all media in the current album
 * @param i the index of the current slide
 */
function setDeletePhotoListener(albumData, i) {
    document.getElementById('deletePhotoBtn').addEventListener('click', () => {
        const mediaId = albumData[i]["mediaId"];
        deletePhotoRequest(mediaId);
    });
}

/**
 * Sets listener for the profile picture button on the current slide
 * @param albumData the data of all media in the current album
 * @param i the index of the current slide
 */
function setMakeProfilePictureListener(albumData, i) {
    document.getElementById('profilePictureBtn').addEventListener('click', () => {
        const mediaId = albumData[i]["mediaId"];
        setProfilePictureRequest(mediaId);
    });
}

/**
 * Sets listener for the destination link button on the current slide
 * @param albumData the data of all media in the current album
 * @param i the index of the current slide
 */
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

/**
 * Sets the listener for the make public/make private button on the current slide
 * @param setPrivacy true for setting the button to make public else false for setting to make private
 * @param mediaId the id of the media to set the button for
 */
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

/**
 * Sets listeners for all buttons on the current slide
 * @param i the index of the current slide
 */
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
            setMakeProfilePictureListener(albumData, i);
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
function showDeleteAlbumModal(isDefault) {
    if (isDefault === false) {
        $(document.getElementById('confirmDeleteAlbumModal')).modal('show')
    } else {
        $(document.getElementById('defaultAlbum')).modal('show')
    }
}

function okDefault(album) {
    $(document.getElementById('defaultAlbum')).modal('hide')
}

// Open the Modal
function deleteAlbum(albumId) {
    console.log(albumId);
    var url = '/users/albums/delete/' + albumId;
    var token = $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function (xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
        url: url,
        method: "Delete",
        headers: {
            'Content-Type': 'application/json'
        }
    });
}


// Open the Modal
function openModal() {
    document.getElementById("myModal").style.display = "block";
}

// Close the Modal
function closeModal() {
    document.getElementById("myModal").style.display = "none";
}


/**
 * Opens the modal to link and unlink destinations to the media supplied
 * @param mediaId the id of the media supplied
 */
function openDestinationModal(mediaId) {
    document.getElementById('destination-modal').style.display = "block";
    getDestData(mediaId);
}

/**
 * Closes the destination linking modal
 */
function closeDestinationModal() {
    document.getElementById('destination-modal').style.display = 'none';
}

/**
 * Gets the data of all destinations the user can see to populate the destination linking modal and then loads it
 * @param mediaId the id of the media to load destination data for
 */
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

/**
 * Adds destination rows to the destination linking table
 * @param destData the data containing all destinations that the user can view
 * @param mediaId the id of the media to link destinations to
 */
function loadDestTable(destData, mediaId) {
    for (let destination of destData) {
        const publicTable = document.getElementById('public-dest-tbody');
        const privateTable = document.getElementById('private-dest-tbody');
        if (destination.isPublic) {
            addDestRow(publicTable, destination, mediaId);
        } else {
            addDestRow(privateTable, destination, mediaId)
        }
    }
}

/**
 * Adds a row containing one destination to the linking table
 * @param table the table to add the destination row to
 * @param destination the destination to add as a row
 * @param mediaId the id of the media that the table belongs to
 */
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
    linkButton.setAttribute('id', `link-${destination.destId}`);
    linkButton.setAttribute('class', 'btn btn-primary');
    linkButton.innerText = 'Link to destination';
    linkButton.addEventListener('click', () => {
        linkDestination(destination.destId, mediaId)
    });

    const unlinkButton = document.createElement('BUTTON');
    unlinkButton.setAttribute('id', `unlink-${destination.destId}`);
    unlinkButton.setAttribute('class', 'btn btn-danger');
    unlinkButton.innerText = 'Unlink from destination';
    unlinkButton.style.display = 'none';
    unlinkButton.addEventListener('click', () => {
        unlinkDestination(destination.destId, mediaId)
    });

    checkButtonStatus(mediaId, destination.destId);

    const div = document.createElement('DIV');
    div.appendChild(linkButton);
    div.appendChild(unlinkButton);
    row.appendChild(div);

    table.appendChild(row);
}

/**
 * Shows only the necessary linking or unlinking button for a row on the destination linking table
 * @param mediaId the id of the media to check for
 * @param destId the id of the destination row on the table to check
 */
function checkButtonStatus(mediaId, destId) {
    $.ajax({
        method: "GET",
        url: `/users/destinations/photos/${destId}`,
        headers: {
            'Content-Type': 'application/json'
        },
        success: function(photos) {
            for (let photo of photos) {
                if (mediaId === photo.mediaId) {
                    toggleButtons(destId);
                    return;
                }
            }
        }
    });
}

/**
 * Links a media item to a destination
 * @param destId the id of the destination to link
 * @param mediaId the id of the media to link
 */
function linkDestination(destId, mediaId) {
    $.ajax({
        method: "PUT",
        url: `/users/destinations/${destId}`,
        data: JSON.stringify({
            photoid: '"' + mediaId + '"'
        }),
        headers: {
            'Content-Type': 'application/json'
        },
        success: function() {
            toggleButtons(destId)
        }
    });
}

/**
 * Links a media item to a destination
 * @param destId the id of the destination to link
 * @param mediaId the id of the media item to link
 */
function unlinkDestination(destId, mediaId) {
    $.ajax({
        type: 'DELETE',
        url: `/users/destinations/${mediaId}/${destId}`,
        contentType: 'application/json',
        success: () => {
            toggleButtons(destId)
        }
    });
}

/**
 * Toggles buttons on a destination row of the linking table between linking and unlinking
 * @param destId the id of the destination row
 */
function toggleButtons(destId) {
    const unlink = document.getElementById(`unlink-${destId}`);
    unlink.style.display === "none" ? unlink.style.display = "block" : unlink.style.display = "none";

    const link = document.getElementById(`link-${destId}`);
    link.style.display === 'none' ? link.style.display = 'block' : link.style.display = 'none';
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
    photoIdToEdit = photoId;
    $('#addProfilePhoto').modal('show');
}


/**
 *
 */
$('#addProfilePhoto').on('show.bs.modal', function (e) {
    $("#myModal").hide();
    var output = document.getElementById('change-profile-pic');
    output.src = "/users/home/serveDestPicture/" + photoIdToEdit;
    $("#selectProfileInput").hide();
});

/**
 *
 */
$('#addProfilePhoto').on('shown.bs.modal', function (e) {
    $.ajax({
        type: 'GET',
        url: '/users/photos/' + photoIdToEdit,
        success: function(data){
            filename = data["url"];
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
                    croppedCanvas = $(this).cropper('getCroppedCanvas');
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


/**
 * Requests the photo caption and sets the captionInput to match that if it exists.
 * @param photoId the id of the photo to get the caption from
 */
function getPhotoCaption(photoId) {
    $.ajax({
        type: 'GET',
        url: '/users/photos/'+ photoId +'/caption',
        success:function(res){
            const captionInput = document.getElementById("captionInput-" + photoId);
            captionInput.value = res;
        },
        error: function(xhr, textStatus, errorThrown){
            console.log(xhr.status + " " + textStatus + " " + errorThrown);

        }
    })
}

/**
 * Toggles the visibility of elements relating to captions.
 * Hides the edit caption button only if the caotion exists
 * @param photoId the id of the photo to toggle displays of
 */
function toggleCaptionDisplays(photoId) {
    const caption = document.getElementById("caption-" + photoId);
    const captionInput = document.getElementById("captionInput-" + photoId);
    const editCaptionBtn = document.getElementById("editCaptionButton-" + photoId);

    if (captionInput.value) {
        editCaptionBtn.style.display = 'none'
    } else {
        editCaptionBtn.style.display = 'block'
    }

    toggleDisplay(caption);
    toggleDisplay(captionInput);
}

/**
 * Used to edit a caption, shows all button required to edit a caption,
 * calls getPhotoCaption to get the caption for the photo so the user
 * can edit it.
 * @param photoId
 */
function editCaption(photoId) {
    getPhotoCaption(photoId);
    toggleCaptionDisplays(photoId);
    document.getElementById("captionInput-" + photoId).focus();
    document.getElementById("editCaptionButton-" + photoId).style.display = 'none';
}

/**
 * Sends a request to change the photo caption and toggles appropriate displays
 * @param caption the new caption
 * @param photoId the id of the photo to change the caption of
 */
function submitEditCaption(caption, photoId) {
    const errorMessage = document.getElementById(`lengthErrorMessage-${photoId}`);

    if (caption.length > 255) {
        errorMessage.style.display = 'block';
        return;
    }

    // Remove error message
    errorMessage.style.display = 'none';

    $.ajax({
        type: 'PUT',
        url: '/users/photos/'+ photoId +'/caption',
        data: JSON.stringify({
            caption: caption
        }),
        headers: {
            'Content-Type': 'application/json'
        },
        success:function(){
            document.getElementById("caption-" + photoId).innerText = caption;
            toggleCaptionDisplays(photoId);
        },
        error: function(xhr, textStatus, errorThrown){
            console.log(xhr.status + " " + textStatus + " " + errorThrown);
        }
    });
}
