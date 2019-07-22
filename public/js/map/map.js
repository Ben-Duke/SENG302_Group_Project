
function initMap() {
    window.globalMap = new google.maps.Map(document.getElementById('map'), {
        center: {lat: -43.522057156877615, lng: 172.62360347218828},
        zoom: 5
    });

    // initPlacesAutocomplete();
    initDestinationMarkers();
    // initMapLegend();

    initTripRoutes();



}

function initTripRoutes() {

    fetch('/users/trips/fetch/trips_routes_json', {
        method: 'GET'})
    .then(res => res.json())
    .then(routes => {

        for (var i = 0; i < routes.length; i++) {
            // console.log(routes[i]);

            var flightPath = new google.maps.Polyline({
                path: routes[i],
                geodesic: true,
                strokeColor: '#'+(Math.random()*0xFFFFFF<<0).toString(16),
                strokeOpacity: 1.0,
                strokeWeight: 2
            });

            flightPath.setMap(window.globalMap);

        flightPath.addListener('click', function() {
            console.log("hello");
        });


        }
    });

}



var currentlyDisplayedTrip;
/**
 * Displays the given trip in the table and centers on map.
 * @param tripId The id of the trip to be displayed
 * @param startLat the latitude to zoom to
 * @param startLng the longitude to zoom to
 */
function displayTrip(tripId, startLat, startLng) {

    if (currentlyDisplayedTrip !== undefined) {
        document.getElementById("tripTable_"+currentlyDisplayedTrip).style.display = "none";
    }

    currentlyDisplayedTrip = tripId;

    document.getElementById("tripTable_"+tripId).style.display = "initial";


    var tripStartLatLng = new google.maps.LatLng(
        startLat, startLng
    );

    window.globalMap.setCenter(tripStartLatLng);
    window.globalMap.setZoom(9);

}



function initPlacesAutocomplete() {
    var input = document.getElementById('placesAutocomplete');
    var autocomplete = new google.maps.places.Autocomplete(input);

    // Bind the map's bounds (viewport) property to the autocomplete object,
    // so that the autocomplete requests use the current map bounds for the
    // bounds option in the request.
    autocomplete.bindTo('bounds', window.globalMap);

    // Set the data fields to return when the user selects a place.
    autocomplete.setFields(
        ['address_components', 'geometry', 'icon', 'name']);

    autocomplete.addListener('place_changed', function() {
        var place = autocomplete.getPlace();

        console.log(place);

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


