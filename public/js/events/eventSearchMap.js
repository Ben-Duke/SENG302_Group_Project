let map;
window.globalMarkers = [];

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
        .then(destinations => {
            let marker;
            let infoWindow;
            for (let index = 0; index < destinations.length; index++) {
                marker = new google.maps.Marker({
                    position: {
                        lat: destinations[index].latitude,
                        lng: destinations[index].longitude
                    },
                    map: window.globalMap,
                    icon: getMarkerIcon(destinations[index].isPublic)
                });

                infoWindow = new google.maps.InfoWindow({
                    content: getMapInfoWindowHTML(destinations[index])
                });

                //make the marker and infoWindow globals (persist in browser session)
                window.globalMarkers.push({
                    marker: marker,
                    infoWindow: infoWindow,
                    isClicked: false
                });

                initMarkerEventHandlers(index);
                initInforWindowEventHandlers(index);
            }
        });
}

/**
 * Initiates all the event handlers for a google maps markers.
 *
 * @param markerIndex The index of the marker and window in window.globalMarkers
 */
function initMarkerEventHandlers(markerIndex) {
// event handler to open infoWindow on mouseout
    window.globalMarkers[markerIndex].marker.addListener('mouseover', () => {
        window.globalMarkers[markerIndex].infoWindow.open(window.globalMap,
            window.globalMarkers[markerIndex].marker);
    });

    // event handler to close infoWindow on mouseout
    window.globalMarkers[markerIndex].marker.addListener('mouseout', () => {

        if (window.globalMarkers[markerIndex].isClicked) {
            // user are clicked on current marker
            // do nothing (dont close on mouseout)
        } else {
            // user hasn't explicitly clicked, so safe to close
            window.globalMarkers[markerIndex].infoWindow.close(window.globalMap,
                window.globalMarkers[markerIndex].marker);
        }
    });

    // event handler to open infoWindow on click
    window.globalMarkers[markerIndex].marker.addListener('click', () => {
        window.globalMarkers[markerIndex].isClicked = true;
        window.globalMarkers[markerIndex].infoWindow.open(window.globalMap,
            window.globalMarkers[markerIndex].marker);
    });
}

/**
 * Initiates all the event handlers for a google maps infoWindows.
 *
 * @param markerIndex The index of the marker and window in window.globalMarkers
 */
function initInforWindowEventHandlers(markerIndex) {
// event handler to handle infoWindow exit
    window.globalMarkers[markerIndex].infoWindow.addListener('closeclick', () => {
        window.globalMarkers[markerIndex].isClicked = false;
        window.globalMarkers[markerIndex].infoWindow.close(window.globalMap,
            window.globalMarkers[markerIndex].marker);
    });
}

/**
 * Gets a JSON of all marker icons.
 *
 * @returns {{greenIcon: {url: string, name: string}, blueIcon: {url: string, name: string}}}
 */
function getAllMarkerIcons() {
    const icons = {
        greenIcon: {
            url: 'https://maps.google.com/mapfiles/ms/icons/green-dot.png',
            name: 'Public Destination'
        },
        blueIcon: {
            url: 'https://maps.google.com/mapfiles/ms/icons/blue-dot.png',
            name: 'Private Destination'
        }
    };
    return icons;
}

/**
 * Gets the Icon (google maps api spec) for the Marker, depends on the Destintions
 * privacy.
 *
 * @param isPublic A boolean, true if Destination is public, false otherwise
 * @returns {icons.blueIcon|{url, scale}|icons.greenIcon} JSON of the icon
 */
function getMarkerIcon(isPublic) {
    const icons = getAllMarkerIcons();

    let selectedIcon;

    if (isPublic) {
        selectedIcon = icons.greenIcon;
    } else {
        selectedIcon = icons.blueIcon;
    }

    return selectedIcon;
}

/**
 * Gets the HTML for a Destinations infoWindow, for the google map.
 *
 * @param destination A JSON object of the Destination, from an AJAX query from
 *  /users/destinations/getalljson endpoint.
 *
 * @returns {string} A String containing the HTML to display in the markers
 *                   infoWindow.
 */
function getMapInfoWindowHTML(destination) {
// create the destinations info window
    const destinationName = destination.destName;
    const destinationType = destination.destType;
    const destinationCountry = destination.country;
    const destinationDistrict = destination.district;


    let infoWindowHTML;
    // uses a ES6 template string
    infoWindowHTML = `<style>.basicLink {text-underline: #0000EE;}</style>
                      <a class="basicLink" href="/users/destinations/view/${destination.destId}" onclick="viewDestination(${destination.destId})">
                        ${destinationName}
                      </a>
                      <div>${destinationType}</div>
                      <div>District: ${destinationDistrict}</div>
                      <div>${destinationCountry}</div>
                      <div>
                      <button id="selectDestination" onclick="selectDestination(${destination.destId}, false)">Add to trip</button>
                      </div>`;
    return infoWindowHTML;
}