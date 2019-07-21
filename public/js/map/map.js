var vistArray = [];

let controlContent = document.getElementById('controlContent');

function initMap() {
    var myLatLng = {lat: -43.522057156877615, lng: 172.62360347218828};

    let map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: -43.522057156877615, lng: 172.62360347218828},
        zoom: 5
    });

    // initPlacesAutocomplete();
    // initDestinationMarkers();
    // initMapLegend();

    var marker = new google.maps.Marker({
        position: myLatLng,
        map: map,
        title: 'Hello World!'
    });

    var marker2 = new google.maps.Marker({
        position: {lat: -40, lng:176.6},
        map: map,
        title: 'Hello World!'
    });

    marker.setMap(map);
    var infowindow = new google.maps.InfoWindow({
        content: "yay content <br> <button onclick='vistArray.push(`Marker 1`); controlContent.innerText = vistArray'' >start a trip</button>"
    });

    marker.addListener('click', function() {
        infowindow.open(map, marker);
    });

    var infowindow2 = new google.maps.InfoWindow({
        content: "second marker <br> <button onclick='vistArray.push(`Marker 2`); controlContent.innerText = vistArray'>" +
            "Start a trip</button>"
    });

    marker2.addListener('click', function() {
        infowindow2.open(map, marker2);
    });
    marker2.setMap(map);
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

