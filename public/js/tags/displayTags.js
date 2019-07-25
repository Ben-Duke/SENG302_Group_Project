window.onload = function () {
    loadContent();
};

function loadContent() {
    const tagId = document.getElementById("tag-feed").dataset.tag;

    $.ajax({
        type: 'GET',
        url: 'destinations/' + tagId,
        success: function(destinationData) {
            createDestinationContent(destinationData)
        }
    });

}

function createDestinationContent(destinationData) {
    const tagFeed = document.getElementById('tag-feed');
    console.log(destinationData);


}