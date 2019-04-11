function sendDeleteVisitRequest(url, rUrl, errorUrl, errorUrl2, errorUrl3) {

    var data = {
        myTextToPass: $('#sometext').val()
    };
    // LOOK AT ME! BETWEEN HERE AND
    var token =  $('input[name="csrfToken"]').attr('value')
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    // HERE
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