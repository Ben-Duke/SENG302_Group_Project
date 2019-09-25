getEventsData(-43.53, 172.620278, '', 1);
let latestUrl;

function getEventsData(latitude, longitude, place, pageNum) {

    const offset = (pageNum - 1) * 20;
    $.ajax({
        url:loader(),
        success: function(){
            $.ajax({
                type: 'GET',
                url: `/events?latitude=${latitude}&longitude=${longitude}&place=${place}&offset=${offset}`,
                contentType: 'application/json',
                success: (eventData) => {
                    const count = eventData["@attributes"].count;
                    const events = eventData.events;
                    if (pageNum > 1) {
                        let eventsPage = document.getElementById("events-results");
                        while (eventsPage.childNodes.length > 0) {
                            eventsPage.removeChild(eventsPage.childNodes[0]);
                        }
                    }
                    displayEvents(events);
                    addPagination(count, pageNum);
                },
                error: () => {
                    failLoad();
                }
            });
        }
    });
}

function getEventsFromApiResponse(eventData, url, pageNum) {
    latestUrl = url;
    const count = eventData["@attributes"].count;
    const events = eventData.events;
    let eventsPage = document.getElementById("events-results");
    while (eventsPage.childNodes.length > 0) {
        eventsPage.removeChild(eventsPage.childNodes[0]);
    }
    let eventsPagination = document.getElementById("eventsPage");
    while (eventsPagination.childNodes.length > 0) {
        eventsPagination.removeChild(eventsPagination.childNodes[0]);
    }
    displayEvents(events);
    addPagination(count, pageNum);
}

function respondToEvent(eventId, responseType){
    $.ajax({
        success: function () {
            $.ajax({
                type: 'PUT',
                url: "/events/respond/" + eventId + "/" + responseType,
                success: function () {
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
                    object.setAttribute('data-'+responseType, "true")
                    object.classList.add("btn-primary")
                },
            })
        }
    })
}

function getEventResponses(eventId, responseType, userId){
    $.ajax({
        type: 'GET',
        url: "/events/responses/" + eventId + "/" + responseType+ "/" + userId,
        success: function (resData) {
            if (resData.responses.length > 0) return true
        },
    })
}

function getEventResponsesByType(responseType){
    $.ajax({
        type: 'GET',
        url: "/events/responses/" + responseType,
        success: function (resData) {
            return resData;
        },
    })
}

function displayEvents(events) {
    const eventResultsDiv = document.querySelector("#events-results");
    const userId = eventResultsDiv.dataset.userid;
    document.getElementById("events-results").appendChild(document.createElement("hr"));
    let isGoingResponses;
    let isInterestedResponses;
    let isNotGoingResponses;

    $.ajax({
        async: false,
            success: function () {
            $.ajax({
            async: false,
                type: 'GET',
                url: "/events/responses/Going",
                success: function (resData) {
                    isGoingResponses = resData;
                },
            })
        }
    })

    $.ajax({
        async: false,
            success: function () {
            $.ajax({
            async: false,
                type: 'GET',
                url: "/events/responses/Interested",
                success: function (resData) {
                    isInterestedResponses = resData;
                },
            })
        }
    })

    $.ajax({
        async: false,
            success: function () {
            $.ajax({
            async: false,
                type: 'GET',
                url: "/events/responses/NotGoing",
                success: function (resData) {
                    isNotGoingResponses = resData;
                },
            })
        }
    })

    const allResponses = [isGoingResponses, isInterestedResponses, isNotGoingResponses]
    for (let i=0; i < events.length; i++) {
        let isGoing = false;
        let isInterested = false;
        let isNotGoing = false;
        const mediaRow = document.createElement("div");
        mediaRow.classList.add("media")
        const mediaLeft = document.createElement("div");
        mediaLeft.classList.add("media-left");

        const eventImageLink = document.createElement("a");
        eventImageLink.setAttribute("target", "_blank");

        const lastImage = events[i].images.images[0].transforms["@attributes"].count - 1;
        eventImageLink.setAttribute("href", events[i].images.images[0].transforms.transforms[lastImage].url);

        const eventImage = document.createElement("img");
        eventImage.classList.add("img-thumbnail");
        eventImage.setAttribute("src", events[i].images.images[0].transforms.transforms[lastImage].url);
        const mediaBody = document.createElement("div");
        mediaBody.classList.add("media-body")
        mediaBody.setAttribute("data-event-id", events[i].id)
        const eventLink = document.createElement("a");
        eventLink.setAttribute("href", events[i].id);

        const goingResponse = document.createElement("a");
        goingResponse.classList.add("btn");
        goingResponse.classList.add("btn-light");
        goingResponse.setAttribute("onclick", "respondToEvent(" + events[i].id + ", 'Going'" + ")");
        goingResponse.setAttribute("data-going", "false");
        const interestedResponse = document.createElement("a");
        interestedResponse.classList.add("btn");
        interestedResponse.classList.add("btn-light");
        interestedResponse.setAttribute("onclick", "respondToEvent(" + events[i].id + ", 'Interested'" + ")");
        interestedResponse.setAttribute("data-interested", "false");
        const notGoingResponse = document.createElement("a");
        notGoingResponse.classList.add("btn");
        notGoingResponse.classList.add("btn-light");
        notGoingResponse.setAttribute("onclick", "respondToEvent(" + events[i].id + ", 'NotGoing'" + ")");
        notGoingResponse.setAttribute("data-notGoing", "false");
        for (let j=0; j < allResponses.length; j++) {
            for (let k=0; k < allResponses[j].responses.length; k++) {
                if (allResponses[j].responses[k].responseType === "Going" &&
                        allResponses[j].responses[k].event.externalId === events[i].id) {
                    goingResponse.classList.add("btn-primary");
                    goingResponse.setAttribute("data-going", "true");
                } else if (allResponses[j].responses[k].responseType === "Interested" &&
                        allResponses[j].responses[k].event.externalId === events[i].id) {
                    interestedResponse.classList.add("btn-primary");
                    interestedResponse.setAttribute("data-interested", "true");
                } else if (allResponses[j].responses[k].responseType === "NotGoing" &&
                        allResponses[j].responses[k].event.externalId === events[i].id) {
                    notGoingResponse.classList.add("btn-primary");
                    notGoingResponse.setAttribute("data-notGoing", "true");
                }
            }
        }

        const eventName = document.createElement("h4");
        eventName.classList.add("media-heading");
        eventName.innerText = events[i]["name"];
        eventLink.appendChild(eventName);
        goingResponse.innerText = "Going";
        interestedResponse.innerText = "Interested";
        notGoingResponse.innerText = "Not Going"
        const eventDateTime = document.createElement("p");
        eventDateTime.innerText = "Starts: " + events[i].datetime_start + "\nEnds: " + events[i].datetime_end;
        const eventAddress = document.createElement("p");
        eventAddress.innerText = events[i].address;
        const eventCategory = document.createElement("p");
        eventCategory.innerText = "Type: " + events[i].category.name
        const eventDescription = document.createElement("p");
        eventDescription.innerText = events[i].description;
        mediaBody.appendChild(eventLink);
        mediaBody.appendChild(eventAddress);
        mediaBody.appendChild(eventDateTime);
        mediaBody.appendChild(eventCategory);
        mediaBody.appendChild(eventDescription);
        mediaBody.appendChild(goingResponse);
        mediaBody.appendChild(interestedResponse);
        mediaBody.appendChild(notGoingResponse);
        eventImageLink.appendChild(eventImage);
        mediaLeft.appendChild(eventImageLink);
        mediaRow.appendChild(mediaLeft);
        mediaRow.appendChild(mediaBody);
        mediaRow.appendChild(document.createElement("hr"));
        document.getElementById("events-results").appendChild(mediaRow);
    }

    unLoader();
}

function addPagination(count, pageNum) {
    numOfPages = [];
    pageNumbers = [];
    latitudes = -43.53;
    longitudes = 172.620278;
    places = '';
    const pagination = document.createElement("ul");
    pagination.classList.add("pagination");
    for (let i=0; i < count; i+=20) {
        numOfPages.push((i/20)+1);
    }

    if (numOfPages.length > 10) {
        if (pageNum > 5) {
            if (numOfPages.length >= pageNum+5) {
                pageNumbers = [pageNum-3,pageNum-2, pageNum-1, pageNum, pageNum+1, pageNum+2, pageNum+3, pageNum+4];
            } else {
                lastPage = numOfPages.length-0;
                pageNumbers = []
                for (let j=lastPage-7; (j<lastPage+1 && j>0); j++) {
                    pageNumbers.push(j);
                }
            }
        } else {
            for (let k=0; k<10; k++) {
                pageNumbers.push(numOfPages[k]);
            }
        }
    } else {
        pageNumbers = numOfPages;
    }
    let item = document.createElement("li");
    pageButton = document.createElement("a");
    currentPageNum = 1;
    pageButton.innerText = "First";
    pageButton.setAttribute("onClick", `searchEvents(${currentPageNum})`);
    item.appendChild(pageButton);
    pagination.appendChild(item);

    item = document.createElement("li");
    pageButton = document.createElement("a");
    if (pageNum<2) {
        currentPageNum = 1;
    } else {
        currentPageNum = pageNum-1;
    }
    pageButton.innerText = "<";
    pageButton.setAttribute("onClick", `searchEvents(${currentPageNum})`);
    item.appendChild(pageButton);
    pagination.appendChild(item);
    for (let i=0; i < pageNumbers.length; i++) {
        let item = document.createElement("li");
        const pageButton = document.createElement("a");
        const currentPageNum = pageNumbers[i];
        pageButton.innerText = pageNumbers[i];
        if (currentPageNum==pageNum) {
            pageButton.classList.add("active");
        }
        pageButton.setAttribute("onClick", `searchEvents(${currentPageNum})`);
        item.appendChild(pageButton);
        pagination.appendChild(item);
    }
    item = document.createElement("li");
    pageButton = document.createElement("a");
    if (pageNum>=numOfPages.length) {
        currentPageNum = numOfPages.length;
    } else {
        currentPageNum = pageNum+1;
    }
    pageButton.innerText = ">";
    pageButton.setAttribute("onClick", `searchEvents(${currentPageNum})`);
    item.appendChild(pageButton);
    pagination.appendChild(item);
    document.getElementById("events-results").appendChild(pagination);

    item = document.createElement("li");
    pageButton = document.createElement("a");
    currentPageNum = numOfPages.length;
    pageButton.innerText = "Last";
    pageButton.setAttribute("onClick", `searchEvents(${currentPageNum})`);
    item.appendChild(pageButton);
    pagination.appendChild(item);
    let eventsPagination = document.getElementById("eventsPage");
    while (eventsPagination.childNodes.length > 0) {
        eventsPagination.childNodes[0].remove();
    }
    eventFindaLogo = document.createElement("img");
    eventFindaLogo.setAttribute("src", "https://www.eventfinda.co.nz/images/global/attribution.gif?pwiomi");
    eventFindaLogo.setAttribute("style", "margin-right:10px; width:170px; height:25px; position: absolute; bottom:0; right:0");
    document.getElementById("eventsPage").appendChild(pagination);
    document.getElementById("eventsPage").appendChild(eventFindaLogo);
}

function loader() {
    document.getElementById("search-container").style.display = "none";
    document.getElementById("events-results").style.display = "none";
    document.getElementById("eventsPage").style.display = "none";
    document.getElementById("loader").style.display = "block";
}

function unLoader() {
    document.getElementById("events-results").style.display = "block";
    document.getElementById("search-container").style.display = "block";
    document.getElementById("eventsPage").style.display = "block";
    window.scrollTo(0, 0);
    document.getElementById("loader").style.display = "none";
}

function failLoad() {
    const page = document.getElementById('loader');
    page.innerText = "We are struggling to reach EventFinda. Please connect to the internet"
}