initNewsfeed();

function initNewsfeed() {
    let responses;
    let token = $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function (xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
        type: 'GET',
        url: '/events/responses/newsfeed/0/10/30-09-2019%2000:00:00',
        success: function (result) {
            responses = result.responses;
            for (response of responses) {
                createNewsFeedEventResponseComponent(response.event, response.user, response.responseDateTime)
            }
        }
    })
}