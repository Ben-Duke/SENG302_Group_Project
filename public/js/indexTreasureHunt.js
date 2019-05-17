
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