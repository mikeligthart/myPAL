package controllers;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.ConfigFactory;
import controllers.avatar.AvatarGestureFactory;
import models.UserMyPAL;
import models.UserType;
import controllers.avatar.AvatarBehaviorFactory;
import controllers.avatar.AvatarReasoner;
import models.avatar.behaviorDefinition.*;
import models.diary.activity.DiaryActivity;
import models.diary.activity.DiaryActivityTypeManager;
import models.diary.measurement.DiaryMeasurement;
import models.diary.measurement.Glucose;
import models.diary.measurement.Insulin;
import models.logging.LogAction;
import models.logging.LogAvatar;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.admin.*;
import views.html.controlFlow.no_access;
import views.interfaces.*;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

import static play.data.Form.form;
import static play.libs.Json.toJson;

/**
 * myPAL
 * Purpose: handles all the controller functions for the admin actions and pages
 *
 * Developed for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 21-8-2015
 */
public class Admin extends Controller {

    private static final long TIMEWINDOW = 15 * 60 * 1000;
    /* PAGES */

    /**
     *
     * @return the admin homepage
     */
    public static Result admin(){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }
        List<UserMyPAL> users = UserMyPAL.find.all();
        List<DiaryActivity> activities = DiaryActivity.find.all();
        int nLogs = LogAction.find.all().size();
        int nMeasurements = Glucose.find.findRowCount() + Insulin.find.findRowCount();
        int nBehaviors = AvatarBehavior.getCount();
        int nGestures = AvatarGesture.getCount();

        return ok(admin_home.render(users.size(), countOnlineUsers(users), activities.size(), nLogs, recentActivities(activities), nMeasurements, nBehaviors, nGestures));
    }

    private static int countOnlineUsers(List<UserMyPAL> users) {
        int nOnlineUsers = 0;
        long lowerTimeLimit = System.currentTimeMillis() - TIMEWINDOW;
        for(Iterator<UserMyPAL> it = users.iterator(); it.hasNext();){
            Timestamp lastActivity = it.next().getLastActivity();
            if (lastActivity != null && lastActivity.getTime() > lowerTimeLimit){
                nOnlineUsers+=1;
            }
        }
        return nOnlineUsers;
    }

    private static List<DiaryActivityToHTML> recentActivities(List<DiaryActivity> activities){
        List<DiaryActivityToHTML> recentActivities = new ArrayList<>();
        LocalDate lowerDateLimit = LocalDate.now().minusDays(3);
        for(Iterator<DiaryActivity> it = activities.iterator(); it.hasNext();){
            DiaryActivity tentativeActivity = it.next();
            LocalDate dateActivity = tentativeActivity.getDate().toLocalDate();
            if(dateActivity.isAfter(lowerDateLimit)){
                recentActivities.add(new DiaryActivityToHTML(tentativeActivity));
            }
        }
        return recentActivities;
    }

    /**
     * Page that lists all users and provides a form to add new users
     * @return admin users page
     */
    public static Result users(){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        Form<UserMyPAL> userForm = form(UserMyPAL.class);
        return ok(admin_user.render(userForm));

    }

    /**
     * Page that provides a pre-filled form to update an existing user
     * @param userName id for an existing user that needs to be updated
     * @return update page for a existing user
     */
    public static Result updatePageUser(String userName){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        UserMyPAL user = UserMyPAL.byUserName(session().get("userName"));
        UserMyPAL updateThisUser = UserMyPAL.byUserName(userName);
        Form<UserMyPAL> userForm = form(UserMyPAL.class);
        if (updateThisUser != null) {
            userForm = userForm.fill(updateThisUser);
            return ok(admin_user_update.render(userName, userForm, user.equals(updateThisUser)));
        } else {
            return forbidden();
        }
    }

    /**
     *
     * @param userName id for existing user that needs to be viewed
     * @return page with all info on selected existing user
     */
    public static Result viewUser(String userName){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        UserMyPAL viewUser = UserMyPAL.byUserName(userName);
        if (viewUser != null) {
            return ok(admin_user_view.render(new UserToHTML(viewUser)));
        } else {
            return forbidden();
        }
    }

    public static Result behavior(){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }
        return ok(admin_behavior.render());
    }

    public static Result addBehaviorPage(){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }
        List<AvatarGesture> gestures = AvatarGesture.find.all();
        List<AvatarHtmlType> htmlTypes = Arrays.asList(AvatarHtmlType.values());
        return ok(admin_behavior_add.render(gestures, htmlTypes));
    }

    public static Result gesture(){
        return gesture("");
    }

    private static Result gesture(String error){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        List<AvatarGesture> gestures = AvatarGesture.find.all();
        return ok(admin_gesture.render(gestures, error));
    }

     /* FUNCTIONS */

    /**
     *
     * @return JSonNode containing all existing users
     */
    public static Result getUsers(){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        List<UserToHTML> users = UserToHTML.fromListToList(UserMyPAL.find.all());
        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.set("data", toJson(users));
        return ok(data);
    }

    /**
     * Listener for a POST request. Saves a user from a form
     * @return redirect to admin users page
     */
    public static Result addUser(){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        Form<UserMyPAL> userForm = form(UserMyPAL.class).bindFromRequest();
        if (userForm.hasErrors()) {
            return badRequest(admin_user.render(userForm));
        } else {
            if(UserMyPAL.byUserName(userForm.data().get("userName")) != null){
                userForm.reject("userName", Messages.get("error.usernameregisteredalready"));
                return badRequest(admin_user.render(userForm));
            }
            UserMyPAL newUser = userForm.get();
            newUser.save();
            DiaryActivityTypeManager.loadStandardTypes(newUser);
            return redirect(routes.Admin.users());
        }
    }

    /**
     * Listener for a POST request. Updates an existing user from a form.
     * @param userName - id of user that needs to be updated
     * @return redirect to admin users page
     */
    public static Result updateUser(String userName){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        UserMyPAL user = UserMyPAL.byUserName(session().get("userName"));
        Form<UserMyPAL> userForm = form(UserMyPAL.class).bindFromRequest();
        UserMyPAL updateThisUser = UserMyPAL.byUserName(userName);
        if (userForm.hasErrors()) {
            return badRequest(admin_user_update.render(userName, userForm, user.equals(updateThisUser)));
        } else {
            UserMyPAL updatedUser = userForm.get();
            if(userForm.data().get("password").equalsIgnoreCase(Messages.get("page.general.dummypassword"))){
                updatedUser.setHashedPassword(updateThisUser.getPassword());
            }
            updatedUser.update();
            return redirect(routes.Admin.users());
        }
    }

    /**
     * Deletes provided existing user
     * @param userName - id for an existing user
     * @return ok 200
     */
    public static Result deleteUser(String userName) {
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        UserMyPAL user = UserMyPAL.byUserName(session().get("userName"));
        UserMyPAL deleteThisUser = UserMyPAL.byUserName(userName);
        if (user.equals(deleteThisUser)){
            return forbidden();
        }

        if (deleteThisUser != null) {
            deleteThisUser.delete();
            return ok();
        } else {
            return forbidden();
        }
    }

    /**
     *
     * @param userName id for existing user
     * @return JsonNode containing LogAction items for provided user
     */
    public static Result getUserLog(String userName){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        UserMyPAL userForLogs = UserMyPAL.byUserName(userName);
        if (userForLogs != null){
            List<LogActionToHTML> logs = LogActionToHTML.fromListToList(LogAction.find.where().eq("user", userForLogs).findList());
            ObjectNode data = JsonNodeFactory.instance.objectNode();
            data.set("data", toJson(logs));
            return ok(data);
        } else {
            return forbidden(no_access.render());
        }
    }

    public static Result getAvatarLog(String userName){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        UserMyPAL userForLogs = UserMyPAL.byUserName(userName);
        if (userForLogs != null){
            List<LogAvatarToHTML> logs = LogAvatarToHTML.fromListToList(LogAvatar.byUser(userForLogs));
            ObjectNode data = JsonNodeFactory.instance.objectNode();
            data.set("data", toJson(logs));
            return ok(data);
        } else {
            return forbidden(no_access.render());
        }
    }

    public static Result getUserActivities(String userName){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        UserMyPAL user = UserMyPAL.byUserName(userName);
        if (user != null){
            List<DiaryActivityToHTML> activities = DiaryActivityToHTML.fromListToList(DiaryActivity.byUser(user));
            ObjectNode data = JsonNodeFactory.instance.objectNode();
            data.set("data", toJson(activities));
            return ok(data);
        } else {
            return forbidden(no_access.render());
        }

    }

    public static Result getUserMeasurements(String userName){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        UserMyPAL user = UserMyPAL.byUserName(userName);
        if (user != null){
            List<DiaryMeasurement> measurements = new ArrayList<>();
            measurements.addAll(Glucose.byUser(user));
            measurements.addAll(Insulin.byUser(user));
            List<MeasurementToHTML> measurementToHTMLs = MeasurementToHTML.fromListToList(measurements);
            ObjectNode data = JsonNodeFactory.instance.objectNode();
            data.set("data", toJson(measurementToHTMLs));
            return ok(data);
        } else {
            return forbidden(no_access.render());
        }
    }

    public static Result downloadUserData(){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        List<UserMyPAL> users = UserMyPAL.find.all();
        String fileName = "privateData/temp/userdata.json";
        try{
            FileWriter file = new FileWriter(fileName);
            file.write(toJson(users).toString());
            file.close();
            return ok(new File(fileName));
        } catch (IOException e) {
            Logger.error("[Admin > downloadUserLog] IOException writing userdata " + e.getMessage());
            return noContent();
        }
    }

    public static Result downloadMeasurementData(){
        List<DiaryMeasurement> measurements = new LinkedList<>();
        measurements.addAll(Glucose.find.all());
        measurements.addAll(Insulin.find.all());

        String fileName = "privateData/temp/measurementdata.json";
        try{
            FileWriter file = new FileWriter(fileName);
            file.write(toJson(measurements).toString());
            file.close();
            return ok(new File(fileName));
        } catch (IOException e) {
            Logger.error("[Admin > downloadUserLog] IOException writing measurementdata " + e.getMessage());
            return noContent();
        }
    }

    public static Result getBehaviors(){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        AvatarReasoner.refresh();
        List<AvatarBehaviorToHTML> behaviors = AvatarBehaviorToHTML.fromListToList(AvatarBehavior.find.all());
        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.set("data", toJson(behaviors));
        return ok(data);
    }

    public static Result addBehavior(){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        AvatarReasoner.refresh();
        DynamicForm requestData = form().bindFromRequest();
        int gestureId = Integer.valueOf(requestData.get("gestureId"));
        List<String> lines = processLines(requestData.get("lines"));
        AvatarHtmlType avatarHtmlType = AvatarHtmlType.valueOf(requestData.get("avatarHtmlType"));
        AvatarBehaviorFactory.addBehavior(gestureId, lines, avatarHtmlType);

        return redirect(routes.Admin.behavior());
    }

    public static Result deleteBehavior(int id){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        if(AvatarBehaviorFactory.deleteBehavior(id)){
            return ok();
        } else {
            return forbidden();
        }
    }

    public static Result generateAudioTemplate(){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        String filePath = ConfigFactory.load().getString("private.data.location") + "audiotext.txt";
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "utf-8"));
        } catch (FileNotFoundException e) {
            Logger.error("[Admin > generateAudioTemplate] failed due to FileNotFoundException " + e.getLocalizedMessage());
            return noContent();
        } catch (UnsupportedEncodingException e) {
            Logger.error("[Admin > generateAudioTemplate] failed due to UnsupportedEncodingException " + e.getLocalizedMessage());
            return noContent();
        }

        AvatarReasoner.refresh();
        List<AvatarBehavior> behaviors = AvatarBehavior.find.all();
        for(AvatarBehavior behavior : behaviors){
            for(AvatarLine line : behavior.getAvatarLines()){
                line.checkIfComplete();
                if(!line.isComplete()){
                    try {
                        writer.write("speech." + behavior.getId() + "." + line.getVersion() + ".wav#"+ line.getLine());
                        writer.newLine();
                    } catch (IOException e) {
                        Logger.error("[Admin > generateAudioTemplate] failed due to IOException " + e.getLocalizedMessage());
                        return noContent();
                    }

                }
            }
        }
        try {
            writer.close();
        } catch (IOException e) {
            Logger.error("[Admin > generateAudioTemplate] failed to close file " + e.getLocalizedMessage());
            return noContent();
        }

        File audioTemplate = new File(filePath);
        if(audioTemplate.exists()){
            return ok(audioTemplate);
        } else {
            return noContent();
        }

    }

    public static Result addGesture(){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("gestureFile");
        DynamicForm requestData = form().bindFromRequest();
        int duration = Integer.valueOf(requestData.get("gestureDuration"));

        AvatarGestureFactory gestureFactory = new AvatarGestureFactory();
        if(gestureFactory.addGesture(filePart, duration)){
            return redirect(routes.Admin.gesture());
        } else {
            return gesture(gestureFactory.getLatestError());
        }
    }

    public static Result deleteGesture(int id){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        AvatarGestureFactory gestureFactory = new AvatarGestureFactory();
        if(gestureFactory.deleteGesture(id)){
            return gesture();
        } else {
            return gesture(gestureFactory.getLatestError());
        }
    }

    public static Result clearDatabaseOfBehaviors(){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        AvatarBehaviorFactory.clearDatabase();
        return ok("database cleared of behaviors");
    }


    /*
    Helper functions
     */
    private static List<String> processLines(String stringOfLines){
        List<String> processedLines = new ArrayList<>();
        Pattern delimiter = Pattern.compile(";\\s*");
        List<String> lines = Arrays.asList(stringOfLines.split(delimiter.pattern()));
        for(String line : lines){
            processedLines.add(processLine(line));
        }
        return processedLines;
    }

    private static String processLine(String line){
        line = line.trim();
        if(!Character.toString(line.charAt(line.length()-1)).matches("\\.|\\?|\\!")){
            line = line + ".";
        }
        return line;
    }

    /**
     * Private classes that check whether a user is logged in properly.
     */
    private static class AdminAuthentication {

        public static AdminAuthenticationResult authenticate(){
            if(session().isEmpty() || session().get("userName") == null){
                return new AdminAuthenticationResult(false, redirect(routes.Application.login()));
            }
            UserMyPAL user = UserMyPAL.byUserName(session().get("userName"));
            if(user.getUserType() != UserType.ADMIN){
                return new AdminAuthenticationResult(false, forbidden(no_access.render()));
            }
            return new AdminAuthenticationResult(true, null);
        }
    }

    private static class AdminAuthenticationResult{

        private boolean hasAcces;
        private Result denyAction;

        public AdminAuthenticationResult(boolean hasAcces, Result denyAction){
            this.hasAcces = hasAcces;
            this.denyAction = denyAction;
        }

        public boolean hasAcces() {
            return hasAcces;
        }

        public Result getDenyAction() {
            return denyAction;
        }
    }

}
