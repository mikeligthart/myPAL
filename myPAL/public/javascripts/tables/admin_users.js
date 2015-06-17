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
    var table = $('#admin_users').DataTable( {
        "ajax": "/admin/users/list",
        "columns": [
            {
                "className":      'details-control',
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
                "defaultContent": '<button id="removeButton" type="button" class="btn btn-default" aria-label="Left Align"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button>'
            }
        ]
    } );

    $('#admin_users tbody').on( 'click', '#removeButton', function () {
        deleteUser(table.row($(this).closest('tr')).data().email);
        table.row($(this).closest('tr')).remove().draw( false );
    } );

    $('#admin_users tbody').on('click', 'td.details-control', function () {
            var tr = $(this).closest('tr');
            var row = table.row( tr );

            if ( row.child.isShown() ) {
                // This row is already open - close it
                row.child.hide();
                tr.removeClass('shown');
            }
            else {
                // Open this row
                row.child( format(row.data()) ).show();
                tr.addClass('shown');
            }
    } );
} );