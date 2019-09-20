let userCount;
const usersPerPage = 10;
let currentPageNum;
let filters;

initUsers();

async function initUsers() {
    updateFilters();
    let count = await getUserCount();
    userCount = count;
    currentPageNum = 1;
    await renderPaginatedData();
}

function updateFilters() {
    filters = {
        male: document.getElementById("travel-partner-male-filter").checked,
        female: document.getElementById("travel-partner-female-filter").checked,
        other: document.getElementById("travel-partner-other-filter").checked,
    };
}

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
    return route;
}

async function onPaginate(currentPage) {
    console.log("got here");
    currentPageNum = currentPage;
    await renderPaginatedData();
}

async function renderPaginatedData() {
    await renderUserData(currentPageNum, usersPerPage);

    addPagination(userCount, currentPageNum);
}

async function getUserCount() {

    const token =  $('input[name="csrfToken"]').attr('value');
    const userCountRoute = "/users/profile/searchprofiles/count?";
    let filteredRoute = appendQueryParameters(userCountRoute);
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

async function renderUserData(pageNum, quantity) {

    const offset = (pageNum - 1) * quantity;
    const searchTravelPartnerRoute = "/users/profile/searchprofiles?";
    let filteredRoute = appendQueryParameters(searchTravelPartnerRoute);
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

    await displayData(users);
}


async function displayData(users) {
    const tableBody = document.getElementById('travel-partner-search-table-body');
    console.log(users);
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

        const changeAdmin = document.createElement("td");

        if (!user.isCurrentUser) {

            const changeAdminButton = document.createElement("a");
            changeAdminButton.class = "btn btn-link";

            if (user.isAdmin) {
                changeAdminButton.href = "/users/admin/remove/" + user.userId;
                changeAdminButton.innerText = "Revoke Admin";
            } else {
                changeAdminButton.href = "/users/admin/make/" + user.userId;
                changeAdminButton.innerText = "Make Admin";
            }
            changeAdmin.appendChild(changeAdminButton);
        }

        row.appendChild(changeAdmin);


        const actAsUser = document.createElement("td");

        if (!user.isCurrentUser) {

            const actAsUserButton = document.createElement("a");
            actAsUserButton.class = "btn btn-link";

            actAsUserButton.href = "/users/admin/actasuser/" + user.userId;
            actAsUserButton.innerText = "Act as user";

            actAsUser.appendChild(actAsUserButton);
        }

        row.appendChild(actAsUser);


        tableBody.append(row);
    }
}

function addPagination(count, pageNum) {
    // remove existing pagination
    let eventsPagination = document.getElementById("travel-partner-search-pagination");
    while (eventsPagination.childNodes.length > 0) {
        eventsPagination.childNodes[0].remove();
    }

    let numOfPages = [];
    let pageNumbers = [];
    let places = '';
    const maxPages = Math.ceil(count/usersPerPage);   // round up as there may be more total users than a multiple of 10
    const pagination = document.createElement("ul");
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
    pageButton.setAttribute("onClick", `onPaginate(${1})`);
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
    pageButton.setAttribute("onClick", `onPaginate(${currentPageNum - 1})`);
    item.appendChild(pageButton);
    pagination.appendChild(item);
    for (let i = 0; i < pageNumbers.length; i++  ) {
        let item = document.createElement("li");
        const pageButton = document.createElement("a");
        const currentPageNum = pageNumbers[i];
        pageButton.innerText = pageNumbers[i];
        if (currentPageNum === pageNum) {
            pageButton.classList.add("active");
        }
        pageButton.setAttribute("onClick", `onPaginate(${pageNumbers[i]})`);
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
    pageButton.setAttribute("onClick", `onPaginate(${currentPageNum + 1})`);
    item.appendChild(pageButton);
    pagination.appendChild(item);
    document.getElementById("travel-partner-search-pagination").appendChild(pagination);

    item = document.createElement("li");
    pageButton = document.createElement("a");

    pageButton.innerText = "Last";
    pageButton.setAttribute("onClick", `onPaginate(${maxPages})`);
    item.appendChild(pageButton);
    pagination.appendChild(item);
}
