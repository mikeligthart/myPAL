package controllers;

import dialogue.Dialogue;
import models.User;
import models.User.Login;
import models.UserType;
import models.diary.DiarySettings;
import models.diary.DiarySettingsManager;
import models.logging.LogAction;
import models.logging.LogActionType;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.controlFlow.*;
import views.html.diary.greeting;

import java.util.HashMap;
import java.util.Map;

import static play.data.Form.form;


public class Application extends Controller {


    private static final Dialogue dialogue = Dialogue.getInstance();

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
            //Set session
            session().clear();
            String email = loginForm.get().email;
            session("email", email);

            //Set DiarySettings
            DiarySettingsManager.getInstance().login(email);

            //Log user activity
            LogAction.log(email, LogActionType.LOGIN);

            //Redirect to right page
            UserType userType = User.byEmail(email).getUserType();
            if(userType == UserType.CHILD){
                return redirect(routes.Application.hello());
            }
            else if (userType == UserType.ADMIN){
                return redirect(routes.Admin.addUser());
            } else {
                return forbidden(no_content.render());
            }
        }
    }

    public static Result logout() {
        //Log user activity
        String email = session().get("email");
        LogAction.log(email, LogActionType.LOGOFF);

        //Clear session
        session().clear();

        //Clear DiarySettings
        DiarySettingsManager.getInstance().logoff(email);

        //Redirect to login page
        return redirect(routes.Application.login());
    }

    /* HELLO AND GOODBYE */

    public static Result hello() {
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        return ok(greeting.render(dialogue.getGreeting(user.getFirstName())));
    }
}
