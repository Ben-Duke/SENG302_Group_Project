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
 * Event is called while the modal is popping up.
 * Displays the destination's photos as a carousel and the destination's
 * traveller type on the fly using ajax queries
 * based on the destination id retrieved from the table row clicked.
 */
$('#orderModal').on('show.bs.modal', function (e) {
    // do something...
    //getIdFromRow = $(event.target).closest('tr').data('id');
    //make your ajax call populate items or what even you need
    //$(this).find('#orderDetails').html($('<b> Destination Id selected: ' + getIdFromRow  + '</b>'));
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
            $('#destTitle').html(destinationData["destName"]);
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
                            if(index == 0){
                                itemNode.classList.add("active")
                            }
                        }
                        var imgNode = document.createElement("img");
                        imgNode.src="/users/home/serveDestPicture/" + element["photoId"];
                        imgNode.classList.add("destination-image");
                        imgNode.id = "photo" + "-" + element["photoId"];
                        itemNode.appendChild(imgNode);
                        outerDivNode.appendChild(itemNode);
                        // $.ajax({
                        //    type: 'GET',
                        //     url: '/users/photos/' + element["photoId"],
                        //     contentType: 'image/png',
                        //     success: function(data){
                        //        imgNode.src = data;
                        //        itemNode.appendChild(imgNode);
                        //        $('#carousel-images').append(itemNode);
                        //     }
                        // });
                        //console.log(element["urlWithPath"]);
                        //imgNode.src = element["urlWithPath"];
                    });
                    $('#destslider').html(outerDivNode);
                    if(destinationOwner != user){
                        $('#primaryPhotoButton').hide();
                    }
                    else{
                        $('#primaryPhotoButton').show();
                    }
                }
            });
        }
    });
});

/**
 * Event when the modal is shown. If there are no photos in the destination, hide the carousel and
 * display a html message that the destination does not have any images.
 */
$('#orderModal').on('shown.bs.modal', function (e) {

    if(photos.length > 0) {
        var photo = document.getElementsByClassName("active")[0].getElementsByTagName('img')[0];
        var photoid = photo.id;
        photoid = photoid.split("-")[1];
        if (destData["primaryPhoto"] != null) {
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
    }
    else{
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
                    destData = destinationData;
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
    console.log(destData["primaryPhoto"]["photoId"]);
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