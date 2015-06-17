package controllers;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dialogue.Dialogue;
import models.Login;
import models.User;
import models.UserMutable;
import play.Logger;
import play.Routes;
import play.data.DynamicForm;
import play.data.Form;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.*;
import views.html.demo.*;
import views.html.diary.*;
import views.html.test.*;
import views.html.controlFlow.*;


import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static play.data.Form.form;
import static play.libs.Json.toJson;


public class Application extends Controller {

    private static Dialogue dialogue = Dialogue.getInstance();

    public static Result index() {
        if(session().isEmpty()){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        return ok(greeting.render(dialogue.getGreeting(user.getFirstName())));
    }
    public static Result showBootstrap() {
        return ok(bootstrap.render("Hello World!"));
    }

    /*JAVASCRIPT ROUTES*/
    /**
    public static Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(Routes.javascriptRouter("jsRoutes", routes.javascript.Application.deleteUser()));
    }
    */

    /*DIARY PAGES */
    public static Result diary(){
        return ok(diary_home.render());
    }

    public static Result calendar(){
        return ok(diary_calendar.render());
    }

    /* ADMIN PAGES */
    public static Result admin(){
        return ok(admin_home.render());
    }

    public static Result addUser(){
        Form<User> userForm = form(User.class).bindFromRequest();
        List<User> users = User.find.all();
        if (userForm.hasErrors()) {
            return badRequest(admin_users.render(userForm, users));
        } else {
            User user = userForm.get();
            user.save();
            return redirect(routes.Application.users());
        }
    }

    public static Result deleteUser(String email) {
        User deleteThisUser = User.byEmail(email);
        if(deleteThisUser != null) {
            deleteThisUser.delete();
            return ok();
        } else {
            return forbidden();
        }
    }

    public static Result getUsers(){
        List<User> users = User.find.all();
        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.put("data", toJson(users));
        return ok(data);
    }

    public static Result users(){
        Form<User> userForm = form(User.class);
        List<User> users = User.find.all();
        return ok(admin_users.render(userForm, users));
    }

    public static Result updatePageUser(String email){
        User updateThisUser = User.byEmail(email);
        Form<UserMutable> userForm = form(UserMutable.class);
        if(updateThisUser != null) {
            userForm = userForm.fill(updateThisUser.getMutables());
            return ok(admin_user_update.render(email, userForm));
        } else {
            return forbidden();
        }
    }

    public static Result updateUser(String email){
        Form<UserMutable> userForm = form(UserMutable.class).bindFromRequest();
        if (userForm.hasErrors()) {
            Logger.debug("error");
            return badRequest(admin_user_update.render(email, userForm));
        } else {
            Logger.debug("succes");
            User user = User.byEmail(email);
            user.updateFromMutables(userForm.get());
            user.update();
            return redirect(routes.Application.users());
        }
    }

    public static Result dataTest(){
        return ok(dataTest.render());
    }

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
}
