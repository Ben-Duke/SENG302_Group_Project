setFollowLink();
$('#follow-button').hover(
    function() {
        $('#follow-button').removeClass('btn-default').addClass('btn-primary');;
    },
    function() {
        $('#follow-button').removeClass('btn-primary').addClass('btn-default');
    }
);

$('#unfollow-button').hover(
    function() {
        $('#unfollow-button').removeClass('btn-default').addClass('btn-danger');;
    },
    function() {
        $('#unfollow-button').removeClass('btn-danger').addClass('btn-default');
    }
);
function setFollowLink() {
    let followButton = document.getElementById("follow-button");
    let unfollowButton = document.getElementById("unfollow-button");
    let profileId = followButton.dataset.profile;
    $.ajax({
        type: 'GET',
        url: '/users/follows/' + profileId,
        success: function(followResult) {
            if(followResult == false) {
                followButton.style.display = "block";
                unfollowButton.style.display = "none";
            } else {
                unfollowButton.style.display = "block";
                followButton.style.display = "none";
            }
        }
    });
}


function followUser(profileId) {
    let followButton = document.getElementById("follow-button");
    let unfollowButton = document.getElementById("unfollow-button");
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
                unfollowButton.style.display = "block";
                followButton.style.display = "none";
            } else {
            }
        }
    })
}

function unfollowUser(profileId) {
    let followButton = document.getElementById("follow-button");
    let unfollowButton = document.getElementById("unfollow-button");
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
                followButton.style.display = "block";
                unfollowButton.style.display = "none";
            } else {
            }
        }
    })
}