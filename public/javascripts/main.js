function deleteUser(email){
    $.ajax({
        type: "DELETE",
        //url: jsRoutes.controllers.Application.deleteUser(email),
        url: "/admin/users/delete/" + email,
        data: {email:email},
    });
}
function getFormattedDate(date) {
  var year = date.getFullYear();
  var month = (1 + date.getMonth()).toString();
  month = month.length > 1 ? month : '0' + month;
  var day = date.getDate().toString();
  day = day.length > 1 ? day : '0' + day;
  return day + '/' + month + '/' + year;
}


$('#datePicker').datepicker();
$('#birthdate').datepicker();

$('#datePicker').datepicker().on('changeDate', function(ev){
    window.location = "/mypal/calendar/" + getFormattedDate(ev.date);
});