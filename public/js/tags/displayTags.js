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
        data.img = "https://img.purch.com/w/660/aHR0cDovL3d3dy5saXZlc2NpZW5jZS5jb20vaW1hZ2VzL2kvMDAwLzAzOS84ODUvb3JpZ2luYWwvc2h1dHRlcnN0b2NrXzc3NDAwNjYxLmpwZw==";
    } else if (item.hasOwnProperty('destName')) {
        data.header = item.destName;
        data.type = 'Destination';
        data.body = item.district + ', ' + item.country;
        if(item.primaryPhoto != null) {
            data.img = '/users/home/servePicture/' + encodeURIComponent(item.primaryPhoto.urlWithPath);
        } else {
            data.img = "/users/home/servePlaceholderPicture/destinationPlaceholder.png"
        }

    } else if (item.hasOwnProperty('caption')) {
        data.header = item.caption;
        if (item.caption === "") {
            data.header = "Uncaptioned Photo"
        }
        data.type = 'Photo';
        data.body = '';
        data.img = '/users/home/servePicture/' + encodeURIComponent(item.urlWithPath);
    }
    return data;
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
    link.href = "http://google.com";
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
        console.log(item);
        const data = getItemData(item);
        addItem(data);
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