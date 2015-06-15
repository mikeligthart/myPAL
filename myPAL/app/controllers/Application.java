package controllers;

import dialogue.Dialogue;
import models.User;
import play.Logger;
import play.Routes;
import play.data.Form;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.*;
import views.html.demo.*;
import views.html.diary.*;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;


public class Application extends Controller {

    private static Dialogue dialogue = Dialogue.getInstance();

    public static Result index() {
        return ok(greeting.render(dialogue.getGreeting("Jelte")));
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
        Form<User> userForm = Form.form(User.class).bindFromRequest();
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

    public static Result users(){
        Form<User> userForm = Form.form(User.class);
        List<User> users = User.find.all();
        return ok(admin_users.render(userForm, users));
    }
}
