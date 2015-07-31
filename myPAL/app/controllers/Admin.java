package controllers;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.User;
import models.UserType;
import play.Logger;
import views.interfaces.UserToHTML;
import models.logging.LogAction;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.admin_home;
import views.html.admin.admin_user_update;
import views.html.admin.admin_user_view;
import views.html.admin.admin_users;
import views.html.controlFlow.no_access;

import java.util.List;

import static play.data.Form.form;
import static play.libs.Json.toJson;

/**
 * Created by Mike on 29-7-2015.
 */
public class Admin extends Controller {

    /* PAGES */
    public static Result admin(){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(user.getUserType() != UserType.ADMIN){
            return forbidden(no_access.render());
        }

        return ok(admin_home.render());
    }

    public static Result users(){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(user.getUserType() != UserType.ADMIN){
            return forbidden(no_access.render());
        }
        Form<User> userForm = form(User.class);
        return ok(admin_users.render(userForm));

    }

    public static Result updatePageUser(String email){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(user.getUserType() != UserType.ADMIN){
            return forbidden(no_access.render());
        }

        User updateThisUser = User.byEmail(email);
        Form<User.UserMutable> userForm = form(User.UserMutable.class);
        if (updateThisUser != null) {
            userForm = userForm.fill(updateThisUser.getMutables());
            return ok(admin_user_update.render(email, userForm));
        } else {
            return forbidden();
        }
    }

    public static Result viewUser(String email){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(user.getUserType() != UserType.ADMIN){
            return forbidden(no_access.render());
        }

        User viewUser = User.byEmail(email);
        if (viewUser != null) {
            return ok(admin_user_view.render(new UserToHTML(viewUser)));
        } else {
            return forbidden();
        }
    }

    /* FUNCTIONS */
    public static Result getUsers(){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(user.getUserType() != UserType.ADMIN){
            return forbidden(no_access.render());
        }

        List<UserToHTML> users = UserToHTML.fromListToList(User.find.all());
        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.put("data", toJson(users));
        return ok(data);
    }

    public static Result addUser(){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(user.getUserType() != UserType.ADMIN){
            return forbidden(no_access.render());
        }

        Form<User> userForm = form(User.class).bindFromRequest();
        if (userForm.hasErrors()) {
            return badRequest(admin_users.render(userForm));
        } else {
            User newUser = userForm.get();
            newUser.save();
            return redirect(routes.Admin.users());
        }
    }

    public static Result updateUser(String email){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(user.getUserType() != UserType.ADMIN){
            return forbidden(no_access.render());
        }

        Form<User.UserMutable> userForm = form(User.UserMutable.class).bindFromRequest();
        if (userForm.hasErrors()) {
            return badRequest(admin_user_update.render(email, userForm));
        } else {
            User updateUser = User.byEmail(email);
            User updatedUser = new User(updateUser);
            updateUser.delete();
            updatedUser.updateFromMutables(userForm.get());
            updatedUser.save();

            return redirect(routes.Admin.users());
        }
    }

    public static Result deleteUser(String email) {
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(user.getUserType() != UserType.ADMIN){
            return forbidden(no_access.render());
        }

        User deleteThisUser = User.byEmail(email);
        if (deleteThisUser != null) {
            deleteThisUser.delete();
            return ok();
        } else {
            return forbidden();
        }
    }

    public static Result getUserLog(String email){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(user.getUserType() != UserType.ADMIN){
            return forbidden(no_access.render());
        }

        User userForLogs = User.byEmail(email);
        if (userForLogs != null){
            List<LogAction> logs = LogAction.find.where().eq("user", userForLogs).findList();
            ObjectNode data = JsonNodeFactory.instance.objectNode();
            data.put("data", toJson(logs));
            return ok(data);
        } else {
            return forbidden(no_access.render());
        }
    }

}
