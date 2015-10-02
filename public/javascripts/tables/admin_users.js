var userLang = navigator.language || navigator.userLanguage;
var lang = "/assets/javascripts/tables/en.json"
if(userLang = "nl"){
    lang = "/assets/javascripts/tables/nl.json"
}

var table = $('#admin_users').DataTable( {
    "ajax": "/admin/users/list",
    "columns": [
        { "data": "email" },
        { "data": "firstName" },
        { "data": "lastName" },
        { "data": "birthdate"},
        { "data": "userType"},
        { "data": "lastActivity"},
        {
            "data": null,
            "orderable":      false,
            "defaultContent":  '<button type="button" class="btn btn-default viewButton" aria-label="Left Align"><span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span></button> <button type="button" class="btn btn-default removeButton" aria-label="Left Align" ><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button> <button type="button" class="btn btn-default editButton" aria-label="Left Align"><span class="glyphicon glyphicon-pencil" aria-hidden="true"></span></button>'
        }
    ],
    "order": [[ 0, "desc" ]],
    "language": {
        "url": lang
    }
} );

$('#admin_users tbody').on( 'click', '.removeButton', function () {
    deleteUser(table.row($(this).closest('tr')).data().email);
    table.row($(this).closest('tr')).remove().draw( false );
} );

$('#admin_users tbody').on( 'click', '.editButton', function () {
    window.location.href = "users/update/" + table.row($(this).closest('tr')).data().email;
} );

$('#admin_users tbody').on( 'click', '.viewButton', function () {
    window.location.href = "users/view/" + table.row($(this).closest('tr')).data().email;
} );

