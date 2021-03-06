let map;
window.globalMarkers = [];

window.onload = setUpRespondButtons;
var latitude;
var longitude;

function setLatLng(lat, lng) {
    latitude = lat;
    longitude = lng;
}

function initMap() {
    map = window.globalMap = new google.maps.Map(document.getElementById('map'), {
        center: {lat: -43.522057156877615, lng: 172.62360347218828},
        zoom: 5,
        mapTypeControl: true,
        mapTypeControlOptions: {
            style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR,
            position: google.maps.ControlPosition.TOP_RIGHT
        }
    });
    initEventMarker()
}


/**
 * Displays all visible destination markers to the google map. Add's the marker
 * objects to the window.globalMarkers global variable.
 */
function initEventMarker() {
    let marker = new google.maps.Marker({
        position: {
            lat: latitude,
            lng: longitude
        },
        map: window.globalMap
    });
    //make the marker and infoWindow globals (persist in browser session)
    window.globalMarkers.push({
        marker: marker,
        isClicked: false
    });
    let destLatLng = new google.maps.LatLng(
        latitude, longitude
    );

    window.globalMap.setCenter(destLatLng);
    window.globalMap.setZoom(10);

}

function linkPhotoToEvent(mediaId, eventId) {
    $.ajax({
        type: 'PUT',
        url: `/events/linkphoto/${mediaId}/${eventId}`,
        success: function () {
            window.location.reload()
        }
    });
}

function unlinkPhotoToEvent(mediaId, eventId) {
    $.ajax({
        type: 'PUT',
        url: `/events/unlinkphoto/${mediaId}/${eventId}`,
        success: function () {
            window.location.reload()
        }
    });
}

$("#photo-carousel").on('hidden.bs.modal', function(){
    const carouselItems = document.getElementById("carousel-inner").children;
    for (let item of carouselItems) {
        item.classList.remove('active');
    }
    activePhotoId = undefined;
});

let activePhotoId;
function displayPhoto(photoId) {
    if (activePhotoId !== undefined) {
        document.getElementById('caro-'+activePhotoId).classList.remove('active');
    }

    document.getElementById('caro-'+photoId).classList.add('active');
    activePhotoId = photoId;
}

/**
 * Sets a photo privacy to the setting specified
 * @param mediaId the id of the media to change privacy
 * @param setPublic true to set to public, false to set to private
 */
function setMediaPrivacy(mediaId, setPublic) {
    const intPublic = setPublic ? 1 : 0;
    $.ajax({
        type: 'GET',
        url: '/users/home/photoPrivacy/' + mediaId + '/' + intPublic,
        contentType: 'application/json',
        success: () => {
            const makePublic = document.getElementById(`makePublicLink-${mediaId}`);
            const makePrivate = document.getElementById(`makePrivateLink-${mediaId}`);

            makePublic.style.display = makePublic.style.display === "none" ? "" : "none";
            makePrivate.style.display = makePrivate.style.display === "none" ? "" : "none"

            const privacyEye = document.getElementById(`privacy-eye-${mediaId}`);
            if (privacyEye.classList.contains("fa-eye-red")) {
                privacyEye.classList.remove("fa-eye-red");
                privacyEye.classList.add("fa-eye-green");
            } else {
                privacyEye.classList.remove("fa-eye-green");
                privacyEye.classList.add("fa-eye-red");
            }
        }
    });
}


function respondToEvent(eventId, responseType){
    let going = document.querySelector('[data-event-id="'+eventId+'"] [data-going]')
    let interested = document.querySelector('[data-event-id="'+eventId+'"] [data-interested]')
    let notGoing = document.querySelector('[data-event-id="'+eventId+'"] [data-notGoing]')
    going.setAttribute("data-Going", "false")
    interested.setAttribute("data-Interested", "false")
    notGoing.setAttribute("data-NotGoing", "false")
    going.classList.remove("btn-primary")
    interested.classList.remove("btn-primary")
    notGoing.classList.remove("btn-primary")
    going.classList.add("btn-light")
    interested.classList.add("btn-light")
    notGoing.classList.add("btn-light")
    let object = document.querySelector('[data-event-id="'+eventId+'"] [data-'+responseType+']')
    object.setAttribute('data-'+responseType, "true");
    object.classList.add("btn-primary");
    $.ajax({
        type: 'PUT',
        url: "/events/respond/" + eventId + "/" + responseType,
        success: function () {
            location.reload();
        },
        error : function () {
            let going = document.querySelector('[data-event-id="'+eventId+'"] [data-going]')
            let interested = document.querySelector('[data-event-id="'+eventId+'"] [data-interested]')
            let notGoing = document.querySelector('[data-event-id="'+eventId+'"] [data-notGoing]')
            going.setAttribute("data-Going", "false")
            interested.setAttribute("data-Interested", "false");
            notGoing.setAttribute("data-NotGoing", "false");
            going.classList.remove("btn-primary");
            interested.classList.remove("btn-primary");
            notGoing.classList.remove("btn-primary");
            going.classList.add("btn-light");
            interested.classList.add("btn-light");
            notGoing.classList.add("btn-light");
        }
    });
}

function setUpRespondButtons() {
    let isGoingResponses;
    let isInterestedResponses;
    let isNotGoingResponses;

    $.ajax({
        async: false,
        type: 'GET',
        url: "/events/responses/Going",
        success: function (resData) {
            isGoingResponses = resData;
        },
    })


    $.ajax({
        async: false,
        type: 'GET',
        url: "/events/responses/Interested",
        success: function (resData) {
            isInterestedResponses = resData;
        },
    })

    $.ajax({
        async: false,
        type: 'GET',
        url: "/events/responses/NotGoing",
        success: function (resData) {
            isNotGoingResponses = resData;
        },
    });

    const allResponses = [isGoingResponses, isInterestedResponses, isNotGoingResponses]

    const respondButtons = document.getElementById("respondButtons");


    const eventId = respondButtons.dataset.eventId;

    const goingResponse = document.createElement("a");
    goingResponse.setAttribute("data-responseButton", true);
    goingResponse.classList.add("btn");
    goingResponse.classList.add("btn-light");
    goingResponse.setAttribute("onclick", "respondToEvent(" + eventId + ", 'Going'" + ")");
    goingResponse.setAttribute("data-going", "false");

    const interestedResponse = document.createElement("a");
    goingResponse.setAttribute("data-responseButton", true);
    interestedResponse.classList.add("btn");
    interestedResponse.classList.add("btn-light");
    interestedResponse.setAttribute("onclick", "respondToEvent(" + eventId + ", 'Interested'" + ")");
    interestedResponse.setAttribute("data-interested", "false");

    const notGoingResponse = document.createElement("a");
    goingResponse.setAttribute("data-responseButton", true);
    notGoingResponse.classList.add("btn");
    notGoingResponse.classList.add("btn-light");
    notGoingResponse.setAttribute("onclick", "respondToEvent(" + eventId + ", 'NotGoing'" + ")");
    notGoingResponse.setAttribute("data-notGoing", "false");

    goingResponse.innerText = "Going";
    interestedResponse.innerText = "Interested";
    notGoingResponse.innerText = "Not Going"

    for (let j=0; j < allResponses.length; j++) {
        for (let k=0; k < allResponses[j].responses.length; k++) {
            if (allResponses[j].responses[k].responseType == "Going" &&
                    allResponses[j].responses[k].event.externalId == eventId) {
                goingResponse.classList.add("btn-primary");
                goingResponse.setAttribute("data-going", "true");
            } else if (allResponses[j].responses[k].responseType == "Interested" &&
                    allResponses[j].responses[k].event.externalId == eventId) {
                interestedResponse.classList.add("btn-primary");
                interestedResponse.setAttribute("data-interested", "true");
            } else if (allResponses[j].responses[k].responseType == "NotGoing" &&
                    allResponses[j].responses[k].event.externalId == eventId) {
                notGoingResponse.classList.add("btn-primary");
                notGoingResponse.setAttribute("data-notGoing", "true");
            }
        }
    }
    respondButtons.appendChild(goingResponse);
    respondButtons.appendChild(interestedResponse);
    respondButtons.appendChild(notGoingResponse);

    // // set up response counts
    // const responseCounts = document.getElementById('event-response-count');
    // responseCounts.innerText = `${isGoingResponses.responses.length} Going | `;
    // responseCounts.innerText += ` ${isInterestedResponses.responses.length} Interested | `;
    // responseCounts.innerText += ` ${isNotGoingResponses.responses.length} Not Going`;

    const eventFindaLogoLink = document.createElement("a");
    eventFindaLogoLink.setAttribute("href", "https://www.eventfinda.co.nz");
    eventFindaLogoLink.setAttribute("target", "_blank");
    const eventFindaLogo = document.createElement("img");
    eventFindaLogo.setAttribute("src", "https://www.eventfinda.co.nz/images/global/attribution.gif?pwiomi");
    eventFindaLogo.setAttribute("style", "margin-right:10px; width:170px; height:25px; position: fixed; bottom:0; right:0");
    eventFindaLogoLink.appendChild(eventFindaLogo);
    document.getElementById("eventsPage").appendChild(eventFindaLogoLink);
}

