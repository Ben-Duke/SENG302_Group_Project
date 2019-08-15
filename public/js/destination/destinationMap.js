/**
 * ES6 source file for the google map on the Destination page.
 */

// global variable : a list of all markers info on the map
// each element is in format :
// {
//     marker: marker,
//     infoWindow: infoWindow,
//     isClicked: false
// }
window.globalMarkers = [];


/**
 * The callback function that is called after the google maps script src loads
 * with the api key.
 *
 * Loads the index destination map with all the markers.
 */
function initIndexDestinationMap() {
    window.globalMap = new google.maps.Map(document.getElementById('map'), {
        center: {lat: -43.522057156877615, lng: 172.62360347218828},
        zoom: 5
    });

    initPlacesAutocomplete();

    initDestinationMarkers();
    initMapLegend();
}


/**
 * The callback function that is called after the google maps script src loads
 * with the api key.
 * Adds listener that gets lat and lng out of map onlick click and fills
 * form fields with those values.
 * Add listeners to the lat and lng fields, so on input it centers the map
 * to that location
 *
 * Loads the create destination map without any markers.
 */
function initCreateDestinationMap() {

    window.globalMap = new google.maps.Map(document.getElementById('map'), {
        zoom: 5
    });

    var initLatLng = new google.maps.LatLng(
        -43.522057156877615, 172.62360347218828
    );

    if (document.getElementById("latitude").value !== "" &&
        document.getElementById("longitude").value !== "") {

        initLatLng = new google.maps.LatLng(
            document.getElementById("latitude").value,
            document.getElementById("longitude").value
        );

        window.globalMap.setZoom(8);
    }

    window.globalMap.setCenter(initLatLng);


    document.getElementById("latitude").addEventListener('input', function(event) {

        if (!isNaN(document.getElementById("latitude").value)) {
            var latlng = new google.maps.LatLng(
                document.getElementById("latitude").value,
                window.globalMap.center.lng());

            window.globalMap.setCenter(latlng);

        }
    });

    document.getElementById("longitude").addEventListener('input', function(event) {

        if (!isNaN(document.getElementById("longitude").value)) {

            var latlng = new google.maps.LatLng(
                window.globalMap.center.lat(),
                document.getElementById("longitude").value);

            window.globalMap.setCenter(latlng);
        }
    });

    window.globalMap.addListener('click', function(event) {
        document.getElementById("latitude").value = event.latLng.lat();
        document.getElementById("longitude").value = event.latLng.lng();
    });

    initPlacesAutocomplete();

    initDestinationMarkers();
    initMapLegend();
}

/**
 * Initialises the google places api auto-complete box
 */
function initPlacesAutocomplete() {
    const input = document.getElementById('placesAutocomplete');
    const autocomplete = new google.maps.places.Autocomplete(input);

    // Bind the map's bounds (viewport) property to the autocomplete object,
    // so that the autocomplete requests use the current map bounds for the
    // bounds option in the request.
    autocomplete.bindTo('bounds', window.globalMap);

    // Set the data fields to return when the user selects a place.
    autocomplete.setFields(
        ['address_components', 'geometry', 'icon', 'name']);

    autocomplete.addListener('place_changed', function() {
        const place = autocomplete.getPlace();

        const coordinates = place.geometry.location;
        const address = place.address_components;

        document.getElementById("destName").value = place.name;

        address.forEach((addressItem) => {
            if (addressItem.types.includes("country")) {
                document.getElementById("country").value = addressItem.long_name;

            } else if (addressItem.types.includes("administrative_area_level_1")
                || addressItem.types.includes("administrative_area_level_2")) {
                document.getElementById("district").value = addressItem.long_name;
            }
        });


        document.getElementById("latitude").value = coordinates.lat();
        document.getElementById("longitude").value = coordinates.lng();
    });
}

/**
 * Creates the destination map legend.
 *
 * Code from here
 * https://developers.google.com/maps/documentation/javascript/adding-a-legend
 */
function initMapLegend() {
    let icons = getAllMarkerIcons();

    let legend = document.getElementById('legend');
    for (let key in icons) {
        let type = icons[key];
        let name = type.name;
        let icon = type.url;
        let div = document.createElement('div');
        div.innerHTML = '<img src="' + icon + '"> ' + name;
        legend.appendChild(div);
    }

    window.globalMap.controls[google.maps.ControlPosition.LEFT_BOTTOM].push(legend);
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
 * Gets the HTML for a Destinations infoWindow, for the google map.
 *
 * @param destination A JSON object of the Destination, from an AJAX query from
 *  /users/destinations/getalljson endpoint.
 *
 * @returns {string} A String containing the HTML to display in the markers
 *                   infoWindow.
 */
function getInfoWindowHTML(destination) {
// create the destinations info window
    const destinationId = destination.destId;
    const destinationName = destination.destName;
    const destinationType = destination.destType;
    const destinationCountry = destination.country;
    const destinationDistrict = destination.district;

    let infoWindowHTML;
    // uses a ES6 template string
    infoWindowHTML = `<style>.basicLink {text-underline: #0000EE;}</style>
                      <a class="basicLink" href="/users/destinations/view/${destinationId}">
                        ${destinationName}
                      </a>
                      <div>${destinationType}</div>
                      <div>District: ${destinationDistrict}</div>
                      <div>${destinationCountry}</div>
                      <script src="indexDestination.js"></script>`;

    return infoWindowHTML;
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
            // console.log(destinations);
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
                    content: getInfoWindowHTML(destinations[index])
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
 * Method to check if a lat and long are valid coordinates in the mercerator
 * map projection.
 *
 * @param latitude A float representing latitude
 * @param longitude A float representing longitude
 * @returns {boolean} true if both are valid, false otherwise
 */
function isLatLongValid(latitude, longitude) {
    let isValid = true;

    if (isNaN(latitude) || isNaN(longitude)) {
        isValid = false;
    } else {
        if (-85 <= latitude && latitude <= 85) {
            // valid latitude
        } else {
            // invalid latitude
            isValid = false;
        }

        if (-180 <= longitude && longitude <= 180) {
            // valid longitude
        } else {
            // invalid longitude
            isValid = false;
        }
    }
    return isValid;
}

/**
 * Add's event handlers to all the "fly to map images" so than when they are
 * clicked they center the google map on the appropiate location.
 *
 * This method is called implicitly when the page loads (dont call it explicitly).
 *
 * If somehow the lat and long attributes of the image are invalid, shows an
 * alert box.
 */
window.onload = function() {
    const images = document.getElementsByClassName("flyToImage");

    for (let image of images) {
        image.addEventListener('click', (event) => {
            const targetIMG = event.target;

            const latitude = parseFloat(targetIMG.getAttribute('latitude'));
            const longitude = parseFloat(targetIMG.getAttribute('longitude'));

            if (! isLatLongValid(latitude, longitude)) {
                alert("Error flying to destination on map, sorry!");
            } else {
                window.globalMap.setCenter({
                    lat: latitude,
                    lng: longitude
                });
            }
        })
    }
};