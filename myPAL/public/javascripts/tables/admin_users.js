$(document).ready(function() {
    var table = $('#admin_users').DataTable( {
        "ajax": "/admin/users/list",
        "columns": [
            { "data": "email" },
            { "data": "firstName" },
            { "data": "lastName" },
            {
                "data": null,
                "defaultContent": '<button id="removeButton" type="button" class="btn btn-default" aria-label="Left Align"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button>'
            }
        ]
    } );

    $('#admin_users tbody').on( 'click', '#removeButton', function () {
        deleteUser(table.row($(this).closest('tr')).data().email);
        table.row($(this).closest('tr')).remove().draw( false );
    } );
} );