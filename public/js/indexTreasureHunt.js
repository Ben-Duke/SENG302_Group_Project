let openTreasureHuntCount = undefined;
const openTreasureHuntsPerPage = 10;
const usersTreasureHuntsPerPage = 10;
let currentPageNum = 1;

/**
 * Initializes the openTreasureHunts paginated table.
 *
 * @param numOpenHuntsPerPage Number of open treasure hunts to show per page.
 */
function initOpenTreasureHunts(numOpenHuntsPerPage) {
    const token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });

    $.ajax({
        type: 'GET',
        url: '/users/treasurehunts/open?offset=0&quantity=' + numOpenHuntsPerPage.toString(),
        contentType: 'application/json',
        success: (res) => {
            importOpenTreasureHunts(res.openTreasureHunts);
        },
        error: (err) => {
            console.log(err);
        }
    });
}

/**
 * Initializes the openTreasureHunts paginated table.
 *
 * @param numHuntsPerPage Number of treasure hunts to show per page.
 */
function initUsersTreasureHunts(numHuntsPerPage) {
    const token =  $('input[name="csrfToken"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Csrf-Token', token);
        }
    });

    $.ajax({
        type: 'GET',
        url: '/users/treasurehunts/user?offset=0&quantity=' + numHuntsPerPage.toString(),
        contentType: 'application/json',
        success: (res) => {
            importUsersTreasureHunts(res.ownTreasureHunts);
        },
        error: (err) => {
            console.log(err);
        }
    });
}

function addTextToRow(row, text) {
    const newCell = document.createElement("TD");
    newCell.innerText = text;
    row.appendChild(newCell);
}

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

function importUsersTreasureHunts(treasureHunts) {
    const table = document.getElementById("ownTreasureHuntTable");
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
}


function importOpenTreasureHunts(treasureHunts) {
    const table = document.getElementById("openTreasureHuntTable");
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

initOpenTreasureHunts(openTreasureHuntsPerPage);

initUsersTreasureHunts(usersTreasureHuntsPerPage);