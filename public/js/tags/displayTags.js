initTagPage();


function initTagPage() {
    function initPhotoDiv() {
        const div = document.getElementById('photoDiv');
        const photos = false;
        if (photos) {
            // todo
        } else {
            div.appendChild(document.createTextNode("There are no photos for this tag (NOT YET IMPLEMENTED)"));
        }
    }

    function initDestDiv() {
        const div = document.getElementById('destinationDiv');
        const destinations = false;
        if (destinations) {
        } else {
            div.appendChild(document.createTextNode("There are no destinations for this tag (NOT YET IMPLEMENTED)"));
        }
    }

    function initTripDiv() {
        const div = document.getElementById('tripDiv');
        const trips = false;
        if (trips) {
            // todo
        } else {
            div.appendChild(document.createTextNode("There are no trips for this tag (NOT YET IMPLEMENTED)"));
        }
    }

    initPhotoDiv();
    initDestDiv();
    initTripDiv();
}