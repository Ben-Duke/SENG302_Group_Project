var visitArray = [];

let controlContent = document.getElementById('controlContent');

function updateTripsTab(){
    controlContent.innerHTML = '';
    for(let i = 0; i < visitArray.length; i ++){
        let visitDiv = document.createElement('div');
        visitDiv.innerText = visitArray[i]['name'];
        visitDiv.appendChild(document.createElement('br'));

        //Arrival date code
        let arrivalDateDiv = document.createElement('div');
        arrivalDateDiv.appendChild(document.createTextNode("Arrival Date"));
        arrivalDateDiv.appendChild(document.createElement('br'));
        visitDiv.appendChild(arrivalDateDiv);
        arrivalDateDiv.setAttribute('style', 'float:left; padding-left:10%');

        let arrivalDate = document.createElement('input');
        arrivalDate.setAttribute('id', i+"arrivalId");
        arrivalDate.setAttribute('type', "date");
        arrivalDate.setAttribute('onblur', `
        let id = this.id.slice(0,1);
        visitArray[id].arrivalDate = this.value;
        console.log(visitArray[id])`);
        arrivalDate.value =  visitArray[i]['arrivalDate'];
        arrivalDateDiv.appendChild(arrivalDate);

        //Departure date code
        let departureDiv = document.createElement('div');
        departureDiv.appendChild(document.createTextNode("Departure Date"))
        visitDiv.appendChild(departureDiv);
        departureDiv.setAttribute('style', 'display: inline-block')
        departureDiv.appendChild(document.createElement('br'));
        let departureDate = document.createElement('input');
        departureDate.setAttribute('id', i+"departureId");
        departureDate.setAttribute('onblur', `
        let id = this.id.slice(0,1);
        visitArray[id].departureDate = this.value;
        console.log(visitArray[id])`);
        departureDate.setAttribute('type', "date");
        departureDate.value = visitArray[i]['departureDate'];
        departureDiv.appendChild(departureDate);
        visitDiv.appendChild(departureDiv);

        //Delete button code
        let deleteButton = document.createElement('button');
        deleteButton.innerText = "X"
        deleteButton.setAttribute('style', 'background-color:red; text-color:white; color: white; display: inline-block');
        deleteButton.setAttribute('id', i+"deleteId");
        deleteButton.setAttribute('onclick','visitArray.pop(this.id.slice(0,1)); console.log(`deleted item`); updateTripsTab()');
        visitDiv.appendChild(deleteButton);
        controlContent.appendChild(visitDiv);
    }
}


function initMap() {
    var myLatLng = {lat: -43.522057156877615, lng: 172.62360347218828};
    window.globalMap = new google.maps.Map(document.getElementById('map'), {
        center: {lat: -43.522057156877615, lng: 172.62360347218828},
        zoom: 5
    });

    // initPlacesAutocomplete();
    initDestinationMarkers();
    // initMapLegend();
    initTripRoutes();

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
        content: "yay content <br> <button onclick='visitArray.push({name : `marker1`, id:1, arrivalDate: new Date().toISOString().slice(0, 10) , departureDate: new Date().toISOString().slice(0, 10)}); updateTripsTab()' >start a trip/Add Destination</button>"
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
                    console.log(routes[i]);

                    var flightPath = new google.maps.Polyline({
                        path: routes[i],
                        geodesic: true,
                        strokeColor: '#'+(Math.random()*0xFFFFFF<<0).toString(16),
                        strokeOpacity: 1.0,
                        strokeWeight: 2
                    });

                    flightPath.setMap(window.globalMap);

                }
            });

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

