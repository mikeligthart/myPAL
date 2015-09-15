package controllers;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.User;
import models.UserType;
import models.diary.activity.DiaryActivity;
import play.i18n.Messages;
import views.interfaces.DiaryActivityToHTML;
import views.interfaces.LogActionToHTML;
import views.interfaces.UserToHTML;
import models.logging.LogAction;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.admin_home;
import views.html.admin.admin_user_update;
import views.html.admin.admin_user_view;
import views.html.admin.admin_users;
import views.html.controlFlow.no_access;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

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
        List<User> users = User.find.all();
        List<DiaryActivity> activities = DiaryActivity.find.all();
        int nLogs = LogAction.find.all().size();

        return ok(admin_home.render(users.size(), countOnlineUsers(users), activities.size(), nLogs, recentActivities(activities)));
    }

    private static int countOnlineUsers(List<User> users) {
        int nOnlineUsers = 0;
        long lowerTimeLimit = System.currentTimeMillis() - TIMEWINDOW;
        for(Iterator<User> it = users.iterator(); it.hasNext();){
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

        Form<User> userForm = form(User.class);
        return ok(admin_users.render(userForm));

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

        User user = User.byEmail(session().get("email"));
        User updateThisUser = User.byEmail(email);
        Form<User> userForm = form(User.class);
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

        User viewUser = User.byEmail(email);
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

        List<UserToHTML> users = UserToHTML.fromListToList(User.find.all());
        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.put("data", toJson(users));
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

        Form<User> userForm = form(User.class).bindFromRequest();
        if (userForm.hasErrors()) {
            return badRequest(admin_users.render(userForm));
        } else {
            if(User.byEmail(userForm.data().get("email")) != null){
                userForm.reject("email", Messages.get("error.emailregisteredalready"));
            }
            User newUser = userForm.get();
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

        User user = User.byEmail(session().get("email"));
        Form<User> userForm = form(User.class).bindFromRequest();
        User updateThisUser = User.byEmail(email);
        if (userForm.hasErrors()) {
            return badRequest(admin_user_update.render(email, userForm, user.equals(updateThisUser)));
        } else {
            User updatedUser = userForm.get();
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

        User user = User.byEmail(session().get("email"));
        User deleteThisUser = User.byEmail(email);
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

        User userForLogs = User.byEmail(email);
        if (userForLogs != null){
            List<LogActionToHTML> logs = LogActionToHTML.fromListToList(LogAction.find.where().eq("user", userForLogs).findList());
            ObjectNode data = JsonNodeFactory.instance.objectNode();
            data.put("data", toJson(logs));
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

        User user = User.byEmail(email);
        if (user != null){
            List<DiaryActivityToHTML> activities = DiaryActivityToHTML.fromListToList(DiaryActivity.byUser(user));
            ObjectNode data = JsonNodeFactory.instance.objectNode();
            data.put("data", toJson(activities));
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
            User user = User.byEmail(session().get("email"));
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
