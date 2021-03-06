var slideIndex = 1;
var photoIdToEdit;
var user;

let destinationsToUnlink_GLOBAL;
let selectedMediaID_GLOBAL;

const quantityToGet = 8;
let offSet = 0;
let albumMediaData = [];
let mediaId;

moveAlbumSearch();

// Add event listener for closing the modal on clicking outside of it
document.getElementById('myModal').addEventListener('click', (e) => {
    e.preventDefault();
    e.stopPropagation();
    e.stopImmediatePropagation();
    if (e.target === document.getElementById('myModal')) {
        closeModal()
    }
});

function moveAlbumSearch() {

      const searchBar = document.getElementById("album-search-move");
      let oldAlbumId = document.getElementById("existingAlbumId").innerText;
      if (searchBar != null) {
          searchBar.addEventListener('input', (e) => {
              if (e.constructor.name !== 'InputEvent') {
                  let title = searchBar.value;
                  $.ajax({
                          type: 'GET',
                          url: '/users/get',
                          contentType: 'application/json',
                          success: function(userData){
                              $.ajax({
                                    type: 'GET',
                                    url: '/users/' + userData + '/albums/getFromTitle/' + title,
                                    contentType: 'application/json',
                                    success: (newAlbumId) => {
                                        moveBetweenAlbums(oldAlbumId, newAlbumId);
                                    }
                                });
                          }
                      });
              }
          });
      }
}

function moveBetweenAlbums(oldAlbumId, newAlbumId) {

  var hidePrivate = false;
      $.ajax({
              type: 'GET',
              url: '/users/albums/get/'+oldAlbumId,
              contentType: 'application/json',
              success: (albumData) => {
                      mediaId = albumData[slideIndex-1]["mediaId"];
                      var token =  $('input[name="csrfToken"]').attr('value');
                      $.ajaxSetup({
                          beforeSend: function(xhr) {
                              xhr.setRequestHeader('Csrf-Token', token);
                          }
                          });
                          $.ajax({
                              url: "/users/albums/move_media/" + newAlbumId,
                              method: "PUT",
                              data: JSON.stringify({
                                  mediaIds: [mediaId]
                              }),
                              headers: {
                                  'Content-Type': 'application/json'
                              },
                              success:function(res, textStatus, xhr){
                                  if (xhr.status == 200) {
                                     window.location = oldAlbumId
                                  }
                              }
                          })
              }
      });


}

/**
 * Clones a node and removes all listeners from it, returns the clone
 * @param original the node to be cloned
 * @returns a copy of the same node without any listeners
 */
function replaceWithClone(original) {
    if (original != null) {
        const clone = original.cloneNode(true);
        original.parentNode.replaceChild(clone, original);
        return clone;
    }
}

/**
 * Sets listener for the delete button on the current slide
 * @param albumData the data of all media in the current album
 * @param i the index of the current slide
 */
function setDeletePhotoListener(albumData, i) {
    function deletePhotoListener() {
        mediaId = albumData[i]["mediaId"];
        selectedMediaID_GLOBAL = mediaId;
        openSelectDestinationsToUnlinkPhotoModal(mediaId);
    }
    const clone = replaceWithClone(document.getElementById('deletePhotoBtn'));
    if (clone != null) {
        clone.addEventListener('click', deletePhotoListener);
    }
}

/**
 * Sets listener for the profile picture button on the current slide
 * @param albumData the data of all media in the current album
 * @param i the index of the current slide
 */
function setMakeProfilePictureListener(albumData, i) {
    function makeProfilePictureListener() {
        mediaId = albumData[i]["mediaId"];
        setProfilePictureRequest(mediaId);
    }
    const original = document.getElementById('profilePictureBtn');
    const clone = replaceWithClone(original);
    if (clone != null) {
        clone.addEventListener('click', makeProfilePictureListener);
    }
}

/**
 * Sets listener for the destination link button on the current slide
 * @param albumData the data of all media in the current album
 * @param i the index of the current slide
 */
function setDestinationLinkListener(albumData, i) {
    function destinationLinkListener() {
        mediaId = albumData[i]["mediaId"];
        openDestinationModal(mediaId);
    }
    const original = document.getElementById('linkDestinationBtn');
    const clone = replaceWithClone(original);
    if (clone != null) {
        clone.addEventListener('click', destinationLinkListener)
    }
}

/**
 * Sets the listener for the make public/make private button on the current slide
 * @param setPrivacy true for setting the button to make public else false for setting to make private
 * @param mediaId the id of the media to set the button for
 */
function setPrivacyListener(setPrivacy, mediaId) {
    function privacyListener() {
        if(clone.innerText === 'Make Private') {
            setMediaPrivacy(mediaId, false, clone)
        } else {
            setMediaPrivacy(mediaId, true, clone)
        }
    }
    const original = document.getElementById('privacyBtn');
    const clone = replaceWithClone(original);
    clone.addEventListener('click', privacyListener);
}

/**
 * Sets listeners for all buttons on the current slide
 * @param i the index of the current slide
 */
function setSlideListeners(albumData, i) {
    const dataset = document.getElementById('myModal').dataset;
    const isOwner = dataset.isowner;
    const isAdmin = dataset.isadmin;

    let setPrivacy;
    mediaId = albumData[i]["mediaId"];
    const caption = albumData[i]["caption"];


    if (isOwner === "true" || isAdmin === "true") {
        setDeletePhotoListener(albumData, i);
        setDestinationLinkListener(albumData, i);
        setMakeProfilePictureListener(albumData, i);
        if (caption !== "") {
            document.querySelector('div[data-mediaId="'+mediaId+'"] [contenteditable]').innerText = caption.toString();
        } else {
            document.querySelector('div[data-mediaId="'+mediaId+'"] [contenteditable]').innerText =
            "Click to add caption.";
        }
        if(albumData[i]["isPublic"]) {setPrivacy=0;}
        else {setPrivacy=1;}
        setPrivacyListener(setPrivacy, mediaId);
    } else {
        if (caption !== "") {
            document.querySelector('div[data-mediaId="'+mediaId+'"] [data-editable]').innerText = caption.toString();
        } else {
            document.querySelector('div[data-mediaId="'+mediaId+'"] [data-editable]').innerText =
            "No Caption";
        }
    }
}

/**
 * Sets a photo privacy to the setting specified
 * @param mediaId the id of the media to change privacy
 * @param setPublic true to set to public, false to set to private
 */
function setMediaPrivacy(mediaId, setPublic, link) {
    const intPublic = setPublic ? 1 : 0;
    $.ajax({
        type: 'GET',
        url: '/users/home/photoPrivacy/' + mediaId + '/' + intPublic,
        contentType: 'application/json',
        success: () => {
            const privacyIcon = document.querySelector('i[data-privacyMediaId="'+mediaId+'"]');
            if (!setPublic) {
                link.innerText = "Make Public";
                document.querySelector('div[data-mediaId="'+mediaId+'"]').setAttribute("data-privacy", false.toString());
                privacyIcon.classList.remove("fa-eye-green");
                privacyIcon.classList.add("fa-eye-red");
            } else {
                link.innerText = "Make Private";
                document.querySelector('div[data-mediaId="'+mediaId+'"]').setAttribute("data-privacy", true.toString());
                privacyIcon.classList.remove("fa-eye-red");
                privacyIcon.classList.add("fa-eye-green");
            }
        }
    });
}



/**
 * Function to search for albums.
 * Updates the rows of photos with album titles matching the search term
 */
function getAlbum(userId, albumId){

    $.ajax({
        type: 'GET',
        url: '/users/albums/get/'+albumId+"/"+offSet+"/"+quantityToGet,
        contentType: 'application/json',
        success: (data) => {

            const mediaData = data.mediaData;
            const totalMediaCount = data.totalMediaCount;

            albumMediaData = albumMediaData.concat(mediaData);

            addAlbum(mediaData, userId, offSet);

            offSet += mediaData.length;

            const loadMoreBtn = document.getElementById('loadMoreBtn');
            if (offSet >= totalMediaCount) {
                loadMoreBtn.style.display = 'none';
            } else {
                loadMoreBtn.style.display = 'block';
            }
        }
    });
}


async function addAlbum(albumData, userId, offSet) {

    var path = "/users/home/servePicture/";
    for (let i=0; i<albumData.length; i++) {
        if(!(albumData[i]["user"]["userid"] !== userId && albumData[i]["isPublic"] === false)) {
            await displayGrid(i, albumData, path, offSet);
            await displaySlides(i, albumData, path);
        }
    }
}

/** Called when displaying the grid of photos */
async function displayGrid(i, albumData, path, offSet) {
    let url = albumData[i]["urlWithPath"];
    let imgContainer = document.createElement("div");
    imgContainer.classList.add("container");
    imgContainer.setAttribute("id", "imgContainer");
    let overlay = document.createElement("div");
    overlay.classList.add("overlay");
    let icon = document.createElement("i");
    icon.classList.add("icon");
    let privacyIcon = document.createElement("i");
    privacyIcon.setAttribute("data-privacyMediaId", albumData[i]["mediaId"]);
    if (albumData[i]["isPublic"]) {
        privacyIcon.classList.add("fa", "fa-eye-green");
    } else {
        privacyIcon.classList.add("fa", "fa-eye-red");
    }
    let img1 = document.createElement("img");
    console.log(url);
    img1.src = path + encodeURIComponent(url);
    img1.setAttribute("data-id", i);
    img1.setAttribute("data-mediaId", albumData[i]["mediaId"]);
    img1.classList.add("hover-shadow");
    img1.addEventListener('click', () => {
        openModal();
        currentSlide(i+1+offSet);
        setSlideListeners(albumData, i);
    });
    icon.appendChild(privacyIcon);
    overlay.appendChild(icon);
    imgContainer.appendChild(overlay);
    imgContainer.appendChild(img1);
    if (i%4==0) {
        document.getElementById('col1').appendChild(imgContainer);
    } else if (i%4==1){
        document.getElementById('col2').appendChild(imgContainer);
    } else if (i%4==2){
        document.getElementById('col3').appendChild(imgContainer);
    } else if (i%4==3){
        document.getElementById('col4').appendChild(imgContainer);
    }
}

async function displaySlides(i, albumData, path) {
    var url = albumData[i]["urlWithPath"];
    mediaId = albumData[i]["mediaId"];
    var lightBox = document.getElementById("lightbox-modal");
    var mySlidesDiv = document.createElement("div");
    var captionInput = document.createElement("p");
    const dataSet = document.getElementById('myModal').dataset;
    const isOwner = dataSet.isowner;
    const isAdmin = dataSet.isadmin;

    captionInput.setAttribute("id", "img-caption");
    captionInput.setAttribute("captionMediaId", mediaId);
    if (isOwner === "true" || isAdmin === "true") {
        captionInput.setAttribute("contenteditable", "true");
    } else {
        captionInput.setAttribute("data-editable", "false");
    }
    captionInput.setAttribute("style", "color: white;");

    mySlidesDiv.classList.add("mySlides");
    mySlidesDiv.setAttribute("data-privacy", albumData[i]["isPublic"]);
    mySlidesDiv.setAttribute("data-mediaId", mediaId);

    var img1 = document.createElement("img");
    img1.setAttribute("id", "img"+(i+1));
    img1.classList.add("center-block");
    img1.src = path + encodeURIComponent(url);
    var figure = document.createElement("figure");
    figure.appendChild(img1);
    var figureCaption = document.createElement("figcaption");
    figureCaption.appendChild(captionInput);
    figure.appendChild(figureCaption);
    mySlidesDiv.appendChild(figure);

    lightBox.appendChild(mySlidesDiv);
    if (isOwner === "true" || isAdmin === "true") {
        var content = document.querySelector('div[data-mediaId="'+mediaId+'"] [contenteditable]');
        // 1. Listen for changes of the content editable element
        content.addEventListener('focus', function (event) {
            const el = event.target,
                input = el.nodeName != 'INPUT',
                data = {};
                el.value = "";

            // This selects the caption text when clicked to edit
            selection = window.getSelection();
            range = document.createRange();
            range.selectNodeContents(el);
            selection.removeAllRanges();
            selection.addRange(range);
            //

            if (input) {
                content.onblur = function (event) {
                    if (el.innerText != "Click to add caption.")
                    data[el.getAttribute('data-name')] = el.innerText;
                    // we could send an ajax request to update the field
                    submitEditCaption(content.innerText, mediaId);
                    el.blur();
                    event.preventDefault();
                }
            }
        });
    }

}

// Open the Modal
function showDeleteAlbumModal(isDefault) {
    if (isDefault === false) {
        $(document.getElementById('confirmDeleteAlbumModal')).modal('show')
    } else {
        $(document.getElementById('defaultAlbum')).modal('show')
    }
}

function okDefault(album) {
    $(document.getElementById('defaultAlbum')).modal('hide')
}

// Open the Modal
function deleteAlbum(albumId) {
    var url = '/users/albums/delete/' + albumId;
    var token = $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function (xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
        url: url,
        method: "Delete",
        headers: {
            'Content-Type': 'application/json'
        },
        success: function() {
            window.location = '/users/albums'
        }
    });
}


// Open the Modal
function openModal() {
    document.getElementById("myModal").style.display = "block";
}

// Close the Modal
function closeModal() {
    document.getElementById("myModal").style.display = "none";
}


/**
 * Opens the modal to link and unlink destinations to the media supplied
 * @param mediaId the id of the media supplied
 */
function openDestinationModal(mediaId) {
    document.getElementById('destination-modal').style.display = "block";
    getDestData(mediaId, 1, 20);
}

/**
 * Closes the destination linking modal
 */
function closeDestinationModal() {
    document.getElementById('destination-modal').style.display = 'none';
}

/**
 * Gets the data of all destinations the user can see to populate the destination linking modal and then loads it
 * @param mediaId the id of the media to load destination data for
 */
function getDestData(mediaId, pageNum, quantity) {
    const offset = (pageNum - 1) * 20;
    let data = {offset: offset,
        quantity: quantity};
    $.ajax({
        type: 'GET',
        url: "/users/destinations/getallpaginatedjson",
        data: data,
        contentType: 'application/json',
        success: (destData) => {
            loadDestTable(destData, mediaId, pageNum, null)
        }
    });
}

/**
 * Adds destination rows to the destination linking table
 * @param destData the data containing all destinations that the user can view
 * @param mediaId the id of the media to link destinations to
 */
function loadDestTable(destData, mediaId, pageNum, search) {
    let count = destData.totalCountPublic;
    const publicTable = document.getElementById('public-dest-tbody');
    const privateTable = document.getElementById('private-dest-tbody');
    if (document.getElementById('paginationEl') != null) {
        document.getElementById('paginationEl').remove();
    }
    while (publicTable.childNodes.length > 0) {
        publicTable.removeChild(publicTable.childNodes[0]);
    }
    while (privateTable.childNodes.length > 0) {
        privateTable.removeChild(privateTable.childNodes[0]);
    }
    for (let destination of destData.destinations) {
        if (destination.isPublic) {
            addDestRow(publicTable, destination, mediaId);
        } else {
            addDestRow(privateTable, destination, mediaId)
        }
    }
    addPagination(mediaId, count, pageNum ,search);

}

function searchByKeyword(mediaId, currentPageNum) {
    const offset = (currentPageNum - 1) * 20;
    let searchInput = document.getElementById("destSearchInput").value;
    if(searchInput != "") {
        let data = {offset: offset,
            quantity: 20};
        $.ajax({
            url: '/users/destinations/matching/' + searchInput,
            data: data,
            method: "GET",
            success: function (res) {
                let displayedIds = [];
                for (let j=0; j < res.destinations.length; j++) {
                    if(res.destinations[j].isPublic === false) {
                        displayedIds.push("destButton" + res.destinations[j].destId);
                    }
                }
                let privateListChildren = document.getElementById("private-dest-tbody").children;

                for(let i=0; i < privateListChildren.length; i++) {
                    if (!displayedIds.includes(privateListChildren[i].id)) {
                        privateListChildren[i].setAttribute("style", "display: none;");
                    } else {
                        privateListChildren[i].setAttribute("style", "display: block;");
                    }
                }
                loadDestTable(res, mediaId, currentPageNum, searchInput)

            }
        });
    } else {
        getDestData(mediaId, 1, 20);
        let privateListChildren = document.getElementById("private-dest-tbody").children;
        for(let i=0; i < privateListChildren.length; i++) {
            privateListChildren[i].setAttribute("style", "display: block;");
        }
    }
}

/**
 * Adds a row containing one destination to the linking table
 * @param table the table to add the destination row to
 * @param destination the destination to add as a row
 * @param mediaId the id of the media that the table belongs to
 */
function addDestRow(table, destination, mediaId) {
    const row = document.createElement("TR");
    const name = document.createElement("TH");
    name.setAttribute('scope', 'row');
    name.innerText = destination.destName;
    row.appendChild(name);

    const type = document.createElement("TD");
    type.innerText = destination.destType;
    row.appendChild(type);

    const country = document.createElement("TD");
    country.innerText = destination.country;
    row.appendChild(country);

    const district = document.createElement("TD");
    district.innerText = destination.district;
    row.appendChild(district);

    const linkButton = document.createElement('BUTTON');
    linkButton.setAttribute('id', `link-${destination.destId}`);
    linkButton.setAttribute('class', 'btn btn-primary');
    linkButton.innerText = 'Link to destination';
    linkButton.addEventListener('click', () => {
        linkDestination(destination.destId, mediaId)
    });

    const unlinkButton = document.createElement('BUTTON');
    unlinkButton.setAttribute('id', `unlink-${destination.destId}`);
    unlinkButton.setAttribute('class', 'btn btn-danger');
    unlinkButton.innerText = 'Unlink from destination';
    unlinkButton.style.display = 'none';
    unlinkButton.addEventListener('click', () => {
        unlinkDestination(destination.destId, mediaId)
    });

    checkButtonStatus(mediaId, destination.destId);

    const div = document.createElement('DIV');
    div.appendChild(linkButton);
    div.appendChild(unlinkButton);
    row.appendChild(div);

    table.appendChild(row);
}

$("#destSearchInput").keyup(function ()
{
    searchByKeyword(mediaId, 1)
});

function addPagination(mediaId, count, pageNum, search, tab) {
    let numOfPages = [];
    let pageNumbers = [];
    const pagination = document.createElement("ul");
    pagination.setAttribute("id", "paginationEl")
    pagination.classList.add("pagination");
    for (let i = 0; i < count; i += 20) {
        numOfPages.push((i / 20) + 1);
    }
    if (numOfPages.length > 10) {
        if (pageNum > 5) {
            if (numOfPages.length >= pageNum + 5) {
                pageNumbers = [pageNum - 3, pageNum - 2, pageNum - 1, pageNum, pageNum + 1, pageNum + 2, pageNum + 3, pageNum + 4];
            } else {
                let lastPage = numOfPages.length - 0;
                pageNumbers = []
                for (let j = lastPage - 7; (j < lastPage + 1 && j > 0); j++) {
                    pageNumbers.push(j);
                }
            }
        } else {
            for (let k = 0; k < 10; k++) {
                pageNumbers.push(numOfPages[k]);
            }
        }
    } else {
        pageNumbers = numOfPages;
    }
    let item = document.createElement("li");
    let pageButton = document.createElement("a");
    let currentPageNum = 1;
    pageButton.innerText = "First";
    if (search == null) {
        pageButton.setAttribute("onClick", `getDestData(${mediaId}, ${currentPageNum} ,${20})`);
    } else {
        pageButton.setAttribute("onClick", `searchByKeyword(${mediaId}, ${currentPageNum})`);

    }
    item.appendChild(pageButton);
    pagination.appendChild(item);

    item = document.createElement("li");
    pageButton = document.createElement("a");
    if (pageNum < 2) {
        currentPageNum = 1;
    } else {
        currentPageNum = pageNum - 1;
    }
    pageButton.innerText = "<";
    if (search == null) {
        pageButton.setAttribute("onClick", `getDestData(${mediaId}, ${currentPageNum} ,${20})`);
    } else {
        pageButton.setAttribute("onClick", `searchByKeyword(${mediaId}, ${currentPageNum})`);

    }
    item.appendChild(pageButton);
    pagination.appendChild(item);
    for (let i=0; i < pageNumbers.length; i++) {
        let item = document.createElement("li");
        const pageButton = document.createElement("a");
        const currentPageNum = pageNumbers[i];
        pageButton.innerText = pageNumbers[i];
        if (currentPageNum==pageNum) {
            item.classList.add("active");
        }
        if (search == null) {
            pageButton.setAttribute("onClick", `getDestData(${mediaId}, ${currentPageNum} ,${20})`);
        } else {
            pageButton.setAttribute("onClick", `searchByKeyword(${mediaId}, ${currentPageNum})`);

        }
        item.appendChild(pageButton);
        pagination.appendChild(item);
    }
    item = document.createElement("li");
    pageButton = document.createElement("a");
    if (pageNum>=numOfPages.length) {
        currentPageNum = numOfPages.length;
    } else {
        currentPageNum = pageNum+1;
    }
    pageButton.innerText = ">";
    if (search == null) {
        pageButton.setAttribute("onClick", `getDestData(${mediaId}, ${currentPageNum} ,${20})`);
    } else {
        pageButton.setAttribute("onClick", `searchByKeyword(${mediaId}, ${currentPageNum})`);

    }
    item.appendChild(pageButton);
    pagination.appendChild(item);
    document.getElementById("publicDestinations").appendChild(pagination);

    item = document.createElement("li");
    pageButton = document.createElement("a");
    currentPageNum = numOfPages.length;
    pageButton.innerText = "Last";
    if (search == null) {
        pageButton.setAttribute("onClick", `getDestData(${mediaId}, ${currentPageNum} ,${20})`);
    } else {
        pageButton.setAttribute("onClick", `searchByKeyword(${mediaId}, ${currentPageNum})`);

    }
    item.appendChild(pageButton);
    pagination.appendChild(item);
    if(numOfPages < 1) {
        pagination.remove()
    }
}

/**
 * Shows only the necessary linking or unlinking button for a row on the destination linking table
 * @param mediaId the id of the media to check for
 * @param destId the id of the destination row on the table to check
 */
function checkButtonStatus(mediaId, destId) {
    $.ajax({
        method: "GET",
        url: `/users/destinations/photos/${destId}`,
        headers: {
            'Content-Type': 'application/json'
        },
        success: function(photos) {
            for (let photo of photos) {
                if (mediaId === photo.mediaId) {
                    toggleButtons(destId);
                    return;
                }
            }
        }
    });
}

/**
 * Links a media item to a destination
 * @param destId the id of the destination to link
 * @param mediaId the id of the media to link
 */
function linkDestination(destId, mediaId) {
    $.ajax({
        method: "PUT",
        url: `/users/destinations/${destId}`,
        data: JSON.stringify({
            photoid: '"' + mediaId + '"'
        }),
        headers: {
            'Content-Type': 'application/json'
        },
        success: function() {
            toggleButtons(destId)
        }
    });
}

/**
 * Links a media item to a destination
 * @param destId the id of the destination to link
 * @param mediaId the id of the media item to link
 */
function unlinkDestination(destId, mediaId) {
    $.ajax({
        type: 'DELETE',
        url: `/users/destinations/${mediaId}/${destId}`,
        contentType: 'application/json',
        success: () => {
            toggleButtons(destId)
        }
    });
}

/**
 * Toggles buttons on a destination row of the linking table between linking and unlinking
 * @param destId the id of the destination row
 */
function toggleButtons(destId) {
    const unlink = document.getElementById(`unlink-${destId}`);
    unlink.style.display === "none" ? unlink.style.display = "block" : unlink.style.display = "none";

    const link = document.getElementById(`link-${destId}`);
    link.style.display === 'none' ? link.style.display = 'block' : link.style.display = 'none';
}

// Next/previous controls
function plusSlides(n) {
    showSlides(slideIndex += n);
}

// Thumbnail image controls
function currentSlide(n) {
    showSlides(slideIndex = n);
}


/** Called when displaying a photo modal */
function showSlides(n) {
    
    let i;

    let slides = document.getElementsByClassName("mySlides");

    if (n > slides.length) {slideIndex = 1}
    if (n < 1) {slideIndex = slides.length}
    for (i = 0; i < slides.length; i++) {
        slides[i].style.display = "none";
    }

    if (typeof changeTaggableModel === "function") { //False when viewing users' photos as no tag editor
        changeTaggableModel(slides[slideIndex - 1].dataset.mediaid, "photo");
    }

    if(slides[slideIndex-1] !== undefined) {

        let mediaId = slides[slideIndex - 1].dataset.mediaid;

        $.ajax({
            type: 'GET',
            url: '/users/photos/'+ mediaId +'/caption',
            headers: {
                'Content-Type': 'application/json'
            },
            success:function (caption) {

                if (caption !== "") {
                    document.querySelector("[captionmediaid='" + mediaId + "']").innerText = caption;
                }
            },
            error: function(xhr, textStatus, errorThrown){
                console.log(xhr.status + " " + textStatus + " " + errorThrown);
            }
        });


        slides[slideIndex-1].style.display = "inline-block";
        const privacyBtn = document.getElementById("privacyBtn");
        if (privacyBtn != null) {
            if (slides[slideIndex - 1].getAttribute("data-privacy") === "true") {
                document.getElementById("privacyBtn").innerText = "Make Private";
            } else if (slides[slideIndex - 1].getAttribute("data-privacy") === "false") {
                document.getElementById("privacyBtn").innerText = "Make Public";
            }
        }
        setSlideListeners(albumMediaData, slideIndex-1);
    }
}



/**
 * New set profile picture from existing picture function. Makes use of the cropper.
 * @param photoId
 */
function setProfilePictureRequest(photoId){
    $("#myModal").modal('hide');
    photoIdToEdit = photoId;
    $('#addProfilePhoto').modal('show');
}


/**
 *
 */
$('#addProfilePhoto').on('show.bs.modal', function (e) {
    $("#myModal").hide();
    var output = document.getElementById('change-profile-pic');
    output.src = "/users/home/serveDestPicture/" + photoIdToEdit;
    $("#selectProfileInput").hide();
});

/**
 *
 */
$('#addProfilePhoto').on('shown.bs.modal', function (e) {
    $.ajax({
        type: 'GET',
        url: '/users/photos/' + photoIdToEdit,
        success: function(data){
            filename = data["url"];
            $('#change-profile-pic').cropper("destroy");

            var $previews = $('.preview');
            $('#change-profile-pic').cropper({
                movable: false,
                autoCropArea: 1,
                aspectRatio: 1,
                ready: function(e){

                    //DO NOT DELETE THIS SET TIMEOUT
                    // setTimeout(function(){
                    //     $('#change-profile-pic').cropper('crop');
                    //     croppedCanvas = $('#change-profile-pic').cropper('getCroppedCanvas');
                    // }, 1);

                    let cropBoxElements = document.getElementsByClassName('cropper-face cropper-move');
                    let cropBoxElement = cropBoxElements[0];
                    let cropBoxMoveEvent = new Event('crop');
                    cropBoxElement.dispatchEvent(cropBoxMoveEvent);

                },
                crop: function (e) {
                    var imageData = $(this).cropper('getImageData');
                    croppedCanvas = $(this).cropper('getCroppedCanvas');
                    $('.preview').html('<img src="' + croppedCanvas.toDataURL() + '" class="thumb-lg img-circle" style="width:100px;height:100px;">');
                    var previewAspectRatio = e.width / e.height;
                    $previews.each(function (){
                        var $preview = $(this);
                        var previewWidth = $preview.width();
                        var previewHeight = previewWidth / previewAspectRatio;
                        var imageScaledRatio = e.width / previewWidth;
                        $preview.height(previewHeight).find('img').css({
                            width: imageData.naturalWidth / imageScaledRatio,
                            height: imageData.naturalHeight / imageScaledRatio,
                            marginLeft: -e.x / imageScaledRatio,
                            marginTop: -e.y / imageScaledRatio
                        });
                    });
                }
            });
        }
    });
});


/**
 * This function is called when the user clicks the upload button to upload the cropped canvas image to the database.
 * Sends an AJAX post request to the backend with the photo's information to store the photo within the database.
 * The cropped image will be used as the user's profile picture.
 */
$('#save-profile').click(function (eve){


    /*
        WARNING
        WARNING
        WARNING
        WARNING

        This solves the bug where the crop box needs to move a little before the image can
        be successfully uploaded.

        We do not know why this works.

        Please do not touch.
     */
    let cropper = $("#change-profile-pic").data('cropper');
    cropper.scale(1);
    /*
        WARNING END
        WARNING END
        WARNING END
        WARNING END
     */



    eve.preventDefault();
    var formData = new FormData();
    croppedCanvas.toBlob(function (blob) {
        formData.append('picture', blob, filename);
        formData.append('album', "Profile Pictures");
        var token = $('input[name="csrfToken"]').attr('value');
        $.ajaxSetup({
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Csrf-Token', token);
            }
        });
        $.ajax({
            type: 'POST',
            processData: false,
            contentType: false,
            url: '/users/home/profilePicture',
            data: formData,
            success: function (data, textStatus, xhr) {
                if (xhr.status == 200) {
                    $("#selectProfileInput").show();
                    window.location = '/users/home'
                } else {
                    window.location = '/users/home'
                }
            }
        })
    });
});

function showMessage(message, action) {
    const x = document.getElementById("snackbar");

    // Add the "show" class to DIV
    x.className = "show";
    x.innerText = message + action;
    // After 3 seconds, remove the show class from DIV
    setTimeout(function(){
        x.className = x.className.replace("show", "");
    }, 3000);
}

function deletePhotoFromUI(mediaId) {
    var slides = document.getElementsByClassName("mySlides");
    var size = slides.length;
    for (var i=0; i < slides.length; i++) {
        if (slides[i].getAttribute('data-mediaId') == mediaId) {
            slides[i].remove();
            document.querySelectorAll("img[data-mediaId='" + mediaId + "']")[0].remove();
            var wellStyle = document.getElementById('emptyAlbumMessage').getAttribute("style");
            wellStyle += "display: none;"
            document.getElementById('emptyAlbumMessage').setAttribute("style", wellStyle);
            size-=1;
        }
    }
    if (size > 0) {plusSlides(1);}
    else
    {
        closeModal();
        var wellStyle = document.getElementById('emptyAlbumMessage').getAttribute("style");
        wellStyle += "display: block;"
        document.getElementById('emptyAlbumMessage').setAttribute("style", wellStyle);
    }
    showMessage("Deleted the photo successfully!", "");
}

/**
 * This function makes a request to the server to delete a photo with the passed id.
 * @param url
 * @param photoid
 * @param imageId
 */
function deletePhotoRequest(photoId){
    var url = '/users/home/deletePicture/' + photoId + '/false';
    var token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    $.ajax({
        url: url,
        method: "Delete",
        data: JSON.stringify({
            photoid: '"' + photoId + '"',
            response: false
        }),
        headers: {
            'Content-Type': 'application/json'
        },
        success: function(res){
            deletePhotoFromUI(photoId);
        },
        error: function(res){
            if(res.responseText === "Is profile picture ask user"){
                $(document.getElementById('myModal')).modal('hide');
                $(document.getElementById('confirmDeleteProfilePhotoModal')).modal('show');

                document.getElementById('yesDeleteProfilePhoto').onclick =
                    function(){
                        $.ajax({
                            url: '/users/unlinkAndDeletePicture/'+photoId,
                            method: "Delete",
                            success:function(res) {
                                deletePhotoFromUI(photoId);
                            },
                            error:function(res){
                                console.log(res.responseText);
                            }
                        });
                        $(document.getElementById('myModal')).modal('show')
                    };
                document.getElementById('noCloseDeleteProfilePhotoButton').onclick =
                    function(){
                        $(document.getElementById('myModal')).modal('show')
                    };

                $('#confirmDeletePhotoModal').on('hidden.bs.modal', function () {
                    $(document.getElementById('myModal')).modal('show');
                })

            }
            else if(res.responseText === "Failed to delete image"){
                $(document.getElementById('myModal')).modal('hide');
                $(document.getElementById('confirmDeletePhotoModal')).modal('show');

                document.getElementById('yesDeletePhoto').onclick =
                    function(){
                        $.ajax({
                            url: '/users/unlinkAndDeletePicture/'+photoId,
                            method: "Delete",
                            success: function (res) {
                                deletePhotoFromUI(photoId);
                            },
                            error: function (res) {
                                console.log(JSON.stringify(res));
                            }
                        });
                        $(document.getElementById('myModal')).modal('show')
                    };
                document.getElementById('noCloseDeletePhotoButton').onclick =
                    function(){
                        $(document.getElementById('myModal')).modal('show')
                    };

                $('#confirmDeletePhotoModal').on('hidden.bs.modal', function () {
                    $(document.getElementById('myModal')).modal('show');
                })

            }
        }

    })
}

/**
 * Sends a request to change the photo caption and toggles appropriate displays
 * @param caption the new caption
 * @param photoId the id of the photo to change the caption of
 */
function submitEditCaption(caption, photoId) {

    $.ajax({
        type: 'PUT',
        url: '/users/photos/'+ photoId +'/caption',
        data: JSON.stringify({
            caption: caption
        }),
        headers: {
            'Content-Type': 'application/json'
        },
        success:function () {
            document.getElementById('undoButton').classList.remove('disabled');
        },
        error: function(xhr, textStatus, errorThrown){
            console.log(xhr.status + " " + textStatus + " " + errorThrown);
        }
    });
}

/**
 * Opens the modal to select which destinations the user photo should remain in
 * after the photo is selected from the users albumn.
 * @param mediaId the id of the media supplied
 */
function openSelectDestinationsToUnlinkPhotoModal(mediaId) {
    $.ajax({
        type: 'GET',
        url: '/users/albums/photos/get_linked_destinations/' + mediaId,
        success:function(res){
            destinationsToUnlink_GLOBAL = res;

            resetSelectDestinationsToUnlinkPhotoModal();
            setDestinationSelectionsForBulkPhotoLeaving();
            $('#selectDestinationsToUnlinkPhotoModal').modal('show');
        },
        error: function(xhr, textStatus, errorThrown){
            console.log(xhr.status + " " + textStatus + " " + errorThrown);
            showImageUnlinkalert();
        }
    });


}

/**
 * Adds all destinations to the modal where the user selects which Destinations
 * to keep the picture in, when deleting pic in user album.
 */
function setDestinationSelectionsForBulkPhotoLeaving() {
    const modalBody = document.querySelector(
        '#selectDestinationsToUnlinkPhotoModal ' +
        '.modal-dialog .modal-content .modal-body');

    const paragraph = document.createElement("p");
    paragraph.innerText = "Select which destinations to remove the photo from:";
    modalBody.appendChild(paragraph);

    for (let destination of destinationsToUnlink_GLOBAL) {
        const checkbox_id = 'destination-unlink-id-' + destination.destid;

        const modalDiv = document.createElement('div');
        modalDiv.classList.add('form-check');

        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.classList.add('form-check-input');
        checkbox.classList.add('destination-delete-photo-checkbox');
        checkbox.id = checkbox_id;
        checkbox.setAttribute('destinationId', destination.destid);
        modalDiv.appendChild(checkbox);

        const label = document.createElement('label');
        label.classList.add('form-check-label');
        label.htmlFor = checkbox_id;
        label.innerText = destination.destName;
        modalDiv.appendChild(label);

        modalBody.appendChild(modalDiv);
    }
}

/**
 * Resets the body of the modal which shows destinations to keep a user photo in.
 */
function resetSelectDestinationsToUnlinkPhotoModal() {
    const modalBody = document.querySelector(
        '#selectDestinationsToUnlinkPhotoModal ' +
        '.modal-dialog .modal-content .modal-body');

    while (modalBody.firstChild) {
        modalBody.removeChild(modalBody.firstChild);
    }

}

/**
 * Closes the selectDestinationsToUnlinkPhoto modal
 */
function closeSelectDestinationsToUnlinkPhotoModal() {
    $('#selectDestinationsToUnlinkPhotoModal').modal('hide');
}

/**
 * Opens the alert to show an action failed while deleting a photo.
 */
function showImageUnlinkalert() {
    const alert = document.querySelector("#imageModalAlert");
    alert.classList.remove('hiddenDiv');
    alert.scrollIntoView();
}

/**
 * Bulk checks or unchecks all checkboxes for removing a user photo from a destination
 * media album.
 *
 * @param isSelected Boolean, true if setting all checkboxes to checked.
 */
function setAllCheckboxDestinationsToDeletePhotoIn(isSelected) {
    const checkboxs = document.querySelectorAll(
                                    ".destination-delete-photo-checkbox");
    for (let checkbox of checkboxs) {
        checkbox.checked = isSelected;
    }
}

/**
 * Method to send POST request to delete a user photo and unlink it from selected
 * destinations.
 */
function deleteAndUnlinkPhoto() {
    const checkboxs = document.querySelectorAll(
                        ".destination-delete-photo-checkbox");

    const deleteReqestJSON = {
        'mediaId': selectedMediaID_GLOBAL,
        'destinationsToUnlink': []
    };
    for (let checkbox of checkboxs) {
        if (checkbox.checked) {
            const destId = parseInt(checkbox.getAttribute('destinationId'));
            deleteReqestJSON['destinationsToUnlink'].push(destId);
        }
    }


    $.ajax({
        type: 'DELETE',
        url: '/users/albums/delete/photo_and_unlink_selected_destinations',
        contentType: 'application/json',
        data: JSON.stringify(deleteReqestJSON),
        success: () => {
            location.reload();
        },
        error: function(xhr, textStatus, errorThrown) {
            showImageUnlinkalert();
        }
    });
}

/* When the user clicks on the button,
toggle between hiding and showing the dropdown content */
function openDropdown() {
    document.getElementById("optionsDropdown").classList.toggle("show");
}


// Close the dropdown menu if the user clicks outside of it
window.onclick = function(event) {
    if (!event.target.matches('.dropbtn')) {
        var openDropdown = document.getElementsByClassName("optionsDropdown");
        if (openDropdown.classList !== undefined) {
            if (openDropdown.classList.contains('show')) {
                openDropdown.classList.remove('show');
            }
        }
    }

};

$('#photo-upload').click(function (eve){
    let searchBar = document.getElementById("album-search-photo");
    let albumId = searchBar.getAttribute("data-albumId");
    let privateInput = document.getElementById("private").checked;
    let filePath = document.getElementById("photoUpload");
    if (searchBar.value === null || searchBar.value === "") {
        document.getElementById("photoAlbumMessage").style.display = "block";
    } else {
        var formData = new FormData();
        formData.append('picture', filePath.files[0]);
        formData.append('private', privateInput);
        formData.append('album', searchBar.value);
        var token = $('input[name="csrfToken"]').attr('value');
        $.ajaxSetup({
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Csrf-Token', token);
            }
        });
        $.ajax({
            type: 'POST',
            processData: false,
            contentType: false,
            url: '/users/home/photo',
            data: formData,
            success: function (data, textStatus, xhr) {
                if (xhr.status == 200) {
                    document.getElementById('undoButton').classList.remove('disabled');
                } else {
                    document.getElementById('undoButton').classList.remove('disabled');
                }
            }
        })
    }
});
