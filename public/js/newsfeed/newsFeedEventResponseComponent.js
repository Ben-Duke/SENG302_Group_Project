/**
 * Creates and displays a new event response on the news feed page
 *
 * The event provided in the function parameter should contain the following attributes:
 * event.name (The title of the event)
 * event.url (The url to the event)
 * event.imageUrl (The picture of the event)
 * event.address (The address of the event)
 * event.startTime (The start time of the event)
 * event.endTime (The end time of the event)
 * event.type (The type of the event)
 * event.description (The description of the event)
 *
 * The user provided in the function parameter should contain the following attributes:
 * user.name (The name of the user)
 * user.profilePicUrl (The profile pic src of the user) (not sure if this will work)
 *
 *
 * @param event the event the user responded to
 * @param user the user who responded to the event
 * @param responseTime the time that the user responded. Format: "HH:mm dd-MM"
 */
function createNewsFeedEventResponseComponent(event, user, responseTime) {

    const path = "/users/home/servePicture/";

    let newsContainer = document.getElementById("newsContainer");

    let newsItemDiv = document.createElement("div");
    newsItemDiv.setAttribute("class", "newsItem");

    let newsHeaderDiv = document.createElement("div");
    newsHeaderDiv.setAttribute("class", "newsHeader");

    let userProfileImg = document.createElement("img");
    userProfileImg.setAttribute("class", "img-circle userProfileImg");
    // userProfileImg.setAttribute("class", "img-circle");
    if (user.profilePicUrl === "null") {
        userProfileImg.src = "/assets/images/Generic.png";
    } else {
        userProfileImg.src = path + encodeURIComponent(user.profilePicUrl);
    }

    let newsTitle = document.createElement("span");
    newsTitle.setAttribute("class", "newsTitle");
    //Insert checks for time. If after today's date, went to event.
    //If before today's date, going to event
    //For now, they are always going to the event
    //For now, "is interested" is omitted but it'll be easy to add in
    newsTitle.innerText = user.name + " is going to " + event.name;

    let newsTimeTxt = document.createElement("span");
    newsTimeTxt.setAttribute("class", "newsTimeTxt");
    newsTimeTxt.innerText = responseTime;

    newsHeaderDiv.appendChild(userProfileImg);
    newsHeaderDiv.appendChild(newsTitle);
    newsHeaderDiv.appendChild(newsTimeTxt);

    let newsBodyDiv = document.createElement("div");
    newsBodyDiv.setAttribute("class", "newsBody");

    let mediaDiv = document.createElement("div");
    mediaDiv.setAttribute("class", "media");

    let mediaLeftDiv = document.createElement("div");
    mediaLeftDiv.setAttribute("class", "media-left");

    let link1 = document.createElement("a");
    link1.href = `events/${event.id}`;
    link1.target = "_blank";

    let eventThumbnailImg = document.createElement("img");
    eventThumbnailImg.setAttribute("class", "img-thumbnail");
    eventThumbnailImg.src = event.imageUrl;

    let mediaBodyDiv = document.createElement("div");
    mediaBodyDiv.setAttribute("class", "media-body");

    let link2 = document.createElement("a");
    link2.href = `events/${event.id}`;
    link2.target = "_blank";


    let eventHeading = document.createElement("h4");
    eventHeading.setAttribute("class", "media-heading");
    eventHeading.innerText = event.name;

    let eventAddress = document.createElement("p");
    eventAddress.innerText = event.address;

    let eventTime = document.createElement("p");
    eventTime.innerText = "Start: " + event.startTime + "   End: " + event.endTime;

    let eventType = document.createElement("p");
    eventType.innerText = "Type: " + event.type;

    let eventDescription = document.createElement("p");
    eventDescription.innerText = event.description;

    let hr = document.createElement("hr");

    link1.appendChild(eventThumbnailImg);
    link2.appendChild(eventHeading);
    mediaLeftDiv.appendChild(link1);
    mediaBodyDiv.appendChild(link2);
    mediaBodyDiv.appendChild(eventAddress);
    mediaBodyDiv.appendChild(eventTime);
    mediaBodyDiv.appendChild(eventType);
    mediaBodyDiv.appendChild(eventDescription);
    mediaDiv.appendChild(mediaLeftDiv);
    mediaDiv.appendChild(mediaBodyDiv);
    mediaDiv.appendChild(hr);
    newsBodyDiv.appendChild(mediaDiv);

    let newsFooterDiv = document.createElement("div");
    newsFooterDiv.setAttribute("class", "newsFooter");

    newsItemDiv.appendChild(newsHeaderDiv);
    newsItemDiv.appendChild(newsBodyDiv);
    newsItemDiv.appendChild(newsFooterDiv);

    newsContainer.appendChild(newsItemDiv);
}