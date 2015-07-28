var userLang = navigator.language || navigator.userLanguage;
var lang = "/assets/javascripts/tables/en.json"
if(userLang = "nl"){
    lang = "/assets/javascripts/tables/nl.json"
}

var email = "mike.ligthart@gmail.com"

var table = $('#admin_user_log').DataTable( {
    "ajax": "/admin/users/view/list/" + email,
    "columns": [
        { "data": "timestamp" },
        { "data": "type" }
    ],
    "order": [[ 0, "desc" ]],
    "language": {
        "url": lang
    }
} );