/**
 * Sends an ajax request to the backend to delete a visit from a trip.
 * @param url the url to send the backend request
 * @param rUrl on success, the url to redirect back to the display trip table page
 * @param errorUrl the url to redirect on bad request error
 * @param errorUrl2 the url to redirect on forbidden error
 * @param errorUrl3 the url to redirect on any other error
 */
function sendDeleteVisitRequest(url, rUrl, errorUrl, errorUrl2, errorUrl3) {

    var data = {
        myTextToPass: $('#sometext').val()
    };
    var token =  $('input[name="csrfToken"]').attr('value')
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
        url: url,
        method: "DELETE",
        data : JSON.stringify(data),
        contentType : 'application/json',
        success: function(data, textStatus, xhr){
            if(xhr.status == 200) {
                window.location = rUrl;
            }
            else{
                window.location = errorUrl;
            }
        },
        error: function(xhr, settings){
            if(xhr.status == 400) {
                window.location = errorUrl;
            }
            else if(xhr.status == 403){
                window.location = errorUrl2;
            }
            else{
                window.location = errorUrl3;
            }
            //window.location.reload();
        }
    });
}