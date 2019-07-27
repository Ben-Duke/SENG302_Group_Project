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

function sendTripTags(tripId) {
    toAddTagList = Array.from(toAddTagList);
    let toAddTagString = "";
    for (var i = 0; i < toAddTagList.length; i++) {
        toAddTagString += toAddTagList[i] + ",";
    }
    if (toAddTagString.length > 0) {
        toAddTagString = toAddTagString.substring(0, toAddTagString.length - 1);
    }
    console.log(toAddTagString);
    console.log(tripId);

    const token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
        url: '/trips/' + tripId + '/tags',
        method: "PUT",
        data: JSON.stringify({
            tag: toAddTagString
        }),
        success: function(res) {
            console.log(res)
        },
        error: function(res) {
            console.log(res)
        }
    });
}