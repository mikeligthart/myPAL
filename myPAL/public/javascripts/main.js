function deleteUser(email){
    $.ajax({
        type: "DELETE",
        //url: jsRoutes.controllers.Application.deleteUser(email),
        url: "/admin/users/delete/" + email,
        data: {email:email},
    });
}