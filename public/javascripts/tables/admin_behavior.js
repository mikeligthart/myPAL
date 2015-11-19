function deleteBehavior(id){
    $.ajax({
        type: "DELETE",
        url: "/admin/behavior/delete/" + id,
        data: {id:id},
    });
}

var userLang = navigator.language || navigator.userLanguage;
var lang = "/assets/javascripts/tables/en.json"
if(userLang = "nl"){
    lang = "/assets/javascripts/tables/nl.json"
}

var table = $('#admin_behavior').DataTable( {
    "ajax": "/admin/behavior/list",
    "columns": [
        { "data": "id" },
        { "data": "gesture" },
        { "data": "lines"},
        { "data": "avatarHtmlType"},
        {
            "data": null,
            "orderable":      false,
            "defaultContent":  '<button type="button" class="btn btn-default removeButton" aria-label="Left Align" ><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button>'
        }
    ],
    "order": [[ 0, "desc" ]],
    "language": {
        "url": lang
    },
    'iDisplayLength': 50
} );

$('#admin_behavior tbody').on( 'click', '.removeButton', function () {
    deleteBehavior(table.row($(this).closest('tr')).data().id);
    table.row($(this).closest('tr')).remove().draw( false );
} );