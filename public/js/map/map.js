var visitArray = [];

let controlContent = document.getElementById('controlContent');

function updateTripsTab(){
    controlContent.innerHTML = '';
    for(let i = 0; i < visitArray.length; i ++){
        var visitDiv = document.createElement('div');
        visitDiv.innerText = visitArray[i]['name'];
        visitDiv.appendChild(document.createElement('br'));
        let arrivalDate = document.createElement('input');
        arrivalDate.setAttribute('id', i+"arrivalid");
        arrivalDate.setAttribute('onblur', `
        console.log(this.id + " would be saved in the array")`);

        arrivalDate.value =  visitArray[i]['arrivalDate'];

        visitDiv.appendChild(arrivalDate);
        let departureDate = document.createElement('input');
        departureDate.setAttribute('id', i+"departureId");
        departureDate.setAttribute('onblur', `
        console.log(this.id + " would be saved in the array")`);
        departureDate.value = visitArray[i]['departureDate'];
        visitDiv.appendChild(departureDate);
        let deleteButton = document.createElement('button');
        deleteButton.setAttribute('id', i+"deleteId");
        deleteButton.setAttribute('onclick','visitArray.pop(this.id.slice(0,1)); console.log(`deleted item`); updateTripsTab()');
        visitDiv.appendChild(deleteButton);
        controlContent.appendChild(visitDiv);
    }
}
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
        title: 'Hello World!',
        destId: 1
    });

    var marker2 = new google.maps.Marker({
        position: {lat: -40, lng:176.6},
        map: map,
        title: 'Hello World!',
        destId: 2
    });

    marker.setMap(map);
    var infowindow = new google.maps.InfoWindow({
        content: "yay content <br> <button onclick='visitArray.push({name : `marker1`, id:1, arrivalDate: new Date().toISOString().slice(0, 10) , departureDate: new Date().toISOString().slice(0, 10)}); updateTripsTab()' >start a trip</button>"
    });

    marker.addListener('click', function() {
        infowindow.open(map, marker);
    });

    var infowindow2 = new google.maps.InfoWindow({
        content: "second marker <br> <button onclick='visitArray.push(" +
            "{name : `marker2`, id:1,  arrivalDate: new Date().toISOString().slice(0, 10)," +
            " departureDate: new Date().toISOString().slice(0, 10)}); updateTripsTab()'>" +
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

