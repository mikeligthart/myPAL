var userLang = navigator.language || navigator.userLanguage;
var lang = "/assets/javascripts/tables/en.json"
if(userLang = "nl"){
    lang = "/assets/javascripts/tables/nl.json"
}

var email = $( "i" ).text();

var table = $('#admin_user_log').DataTable( {
    "ajax": "/admin/users/view/log/" + email,
    "columns": [
        { "data": "timestamp" },
        { "data": "type" }
    ],
    "order": [[ 0, "desc" ]],
    "language": {
        "url": lang
    }
} );

var activityTable =  $('#admin_user_activity_overview').DataTable( {
                        "ajax": "/admin/users/view/activities/" + email,
                        "columns": [
                            { "data": "date" },
                            { "data": "startTime" },
                            { "data": "endTime" },
                            { "data": "type" },
                            { "data": "emotion" },
                            { "data": "hasPictureString" },
                            { "data": "shortDescription" }
                        ],
                        "order": [[ 0, "desc" ]],
                        "language": {
                            "url": lang
                        }
} );

