
var latitude;
var longitude;

function setLatLng(lat, lng) {
    latitude = lat;
    longitude = lng;
}

/**
 * The callback function for the google maps
 * api. Initialises and displays map. Initialises
 * marker and listeners.
 */
function initMap() {

    var initLatLng = new google.maps.LatLng(
        latitude, longitude
    );

    window.globalMap = new google.maps.Map(document.getElementById('map'), {
        center: initLatLng,
        zoom: 8
    });

    initMapMarker();
    initMapPositionListeners();
}

/**
 * Initialises and displays the marker for the
 * current destination.
 */
function initMapMarker() {

    var marker = new google.maps.Marker({
        position: {
            lat: latitude,
            lng: longitude
        },
        map: window.globalMap
    });
}

/**
 * If in edit mode then listeners will
 * be added to the latitude and longitude
 * fields so they will update the maps center.
 * A listener is added to the map so clicking
 * will update the latitude and longitude fields.
 */
function initMapPositionListeners() {

    if (document.getElementById("latitude") !== null
        && document.getElementById("longitude") !== null) {

        document.getElementById("latitude").addEventListener('input', function (event) {

            if (!isNaN(document.getElementById("latitude").value)) {
                var latlng = new google.maps.LatLng(
                    document.getElementById("latitude").value,
                    window.globalMap.center.lng());

                window.globalMap.setCenter(latlng);

            }
        });

        document.getElementById("longitude").addEventListener('input', function (event) {

            if (!isNaN(document.getElementById("longitude").value)) {

                var latlng = new google.maps.LatLng(
                    window.globalMap.center.lat(),
                    document.getElementById("longitude").value);

                window.globalMap.setCenter(latlng);
            }
        });

        window.globalMap.addListener('click', function (event) {
            document.getElementById("latitude").value = event.latLng.lat();
            document.getElementById("longitude").value = event.latLng.lng();
        });

    }
}

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
            if (!setPublic) {
                let publicButton = document.getElementById("makePrivateLink-"+mediaId);
                publicButton.innerHTML = "Make Public";
                publicButton.setAttribute('id', "makePublicLink-"+mediaId);
                publicButton.setAttribute('onclick', "setMediaPrivacy("+mediaId+", " + !setPublic + ")");
            } else {
                let privateButton = document.getElementById("makePublicLink-"+mediaId);
                privateButton.innerHTML = "Make Private";
                privateButton.setAttribute('id', "makePrivateLink-"+mediaId);
                privateButton.setAttribute('onclick', "setMediaPrivacy("+mediaId+", " + !setPublic + ")");
            }
        }
    });
}
