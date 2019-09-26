let lastScrollY_GLOBAL = window.scrollY;
let hasNewsFeedFinishedInitialLoad_GLOBAL = false;

let lazyLoadingFinished = false;

const eventsResponsesLoaded = new Set();
const mediaLoaded = new Set();

let oldestDateTimeOfLoadedEventResponse_GLOBAL = getCurrentDate();
let oldestDateTimeOfLoadedMedia_GLOBAL = getCurrentDate();

initNewsfeed();
initLazyLoading();

function getCurrentDate() {
    const date = new Date();
    let day = '' + date.getDate();
    let month = '' + (date.getMonth() + 1);
    const year = date.getFullYear();

    let hour = '' + date.getHours();
    let minutes = '' + date.getMinutes();
    let seconds = '' + date.getSeconds();

    if (month.length < 2)
        month = '0' + month;
    if (day.length < 2) {
        day = '0' + day;
    }
    if (hour.length < 2) {
        hour = '0' + hour
    }
    if (minutes.length < 2) {
        minutes = '0' + minutes
    }
    if (seconds.length < 2) {
        seconds = '0' + seconds
    }
    return `${day}-${month}-${year} ${hour}:${minutes}:${seconds}`
}


function parseDate(dateStr) {
    const workingDateParts = dateStr.trim().split(" ");
    const dateParts = workingDateParts[0].split("-");
    const timePart = workingDateParts[1];

    const dateString = `${dateParts[2]}-${dateParts[1]}-${dateParts[0]} ${timePart}`;
    return new Date(dateString)
}

function populateNewsFeed(responses, mediaResult) {
    let newsFeedItems = [];
    for (let photo of mediaResult) {
        photo.dateKey = parseDate(photo.date_created);
        if (!mediaLoaded.has(photo.url)) {
            mediaLoaded.add(photo.url);
            newsFeedItems.push(photo);
        }
    }
    for (let eventResponse of responses) {
        if (!eventsResponsesLoaded.has(eventResponse.responseId)) {
            eventResponse.dateKey = parseDate(eventResponse.responseDateTime);
            eventsResponsesLoaded.add(eventResponse.responseId);
            newsFeedItems.push(eventResponse);
        }
    }
    newsFeedItems.sort((b, a) => {
        if (a.dateKey > b.dateKey)
            return 1;
        else if (a.dateKey < b.dateKey)
            return -1;
        return 0
    });
    for (let item of newsFeedItems) {
        if (item.hasOwnProperty("responseId")) {
            createNewsFeedEventResponseComponent(item.event, item.user, item.responseDateTime)
        }
        else if (item.hasOwnProperty("url")) {
            createNewsFeedMediaComponent(item.url, item.user, item.date_created);
        }
    }

    if (responses.length + mediaResult.length === 0) {
        finishLazyLoading();
    } else {
        try {
            oldestDateTimeOfLoadedMedia_GLOBAL = mediaResult[mediaResult.length - 1].date_created;
        } catch {
            console.log("No media in this request");
        }
        try {
            oldestDateTimeOfLoadedEventResponse_GLOBAL = responses[responses.length - 1].responseDateTime;
        } catch {
            console.log("No event responses in this request")
        }
    }

    hasNewsFeedFinishedInitialLoad_GLOBAL = true;
}

function requestMediaItemsAndLoadNewsFeed(responses) {
    $.ajax({
        type: 'GET',
        data: {
            offset: 0,
            limit: 10,
            localDateTime: oldestDateTimeOfLoadedMedia_GLOBAL
        },
        url: '/users/newsfeed/media',
        success: function (mediaResult) {
            populateNewsFeed(responses, mediaResult);
        },
        error: (err) => {
            console.error(err);
            hasNewsFeedFinishedInitialLoad_GLOBAL = true;
        }
    })
}

function requestNewsfeedItems() {
    $.ajax({
        url: '/events/responses/getjson',
        type: 'GET',
        data: {
            offset: 0,
            limit: 5,
            localDateTime: oldestDateTimeOfLoadedEventResponse_GLOBAL
        },
        success: function (eventResponses) {
            const responses = eventResponses.responses;
            requestMediaItemsAndLoadNewsFeed(responses)
        },
        error: (err) => {
            console.error(err);
            hasNewsFeedFinishedInitialLoad_GLOBAL = true;
        }
    });
}

function getAndLoadMoreNewsFeedItems() {
    if (lazyLoadingFinished) return; // Returns early if lazy loading finished

    let token = $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function (xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    requestNewsfeedItems();
}


function finishLazyLoading() {
    lazyLoadingFinished = true;
    document.getElementById('loadMoreBtn').style.display = 'none';
    document.getElementById('endOfScroll').style.display = "inline-block";
}


function initNewsfeed() {
    getAndLoadMoreNewsFeedItems();
}

/**
 * Function to setup lazy loading.
 *
 * Sets the scrollY for figuring out in the future which direction the page was
 * scrolled. Also sets up the scroll event listener.
 */
function initLazyLoading() {
    lastScrollY_GLOBAL = window.scrollY;
    document.addEventListener("scroll", processScrollEvent);
}

/**
 * Checks if the page should be lazy loaded.
 *
 * NOTE: this function has side effects: the global variable lastScrollY_GLOBAL
 * gets modified as part of this process.
 *
 * @returns {boolean} true if lazy loading should occur
 */
function isLazyLoadingTriggered() {
    if (! hasNewsFeedFinishedInitialLoad_GLOBAL) {
        lastScrollY_GLOBAL = window.scrollY;
        return false; // news feed has not finished innital page load.
    }

    // checking if the page has been scrolled down
    const currentScrollY = window.scrollY;
    const scrollDelta = currentScrollY - lastScrollY_GLOBAL;
    if (scrollDelta < 1) {
        lastScrollY_GLOBAL = window.scrollY;
        return false; // page scrolled up, so dont try load new stuff
    }

    const hasScrolledBelowTriggerPoint = (window.innerHeight + window.scrollY)
        >= (document.body.offsetHeight * 0.95);

    if (! hasScrolledBelowTriggerPoint) {
        lastScrollY_GLOBAL = window.scrollY;
        return false; // page hasn't scrolled far enough down to load new stuff
    }

    lastScrollY_GLOBAL = window.scrollY;
    return true;
}

/**
 * A function to handle a scroll event.
 *
 * @param event
 */
function processScrollEvent(event) {
    if (isLazyLoadingTriggered() && !lazyLoadingFinished) {
        getAndLoadMoreNewsFeedItems();
    }
}