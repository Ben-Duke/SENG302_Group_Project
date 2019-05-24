function sendDeleteTripRequest(tripId, homeURL){
    let token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
        url: '/users/trips/json/' + tripId,
        method: "GET",
        success:function(res){
            let confirmation = confirm("Are you sure you want to delete "
                + res.tripName + "?");
            if(confirmation){
                $.ajax({
                    url: '/users/trips/' + tripId,
                    method: "DELETE",
                    success:function(res){
                        window.location = homeURL;
                    }
                });
            }
        }
    })
}