var userLang = navigator.language || navigator.userLanguage;
var lang = "/assets/javascripts/tables/en.json"
if(userLang = "nl"){
    lang = "/assets/javascripts/tables/nl.json"
}

var table = $('#admin_behavior').DataTable( {
    "ajax": "/admin/behavior/list",
    "columns": [
        {
            "data": null,
            "orderable":      false,
            "defaultContent":  '<button type="button" class="btn btn-primary addButton" aria-label="Left Align" ><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></button>'
        },
        { "data": "id" },
        { "data": "gesture" },
        { "data": "lines"},
        { "data": "avatarHtmlType"},
        { "data": "bundleId"}
    ],
    "order": [[ 1, "desc" ]],
    "language": {
        "url": lang
    },
    'iDisplayLength': 50
} );
