/**
 * Creates and displays a new media upload on the news feed page
 *
 * The media provided in the function parameter should contain the following attributes:
 * media.url (The url to the src of the media)
 *
 * The user provided in the function parameter should contain the following attributes:
 * user.name (The name of the user)
 * user.profilePicUrl (The profile pic src of the user) (not sure if this will work)
 *
 *
 * @param media the media that the user uploaded
 * @param user the user who uploaded the media
 * @param responseTime the time that the user uploaded the media
 */
function createNewsFeedMediaComponent(media, user, responseTime) {
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
    newsTitle.innerText = user.name + " uploaded a photo";

    let newsTimeTxt = document.createElement("span");
    newsTimeTxt.setAttribute("class", "newsTimeTxt");
    newsTimeTxt.innerText = responseTime;

    newsHeaderDiv.appendChild(userProfileImg);
    newsHeaderDiv.appendChild(newsTitle);
    newsHeaderDiv.appendChild(newsTimeTxt);

    let newsBodyDiv = document.createElement("div");
    newsBodyDiv.setAttribute("class", "newsBody");

    let userPhotoImg = document.createElement("img");
    userPhotoImg.setAttribute("class", "userPhotoImg");
    //Might not work. albumView.js line 349 for reference.
    // Maybe ask Priyesh on how to do this.
    userPhotoImg.src = media.url;

    newsBodyDiv.appendChild(userPhotoImg);

    let newsFooterDiv = document.createElement("div");
    newsFooterDiv.setAttribute("class", "newsFooter");

    newsItemDiv.appendChild(newsHeaderDiv);
    newsItemDiv.appendChild(newsBodyDiv);
    newsItemDiv.appendChild(newsFooterDiv);

    newsContainer.appendChild(newsItemDiv);
}