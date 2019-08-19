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
            console.log(events);
            displayEvents(events);
            addPagination(count);
        }
    });
}

function displayEvents(events) {
    for (let i=0; i < events.length; i++) {
        const mediaRow = document.createElement("div");
        mediaRow.classList.add("row", "media")
        const mediaLeft = document.createElement("div");
        mediaLeft.classList.add("media-left");
        const eventImageLink = document.createElement("a");
        eventImageLink.setAttribute("href", "#");
        const eventImage = document.createElement("img-thumbnail");
        eventImage.setAttribute("src", "/assets/images/destinationPlaceHolder.png");


        const mediaBody = document.createElement("div");
        mediaBody.classList.add("media-body")
        const eventName = document.createElement("h4");
        eventName.classList.add("media-heading");
        eventName.innerText = events[i]["name"];
        const eventDateTime = document.createElement("p");
        eventDateTime.innerText = events[i].datetime_start + "-" + events[i].datetime_end;
        const eventAddress = document.createElement("p");
        eventAddress.innerText = events[i].address;
        const eventDescription = document.createElement("p");
        eventDescription.innerText = events[i].description;

        mediaBody.appendChild(eventName);
        mediaBody.appendChild(eventDateTime);
        mediaBody.appendChild(eventAddress);
        mediaBody.appendChild(eventDescription);

        eventImageLink.appendChild(eventImage);
        mediaLeft.appendChild(eventImageLink);
        mediaRow.appendChild(mediaLeft);
        mediaRow.appendChild(mediaBody);

        document.getElementById("events-results").appendChild(mediaRow);

    }
}

function addPagination(count) {
    for (let i=0; i < count; i+=20) {
        const pageButton = document.createElement("Button");
        pageButton.innerText = ((i/20)+1);
        document.getElementById("events-results").appendChild(pageButton);
    }
}

