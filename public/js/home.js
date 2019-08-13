/**
 * Initilizes the page.
 */
this.initSetProfilePicToDefaultButton();

var croppedCanvas;
var filename;
var isExistingPhoto = false;
var photoIdToEdit;



/**
 * This function is called when an image file is chosen and uploaded by the user.
 * Creates a crop canvas using cropperjs where the user can crop their image, which is stored into a croppedCanvas variable.
 * The croppedCanvas variable is updated every time the user changes the crop box.
 * A preview image is also displayed beside the canvas with a circle preview of how the cropped canvas will look like as a profile picture.
 * @param event the event that an image file is chosen and uploaded by the user
 */
var loadFile = function (event) {
    var output = document.getElementById('change-profile-pic');
    var upload = document.getElementById('selectProfileInput');

    if(upload.files[0].size > 2097152) {
        alert("File is too big! (larger than 2MB)");
        upload.value = "";
    } else {
        output.src = URL.createObjectURL(event.target.files[0]);
        filename = event.target.files[0].name;
        $('#change-profile-pic').cropper("destroy");

        var $previews = $('.preview');
        $('#change-profile-pic').cropper({
            aspectRatio:1,
            data:{
                width: 150,
                height: 150
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
        })
    }
};




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
        console.log(searchBar.value);
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
                    window.location = '/users/home'
                } else {
                    window.location = '/users/home'
                }
            }
        })
    }
});


/**
 * Shows an error message to the user in the set profile pic modal.
 *
 * @param message A String of the message to show the user.
 */
function showPlaceHolderImageError(message) {
    const infoAlert = document.querySelector("#setProfilePictureToDefaultError");

    infoAlert.classList.remove('hiddenDiv');
    infoAlert.classList.remove('alert-success');
    infoAlert.classList.add('alert-danger');
    infoAlert.textContent = message;
}

/**
 * Function to hide the errors shown if setting the profile picture to the placeholder
 * fails, after a set timeout.
 *
 * @param delayMS An integer, milliseconds after which to hide the error.
 */
function hideErrorDivDelay(delayMS) {
    const infoAlert = document.querySelector("#setProfilePictureToDefaultError");

    setTimeout(() => {
        infoAlert.classList.add('hiddenDiv');
    }, delayMS);
}

/**
 * Function to set up the event handler for clicking on the "Use Placeholder
 * Picture" button.
 *
 * Event fires an ajax request to set the users profile pic to the placeholder.
 * If it a status 200 is received it refreshes the page to show changes.
 * If not status 200 the page shows some error that hides after 5 seconds.
 */
function initUseDefaultProfilePicButtonEventHandler() {
    const setProfilePicDefaultBtn = document
                          .querySelector('#change-profile-photo-to-placeholder');

    setProfilePicDefaultBtn.addEventListener('click', (event) => {
        fetch('/users/home/profilePicture1/removeProfilePictureStatus1', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': '*',
                'Csrf-Token': "nocheck"
            },
            body: JSON.stringify("test")
        })
            .then(res => {
                if (res.status === 500) {
                    this.showPlaceHolderImageError("Internal server error, try again later.");
                    this.hideErrorDivDelay(5000);
                } else if (res.status === 400) {
                    this.showPlaceHolderImageError("Bad request, you are already using the " +
                                                            "placeholder image.");
                    this.hideErrorDivDelay(5000);
                } else if (res.status === 200) {
                    window.location = '/users/home' //refresh the page to show changes
                }
            })
            .catch(err => {
                this.showPlaceHolderImageError("Unknown error occurred, try again later.");
                this.hideErrorDivDelay(5000);
            });
    });
}

/**
 * Sets the "use placeholder image" buttons visibility to visible if the user
 * has a profile picture, else it hides the button.
 *
 * Sends an AJAX request.
 *
 */
function initSetProfilePicToDefaultButtonVisibility() {
    const setProfilePicDefaultBtn = document
                        .querySelector('#change-profile-photo-to-placeholder');
    const urlIsProfilePicSet = "/users/profilepicture/isSet";
    fetch(urlIsProfilePicSet)
        .then(res => {
            if (res.status === 200) {
                res.json()
                    .then(data => {
                        const hasProfilePic = data['isProfilePicSet'];
                        if (!hasProfilePic) {
                            setProfilePicDefaultBtn.style.visibility = "hidden";
                        }
                    })
                    .catch(() => {
                        console.log('Error checking if user has profile pic.');
                    });
            } else {
                console.log(res)
            }
        })
        .catch(err => {
            console.log(err);
        });
}

/**
 * Helper function which calls all methods related to setting up the "use
 * placeholder" button.
 */
function initSetProfilePicToDefaultButton() {
    this.initSetProfilePicToDefaultButtonVisibility();
    this.initUseDefaultProfilePicButtonEventHandler();
}

