package controllers;

import dialogue.Dialogue;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.*;
import views.html.demo.*;
import views.html.greeting;

import java.util.List;


public class Application extends Controller {

    private static Dialogue dialogue = Dialogue.getInstance();

    public static Result index() {
        return ok(greeting.render(dialogue.getGreeting("Jelte")));
    }
    public static Result showBootstrap() {
        return ok(bootstrap.render("Hello World!"));
    }


    /* ADMIN PAGES */
    public static Result admin(){
        return ok(admin_home.render());
    }

    public static Result addUser(){
        User user = Form.form(User.class).bindFromRequest().get();
        user.save();
        return redirect(routes.Application.users());
    }

    public static Result users(){
        List<User> users = User.find.all();
        return ok(admin_users.render(users));
    }
}
