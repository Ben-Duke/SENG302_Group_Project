window.onload = function () {
    loadContent();
};

function loadContent() {
    const tagId = document.getElementById("tag-feed").dataset.tag;

    $.ajax({
        type: 'GET',
        url: '/tags/items/' + tagId,
        success: function(tagData) {
            createTagContent(tagData)
        }
    });

}

function getItemData(item) {
    const data = {};
    if (item.hasOwnProperty('tripName')) {
        data.header = item.tripName;
        data.type = 'Trip';
        if (item.tripStart && item.tripEnd) {
            data.body = item.tripStart + ' to ' + item.tripEnd;
        } else {
            data.body = "No date entered"
        }
        data.link = "/users/map_home";

        const tripImageURL = "/users/trips/" + item.tripid + "/tripPicture";
        $.ajax({
            type: 'GET',
            url: tripImageURL,
            success: function(data, textStatus, xhr) {
                if (xhr.status !== 200) {
                    data.img = '/assets/images/destinationPlaceHolder.png';
                } else {
                    data.img = tripImageURL;
                }
                addItem(data);
            },
            error: function() {
                data.img = '/assets/images/destinationPlaceHolder.png';
                addItem(data);
            }
        });
    } else if (item.hasOwnProperty('destName')) {
        data.header = item.destName;
        data.type = 'Destination';
        data.body = item.district + ', ' + item.country;
        data.link = "/users/destinations/view/" + item.destId;

        const destImageURL = '/users/destinations/getprimaryphoto/' + item.destId;

        $.ajax({
            type: 'GET',
            url: destImageURL,
            success: function(albumData) {
                data.img = '/users/destinations/getprimaryphoto/' + item.destId;
                addItem(data);
            },
            error: function() {
                data.img = '/assets/images/destinationPlaceHolder.png';
                addItem(data);
            }
        });
    } else if (item.hasOwnProperty('caption')) {
        $.ajax({
                type: 'GET',
                url: '/users/albums/getAlbumFromMediaId/' + item.mediaId,
                success: function(albumData) {
                    data.link = "/users/albums/" + albumData.albumId;
                    data.header = item.caption;
                    if (item.caption === "") {
                        data.header = "Uncaptioned Photo"
                    }
                    data.type = 'Photo';
                    data.body = 'Album: ' + albumData.title;
                    data.img = '/users/home/servePicture/' + encodeURIComponent(item.urlWithPath);
                    addItem(data);
                },
                error: function() {
                    data.link = "/users/albums";
                    data.header = item.caption;
                    if (item.caption === "") {
                        data.header = "Uncaptioned Photo"
                    }
                    data.type = 'Photo';
                    data.body = '';
                    data.img = '/users/home/servePicture/' + encodeURIComponent(item.urlWithPath);
                    addItem(data);
                }
            });


    }

}

function addItem(data) {
    const tagFeed = document.getElementById('tag-feed');

    const media = document.createElement('DIV');
    media.classList.add('media-' + data.type);
    media.style.display = "block";

    const imgDiv = document.createElement('DIV');
    imgDiv.classList.add('media-left');

    const img = document.createElement('IMG');
    img.classList.add('img-thumbnail');
    img.src = data.img;

    const body = document.createElement('DIV');
    body.classList.add('media-body');

    const heading = document.createElement('H3');
    heading.classList.add('media-heading');

    const link = document.createElement('A');
    link.href = data.link;
    link.innerText = data.header;

    const title = document.createElement('STRONG');
    title.innerText = data.body;

    const type = document.createElement('P');
    type.innerText = data.type;

    imgDiv.appendChild(img);
    heading.appendChild(link);
    body.appendChild(heading);
    body.appendChild(title);
    body.appendChild(type);
    media.appendChild(imgDiv);
    media.appendChild(body);
    tagFeed.appendChild(media);

}

function createTagContent(tagData) {
    for (let item of tagData) {
        const data = getItemData(item);
    }
}

/**
 * Filter the tags when a checkbox is clicked
 */
function filterTags() {
    let photoCheckBox = document.getElementById("photos-check");
    if (photoCheckBox.checked === false) {
       removeTags("Photo");
    } else {
        addTags("Photo");
    }

    let tripCheckBox = document.getElementById("trips-check");
    if (tripCheckBox.checked === false) {
        removeTags("Trip");
    } else {
        addTags("Trip");
    }

    let destinationCheckBox = document.getElementById("destinations-check");
    if (destinationCheckBox.checked === false) {
        removeTags("Destination");
    } else {
        addTags("Destination");
    }
}

/**
 * Make tags of a tag type not visible in the list
 * @param tagType the type of tag to hide
 */
function removeTags(tagType) {
    let tags = document.getElementsByClassName("media-" + tagType);
    for(let i = 0; i < tags.length; i++) {
        tags[i].style.display = "none"
    }
}

/**
 * Make tags of a particular tag type visible in the list
 * @param tagType the type of tag to show
 */
function addTags(tagType) {
    let tags = document.getElementsByClassName("media-" + tagType);
    for(let i = 0; i < tags.length; i++) {
        tags[i].style.display = "block"
    }
}