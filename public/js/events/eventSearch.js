
function searchEvents() {

    let keyword = document.getElementById('keyword-search-input').value;
    let category = document.getElementById('category-search-input').value;
    let artist = document.getElementById('artist-search-input').value;
    let startDate = document.getElementById('start-datepicker').value;
    let endDate = document.getElementById('end-datepicker').value;
    let minPrice = document.getElementById('minprice-search-input').value;
    let maxPrice = document.getElementById('maxprice-search-input').value;
    let destination = document.getElementById('destination-search-input').name;
    let sortBy = document.getElementById('sort-search-input').value;

    let query = {
        keyword: keyword,
        category: category,
        artist: artist,
        startDate: startDate,
        endDate: endDate,
        minPrice: minPrice,
        maxPrice: maxPrice,
        destination: destination,
        sortBy: sortBy
    };


    let url = "/users/events/";

    url += "?keyword=" + query.keyword;
    url += "&category=" + query.category;
    url += "&artist=" + query.artist;
    url += "&startDate=" + query.startDate;
    url += "&endDate=" + query.endDate;
    url += "&minPrice=" + query.minPrice;
    url += "&maxPrice=" + query.maxPrice;
    url += "&destination=" + query.destination;
    url += "&sortBy=" + query.sortBy;


    // POST to server using $.post or $.ajax
    $.ajax({
        type: 'GET',
        url: url,
        success: function (data) {
            createEventsResultsElements(data);

        }
    });
}

function createEventsResultsElements(data) {

    for (let i = 0; i < data.size(); i++) {
        let event = data[i];
        let eventRow = document.createElement("div");
        eventRow.setAttribute("class", "row");
        eventRow.setAttribute("id", "media-row-"+i);
        let media = document.createElement("div");
        media.setAttribute("class", "media");
        let mediaLeft = document.createElement("div");
        mediaLeft.setAttribute("class", "media-left");
        let mediaBody = document.createElement("div");
        mediaBody.setAttribute("class", "media-body");

        let imgLink = document.createElement("a");
        let img = document.createElement("img");
        img.setAttribute("class", "img-thumbnail");
        img.setAttribute("src", "/assets/images/destinationPlaceHolder.png");

        imgLink.appendChild(img);
        mediaLeft.appendChild(imgLink);
        media.appendChild(mediaLeft);

        let eventName = document.createElement("h4");
        eventName.setAttribute("class", "media-heading");
        eventName.innerText = event.name;

        let eventDate = document.createElement("p");
        eventDate.innerText = event.startDate;

        let eventCity = document.createElement("p");
        eventCity.innerText = event.city;

        mediaBody.appendChild(eventName);
        mediaBody.appendChild(eventDate);
        mediaBody.appendChild(eventCity);

        media.appendChild(mediaBody);

        eventRow.appendChild(media);

    }
}