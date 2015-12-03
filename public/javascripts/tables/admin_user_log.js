var userLang = navigator.language || navigator.userLanguage;
var lang = "/assets/javascripts/tables/en.json"
if(userLang = "nl"){
    lang = "/assets/javascripts/tables/nl.json"
}

var userName = $( "i" ).text();

var table = $('#admin_user_log').DataTable( {
    "ajax": "/admin/users/view/log/" + userName,
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
                        "ajax": "/admin/users/view/activities/" + userName,
                        "columns": [
                            { "data": "id" },
                            { "data": "date" },
                            { "data": "startTime" },
                            { "data": "endTime" },
                            { "data": "type" },
                            { "data": "name" },
                            { "data": "emotion" },
                            { "data": "hasPictureString" },
                            { "data": "shortDescription" }
                        ],
                        "order": [[ 0, "desc" ]],
                        "language": {
                            "url": lang
                        }
} );

var MeasurementTable =  $('#admin_user_measurement_overview').DataTable( {
                        "ajax": "/admin/users/view/measurements/" + userName,
                        "columns": [
                            { "data": "id" },
                            { "data": "date" },
                            { "data": "startTime" },
                            { "data": "displayName" },
                            { "data": "value" },
                            { "data": "unit" },
                            { "data": "comment" }
                        ],
                        "order": [[ 0, "desc" ]],
                        "language": {
                            "url": lang
                        }
} );

var table = $('#admin_avatar_log').DataTable( {
    "ajax": "/admin/users/view/avatarlog/" + userName,
    "columns": [
        { "data": "timestamp" },
        { "data": "type" }
    ],
    "order": [[ 0, "desc" ]],
    "language": {
        "url": lang
    }
} );

