let visitArray = [];
const updateVisitDateUrl = "/user/trips/visit/dates/";
const colors = ['6b5b95', 'feb236', 'd64161', 'ff7b25',
    '6b5b95', '86af49', '3e4444', 'eca1a6', 'ffef96', 'bc5a45', 'c1946a'];

let map;
window.globalMarkers = [];
let tripFlightPaths = {};
let isNewTrip = false;

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
                      <a class="basicLink" href="/users/destinations/view/${destination.destId}" onclick="viewDestination(${destination.destId})">
                        ${destinationName}
                      </a>
                      <div>${destinationType}</div>
                      <div>District: ${destinationDistrict}</div>
                      <div>${destinationCountry}</div>
                      <div><button id="addToTripButton" onclick="addSelectedToVisitToTrip(${destination.destId})">Add to trip</button></div>
                      <script src="indexDestination.js"></script>`;

    return infoWindowHTML;
}

/**
 * Sets the global dest id to the destination id then opens the modal based on the dest id.
 * @param destid
 */
function viewDestination(destid){
    getIdFromRow = destid;
    $('#orderModal').modal('show');
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

function addSelectedToVisitToTrip(destId){
    if(currentlyDisplayedTripId == null){
        //Start a new trip
        isNewTrip = true;
        let data = '';
        let url = "/users/trips/createFromJS/" + destId;
        // POST to server using $.post or $.ajax
        $.ajax({
            data : JSON.stringify(data),
            contentType : 'application/json',
            type: 'POST',
            url: url,
            success: function(data){

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
                addTripRoutes(data.tripId);
                displayTrip(data.tripId, data.latitude, data.longitude);


            },
            error: function(xhr, textStatus, errorThrown){
                alert(errorThrown);
            }
        });
    }
    else {
        let data = '';
        let url = '/users/trips/' + currentlyDisplayedTripId + '/addVisit/' + destId;
        // POST to server using $.post or $.ajax
        $.ajax({
            data : JSON.stringify(data),
            contentType : 'application/json',
            type: 'POST',
            url: url,
            success: function(data){
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

    map = window.globalMap = new google.maps.Map(document.getElementById('map'), {
        center: {lat: -43.522057156877615, lng: 172.62360347218828},
        zoom: 5,
        mapTypeControl: true,
        mapTypeControlOptions: {
            style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR,
            position: google.maps.ControlPosition.TOP_RIGHT
        }
    });

    initPlacesAutocompleteSearch();
    initDestinationMarkers();
    initMapLegend();
    initTripRoutes();



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

function tripVisittableRefresh(data){
    let targetTable = document.getElementById("placeholderTripTable");
    let targetTrip = document.getElementById("tripTable_" + currentlyDisplayedTripId);
    let newRow = document.createElement('tr');
    newRow.setAttribute('id', "visit_row_" + data[0]);
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
    let deleteButton = document.createElement('td');
    let deleteButtonText = document.createElement('a');
    deleteButtonText.innerText = '❌';
    deleteButtonText.setAttribute('style', 'deleteButton');
    let urlForDelete = '/users/trips/edit/' + data[0];//data.tripId;
    deleteButtonText.setAttribute('onclick', 'sendDeleteVisitRequest(' + '"' + urlForDelete + '"' + ','
        + data[0] + ')');
    deleteButton.appendChild(deleteButtonText);
    tableDataDeparture.appendChild(departureDateInput);

    newRow.appendChild(tableHeader);
    newRow.appendChild(tableDataDestType);
    newRow.appendChild(tableDataArrival);
    newRow.appendChild(tableDataDeparture);
    newRow.appendChild(deleteButton);
    if(isNewTrip === false) {
        targetTrip.appendChild(newRow);
    }
    else{
        targetTable.appendChild(newRow);
    }
    displayTrip(currentlyDisplayedTripId, data.latitude, data.longitude);
}


var tripRoutes = [];


/**
 * Will toggle the flight path of the trip on the map
 * according to the value of the checkbox
 * @param tripid The id of the trip on the map
 */
function toggleTrips(tripid) {
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

function addTripRoutes(newTripId) {

    fetch('/users/trips/fetch/trips_routes_json', {
        method: 'GET'})
        .then(res => res.json())
        .then(tripRoutes => {
            let color;

            for (let tripId in tripRoutes) {
                if(tripId == newTripId) {
                    color = colors[Math.floor(Math.random() * colors.length)];

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
    if(tripId !== currentlyDisplayedTripId && currentlyDisplayedTripId !== undefined) {
        if(document.getElementById("singleTrip_" + currentlyDisplayedTripId) != null) {
            document.getElementById("singleTrip_" + currentlyDisplayedTripId).style.display = "none";
        }
        isNewTrip = false;
        currentlyDisplayedTripId = undefined;
    }
    let checkBox = document.getElementById("Toggle" + tripId);
    if (checkBox.checked === true) {
        if (currentlyDisplayedTripId !== undefined) {
            document.getElementById("singleTrip_" + currentlyDisplayedTripId).style.display = "none";
        } else {
            document.getElementById("placeholderTripTable").style.display = "none";
        }

        currentlyDisplayedTripId = tripId;
        if(isNewTrip === false) {
            if(document.getElementById("placeholderTripTable").style.display != "none") {
                document.getElementById("placeholderTripTable").style.display = "none";
            }
            document.getElementById("singleTrip_" + tripId).style.display = "block";
        }
        else{
            document.getElementById("placeholderTripTable").style.display = "block";
        }

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
    document.getElementById("createDestination").style.display = "none";

    currentlyDisplayedDestId = destId;

    document.getElementById("singleDestination_"+destId).style.display = "block";


    let tripStartLatLng = new google.maps.LatLng(
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
                    document.getElementById("visit_row_" + visitId).remove();
                    document.getElementById('undoButton').classList.remove('disabled');
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
                    document.getElementById('undoButton').classList.remove('disabled');
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

/**
 * Initialises the google places api auto-complete box
 */
function initPlacesAutocompleteSearch() {
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

        let data = {
            name: place.name,
            country: '',
            district: '',
            latitude: coordinates.lat(),
            longitude: coordinates.lng(),
        };

        address.forEach((addressItem) => {
            if (addressItem.types.includes("country")) {
                data.country = addressItem.long_name;

            } else if (addressItem.types.includes("administrative_area_level_1")
                || addressItem.types.includes("administrative_area_level_2")) {
                data.district = addressItem.long_name;
            }
        });

        let token =  $('input[name="csrfToken"]').attr('value');
        $.ajaxSetup({
            beforeSend: function(xhr) {
                xhr.setRequestHeader('Csrf-Token', token);
            }
        });
        $.ajax({
            url: '/users/destination/exists',
            method: "POST",
            data: JSON.stringify(data),
            contentType : 'application/json',
            success: function(data, textStatus, xhr){
                if(xhr.status == 200) {
                    let destLatLng = new google.maps.LatLng(
                        coordinates.lat(), coordinates.lng()
                    );

                    window.globalMap.setCenter(destLatLng);
                    window.globalMap.setZoom(10);
                }
                else if (xhr.status == 201) {
                    $('[href="#destinationsTab"]').tab('show');
                    document.getElementById('createDestination').style.display = 'block';

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
                    console.log("Done the request for search")
                    moveTo = new google.maps.LatLng(coordinates.lat(), coordinates.lng());
                    map.panTo(moveTo)
                    map.setZoom(14);
                }
            },
            error: function(xhr, settings){
                if(xhr.status == 400) {
                }
                else if(xhr.status == 404){

                }
                else{
                }
            }
        });


    });
}