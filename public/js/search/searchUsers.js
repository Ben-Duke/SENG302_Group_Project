configureFollowLinks();

//n = 0, sorting by email
//n = 2, sorting by gender
//n = 3, sorting by the travelers first nationality
//n = 4, sorting by date of birth
//n = 5, sorting by the travelers first traveler type
function sortTable(n, tableName) {
    let table, rows, switching, i, x, y, shouldSwitch, dir, switchCount = 0;
    table = document.getElementById(tableName);
    switching = true;
    // Set the sorting direction to ascending:
    dir = "asc";
    /* Make a loop that will continue until
    no switching has been done: */
    while (switching) {
        // Start by saying: no switching is done:
        switching = false;
        rows = table.rows;
        /* Loop through all table rows (except the
        first, which contains table headers): */
        for (i = 1; i < (rows.length - 1); i++) {
            // Start by saying there should be no switching:
            shouldSwitch = false;
            /* Get the two elements you want to compare,
            one from current row and one from the next: */
            if (n === 2 || n === 4) {
                /*This is for the nationality and traveler type columns, the first traveler type
                or nationality is taken */
                x = rows[i].getElementsByTagName("TD")[n].getElementsByTagName("P")[0];
                y = rows[i + 1].getElementsByTagName("TD")[n].getElementsByTagName("P")[0];
            } else {
                x = rows[i].getElementsByTagName("TD")[n];
                y = rows[i + 1].getElementsByTagName("TD")[n];
            }

            // console.log(dir);
            // console.log(x.innerHTML.toLowerCase() +" "+ y.innerHTML.toLowerCase());
            // console.log(x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase());

            /* Check if the two rows should switch place,
            based on the direction, asc or desc: */
            if (dir === "asc") {
                if (x.innerText.toLowerCase() > y.innerText.toLowerCase()) {
                    // If so, mark as a switch and break the loop:
                    shouldSwitch = true;
                    break;
                }
            } else if (dir === "desc") {
                if (x.innerText.toLowerCase() < y.innerText.toLowerCase()) {
                    // If so, mark as a switch and break the loop:
                    shouldSwitch = true;
                    break;
                }
            }
        }
        if (shouldSwitch) {
            /* If a switch has been marked, make the switch
            and mark that a switch has been done: */
            rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
            switching = true;
            // Each time a switch is done, increase this count by 1:
            switchCount++;
        } else {
            /* If no switching has been done AND the direction is "asc",
            set the direction to "desc" and run the while loop again. */
            if (switchCount === 0 && dir === "asc") {
                dir = "desc";
                switching = true;
            }
        }
    }
}

function configureFollowLinks() {
    const linkContainers = document.getElementsByClassName("followLink");
    for (let linkContainer of linkContainers) {
        const profileId = linkContainer.dataset.profile;
        setFollowLink(profileId)
    }

    const followerLinkContainers = document.getElementsByClassName("followerTabLink");
    for (let linkContainer of followerLinkContainers) {
        const tabProfileId = linkContainer.dataset.profile;
        setFollowedLink(tabProfileId)
    }

}

function setFollowLink(profileId) {
    let success;
    $.ajax({
        type: 'GET',
        url: `/users/follows/${profileId}`,
        success: function(followResult) {
            if(followResult == false) {
                document.getElementById("follow-" + profileId).style.display = "block";
            } else {
                document.getElementById("unfollow-" + profileId).style.display = "block";
            }
        }
    });
}

function setFollowedLink(profileId) {
    let success;
    $.ajax({
        type: 'GET',
        url: `/users/followed/${profileId}`,
        success: function(followResult) {
            if(followResult == false) {
                document.getElementById("followingTab-follow-" + profileId).style.display = "block";
            } else {
                document.getElementById("followingTab-follow-" + profileId).style.display = "block";
            }
        }
    });
}



function followUser(profileId, tab) {
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
                if (tab !== undefined) {
                    document.getElementById(tab + "follow-" + profileId).style.display = "none";
                    document.getElementById(tab + "unfollow-" + profileId).style.display = "block";
                } else {
                    document.getElementById("follow-" + profileId).style.display = "none";
                    document.getElementById("unfollow-" + profileId).style.display = "block";
                }
            }
        }
    })
}

function unfollowUser(profileId, tab) {
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
                if (tab !== undefined) {
                    document.getElementById(tab + "unfollow-" + profileId).style.display = "none";
                    document.getElementById(tab + "follow-" + profileId).style.display = "block";
                } else {
                    document.getElementById("unfollow-" + profileId).style.display = "none";
                    document.getElementById("follow-" + profileId).style.display = "block";
                }
            }
        }
    })
}
