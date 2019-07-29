var visitArray = [];
const updateVisitDateUrl = "/user/trips/visit/dates/";



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

        for (let tripId in tripRoutes) {

            let flightPath = new google.maps.Polyline({
                path: tripRoutes[tripId],
                geodesic: true,
                strokeColor: '#'+(Math.random()*0xFFFFFF<<0).toString(16),
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
    var checkBox = document.getElementById(tripId);
    if (checkBox.checked === true) {
        if (currentlyDisplayedTripId !== undefined) {
            document.getElementById("tripTable_" + currentlyDisplayedTripId).style.display = "none";
        }

        currentlyDisplayedTripId = tripId;

        document.getElementById("tripTable_" + tripId).style.display = "table-row-group";


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
        var token =  $('input[name="csrfToken"]').attr('value')
        $.ajaxSetup({
            beforeSend: function(xhr) {
                xhr.setRequestHeader('Csrf-Token', token);
            }
        });
        var data = jQuery('#tripTable_'+currentlyDisplayedTripId+' tr').map(function(){
            return jQuery (this).attr("id");
        }).get();
        var url = '/users/trips/edit/' + currentlyDisplayedTripId;
        // POST to server using $.post or $.ajax

        console.log(data);

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