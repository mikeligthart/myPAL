function deleteUser(email){
    $.ajax({
        type: "DELETE",
        //url: jsRoutes.controllers.Application.deleteUser(email),
        url: "/admin/users/" + email,
        data: {email:email},
    });
}