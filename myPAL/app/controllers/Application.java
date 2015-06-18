package controllers;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dialogue.Dialogue;
import models.User;
import models.User.*;
import play.Logger;
import play.data.Form;
import play.i18n.Lang;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.*;
import views.html.demo.*;
import views.html.diary.*;
import views.html.test.*;
import views.html.controlFlow.*;
import java.util.List;
import static play.data.Form.form;
import static play.libs.Json.toJson;


public class Application extends Controller {

    private static Dialogue dialogue = Dialogue.getInstance();

    /* CONTROL FLOW */
    public static Result login() {
        return ok(
                login.render(form(Login.class))
        );
    }

    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            session("email", loginForm.get().email);
            return redirect(routes.Application.index());
        }
    }

    public static Result logout(){
        session().clear();
        return redirect(routes.Application.login());
    }

    public static Result index() {
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        return ok(greeting.render(dialogue.getGreeting(user.getFirstName())));
    }


    /*DIARY */
    /* Pages */
    public static Result diary(){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        return ok(diary_home.render());
    }

    public static Result calendar(){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        return ok(diary_calendar.render());
    }

    /* ADMIN */
    /* Pages */
    public static Result admin(){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(!user.getUserType().equalsIgnoreCase("4")){
            return forbidden(no_access.render());
        }

        return ok(admin_home.render());
    }

    public static Result users(){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(!user.getUserType().equalsIgnoreCase("4")){
            return forbidden(no_access.render());
        }
        Form<User> userForm = form(User.class);
        List<User> users = User.find.all();
        return ok(admin_users.render(userForm, users));

    }

    public static Result updatePageUser(String email){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(!user.getUserType().equalsIgnoreCase("4")){
            return forbidden(no_access.render());
        }

        User updateThisUser = User.byEmail(email);
        Form<UserMutable> userForm = form(User.UserMutable.class);
        if (updateThisUser != null) {
            userForm = userForm.fill(updateThisUser.getMutables());
            return ok(admin_user_update.render(email, userForm));
        } else {
            return forbidden();
        }
    }

    /* Functionalities */
    public static Result getUsers(){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(!user.getUserType().equalsIgnoreCase("4")){
            return forbidden();
        }
        List<User> users = User.find.all();
        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.put("data", toJson(users));
        return ok(data);
    }

    public static Result addUser(){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(!user.getUserType().equalsIgnoreCase("4")){
            return forbidden();
        }

        Form<User> userForm = form(User.class).bindFromRequest();
        List<User> users = User.find.all();
        if (userForm.hasErrors()) {
            return badRequest(admin_users.render(userForm, users));
        } else {
            User newUser = userForm.get();
            newUser.save();
            return redirect(routes.Application.users());
        }
    }

    public static Result updateUser(String email){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(!user.getUserType().equalsIgnoreCase("4")){
            return forbidden();
        }

        Form<UserMutable> userForm = form(UserMutable.class).bindFromRequest();
        if (userForm.hasErrors()) {
            Logger.debug("error");
            return badRequest(admin_user_update.render(email, userForm));
        } else {
            Logger.debug("succes");
            User updateUser = User.byEmail(email);
            updateUser.updateFromMutables(userForm.get());
            updateUser.update();
            return redirect(routes.Application.users());
        }
    }

    public static Result deleteUser(String email) {
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(!user.getUserType().equalsIgnoreCase("4")){
            return forbidden();
        }

        User deleteThisUser = User.byEmail(email);
        if (deleteThisUser != null) {
            deleteThisUser.delete();
            return ok();
        } else {
            return forbidden();
        }
    }

    /* TEST PAGES */
    public static Result dataTest(){
        return ok(dataTest.render());
    }

    public static Result showBootstrap() {
        return ok(bootstrap.render("Hello World!"));
    }
}
