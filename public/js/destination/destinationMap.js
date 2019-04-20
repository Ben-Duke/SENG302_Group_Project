/**
 * ES6 source file for the google map on the Destination page.
 */

window.globalMarkers = []; // global variable : a list of all marker objects on the map

/**
 * The callback function that is called after the google maps script src loads
 * with the api key.
 *
 * Loads the map with all the markers.
 */
function initMap() {
    window.globalMap = new google.maps.Map(document.getElementById('map'), {
        center: {lat: -34.397, lng: 150.644},
        zoom: 8
    });

    initDestinationMarkers();
}

/**
 * Displays all visible destination markers to the google map. Add's the marker
 * objects to the window.globalMarkers global variable.
 */
function initDestinationMarkers() {
    fetch('/users/destinations/getalljson', {
        method: 'GET'})
        .then(res => res.json())
        .then(data => {
            const destinations = data;

            for (destination of destinations) {
                window.globalMarkers.push(new google.maps.Marker({
                    position: {
                        lat: destination.latitude,
                        lng: destination.longitude
                    },
                    map: window.globalMap,
                    icon:'https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png'
                }));
            }
        });
}