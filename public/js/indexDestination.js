//Function to search by name, type and country
function searchDestination(){
    // Declare variables
    var input, filter, table, tr, th, td, td2, i, txtValue, txtValue2, txtValue3;
    input = document.getElementById("searchDestinations");
    filter = input.value.toUpperCase();
    table = document.getElementById("privateDestinations");
    tr = table.getElementsByTagName("tr");

    // Loop through all table rows, and hide those who don't match the search query
    for (i = 0; i < tr.length; i++) {
        th = tr[i].getElementsByTagName("th")[0];
        td = tr[i].getElementsByTagName("td")[0];
        td2 = tr[i].getElementsByTagName("td")[1];
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

function searchPublicDestination(){
    // Declare variables
    var input, filter, table, tr, th, td, td2, i, txtValue, txtValue2, txtValue3;
    input = document.getElementById("searchPublicDestinations");
    filter = input.value.toUpperCase();
    table = document.getElementById("publicDestinations");
    tr = table.getElementsByTagName("tr");

    // Loop through all table rows, and hide those who don't match the search query
    for (i = 0; i < tr.length; i++) {
        th = tr[i].getElementsByTagName("th")[0];
        td = tr[i].getElementsByTagName("td")[0];
        td2 = tr[i].getElementsByTagName("td")[1];
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

function makeDestinationPublic(url){
    if(confirm("Are you sure you want to make this destination public? You won't be able to make it private again.")){
        location.href = url;
    }
    else{

    }
}

function showPrivateDestinations(element){
    if(element.checked){
        document.getElementById("hideDivPrivate").style.display = "";
    }
    else{
        document.getElementById("hideDivPrivate").style.display = "none";
    }
}

function showPublicDestinations(element){
    if(element.checked){
        document.getElementById("hideDivPublic").style.display = "";
    }
    else{
        document.getElementById("hideDivPublic").style.display = "none";
    }
}