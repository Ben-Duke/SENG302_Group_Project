getEventsData(-43.53, 172.620278, 'Christchurch', 1);

function getEventsData(latitude, longitude, place, pageNum) {

    const offset = (pageNum - 1) * 20;
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
            addPagination(count);
        }
    });
}

function displayEvents(events) {
    for (let i=0; i < events.length; i++) {
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
        const eventLink = document.createElement("a");
        eventLink.setAttribute("href", events[i].url);
        eventLink.setAttribute("target", "_blank");
        const eventName = document.createElement("h4");
        eventName.classList.add("media-heading");
        eventName.innerText = events[i]["name"];
        eventLink.appendChild(eventName);
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
        eventImageLink.appendChild(eventImage);
        mediaLeft.appendChild(eventImageLink);
        mediaRow.appendChild(mediaLeft);
        mediaRow.appendChild(mediaBody);
        mediaRow.appendChild(document.createElement("hr"));
        document.getElementById("events-results").appendChild(mediaRow);
    }
}

function addPagination(count) {
    latitudes = -43.53;
    longitudes = 172.620278;
    places = 'Christchurch';
    for (let i=0; i < count; i+=20) {
        const pageButton = document.createElement("Button");
        const pageNum = (i/20)+1;
        pageButton.innerText = (i/20)+1;
        pageButton.setAttribute("onClick", `getEventsData(-43.53, 172.620278, 'Christchurch', ${pageNum})`);
        document.getElementById("events-results").appendChild(pageButton);
    }
}

