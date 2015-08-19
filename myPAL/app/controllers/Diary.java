package controllers;

import com.fasterxml.jackson.databind.JsonNode;
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
import static play.libs.Json.toJson;

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

        //Manage the right settings such as date for the calendar
        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(email);

        //Retrieve the number of activities
        List<DiaryActivity> diaryActivities = DiaryActivity.byUserAndDate(User.byEmail(email), Date.valueOf(diarySettings.getCalendarDate()));

        return ok(diary_calendar.render(User.byEmail(email).getUserType(), diarySettings.getDateString(true), diarySettings.getDateString(false), diaryActivities.size()));
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
        //Check whether a user is logged in
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

    public static Result viewActivity(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Log user behavior
        LogAction.log(email, LogActionType.VIEWACTIVITY);

        //Check if user has access to view this activity
        User user = User.byEmail(email);
        DiaryActivity activity = DiaryActivity.byID(id);
        if(activity == null){
            return forbidden();
        }
        if(!activity.getUser().equals(user)){
            return forbidden();
        }

        //Manage the right settings such as date for the calendar
        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(email);

        return ok(diary_calendar_view_activity.render(User.byEmail(email).getUserType(), diarySettings.getDateString(true), diarySettings.getDateString(false), new DiaryActivityToHTML(activity)));
    }

    public static Result updateActivityPage(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Log user behavior
        LogAction.log(email, LogActionType.VIEWACTIVITY);

        //Check if user has access to view this activity
        User user = User.byEmail(email);
        DiaryActivity activity = DiaryActivity.byID(id);
        if(activity == null){
            return forbidden();
        }
        if(!activity.getUser().equals(user)){
            return forbidden();
        }

        Form<DiaryActivity> activityForm = form(DiaryActivity.class);
        activityForm = activityForm.fill(activity);

        return ok(diary_update_diaryActivity.render(user.getUserType(), activityForm, new DiaryActivityToHTML(activity)));
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

        //Retrieve the number of activities
        List<DiaryActivity> diaryActivities = DiaryActivity.byUserAndDate(User.byEmail(email), Date.valueOf(diarySettings.getCalendarDate()));

        return ok(diary_calendar.render(User.byEmail(email).getUserType(), diarySettings.getDateString(true), diarySettings.getDateString(false), diaryActivities.size()));
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

        //Retrieve the number of activities
        List<DiaryActivity> diaryActivities = DiaryActivity.byUserAndDate(User.byEmail(email), Date.valueOf(diarySettings.getCalendarDate()));

        return ok(diary_calendar.render(User.byEmail(email).getUserType(), diarySettings.getDateString(true), diarySettings.getDateString(false), diaryActivities.size()));
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
                    newDiaryActivity.save();
                    picture.save();
                    newDiaryActivity.setPicture(picture);
                    newDiaryActivity.update();
                    return redirect(routes.Diary.calendar());
                } else {
                    diaryActivityForm.reject(pictureFactory.getLatestError());
                    return badRequest(diary_add_diaryActivity.render(User.byEmail(email).getUserType(), diaryActivityForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false)));
                }
            } else {
                newDiaryActivity.save();
                return redirect(routes.Diary.calendar());
            }
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

    public static Result getActivities(){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(email);
        List<DiaryActivity> diaryActivities = DiaryActivity.byUserAndDate(User.byEmail(email), Date.valueOf(diarySettings.getCalendarDate()));
        Logger.debug("[Diary > getActivities] diaryAcitivites size: " + diaryActivities.size());

        List<DiaryActivityToHTML> activities = DiaryActivityToHTML.fromListToList(diaryActivities);
        Logger.debug("[Diary > getActivities] diaryAcitivitestoHTML size: " + activities.size());

        JsonNode jsonActivities = toJson(activities);
        Logger.debug("[Diary > getActivities] JsonNode: " + jsonActivities.asText());

        return ok(jsonActivities);
    }

    public static Result deleteActivity(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Log user behavior
        LogAction.log(email, LogActionType.DELETEACTIVITY);

        //Check if user has access to view this activity
        User user = User.byEmail(email);
        DiaryActivity activity = DiaryActivity.byID(id);
        if(activity == null){
            return forbidden();
        }
        if(!activity.getUser().equals(user)) {
            return forbidden();
        }

        user.removeDiaryActivity(activity);
        user.update();

        if(activity.hasPicture()) {
            Picture picture = activity.getPicture();
            picture.setDiaryActivity(null);
            picture.setUser(null);
            activity.setPicture(null);

            picture.update();
            activity.update();
            picture.delete();
        }

        activity.setUser(null);
        activity.delete();

        return redirect(routes.Diary.calendar());
    }

    public static Result updateActivity(){
        return ok();
    }

    public static Result deletePictureFromActivity(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Log user behavior
        LogAction.log(email, LogActionType.DELETEACTIVITY);

        //Check if user has access to view this activity
        User user = User.byEmail(email);
        DiaryActivity activity = DiaryActivity.byID(id);
        if(activity == null){
            return forbidden();
        }
        if(!activity.getUser().equals(user)) {
            return forbidden();
        }

        if(activity.hasPicture()) {
            Picture picture = activity.getPicture();
            activity.setPicture(null);
            picture.setDiaryActivity(null);
            picture.setUser(null);

            picture.update();
            activity.update();
            picture.delete();
        }

        return redirect(routes.Diary.updateActivityPage(id));
    }

    public static Result unlinkPictureFromActivity(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Log user behavior
        LogAction.log(email, LogActionType.DELETEACTIVITY);

        //Check if user has access to view this activity
        User user = User.byEmail(email);
        DiaryActivity activity = DiaryActivity.byID(id);
        if(activity == null){
            return forbidden();
        }
        if(!activity.getUser().equals(user)) {
            return forbidden();
        }

        if(activity.hasPicture()) {
            Picture picture = activity.getPicture();
            activity.setPicture(null);
            picture.setDiaryActivity(null);
            picture.update();
            activity.update();
        }

        return redirect(routes.Diary.updateActivityPage(id));
    }
}
