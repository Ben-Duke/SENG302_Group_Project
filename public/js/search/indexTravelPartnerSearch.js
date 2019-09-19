let userCount;
const usersPerPage = 10;
let currentPageNum;

initUsers();

async function initUsers() {

    let count = await getUserCount();
    userCount = count.count;
    currentPageNum = 1;

    await renderPaginatedData();
}

async function onPaginate(currentPage) {
    currentPageNum = currentPage;
    await renderPaginatedData();
}

async function renderPaginatedData() {
    await renderUserData(currentPageNum, usersPerPage);

    addPagination(userCount, currentPageNum);
}

async function getUserCount() {

    const token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });
    return $.ajax({
        type: 'GET',
        url: `/users/admin/userCount`,
        contentType: 'application/json',
        success: (res) => {
            return res;
        }
    });
}

async function renderUserData(pageNum, quantity) {

    const offset = (pageNum - 1) * quantity;


    const token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });

    const users = await $.ajax({
        type: 'GET',
        url: `/users/admin/data?offset=${offset}&quantity=${10}`,
        contentType: 'application/json',
    });

    await displayData(users);
}


async function displayData(users) {
    const tableBody = document.getElementById('user-table-body');

    while (tableBody.childNodes.length > 0) {
        tableBody.childNodes[0].remove();
    }

    for (let user of users) {
        const row = document.createElement("tr");

        const userId = document.createElement("th");
        userId.innerText = user.userId;
        row.appendChild(userId);

        const email = document.createElement("td");
        email.innerText = user.email;
        row.appendChild(email);

        const firstName = document.createElement("td");
        firstName.innerText = user.firstName;
        row.appendChild(firstName);

        const lastName = document.createElement("td");
        lastName.innerText = user.lastName;
        row.appendChild(lastName);

        const gender = document.createElement("td");
        gender.innerText = user.gender;
        row.appendChild(gender);

        const dob = document.createElement("td");
        dob.innerText = user.dob;
        row.appendChild(dob);

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
