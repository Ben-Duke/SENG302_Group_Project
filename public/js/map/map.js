var visitArray = [];
const updateVisitDateUrl = "/user/trips/visit/dates/";
const colors = ['6b5b95', 'feb236', 'd64161', 'ff7b25',
    '6b5b95', '86af49', '3e4444', 'eca1a6', 'ffef96', 'bc5a45', 'c1946a'];


let tripFlightPaths = {};

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
                      <a class="basicLink" href="javascript:;" onclick="viewDestination(${destination.destId})">
                        ${destinationName}
                      </a>
                      <div>${destinationType}</div>
                      <div>District: ${destinationDistrict}</div>
                      <div>${destinationCountry}</div>
                      <div><button id="addToTripButton" onclick="addSelectedToVisitToTrip(${destination.destId})">Add to trip</button></div>
                      <script src="indexDestination.js"></script>`;

    return infoWindowHTML;
}

function addSelectedToVisitToTrip(destId){
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
            success: function(data){
                console.log(data);

                //currentlyDisplayedTripId = data.tripId;
                let destTab = document.getElementById("destinationsTabListItem");
                let tripsTab = document.getElementById("tripsTabListItem");
                let tripsDiv = document.getElementById("tripsTab");
                let destDiv = document .getElementById("destinationsTab");
                tripsDiv.setAttribute("class", "tab-pane fade in active");
                destDiv.setAttribute("class", "tab-pane fade");
                destTab.setAttribute('class', "");
                tripsTab.setAttribute('class', "active");



                let targetTable = document.getElementById("placeholderTripTable");
                let tableBody = document.createElement("tbody");
                tableBody.setAttribute("id", "tripTableBody_"+ data.tripId);
                tableBody.style.display = "none";
                let newRow = document.createElement('tr');
                newRow.setAttribute('id', "visit_row_" + data.visitId);
                let tableHeader = document.createElement('th');
                tableHeader.setAttribute('scope', 'row');
                tableHeader.innerText = data.visitName;
                let tableDataDestType = document.createElement('td');
                tableDataDestType.innerText = data.destType;
                let tableDataArrival = document.createElement('td');
                let arrivalDateInput = document.createElement('input');
                arrivalDateInput.setAttribute('type', 'date');
                arrivalDateInput.setAttribute('class', 'tripDateInput');
                arrivalDateInput.setAttribute('onblur', "updateVisitDate(" + data[0]+")");
                arrivalDateInput.setAttribute('id', 'arrival_'+data[0]);
                tableDataArrival.appendChild(arrivalDateInput);
                let tableDataDeparture = document.createElement('td');
                let departureDateInput = document.createElement('input');
                departureDateInput.setAttribute('id', 'departure_'+data[0]);
                departureDateInput.setAttribute('type', 'date');
                departureDateInput.setAttribute('class', 'tripDateInput');
                departureDateInput.setAttribute('onblur', "updateVisitDate(" + data[0]+")");
                let deleteButton = document.createElement('td');
                let deleteButtonText = document.createElement('a');
                deleteButtonText.innerText = '❌';
                deleteButtonText.setAttribute('style', 'deleteButton');
                let urlForDelete = '/users/trips/edit/' + data.visitId;//data.tripId;
                deleteButtonText.setAttribute('onclick', 'sendDeleteVisitRequest(' + '"' + urlForDelete + '"' + ','
                    + data.visitId + ')');
                deleteButton.appendChild(deleteButtonText);
                tableDataDeparture.appendChild(departureDateInput);
                newRow.appendChild(tableHeader);
                newRow.appendChild(tableDataDestType);
                newRow.appendChild(tableDataArrival);
                newRow.appendChild(tableDataDeparture);
                newRow.appendChild(deleteButton);
                //tableBody.appendChild(newRow);
                targetTable.appendChild(newRow);

                let listGroup = document.getElementById('trip-list-group');
                let groupDiv = document.createElement('div');

                let tripLink = document.createElement('a');
                let tripCheckBox = document.createElement('input');
                tripCheckBox.setAttribute('type', 'checkbox');
                tripCheckBox.setAttribute('id',"Toggle"+data.tripId);
                tripCheckBox.setAttribute('checked', 'true');
                tripCheckBox.setAttribute('onclick', 'toggleTrips(' + data.tripId + ')');
                tripCheckBox.setAttribute('class', 'form-check-label');
                let mapLabel = document.createElement('label');
                mapLabel.setAttribute('class', 'form-check-label');
                mapLabel.setAttribute('for', "toggleMap");
                mapLabel.innerText = 'Show on map';

                tripLink.setAttribute('class', "list-group-item list-group-item-action");
                tripLink.innerText = data.tripName + ' | No arrival dates';
                tripLink.setAttribute("onclick", "displayTrip(" + currentlyDisplayedTripId + ", " + data.latitude+ ", "+ data.longitude + ")");
                listGroup.appendChild(tripLink);
                listGroup.appendChild(groupDiv);
                groupDiv.appendChild(tripCheckBox);
                groupDiv.appendChild(mapLabel);
                displayTrip(currentlyDisplayedTripId, data.latitude, data.longitude);




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
            success: function(data){
                console.log(data);
                tripVisittableRefresh(data);


                for (let i in tripRoutes) {
                    tripRoutes[i].setMap(null);
                }
                tripRoutes =[];
                initTripRoutes();

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

function tripVisittableRefresh(data){
    console.log("refresh called ");
    let targetTable = document.getElementById("placeholderTripTable");
    let newRow = document.createElement('tr');
    newRow.setAttribute('id', data[0]);
    let tableHeader = document.createElement('th');
    tableHeader.setAttribute('scope', 'row');
    tableHeader.innerText = data[1];
    let tableDataDestType = document.createElement('td');
    tableDataDestType.innerText = data[2];
    let tableDataArrival = document.createElement('td');
    let arrivalDateInput = document.createElement('input');
    arrivalDateInput.setAttribute('type', 'date');
    arrivalDateInput.setAttribute('class', 'tripDateInput');
    arrivalDateInput.setAttribute('onblur', "updateVisitDate(" + data[0]+")");
    arrivalDateInput.setAttribute('id', 'arrival_'+data[0]);
    tableDataArrival.appendChild(arrivalDateInput);
    let tableDataDeparture = document.createElement('td');
    let departureDateInput = document.createElement('input');
    departureDateInput.setAttribute('id', 'departure_'+data[0]);
    departureDateInput.setAttribute('type', 'date');
    departureDateInput.setAttribute('class', 'tripDateInput');
    departureDateInput.setAttribute('onblur', "updateVisitDate(" + data[0]+")");
    tableDataDeparture.appendChild(departureDateInput);

    newRow.appendChild(tableHeader);
    newRow.appendChild(tableDataDestType);
    newRow.appendChild(tableDataArrival);
    newRow.appendChild(tableDataDeparture);
    targetTable.appendChild(newRow);
}


var tripRoutes = [];


/**
 * Will toggle the flight path of the trip on the map
 * according to the value of the checkbox
 * @param tripid The id of the trip on the map
 */
function toggleTrips(tripid) {
    console.log(tripid)
    var checkBox = document.getElementById("Toggle" + tripid);
    if (checkBox.checked === false) {
        tripFlightPaths[tripid].setMap(null);
    } else {
        tripFlightPaths[tripid].setMap(window.globalMap);
    }
}

function initTripRoutes() {

    fetch('/users/trips/fetch/trips_routes_json', {
        method: 'GET'})
        .then(res => res.json())
        .then(tripRoutes => {
            let color;

        for (let tripId in tripRoutes) {
            color = colors[Math.floor(Math.random()*colors.length)];

            let flightPath = new google.maps.Polyline({
                path: tripRoutes[tripId],
                geodesic: true,
                strokeColor: '#' + color,
                strokeOpacity: 1.0,
                strokeWeight: 2
            });

            tripFlightPaths[tripId] = flightPath;

            // tripFlightPaths.push({'tripId': flightPath});

            flightPath.setMap(window.globalMap);

        }
    });
}



let currentlyDisplayedTripId;
/**
 * Displays the given trip in the table and centers on map.
 * @param tripId The id of the trip to be displayed
 * @param startLat the latitude to zoom to
 * @param startLng the longitude to zoom to
 */
function displayTrip(tripId, startLat, startLng) {
    console.log(currentlyDisplayedTripId);
    console.log(tripId)
    let checkBox = document.getElementById("Toggle" + tripId);
    console.log(checkBox);
    if (checkBox.checked === true) {
        if (currentlyDisplayedTripId !== undefined) {
            document.getElementById("singleTrip_" + currentlyDisplayedTripId).style.display = "none";
        } else {
            document.getElementById("placeholderTripTable").style.display = "none";
        }

        currentlyDisplayedTripId = tripId;

        document.getElementById("singleTrip_" + tripId).style.display = "block";
        console.log("got here");


        var tripStartLatLng = new google.maps.LatLng(
            startLat, startLng
        );

        window.globalMap.setCenter(tripStartLatLng);
        window.globalMap.setZoom(9);
    }

}



$('tbody').sortable({
    axis: 'y',
    update: function (event, ui) {
        var token =  $('input[name="csrfToken"]').attr('value');
        $.ajaxSetup({
            beforeSend: function(xhr) {
                xhr.setRequestHeader('Csrf-Token', token);
            }
        });
        var data = jQuery('#tripTableBody_'+currentlyDisplayedTripId+' tr').map(function(){
            return jQuery (this).attr("id");
        }).get();
        var url = '/users/trips/edit/' + currentlyDisplayedTripId;
        // POST to server using $.post or $.ajax

        // console.log(data);

        $.ajax({
            data : JSON.stringify(data),
            contentType : 'application/json',
            type: 'PUT',
            url: url,
            success: function(data, textStatus, xhr) {

                if(xhr.status == 200) {
                    //This is an inefficient way of update the route
                    for (let tripId in tripFlightPaths) {
                        tripFlightPaths[tripId].setMap(null);
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

let currentlyDisplayedDestId;

function displayDestination(destId, startLat, startLng) {

    if (currentlyDisplayedDestId !== undefined) {
        document.getElementById("singleDestination_"+currentlyDisplayedDestId).style.display = "none";
    }

    currentlyDisplayedDestId = destId;

    document.getElementById("singleDestination_"+destId).style.display = "block";


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

function toggleEditTripName(toEdit) {
    if (toEdit) {
        document.getElementById("tripName_"+currentlyDisplayedTripId).style.display = 'none';
        document.getElementById("tripNameInput_"+currentlyDisplayedTripId).style.display = 'inline';
        document.getElementById("tripNameInput_"+currentlyDisplayedTripId).focus();
    } else {
        document.getElementById("tripName_"+currentlyDisplayedTripId).style.display = 'inline';
        document.getElementById("tripNameInput_"+currentlyDisplayedTripId).style.display = 'none'
    }
}

function updateTripName(newName) {

    let token = $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function (xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
        url: '/user/trips/edit/' + currentlyDisplayedTripId,
        method: "PATCH",
        data: JSON.stringify(newName),
        contentType: 'application/json',
        success: function (data, textStatus, xhr) {
            if (xhr.status == 200) {
                document.getElementById("tripName_" + currentlyDisplayedTripId).innerText = newName;
                toggleEditTripName(false);
            }
            else {

            }
        },
        error: function (xhr, settings) {
            if (xhr.status == 400) {
                document.getElementById("tripNameInput_" + currentlyDisplayedTripId).value =
                    document.getElementById("tripName_" + currentlyDisplayedTripId).innerText;
            }
            else if (xhr.status == 403) {
            }
            else {
            }
        }
    });

}
    function deleteTripRequest(tripId, url) {
        let token = $('input[name="csrfToken"]').attr('value');
        $.ajaxSetup({
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Csrf-Token', token);
            }
        });
        $.ajax({
            url: '/users/trips/' + tripId,
            method: "DELETE",
            success: function (res) {
                window.location = url;
            }
        });
    }


    function sendDeleteVisitRequest(url, visitId) {
        console.log(url);
        let token = $('input[name="csrfToken"]').attr('value');
        $.ajaxSetup({
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Csrf-Token', token);
            }
        });
        $.ajax({
            url: url,
            method: "DELETE",
            contentType: 'application/json',
            success: function (data, textStatus, xhr) {
                if (xhr.status == 200) {
                    console.log("t1")
                    console.log("visit_row_" + visitId);
                    document.getElementById("visit_row_" + visitId).remove();
                    console.log("t2")
                }
                else {
                    console.log("error in success function");
                }
            },
            error: function (xhr, settings) {
                if (xhr.status == 400) {
                    console.log("400 error");
                }
                else if (xhr.status == 403) {
                    console.log("403 error");
                }
                else {
                }
            }
        });
    }


    function updateVisitDate(visitId) {
        let arrival = document.getElementById("arrival_" + visitId).value;
        let departure = document.getElementById("departure_" + visitId).value;

        let data = {
            arrival: arrival,
            departure: departure
        };

        let token = $('input[name="csrfToken"]').attr('value');
        $.ajaxSetup({
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Csrf-Token', token);
            }
        });
        $.ajax({
            url: updateVisitDateUrl + visitId,
            method: "PATCH",
            data: JSON.stringify(data),
            contentType: 'application/json',
            success: function (data, textStatus, xhr) {
                if (xhr.status == 200) {

                }
                else {

                }
            },
            error: function (xhr, settings) {
                if (xhr.status == 400) {
                }
                else if (xhr.status == 403) {
                }
                else {
                }
            }
        });

}