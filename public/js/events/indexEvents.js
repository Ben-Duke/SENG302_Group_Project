getEventsData(-43.53, 172.620278, 'Christchurch', 1);

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
                }
            });
        }
    });
}

function displayEvents(events) {
    document.getElementById("events-results").appendChild(document.createElement("hr"));
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
        document.getElementById("events-results").style.display = "block";
        document.getElementById("search-container").style.display = "block";
        window.scrollTo(0, 0);
        document.getElementById("loader").style.display = "none";
    }
}

function addPagination(count, pageNum) {
    numOfPages = [];
    pageNumbers = [];
    latitudes = -43.53;
    longitudes = 172.620278;
    places = 'Christchurch';
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
    console.log(pageNumbers);
    let item = document.createElement("li");
    pageButton = document.createElement("a");
    currentPageNum = pageNumbers[0];
    pageButton.innerText = "First";
    pageButton.setAttribute("onClick", `getEventsData(-43.53, 172.620278, 'Christchurch', ${currentPageNum})`);
    item.appendChild(pageButton);
    pagination.appendChild(item);

    item = document.createElement("li");
    pageButton = document.createElement("a");
    currentPageNum = pageNum-1;
    pageButton.innerText = "<";
    pageButton.setAttribute("onClick", `getEventsData(-43.53, 172.620278, 'Christchurch', ${currentPageNum})`);
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
        pageButton.setAttribute("onClick", `getEventsData(-43.53, 172.620278, 'Christchurch', ${currentPageNum})`);
        item.appendChild(pageButton);
        pagination.appendChild(item);
    }
    item = document.createElement("li");
    pageButton = document.createElement("a");
    currentPageNum = pageNum+1;
    pageButton.innerText = ">";
    pageButton.setAttribute("onClick", `getEventsData(-43.53, 172.620278, 'Christchurch', ${currentPageNum})`);
    item.appendChild(pageButton);
    pagination.appendChild(item);
    document.getElementById("events-results").appendChild(pagination);

    item = document.createElement("li");
    pageButton = document.createElement("a");
    currentPageNum = numOfPages.length;
    pageButton.innerText = "Last";
    pageButton.setAttribute("onClick", `getEventsData(-43.53, 172.620278, 'Christchurch', ${currentPageNum})`);
    item.appendChild(pageButton);
    pagination.appendChild(item);
    document.getElementById("events-results").appendChild(pagination);
}

function loader() {
    document.getElementById("search-container").style.display = "none";
    document.getElementById("events-results").style.display = "none";
    document.getElementById("loader").style.display = "block";
}