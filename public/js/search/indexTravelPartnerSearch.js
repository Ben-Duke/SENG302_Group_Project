let userCount;
const usersPerPage = 10;
let currentPageNum;
let filters;
init();

/**
 * Initialise function called when the page is loaded
 */
async function init() {
    await initNationalities();
    initDates();
    searchUsers();
    searchFollowing();
}

/**
 * Initialises the nationalities by filling the nationality select form
 */
async function initNationalities() {
    let nationalitySelectBox = document.getElementById('travel-partner-nationality-filter');

    //Default nationality of none
    let opt = document.createElement('option');
    opt.appendChild(document.createTextNode('Any nationality'));
    opt.value = "null";
    nationalitySelectBox.appendChild(opt);

    let nationalities = await getNationalities();
    for (let nationality of nationalities) {
        let opt = document.createElement('option');
        opt.appendChild(document.createTextNode(nationality));
        opt.value = nationality;
        nationalitySelectBox.appendChild(opt);
    }

}

/**
 * Gets and returns a list of nationalities from the database
 */
async function getNationalities() {
    const token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    return $.ajax({
        type: 'GET',
        url: '/nationalities',
        contentType: 'application/json',
        success: (res) => {
            return res;
        }
    });
}

/**
 * Called when the user clicks on "Search travellers" and on initialises.
 * Updates the filters that the user selected then searches the database for users
 * based on the filters.
 */
async function searchUsers() {
    updateFilters();
    let count = await getUserCount();
    userCount = count;
    currentPageNum = 1;
    await renderPaginatedData();
}

async function searchFollowing() {
    updateFilters();
    let count = await getUserCount('following');
    userCount = count;
    currentPageNum = 1;
    await renderPaginatedData('following');
}



/**
 * Updates the filters based on what the user has selected
 */
function updateFilters() {
    filters = {
        male: document.getElementById("travel-partner-male-filter").checked,
        female: document.getElementById("travel-partner-female-filter").checked,
        other: document.getElementById("travel-partner-other-filter").checked,
        nationality: document.getElementById("travel-partner-nationality-filter").value,
        bornAfter: document.getElementById("travel-partner-born-after-filter").value,
        bornBefore: document.getElementById("travel-partner-born-before-filter").value,
    };
}

/**
 * Adds query parameters to the given route based on the filters.
 * The updated route is used to query the database.
 * @param route the base route without any query parameters
 */
function appendQueryParameters(route) {
    if(filters["male"] === true) {
        route += "&gender1=male"
    }
    if(filters["female"] === true) {
        route += "&gender2=female"
    }
    if(filters["other"] === true) {
        route += "&gender3=other"
    }
    if(filters["nationality"] !== "null") {
        route += `&nationality=${filters["nationality"]}`
    }
    if(filters["bornAfter"] !== "") {
        route += `&bornafter=${filters["bornAfter"]}`
    }
    if(filters["bornBefore"] !== "") {
        route += `&bornbefore=${filters["bornBefore"]}`
    }
    return route;
}

/**
 * Called when the user clicks on a pagination button to switch pages.
 */
async function onPaginate(currentPage, type) {
    currentPageNum = currentPage;

    await renderPaginatedData(type);
}

/**
 * Renders the new table elements based on pagination
 */
async function renderPaginatedData(type) {
    await renderUserData(currentPageNum, usersPerPage, type);

    addPagination(userCount, currentPageNum, type);
}

/**
 * Gets the total count of users in the database based on query parameters
 */
async function getUserCount(type) {

    const token =  $('input[name="csrfToken"]').attr('value');
    const userCountRoute = "/users/profile/searchprofiles/count?";
    let filteredRoute = appendQueryParameters(userCountRoute);

    if (type === 'following') {
        filteredRoute += '&getfollowing=true';
    }

    console.log(filteredRoute);

    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    return $.ajax({
        type: 'GET',
        url: filteredRoute,
        contentType: 'application/json',
        success: (res) => {
            return res;
        }
    });
}

/**
 * Retrieves all users from the database that fits the filters, then displays the data
 * @param pageNum the page number to retrieve
 * @param quantity the quantity of users to retrieve
 */
async function renderUserData(pageNum, quantity, type) {
    console.log(type)

    const offset = (pageNum - 1) * quantity;
    const searchTravelPartnerRoute = "/users/profile/searchprofiles?";
    let filteredRoute = appendQueryParameters(searchTravelPartnerRoute);

    if (type == 'following') {
        filteredRoute += '&getfollowing=true';
    }

    filteredRoute += `&offset=${offset}&quantity=${10}`;

    const token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });

    const users = await $.ajax({
        type: 'GET',
        url: filteredRoute,
        contentType: 'application/json',
    });
    console.log(users)

    await displayData(users, type);
}

/**
 * Displays the user data retrieved from the database as rows on a table
 * Each row in the table represents the user.
 * Displays the following attributes: Name, gender, nationalities, date of birth,
 * traveller types
 * @param users the user data retrieved from the database
 */
async function displayData(users, type) {

    let tableBody;

    if (type === 'following') {
        tableBody = document.getElementById('following-table-body');

    } else if (type === 'follower') {

    } else {
        tableBody = document.getElementById('travel-partner-search-table-body');
    }


    while (tableBody.childNodes.length > 0) {
        tableBody.childNodes[0].remove();
    }

    for (let user of users) {
        const row = document.createElement("tr");

        const name = document.createElement("th");
        name.innerText = user.name;
        row.appendChild(name);

        const gender = document.createElement("td");
        gender.innerText = user.gender;
        row.appendChild(gender);

        const nationalities = document.createElement("td");
        nationalities.innerText = user.nationalities;
        row.appendChild(nationalities);

        const dob = document.createElement("td");
        dob.innerText = user.dob;
        row.appendChild(dob);

        const travellerTypes = document.createElement("td");
        travellerTypes.innerText = user.travellerTypes;
        row.appendChild(travellerTypes);


            const followLink = document.createElement("td");
            followLink.setAttribute("class", "followLink");
            followLink.setAttribute("data-profile", user.userId);

            const viewUser = document.createElement("td");
            const viewUserBtn = document.createElement("a");
            viewUserBtn.setAttribute("class", "btn");
            viewUserBtn.setAttribute("class", "btn-link");
            viewUserBtn.href = "/users/profile/"+user.userId;
            viewUserBtn.innerText = "View";

            viewUser.appendChild(viewUserBtn);

            const followUserBtn = document.createElement("a");
            followUserBtn.setAttribute("class", "btn");
            followUserBtn.setAttribute("class", "btn-link");
            followUserBtn.id = "follow-"+user.userId;
            followUserBtn.setAttribute("onclick", "followUser("+user.userId+")");
            followUserBtn.innerText = "Follow";
            followUserBtn.style.cursor = "pointer";


            const unfollowUserBtn = document.createElement("a");
            unfollowUserBtn.setAttribute("class", "btn");
            unfollowUserBtn.setAttribute("class", "btn-link");
            unfollowUserBtn.id = "unfollow-"+user.userId;
            unfollowUserBtn.setAttribute("onclick", "unfollowUser("+user.userId+")");
            unfollowUserBtn.innerText = "Unfollow";
            unfollowUserBtn.style.cursor = "pointer";


            if (user.isFollowedByCurrentUser) {
                followUserBtn.style.display = "none";
            } else {
                unfollowUserBtn.style.display = "none";
            }


            followLink.appendChild(followUserBtn);
            followLink.appendChild(unfollowUserBtn);

            row.appendChild(viewUser);
            row.appendChild(followLink);


        tableBody.append(row);
        // configureFollowLinks();
    }
}

/**
 * Adds pagination elements
 * @param count the total number of data objects
 * @param pageNum number of pages to add
 */
function addPagination(count, pageNum, type) {
    console.log(count);
    // remove existing pagination

    let tablePagination;

    if (type === 'following') {
        tablePagination = document.getElementById('following-pagination');

    } else if (type === 'follower') {

    } else {
        tablePagination = document.getElementById("travel-partner-search-pagination");
    }

    let newType = "'"+type+"'";


    while (tablePagination.childNodes.length > 0) {
        tablePagination.childNodes[0].remove();
    }

    let numOfPages = [];
    let pageNumbers = [];
    let places = '';
    let maxPages = Math.ceil(count/usersPerPage);   // round up as there may be more total users than a multiple of 10
    let pagination = document.createElement("ul");
    pagination.classList.add("pagination");
    for (let i = 0; i < maxPages; i++) {
        numOfPages.push(i + 1);
    }

    if (numOfPages.length > 10) {
        if (pageNum > 5) {
            if (numOfPages.length >= pageNum + 5) {
                pageNumbers = [pageNum - 3, pageNum - 2, pageNum - 1, pageNum, pageNum + 1, pageNum + 2, pageNum + 3, pageNum + 4];
            } else {
                lastPage = numOfPages.length - 0;
                pageNumbers = [];
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
    pageButton = document.createElement("a");
    currentPageNum = 1;
    pageButton.innerText = "First";
    pageButton.setAttribute("onClick", `onPaginate(${1} , ${newType})`);
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
    pageButton.setAttribute("onClick", `onPaginate(${currentPageNum - 1} , ${newType})`);
    item.appendChild(pageButton);
    pagination.appendChild(item);
    for (let i = 0; i < pageNumbers.length; i++  ) {
        let item = document.createElement("li");
        const pageButton = document.createElement("a");
        const currentPageNum = pageNumbers[i];
        pageButton.innerText = pageNumbers[i];
        if (currentPageNum === pageNum) {

            // console.log(1.2);

            pageButton.classList.add("active");
        }
        pageButton.setAttribute("onClick", `onPaginate(${pageNumbers[i]} , ${newType})`);
        item.appendChild(pageButton);
        pagination.appendChild(item);
    }
    item = document.createElement("li");
    pageButton = document.createElement("a");
    if (pageNum >= numOfPages.length) {
        currentPageNum = numOfPages.length;
    } else {
        currentPageNum = pageNum + 1;
    }
    pageButton.innerText = ">";
    pageButton.setAttribute("onClick", `onPaginate(${currentPageNum + 1} , ${newType})`);
    item.appendChild(pageButton);
    pagination.appendChild(item);
    if (type === 'following') {
        document.getElementById("following-pagination").appendChild(pagination);
    } else if (type === 'follower') {

    } else {
        document.getElementById("travel-partner-search-pagination").appendChild(pagination);
    }

    item = document.createElement("li");
    pageButton = document.createElement("a");

    pageButton.innerText = "Last";
    pageButton.setAttribute("onClick", `onPaginate(${maxPages}, ${newType})`);
    item.appendChild(pageButton);
    pagination.appendChild(item);
}

/**
 * Sets the maximum date that can be picked from the date inputs to today's date
 */
function initDates() {
    let today = new Date();
    let dd = today.getDate();
    let mm = today.getMonth()+1; //January is 0!
    let yyyy = today.getFullYear();
    if(dd<10){
        dd='0'+dd
    }
    if(mm<10){
        mm='0'+mm
    }

    today = yyyy+'-'+mm+'-'+dd;
    document.getElementById("travel-partner-born-after-filter").setAttribute("max", today);
    document.getElementById("travel-partner-born-before-filter").setAttribute("max", today);
}