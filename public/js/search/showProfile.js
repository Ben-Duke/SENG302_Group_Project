setFollowLink();


function setHover(btnType, btnTextMain, btnTextSecondary) {
    let followText = document.getElementById("follow-text");
    $('#follow-button').hover(
    function() {
            $('#follow-button').removeClass().addClass(btnType);
            followText.innerText = btnTextSecondary;
        },
    function() {
        $('#follow-button').removeClass().addClass('btn btn-default');
        followText.innerText = btnTextMain;
    }
    );
}


function setFollowLink() {
    let followButton = document.getElementById("follow-button");
    let profileId = followButton.dataset.profile;
    $.ajax({
        type: 'GET',
        url: '/users/follows/' + profileId,
        success: function(followResult) {
            if(followResult == false) {
                document.getElementById("follow-text").innerText = "Not Following";
                followButton.setAttribute("onclick", "followUser(" + profileId + ")");
                setHover("btn btn-primary", "Not Following", "Follow");

            } else {
                document.getElementById("follow-text").innerText = "Following";
                followButton.setAttribute("onclick", "unfollowUser(" + profileId + ")");
                setHover("btn btn-danger", "Following", "Unfollow");
            }
        }
    });
}


function followUser(profileId) {
    let followButton = document.getElementById("follow-button");
    var token = $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function (xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
        type: 'POST',
        contentType: false,
        url: '/users/follow/' + profileId,
        success: function (data, textStatus, xhr) {
            if (xhr.status == 200) {
                console.log("followed");
                document.getElementById("follow-text").innerText = "Following";
                followButton.setAttribute("onclick", "unfollowUser(" + profileId + ")");
                setHover("btn btn-danger", "Following", "Unfollow");
                followButton.setAttribute("class", "btn btn-default");
            }
        }
    })
}

function unfollowUser(profileId) {
    let followButton = document.getElementById("follow-button");
    var token = $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function (xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
        type: 'POST',
        contentType: false,
        url: '/users/unfollow/' + profileId,
        success: function (data, textStatus, xhr) {
            if (xhr.status == 200) {
            console.log("unfollowed");
            document.getElementById("follow-text").innerText = "Not Following";
            followButton.setAttribute("onclick", "followUser(" + profileId + ")");
            setHover("btn btn-primary", "Not Following", "Follow");
            followButton.setAttribute("class", "btn btn-default");
            }
        }
    })
}