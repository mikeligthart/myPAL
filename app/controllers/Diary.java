package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.goals.GoalFactory;
import models.UserMyPAL;
import controllers.avatar.AvatarReasoner;
import models.avatar.behaviorDefinition.AvatarBehavior;
import models.avatar.behaviorSelection.decisionInformation.AvatarTrigger;
import models.goals.Goal;
import models.goals.GoalTarget;
import models.goals.GoalType;
import util.DiarySettings;
import util.DiarySettingsManager;
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
import util.AppException;
import util.GluconlineClient;
import util.NoValidGluconlineIDException;
import util.PictureFactory;
import views.html.diary.calendar.activity.*;
import views.html.diary.calendar.diary_calendar;
import views.html.diary.calendar.measurement.*;
import views.html.diary.gallery.*;
import views.html.diary.goals.*;
import views.interfaces.DiaryActivityToHTML;
import views.interfaces.MeasurementToHTML;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
        if(session().isEmpty() || session().get("userName") == null){
            return redirect(routes.Application.login());
        }
        return calendar();
    }

    public static Result calendar(){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("userName") == null){
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        //Manage the right settings such as date for the calendar
        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(userName);
        Date date = Date.valueOf(diarySettings.getCalendarDate());

        //Retrieve the number of activities
        int DiaryItemSize = DiaryActivity.byUserAndDate(user, date).size() +
                Glucose.byUserAndDate(user, date).size() + Insulin.byUserAndDate(user, date).size();

        //Log user activity
        LogAction.log(userName, LogActionType.ACCESSCALENDAR);

        //Generate AvatarBehavior
        AvatarReasoner reasoner = AvatarReasoner.getReasoner(user);
        List<AvatarBehavior> avatarBehavior = reasoner.selectAvatarBehaviors(new AvatarTrigger(AvatarTrigger.PAGE));

        return ok(diary_calendar.render(user, diarySettings.getDateString(true), diarySettings.getDateString(false), DiaryItemSize, avatarBehavior));
    }

    public static Result goals() {
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        //Log user activity
        LogAction.log(userName, LogActionType.ACCESSGOALS);

        //Generate AvatarBehavior
        AvatarReasoner reasoner = AvatarReasoner.getReasoner(user);
        List<AvatarBehavior> avatarBehavior = reasoner.selectAvatarBehaviors(new AvatarTrigger(AvatarTrigger.PAGE));

        //Load goals
        List<Goal> pendingDaily = GoalFactory.getGoals(user, false, GoalType.DAILY);
        List<Goal> metDaily = GoalFactory.getGoals(user, true, GoalType.DAILY);
        List<Goal> pendingTotal = GoalFactory.getGoals(user, false, GoalType.TOTAL);
        List<Goal> metTotal = GoalFactory.getGoals(user, true, GoalType.TOTAL);

        return ok(diary_goals.render(user, pendingDaily, metDaily, pendingTotal, metTotal, avatarBehavior));
    }

    public static Result gallery(){
        return gallery("");
    }

    private static Result gallery(String error){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");

        //Retrieve the pictures belonging to the user
        UserMyPAL user = UserMyPAL.byUserName(userName);
        List<Picture> pictures = Picture.byUser(user, PictureSort.DATEASC);

        //If there is no error load the gallery else return badrequest
        if(error.isEmpty()) {
            LogAction.log(userName, LogActionType.ACCESSGALLERY);
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
        if(session().isEmpty() || session().get("userName") == null){
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        //Generate addActivity page
        Form<DiaryActivity> activityForm = form(DiaryActivity.class);

        //Log user activity
        LogAction.log(userName, LogActionType.ACCESSADDACTIVITYPAGE);

        return ok(diary_calendar_diaryActivity_add.render(user, activityForm, DiarySettingsManager.getInstance().retrieve(userName).getDateString(false), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), "", error));
    }

    public static Result togetherOrSelf(){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("userName") == null){
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        //Manage the right settings such as date for the calendar
        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(userName);
        Date date = Date.valueOf(diarySettings.getCalendarDate());

        //Retrieve the number of activities
        int DiaryItemSize = DiaryActivity.byUserAndDate(user, date).size() +
                Glucose.byUserAndDate(user, date).size() + Insulin.byUserAndDate(user, date).size();

        //Log user activity
        LogAction.log(userName, LogActionType.TOGETHERORSELF);

        //Generate AvatarBehavior
        AvatarReasoner reasoner = AvatarReasoner.getReasoner(user);
        List<AvatarBehavior> avatarBehavior = reasoner.selectAvatarBehaviors(new AvatarTrigger(AvatarTrigger.TOGETHERORSELF));

        return ok(diary_calendar.render(user, diarySettings.getDateString(true), diarySettings.getDateString(false), DiaryItemSize, avatarBehavior));
    }

    public static Result together(){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("userName") == null){
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        return ok(diary_calendar_diaryActivity_add_together.render(user));
    }

    public static Result addPicturePage(){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("userName") == null){
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");

        //Log user activity
        LogAction.log(userName, LogActionType.ACCESSADDPICTUREPAGE);

        return ok(diary_gallery_picture_add.render());
    }

    public static Result viewActivity(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        //Check if user has access to view this activity
        DiaryActivity activity = DiaryActivity.byID(id);
        if(activity == null){
            return forbidden();
        }
        if(!activity.getUser().equals(user)){
            return forbidden();
        }

        //Manage the right settings such as date for the calendar
        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(userName);

        //Log user behavior
        LogAction.log(userName, LogActionType.VIEWACTIVITY);

        //Generate AvatarBehavior
        AvatarReasoner reasoner = AvatarReasoner.getReasoner(user);
        List<AvatarBehavior> avatarBehavior = reasoner.selectAvatarBehaviors(new AvatarTrigger(AvatarTrigger.PAGE));

        return ok(diary_calendar_diaryActivity_view.render(user, diarySettings.getDateString(true), diarySettings.getDateString(false), new DiaryActivityToHTML(activity), avatarBehavior));
    }

    private static Result updateActivityPage(int id, String error){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");

        //Check if user has access to view this activity
        UserMyPAL user = UserMyPAL.byUserName(userName);
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
        LogAction.log(userName, LogActionType.ACCESSUPDATEACTIVITYPAGE);

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
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");

        UserMyPAL user = UserMyPAL.byUserName(userName);
        List<Picture> pictures = Picture.byUserOnlyUnlinked(user, PictureSort.DATEASC);

        if(error.isEmpty()){
            //Log user activity
            LogAction.log(userName, LogActionType.SELECTPICTUREFROMGALLRERYPAGE);

            return ok(diary_gallery_picture_select.render(user.getUserType(), pictures,"", id));
        } else {
            return ok(diary_gallery_picture_select.render(user.getUserType(), pictures, error, id));
        }

    }

    public static Result addPictureDirectlyPage(int id){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("userName") == null){
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");

        //Log user activity
        LogAction.log(userName, LogActionType.ACCESSADDPICTUREDIRECTLYPAGE);

        return ok(diary_gallery_picture_addDirect.render(id));
    }

    public static Result addDiaryActivityTypePage(String source, int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");

        //Log user activity
        LogAction.log(userName, LogActionType.ACCESSADDACTIVITYTYPEPAGE);

        return ok(diary_calendar_diaryActivityType_add.render(source, id));
    }

    public static Result removeDiaryActivityTypePage(String source, int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        UserMyPAL user = UserMyPAL.byUserName(session().get("userName"));
        List<DiaryActivityType> diaryActivityTypes = DiaryActivityType.byUser(user);

        //Log user activity
        LogAction.log(user.getUserName(), LogActionType.ACCESSADDACTIVITYTYPEPAGE);

        return ok(diary_calendar_diaryActivityType_remove.render(source, id, diaryActivityTypes));
    }

    public static Result selectMeasurement(){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        LogAction.log(userName, LogActionType.ACCESSSELECTMEASUREMENTPAGE);

        return ok(diary_calendar_measurement_select.render());
    }

    public static Result addGlucosePage(){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("userName") == null){
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        //Generate addActivity page
        Form<Glucose> glucoseForm = form(Glucose.class);

        //Log user activity
        LogAction.log(userName, LogActionType.ACCESSADDGLUCOSEPAGE);

        return ok(diary_calendar_glucose_add.render(user, glucoseForm, DiarySettingsManager.getInstance().retrieve(userName).getDateString(false)));
    }

    public static Result addInsulinPage(){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("userName") == null){
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        //Generate addActivity page
        Form<Insulin> insulinForm = form(Insulin.class);

        //Log user activity
        LogAction.log(userName, LogActionType.ACCESSADDINSULINPAGE);

        return ok(diary_calendar_insulin_add.render(user, insulinForm, DiarySettingsManager.getInstance().retrieve(userName).getDateString(false)));
    }

    public static Result viewMeasurement(int id, int measurementType) {
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        DiaryMeasurementType type = DiaryMeasurementType.fromInteger(measurementType);
        DiaryMeasurement measurement;
        switch(type){
            case GLUCOSE: measurement = Glucose.byID(id);
                break;
            case INSULIN: measurement = Insulin.byID(id);
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
        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(userName);

        LogAction.log(userName, LogActionType.VIEWMEASUREMENT);

        //Generate AvatarBehavior
        AvatarReasoner reasoner = AvatarReasoner.getReasoner(user);
        List<AvatarBehavior> avatarBehavior = reasoner.selectAvatarBehaviors(new AvatarTrigger(AvatarTrigger.PAGE));

        return ok(diary_calendar_measurement_view.render(user.getUserType(), diarySettings.getDateString(true), diarySettings.getDateString(false), new MeasurementToHTML(measurement), avatarBehavior));
    }

    public static Result updateGlucosePage(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);
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
        LogAction.log(userName, LogActionType.ACCESSUPDATEGLUCOSEPAGE);

        return ok(diary_calendar_glucose_update.render(user, glucoseForm, id));
    }

    public static Result updateInsulinPage(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

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
        LogAction.log(userName, LogActionType.ACCESSUPDATEINSULINPAGE);

        return ok(diary_calendar_insulin_update.render(user, insulinForm, id));
    }

    public static Result addGoalDailyPage(){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        LogAction.log(userName, LogActionType.ACCESSGOALADDDAILYPAGE);

        return ok(diary_goals_add_dailyGoal.render(user));
    }

    public static Result addGoalTotalPage(){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        LogAction.log(userName, LogActionType.ACCESSGOALADDTOTALPAGE);

        return ok(diary_goals_add_totalGoal.render(user));
    }


    /* FUNCTIONALITIES */

    public static Result calendarUpdate(String update){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("userName") == null){
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        //Update calendar based on date change
        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(userName);
        if(update.contentEquals("-")){
            LogAction.log(userName, LogActionType.UPDATECALENDARDOWN);
            diarySettings.dateMinusOne();
        } else if (update.contentEquals("+")){
            LogAction.log(userName, LogActionType.UPDATECALENDARUP);
            diarySettings.datePlusOne();
        } else {
            return forbidden();
        }
        Date date = Date.valueOf(diarySettings.getCalendarDate());

        //Retrieve the number of activities
        int diaryItemSize = DiaryActivity.byUserAndDate(user, date).size() +
                Glucose.byUserAndDate(user, date).size() + Insulin.byUserAndDate(user, date).size();


        //Generate AvatarBehavior
        AvatarReasoner reasoner = AvatarReasoner.getReasoner(user);
        List<AvatarBehavior> avatarBehavior = reasoner.selectAvatarBehaviors(new AvatarTrigger(AvatarTrigger.PAGE));

        return ok(diary_calendar.render(user, diarySettings.getDateString(true), diarySettings.getDateString(false), diaryItemSize, avatarBehavior));
    }

    public static Result calendarSet(String day, String month, String year){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("userName") == null){
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        //Update calendar based on date change
        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(userName);
        diarySettings.dateUpdate(day, month, year);
        Date date = Date.valueOf(diarySettings.getCalendarDate());

        //Retrieve the number of activities
        int diaryItemSize = DiaryActivity.byUserAndDate(user, date).size() +
                Glucose.byUserAndDate(user, date).size() + Insulin.byUserAndDate(user, date).size();

        //Log user activity
        LogAction.log(userName, LogActionType.UPDATECALENDARDIRECTLY);

        //Generate AvatarBehavior
        AvatarReasoner reasoner = AvatarReasoner.getReasoner(user);
        List<AvatarBehavior> avatarBehavior = reasoner.selectAvatarBehaviors(new AvatarTrigger(AvatarTrigger.PAGE));

        return ok(diary_calendar.render(user, diarySettings.getDateString(true), diarySettings.getDateString(false), diaryItemSize, avatarBehavior));
    }

    public static Result addActivity() {
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        //Retrieve data from input elements on webpage
        Form<DiaryActivity> diaryActivityForm = form(DiaryActivity.class).bindFromRequest();
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("picture_file");
        DynamicForm requestData = form().bindFromRequest();
        boolean addFromGallery = requestData.get("isaddfromgallery").equalsIgnoreCase("true");

        if(requestData.get("diaryActivityType") == null){
            return badRequest(diary_calendar_diaryActivity_add.render(user, diaryActivityForm, DiarySettingsManager.getInstance().retrieve(userName).getDateString(false), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), "", Messages.get("error.notypeselected")));
        }
        DiaryActivityType diaryActivityType = DiaryActivityType.byId(Integer.valueOf(requestData.get("diaryActivityType")));

        if (diaryActivityForm.hasErrors()) {
            return badRequest(diary_calendar_diaryActivity_add.render(user, diaryActivityForm, DiarySettingsManager.getInstance().retrieve(userName).getDateString(false), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), diaryActivityType.getName(), ""));
        } else {
            //Retrieve the activity from the form
            DiaryActivity newDiaryActivity = diaryActivityForm.get();
            //Link the activity to a user
            newDiaryActivity.setUser(UserMyPAL.byUserName(userName));
            //Add timestamp
            newDiaryActivity.setAdded(Timestamp.valueOf(LocalDateTime.now()));
            //Link the activity to a type
            newDiaryActivity.setType(diaryActivityType);
            DiarySettingsManager.getInstance().retrieve(userName).dateUpdate(newDiaryActivity.getDate());

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
                        LogAction.log(userName, LogActionType.ADDEDACTIVITY);
                        return redirect(routes.Diary.calendar());
                    } else {
                        diaryActivityForm.reject(pictureFactory.getLatestError());
                        return badRequest(diary_calendar_diaryActivity_add.render(user, diaryActivityForm, DiarySettingsManager.getInstance().retrieve(userName).getDateString(false), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), diaryActivityType.getName(), ""));
                    }
                } else {
                    newDiaryActivity.save();
                    LogAction.log(userName, LogActionType.ADDEDACTIVITY);
                    return redirect(routes.Diary.calendar());
                }
            }
        }
    }

    public static Result addPicture(){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("picture_file");

        //If a file is added
        if (filePart != null) {
            //Retrieve, move and store image file to disk and save picture object
            PictureFactory pictureFactory = new PictureFactory();
            Picture picture = pictureFactory.processUploadedFile(filePart, UserMyPAL.byUserName(userName), Date.valueOf(LocalDate.now()));
            if (picture != null) {
                picture.save();
                LogAction.log(userName, LogActionType.ADDEDPICTURE);
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
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");

        DiaryActivity diaryActivity = DiaryActivity.byID(id);
        if (diaryActivity == null){
            return selectFromGalleryPage(id, Messages.get("error.activityNotFound"));
        }

        //Check if user has access to manipulate this activity
        UserMyPAL user = UserMyPAL.byUserName(userName);
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
                LogAction.log(userName, LogActionType.ADDEDPICTUREDIRECTLY);

                DiarySettingsManager.getInstance().retrieve(userName).dateUpdate(diaryActivity.getDate());
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
        if(session().isEmpty() || session().get("userName") == null){
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");

        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(userName);
        List<DiaryActivity> diaryActivities = DiaryActivity.byUserAndDate(UserMyPAL.byUserName(userName), Date.valueOf(diarySettings.getCalendarDate()));

        List<DiaryActivityToHTML> activities = DiaryActivityToHTML.fromListToList(diaryActivities);

        JsonNode jsonActivities = toJson(activities);

        return ok(jsonActivities);
    }

    public static Result deleteActivity(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");

        //Check if user has access to view this activity
        UserMyPAL user = UserMyPAL.byUserName(userName);
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
        LogAction.log(userName, LogActionType.DELETEACTIVITY);
        return redirect(routes.Diary.calendar());
    }

    public static Result updateActivity(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        //Check if user has access to view this activity
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
            return badRequest(diary_calendar_diaryActivity_add.render(user, diaryActivityForm, DiarySettingsManager.getInstance().retrieve(userName).getDateString(false), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), "", Messages.get("error.notypeselected")));
        }
        DiaryActivityType diaryActivityType = DiaryActivityType.byId(Integer.valueOf(requestData.get("diaryActivityType")));


        if (diaryActivityForm.hasErrors()) {
            return badRequest(diary_calendar_diaryActivity_update.render(user, diaryActivityForm, new DiaryActivityToHTML(activity), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), diaryActivityType.getName(), ""));
        } else {
            //Retrieve the activity from the form
            DiaryActivity updateActivity = diaryActivityForm.get();
            updateActivity.setType(diaryActivityType);
            updateActivity.update(id);
            DiarySettingsManager.getInstance().retrieve(userName).dateUpdate(updateActivity.getDate());
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
                    LogAction.log(userName, LogActionType.UPDATEACTIVITY);

                    return redirect(routes.Diary.viewActivity(id));
                } else {
                    diaryActivityForm.reject(pictureFactory.getLatestError());
                    return badRequest(diary_calendar_diaryActivity_update.render(UserMyPAL.byUserName(userName), diaryActivityForm, new DiaryActivityToHTML(activity), DiaryActivityTypeManager.retrieveDiaryActivityTypes(user), diaryActivityType.getName(), ""));
                }
            } else {
                LogAction.log(userName, LogActionType.UPDATEACTIVITY);
                return redirect(routes.Diary.viewActivity(id));
            }
        }
    }

    public static Result deletePictureFromActivity(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");


        //Check if user has access to view this activity
        UserMyPAL user = UserMyPAL.byUserName(userName);
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

        LogAction.log(userName, LogActionType.DELETEPICTUREFROMACTIVITY);
        return redirect(routes.Diary.updateActivityPage(id));
    }

    public static Result unlinkPictureFromActivity(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");

        //Check if user has access to view this activity
        UserMyPAL user = UserMyPAL.byUserName(userName);
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

        LogAction.log(userName, LogActionType.UNLINKPICTUREFROMACTIVITY);
        return redirect(routes.Diary.updateActivityPage(id));
    }

    public static Result linkPictureToActivity(int activityID, int pictureID){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");

        //Check permissions
        UserMyPAL user = UserMyPAL.byUserName(userName);
        DiaryActivity activity = DiaryActivity.byID(activityID);
        DiarySettingsManager.getInstance().retrieve(userName).dateUpdate(activity.getDate());
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
        LogAction.log(userName, LogActionType.LINKPICTURETOACTIVITY);
        return redirect(routes.Diary.viewActivity(activityID));
    }

    public static Result deletePictureFromGallery(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");

        //Check if user has access to view this picture
        UserMyPAL user = UserMyPAL.byUserName(userName);
        Picture picture = Picture.byID(id);
        if(picture == null){
            return forbidden();
        }
        if(!user.equals(picture.getUser())){
            return forbidden();
        }

        PictureFactory.deletePictureFromGallery(picture);

        LogAction.log(userName, LogActionType.DELETEPICTUREFROMGALLERY);
        return redirect(routes.Diary.gallery());

    }

    public static Result addDiaryActivityType(String source, int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");

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

        DiaryActivityTypeManager.createDiaryActivityType(UserMyPAL.byUserName(userName), name, color);

        //Log user activity
        LogAction.log(userName, LogActionType.ADDEDACTIVITYTYPE);

        if(isUpdate){
            return redirect(routes.Diary.updateActivity(id));
        } else {
            return redirect(routes.Diary.addActivityPage());
        }
    }

    public static Result removeDiaryActivityType(String source, int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        UserMyPAL user = UserMyPAL.byUserName(session().get("userName"));

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

        //Log user activity
        LogAction.log(user.getUserName(), LogActionType.DELETEACTIVITYTYPE);

        if(isUpdate){
            return redirect(routes.Diary.updateActivity(id));
        } else {
            return redirect(routes.Diary.addActivityPage());
        }
    }

    public static Result addGlucose(){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        //Retrieve form data
        Form<Glucose> glucoseForm = form(Glucose.class).bindFromRequest();

        if (glucoseForm.hasErrors()){
            return badRequest(diary_calendar_glucose_add.render(user, glucoseForm, DiarySettingsManager.getInstance().retrieve(userName).getDateString(false)));
        } else {
            //Retrieve glucose object and save it
            Glucose glucose = glucoseForm.get();
            glucose.setUser(user);
            //Add timestamp
            glucose.setAdded(Timestamp.valueOf(LocalDateTime.now()));
            glucose.save();

            //Change the date to match the glucose object's date
            DiarySettingsManager.getInstance().retrieve(userName).dateUpdate(glucose.getDate());

            //Redirect to calendar page
            LogAction.log(userName, LogActionType.ADDEDGLUCOSE);


            return redirect(routes.Diary.calendar());
        }
    }

    public static Result addInsulin(){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        //Retrieve form data
        Form<Insulin> insulinForm = form(Insulin.class).bindFromRequest();

        if (insulinForm.hasErrors()){
            return badRequest(diary_calendar_insulin_add.render(user, insulinForm, DiarySettingsManager.getInstance().retrieve(userName).getDateString(false)));
        } else {
            //Retrieve glucose object and save it
            Insulin insulin = insulinForm.get();
            insulin.setUser(user);
            //Add timestamp
            insulin.setAdded(Timestamp.valueOf(LocalDateTime.now()));
            insulin.save();

            //Change the date to match the glucose object's date
            DiarySettingsManager.getInstance().retrieve(userName).dateUpdate(insulin.getDate());

            //Redirect to calendar page
            LogAction.log(userName, LogActionType.ADDEDINSULIN);
            return redirect(routes.Diary.calendar());
        }
    }

    public static Result getMeasurements(){
        //Check whether a user is logged in
        if(session().isEmpty() || session().get("userName") == null){
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);
        DiarySettings diarySettings = DiarySettingsManager.getInstance().retrieve(userName);
        Date date = Date.valueOf(diarySettings.getCalendarDate());


        List<DiaryMeasurement> diaryMeasurements = new ArrayList<>();
        diaryMeasurements.addAll(Glucose.byUserAndDate(user, date));
        diaryMeasurements.addAll(Insulin.byUserAndDate(user, date));

        List<MeasurementToHTML> measurements = MeasurementToHTML.fromListToList(diaryMeasurements);

        JsonNode jsonActivities = toJson(measurements);

        return ok(jsonActivities);
    }

    public static Result deleteMeasurement(int id, int measurementType) {
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

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
            case OTHER:
            default: return forbidden();
        }
        LogAction.log(userName, LogActionType.REMOVEDMEASUREMENT);
        return redirect(routes.Diary.calendar());
    }

    public static Result updateMeasurement(int id, int measurementType){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        DiaryMeasurementType type = DiaryMeasurementType.fromInteger(measurementType);
        switch (type) {
            case GLUCOSE:
                //Log user activity
                LogAction.log(userName, LogActionType.DELETEGLUCOSE);
                return redirect(routes.Diary.updateGlucosePage(id));
            case INSULIN:
                //Log user activity
                LogAction.log(userName, LogActionType.DELETEINSULIN);
                return redirect(routes.Diary.updateInsulinPage(id));
            case OTHER:
            default: return forbidden();
        }
    }

    public static Result updateGlucose(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

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
        LogAction.log(userName, LogActionType.UPDATEDGLUCOSE);

        DiarySettingsManager.getInstance().retrieve(userName).dateUpdate(updatedGlucose.getDate());
        return redirect(routes.Diary.viewMeasurement(id, DiaryMeasurementType.GLUCOSE.ordinal()));
    }

    public static Result updateInsulin(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

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
        LogAction.log(userName, LogActionType.UPDATEDINSULINE);

        DiarySettingsManager.getInstance().retrieve(userName).dateUpdate(updatedInsulin.getDate());
        return redirect(routes.Diary.viewMeasurement(id, DiaryMeasurementType.INSULIN.ordinal()));
    }

    public static Result gluconline(){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        try {
            GluconlineClient gluconlineClient = new GluconlineClient(user);
            JsonNode result = gluconlineClient.retrieve();
            if(result != null) {
                gluconlineClient.updateMeasurements(result);
                //Log user activity
                LogAction.log(userName, LogActionType.ADDEDWITHGLUCONLINE);
            }
        } catch (NoValidGluconlineIDException e) {
            Logger.error("[Diary > gluconline] NoValidGluconlineIDException: " + e.getLocalizedMessage());
        } catch (AppException e) {
            Logger.error("[Application > authenticate] AppException: " + e.getMessage());
        }

        return redirect(routes.Diary.calendar());
    }

    public static Result addGoalDaily(){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        //Retrieve information from html form
        DynamicForm requestData = form().bindFromRequest();
        GoalTarget target = GoalTarget.valueOf(requestData.get("target"));
        int targetValue = Integer.valueOf(requestData.get("targetValue"));


        //Create new goal
        Goal goal = new Goal();
        goal.setUser(user);
        goal.setGoalType(GoalType.DAILY);
        goal.setTarget(target);
        goal.setTargetValue(targetValue);
        goal.save();

        //Log action
        LogAction.log(userName, LogActionType.ADDEDGOALDAILY);

        //Return to goal overview page
        return redirect(routes.Diary.goals());
    }

    public static Result addGoalTotal(){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        //Retrieve information from html form
        DynamicForm requestData = form().bindFromRequest();
        GoalTarget target = GoalTarget.valueOf(requestData.get("target"));
        int targetValue = Integer.valueOf(requestData.get("targetValue"));

        //Create new goal
        Goal goal = new Goal();
        goal.setUser(user);
        goal.setGoalType(GoalType.TOTAL);
        goal.setTarget(target);
        goal.setTargetValue(targetValue);
        goal.save();

        //Log action
        LogAction.log(userName, LogActionType.ADDEDGOALTOTAL);

        //Return to goal overview page
        return redirect(routes.Diary.goals());
    }

    public static Result deleteGoal(int id){
        //Check whether a user is logged in
        if (session().isEmpty() || session().get("userName") == null) {
            return redirect(routes.Application.login());
        }
        String userName = session().get("userName");
        UserMyPAL user = UserMyPAL.byUserName(userName);

        //Check if user has access to view this activity
        Goal goal = Goal.byID(id);
        if(goal == null){
            return forbidden();
        }
        if(!goal.getUser().equals(user)) {
            return forbidden();
        }

        //Remove goal
        goal.setUser(null);
        goal.delete();

        //Log user behavior
        LogAction.log(userName, LogActionType.DELETEGOAL);

        return redirect(routes.Diary.goals());
    }
}
