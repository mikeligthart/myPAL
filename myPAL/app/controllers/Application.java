package controllers;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dialogue.Dialogue;
import models.Emotion;
import models.User;
import models.User.*;
import models.diary.Diary;
import models.diary.DiaryActivity;
import models.diary.DiaryActivityType;
import models.diary.interfaces.DiaryActivityToHTML;
import models.logging.LogActionType;
import models.UserType;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.*;
import views.html.diary.*;
import views.html.test.*;
import views.html.controlFlow.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static play.data.Form.form;
import static play.libs.Json.toJson;


public class Application extends Controller {


    private static final Map<String, Diary> listOfDiaries = new HashMap<>();
    private static final Dialogue dialogue = Dialogue.getInstance();

    /* CONTROL FLOW */
    public static Result login() {
        return ok(
                login.render(form(Login.class))
        );
    }

    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            session("email", loginForm.get().email);
            listOfDiaries.put(session().get("email"), new Diary());
            User user = User.byEmail(session().get("email"));
            user.addLogAction(LogActionType.LOGIN);
            user.update();
            return redirect(routes.Application.index());
        }
    }

    public static Result logout(){
        User user = User.byEmail(session().get("email"));
        user.addLogAction(LogActionType.LOGOFF);
        user.update();
        session().clear();
        listOfDiaries.remove(session().get("email"));
        return redirect(routes.Application.login());
    }

    public static Result index() {
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        return ok(greeting.render(dialogue.getGreeting(user.getFirstName())));
    }


    /*DIARY */
    /* Pages */
    public static Result diary(){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        return calendar();
    }

    public static Result calendar(){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        user.addLogAction(LogActionType.ACCESSCALENDAR);
        user.update();

        Diary diary = listOfDiaries.get(session().get("email"));

        DiaryActivity testDiaryActivity = new DiaryActivity(0);
        testDiaryActivity.setDate(Date.valueOf(LocalDate.now()));
        testDiaryActivity.setStarttime(new Time(13, 45, 0));
        testDiaryActivity.setEndtime(new Time(15, 30, 0));
        testDiaryActivity.setType(DiaryActivityType.SPORT);
        testDiaryActivity.setDescription("This is a test activity");
        Emotion emotion = new Emotion(0.1f, 0.1f, 0.1f);
        emotion.save();
        testDiaryActivity.setEmotion(emotion);
        testDiaryActivity.setPicture("/location/pic.png");
        testDiaryActivity.save();
        List<DiaryActivity> diaryActivities = DiaryActivity.find.where().eq("date", Date.valueOf(diary.getCalendarDate())).findList();
        //List<DiaryMeasurement> diaryMeasurements = DiaryMeasurement.find.where().eq("date", diary.getDateString(false)).findList();
        return ok(diary_calendar.render(diary.getDateString(true), diary.getDateString(false), DiaryActivityToHTML.fromListToList(diaryActivities)));
    }

    public static Result calendarUpdate(String update){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        Diary diary = listOfDiaries.get(session().get("email"));
        User user = User.byEmail(session().get("email"));
        user.addLogAction(LogActionType.BUTTONPRESS);
        user.update();

        if(update.contentEquals("-")){
            diary.dateMinusOne();
        } else if (update.contentEquals("+")){
            diary.datePlusOne();
        } else {
            return forbidden();
        }
        List<DiaryActivity> diaryActivities = DiaryActivity.find.where().eq("date",Date.valueOf(diary.getCalendarDate())).findList();
        return ok(diary_calendar.render(diary.getDateString(true), diary.getDateString(false), DiaryActivityToHTML.fromListToList(diaryActivities)));
    }

    public static Result calendarSet(String day, String month, String year){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        Diary diary = listOfDiaries.get(session().get("email"));

        diary.dateUpdate(day, month, year);
        List<DiaryActivity> diaryActivities = DiaryActivity.find.where().eq("date",Date.valueOf(diary.getCalendarDate())).findList();
        return ok(diary_calendar.render(diary.getDateString(true), diary.getDateString(false), DiaryActivityToHTML.fromListToList(diaryActivities)));
    }

    public static Result goals(){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        user.addLogAction(LogActionType.ACCESSGOALS);
        user.update();

        return ok(diary_goals.render());
    }

    /* ADMIN */
    /* Pages */
    public static Result admin(){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(user.getUserType() != UserType.ADMIN){
            return forbidden(no_access.render());
        }

        return ok(admin_home.render());
    }

    public static Result users(){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(user.getUserType() != UserType.ADMIN){
            return forbidden(no_access.render());
        }
        Form<User> userForm = form(User.class);
        List<User> users = User.find.all();
        return ok(admin_users.render(userForm, users));

    }

    public static Result updatePageUser(String email){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(user.getUserType() != UserType.ADMIN){
            return forbidden(no_access.render());
        }

        User updateThisUser = User.byEmail(email);
        Form<UserMutable> userForm = form(User.UserMutable.class);
        if (updateThisUser != null) {
            userForm = userForm.fill(updateThisUser.getMutables());
            return ok(admin_user_update.render(email, userForm));
        } else {
            return forbidden();
        }
    }

    /* Functionalities */
    public static Result getUsers(){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(user.getUserType() != UserType.ADMIN){
            return forbidden(no_access.render());
        }
        List<User> users = User.find.all();
        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.put("data", toJson(users));
        return ok(data);
    }

    public static Result addUser(){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(user.getUserType() != UserType.ADMIN){
            return forbidden(no_access.render());
        }

        Form<User> userForm = form(User.class).bindFromRequest();
        List<User> users = User.find.all();
        if (userForm.hasErrors()) {
            return badRequest(admin_users.render(userForm, users));
        } else {
            User newUser = userForm.get();
            newUser.save();
            return redirect(routes.Application.users());
        }
    }

    public static Result updateUser(String email){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(user.getUserType() != UserType.ADMIN){
            return forbidden(no_access.render());
        }

        Form<UserMutable> userForm = form(UserMutable.class).bindFromRequest();
        if (userForm.hasErrors()) {
            Logger.debug("error");
            return badRequest(admin_user_update.render(email, userForm));
        } else {
            Logger.debug("succes");
            User updateUser = User.byEmail(email);
            updateUser.updateFromMutables(userForm.get());
            updateUser.update();
            return redirect(routes.Application.users());
        }
    }

    public static Result deleteUser(String email) {
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        if(user.getUserType() != UserType.ADMIN){
            return forbidden(no_access.render());
        }

        User deleteThisUser = User.byEmail(email);
        if (deleteThisUser != null) {
            deleteThisUser.delete();
            return ok();
        } else {
            return forbidden();
        }
    }

    /* TEST PAGES */
    public static Result dataTest(){
        return ok(dataTest.render());
    }

    public static Result showBootstrap() {
        return ok(bootstrap.render("Hello World!"));
    }

    public static Result googleTest() {
        return ok(google_visualization.render());
    }

    /* AVATAR */
}
