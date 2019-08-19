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
            addPagination(count);
        }
    });
}

function addPagination(count) {
    for (let i=0; i < count; i+=20) {
        const pageButton = document.createElement("Button");
        pageButton.innerText = ((i/20)+1);
        document.getElementById("events-results").appendChild(pageButton);
    }
}