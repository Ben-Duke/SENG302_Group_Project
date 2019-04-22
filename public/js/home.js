var croppedCanvas;
var filename;

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
    console.log("filename1 is " + filename);
    $('#change-profile-pic').cropper("destroy");

    var $previews = $('.preview');
    $('#change-profile-pic').cropper({
        ready: function () {
            var $clone = $(this).clone().removeClass('cropper-hidden');
            $clone.css({
                display: 'block',
                width: '100%',
                minWidth: 0,
                minHeight: 0,
                maxWidth: 'none',
                maxHeight: 'none'
            });
            $previews.css({
                width: '100%',
                overflow: 'hidden'
            }).html($clone);
        },
        crop: function (e) {
            console.log("crop");
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

};

/**
 * This function is called when the user clicks the upload button to upload the cropped canvas image to the database.
 * Sends an AJAX post request to the backend with the photo's information to store the photo within the database.
 * The cropped image will be used as the user's profile picture.
 */
$('#save-profile').click(function (eve){
    console.log("filename2 is " + filename);
    eve.preventDefault();
    var formData = new FormData();
    var private = $('input[type=checkbox]').attr('checked');
    croppedCanvas.toBlob(function(blob){
        formData.append('picture', blob, 'filename');
        formData.append('private', private);
        formData.append('filename', filename);
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
                    window.location = '/users/home'
                }
                else{
                    window.location = '/users/home'
                }
            }
        })
    });
});

function setProfilePictureRequest(url, photoId){
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
            photoId: '"' + photoId + '"'
        }),
        headers: {
            'Content-Type': 'application/json'
        },
        success:function(res){
            $("#" + photoId).modal('hide');
            console.log("Success!");
        }
    })
}

// $("#imgInp").change(function(){
//     readURL(this);
// });