var visitArray = [];

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
    const destinationName = destination.destName;
    const destinationType = destination.destType;
    const destinationCountry = destination.country;
    const destinationDistrict = destination.district;


    let infoWindowHTML;
    // uses a ES6 template string
    infoWindowHTML = `<style>.basicLink {text-underline: #0000EE;}</style>
                      <a class="basicLink" href="javascript:;" onclick="viewDestination(${destination.destid})">
                        ${destinationName}
                      </a>
                      <div>${destinationType}</div>
                      <div>District: ${destinationDistrict}</div>
                      <div>${destinationCountry}</div>
                      <div><button id="addToTripButton" onclick="addSelectedToVistToTrip(${destination.destid})">Add to trip</button></div>
                      <script src="indexDestination.js"></script>`;

    return infoWindowHTML;
}

function addSelectedToVistToTrip(destId){
    if(currentlyDisplayedTripId == null){
        //Start a new trip
        console.log("No trip, creating a new one");
        let data = '';
        let url = "/users/trips/createFromJS/" + destId;
        // POST to server using $.post or $.ajax
        $.ajax({
            data : JSON.stringify(data),
            contentType : 'application/json',
            type: 'POST',
            url: url,
            success: function(data, textStatus, xhr){
                console.log(data);

            // <a onclick="displayTrip(@trip.getTripid(),
            // @trip.getOrderedVisits().get(0).getDestination().getLatitude(),
            // @trip.getOrderedVisits().get(0).getDestination().getLongitude())"
            // class="list-group-item list-group-item-action">
            //         @if(trip.getTripStart() != null){
            //     @trip.getTripName() | Arrival date: @trip.getTripStart()
            //             } else{
            //     @trip.getTripName | No arrival dates
            //     }
            //
                currentlyDisplayedTripId = data.tripId;
                /*let destTab = document.getElementById("destinationsTabListItem");
                let tripsTab = document.getElementById("tripsTabListItem");
                destTab.setAttribute('class', "");
                tripsTab.setAttribute('class', "active");*/



                let targetTable = document.getElementById("tripTable");
                let tableBody = document.createElement("tbody");
                tableBody.setAttribute("id", "tripTable_"+ data.tripId);
                tableBody.style.display = "none";
                let newRow = document.createElement('tr');
                newRow.setAttribute('id', data.visitId);
                let tableHeader = document.createElement('th');
                tableHeader.setAttribute('scope', 'row');
                tableHeader.innerText = data.visitName;
                let tableDataDestType = document.createElement('td');
                tableDataDestType.innerText = data.destType;
                let tableDataArrival = document.createElement('td');
                tableDataArrival.innerText = data.arrival;
                let tableDataDeparture = document.createElement('td');
                tableDataDeparture.innerText = data.departure;
                newRow.appendChild(tableHeader);
                newRow.appendChild(tableDataDestType);
                newRow.appendChild(tableDataArrival);
                newRow.appendChild(tableDataDeparture);
                tableBody.appendChild(newRow);
                targetTable.appendChild(tableBody);

                let listGroup = document.getElementById('trip-list-group');
                let tripLink = document.createElement('a');
                tripLink.onclick = displayTrip(currentlyDisplayedTripId, data.latitude, data.longitude);
                listGroup.appendChild(tripLink);




            },
            error: function(xhr, textStatus, errorThrown){
                alert(errorThrown);
            }
        });
    }
    else {
        console.log("DestId is " + destId);
        let data = '';
        let url = '/users/trips/' + currentlyDisplayedTripId + '/addVisit/' + destId;
        // POST to server using $.post or $.ajax
        $.ajax({
            data : JSON.stringify(data),
            contentType : 'application/json',
            type: 'POST',
            url: url,
            success: function(data, textStatus, xhr){
                console.log(data);
                tripVisttableRefresh(data);

                //displayTrip(currentlyDisplayedTripId, data.latitude, data.longitude);

            },
            error: function(xhr, textStatus, errorThrown){
                alert(errorThrown);
            }
        });
    }
}

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

function tripVisttableRefresh(data){
    let targetTable = document.getElementById("tripTable_"+ currentlyDisplayedTripId);
    let newRow = document.createElement('tr');
    newRow.setAttribute('id', data[0]);
    let tableHeader = document.createElement('th');
    tableHeader.setAttribute('scope', 'row');
    tableHeader.innerText = data[1];
    let tableDataDestType = document.createElement('td');
    tableDataDestType.innerText = data[2];
    let tableDataArrival = document.createElement('td');
    tableDataArrival.innerText = "";
    let tableDataDeparture = document.createElement('td');
    tableDataDeparture.innerText = "";

    newRow.appendChild(tableHeader);
    newRow.appendChild(tableDataDestType);
    newRow.appendChild(tableDataArrival);
    newRow.appendChild(tableDataDeparture);
    targetTable.appendChild(newRow);
}


var tripRoutes = [];

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

                tripRoutes.push(flightPath);

            flightPath.setMap(window.globalMap);

        }
    });
}



var currentlyDisplayedTripId;
/**
 * Displays the given trip in the table and centers on map.
 * @param tripId The id of the trip to be displayed
 * @param startLat the latitude to zoom to
 * @param startLng the longitude to zoom to
 */
function displayTrip(tripId, startLat, startLng) {

    if (currentlyDisplayedTripId !== undefined) {
        document.getElementById("tripTable_"+currentlyDisplayedTripId).style.display = "none";
    }

    currentlyDisplayedTripId = tripId;

    document.getElementById("tripTable_"+tripId).style.display = "table-row-group";


    var tripStartLatLng = new google.maps.LatLng(
        startLat, startLng
    );

    window.globalMap.setCenter(tripStartLatLng);
    window.globalMap.setZoom(9);

}



$('tbody').sortable({
    axis: 'y',
    update: function (event, ui) {
        var token =  $('input[name="csrfToken"]').attr('value')
        $.ajaxSetup({
            beforeSend: function(xhr) {
                xhr.setRequestHeader('Csrf-Token', token);
            }
        });
        var data = jQuery('#myTable tr').map(function(){
            return jQuery (this).attr("id");
        }).get();
        var url = '/users/trips/edit/' + currentlyDisplayedTripId;
        // POST to server using $.post or $.ajax
        $.ajax({
            data : JSON.stringify(data),
            contentType : 'application/json',
            type: 'PUT',
            url: url,
            success: function(data, textStatus, xhr){

                if(xhr.status == 200) {

                    //This is an inefficient way of update the route
                    for (var i in tripRoutes) {
                        tripRoutes[i].setMap(null);

                    }
                    initTripRoutes();

                }
                else{
                }
            },
            error: function(xhr, textStatus, errorThrown){
                $('tbody').sortable('cancel');
                alert("You cannot visit the same destination twice in a row!");
            }
        });
    }
});




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




// let controlContent = document.getElementById('controlContent');
//
// function updateTripsTab(){
//     controlContent.innerHTML = '';
//     for(let i = 0; i < visitArray.length; i ++){
//         let visitDiv = document.createElement('div');
//         visitDiv.innerText = visitArray[i]['name'];
//         visitDiv.appendChild(document.createElement('br'));
//
//         //Arrival date code
//         let arrivalDateDiv = document.createElement('div');
//         arrivalDateDiv.appendChild(document.createTextNode("Arrival Date"));
//         arrivalDateDiv.appendChild(document.createElement('br'));
//         visitDiv.appendChild(arrivalDateDiv);
//         arrivalDateDiv.setAttribute('style', 'float:left; padding-left:10%');
//
//         let arrivalDate = document.createElement('input');
//         arrivalDate.setAttribute('id', i+"arrivalId");
//         arrivalDate.setAttribute('type', "date");
//         arrivalDate.setAttribute('onblur', `
//         let id = this.id.slice(0,1);
//         visitArray[id].arrivalDate = this.value;
//         console.log(visitArray[id])`);
//         arrivalDate.value =  visitArray[i]['arrivalDate'];
//         arrivalDateDiv.appendChild(arrivalDate);
//
//         //Departure date code
//         let departureDiv = document.createElement('div');
//         departureDiv.appendChild(document.createTextNode("Departure Date"))
//         visitDiv.appendChild(departureDiv);
//         departureDiv.setAttribute('style', 'display: inline-block')
//         departureDiv.appendChild(document.createElement('br'));
//         let departureDate = document.createElement('input');
//         departureDate.setAttribute('id', i+"departureId");
//         departureDate.setAttribute('onblur', `
//         let id = this.id.slice(0,1);
//         visitArray[id].departureDate = this.value;
//         console.log(visitArray[id])`);
//         departureDate.setAttribute('type', "date");
//         departureDate.value = visitArray[i]['departureDate'];
//         departureDiv.appendChild(departureDate);
//         visitDiv.appendChild(departureDiv);
//
//         //Delete button code
//         let deleteButton = document.createElement('button');
//         deleteButton.innerText = "X"
//         deleteButton.setAttribute('style', 'background-color:red; text-color:white; color: white; display: inline-block');
//         deleteButton.setAttribute('id', i+"deleteId");
//         deleteButton.setAttribute('onclick','visitArray.pop(this.id.slice(0,1)); console.log(`deleted item`); updateTripsTab()');
//         visitDiv.appendChild(deleteButton);
//         controlContent.appendChild(visitDiv);
//     }
// }


