package controllers;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.UserMyPAL;
import models.UserType;
import models.diary.activity.DiaryActivity;
import models.logging.LogAction;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.admin_home;
import views.html.admin.admin_user;
import views.html.admin.admin_user_update;
import views.html.admin.admin_user_view;
import views.html.controlFlow.no_access;
import views.interfaces.DiaryActivityToHTML;
import views.interfaces.LogActionToHTML;
import views.interfaces.UserToHTML;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static play.data.Form.form;
import static play.libs.Json.toJson;

/**
 * myPAL
 * Purpose: handles all the controller functions for the admin actions and pages
 *
 * Developped for TNO.
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

        return ok(admin_home.render(users.size(), countOnlineUsers(users), activities.size(), nLogs, recentActivities(activities)));
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
     * @param email id for an existing user that needs to be updated
     * @return update page for a existing user
     */
    public static Result updatePageUser(String email){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        UserMyPAL user = UserMyPAL.byEmail(session().get("email"));
        UserMyPAL updateThisUser = UserMyPAL.byEmail(email);
        Form<UserMyPAL> userForm = form(UserMyPAL.class);
        if (updateThisUser != null) {
            userForm = userForm.fill(updateThisUser);
            return ok(admin_user_update.render(email, userForm, user.equals(updateThisUser)));
        } else {
            return forbidden();
        }
    }

    /**
     *
     * @param email id for existing user that needs to be viewed
     * @return page with all info on selected existing user
     */
    public static Result viewUser(String email){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        UserMyPAL viewUser = UserMyPAL.byEmail(email);
        if (viewUser != null) {
            return ok(admin_user_view.render(new UserToHTML(viewUser)));
        } else {
            return forbidden();
        }
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
            if(UserMyPAL.byEmail(userForm.data().get("email")) != null){
                userForm.reject("email", Messages.get("error.emailregisteredalready"));
                return badRequest(admin_user.render(userForm));
            }
            UserMyPAL newUser = userForm.get();
            newUser.save();
            return redirect(routes.Admin.users());
        }
    }

    /**
     * Listener for a POST request. Updates an existing user from a form.
     * @param email - id of user that needs to be updated
     * @return redirect to admin users page
     */
    public static Result updateUser(String email){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        UserMyPAL user = UserMyPAL.byEmail(session().get("email"));
        Form<UserMyPAL> userForm = form(UserMyPAL.class).bindFromRequest();
        UserMyPAL updateThisUser = UserMyPAL.byEmail(email);
        if (userForm.hasErrors()) {
            return badRequest(admin_user_update.render(email, userForm, user.equals(updateThisUser)));
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
     * @param email - id for an existing user
     * @return ok 200
     */
    public static Result deleteUser(String email) {
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        UserMyPAL user = UserMyPAL.byEmail(session().get("email"));
        UserMyPAL deleteThisUser = UserMyPAL.byEmail(email);
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
     * @param email id for existing user
     * @return JsonNode containing LogAction items for provided user
     */
    public static Result getUserLog(String email){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        UserMyPAL userForLogs = UserMyPAL.byEmail(email);
        if (userForLogs != null){
            List<LogActionToHTML> logs = LogActionToHTML.fromListToList(LogAction.find.where().eq("user", userForLogs).findList());
            ObjectNode data = JsonNodeFactory.instance.objectNode();
            data.set("data", toJson(logs));
            return ok(data);
        } else {
            return forbidden(no_access.render());
        }
    }

    public static Result getUserActivities(String email){
        AdminAuthenticationResult result = AdminAuthentication.authenticate();
        if(!result.hasAcces){
            return result.denyAction;
        }

        UserMyPAL user = UserMyPAL.byEmail(email);
        if (user != null){
            List<DiaryActivityToHTML> activities = DiaryActivityToHTML.fromListToList(DiaryActivity.byUser(user));
            ObjectNode data = JsonNodeFactory.instance.objectNode();
            data.set("data", toJson(activities));
            return ok(data);
        } else {
            return forbidden(no_access.render());
        }

    }

    /**
     * Private classes that check whether a user is logged in properly.
     */
    private static class AdminAuthentication {

        public static AdminAuthenticationResult authenticate(){
            if(session().isEmpty() || session().get("email") == null){
                return new AdminAuthenticationResult(false, redirect(routes.Application.login()));
            }
            UserMyPAL user = UserMyPAL.byEmail(session().get("email"));
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
