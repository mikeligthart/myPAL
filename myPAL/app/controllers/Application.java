package controllers;

import com.typesafe.config.ConfigFactory;
import dialogue.Dialogue;
import models.User;
import models.User.Login;
import models.UserType;
import models.diary.DiarySettingsManager;
import models.diary.activity.Picture;
import models.logging.LogAction;
import models.logging.LogActionType;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.controlFlow.*;
import views.html.diary.diary_greeting;
import views.html.interfaces.interfaces_description_box;
import java.io.File;

import static play.data.Form.form;

/**
 * myPAL
 * Purpose: handles all the controller functions for the general or misc control flow and pages
 *
 * Developped for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 21-8-2015
 */
public class Application extends Controller {

    private static final Dialogue dialogue = Dialogue.getInstance();

    /* CONTROL FLOW */

    /**
     *
     * @return rendered login page
     */
    public static Result login() {
        return ok(login.render(form(Login.class)));
    }

    /**
     *
     * @return redirect to right page depending on user type
     */
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
                return redirect(routes.Admin.admin());
            } else {
                return forbidden(no_content.render());
            }
        }
    }

    /**
     * Log out user
     * @return
     */
    public static Result logout() {
        //Retrieve session details
        String email = session().get("email");

        //Clear session
        session().clear();

        //Clear DiarySettings
        DiarySettingsManager.getInstance().logoff(email);

        //Log user activity
        LogAction.log(email, LogActionType.LOGOFF);

        //Redirect to login page
        return redirect(routes.Application.login());
    }

    /* HELLO AND GOODBYE */

    /**
     *
     * @return rendering of the avatar greeting page
     */
    public static Result hello() {
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        return ok(diary_greeting.render(dialogue.getGreeting(user.getFirstName())));
    }

    /* PRIVATE FILE MANAGEMENT */

    /**
     * Validates whether user may retrieve a requested picture and if so returns it
     * @param fileName - file name of the requested picture
     * @return requested picture
     */
    public static Result getPicture(String fileName){
        //Check if someone is logged in and whether the request is not empty
        if(session().isEmpty() || session().get("email") == null || fileName.isEmpty()){
            return forbidden();
        }

        //Retrieve a picture if it exists
        Picture picture;
        if(fileName.contains("picture_")) {
            picture = Picture.byName(fileName);
        } else if(fileName.contains("thumbnail_")){
            picture = Picture.byThumbnail(fileName);
        } else {
            return forbidden();
        }

        //Check if the picture exists
       if (picture == null || picture.getUser() == null){
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

    public static Result contentBox(String content){
        if(session().isEmpty() || session().get("email") == null || content.isEmpty()){
            return forbidden();
        }

        return ok(interfaces_description_box.render(content));
    }
}

