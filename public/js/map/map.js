let visitArray = [];
const updateVisitDateUrl = "/user/trips/visit/dates/";
const colors = ['6b5b95', 'feb236', 'd64161', 'ff7b25',
    '6b5b95', '86af49', '3e4444', 'eca1a6', 'ffef96', 'bc5a45', 'c1946a'];

let map;
window.globalMarkers = [];
let markerCluster;
let tripFlightPaths = {};
let isNewTrip = false;
let geoCoder;
let destMarker;
let tripPageNum = 0;
let paginatedTripData;


async function initMap() {

    map = window.globalMap = new google.maps.Map(document.getElementById('map'), {
        center: {lat: -43.522057156877615, lng: 172.62360347218828},
        zoom: 5,
        mapTypeControl: true,
        mapTypeControlOptions: {
            style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR,
            position: google.maps.ControlPosition.TOP_RIGHT
        },
    });

    geoCoder = new google.maps.Geocoder;

    initPlacesAutocompleteSearch();
    initDestinationMarkers();
    initMapLegend();
    initMapPositionListeners();
    hideTagEditor();
    getPublicDestinations(1, 20, null);

    checkTripVisits();
    tripPageNum = 1;
    let tripJSON = await getPaginatedTripResults(tripPageNum, 5);
    let trips  = tripJSON.trips;
    let tripCount = tripJSON.tripCount;
    if (tripCount > 0) {
        setTripPaginationLinks(tripCount, 5);
    }

    initTripRoutes();
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
                      <div><button id="addToTripButton" onclick="addSelectedToVisitToTrip(${destination.destId}, false)">Add to trip</button>
                      <button id="createNewTripButton" onclick="createNewTrip(${destination.destId})">Start trip</button></div>`;
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

        let img = document.createElement('img');
        img.setAttribute("src", icon);
        let span = document.createElement('span');
        span.innerText = name;

        div.appendChild(img);
        div.appendChild(span);
        legend.appendChild(div);
    }

    window.globalMap.controls[google.maps.ControlPosition.LEFT_BOTTOM].push(legend);
}

async function createNewPaginatedTrip() {
    let paginationList = document.getElementById("trip-pagination-list");
    while (paginationList.firstChild) {
        paginationList.removeChild(paginationList.firstChild);
    }


    let tripJSON = await getPaginatedTripResults(tripPageNum, 5);


    let tripCount = tripJSON.tripCount;
    let numPages = Math.ceil(tripCount/5);

    tripJSON = await getPaginatedTripResults(numPages, 5);
    let trips  = tripJSON.trips;

    if (tripCount > 0) {
        setTripPaginationLinks(tripCount, 5);
    }
    let trip = trips[trips.length-1];
    let data = trip;

    await jumpToTripPage(numPages);


    $('tbody').sortable({
        axis: 'y',
        update: function (event, ui) {
            swapVisitOnSort();
        }
    });


    displayTrip(trip.tripId, trip.visits[0].lat, trip.visits[0].lng);

}

function createNewTrip(destId) {
    if(currentlyDisplayedTripId === undefined) {
        addSelectedToVisitToTrip(destId, false)
    } else {
        addSelectedToVisitToTrip(destId, true)
    }
}

function addSelectedToVisitToTrip(destId, startTrip){
    if(currentlyDisplayedTripId === undefined || (startTrip && currentlyDisplayedTripId !== undefined)) {
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
            success: async function(data) {

                isNewTrip = false;
                createNewPaginatedTrip();


                // document.getElementById("placeholderTripTable").style.display = "none";
                //
                // let destTab = document.getElementById("destinationsTabListItem");
                // let tripsTab = document.getElementById("tripsTabListItem");
                // let tripsDiv = document.getElementById("tripsTab");
                // let destDiv = document .getElementById("destinationsTab");
                // tripsDiv.setAttribute("class", "tab-pane fade in active");
                // destDiv.setAttribute("class", "tab-pane fade");
                // destTab.setAttribute('class', "");
                // tripsTab.setAttribute('class', "active");
                //
                // //Handle outer div
                // let singleTripContainer = document.getElementById("singleTripContainer");
                // let outerTripDiv = document.createElement("div");
                // outerTripDiv.setAttribute("id", "singleTrip_" + data.tripId);
                // outerTripDiv.setAttribute("class", "singleTrip");
                // outerTripDiv.setAttribute("style", "display: block;");
                //
                // //Handle title div
                // let titleDiv = createTripTitleDiv(data);
                // outerTripDiv.appendChild(titleDiv);
                //
                //
                // //Handle Table
                // let newTable = createTripTable(data);
                // outerTripDiv.appendChild(newTable);
                //
                // //Handle delete button
                // let deleteButton = document.createElement("button");
                // deleteButton.setAttribute("class", "btn btn-danger deleteTripBtn");
                // deleteButton.setAttribute("onclick", "deleteTripRequest(" + data.tripId + ", 'map_home')");
                // deleteButton.innerText = "Delete trip";
                // outerTripDiv.appendChild(deleteButton);
                //
                // // Handle checking the show/hide all button
                // document.getElementById('show-hide-all-btn').style.display = 'block';
                //
                // //Add outer div to single trip view
                // singleTripContainer.appendChild(outerTripDiv);
                //
                //
                // //Handle List Group
                // let listGroup = document.getElementById('trip-list-group');
                // let tripLink = document.createElement('a');
                // tripLink.setAttribute('class', "list-group-item list-group-item-action");
                // tripLink.innerText = data.tripName + ' | No arrival dates';
                // tripLink.setAttribute("onclick", "displayTrip(" + data.tripId + ", " + data.latitude+ ", "+ data.longitude + ")");
                //
                // let formCheckDiv = document.createElement("div");
                // formCheckDiv.setAttribute("class", "form-check");
                //
                // let tripCheckBox = document.createElement('input');
                // tripCheckBox.setAttribute('type', 'checkbox');
                // tripCheckBox.setAttribute('id',"Toggle"+data.tripId);
                // tripCheckBox.setAttribute('checked', 'true');
                // tripCheckBox.setAttribute('onchange', 'toggleTrips(' + data.tripId + ')');
                // tripCheckBox.setAttribute('class', 'form-check-input map-check');
                // tripCheckBox.setAttribute('autocomplete', 'off');
                // let mapLabel = document.createElement('label');
                // mapLabel.setAttribute('class', 'form-check-label');
                // mapLabel.setAttribute('for', "toggleMap");
                // mapLabel.innerText = 'Show on map';
                // formCheckDiv.appendChild(tripCheckBox);
                // formCheckDiv.appendChild(mapLabel);
                // tripLink.appendChild(formCheckDiv);
                // listGroup.appendChild(tripLink);
                //
                // //Update tags
                // changeTaggableModel(data.tripId, "trip");
                // showTagEditor();

                //
                //
                //
                //




            },
            error: function(xhr, textStatus, errorThrown){
                alert(errorThrown);
            }
        });
    } else {
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

                updatePaginatedTripData(currentlyDisplayedTripId);

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
    let newTable = document.createElement("table");
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
    tableBody.setAttribute("class", "ui-sortable");

    let newRow = document.createElement('tr');
    newRow.setAttribute('id', data.visitId);
    newRow.setAttribute('class', "ui-sortable-handle");
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
    let errorMsg = document.createElement('td');
    errorMsg.colspan = '4';
    errorMsg.classList.add('text-center');
    errorMsg.id = 'dateError_' + data[0];
    errorMsg.style.color = 'red';
    errorMsg.style.display = 'none';
    errorMsg.innerText = 'Arrival date must be before departure date';
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

function tripVisitTableRefresh(data) {
    let targetTable = document.getElementById("placeholderTripTable");
    let targetTripBody = document.getElementById("tripTableBody_" + currentlyDisplayedTripId);
    let newRow = document.createElement('tr');
    newRow.setAttribute('id', data.visitId);
    newRow.classList.add("ui-sortable-handle");
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
        + data.visitId + ')');
    deleteButton.appendChild(deleteButtonText);
    tableDataDeparture.appendChild(departureDateInput);

    newRow.appendChild(tableHeader);
    newRow.appendChild(tableDataDestType);
    newRow.appendChild(tableDataArrival);
    newRow.appendChild(tableDataDeparture);
    newRow.appendChild(deleteButton);
    targetTripBody.appendChild(newRow);

    // var tripStartLatLng = new google.maps.LatLng(
    //     data.latitude, data.longitude
    // );

    // window.globalMap.setCenter(tripStartLatLng);
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

    for (let path in tripFlightPaths) {
        tripFlightPaths[path].setMap(null);
    }

    let color;

    for (let trip of paginatedTripData.trips) {

        let tripVisitData = [];

        for (let visit of trip.visits) {
            tripVisitData.push({"lat": visit.lat, "lng": visit.lng});
        }

        color = colors[Math.floor(Math.random() * colors.length)];

        let flightPath = new google.maps.Polyline({
            path: tripVisitData,
            geodesic: true,
            strokeColor: '#' + color,
            strokeOpacity: 1.0,
            strokeWeight: 2
        });

        google.maps.event.addListener(flightPath, 'click', function (e) {

            displayTrip(trip.tripId, paginatedTripData.trips.visits[0]['lat'], paginatedTripData.trips.visits[0]['lng']);
        });

        if (tripFlightPaths[trip.tripId] != null) {
            tripFlightPaths[trip.tripId].setMap(null);
            tripFlightPaths[trip.tripId] = flightPath;

        } else {
            tripFlightPaths[trip.tripId] = flightPath;
        }

        const checkBox = document.getElementById("Toggle" + trip.tripId);

        if (checkBox !== null && checkBox.checked === false) {
                tripFlightPaths[trip.tripId].setMap(null);
        } else {
            if (tripFlightPaths[trip.tripId].getMap() == null) {
                tripFlightPaths[trip.tripId].setMap(window.globalMap);
            }
        }

    }
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
    const errorMsgs = document.getElementsByClassName('dateError')
    if(currentlyDisplayedTripId !== undefined) {
        for (let errorMsg of errorMsgs) {
            errorMsg.style.display = "None";
        }
    }
    if(tripId !== currentlyDisplayedTripId && currentlyDisplayedTripId !== undefined) {
        if(document.getElementById("singleTrip_" + currentlyDisplayedTripId) != undefined) {
            document.getElementById("singleTrip_" + currentlyDisplayedTripId).style.display = "none";
        }
        currentlyDisplayedTripId = undefined;
        isNewTrip = false;
    }
    let checkBox = document.getElementById("Toggle" + tripId);
    if (currentlyDisplayedTripId !== undefined) {
        document.getElementById("singleTrip_" + currentlyDisplayedTripId).style.display = "none";
    } else {
        document.getElementById("placeholderTripTable").style.display = "none";
    }
    if(isNewTrip === false) {
        if(document.getElementById("placeholderTripTable").style.display != "none") {
            document.getElementById("placeholderTripTable").style.display = "none";
        }
        document.getElementById("singleTrip_" + tripId).style.display = "block";
    } else {
        document.getElementById("placeholderTripTable").style.display = "block";
    }

    var tripStartLatLng = new google.maps.LatLng(
        startLat, startLng
    );
    currentlyDisplayedTripId = tripId;
    window.globalMap.setCenter(tripStartLatLng);
    window.globalMap.setZoom(7);

    //Update tags
    changeTaggableModel(tripId, "trip");
    showTagEditor();
}



$('tbody').sortable({
    axis: 'y',
    update: function (event, ui) {
        swapVisitOnSort();
    }
});

function updatePaginatedTripData(tripId) {

    $.ajax({
        type: 'GET',
        url: '/users/trips/'+ tripId + '/asJson',
        success: function(data) {

            for (let i=0; i < paginatedTripData.trips.length; i++) {
                let trip = paginatedTripData.trips[i];
                if(trip.tripId == tripId) {
                    paginatedTripData.trips[i] = data;
                }
            }
            initTripRoutes();
        }
    });
}

function swapVisitOnSort() {
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

            updatePaginatedTripData(currentlyDisplayedTripId);
        },
        error: function(xhr, textStatus, errorThrown){
            $('tbody').sortable('cancel');
            alert("You cannot visit the same destination twice in a row!");
        }
    });
}

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
    window.globalMap.setZoom(8);

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

        let img = document.createElement('img');
        img.setAttribute("src", icon);
        let span = document.createElement('span');
        span.innerText = name;

        div.appendChild(img);
        div.appendChild(span);
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
                document.getElementById(visitId).remove();
                document.getElementById('undoButton').classList.remove('disabled');

                updatePaginatedTripData(currentlyDisplayedTripId);

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
function isValidDate(d) {
    return d instanceof Date && !isNaN(d);
}
let thereIsAnError = {errors: []};
function updateVisitDate(visitId) {
    let arrival = document.getElementById("arrival_" + visitId).value;
    let departure = document.getElementById("departure_" + visitId).value;

    let data = {
        arrival: arrival,
        departure: departure
    };
    const arrivalDate = new Date(data.arrival);
    const departureDate = new Date(data.departure);
    const errorMsgs = document.getElementsByClassName('dateError');
    if (! isValidDate(arrivalDate) || ! isValidDate(departureDate) || (arrivalDate <= departureDate)) {
        if (thereIsAnError.errors.includes(visitId)) {
            thereIsAnError.errors = thereIsAnError.errors.filter((value) => {
                return value !== visitId;
            });
        }
        if(! thereIsAnError.errors.length) {
            for (let errorMsg of errorMsgs) {
                errorMsg.style.display = 'None';
            }
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
                }
            });
        }
    } else {
        thereIsAnError.errors.push(visitId);
        for (let errorMsg of errorMsgs) {
            errorMsg.style.display = "inline";
        }
    }

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
                    if (document.getElementById('createDestination').style.display = 'block') {
                        document.getElementById('createDestination').style.display = 'none';
                    }
                    $('[href="#destinationsTab"]').tab('show');
                    displayDestination(data, coordinates.lat(), coordinates.lng());
                }
                else if (xhr.status == 201) {
                    $('[href="#destinationsTab"]').tab('show');
                    for (let childNode of document.getElementById('singleDestinationContainer').children) {
                        if(childNode.style.display == "block") {
                            childNode.style.display = "none";
                        }
                    }
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
                    moveTo = new google.maps.LatLng(coordinates.lat(), coordinates.lng());
                    map.panTo(moveTo);
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
            let markers = [];
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

                markers.push(marker);

                //make the marker and infoWindow globals (persist in browser session)
                window.globalMarkers.push({
                    marker: marker,
                    infoWindow: infoWindow,
                    isClicked: false
                });

                initMarkerEventHandlers(index);
                initInforWindowEventHandlers(index);
            }

            markerCluster = new MarkerClusterer(window.globalMap, markers,
                {imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'});
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

async function closeAllInfoWindows() {
    for (let marker of window.globalMarkers) {
        marker.infoWindow.close(window.globalMap, marker.marker)
    }
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
        closeAllInfoWindows().then(() => {
            window.globalMarkers[markerIndex].infoWindow.open(window.globalMap,
                window.globalMarkers[markerIndex].marker);
        });
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


function viewCreatePanel() {

    if (currentlyDisplayedDestId !== undefined) {
        document.getElementById('singleDestination_'+currentlyDisplayedDestId).style.display = 'none';
    }
    document.getElementById('createDestination').style.display = 'block';
}



/**
 * If in edit mode then listeners will
 * be added to the latitude and longitude
 * fields so they will update the maps center.
 * A listener is added to the map so clicking
 * will update the latitude and longitude fields.
 */
function initMapPositionListeners() {
    window.globalMarkers = [];

    if (document.getElementById("latitude") !== null
        && document.getElementById("longitude") !== null) {

        document.getElementById("latitude").addEventListener('input', function (event) {

            if (!isNaN(document.getElementById("latitude").value)) {
                var latlng = new google.maps.LatLng(
                    document.getElementById("latitude").value,
                    window.globalMap.center.lng());
            }
        });

        document.getElementById("longitude").addEventListener('input', function (event) {

            if (!isNaN(document.getElementById("longitude").value)) {

                var latlng = new google.maps.LatLng(
                    window.globalMap.center.lat(),
                    document.getElementById("longitude").value);
            }
        });

        window.globalMap.addListener('click', function (event) {
            document.getElementById("latitude").value = event.latLng.lat();
            document.getElementById("longitude").value = event.latLng.lng();
            var latlng = {lat: parseFloat(event.latLng.lat()), lng: parseFloat(event.latLng.lng())};
            geoCoder.geocode({'location': latlng}, function(results, status) {
                if (status === 'OK') {
                    if (results[0]) {
                        map.panTo(latlng);
                        let placeId = results[0].place_id;
                        if (destMarker != undefined) {
                            destMarker.setMap(null);
                        }
                        destMarker = new google.maps.Marker({
                            position: latlng,
                            map: map
                        });
                        $.ajax({
                            url: "/users/destination/placeid/" + placeId,
                            method: "GET",
                            //contentType: 'application/json',
                            dataType: 'json',
                            cache: false,
                            success: function (data, textStatus, xhr) {
                                if (xhr.status == 200) {
                                    let placeData = data.result.address_components;
                                    //Check if it can be a number if it can leave it blank

                                    placeData.forEach((addressItem) => {
                                        if (addressItem.types.includes("country")) {
                                            document.getElementById("country").value = addressItem.long_name;

                                        } else if (addressItem.types.includes("administrative_area_level_1")
                                            || addressItem.types.includes("administrative_area_level_2")) {
                                            document.getElementById("district").value = addressItem.long_name;
                                        }
                                    });

                                     if (placeData.length < 2) {
                                         document.getElementById("destName").value = placeData[0].long_name
                                     } else if(placeData[0].types[0] == 'street_number' || placeData[0].types[0] == "postal_code"){
                                         if(placeData[1].types[0] == 'route') {
                                             document.getElementById("destName").value = placeData[2].long_name;
                                         } else {
                                             document.getElementById("destName").value = placeData[1].long_name;
                                         }
                                     }else{
                                         if (data.result.name == "Unnamed Road") {
                                             document.getElementById("destName").value = placeData[1].long_name;
                                         } else {
                                             document.getElementById("destName").value = data.result.name;
                                         }
                                     }
                                    $('[href="#destinationsTab"]').tab('show');
                                    viewCreatePanel();
                                }
                            },
                            error: function (error) {
                               console.log(error);
                            }
                        });
                    } else {
                        window.alert('No results found');
                    }

                } else {
                    window.alert('Geocoder failed due to: ' + status);
                }
            });

        });

    }
}



/**
 * Used to check invalid trips with less than two visits and deletes them on reload
 */
function checkTripVisits() {
    fetch('/users/trips/userTrips', {
        method: 'GET'})
        .then(res => res.json())
        .then(data => {
            for (let trip in tripFlightPaths) {
                if (data[trip] < 2) {
                    let token = $('input[name="csrfToken"]').attr('value');
                    $.ajaxSetup({
                        beforeSend: function (xhr) {
                            xhr.setRequestHeader('Csrf-Token', token);
                        }
                    });
                    $.ajax({
                        url: '/users/trips/' + trip,
                        method: "DELETE",
                        success: function () {
                            let element = document.getElementById("Button" + trip);
                            element.parentNode.removeChild(element);
                            const x = document.getElementById("snackbar");

                            // Add the "show" class to DIV
                            x.className = "show";
                            x.innerText = "Trip with id: " + trip + " has been deleted as it has less than two visits ";
                            // After 3 seconds, remove the show class from DIV
                            setTimeout(function(){
                                x.className = x.className.replace("show", "");
                            }, 5000);
                        }
                    });
                }
            }
        })
}

$("#tripSearchInput").keyup(async function()
{
    let searchInput = document.getElementById("tripSearchInput").value;
    if(searchInput != "") {
        let data = {offset: 0,
                quantity: 5};

        $.ajax({
            url: '/users/trips/matching/' + searchInput,
            data: data,
            method: "GET",
            success: function (res) {
                let paginationList = document.getElementById("trip-pagination-list");
                while (paginationList.firstChild) {
                    paginationList.removeChild(paginationList.firstChild);
                }
                if (tripCount > 0) {
                    setTripPaginationLinks(res.tripCount, 5, searchInput);
                }
                tripPageNum = 1;


                paginatedTripData = res;

                displayTripTablePage(res.trips);

            }
        });
    } else {
        let paginationList = document.getElementById("trip-pagination-list");
        while (paginationList.firstChild) {
            paginationList.removeChild(paginationList.firstChild);
        }
        tripPageNum = 1;
        let tripJSON = await getPaginatedTripResults(tripPageNum, 5);
        let trips  = tripJSON.trips;
        let tripCount = tripJSON.tripCount;
        if (tripCount > 0) {
            setTripPaginationLinks(tripCount, 5);
        }
        displayTripTablePage(trips);

    }
});

$("#formBody").submit(function(e){
    e.preventDefault();
});

$("#destSearchInput").keyup(function ()
{
    searchByKeyword(1, "all")
});

function searchByKeyword(currentPageNum, tab) {
    const offset = (currentPageNum - 1) * 20;
    let searchInput = document.getElementById("destSearchInput").value;
    if(searchInput != "") {
        let data = {offset: offset,
        quantity: 20};
        $.ajax({
            url: '/users/destinations/matching/' + searchInput,
            data: data,
            method: "GET",
            success: function (res) {
                if (tab.id == "publicDestinationList" || tab == "all") {
                    let publicCount = res.totalCountPublic;
                    let publicData = document.getElementById("publicDestinationList");
                    if (currentPageNum > 0) {
                        while (publicData.childNodes.length > 0) {
                            publicData.removeChild(publicData.childNodes[0]);
                        }
                    }
                    for (let destination of res.destinations) {
                        let destElement = document.createElement('a');
                        destElement.setAttribute("onClick", `displayDestination(${destination.destid}, ${destination.latitude}, ${destination.longitude})`);
                        destElement.setAttribute('class', "list-group-item list-group-item-action");
                        destElement.setAttribute('id', "destButton" + destination.destid)
                        destElement.innerText = destination.destName + " | " + destination.destType + " | " + destination.country
                        if (destination.isPublic == true) {
                            publicData.appendChild(destElement);
                        }

                    }
                    if (publicCount > 0) {
                        addPagination(publicCount, currentPageNum, searchInput, "publicDestinationList");
                    }
                }
                if (tab.id == "privateDestinationList" || tab == "all"){
                    let privateCount = res.totalCountPrivate
                    let privateData = document.getElementById("privateDestinationList");
                    if (currentPageNum > 0) {
                        while (privateData.childNodes.length > 0) {
                            privateData.removeChild(privateData.childNodes[0]);
                        }
                    }
                    for (let destination of res.destinations) {
                        let destElement = document.createElement('a');
                        destElement.setAttribute("onClick", `displayDestination(${destination.destid}, ${destination.latitude}, ${destination.longitude})`);
                        destElement.setAttribute('class', "list-group-item list-group-item-action");
                        destElement.setAttribute('id', "destButton" + destination.destid)
                        destElement.innerText = destination.destName + " | " + destination.destType + " | " + destination.country
                        if(destination.isPublic != true) {
                            privateData.appendChild(destElement);

                        }
                    }
                    if (privateCount > 0) {
                        addPagination(privateCount, currentPageNum, searchInput, "privateDestinationList");
                    }
                }

            }
        });
    } else {
        getPrivateDestinations(1, 20);
        getPublicDestinations(1, 20);
    }
}



/**
 * Function used to submit destination creation.
 * If there are errors in the form messages will be shown or updated when necessary.
 */
$("#submit").click(function(e){
    var formData = {
        destName: $("#destName").val(),
        country: $("#country").val(),
        district: $("#district").val(),
        latitude: $("#latitude").val(),
        longitude: $("#longitude").val(),
        destType: $("#destType").val()
    }
    $.ajax({
        type: 'POST',
        url: '/users/destinations/save',
        contentType: 'application/json',
        data: JSON.stringify(formData),
        dataType: 'text',
        timeout: 5000,
        success: function(response) {
            console.log(response);
            location.reload()
        },
        error: function(response) {
            let responseData = JSON.parse(response.responseText);
            let count = 0;
            if (responseData.destName != null) {
                count += 1;
                document.getElementById("nameError").innerText = responseData.destName[0];
            } else {
                document.getElementById("nameError").innerText = "";
            }
            if (responseData.country != null) {
                count += 1;
                document.getElementById("countryError").innerText = responseData.country[0];
            } else {
                document.getElementById("countryError").innerText = "";
            }
            if (responseData.district != null) {
                count += 1;
                document.getElementById("districtError").innerText = responseData.district[0];
            } else {
                document.getElementById("districtError").innerText = "";
            }
            if (responseData.latitude != null) {
                count += 1;
                document.getElementById("latitudeError").innerText = responseData.latitude[0];
            } else {
                document.getElementById("latitudeError").innerText = "";
            }
            if (responseData.longitude != null) {
                count += 1;
                document.getElementById("longitudeError").innerText = responseData.longitude[0];
            } else {
                document.getElementById("longitudeError").innerText = "";
            }
            if (responseData.destType != null) {
                count += 1;
                document.getElementById("typeError").innerText = responseData.destType[0];
            } else {
                document.getElementById("typeError").innerText = "";
            }
            if (count === 0) {
                document.getElementById("existingMessage").innerText = "Similar Destination already exists"
            } else {
                document.getElementById("existingMessage").innerText = "";
            }
        }

    });
});

function getPublicDestinations(pageNum, quantity){
    const offset = (pageNum - 1) * 20;
    let data = {offset: offset,
                quantity: quantity};

    $.ajax({
        type: 'GET',
        url: "/users/destinations/getpublicpaginatedjson",
        data: data,
        contentType: 'application/json',
        success: (destData) => {
            let publicCount = destData.totalCountPublic;
            let destinationData = document.getElementById("publicDestinationList");
            if (pageNum > 0) {
                while (destinationData.childNodes.length > 0) {
                    destinationData.removeChild(destinationData.childNodes[0]);
                }
            }
            for (let destination of destData.destinations) {
                let destElement = document.createElement('a');
                destElement.setAttribute("onClick", `displayDestination(${destination.destid}, ${destination.latitude}, ${destination.longitude})`);
                destElement.setAttribute('class', "list-group-item list-group-item-action");
                destElement.setAttribute('id', "destButton" + destination.destid)
                destElement.innerText = destination.destName + " | " + destination.destType + " | " + destination.country
                destinationData.appendChild(destElement);
            }
            if (publicCount > 0) {
                addPagination(publicCount, pageNum, null, "publicDestinationList");
            }
        }, error: function (error) {
            console.log(error);
        }
    })
}

function getPrivateDestinations(pageNum, quantity){
    const offset = (pageNum - 1) * 20;
    let data = {offset: offset,
        quantity: quantity};

    $.ajax({
        type: 'GET',
        url: "/users/destinations/getprivatepaginatedjson",
        data: data,
        contentType: 'application/json',
        success: (destData) => {
            let privateCount = destData.totalCountPrivate;
            let destinationData = document.getElementById("privateDestinationList");
            if (pageNum > 0) {
                while (destinationData.childNodes.length > 0) {
                    destinationData.removeChild(destinationData.childNodes[0]);
                }
            }
            for (let destination of destData.destinations) {
                let destElement = document.createElement('a');
                destElement.setAttribute("onClick", `displayDestination(${destination.destid}, ${destination.latitude}, ${destination.longitude})`);
                destElement.setAttribute('class', "list-group-item list-group-item-action");
                destElement.setAttribute('id', "destButton" + destination.destid)
                destElement.innerText = destination.destName + " | " + destination.destType + " | " + destination.country
                destinationData.appendChild(destElement);
            }
            if (privateCount > 0) {
                addPagination(privateCount, pageNum, null, "privateDestinationList");
            }
        }, error: function (error) {
            console.log(error);
        }
    })
}

function addPagination(count, pageNum, search, tab) {
    let numOfPages = [];
    let pageNumbers = [];
    const pagination = document.createElement("ul");
    pagination.classList.add("pagination");
    for (let i = 0; i < count; i += 20) {
        numOfPages.push((i / 20) + 1);
    }
    if (numOfPages.length > 10) {
        if (pageNum > 5) {
            if (numOfPages.length >= pageNum + 5) {
                pageNumbers = [pageNum - 3, pageNum - 2, pageNum - 1, pageNum, pageNum + 1, pageNum + 2, pageNum + 3, pageNum + 4];
            } else {
                let lastPage = numOfPages.length - 0;
                pageNumbers = []
                for (let j = lastPage - 7; (j < lastPage + 1 && j > 0); j++) {
                    pageNumbers.push(j);
                }
            }
        } else {
            for (let k = 0; k < 10; k++) {
                pageNumbers.push(numOfPages[k]);
            }
        }
    } else {
        pageNumbers = numOfPages;
    }
    let item = document.createElement("li");
    let pageButton = document.createElement("a");
    let currentPageNum = 1;
    pageButton.innerText = "First";
    if (search == null) {
        if (tab == "publicDestinationList") {
            pageButton.setAttribute("onClick", `getPublicDestinations(${currentPageNum}, ${20})`);
        } else  {
            pageButton.setAttribute("onClick", `getPrivateDestinations(${currentPageNum}, ${20})`);

        }
    } else {
        pageButton.setAttribute("onClick", `searchByKeyword(${currentPageNum}, ${tab})`);

    }
    item.appendChild(pageButton);
    pagination.appendChild(item);

    item = document.createElement("li");
    pageButton = document.createElement("a");
    if (pageNum < 2) {
        currentPageNum = 1;
    } else {
        currentPageNum = pageNum - 1;
    }
    pageButton.innerText = "<";
    if (search == null) {
        if (tab == "publicDestinationList") {
            pageButton.setAttribute("onClick", `getPublicDestinations(${currentPageNum}, ${20})`);
        } else  {
            pageButton.setAttribute("onClick", `getPrivateDestinations(${currentPageNum}, ${20})`);

        }
    } else {
            pageButton.setAttribute("onClick", `searchByKeyword(${currentPageNum}, ${tab})`);
    }
    item.appendChild(pageButton);
    pagination.appendChild(item);
    for (let i=0; i < pageNumbers.length; i++) {
        let item = document.createElement("li");
        const pageButton = document.createElement("a");
        const currentPageNum = pageNumbers[i];
        pageButton.innerText = pageNumbers[i];
        if (currentPageNum==pageNum) {
            item.classList.add("active");
        }
        if (search == null) {
            if (tab == "publicDestinationList") {
                pageButton.setAttribute("onClick", `getPublicDestinations(${currentPageNum}, ${20})`);
            } else  {
                pageButton.setAttribute("onClick", `getPrivateDestinations(${currentPageNum}, ${20})`);

            }
        } else {
            pageButton.setAttribute("onClick", `searchByKeyword(${currentPageNum}, ${tab})`);

        }
        item.appendChild(pageButton);
        pagination.appendChild(item);
    }
    item = document.createElement("li");
    pageButton = document.createElement("a");
    if (pageNum>=numOfPages.length) {
        currentPageNum = numOfPages.length;
    } else {
        currentPageNum = pageNum+1;
    }
    pageButton.innerText = ">";
    if (search == null) {
        if (tab == "publicDestinationList") {
            pageButton.setAttribute("onClick", `getPublicDestinations(${currentPageNum}, ${20})`);
        } else  {
            pageButton.setAttribute("onClick", `getPrivateDestinations(${currentPageNum}, ${20})`);

        }
    } else {
        pageButton.setAttribute("onClick", `searchByKeyword(${currentPageNum}, ${tab})`);

    }
    item.appendChild(pageButton);
    pagination.appendChild(item);
    document.getElementById(tab).appendChild(pagination);

    item = document.createElement("li");
    pageButton = document.createElement("a");
    currentPageNum = numOfPages.length;
    pageButton.innerText = "Last";
    if (search == null) {
        if (tab == "publicDestinationList") {
            pageButton.setAttribute("onClick", `getPublicDestinations(${currentPageNum}, ${20})`);
        } else  {
            pageButton.setAttribute("onClick", `getPrivateDestinations(${currentPageNum}, ${20})`);

        }
    } else {
        pageButton.setAttribute("onClick", `searchByKeyword(${currentPageNum}, ${tab})`);

    }
    item.appendChild(pageButton);
    pagination.appendChild(item);
}




async function nextTripPage(search) {
    jumpToTripPage(tripPageNum + 1, search);
}

async function previousTripPage(search) {
    let newTripPageNumber = tripPageNum;
    if(tripPageNum > 1) {
        newTripPageNumber -= 1;
    } else {
        newTripPageNumber = 1;
    }
    jumpToTripPage(newTripPageNumber, search);
}

async function jumpToTripPage(pageNumber, search) {
    document.getElementById("trip-pagination-link-" + tripPageNum).removeAttribute("class");

    tripPageNum = pageNumber;
    document.getElementById("trip-pagination-link-" + tripPageNum).setAttribute("class", "active");

    if (search == undefined) {
        let newlyDisplayedTrips = await getPaginatedTripResults(tripPageNum, 5);
        displayTripTablePage(newlyDisplayedTrips.trips);
    } else {
        let newlyDisplayedTrips = await getPaginatedTripSearchResults(tripPageNum, 5, search);
        displayTripTablePage(newlyDisplayedTrips.trips);
    }


}



/**
 * Change the HTML elements to display a new page of trips given in the trips parameter.
 */
function displayTripTablePage(trips) {
    let tripListGroup = document.getElementById("trip-list-group");
    while (tripListGroup.firstChild) {
        tripListGroup.removeChild(tripListGroup.firstChild);
    }

    let tripNodes = document.getElementsByClassName("singleTrip");
    while (tripNodes[0]) {
        document.getElementById("singleTripContainer").removeChild(tripNodes[0]);
    }

    document.getElementById("tag-container").setAttribute("style", "visibility: hidden;");

    if (trips !== undefined) {
        for(let i = 0; i < trips.length; i++) {
            let trip = trips[i];
            let tripListItem = document.createElement("a");
            tripListItem.setAttribute("id", "Button" + trip.tripId);
            tripListItem.setAttribute("class", "list-group-item list-group-item-action");
            tripListItem.setAttribute("onclick", "displayTrip(" + trip.tripId + "," + trip.visits[0].lat + "," + trip.visits[0].lng + ")");
            if(trip.startDate != null) {
                tripListItem.innerText = trip.tripName + " | Arrival date: " + trip.startDate;
            } else {
                tripListItem.innerText = trip.tripName + " | No arrival dates";
            }

            let formCheckDiv = document.createElement("div");
            formCheckDiv.setAttribute("class", "form-check");
            let tripCheckBox = document.createElement('input');
            tripCheckBox.setAttribute('type', 'checkbox');
            tripCheckBox.setAttribute('id',"Toggle"+trip.tripId);
            tripCheckBox.setAttribute('checked', 'true');
            tripCheckBox.setAttribute('onchange', 'toggleTrips(' + trip.tripId + ')');
            tripCheckBox.setAttribute('class', 'form-check-input map-check');
            tripCheckBox.setAttribute('autocomplete', 'off');
            let mapLabel = document.createElement('label');
            mapLabel.setAttribute('class', 'form-check-label');
            mapLabel.setAttribute('for', "toggleMap");
            mapLabel.innerText = 'Show on map';
            formCheckDiv.appendChild(tripCheckBox);
            formCheckDiv.appendChild(mapLabel);

            tripListItem.appendChild(formCheckDiv);

            tripListGroup.appendChild(tripListItem);



            //Handle outer div
            let singleTripContainer = document.getElementById("singleTripContainer");
            let outerTripDiv = document.createElement("div");
            outerTripDiv.setAttribute("id", "singleTrip_" + trip.tripId);
            outerTripDiv.setAttribute("class", "singleTrip");

            //Handle title div
            let titleDiv = createTripTitleDiv(trip);
            outerTripDiv.appendChild(titleDiv);


            //Handle Table
            let newTable = updateTripPageTable(trip);
            outerTripDiv.appendChild(newTable);



            $('tbody').sortable({
                axis: 'y',
                update: function (event, ui) {
                    swapVisitOnSort();
                }
            });

            //Handle delete button
            let deleteButton = document.createElement("button");
            deleteButton.setAttribute("class", "btn btn-danger deleteTripBtn");
            deleteButton.setAttribute("onclick", "deleteTripRequest(" + trip.tripId + ", 'map_home')");
            deleteButton.innerText = "Delete trip";
            outerTripDiv.appendChild(deleteButton);

            // Handle checking the show/hide all button
            document.getElementById('show-hide-all-btn').style.display = 'block';

            //Add outer div to single trip view
            singleTripContainer.appendChild(outerTripDiv);


        }
    }

    initTripRoutes();
}

function updateTripPageTable(data) {
    let newTable = document.createElement("table");
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
    tableBody.setAttribute("class", "ui-sortable");

    let visits = data.visits;

    for (let i = 0; i < visits.length; i++) {
        let visit = visits[i];
        let newRow = document.createElement('tr');
        newRow.setAttribute('id', visit.visitId);
        newRow.setAttribute('class', "ui-sortable-handle");
        let tableHeader = document.createElement('th');
        tableHeader.setAttribute('scope', 'row');
        tableHeader.innerText = visit.visitName;
        let tableDataDestType = document.createElement('td');
        tableDataDestType.innerText = visit.destType;
        let tableDataArrival = document.createElement('td');
        let arrivalDateInput = document.createElement('input');
        arrivalDateInput.setAttribute('type', 'date');
        arrivalDateInput.setAttribute('class', 'tripDateInput arrivalDate');
        arrivalDateInput.setAttribute('onblur', "updateVisitDate(" + visit.visitId+")");
        arrivalDateInput.setAttribute('id', 'arrival_'+visit.visitId);
        if(visit.arrivalDate != null) {
            arrivalDateInput.setAttribute('value', visit.arrivalDate);
        }
        tableDataArrival.appendChild(arrivalDateInput);
        let tableDataDeparture = document.createElement('td');
        let departureDateInput = document.createElement('input');
        departureDateInput.setAttribute('id', 'departure_'+visit.visitId);
        departureDateInput.setAttribute('type', 'date');
        departureDateInput.setAttribute('class', 'tripDateInput departureDate');
        departureDateInput.setAttribute('onblur', "updateVisitDate(" + visit.visitId+")");
        if(visit.departureDate != null) {
            departureDateInput.setAttribute('value', visit.departureDate);
        }
        let deleteButton = document.createElement('td');
        let deleteButtonText = document.createElement('a');
        deleteButtonText.innerText = '❌';
        deleteButtonText.setAttribute('style', 'deleteButton');
        let urlForDelete = '/users/trips/edit/' + visit.visitId;//data.tripId;
        deleteButtonText.setAttribute('onclick', 'sendDeleteVisitRequest(' + '"' + urlForDelete + '"' + ','
            + visit.visitId + ')');
        let errorMsg = document.createElement('td');
        errorMsg.colspan = '4';
        errorMsg.classList.add('text-center');
        errorMsg.id = 'dateError_' + visit.visitId;
        errorMsg.style.color = 'red';
        errorMsg.style.display = 'none';
        errorMsg.innerText = 'Arrival date must be before departure date';
        deleteButton.appendChild(deleteButtonText);
        tableDataDeparture.appendChild(departureDateInput);
        newRow.appendChild(tableHeader);
        newRow.appendChild(tableDataDestType);
        newRow.appendChild(tableDataArrival);
        newRow.appendChild(tableDataDeparture);
        newRow.appendChild(deleteButton);
        tableBody.appendChild(newRow);
    }

    newTable.appendChild(tableBody);


    return newTable;
}


/**
 * Function used to get a paginated list of trips based on the quantity and page number
 */
async function getPaginatedTripResults(pageNum, quantity) {
    const offset = (pageNum - 1) * quantity;

    let trips = await $.ajax({
        type: 'GET',
        url: '/users/trips/paginated/' + offset + '/' + quantity,
        contentType: 'application/json',
    });

    paginatedTripData = trips;

    return trips;
}

async function getPaginatedTripSearchResults(pageNum, quantity, searchInput) {
    const offset = (pageNum - 1) * quantity;
    let data = {offset: offset,
                    quantity: quantity};

    let trips = await $.ajax({
        url: '/users/trips/matching/' + searchInput,
        data: data,
        method: "GET"
    });

    paginatedTripData = trips;

    return trips;
}

function setTripPaginationLinks(tripCount, perPage, search) {
    let paginationList = document.getElementById("trip-pagination-list");
    let previousArrowLink = document.createElement("li");
    let previousArrow = document.createElement("a");
    previousArrow.setAttribute("id", "previous-trip");
    previousArrow.setAttribute("onclick","previousTripPage(" + search + ")");
    previousArrow.innerText = "<";
    previousArrowLink.appendChild(previousArrow);
    let nextArrowLink = document.createElement("li");
    let nextArrow = document.createElement("a");
    nextArrow.setAttribute("id", "next-trip");
    nextArrow.setAttribute("onclick","nextTripPage(" + search + ")");
    nextArrow.innerText = ">";
    nextArrowLink.appendChild(nextArrow);
    paginationList.appendChild(previousArrowLink);

    let numPages = Math.ceil(tripCount/perPage);
    let lastPageCount = tripCount % perPage;
    if (lastPageCount == 0) {
        lastPageCount = perPage;
    }

    for (let i=1; i < numPages+1; i++) {
        let pageNumber = document.createElement("li");
        pageNumber.setAttribute("id", "trip-pagination-link-" + i);
        if (i==1) {
            tripPageNum = 1;
            pageNumber.setAttribute("class", "active");
        }
        let pageNumberLink = document.createElement("a");
        pageNumberLink.innerText = i.toString();

        pageNumberLink.setAttribute("onclick", "jumpToTripPage(" + i + "," + search + ")");
        pageNumber.appendChild(pageNumberLink);
        paginationList.appendChild(pageNumber);
    }


    paginationList.appendChild(nextArrowLink);




}


window.onload = function() {
    checkTripVisits();
    getPublicDestinations(1, 20, null);
    getPrivateDestinations(1, 20, null);

};



