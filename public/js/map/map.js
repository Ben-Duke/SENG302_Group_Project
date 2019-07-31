var visitArray = [];
const updateVisitDateUrl = "/user/trips/visit/dates/";
const colors = ['6b5b95', 'feb236', 'd64161', 'ff7b25',
    '6b5b95', '86af49', '3e4444', 'eca1a6', 'ffef96', 'bc5a45', 'c1946a'];



function initMap() {

    window.globalMap = new google.maps.Map(document.getElementById('map'), {
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

let tripFlightPaths = {};

/**
 * Will toggle the flight path of the trip on the map
 * according to the value of the checkbox
 * @param tripid The id of the trip on the map
 */
function toggleTrips(tripid) {
    var checkBox = document.getElementById(tripid);
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



var currentlyDisplayedTripId;
/**
 * Displays the given trip in the table and centers on map.
 * @param tripId The id of the trip to be displayed
 * @param startLat the latitude to zoom to
 * @param startLng the longitude to zoom to
 */
function displayTrip(tripId, startLat, startLng) {

    var checkBox = document.getElementById("showTripInput_"+tripId);
    if (checkBox.checked === true) {
        if (currentlyDisplayedTripId !== undefined) {
            document.getElementById("singleTrip_" + currentlyDisplayedTripId).style.display = "none";
        } else {
            document.getElementById("placeholderTripTable").style.display = "none";
        }

        currentlyDisplayedTripId = tripId;

        document.getElementById("singleTrip_" + tripId).style.display = "block";


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

var currentlyDisplayedDestId;

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

        console.log(place);

    });
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

    let token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
        url: '/user/trips/edit/'+currentlyDisplayedTripId,
        method: "PATCH",
        data: JSON.stringify(newName),
        contentType : 'application/json',
        success: function(data, textStatus, xhr){
            if(xhr.status == 200) {
                document.getElementById("tripName_"+currentlyDisplayedTripId).innerText = newName;
                toggleEditTripName(false);
            }
            else{

            }
        },
        error: function(xhr, settings){
            if(xhr.status == 400) {
                document.getElementById("tripNameInput_"+currentlyDisplayedTripId).value =
                    document.getElementById("tripName_"+currentlyDisplayedTripId).innerText;
            }
            else if(xhr.status == 403){
            }
            else{
            }
        }
    });



}

function deleteTripRequest(tripId, url){
    let token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
        url: '/users/trips/' + tripId,
        method: "DELETE",
        success: function(res) {
            window.location = url;
        }
    });
}



function sendDeleteVisitRequest(url, visitId) {
    console.log(url);
    let token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
        url: url,
        method: "DELETE",
        contentType : 'application/json',
        success: function(data, textStatus, xhr){
            if(xhr.status == 200) {
                document.getElementById("visit_row_" + visitId).remove();
            }
            else{

            }
        },
        error: function(xhr, settings){
            if(xhr.status == 400) {

            }
            else if(xhr.status == 403){
            }
            else{
            }
        }
    });
}


function updateVisitDate(visitId) {
    let arrival = document.getElementById("arrival_"+visitId).value;
    let departure = document.getElementById("departure_"+visitId).value;

    let data = {
        arrival: arrival,
        departure: departure
    };

    let token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
        url: updateVisitDateUrl + visitId,
        method: "PATCH",
        data: JSON.stringify(data),
        contentType : 'application/json',
        success: function(data, textStatus, xhr){
            if(xhr.status == 200) {

            }
            else{

            }
        },
        error: function(xhr, settings){
            if(xhr.status == 400) {
            }
            else if(xhr.status == 403){
            }
            else{
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