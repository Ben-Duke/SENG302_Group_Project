let lastScrollY_GLOBAL = window.scrollY;
let hasNewsFeedFinishedInnitialLoad_GLOBAL = false;

let oldestDateTimeOfLoadedEventResponse_GLOBAL = '30-09-2019%2000:00:00';
let oldestDateTimeOfLoadedMedia = undefined;

initNewsfeed();
initLazyLoading();

function getDateTimeForURL(dateTimeString) {
    const result = dateTimeString.replace("\\s", '%');
    console.log(result);
    return result;
}

function getAndLoadMoreNewsFeedItems() {
    let token = $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function (xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });

    $.ajax({
        url: '/events/responses/getjson',
        type: 'GET',
        data: {
            offset: 0,
            limit: 10,
            localDateTime: getDateTimeForURL(oldestDateTimeOfLoadedEventResponse_GLOBAL)
        },
        success: function (result) {
            const responses = result.responses;
            for (response of responses) {
                createNewsFeedEventResponseComponent(response.event, response.user, response.responseDateTime)
            }

            if (0 < responses.length) {
                oldestDateTimeOfLoadedEventResponse_GLOBAL = responses[responses.length].responseDateTime;
            }

            hasNewsFeedFinishedInnitialLoad_GLOBAL = true;
        },
        error: (err) => {
            console.error(err);
            hasNewsFeedFinishedInnitialLoad_GLOBAL = true;
        }
    })
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
    if (! hasNewsFeedFinishedInnitialLoad_GLOBAL) {
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
    if (isLazyLoadingTriggered()) {
        getAndLoadMoreNewsFeedItems();
    }
}