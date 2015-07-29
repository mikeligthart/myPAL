package controllers;

import dialogue.Dialogue;
import models.User;
import models.User.Login;
import models.diary.DiarySettings;
import models.logging.LogActionType;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.controlFlow.login;
import views.html.diary.greeting;

import java.util.HashMap;
import java.util.Map;

import static play.data.Form.form;


public class Application extends Controller {


    public static final Map<String, DiarySettings> listOfDiaries = new HashMap<>();
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
            session().clear();
            session("email", loginForm.get().email);
            listOfDiaries.put(session().get("email"), new DiarySettings());
            User user = User.byEmail(session().get("email"));
            user.addLogAction(LogActionType.LOGIN);
            user.update();
            return redirect(routes.Application.hello());
        }
    }

    public static Result logout() {
        User user = User.byEmail(session().get("email"));
        user.addLogAction(LogActionType.LOGOFF);
        user.update();
        session().clear();
        listOfDiaries.remove(session().get("email"));
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
