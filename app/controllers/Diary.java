package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.UserMyPAL;
import models.diary.DiarySettings;
import models.diary.DiarySettingsManager;
import models.diary.activity.*;
import models.diary.measurement.*;
import models.logging.LogAction;
import models.logging.LogActionType;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import util.GluconlineClient;
import util.NoValidGluconlineIDException;
import util.PictureFactory;
import views.html.diary.calendar.activity.*;
import views.html.diary.calendar.diary_calendar;
import views.html.diary.calendar.measurement.*;
import views.html.diary.gallery.*;
import views.html.diary.goals.diary_goals;
import views.interfaces.DiaryActivityToHTML;
import views.interfaces.MeasurementToHTML;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        UserMyPAL user = UserMyPAL.byEmail(email);

        //Manage the right settings such as date for the calendar
        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(email);
        Date date = Date.valueOf(diarySettings.getCalendarDate());

        //Retrieve the number of activities
        int DiaryItemSize = DiaryActivity.byUserAndDate(user, date).size() +
                Glucose.byUserAndDate(user, date).size() + Insulin.byUserAndDate(user, date).size() + CarboHydrate.byUserAndDate(user, date).size();

        //Log user activity
        LogAction.log(email, LogActionType.ACCESSCALENDAR);

        return ok(diary_calendar.render(user.getUserType(), diarySettings.getDateString(true), diarySettings.getDateString(false), DiaryItemSize));
    }

    public static Result goals() {
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Log user activity
        LogAction.log(email, LogActionType.ACCESSGOALS);

        return ok(diary_goals.render(UserMyPAL.byEmail(email).getUserType()));
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
        UserMyPAL user = UserMyPAL.byEmail(email);
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
        UserMyPAL user = UserMyPAL.byEmail(email);

        //Generate addActivity page
        Form<DiaryActivity> activityForm = form(DiaryActivity.class);

        //Log user activity
        LogAction.log(email, LogActionType.ACCESSADDACTIVITYPAGE);

        return ok(diary_calendar_diaryActivity_add.render(user, activityForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), "", error));
    }

    public static Result addPicturePage(){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Log user activity
        LogAction.log(email, LogActionType.ACCESSADDPICTUREPAGE);

        return ok(diary_gallery_picture_add.render());
    }

    public static Result viewActivity(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Check if user has access to view this activity
        UserMyPAL user = UserMyPAL.byEmail(email);
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

        return ok(diary_calendar_diaryActivity_view.render(UserMyPAL.byEmail(email).getUserType(), diarySettings.getDateString(true), diarySettings.getDateString(false), new DiaryActivityToHTML(activity)));
    }

    private static Result updateActivityPage(int id, String error){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");

        //Check if user has access to view this activity
        UserMyPAL user = UserMyPAL.byEmail(email);
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

        return ok(diary_calendar_diaryActivity_update.render(user, activityForm, new DiaryActivityToHTML(activity), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), activity.getType().getName(), error));
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

        UserMyPAL user = UserMyPAL.byEmail(email);
        List<Picture> pictures = Picture.byUserOnlyUnlinked(user, PictureSort.DATEASC);

        if(error.isEmpty()){
            //Log user activity
            LogAction.log(email, LogActionType.SELECTPICTUREFROMGALLRERYPAGE);

            return ok(diary_gallery_picture_select.render(user.getUserType(), pictures,"", id));
        } else {
            return ok(diary_gallery_picture_select.render(user.getUserType(), pictures, error, id));
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

        return ok(diary_gallery_picture_addDirect.render(id));
    }

    public static Result addDiaryActivityTypePage(String source, int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }

        return ok(diary_calendar_diaryActivityType_add.render(source, id));
    }

    public static Result removeDiaryActivityTypePage(String source, int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        UserMyPAL user = UserMyPAL.byEmail(session().get("email"));
        List<DiaryActivityType> diaryActivityTypes = DiaryActivityType.byUser(user);

        return ok(diary_calendar_diaryActivityType_remove.render(source, id, diaryActivityTypes));
    }

    public static Result selectMeasurement(){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        LogAction.log(email, LogActionType.ACCESSSELECTMEASUREMENTPAGE);

        return ok(diary_calendar_measurement_select.render());
    }

    public static Result addGlucosePage(){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);

        //Generate addActivity page
        Form<Glucose> glucoseForm = form(Glucose.class);

        //Log user activity
        LogAction.log(email, LogActionType.ACCESSADDGLUCOSEPAGE);

        return ok(diary_calendar_glucose_add.render(user, glucoseForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false)));
    }

    public static Result addInsulinPage(){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);

        //Generate addActivity page
        Form<Insulin> insulinForm = form(Insulin.class);

        //Log user activity
        LogAction.log(email, LogActionType.ACCESSADDGLUCOSEPAGE);

        return ok(diary_calendar_insulin_add.render(user, insulinForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false)));
    }

    public static Result addCarboHydratePage(){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);

        //Generate addActivity page
        Form<CarboHydrate> carboHydrateForm = form(CarboHydrate.class);

        //Log user activity
        LogAction.log(email, LogActionType.ACCESSADDCARBOHYDRATEPAGE);

        return ok(diary_calendar_carboHydrate_add.render(user, carboHydrateForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false)));
    }

    public static Result viewMeasurement(int id, int measurementType) {
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);

        DiaryMeasurementType type = DiaryMeasurementType.fromInteger(measurementType);
        DiaryMeasurement measurement;
        switch(type){
            case GLUCOSE: measurement = Glucose.byID(id);
                break;
            case INSULIN: measurement = Insulin.byID(id);
                break;
            case CARBOHYDRATE: measurement = CarboHydrate.byID(id);
                break;
            case OTHER:
            default: measurement = null;
                break;
        }
        //Check if user has access to view this activity
        if(measurement == null){
            return forbidden();
        }
        if(!measurement.getUser().equals(user)){
            return forbidden();
        }

        //Manage the right settings such as date for the calendar
        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(email);

        LogAction.log(email, LogActionType.VIEWMEASUREMENT);

        return ok(diary_calendar_measurement_view.render(user.getUserType(), diarySettings.getDateString(true), diarySettings.getDateString(false), new MeasurementToHTML(measurement)));
    }

    public static Result updateGlucosePage(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);
        //Check if user has access to view this activity

        Glucose glucose = Glucose.byID(id);
        if(glucose == null){
            return forbidden();
        }
        if(!glucose.getUser().equals(user)){
            return forbidden();
        }

        Form<Glucose> glucoseForm = form(Glucose.class);
        glucoseForm = glucoseForm.fill(glucose);

        //Log user behavior
        LogAction.log(email, LogActionType.UPDATEGLUCOSE);

        return ok(diary_calendar_glucose_update.render(user, glucoseForm, id));
    }

    public static Result updateInsulinPage(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);

        //Check if user has access to view this activity
        Insulin insulin = Insulin.byID(id);
        if(insulin == null){
            return forbidden();
        }
        if(!insulin.getUser().equals(user)){
            return forbidden();
        }

        Form<Insulin> insulinForm = form(Insulin.class);
        insulinForm = insulinForm.fill(insulin);

        //Log user behavior
        LogAction.log(email, LogActionType.UPDATEINSULIN);

        return ok(diary_calendar_insulin_update.render(user, insulinForm, id));
    }

    public static Result updateCarboHydratePage(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);

        //Check if user has access to view this activity
        CarboHydrate carboHydrate = CarboHydrate.byID(id);
        if(carboHydrate == null){
            return forbidden();
        }
        if(!carboHydrate.getUser().equals(user)){
            return forbidden();
        }

        Form<CarboHydrate> carboHydrateForm = form(CarboHydrate.class);
        carboHydrateForm = carboHydrateForm.fill(carboHydrate);

        //Log user behavior
        LogAction.log(email, LogActionType.UPDATECARBOHYDRATE);

        return ok(diary_calendar_carboHydrate_update.render(user, carboHydrateForm, id));
    }


    /* FUNCTIONALITIES */

    public static Result calendarUpdate(String update){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);

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
        Date date = Date.valueOf(diarySettings.getCalendarDate());

        //Retrieve the number of activities
        int diaryItemSize = DiaryActivity.byUserAndDate(user, date).size() +
                Glucose.byUserAndDate(user, date).size();


        return ok(diary_calendar.render(user.getUserType(), diarySettings.getDateString(true), diarySettings.getDateString(false), diaryItemSize));
    }

    public static Result calendarSet(String day, String month, String year){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);

        //Update calendar based on date change
        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(email);
        diarySettings.dateUpdate(day, month, year);
        Date date = Date.valueOf(diarySettings.getCalendarDate());

        //Retrieve the number of activities
        int diaryItemSize = DiaryActivity.byUserAndDate(user, date).size() +
                Glucose.byUserAndDate(user, date).size();

        //Log user activity
        LogAction.log(email, LogActionType.UPDATECALENDARDIRECTLY);

        return ok(diary_calendar.render(user.getUserType(), diarySettings.getDateString(true), diarySettings.getDateString(false), diaryItemSize));
    }

    public static Result addActivity() {
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);

        //Retrieve data from input elements on webpage
        Form<DiaryActivity> diaryActivityForm = form(DiaryActivity.class).bindFromRequest();
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("picture_file");
        DynamicForm requestData = form().bindFromRequest();
        boolean addFromGallery = requestData.get("isaddfromgallery").equalsIgnoreCase("true");

        if(requestData.get("diaryActivityType") == null){
            return badRequest(diary_calendar_diaryActivity_add.render(user, diaryActivityForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), "", Messages.get("error.notypeselected")));
        }
        DiaryActivityType diaryActivityType = DiaryActivityType.byId(Integer.valueOf(requestData.get("diaryActivityType")));

        if (diaryActivityForm.hasErrors()) {
            return badRequest(diary_calendar_diaryActivity_add.render(user, diaryActivityForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), diaryActivityType.getName(), ""));
        } else {
            //Retrieve the activity from the form
            DiaryActivity newDiaryActivity = diaryActivityForm.get();
            //Link the activity to a user
            newDiaryActivity.setUser(UserMyPAL.byEmail(email));
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
                        return badRequest(diary_calendar_diaryActivity_add.render(user, diaryActivityForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), diaryActivityType.getName(), ""));
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
            Picture picture = pictureFactory.processUploadedFile(filePart, UserMyPAL.byEmail(email), Date.valueOf(LocalDate.now()));
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
        UserMyPAL user = UserMyPAL.byEmail(email);
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
        List<DiaryActivity> diaryActivities = DiaryActivity.byUserAndDate(UserMyPAL.byEmail(email), Date.valueOf(diarySettings.getCalendarDate()));

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
        UserMyPAL user = UserMyPAL.byEmail(email);
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
        UserMyPAL user = UserMyPAL.byEmail(email);
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
            return badRequest(diary_calendar_diaryActivity_add.render(user, diaryActivityForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), "", Messages.get("error.notypeselected")));
        }
        DiaryActivityType diaryActivityType = DiaryActivityType.byId(Integer.valueOf(requestData.get("diaryActivityType")));

        if (diaryActivityForm.hasErrors()) {
            return badRequest(diary_calendar_diaryActivity_update.render(UserMyPAL.byEmail(email), diaryActivityForm, new DiaryActivityToHTML(activity), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), diaryActivityType.getName(), ""));
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
                    return badRequest(diary_calendar_diaryActivity_update.render(UserMyPAL.byEmail(email), diaryActivityForm, new DiaryActivityToHTML(activity), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), diaryActivityType.getName(), ""));
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
        UserMyPAL user = UserMyPAL.byEmail(email);
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
        UserMyPAL user = UserMyPAL.byEmail(email);
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
        UserMyPAL user = UserMyPAL.byEmail(email);
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
        UserMyPAL user = UserMyPAL.byEmail(email);
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

        DiaryActivityTypeManager.createDiaryActivityType(UserMyPAL.byEmail(email), name, color);

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
        UserMyPAL user = UserMyPAL.byEmail(session().get("email"));

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

    public static Result addGlucose(){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);

        //Retrieve form data
        Form<Glucose> glucoseForm = form(Glucose.class).bindFromRequest();

        if (glucoseForm.hasErrors()){
            return badRequest(diary_calendar_glucose_add.render(user, glucoseForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false)));
        } else {
            //Retrieve glucose object and save it
            Glucose glucose = glucoseForm.get();
            glucose.setUser(user);
            glucose.save();

            //Change the date to match the glucose object's date
            DiarySettingsManager.getInstance().retrieve(email).dateUpdate(glucose.getDate());

            //Redirect to calendar page
            LogAction.log(email, LogActionType.ADDEDGLUCOSE);
            return redirect(routes.Diary.calendar());
        }
    }

    public static Result addInsulin(){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);

        //Retrieve form data
        Form<Insulin> insulinForm = form(Insulin.class).bindFromRequest();

        if (insulinForm.hasErrors()){
            return badRequest(diary_calendar_insulin_add.render(user, insulinForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false)));
        } else {
            //Retrieve glucose object and save it
            Insulin insulin = insulinForm.get();
            insulin.setUser(user);
            insulin.save();

            //Change the date to match the glucose object's date
            DiarySettingsManager.getInstance().retrieve(email).dateUpdate(insulin.getDate());

            //Redirect to calendar page
            LogAction.log(email, LogActionType.ADDEDINSULIN);
            return redirect(routes.Diary.calendar());
        }
    }

    public static Result addCarboHydrate(){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);

        //Retrieve form data
        Form<CarboHydrate> carboHydrateForm = form(CarboHydrate.class).bindFromRequest();

        if (carboHydrateForm.hasErrors()){
            return badRequest(diary_calendar_carboHydrate_add.render(user, carboHydrateForm, DiarySettingsManager.getInstance().retrieve(email).getDateString(false)));
        } else {
            //Retrieve glucose object and save it
            CarboHydrate carboHydrate = carboHydrateForm.get();
            carboHydrate.setUser(user);
            carboHydrate.save();

            //Change the date to match the glucose object's date
            DiarySettingsManager.getInstance().retrieve(email).dateUpdate(carboHydrate.getDate());

            //Redirect to calendar page
            LogAction.log(email, LogActionType.ADDEDCARBOHYDRATE);
            return redirect(routes.Diary.calendar());
        }
    }

    public static Result getMeasurements(){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);
        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(email);
        Date date = Date.valueOf(diarySettings.getCalendarDate());


        List<DiaryMeasurement> diaryMeasurements = new ArrayList<>();
        diaryMeasurements.addAll(Glucose.byUserAndDate(user, date));
        diaryMeasurements.addAll(Insulin.byUserAndDate(user, date));
        diaryMeasurements.addAll(CarboHydrate.byUserAndDate(user, date));

        List<MeasurementToHTML> measurements = MeasurementToHTML.fromListToList(diaryMeasurements);

        JsonNode jsonActivities = toJson(measurements);

        return ok(jsonActivities);
    }

    public static Result deleteMeasurement(int id, int measurementType) {
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);

        DiaryMeasurementType type = DiaryMeasurementType.fromInteger(measurementType);
        switch (type) {
            case GLUCOSE:
                Glucose glucose = Glucose.byID(id);
                //Check if user has access to view this activity
                if (glucose == null) {
                    return forbidden();
                }
                if (!glucose.getUser().equals(user)) {
                    return forbidden();
                }
                glucose.delete();
                break;
            case INSULIN:
                Insulin insulin = Insulin.byID(id);
                //Check if user has access to view this activity
                if (insulin == null) {
                    return forbidden();
                }
                if (!insulin.getUser().equals(user)) {
                    return forbidden();
                }
                insulin.delete();
                break;
            case CARBOHYDRATE:
                CarboHydrate carboHydrate = CarboHydrate.byID(id);
                //Check if user has access to view this activity
                if (carboHydrate == null) {
                    return forbidden();
                }
                if (!carboHydrate.getUser().equals(user)) {
                    return forbidden();
                }
                carboHydrate.delete();
                break;
            case OTHER:
            default: return forbidden();
        }
        LogAction.log(email, LogActionType.REMOVEDMEASUREMENT);
        return redirect(routes.Diary.calendar());
    }

    public static Result updateMeasurement(int id, int measurementType){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);

        DiaryMeasurementType type = DiaryMeasurementType.fromInteger(measurementType);
        switch (type) {
            case GLUCOSE: return redirect(routes.Diary.updateGlucosePage(id));
            case INSULIN: return redirect(routes.Diary.updateInsulinPage(id));
            case CARBOHYDRATE: return redirect(routes.Diary.updateCarboHydratePage(id));
            case OTHER:
            default: return forbidden();
        }
    }

    public static Result updateGlucose(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);

        //Check if user has access to view this activity
        Glucose glucose = Glucose.byID(id);
        if(glucose == null){
            return forbidden();
        }
        if(!glucose.getUser().equals(user)){
            return forbidden();
        }

        Form<Glucose> glucoseForm = form(Glucose.class).bindFromRequest();
        Glucose updatedGlucose = glucoseForm.get();
        updatedGlucose.update(id);
        LogAction.log(email, LogActionType.UPDATEDGLUCOSE);

        DiarySettingsManager.getInstance().retrieve(email).dateUpdate(updatedGlucose.getDate());
        return redirect(routes.Diary.viewMeasurement(id, DiaryMeasurementType.GLUCOSE.ordinal()));
    }

    public static Result updateInsulin(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);

        //Check if user has access to view this activity
        Insulin insulin = Insulin.byID(id);
        if(insulin == null){
            return forbidden();
        }
        if(!insulin.getUser().equals(user)){
            return forbidden();
        }

        Form<Insulin> insulinForm = form(Insulin.class).bindFromRequest();
        Insulin updatedInsulin = insulinForm.get();
        updatedInsulin.update(id);
        LogAction.log(email, LogActionType.UPDATEDINSULINE);

        DiarySettingsManager.getInstance().retrieve(email).dateUpdate(updatedInsulin.getDate());
        return redirect(routes.Diary.viewMeasurement(id, DiaryMeasurementType.INSULIN.ordinal()));
    }

    public static Result updateCarboHydrate(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);

        //Check if user has access to view this activity
        CarboHydrate carboHydrate = CarboHydrate.byID(id);
        if(carboHydrate == null){
            return forbidden();
        }
        if(!carboHydrate.getUser().equals(user)){
            return forbidden();
        }

        Form<CarboHydrate> glucoseForm = form(CarboHydrate.class).bindFromRequest();
        CarboHydrate updatedCarboHydrate = glucoseForm.get();
        updatedCarboHydrate.update(id);
        LogAction.log(email, LogActionType.UPDATEDCARBOHYDRATE);

        DiarySettingsManager.getInstance().retrieve(email).dateUpdate(updatedCarboHydrate.getDate());
        return redirect(routes.Diary.viewMeasurement(id, DiaryMeasurementType.CARBOHYDRATE.ordinal()));
    }

    public static Result gluconline(){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        String email = session().get("email");
        UserMyPAL user = UserMyPAL.byEmail(email);

        JsonNode result = null;
        try {
            GluconlineClient gluconlineClient = new GluconlineClient(user);
            result = gluconlineClient.retrieve();
            gluconlineClient.updateMeasurements(result);
        } catch (NoValidGluconlineIDException e) {
            Logger.error("[Diary > gluconline] NoValidGluconlineIDException: " + e.getLocalizedMessage());
        }
       return redirect(routes.Diary.calendar());
    }
}
