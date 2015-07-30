package controllers;

import models.User;
import models.diary.DiaryActivity;
import models.diary.DiarySettings;
import models.diary.DiarySettingsManager;
import models.logging.LogAction;
import play.Logger;
import views.interfaces.DiaryActivityToHTML;
import models.logging.LogActionType;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.diary.diary_calendar;
import views.html.diary.diary_goals;

import java.sql.Date;
import java.util.List;

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
        List<DiaryActivity> diaryActivities = DiaryActivity.find.where().eq("date", Date.valueOf(diarySettings.getCalendarDate())).findList();
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
        List<DiaryActivity> diaryActivities = DiaryActivity.find.where().eq("date",Date.valueOf(diarySettings.getCalendarDate())).findList();

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
        List<DiaryActivity> diaryActivities = DiaryActivity.find.where().eq("date",Date.valueOf(diarySettings.getCalendarDate())).findList();

        return ok(diary_calendar.render(User.byEmail(email).getUserType(), diarySettings.getDateString(true), diarySettings.getDateString(false), DiaryActivityToHTML.fromListToList(diaryActivities)));
    }
}
