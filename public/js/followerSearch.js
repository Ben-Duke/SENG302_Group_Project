function followUser(user) {
    let followElement = document.getElementById("follow_" + user);
    let url;
    if (followElement.innerText == "Follow") {
        url = '/users/follow/' + user;
    } else {
        url = '/users/unfollow/' + user;
    }
    var token = $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function (xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
        type: 'POST',
        contentType: false,
        url: url,
        success: function (data, textStatus, xhr) {
            if (xhr.status == 200) {
                if (followElement.innerText == "Follow") {
                    followElement.innerText = "Unfollow";
                } else {
                    followElement.innerText = "Follow";
                }
            } else {
            }
        }
    })
}