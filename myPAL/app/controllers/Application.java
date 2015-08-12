package controllers;

import com.typesafe.config.ConfigFactory;
import dialogue.Dialogue;
//import jsmessages.JsMessages;
//import jsmessages.JsMessagesFactory;
import models.User;
import models.User.Login;
import models.UserType;
import models.diary.DiarySettings;
import models.diary.DiarySettingsManager;
import models.diary.Picture;
import models.logging.LogAction;
import models.logging.LogActionType;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.controlFlow.*;
import views.html.diary.greeting;
//import play.api.i18n.DefaultMessagesApi;

//import javax.inject.Inject;
import java.io.File;
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

    /* PRIVATE FILE MANAGEMENT */
    public static Result getPicture(String fileName){
        //Retrieve a picture if it exists
        Picture picture;
        if(fileName.contains("picture_")) {
            picture = Picture.byName(fileName);
        } else if(fileName.contains("thumbnail_")){
            picture = Picture.byThumbnail(fileName);
        } else {
            return forbidden();
        }

        //Check if someone is logged in and whether the request is not empty
        if(session().isEmpty() || session().get("email") == null || fileName.isEmpty()|| picture == null || picture.getUser() == null){
            return forbidden();
        }

        //Check if someone has access to the picture
        User user = User.byEmail(session().get("email"));
        if(picture.getUser().equals(user) || user.getUserType() == UserType.ADMIN){
            //Serve the picture
            return ok(new File(ConfigFactory.load().getString("private.data.location") + fileName));
        }
        return forbidden();
    }
}
