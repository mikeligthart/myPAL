function deleteUser(email){
    $.ajax({
        type: "DELETE",
        //url: jsRoutes.controllers.Application.deleteUser(email),
        url: "/admin/users/" + email,
        data: {email:email},
    });
}

/*
function deleteItem(id)
{
 console.log(id)
  $.ajax({
   type: "DELETE",
   url: "item/" + id,
   data: {
    id:id
   }
 });
}
*/
