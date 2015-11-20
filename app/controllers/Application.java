package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.ConfigFactory;
import dialogue.Dialogue;
import models.UserMyPAL;
import models.UserMyPAL.Login;
import models.UserType;
import controllers.avatar.AvatarReasoner;
import models.diary.DiarySettingsManager;
import models.diary.activity.Picture;
import models.logging.LogAction;
import models.logging.LogActionType;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import util.AppException;
import util.GluconlineClient;
import util.NoValidGluconlineIDException;
import util.PictureFactory;
import views.html.controlFlow.login;
import views.html.controlFlow.no_content;
import views.html.diary.diary_greeting;
import views.html.interfaces.interfaces_description_box;
import views.html.interfaces.interfaces_show_gesture_video;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;

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
            String userName = loginForm.get().userName;
            session("userName", userName);
            UserMyPAL user =  UserMyPAL.byUserName(userName);

            //Set DiarySettings
            DiarySettingsManager.getInstance().login(userName);

            //Load avatarReasoner
            AvatarReasoner.refresh();

            //Load new instances from app if gluconlineID is present
            if(!user.getGluconlineID().isEmpty()){
                try {
                    GluconlineClient gluconlineClient = new GluconlineClient(user);
                    JsonNode measurements = gluconlineClient.retrieve();
                    if(measurements != null) {
                        gluconlineClient.updateMeasurements(measurements);
                    }
                } catch (NoValidGluconlineIDException e) {
                    Logger.error("[Application > authenticate] NoValidGluconlineIDException: " + e.getMessage());
                } catch (AppException e) {
                    Logger.error("[Application > authenticate] AppException: " + e.getMessage());
                }
            }

            //Log user activity
            LogAction.log(userName, LogActionType.LOGIN);

            //Redirect to right page
            UserType userType = user.getUserType();
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
        String userName = session().get("userName");

        //Clear session
        session().clear();

        //Clear DiarySettings
        DiarySettingsManager.getInstance().logoff(userName);

        //Log user activity
        LogAction.log(userName, LogActionType.LOGOFF);

        //Redirect to login page
        return redirect(routes.Application.login());
    }

    /* HELLO AND GOODBYE */

    /**
     *
     * @return rendering of the avatar greeting page
     */
    public static Result hello() {
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        UserMyPAL user = UserMyPAL.byUserName(session().get("userName"));
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
        if(session().isEmpty() || session().get("userName") == null || fileName.isEmpty()){
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
        UserMyPAL user = UserMyPAL.byUserName(session().get("userName"));
        if(picture.getUser().equals(user) || user.getUserType() == UserType.ADMIN){
            //Serve the picture
            return ok(new File(ConfigFactory.load().getString("private.data.location") + fileName));
        }
        return forbidden();
    }

    public static Result dropForMyPALPhoto(){

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("picture");

        UserMyPAL user = UserMyPAL.byUserName("mike.ligthart@gmail.com");
        //If a file is added
        if (filePart != null) {
            //Retrieve, move and store image file to disk and save picture object
            PictureFactory pictureFactory = new PictureFactory();
            Picture picture = pictureFactory.processUploadedFile(filePart, user, Date.valueOf(LocalDate.now()));
            if (picture != null) {
                picture.save();
                LogAction.log(user.getUserName(), LogActionType.UPLOADEDPICTURE);
                return ok("Received");
            } else {
                return badRequest(pictureFactory.getLatestError());
            }
        } else {
            return badRequest(Messages.get("error.pleaseAddFile"));
        }
    }

    public static Result contentBox(String content){
        if(session().isEmpty() || session().get("userName") == null || content.isEmpty()){
            return forbidden();
        }

        return ok(interfaces_description_box.render(content));
    }

    public static Result showGesture(String source){
        if(session().isEmpty() || session().get("userName") == null || source.isEmpty()){
            return forbidden();
        }

        return ok(interfaces_show_gesture_video.render(source));
    }
}

