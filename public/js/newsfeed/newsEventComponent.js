
function createNewsEventComponent(event, user) {
    let newsContainer = document.getElementById("newsContainer");

    let newsItemDiv = document.createElement("div");
    newsItemDiv.setAttribute("class", "newsItem");

    let newsHeaderDiv = document.createElement("div");
    newsHeaderDiv.setAttribute("class", "newsHeader");

    let userProfileImg = document.createElement("img");
    userProfileImg.setAttribute("class", "userProfileImg");
    userProfileImg.setAttribute("class", "img-circle");
    userProfileImg.src = user.profilePicUrl;

    let newsTitle = document.createElement("span");
    newsTitle.setAttribute("class", "newsTitle");
    //Insert checks for time. If after today's date, went to event.
    //If before today's date, going to event
    newsTitle.innerText = event.title;

    let newsTimeTxt = document.createElement("span");
    newsTitle.setAttribute("class", "newsTimeTxt");
    newsTimeTxt.innerText = event.time;

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
    link1.href = event.url;
    link1.target = "_blank";

    let eventThumbnailImg = document.createElement("img");
    eventThumbnailImg.setAttribute("class", "img-thumbnail");
    eventThumbnailImg.src = event.eventPic;

    let mediaBodyDiv = document.createElement("div");
    mediaBodyDiv.setAttribute("class", "media-body");

    let link2 = document.createElement("a");
    link2.href = event.url;
    link2.target = "_blank";


    let eventHeading = document.createElement("h4");
    eventHeading.setAttribute("class", "media-heading");
    eventHeading.innerText = event.title;

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