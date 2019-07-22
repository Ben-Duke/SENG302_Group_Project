var getIdFromRow;
var destData;
var photos;
var user;
var destinationOwner;

/**
 * Function to search for private destinations.
 * Updates the rows of tables with class "privateDestinations" depending on what's entered in the input form with class name "searchDestinations"
 */
function searchDestination(){
    // Declare variables
    var input, elements, filter, tables, table, tr, th, td, td2, i, txtValue, txtValue2, txtValue3;
    let districtTd, districtValue;
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
            districtTd = tr[i].getElementsByTagName("td")[2];
            if (td) {
                txtValue = td.textContent || td.innerText;
                txtValue2 = th.textContent || th.innerText;
                txtValue3 = td2.textContent || td2.innerText;
                districtValue = districtTd.textContent || districtTd.innerText;
                if (txtValue.toUpperCase().indexOf(filter) > -1 || txtValue2.toUpperCase().indexOf(filter) > -1
                    || txtValue3.toUpperCase().indexOf(filter) > -1 || districtValue.toUpperCase().indexOf(filter) > -1) {
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
    let districtTd, districtValue;
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
            districtTd = tr[i].getElementsByTagName("td")[2];
            if (td) {
                txtValue = td.textContent || td.innerText;
                txtValue2 = th.textContent || th.innerText;
                txtValue3 = td2.textContent || td2.innerText;
                districtValue = districtTd.textContent || districtTd.innerText;
                if (txtValue.toUpperCase().indexOf(filter) > -1 || txtValue2.toUpperCase().indexOf(filter) > -1
                    || txtValue3.toUpperCase().indexOf(filter) > -1 || districtValue.toUpperCase().indexOf(filter) > -1) {
                    tr[i].style.display = "";
                } else {
                    tr[i].style.display = "none";
                }
            }
        }
    }
}

/**
 * Confirmation message to make destination public (to be later implemented, this is gold plating)
 * not working right now
 * @param url
 */
function makeDestinationPublic(url){
    if(confirm("Are you sure you want to make this destination public? You won't be able to make it private again.")){
        location.href = url;
    }
    else{

    }
}

/**
 * Function to toggle the private destinations table.
 * Turns on or off the display of an elements with id "hideDivPrivate"
 * @param element the checkbox
 */
function showPrivateDestinations(element){
    if(element.checked){
        document.getElementById("hideDivPrivate").style.display = "";
    }
    else{
        document.getElementById("hideDivPrivate").style.display = "none";
    }
}

/**
 * Function to toggle the public destinations table.
 * Turns on or off the display of an elements with id "hideDivPublic"
 * @param element the checkbox
 */
function showPublicDestinations(element){
    if(element.checked){
        document.getElementById("hideDivPublic").style.display = "";
    }
    else{
        document.getElementById("hideDivPublic").style.display = "none";
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

/**
 * Sets the global dest id to the destination id then opens the modal based on the dest id.
 * @param destid
 */
function viewDestination(destid){
    getIdFromRow = destid;
    $('#orderModal').modal('show');
}

/**
 * Displays the destination's photos as a carousel and the destination's
 * traveller type on the fly using ajax queries
 * based on the destination id retrieved from the table row clicked.
 * If there are no photos in the destination, hide the carousel and
 * display a html message that the destination does not have any images.
 */
function populateViewDestinationModal()
{
    var token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
       type: 'GET',
       url: '/users/get',
        contentType: 'application/json',
        success: function(userData){
           user = userData;
        }
    });
    $.ajax({
       type: 'GET',
       url: '/users/photos',
        contentType: 'application/json',
        success: function(photosData){
           userPhotos = photosData;
        }
    });
    $.ajax({
        type: 'GET',
        url: '/users/destinations/owner/' + getIdFromRow,
        contentType: 'application/json',
        success: function(ownerData){
            destinationOwner = ownerData;
        }
    });
    $.ajax({
        type: 'GET',
        url: '/users/destinations/get/' + getIdFromRow,
        contentType: 'application/json',
        success: function(destinationData){
            destData = destinationData;
            $('#destTitle').html(destinationData.destName);
            $('#destLocation').html(destinationData.district + ", " + destinationData.country);
            $('#coordinates').html("Coordinates: (" + destinationData.latitude + ", " + destinationData.longitude + ")");
            $.ajax({
                type: 'GET',
                url: '/users/destinations/ttypes/' + getIdFromRow,
                contentType: 'application/json',
                success: function(data) {
                    // $('#travellerTypes').append("<p>Traveller type(s):</p>");
                    var outerDivNode = document.createElement("div");
                    // outerDivNode.classList.
                    var parNode = document.createElement("p");
                    var parTextNode = document.createTextNode("Traveller type(s)");
                    parNode.appendChild(parTextNode);
                    outerDivNode.appendChild(parNode);
                    var ulNode = document.createElement("ul");
                    ulNode.classList.add("list-group");
                    outerDivNode.appendChild(ulNode);

                    $.each(data, function(index, element){
                        var liNode = document.createElement("li");
                        liNode.classList.add("list-group-item");
                        var liTextNode = document.createTextNode(element["travellerTypeName"]);
                        liNode.appendChild(liTextNode);
                        ulNode.appendChild(liNode);
                    });
                    // outerDivNode.classList.add("col-md-offset-1");
                    // outerDivNode.classList.add("col-md-4");
                    $('#travellerTypes').html(outerDivNode);
                }
            });
            $.ajax({
                type: 'GET',
                url: '/users/destinations/photos/' + getIdFromRow,
                contentType: 'application/json',
                success: function(data) {
                    photos = data;
                    if(photos.length > 0)
                    {
                        var outerDivNode = document.createElement("div");
                        outerDivNode.classList.add("carousel-inner");
                        $.each(data, function(index, element){
                            var itemNode = document.createElement("div");
                            itemNode.classList.add("item");
                            if(destData["primaryPhoto"] != null) {
                                if (element["photoId"] == destData["primaryPhoto"]["photoId"]) {
                                    itemNode.classList.add("active")
                                }
                            }
                            else{
                                if(index === 0){
                                    itemNode.classList.add("active")
                                }
                            }
                            var imgNode = document.createElement("img");
                            imgNode.src="/users/home/serveDestPicture/" + element["photoId"];
                            imgNode.classList.add("destination-image");
                            imgNode.id = "photo" + "-" + element["photoId"];
                            itemNode.appendChild(imgNode);
                            outerDivNode.appendChild(itemNode);
                            if (destinationOwner !== user) {
                                var found = userPhotos.find(function(elementId) {
                                    return elementId == element["photoId"];
                                });
                                if (!found) {
                                    $('#removePhotoButton').hide();
                                } else {
                                    $('#removePhotoButton').show();
                                }
                            } else {
                                $('#removePhotoButton').show();
                            }
                        });
                        $('#destslider').html(outerDivNode);
                        if(destinationOwner !== user){
                            $('#primaryPhotoButton').hide();
                        }
                        else{
                            $('#primaryPhotoButton').show();
                        }

                        $('.left').show();
                        $('.right').show();
                    }
                    else {
                        $('#removePhotoButton').hide();
                        $('#primaryPhotoButton').hide();
                        $('.left').hide();
                        $('.right').hide();
                        var outerDivNode = document.createElement("div");
                        // outerDivNode.classList.
                        var imgNode = document.createElement("img");
                        imgNode.src= "/assets/images/destinationPlaceHolder.png";
                        imgNode.setAttribute("width", "200");
                        imgNode.setAttribute("height", "150");
                        var textNode = document.createTextNode(destData["destName"] + " has no pictures!");
                        outerDivNode.appendChild(textNode);
                        outerDivNode.appendChild(imgNode);

                        $('#destslider').html(outerDivNode);
                    }
                }
            });
        }
    });
}

/**
 * Event is called while the view destination modal is popping up.
 */
$('#orderModal').on('show.bs.modal', function (e){
    populateViewDestinationModal();
});


/**
 * Function that listens for when someone clicks on the button to set a primary photo.
 * Sets the primary photo based on the active item on the carousel displaying destination photos.
 * The set primary photo button is then disabled.
 */
$('#primaryPhotoButton').click(function(e){
    var photo = document.getElementsByClassName("active")[0].getElementsByTagName('img')[0];
    var photoid = photo.id;
    photoid = photoid.split("-")[1];
    var token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
        url: '/users/destinations/primary/' + getIdFromRow,
        method: 'PUT',
        data: JSON.stringify({
            photoid: '"' + photoid + '"'
        }),
        headers: {
            'Content-Type': 'application/json'
        },
        success: function(){
            $('#primaryPhotoButton').attr('disabled','disabled');
            //refreshing the destination
            $.ajax({
                type: 'GET',
                url: '/users/destinations/get/' + getIdFromRow,
                contentType: 'application/json',
                success: function (destinationData) {
                    var outerDivNode = document.createElement("span");
                    destData = destinationData;
                    var target = destData["destid"];
                    var idTarget = "primary" + target;
                    var imgNode = document.createElement("img");
                    imgNode.width = 50;
                    imgNode.height = 60;
                    var primaryPhoto = destData["primaryPhoto"];
                    var photoId = primaryPhoto["photoId"];
                    imgNode.src="/users/home/serveDestPicture/" + photoId;
                    outerDivNode.appendChild(imgNode);
                    console.log("Log from Ajax success");
                    console.log(destData);
                    $("#"+idTarget).html(outerDivNode);
                }
            });
        }
    });
});

/**
 * After the slider has slid, enable the set to primary photo button if the
 * active picture is not the primary photo.
 * Disable the set to primary photo button otherwise.
 */
$('#destslider').bind('slid.bs.carousel', function(e){
    var photo = document.getElementsByClassName("active")[0].getElementsByTagName('img')[0];
    var photoid = photo.id;
    photoid = photoid.split("-")[1];
    if(destData["primaryPhoto"] != null) {
        if (destData["primaryPhoto"]["photoId"] == photoid) {
            $('#primaryPhotoButton').attr('disabled', 'disabled');
        }
        else {
            $('#primaryPhotoButton').removeAttr('disabled');
        }
    }
    else {
        $('#primaryPhotoButton').removeAttr('disabled');
    }
});

/**
 * Disables the primary photo button while the carousel is sliding.
 */
$('#destslider').bind('slide.bs.carousel', function(e){
    $('#primaryPhotoButton').attr('disabled','disabled');
});

function addPhotoToDestinationRequest(photoId){
    var token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({

        url: '/users/destinations/' + getIdFromRow + '/' + photoId,
        method: "POST",
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
};


$('#confirmDeleteDestinationModal').on('show.bs.modal', function(e) {
    var destId = $(event.target).closest('tr').data('id');
    var destName = $(event.target).closest('tr').data('dest-name');
    document.getElementById("message").textContent = "Are you sure you want to delete " + destName + "?";
    $('#yesDelete').click(function(e){
        var token =  $('input[name="csrfToken"]').attr('value');
        $.ajaxSetup({
            beforeSend: function(xhr) {
                xhr.setRequestHeader('Csrf-Token', token);
            }
        });
        $.ajax({
            url: '/users/destinations/delete/' + destId,
            method: "GET",
            success:function(res){
                document.location.reload(true);
            }
        });
    });
});


/**
 * Function that listens for when someone clicks on the button to remove a photo from the destination.
 * Remove the photo based on the active item on the carousel displaying destination photos.
 *
 */
$('#removePhotoButton').click(function(e){
    var photo = document.getElementsByClassName("active")[0].getElementsByTagName('img')[0];
    var photoid = photo.id;
    photoid = parseInt(photoid.split("-")[1]);
    url = 'destinations/' + photoid + '/' + getIdFromRow;
    const token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
        url: url,
        method: "DELETE",
        success: function(res) {
            populateViewDestinationModal();
        }
    });
});

/**
 * Called when the user clicks the create destination button.
 * Sends an AJAX post request to the backend with the destination and tag information.
 */
$('#createDestinationForm').submit(function(eve) {
    eve.preventDefault();
    let form = document.getElementById("createDestinationForm")
    var formData = new FormData(form);

    toAddTagList = Array.from(toAddTagList);
    console.log(toAddTagList);

    let tags_delimited_string = "";
    for (var i = 0; i < toAddTagList.length; i++) {
        tags_delimited_string += ",";
        tags_delimited_string += toAddTagList[i];
    }
    formData.append('tags', tags_delimited_string);

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
            url: '/users/destinations/save',
            data: formData,
            success: function(data, textStatus, xhr){
                if(xhr.status == 200) {
                    window.location = '/users/destinations'
                }
                else{
                    window.location = '/users/destinations/create'
                }
            }
        })
});


