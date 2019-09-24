let map;
window.globalMarkers = [];

var latitude;
var longitude;

function setLatLng(lat, lng) {
    latitude = lat;
    longitude = lng;
}

function initMap() {
    map = window.globalMap = new google.maps.Map(document.getElementById('map'), {
        center: {lat: -43.522057156877615, lng: 172.62360347218828},
        zoom: 5,
        mapTypeControl: true,
        mapTypeControlOptions: {
            style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR,
            position: google.maps.ControlPosition.TOP_RIGHT
        }
    });
    initEventMarker()
}


/**
 * Displays all visible destination markers to the google map. Add's the marker
 * objects to the window.globalMarkers global variable.
 */
function initEventMarker() {
    let marker = new google.maps.Marker({
        position: {
            lat: latitude,
            lng: longitude
        },
        map: window.globalMap
    });
    //make the marker and infoWindow globals (persist in browser session)
    window.globalMarkers.push({
        marker: marker,
        isClicked: false
    });
    let destLatLng = new google.maps.LatLng(
        latitude, longitude
    );

    window.globalMap.setCenter(destLatLng);
    window.globalMap.setZoom(10);

}

function linkPhotoToEvent(mediaId, eventId) {
    $.ajax({
        type: 'PUT',
        url: `/events/linkphoto/${mediaId}/${eventId}`,
        success: function () {
            window.location.reload()
        }
    });
}

function unlinkPhotoToEvent(mediaId, eventId) {
    $.ajax({
        type: 'PUT',
        url: `/events/unlinkphoto/${mediaId}/${eventId}`,
        success: function () {
            window.location.reload()
        }
    });
}

$("#photo-carousel").on('hidden.bs.modal', function(){
    const carouselItems = document.getElementById("carousel-inner").children;
    for (let item of carouselItems) {
        item.classList.remove('active');
    }
    activePhotoId = undefined;
});

let activePhotoId;
function displayPhoto(photoId) {
    if (activePhotoId !== undefined) {
        document.getElementById('caro-'+activePhotoId).classList.remove('active');
    }

    document.getElementById('caro-'+photoId).classList.add('active');
    activePhotoId = photoId;
}

/**
 * Sets a photo privacy to the setting specified
 * @param mediaId the id of the media to change privacy
 * @param setPublic true to set to public, false to set to private
 */
function setMediaPrivacy(mediaId, setPublic) {
    const intPublic = setPublic ? 1 : 0;
    $.ajax({
        type: 'GET',
        url: '/users/home/photoPrivacy/' + mediaId + '/' + intPublic,
        contentType: 'application/json',
        success: () => {
            const makePublic = document.getElementById(`makePublicLink-${mediaId}`);
            const makePrivate = document.getElementById(`makePrivateLink-${mediaId}`);

            makePublic.style.display = makePublic.style.display === "none" ? "" : "none";
            makePrivate.style.display = makePrivate.style.display === "none" ? "" : "none"

            const privacyEye = document.getElementById(`privacy-eye-${mediaId}`);
            if (privacyEye.classList.contains("fa-eye-red")) {
                privacyEye.classList.remove("fa-eye-red");
                privacyEye.classList.add("fa-eye-green");
            } else {
                privacyEye.classList.remove("fa-eye-green");
                privacyEye.classList.add("fa-eye-red");
            }
        }
    });
}



