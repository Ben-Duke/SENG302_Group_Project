window.onload = function () {
    loadContent();
};

function loadContent() {
    const tagId = document.getElementById("tag-feed").dataset.tag;

    $.ajax({
        type: 'GET',
        url: '/tags/taggedItems/' + tagId,
        success: function(tagData) {
            createTagContent(tagData)
        }
    });

}

function createTagContent(tagData) {
    const tagFeed = document.getElementById('tag-feed');
    console.log(tagData);
}

function createPhotoContent(photoData) {
    const tagFeed = document.getElementById('tag-feed');
    console.log(photoData);
}

function createTripContent(tripData) {
    const tagFeed = document.getElementById('tag-feed');
    console.log(tripData);
}