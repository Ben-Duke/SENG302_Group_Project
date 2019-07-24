
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





