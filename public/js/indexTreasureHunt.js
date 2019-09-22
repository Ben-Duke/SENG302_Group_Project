const treasureHuntsPerPage = 10;
let currentOpenPageNum = 1;
let currentOwnPageNum = 1;

/**
 * Initializes the openTreasureHunts paginated table.
 *
 */
function initOpenTreasureHunts() {
    const offset = (currentOpenPageNum - 1) * treasureHuntsPerPage;
    const token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });

    $.ajax({
        type: 'GET',
        url: `/users/treasurehunts/open?offset=${offset}&quantity=${treasureHuntsPerPage}`,
        contentType: 'application/json',
        success: (res) => {
            importOpenTreasureHunts(res.openTreasureHunts,
                res.totalCountOpenTreasureHunts);
        },
        error: (err) => {
            console.log(err);
        }
    });
}

/**
 * Initializes the usersTreasureHunts paginated table.
 */
function initUsersTreasureHunts() {
    const offset = (currentOwnPageNum - 1) * treasureHuntsPerPage;

    const token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });

    $.ajax({
        type: 'GET',
        url: `/users/treasurehunts/user?offset=${offset}&quantity=${treasureHuntsPerPage}`,
        contentType: 'application/json',
        success: (res) => {
            importUsersTreasureHunts(res.ownTreasureHunts, res.totalCountOpenTreasureHunts);
        },
        error: (err) => {
            console.log(err);
        }
    });
}

/**
 * Adds a cell to a row in a table
 * @param row the row to add
 * @param text the text to put in the cell
 */
function addTextToRow(row, text) {
    const newCell = document.createElement("TD");
    newCell.innerText = text;
    row.appendChild(newCell);
}

/**
 * Adds a delete buttton for the treasure hunts tables
 * @param row the row to add the button to
 * @param treasureHunt the TreasureHunt the button is linked to
 */
function addDeleteButtonToRow(row, treasureHunt) {
    const newCell = document.createElement("TD");

    const deleteBtn = document.createElement("btn");
    deleteBtn.classList.add("btn");
    deleteBtn.classList.add("btn-danger");

    if (treasureHunt.isHidden) {
        deleteBtn.innerText = "Unauthorized";
        deleteBtn.setAttribute('disabled', "")
    } else {
        deleteBtn.innerText = "Delete";
        deleteBtn.setAttribute('data-toggle', 'modal');
        deleteBtn.setAttribute('data-target', '#confirmTreasureHuntDeleteModal')
    }
    newCell.appendChild(deleteBtn);
    row.appendChild(newCell);
}

/**
 * Adds an edit button to a row of a treasure hunt table
 * @param row the row to add the button to
 * @param treasureHunt the TreasureHunt the edit button is linked to
 */
function addEditButtonToRow(row, treasureHunt) {
    const newCell = document.createElement("TD");

    const editBtn = document.createElement("A");
    editBtn.classList.add("btn");
    editBtn.classList.add("btn-primary");

    editBtn.innerText = "Edit";
    editBtn.href = `/users/treasurehunts/edit/${treasureHunt.id}`;

    newCell.appendChild(editBtn);
    row.appendChild(newCell);
}

/**
 * Adds all rows to the table for a user's own TreasureHunts
 * @param treasureHunts the treasurehunts to add
 * @param count the number of treasure hunts
 */
function importUsersTreasureHunts(treasureHunts, count) {
    const table = document.getElementById("ownTreasureHuntTable");
    while (table.firstChild) {
        table.removeChild(table.firstChild)
    }
    for (let treasureHunt of treasureHunts) {
        const row = document.createElement("TR");
        row.classList.add("clickable");

        const titleCell = document.createElement("TH");
        titleCell.setAttribute("scope", "row");
        titleCell.innerText = treasureHunt.title;
        row.appendChild(titleCell);

        addTextToRow(row, treasureHunt.isOpen ? "Open" : "Closed");
        addTextToRow(row, treasureHunt.startDate);
        addTextToRow(row, treasureHunt.endDate);
        addTextToRow(row, treasureHunt.destName);
        addTextToRow(row, treasureHunt.riddle);
        addEditButtonToRow(row, treasureHunt);
        addDeleteButtonToRow(row, treasureHunt);

        table.appendChild(row);

    }
    addPagination(table, count, currentOwnPageNum);
}


/**
 * Adds all rows to the table for all public open TreasureHunts
 * @param treasureHunts the treasurehunts to add
 * @param count the number of treasure hunts
 */
function importOpenTreasureHunts(treasureHunts, count) {
    const table = document.getElementById("openTreasureHuntTable");
    while (table.firstChild) {
        table.removeChild(table.firstChild)
    }
    for (let treasureHunt of treasureHunts) {
        const row = document.createElement("TR");
        row.classList.add("clickable");

        const titleCell = document.createElement("TH");
        titleCell.setAttribute("scope", "row");
        titleCell.innerText = treasureHunt.title;
        row.appendChild(titleCell);

        addTextToRow(row, treasureHunt.endDate);
        addTextToRow(row, treasureHunt.destName);
        addTextToRow(row, treasureHunt.riddle);
        addDeleteButtonToRow(row, treasureHunt);

        table.appendChild(row);
    }
    addPagination(table, count, currentOpenPageNum);
}

/**
 * Adds pagination to a table
 * @param table the table to add pagination to
 * @param count the number of items to account for
 * @param pageNum the current page num
 */
function addPagination(table, count, pageNum) {
    function getOnClick(text, tableId) {
        if (typeof text === "number") {
            if (table.id === "openTreasureHuntTable") {
                return function () {
                    currentOpenPageNum = text;
                    initOpenTreasureHunts()
                }
            } else {
                return function () {
                    currentOwnPageNum = text;
                    initUsersTreasureHunts()
                }
            }
        } else if (typeof(text) === "string") {
            switch (text) {
                case "First":
                    return getOnClick(1, tableId);
                case "Last":
                    return getOnClick(pageNumbers.length, tableId);
                case "<":
                    if (table.id === "openTreasureHuntTable") {
                        return function () {
                            if (currentOpenPageNum > 1) {
                                currentOpenPageNum--;
                            }
                            initOpenTreasureHunts()
                        }
                    } else {
                        return function () {
                            if (currentOpenPageNum > 1) {
                                currentOpenPageNum--;
                            }
                            initUsersTreasureHunts()
                        }
                    }
                case ">":
                    if (table.id === "openTreasureHuntTable") {
                        return function () {
                            if (currentOpenPageNum < pageNumbers.length) {
                                currentOpenPageNum++;
                            }
                            initOpenTreasureHunts()
                        }
                    } else {
                        return function () {
                            if (currentOpenPageNum < pageNumbers.length) {
                                currentOpenPageNum++;
                            }
                            initUsersTreasureHunts()
                        }
                    }
            }
        }
    }
    function addPaginationButton(table, pagination, text, isCurrent) {
        const item = document.createElement("li");
        const pageButton = document.createElement("a");
        pageButton.innerText = text;
        if (isCurrent) {
            item.classList.add("active");
        }

        item.onclick = getOnClick(text, table.id);

        item.appendChild(pageButton);
        pagination.appendChild(item);
    }
    let numOfPages = [];
    let pageNumbers = [];
    const pagination = document.createElement("ul");
    pagination.classList.add("pagination");
    for (let i = 0; i < count; i += treasureHuntsPerPage) {
        numOfPages.push((i / treasureHuntsPerPage) + 1);
    }

    if (numOfPages.length > 10) {
        if (pageNum > 5) {
            if (numOfPages.length >= pageNum + 5) {
                pageNumbers = [pageNum - 3, pageNum - 2, pageNum - 1, pageNum, pageNum + 1, pageNum + 2, pageNum + 3, pageNum + 4];
            } else {
                let lastPage = numOfPages.length;
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

    addPaginationButton(table, pagination, "First");
    addPaginationButton(table, pagination, "<");

    for (let currentPageNum of pageNumbers) {
        const isCurrent = currentPageNum === pageNum;
        addPaginationButton(table, pagination, currentPageNum, isCurrent);
    }
    addPaginationButton(table, pagination, ">");
    addPaginationButton(table, pagination, "Last");

    table.appendChild(pagination);
}

/**
 * Function to search for private destinations.
 * Updates the rows of tables with class "privateDestinations" depending on what's entered in the input form with class name "searchDestinations"
 */
function searchTreasureHunt(){
    // Declare variables
    var input, elements, filter, tables, table, tr, th, td, td2, i, txtValue, txtValue2, txtValue3;
    elements = document.getElementsByClassName("searchTreasureHunts");
    for(var a=0; a<elements.length; a++) {
        input = elements[a];
        filter = input.value.toUpperCase();
        tables = document.getElementsByClassName("treasureHunts");
        table = tables[a];
        tr = table.getElementsByTagName("tr");
        // Loop through all table rows, and hide those who don't match the search query
        for (i = 0; i < tr.length; i++) {
            th = tr[i].getElementsByTagName("th")[0];
            td = tr[i].getElementsByTagName("td")[0];
            td2 = tr[i].getElementsByTagName("td")[2];
            if (td) {
                txtValue = td.textContent || td.innerText;
                txtValue2 = th.textContent || th.innerText;
                txtValue3 = td2.textContent || td2.innerText;
                if (txtValue.toUpperCase().indexOf(filter) > -1 || txtValue2.toUpperCase().indexOf(filter) > -1
                    || txtValue3.toUpperCase().indexOf(filter) > -1) {
                    tr[i].style.display = "";
                } else {
                    tr[i].style.display = "none";
                }
            }
        }
    }
}

$('#confirmTreasureHuntDeleteModal').on('show.bs.modal', function(e) {
    var treasureHuntId = $(event.target).closest('tr').data('id');
    $('#yesDelete').click(function(e){
        var token =  $('input[name="csrfToken"]').attr('value');
        $.ajaxSetup({
            beforeSend: function(xhr) {
                xhr.setRequestHeader('Csrf-Token', token);
            }
        });
        $.ajax({
            url: '/users/treasurehunts/delete/' + treasureHuntId,
            method: "GET",
            success:function(res){
                document.location.reload(true);
            }
        });
    });
});

initOpenTreasureHunts();

initUsersTreasureHunts();