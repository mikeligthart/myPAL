package controllers;

import models.User;
import models.diary.*;
import models.logging.LogAction;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Http;
import util.PictureFactory;
import views.interfaces.DiaryActivityToHTML;
import models.logging.LogActionType;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.diary.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static play.data.Form.form;

/**
 * Created by Mike on 29-7-2015.
 */
public class Diary extends Controller {

    /* PAGES */
    public static Result diary(){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        return calendar();
    }

    public static Result calendar(){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Log user activity
        LogAction.log(email, LogActionType.ACCESSCALENDAR);

        //Retrieve and show activities and measurements for a specific date
        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(email);
        List<DiaryActivity> diaryActivities = DiaryActivity.byUserAndDate(User.byEmail(email), Date.valueOf(diarySettings.getCalendarDate()));
        //List<DiaryMeasurement> diaryMeasurements = DiaryMeasurement.find.where().eq("date", diarySettings.getDateString(false)).findList();

        return ok(diary_calendar.render(User.byEmail(email).getUserType(), diarySettings.getDateString(true), diarySettings.getDateString(false), DiaryActivityToHTML.fromListToList(diaryActivities)));
    }

    public static Result goals() {
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        LogAction.log(email, LogActionType.ACCESSGOALS);

        return ok(diary_goals.render(User.byEmail(email).getUserType()));
    }

    public static Result gallery(){
        return gallery("");
    }

    private static Result gallery(String error){
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        LogAction.log(email, LogActionType.ACCESSGALLERY);
        User user = User.byEmail(email);

        List<Picture> pictures = Picture.byUser(user, PictureSort.DATEASC);

        if(error.isEmpty()) {
            return ok(diary_gallery.render(user.getUserType(), pictures, ""));
        }
        else {
            return badRequest(diary_gallery.render(user.getUserType(), pictures, error));
        }
    }

    public static Result addActivityPage(){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Log user activity
        LogAction.log(email, LogActionType.ACCESSADDACTIVITYPAGE);

        //Generate addActivity page
        Form<DiaryActivity> activityForm = form(DiaryActivity.class);
        return ok(diary_add_diaryActivity.render(User.byEmail(email).getUserType(), activityForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false)));
    }

    public static Result addPicturePage(){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Log user activity
        LogAction.log(email, LogActionType.ACCESSADDPICTUREPAGE);

        return ok(diary_add_picture_page.render());
    }

    /* FUNCTIONALITIES */

    public static Result calendarUpdate(String update){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Log user activity
        LogAction.log(email, LogActionType.BUTTONPRESS);

        //Update calendar based on date change
        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(email);
        if(update.contentEquals("-")){
            diarySettings.dateMinusOne();
        } else if (update.contentEquals("+")){
            diarySettings.datePlusOne();
        } else {
            return forbidden();
        }
        List<DiaryActivity> diaryActivities = DiaryActivity.byUserAndDate(User.byEmail(email), Date.valueOf(diarySettings.getCalendarDate()));

        return ok(diary_calendar.render(User.byEmail(email).getUserType(), diarySettings.getDateString(true), diarySettings.getDateString(false), DiaryActivityToHTML.fromListToList(diaryActivities)));
    }

    public static Result calendarSet(String day, String month, String year){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Log user activity
        LogAction.log(email, LogActionType.BUTTONPRESS);

        //Update calendar based on date change
        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(email);
        diarySettings.dateUpdate(day, month, year);
        List<DiaryActivity> diaryActivities = DiaryActivity.byUserAndDate(User.byEmail(email), Date.valueOf(diarySettings.getCalendarDate()));

        return ok(diary_calendar.render(User.byEmail(email).getUserType(), diarySettings.getDateString(true), diarySettings.getDateString(false), DiaryActivityToHTML.fromListToList(diaryActivities)));
    }

    public static Result addActivity() {
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Log user activity
        LogAction.log(email, LogActionType.ADDEDACTIVITY);

        //Retrieve data from input elements on webpage
        Form<DiaryActivity> diaryActivityForm = form(DiaryActivity.class).bindFromRequest();
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("picture_file");

        if (diaryActivityForm.hasErrors()) {
            return badRequest(diary_add_diaryActivity.render(User.byEmail(email).getUserType(), diaryActivityForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false)));
        } else {
            //Retrieve the activity from the form
            DiaryActivity newDiaryActivity = diaryActivityForm.get();
            //Link the activity to a user
            newDiaryActivity.setUser(User.byEmail(email));

            //If a file is added
            if (filePart != null) {
                //Retrieve, move and store image file to disk and save picture object
                PictureFactory pictureFactory = new PictureFactory();
                Picture picture = pictureFactory.processUploadedFile(filePart, newDiaryActivity);
                if (picture != null) {
                    picture.save();
                    newDiaryActivity.setPicture(picture);
                } else {
                    diaryActivityForm.reject(pictureFactory.getLatestError());
                    return badRequest(diary_add_diaryActivity.render(User.byEmail(email).getUserType(), diaryActivityForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false)));
                }
            }

            //Save newly created DiaryActivity
            newDiaryActivity.save();
            return redirect(routes.Diary.calendar());
        }
    }

    public static Result addPicture(){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Log user activity
        LogAction.log(email, LogActionType.ADDEDPICTURE);

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("picture_file");

        //If a file is added
        if (filePart != null) {
            //Retrieve, move and store image file to disk and save picture object
            PictureFactory pictureFactory = new PictureFactory();
            Picture picture = pictureFactory.processUploadedFile(filePart, User.byEmail(email), Date.valueOf(LocalDate.now()));
            if (picture != null) {
                picture.save();
                return gallery();
            } else {
                return gallery(pictureFactory.getLatestError());
            }
        } else {
            return gallery(Messages.get("error.pleaseAddFile"));
        }
    }


}
