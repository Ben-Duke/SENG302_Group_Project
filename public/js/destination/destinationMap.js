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
    const icons = {
        greenIcon: {
            url: 'https://maps.google.com/mapfiles/kml/paddle/grn-blank.png',
            scale: 5
        },
        blueIcon: {
            url: 'https://maps.google.com/mapfiles/kml/paddle/blu-blank.png',
            scale: 5
        }

    };

    fetch('/users/destinations/getalljson', {
        method: 'GET'})
        .then(res => res.json())
        .then(data => {
            const destinations = data;

            let customIcon;
            for (destination of destinations) {
                if (destination.isPublic) {
                    customIcon = icons.greenIcon;
                } else {
                    customIcon = icons.blueIcon;
                }

                window.globalMarkers.push(new google.maps.Marker({
                    position: {
                        lat: destination.latitude,
                        lng: destination.longitude
                    },
                    map: window.globalMap,
                    icon: customIcon
                }));
            }
        });
}