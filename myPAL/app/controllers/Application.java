package controllers;

import dialogue.Dialogue;
import models.User;
import play.Logger;
import play.data.Form;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.*;
import views.html.demo.*;
import views.html.diary.*;
import views.html.greeting;

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
            Logger.debug("badrequest");
            return badRequest(admin_users.render(userForm, users));
        } else {
            User user = userForm.get();
            user.save();
            return redirect(routes.Application.users());
        }
    }

    public static Result users(){
        Form<User> userForm = Form.form(User.class);
        List<User> users = User.find.all();
        return ok(admin_users.render(userForm, users));
    }

    /**
    public static class UserAdd {

        @Id
        @Constraints.Required(message="This is field required")
        @Constraints.Email
        public String email;

        @Constraints.Required(message="This is field required")
        public String firstName;

        @Constraints.Required
        public String lastName;

        @Constraints.Required
        @Formats.NonEmpty
        public String password;

        @Constraints.Required
        public int userType;

        public String validate() {
            if (User.byEmail(email) != null) {
                return "This e-mail is already registered.";
            }
            return null;
        }

    }
     */
}
