let userCount;

initUsers();

async function initUsers() {

    let count = await getUserCount();
    userCount = count.count;

    getUserData(1, 10);

    addPagination(10, 0);
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

async function getUserData(pageNum, quantity) {

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

    displayData(users)

}

// @for(user <- users) {
//     @if(user.getUserid != currentUser.getUserid){
//         <tr>
//             <th scope ="row">@user.getUserid()</th>
//                 <td>@user.getEmail()</td>
//                 <td>@user.getFName()</td>
//                 <td>@user.getLName()</td>
//                 <td>@user.getGender()</td>
//                 <td>@user.getDateOfBirth</td>
//         @if(user.userIsAdmin()) {
//             <td><a class="btn btn-link" href="@routes.AdminController.adminToUser(user.getUserid)" role="button">Revoke Admin</a></td>
//             } else {
//             <td><a class="btn btn-link" href="@routes.AdminController.userToAdmin(user.getUserid)" role="button">Make Admin</a></td>
//             }
//         @if(currentUser.getUserid == adminUser.getUserid) {
//             <td><a class="btn btn-link" href="@routes.AdminController.setUserToActAs(user.getUserid)" role="button">Act as user</a></td>
//             } else {
//             <td><a class="btn btn-link" href="@routes.AdminController.setUserToActAs(user.getUserid)" role="button" disabled>Act as user</a></td>
//             }
//         </tr>
//         }
//     }

function displayData(users) {
    const tableBody = document.getElementById('user-table-body');

    // remove existing rows
    for (let child of tableBody.children) {
        child.remove()
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


        tableBody.append(row)

        // eventCategory.innerText = "Type: " + events[i].category.name
        //
        // mediaBody.appendChild(eventLink);
        //
        //
        // document.getElementById("events-results").appendChild(mediaRow);
    }
}

function addPagination(count, pageNum) {
    numOfPages = [];
    pageNumbers = [];
    latitudes = -43.53;
    longitudes = 172.620278;
    places = '';
    const pagination = document.createElement("ul");
    pagination.classList.add("pagination");
    for (let i = 0; i < count; i += 20) {
        numOfPages.push((i / 20) + 1);
    }

    if (numOfPages.length > 10) {
        if (pageNum > 5) {
            if (numOfPages.length >= pageNum + 5) {
                pageNumbers = [pageNum - 3, pageNum - 2, pageNum - 1, pageNum, pageNum + 1, pageNum + 2, pageNum + 3, pageNum + 4];
            } else {
                lastPage = numOfPages.length - 0;
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
    pageButton = document.createElement("a");
    currentPageNum = 1;
    pageButton.innerText = "First";
    pageButton.setAttribute("onClick", `searchEvents(${currentPageNum})`);
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
    pageButton.setAttribute("onClick", `searchEvents(${currentPageNum})`);
    item.appendChild(pageButton);
    pagination.appendChild(item);
    for (let i = 0; i < pageNumbers.length; i++) {
        let item = document.createElement("li");
        const pageButton = document.createElement("a");
        const currentPageNum = pageNumbers[i];
        pageButton.innerText = pageNumbers[i];
        if (currentPageNum == pageNum) {
            pageButton.classList.add("active");
        }
        pageButton.setAttribute("onClick", `searchEvents(${currentPageNum})`);
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
    pageButton.setAttribute("onClick", `searchEvents(${currentPageNum})`);
    item.appendChild(pageButton);
    pagination.appendChild(item);
    document.getElementById("events-results").appendChild(pagination);

    item = document.createElement("li");
    pageButton = document.createElement("a");
    currentPageNum = numOfPages.length;
    // console.log(currentPageNum);
    pageButton.innerText = "Last";
    pageButton.setAttribute("onClick", `searchEvents(${currentPageNum})`);
    item.appendChild(pageButton);
    pagination.appendChild(item);
    let eventsPagination = document.getElementById("eventsPage");
    while (eventsPagination.childNodes.length > 0) {
        eventsPagination.childNodes[0].remove();
    }
}
