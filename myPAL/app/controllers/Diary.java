package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.User;
import models.diary.*;
import models.logging.LogAction;
import play.data.DynamicForm;
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
 * myPAL
 * Purpose: handles all the controller functions for the diary actions and pages
 *
 * Developped for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 21-8-2015
 */
public class Diary extends Controller {

    /* PAGES */

    /**
     *
     * @return the main diary page
     */
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

        //Manage the right settings such as date for the calendar
        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(email);

        //Retrieve the number of activities
        List<DiaryActivity> diaryActivities = DiaryActivity.byUserAndDate(User.byEmail(email), Date.valueOf(diarySettings.getCalendarDate()));

        //Log user activity
        LogAction.log(email, LogActionType.ACCESSCALENDAR);

        return ok(diary_calendar.render(User.byEmail(email).getUserType(), diarySettings.getDateString(true), diarySettings.getDateString(false), diaryActivities.size()));
    }

    public static Result goals() {
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Log user activity
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

        //Retrieve the pictures belonging to the user
        User user = User.byEmail(email);
        List<Picture> pictures = Picture.byUser(user, PictureSort.DATEASC);

        //If there is no error load the gallery else return badrequest
        if(error.isEmpty()) {
            LogAction.log(email, LogActionType.ACCESSGALLERY);
            return ok(diary_gallery.render(user.getUserType(), pictures, ""));
        }
        else {
            return badRequest(diary_gallery.render(user.getUserType(), pictures, error));
        }
    }

    public static Result addActivityPage(){
        return addActivityPage("");
    }

    private static Result addActivityPage(String error){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        User user = User.byEmail(email);

        //Generate addActivity page
        Form<DiaryActivity> activityForm = form(DiaryActivity.class);

        //Log user activity
        LogAction.log(email, LogActionType.ACCESSADDACTIVITYPAGE);

        return ok(diary_add_diaryActivity.render(user, activityForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), "", error));

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

        //Log user behavior
        LogAction.log(email, LogActionType.VIEWACTIVITY);

        return ok(diary_calendar_view_activity.render(User.byEmail(email).getUserType(), diarySettings.getDateString(true), diarySettings.getDateString(false), new DiaryActivityToHTML(activity)));
    }

    private static Result updateActivityPage(int id, String error){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

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

        //Log user behavior
        LogAction.log(email, LogActionType.VIEWACTIVITY);

        return ok(diary_update_diaryActivity.render(user, activityForm, new DiaryActivityToHTML(activity), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), activity.getType().getName(), error));
    }

    public static Result updateActivityPage(int id){
        return updateActivityPage(id, "");
    }

    public static Result selectFromGalleryPage(int id){
        return selectFromGalleryPage(id, "");
    }

    private static Result selectFromGalleryPage(int id, String error){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        User user = User.byEmail(email);
        List<Picture> pictures = Picture.byUserOnlyUnlinked(user, PictureSort.DATEASC);

        if(error.isEmpty()){
            //Log user activity
            LogAction.log(email, LogActionType.SELECTPICTUREFROMGALLRERYPAGE);

            return ok(diary_gallery_select_picture.render(user.getUserType(), pictures,"", id));
        } else {
            return ok(diary_gallery_select_picture.render(user.getUserType(), pictures, error, id));
        }

    }

    public static Result addPictureDirectlyPage(int id){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Log user activity
        LogAction.log(email, LogActionType.ACCESSADDPICTUREDIRECTLYPAGE);

        return ok(diary_add_picture_page_direct.render(id));
    }

    public static Result addDiaryActivityTypePage(String source, int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }

        return ok(diary_add_diaryActivityType.render(source, id));
    }

    public static Result removeDiaryActivityTypePage(String source, int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        List<DiaryActivityType> diaryActivityTypes = DiaryActivityType.byUser(user);

        return ok(diary_remove_diaryActivityType.render(source, id, diaryActivityTypes));
    }

    /* FUNCTIONALITIES */

    public static Result calendarUpdate(String update){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Update calendar based on date change
        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(email);
        if(update.contentEquals("-")){
            LogAction.log(email, LogActionType.UPDATECALENDARDOWN);
            diarySettings.dateMinusOne();
        } else if (update.contentEquals("+")){
            LogAction.log(email, LogActionType.UPDATECALENDARUP);
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

        //Update calendar based on date change
        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(email);
        diarySettings.dateUpdate(day, month, year);

        //Retrieve the number of activities
        List<DiaryActivity> diaryActivities = DiaryActivity.byUserAndDate(User.byEmail(email), Date.valueOf(diarySettings.getCalendarDate()));

        //Log user activity
        LogAction.log(email, LogActionType.UPDATECALENDARDIRECTLY);

        return ok(diary_calendar.render(User.byEmail(email).getUserType(), diarySettings.getDateString(true), diarySettings.getDateString(false), diaryActivities.size()));
    }

    public static Result addActivity() {
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        User user = User.byEmail(email);

        //Retrieve data from input elements on webpage
        Form<DiaryActivity> diaryActivityForm = form(DiaryActivity.class).bindFromRequest();
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("picture_file");
        DynamicForm requestData = form().bindFromRequest();
        boolean addFromGallery = requestData.get("isaddfromgallery").equalsIgnoreCase("true");

        if(requestData.get("diaryActivityType") == null){
            return badRequest(diary_add_diaryActivity.render(user, diaryActivityForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), "", Messages.get("error.notypeselected")));
        }
        DiaryActivityType diaryActivityType = DiaryActivityType.byId(Integer.valueOf(requestData.get("diaryActivityType")));

        if (diaryActivityForm.hasErrors()) {
            return badRequest(diary_add_diaryActivity.render(user, diaryActivityForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), diaryActivityType.getName(), ""));
        } else {
            //Retrieve the activity from the form
            DiaryActivity newDiaryActivity = diaryActivityForm.get();
            //Link the activity to a user
            newDiaryActivity.setUser(User.byEmail(email));
            //Link the activity to a type
            newDiaryActivity.setType(diaryActivityType);
            DiarySettingsManager.getInstance().retrieve(email).dateUpdate(newDiaryActivity.getDate());

            // if the user wants to add a picture from gallery
            if(addFromGallery){
                newDiaryActivity.save();
                return redirect(routes.Diary.selectFromGalleryPage(newDiaryActivity.getId()));
            } else {
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
                        LogAction.log(email, LogActionType.ADDEDACTIVITY);
                        return redirect(routes.Diary.calendar());
                    } else {
                        diaryActivityForm.reject(pictureFactory.getLatestError());
                        return badRequest(diary_add_diaryActivity.render(user, diaryActivityForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), diaryActivityType.getName(), ""));
                    }
                } else {
                    newDiaryActivity.save();
                    LogAction.log(email, LogActionType.ADDEDACTIVITY);
                    return redirect(routes.Diary.calendar());
                }
            }
        }
    }

    public static Result addPicture(){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("picture_file");

        //If a file is added
        if (filePart != null) {
            //Retrieve, move and store image file to disk and save picture object
            PictureFactory pictureFactory = new PictureFactory();
            Picture picture = pictureFactory.processUploadedFile(filePart, User.byEmail(email), Date.valueOf(LocalDate.now()));
            if (picture != null) {
                picture.save();
                LogAction.log(email, LogActionType.ADDEDPICTURE);
                return gallery();
            } else {
                return gallery(pictureFactory.getLatestError());
            }
        } else {
            return gallery(Messages.get("error.pleaseAddFile"));
        }
    }

    public static Result addPictureDirectly(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        DiaryActivity diaryActivity = DiaryActivity.byID(id);
        if (diaryActivity == null){
            return selectFromGalleryPage(id, Messages.get("error.activityNotFound"));
        }

        //Check if user has access to manipulate this activity
        User user = User.byEmail(email);
        if(!diaryActivity.getUser().equals(user)) {
            return forbidden();
        }
        if (diaryActivity.hasPicture()){
            return selectFromGalleryPage(id, Messages.get("error.hasAlreadyPicture"));
        }

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("picture_file");

        //If a file is added
        if (filePart != null) {
            //Retrieve, move and store image file to disk and save picture object and link it to DiaryActivity
            PictureFactory pictureFactory = new PictureFactory();
            Picture picture = pictureFactory.processUploadedFile(filePart, diaryActivity);
            if (picture != null) {
                picture.save();
                diaryActivity.setPicture(picture);
                diaryActivity.update();
                LogAction.log(email, LogActionType.ADDEDPICTUREDIRECTLY);

                DiarySettingsManager.getInstance().retrieve(email).dateUpdate(diaryActivity.getDate());
                return redirect(routes.Diary.viewActivity(id));
            } else {
                return selectFromGalleryPage(id, pictureFactory.getLatestError());
            }
        } else {
            return selectFromGalleryPage(id, Messages.get("error.pleaseAddFile"));
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

        List<DiaryActivityToHTML> activities = DiaryActivityToHTML.fromListToList(diaryActivities);

        JsonNode jsonActivities = toJson(activities);

        return ok(jsonActivities);
    }

    public static Result deleteActivity(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

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
        DiaryActivityType diaryActivityType = DiaryActivityType.byId(activity.getType().getId());
        diaryActivityType.removeDiaryActivity(activity);
        diaryActivityType.update();

        if(activity.hasPicture()) {
            PictureFactory.deletePictureFromActivity(activity);
        }

        activity.setUser(null);
        activity.setType(null);
        activity.delete();

        //Log user behavior
        LogAction.log(email, LogActionType.DELETEACTIVITY);
        return redirect(routes.Diary.calendar());
    }

    public static Result updateActivity(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Check if user has access to view this activity
        User user = User.byEmail(email);
        DiaryActivity activity = DiaryActivity.byID(id);
        if(activity == null){
            return forbidden();
        }
        if(!activity.getUser().equals(user)){
            return forbidden();
        }

        //Retrieve data from input elements on webpage
        Form<DiaryActivity> diaryActivityForm = form(DiaryActivity.class).bindFromRequest();
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("picture_file");
        DynamicForm requestData = form().bindFromRequest();

        if(requestData.get("diaryActivityType") == null){
            return badRequest(diary_add_diaryActivity.render(user, diaryActivityForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), "", Messages.get("error.notypeselected")));
        }
        DiaryActivityType diaryActivityType = DiaryActivityType.byId(Integer.valueOf(requestData.get("diaryActivityType")));

        if (diaryActivityForm.hasErrors()) {
            return badRequest(diary_update_diaryActivity.render(User.byEmail(email), diaryActivityForm, new DiaryActivityToHTML(activity), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), diaryActivityType.getName(), ""));
        } else {
            //Retrieve the activity from the form
            DiaryActivity updateActivity = diaryActivityForm.get();
            updateActivity.setType(diaryActivityType);
            updateActivity.update(id);
            DiarySettingsManager.getInstance().retrieve(email).dateUpdate(updateActivity.getDate());
            //If a file is added
            if (filePart != null) {
                //Retrieve, move and store image file to disk and save picture object
                DiaryActivity updatedApartFromPictureDiaryActivity = DiaryActivity.byID(id);
                PictureFactory pictureFactory = new PictureFactory();
                Picture picture = pictureFactory.processUploadedFile(filePart, updatedApartFromPictureDiaryActivity);
                if (picture != null) {
                    picture.save();
                    updatedApartFromPictureDiaryActivity.setPicture(picture);
                    updatedApartFromPictureDiaryActivity.update();
                    LogAction.log(email, LogActionType.UPDATEACTIVITY);

                    return redirect(routes.Diary.viewActivity(id));
                } else {
                    diaryActivityForm.reject(pictureFactory.getLatestError());
                    return badRequest(diary_update_diaryActivity.render(User.byEmail(email), diaryActivityForm, new DiaryActivityToHTML(activity), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), diaryActivityType.getName(), ""));
                }
            } else {
                LogAction.log(email, LogActionType.UPDATEACTIVITY);
                return redirect(routes.Diary.viewActivity(id));
            }
        }
    }

    public static Result deletePictureFromActivity(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");


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
            PictureFactory.deletePictureFromActivity(activity);
        }

        LogAction.log(email, LogActionType.DELETEPICTUREFROMACTIVITY);
        return redirect(routes.Diary.updateActivityPage(id));
    }

    public static Result unlinkPictureFromActivity(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

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

        LogAction.log(email, LogActionType.UNLINKPICTUREFROMACTIVITY);
        return redirect(routes.Diary.updateActivityPage(id));
    }

    public static Result linkPictureToActivity(int activityID, int pictureID){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Check permissions
        User user = User.byEmail(email);
        DiaryActivity activity = DiaryActivity.byID(activityID);
        DiarySettingsManager.getInstance().retrieve(email).dateUpdate(activity.getDate());
        Picture picture = Picture.byID(pictureID);
        if (activity == null || picture == null){
            return selectFromGalleryPage(activityID, Messages.get("error.activityNotFound"));
        }
        if (!user.equals(activity.getUser()) || !user.equals(picture.getUser())){
            return forbidden();
        }
        if(activity.hasPicture()){
            return selectFromGalleryPage(activityID, Messages.get("error.hasAlreadyPicture"));
        }

        picture.setDiaryActivity(activity);
        picture.update();
        activity.setPicture(picture);
        activity.update();

        //Log user behavior
        LogAction.log(email, LogActionType.LINKPICTURETOACTIVITY);
        return redirect(routes.Diary.viewActivity(activityID));
    }

    public static Result deletePictureFromGallery(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Check if user has access to view this picture
        User user = User.byEmail(email);
        Picture picture = Picture.byID(id);
        if(picture == null){
            return forbidden();
        }
        if(!user.equals(picture.getUser())){
            return forbidden();
        }

        PictureFactory.deletePictureFromGallery(picture);

        LogAction.log(email, LogActionType.DELETEPICTUREFROMGALLERY);
        return redirect(routes.Diary.gallery());

    }

    public static Result addDiaryActivityType(String source, int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        if(source == null){
            return badRequest();
        }
        boolean isUpdate = false;
        if(source.equalsIgnoreCase(Messages.get("page.diary.diaryActivityType.source.updateActivity"))){
            if (DiaryActivity.byID(id) == null){
                return badRequest();
            }
            isUpdate = true;
        }

        DynamicForm requestData = form().bindFromRequest();
        String name = requestData.get("name");
        String color = requestData.get("colorInput");

        if(name == null || color == null || name.isEmpty()){
            if(isUpdate){
                return updateActivityPage(id, Messages.get("error.nonameorcolor"));
            } else {
                return addActivityPage(Messages.get("error.nonameorcolor"));
            }
        }

        DiaryActivityTypeManager.createDiaryActivityType(User.byEmail(email), name, color);

        if(isUpdate){
            return redirect(routes.Diary.updateActivity(id));
        } else {
            return redirect(routes.Diary.addActivityPage());
        }
    }

    public static Result removeDiaryActivityType(String source, int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));

        if(source == null){
            return badRequest();
        }
        boolean isUpdate = false;
        if(source.equalsIgnoreCase(Messages.get("page.diary.diaryActivityType.source.updateActivity"))){
            if (DiaryActivity.byID(id) == null){
                return badRequest();
            }
            isUpdate = true;
        }

        DynamicForm requestData = form().bindFromRequest();
        String typeIdString = requestData.get("diaryActivityType");
        if(typeIdString == null || typeIdString.isEmpty()){
            if(isUpdate){
                return updateActivityPage(id, Messages.get("error.notypeselected"));
            } else {
                return addActivityPage(Messages.get("error.notypeselected"));
            }
        }

        if(!DiaryActivityTypeManager.removeDiaryActivity(user, Integer.valueOf(typeIdString))){
            if(isUpdate){
                return updateActivityPage(id, Messages.get("error.cannotBeRemoved"));
            } else {
                return addActivityPage(Messages.get("error.cannotBeRemoved"));
            }
        }

        if(isUpdate){
            return redirect(routes.Diary.updateActivity(id));
        } else {
            return redirect(routes.Diary.addActivityPage());
        }
    }
}
