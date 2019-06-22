

function initMap() {
    window.globalMap = new google.maps.Map(document.getElementById('map'), {
        center: {lat: -43.522057156877615, lng: 172.62360347218828},
        zoom: 5
    });

    document.getElementById('placesSearch').addEventListener('input', function() {

        var inputText = document.getElementById('placesSearch').value;

        $.ajaxSetup({
            beforeSend: function(xhr) {
                xhr.setRequestHeader('Csrf-Token', token);
            }
        });


        $.ajax({
            url: "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+inputText+"?key=AIzaSyDCyC_ON0GgSmJIfPlL7oke5PZgbR8TLbg",
            contentType: 'application/json',
            success: function(res) {
                console.log("Success!");
            }
        });



    });

}





// var ac = new google.maps.places.Autocomplete(document.getElementById('placesSearch'));

// google.maps.event.addEventListener(ac, 'place_changed', function() {
//
//     var place = ac.getPlace();
//     console.log(place.formatted_adress);
//     console.log(place.url);
//     console.log(place.geometry.location );
//
//
// });

// var ac = new google.maps.places.Autocomplete