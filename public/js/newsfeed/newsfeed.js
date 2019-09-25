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
                createNewsFeedEventResponseComponent(response.event, response.user, response.responseDateTime, response.responseType)
            }
        }
    });

    let date = new Date();
    let dateString = date.toLocaleString();
    dateString = dateString.replace("/", "-");
    dateString = dateString.replace("/", "-");
    dateString = dateString.replace(",", "");

    let data = {offset: 0,
                limit: 10,
                localDateTime: dateString};
    $.ajax({
        type: 'GET',
        data: data,
        url: '/users/newsfeed/media',
        success: function (result) {
            responses = result;
            for (response of responses) {
                createNewsFeedMediaComponent(response.url, response.user, response.date_created);
            }
        }
    })


}