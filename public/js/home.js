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
            console.log($(this).cropper('getCroppedCanvas'));
            console.log(croppedCanvas.toDataURL());
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
    croppedCanvas.toBlob(function(blob){
        formData.append('picture', blob, filename);
        var token =  $('input[name="csrfToken"]').attr('value');
        $.ajaxSetup({
            beforeSend: function(xhr) {
                xhr.setRequestHeader('Csrf-Token', token);
            }
        });
        $.ajax({
            type: 'POST',
            processData: false,
            contentType: false,
            url:'/users/home/profilePicture',
            data: formData,
            success: function(data, textStatus, xhr){
                if(xhr.status == 200) {
                    $("#selectProfileInput").show();
                    window.location = '/users/home'
                }
                else{
                    window.location = '/users/home'
                }
            }
        })
    });
});

/**
 * New set profile picture from existing picture function. Makes use of the cropper.
 * @param photoId
 */
function setProfilePictureRequest(photoId){
            $("#destination-carousel").modal('hide');
            isExistingPhoto = true;
            photoIdToEdit = photoId;
            $('#addProfilePhoto').modal('show');
}


/**
 * Same as the previous function, but for adding a photo from an existing destination
 */
$('#addProfilePhoto').on('show.bs.modal', function (e) {
    if(isExistingPhoto == true){
        var output = document.getElementById('change-profile-pic');
        output.src = "/users/home/serveDestPicture/" + photoIdToEdit;
        $("#selectProfileInput").hide();
    }
});

/**
 * Same as the previous function, but for adding a photo from an existing destination
 */
$('#addProfilePhoto').on('shown.bs.modal', function (e) {
    if(isExistingPhoto == true){
        isExistingPhoto = false;
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
    }
});
// $("#imgInp").change(function(){
//     readURL(this);
// });

/**
 * Function to search for private destinations.
 * Updates the rows of tables with class "privateDestinations" depending on what's entered in the input form with class name "searchDestinations"
 */
function searchDestination(){
    // Declare variables
    var input, elements, filter, tables, table, tr, th, td, td2, i, txtValue, txtValue2, txtValue3;
    elements = document.getElementsByClassName("searchDestinations");
    for(var a=0; a<elements.length; a++) {
        input = elements[a];
        filter = input.value.toUpperCase();
        tables = document.getElementsByClassName("privateDestinations");
        table = tables[a];
        tr = table.getElementsByTagName("tr");
        // Loop through all table rows, and hide those who don't match the search query
        for (i = 0; i < tr.length; i++) {
            th = tr[i].getElementsByTagName("th")[0];
            td = tr[i].getElementsByTagName("td")[0];
            td2 = tr[i].getElementsByTagName("td")[1];
            if (td) {
                txtValue = td.textContent || td.innerText;
                txtValue2 = th.textContent || th.innerText;
                txtValue3 = td2.textContent || td2.innerText;
                if (txtValue.toUpperCase().indexOf(filter) > -1 || txtValue2.toUpperCase().indexOf(filter) > -1
                    || txtValue3.toUpperCase().indexOf(filter) > -1) {
                    tr[i].style.display = "";
                } else {
                    tr[i].style.display = "none";
                }
            }
        }
    }
}

/**
 * Function to search for public destinations.
 * Updates the rows of tables with class "publicDestinations" depending on what's entered in the input form with class name "searchPublicDestinations"
 */
function searchPublicDestination(){
    // Declare variables
    var input, elements, filter, tables, table, tr, th, td, td2, i, txtValue, txtValue2, txtValue3;
    elements = document.getElementsByClassName("searchPublicDestinations");
    for(var a=0; a<elements.length; a++) {
        input = elements[a];
        filter = input.value.toUpperCase();
        tables = document.getElementsByClassName("publicDestinations");
        table = tables[a];
        tr = table.getElementsByTagName("tr");

        // Loop through all table rows, and hide those who don't match the search query
        for (i = 0; i < tr.length; i++) {
            th = tr[i].getElementsByTagName("th")[0];
            td = tr[i].getElementsByTagName("td")[0];
            td2 = tr[i].getElementsByTagName("td")[1];
            if (td) {
                txtValue = td.textContent || td.innerText;
                txtValue2 = th.textContent || th.innerText;
                txtValue3 = td2.textContent || td2.innerText;
                if (txtValue.toUpperCase().indexOf(filter) > -1 || txtValue2.toUpperCase().indexOf(filter) > -1
                    || txtValue3.toUpperCase().indexOf(filter) > -1) {
                    tr[i].style.display = "";
                } else {
                    tr[i].style.display = "none";
                }
            }
        }
    }
}

/**
 * Function to link a photo with a destination
 * Sends a PUT ajax request to the backend to link destinations to a photo (the photoid is sent)
 * @param url to send the ajax request to
 * @param photoid the id of the photo you want to link
 */
function sendLinkDestinationRequest(url, photoid){
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
            photoid: '"' + photoid + '"'
        }),
        headers: {
            'Content-Type': 'application/json'
        },
        success:function(res){
            $("#" + photoid).modal('hide');
            console.log("Success!");
        }
    })
}