function format ( d ) {
    // `d` is the original data object for the row
    return '<table class="table table-condensed" cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">'+
        '<tr>'+
            '<td>Password:</td>'+
            '<td>'+d.password+'</td>'+
        '</tr>'+
        '<tr>'+
            '<td>UserType:</td>'+
            '<td>'+d.userType+'</td>'+
        '</tr>'+
        '<tr>'+
            '<td>Extra info:</td>'+
            '<td>And any further details here (images etc)...</td>'+
        '</tr>'+
    '</table>';
}

$(document).ready(function() {
    var userLang = navigator.language || navigator.userLanguage;
    var lang = "/assets/javascripts/tables/en.json"
    if(userLang = "nl"){
        lang = "/assets/javascripts/tables/nl.json"
    }

    var table = $('#admin_users').DataTable( {
        "ajax": "/assets/test/testdata3.txt",
        "columns": [
            {
                "className":      'details-control glyphicon glyphicon-triangle-bottom',
                "orderable":      false,
                "data":           null,
                "defaultContent": ''
            },
            { "data": "email" },
            { "data": "firstName" },
            { "data": "lastName" },
            {
                "data": null,
                "orderable":      false,
                "defaultContent": '<button id="removeButton" type="button" class="btn btn-default" aria-label="Left Align"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button> <button id="editButton" type="button" class="btn btn-default" aria-label="Left Align"><span class="glyphicon glyphicon-pencil" aria-hidden="true"></span></button>'
            }
        ],
        "order": [[ 1, "desc" ]],
        "language": {
            "url": lang
        }
    } );

    $('#admin_users tbody').on( 'click', '#removeButton', function () {
        deleteUser(table.row($(this).closest('tr')).data().email);
        table.row($(this).closest('tr')).remove().draw( false );
    } );

    $('#admin_users tbody').on( 'click', '#editButton', function () {
        window.location.href = "users/update/" + table.row($(this).closest('tr')).data().email;
    } );

    $('#admin_users tbody').on('click', 'td.details-control', function () {
            var row = table.row($(this).closest('tr'));

            if ( row.child.isShown() ) {
                // This row is already open - close it
                row.child.hide();
                $(this).removeClass('details-control glyphicon glyphicon-triangle-top');
                $(this).addClass('details-control glyphicon glyphicon-triangle-bottom');
            }
            else {
                // Open this row
                row.child( format(row.data()) ).show();
                $(this).removeClass('details-control glyphicon glyphicon-triangle-bottom');
                $(this).addClass('details-control glyphicon glyphicon-triangle-top');
            }
    } );
} );