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


