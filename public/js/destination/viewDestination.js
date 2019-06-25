/**
 * The callback function that is called after the google maps script src loads
 * with the api key.
 * Adds listener that gets lat and lng out of map onlick click and fills
 * form fields with those values.
 * Add listeners to the lat and lng fields, so on input it centers the map
 * to that location
 *
 * Loads the create destination map without any markers.
 */
function initMap() {

    window.globalMap = new google.maps.Map(document.getElementById('map'), {
        zoom: 5
    });

    var initLatLng = new google.maps.LatLng(
        -43.522057156877615, 172.62360347218828
    );

    window.globalMap.setCenter(initLatLng);


}