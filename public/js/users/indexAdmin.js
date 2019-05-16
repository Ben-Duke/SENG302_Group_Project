/**
 * Function to update search users on admin screen to show only searched users by email
 */
function searchUsers() {
    var input, filter, table, tr, td, i, txtValue;
    input = document.getElementById("searchUsers");
    filter = input.value.toUpperCase();
    table = document.getElementById("userTable");
    tr = table.getElementsByTagName("tr");
    console.log(filter);
    for (i = 1; i < tr.length; i++) {
        td = tr[i].getElementsByTagName("td")[0];
        if(filter == "") {
            tr[i].style.display = "none";
        }
        else if (td) {
            txtValue = td.textContent || td.innerText;
            if (txtValue.toUpperCase().indexOf(filter) > -1) {
                tr[i].style.display = "";
            } else {
                tr[i].style.display = "none";
            }
        }
    }
}