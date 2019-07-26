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

function createTagContent(tagData) {
    const tagFeed = document.getElementById('tag-feed');
    for (item of tagData) {
        const media = document.createElement('DIV');
        media.classList.add('media');

        const imgDiv = document.createElement('DIV');
        imgDiv.classList.add('media-left');

        const img = document.createElement('IMG');
        img.classList.add('img-thumbnail');
        img.src = "https://img.purch.com/w/660/aHR0cDovL3d3dy5saXZlc2NpZW5jZS5jb20vaW1hZ2VzL2kvMDAwLzAzOS84ODUvb3JpZ2luYWwvc2h1dHRlcnN0b2NrXzc3NDAwNjYxLmpwZw==";

        const body = document.createElement('DIV');
        body.classList.add('media-body');

        const heading = document.createElement('H3');
        heading.classList.add('media-heading');

        const link = document.createElement('A');
        link.href = "http://google.com";
        link.innerText = 'Paris';

        const title = document.createElement('STRONG');
        title.innerText = "Paris, France";

        const type = document.createElement('P');
        type.innerText = "Destination";

        imgDiv.appendChild(img);
        heading.appendChild(link);
        body.appendChild(heading);
        body.appendChild(title);
        body.appendChild(type);
        media.appendChild(imgDiv);
        media.appendChild(body);
        tagFeed.appendChild(media);

    }
}