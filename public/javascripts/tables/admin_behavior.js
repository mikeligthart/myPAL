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
        { "data": "avatarHtmlType"}
    ],
    "order": [[ 0, "asc" ]],
    "language": {
        "url": lang
    }
} );