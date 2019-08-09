let visitArray = [];
window.globalMarkers = [];
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
                      <div><button id="addToTripButton" onclick="addSelectedToVisitToTrip(${destination.destId})">Add to trip</button></div>`;

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

                //Get outer divs to set to correct tab and view
                currentlyDisplayedTripId = data.tripId;
                let destTab = document.getElementById("destinationsTabListItem");
                let tripsTab = document.getElementById("tripsTabListItem");
                let tripsDiv = document.getElementById("tripsTab");
                let destDiv = document .getElementById("destinationsTab");
                tripsDiv.setAttribute("class", "tab-pane fade in active");
                destDiv.setAttribute("class", "tab-pane fade");
                destTab.setAttribute('class', "");
                tripsTab.setAttribute('class', "active");

                //Handle outer div
                let singleTripContainer = document.getElementById("singleTripContainer");
                let outerTripDiv = document.createElement("div");
                outerTripDiv.setAttribute("id", "singleTrip_" + data.tripId);
                outerTripDiv.setAttribute("class", "singleTrip");
                outerTripDiv.setAttribute("style", "display: block;");

                //Handle title div
                let titleDiv = createTripTitleDiv(data);
                outerTripDiv.appendChild(titleDiv);


                //Handle Table
                let newTable = createTripTable(data);
                outerTripDiv.appendChild(newTable);

                //Handle delete button
                let deleteButton = document.createElement("button");
                deleteButton.setAttribute("class", "btn btn-danger");
                deleteButton.setAttribute("onclick", "deleteTripRequest(" + data.tripId + ", 'map_home')");
                deleteButton.innerText = "Delete trip";
                outerTripDiv.appendChild(deleteButton);

                // Handle checking the show/hide all button
                document.getElementById('show-hide-all-btn').style.display = 'block';

                //Add outer div to single trip view
                singleTripContainer.appendChild(outerTripDiv);



                //Handle List Group
                let listGroup = document.getElementById('trip-list-group');
                let tripLink = document.createElement('a');
                tripLink.setAttribute('class', "list-group-item list-group-item-action");
                tripLink.innerText = data.tripName + ' | No arrival dates';
                tripLink.setAttribute("onclick", "displayTrip(" + currentlyDisplayedTripId + ", " + data.latitude+ ", "+ data.longitude + ")");

                let formCheckDiv = document.createElement("div");
                formCheckDiv.setAttribute("class", "form-check");

                let tripCheckBox = document.createElement('input');
                tripCheckBox.setAttribute('type', 'checkbox');
                tripCheckBox.setAttribute('id',"Toggle"+data.tripId);
                tripCheckBox.setAttribute('checked', 'true');
                tripCheckBox.setAttribute('onclick', 'toggleTrips(' + data.tripId + ')');
                tripCheckBox.setAttribute('class', 'form-check-input map-check');
                let mapLabel = document.createElement('label');
                mapLabel.setAttribute('class', 'form-check-label');
                mapLabel.setAttribute('for', "toggleMap");
                mapLabel.innerText = 'Show on map';
                formCheckDiv.appendChild(tripCheckBox);
                formCheckDiv.appendChild(mapLabel);
                tripLink.appendChild(formCheckDiv);
                listGroup.appendChild(tripLink);


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
                tripVisitTableRefresh(data);


                for (let i in tripRoutes) {
                    tripRoutes[i].setMap(null);
                }
                tripRoutes =[];
                initTripRoutes();

            },
            error: function(xhr, textStatus, errorThrown){
                const x = document.getElementById("snackbar");

                // Add the "show" class to DIV
                x.className = "show";
                x.innerText = xhr.responseText;
                // After 3 seconds, remove the show class from DIV
                setTimeout(function(){
                    x.className = x.className.replace("show", "");
                }, 3000);
            }
        });
    }
}

function createTripTitleDiv(data) {
    let titleDiv = document.createElement("div");
    titleDiv.setAttribute("style", "margin-top: 15px; height: 30px;");
    let titleText = document.createElement("h4");
    titleText.setAttribute("id", "tripName_" + data.tripId);
    titleText.setAttribute("style","cursor: pointer;");
    titleText.setAttribute("onclick", onclick="toggleEditTripName(true)");
    titleText.innerText = data.tripName;
    let titleInput = document.createElement("input");
    titleInput.setAttribute("style", "display: none;");
    titleInput.setAttribute("type", "text");
    titleInput.setAttribute("id", "tripNameInput_" + data.tripId);
    titleInput.setAttribute("value", data.tripName);
    titleInput.setAttribute("onblur", "updateTripName(this.value)");
    titleDiv.appendChild(titleText);
    titleDiv.appendChild(titleInput);
    return titleDiv;
}

function createTripTable(data) {
    let newTable = document.createElement("div");
    newTable.setAttribute("id", "tripTable_" + data.tripId);
    newTable.setAttribute("class", "table table-hover");


    //Handle Table Head
    let tableHead = document.createElement("thead");
    let tableRowHeaders = document.createElement("tr");
    let nameHeader = document.createElement("th");
    nameHeader.setAttribute("scope", "col");
    nameHeader.innerText = "Name";
    let typeHeader = document.createElement("th");
    typeHeader.setAttribute("scope", "col");
    typeHeader.innerText = "Type";
    let arrivalHeader = document.createElement("th");
    arrivalHeader.setAttribute("scope", "col");
    arrivalHeader.innerText = "Arrival";
    let departureHeader = document.createElement("th");
    departureHeader.setAttribute("scope", "col");
    departureHeader.innerText = "Departure";
    let deleteHeader = document.createElement("th");
    deleteHeader.setAttribute("scope", "col");
    tableRowHeaders.appendChild(nameHeader);
    tableRowHeaders.appendChild(typeHeader);
    tableRowHeaders.appendChild(arrivalHeader);
    tableRowHeaders.appendChild(departureHeader);
    tableRowHeaders.appendChild(deleteHeader);
    tableHead.appendChild(tableRowHeaders);
    newTable.appendChild(tableHead);



    //Handle table body
    let tableBody = document.createElement("tbody");
    tableBody.setAttribute("id", "tripTableBody_"+ data.tripId);
    tableBody.setAttribute("class", "table table-hover");
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
    tableBody.appendChild(newRow);
    newTable.appendChild(tableBody);

    return newTable;

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

function tripVisitTableRefresh(data){
    let targetTable = document.getElementById("placeholderTripTable");
    let targetTripBody = document.getElementById("tripTableBody_" + currentlyDisplayedTripId);
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
    arrivalDateInput.setAttribute('onblur', "updateVisitDate(" + data.visitId+")");
    arrivalDateInput.setAttribute('id', 'arrival_'+data.visitId);
    tableDataArrival.appendChild(arrivalDateInput);
    let tableDataDeparture = document.createElement('td');
    let departureDateInput = document.createElement('input');
    departureDateInput.setAttribute('id', 'departure_'+data.visitId);
    departureDateInput.setAttribute('type', 'date');
    departureDateInput.setAttribute('class', 'tripDateInput');
    departureDateInput.setAttribute('onblur', "updateVisitDate(" + data.visitId+")");
    tableDataDeparture.appendChild(departureDateInput);
    let deleteButton = document.createElement('td');
    let deleteButtonText = document.createElement('a');
    deleteButtonText.innerText = '❌';
    deleteButtonText.setAttribute('style', 'deleteButton');
    let urlForDelete = '/users/trips/edit/' + data.visitId;
    deleteButtonText.setAttribute('onclick', 'sendDeleteVisitRequest(' + '"' + urlForDelete + '"' + ','
        + data[0] + ')');
    deleteButton.appendChild(deleteButtonText);
    tableDataDeparture.appendChild(departureDateInput);

    newRow.appendChild(tableHeader);
    newRow.appendChild(tableDataDestType);
    newRow.appendChild(tableDataArrival);
    newRow.appendChild(tableDataDeparture);
    newRow.appendChild(deleteButton);
    targetTripBody.appendChild(newRow);

    var tripStartLatLng = new google.maps.LatLng(
        data.latitude, data.longitude
    );

    window.globalMap.setCenter(tripStartLatLng);
    window.globalMap.setZoom(9);
}


var tripRoutes = [];


/**
 * Will toggle the flight path of the trip on the map
 * according to the value of the checkbox
 * @param tripid The id of the trip on the map
 */
function toggleTrips(tripid) {
    const checkBox = document.getElementById("Toggle" + tripid);
    if (checkBox.checked === false) {
        tripFlightPaths[tripid].setMap(null);
    } else {
        tripFlightPaths[tripid].setMap(window.globalMap);
    }
    const checkboxes = document.getElementsByClassName("map-check");
    const showHideAllButton = document.getElementById("show-hide-all-btn");
    // === true is important here
    const showHideResult = getAllChecksChecked(checkboxes);
    if (showHideResult === "Checked") {
        showHideAllButton.innerText = "Hide all"
    } else if (showHideResult === "Unchecked") {
        showHideAllButton.innerText = "Show all"
    }
}

/**
 * Finds if ALL checkboxes in a list of checkboxes are checked or unchecked or mixed
 * @param checkboxes list of checkboxes
 * @returns string  "Checked" if all checkboxes are checked,
 *                  "Unchecked" if all checkboxes are unchecked
 *                   and "Do not all match" otherwise
 */
function getAllChecksChecked(checkboxes) {
    let currentStates = null;
    for (let check of checkboxes) {
        if (currentStates == null) {
            currentStates = check.checked;
        } else if (currentStates !== check.checked) {
            return "Do not all match";
        }
    }
    return currentStates ? "Checked" : "Unchecked";
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
            flightPath.path = tripRoutes[tripId];

            google.maps.event.addListener(flightPath, 'click', function(e) {

                displayTrip(tripId, tripRoutes[tripId][0]['lat'], tripRoutes[tripId][0]['lng'])
            });

            if (tripFlightPaths[tripId] != null) {
                if (tripFlightPaths[tripId].path.length !== flightPath.path.length) {
                    tripFlightPaths[tripId].setMap(null);
                    tripFlightPaths[tripId] = flightPath;
                }
            }
            else{
                tripFlightPaths[tripId] = flightPath;
            }
            const checkBox = document.getElementById("Toggle" + tripId);
            if (checkBox.checked === false) {
                tripFlightPaths[tripId].setMap(null);
            } else {
                if (tripFlightPaths[tripId].getMap() == null) {
                    tripFlightPaths[tripId].setMap(window.globalMap);
                }
            }
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
        var data = jQuery('#tripTableBody_'+currentlyDisplayedTripId+' tr').map(function() {
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

/**
 * Changes all checkboxes for showing routes to either true or false and dispatches events for the routes on map
 */
function showHideMapTrips() {
    const button = document.getElementById("show-hide-all-btn");
    const checkboxes = document.getElementsByClassName('map-check');
    if (button.innerText === "Hide all") {
        button.innerText = "Show all";
        for (let check of checkboxes) {
            check.checked = false;
            // Dispatch an event to fire the listener on the checkbox
            const event = new Event('change');
            check.dispatchEvent(event);
        }
    } else {
        button.innerText = "Hide all";
        for (let check of checkboxes) {
            check.checked = true;
            // Dispatch an event to fire the listener on the checkbox
            const event = new Event('change');
            check.dispatchEvent(event);
        }
    }
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
