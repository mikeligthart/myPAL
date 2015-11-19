function deleteBehaviorBundle(id){
    $.ajax({
        type: "DELETE",
        url: "/admin/behavior/bundle/delete/" + id,
        data: {id:id},
    });
}

var userLang = navigator.language || navigator.userLanguage;
var lang = "/assets/javascripts/tables/en.json"
if(userLang = "nl"){
    lang = "/assets/javascripts/tables/nl.json"
}

var table = $('#admin_behavior_bundle').DataTable( {
    "ajax": "/admin/behavior/bundle/list",
    "columns": [
        { "data": "id" },
        { "data": "behaviors" },
        { "data": "isValid"},
        { "data": "description"},
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

$('#admin_behavior_bundle tbody').on( 'click', '.removeButton', function () {
    deleteBehaviorBundle(table.row($(this).closest('tr')).data().id);
    table.row($(this).closest('tr')).remove().draw( false );
} );

$('#admin_behavior_bundle tbody').on( 'click', '.behaviorButton', function (e) {
    e.preventDefault();
    var avatarBehavior = "#avatarBehavior" + $(this).attr("value");
    $(".avatarBehaviors").hide();
    $(avatarBehavior).show();
});

