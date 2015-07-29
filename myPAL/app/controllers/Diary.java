package controllers;

import models.User;
import models.diary.DiaryActivity;
import models.diary.DiarySettings;
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

        DiarySettings diarySettings = Application.listOfDiaries.get(session().get("email"));
        List<DiaryActivity> diaryActivities = DiaryActivity.find.where().eq("date", Date.valueOf(diarySettings.getCalendarDate())).findList();
        //List<DiaryMeasurement> diaryMeasurements = DiaryMeasurement.find.where().eq("date", diarySettings.getDateString(false)).findList();
        return ok(diary_calendar.render(diarySettings.getDateString(true), diarySettings.getDateString(false), DiaryActivityToHTML.fromListToList(diaryActivities)));
    }

    public static Result goals() {
        if (session().isEmpty() || session().get("email") == null) {
            return redirect(routes.Application.login());
        }
        User user = User.byEmail(session().get("email"));
        user.addLogAction(LogActionType.ACCESSGOALS);
        user.update();

        return ok(diary_goals.render());
    }

    /* FUNCTIONALITIES */

    public static Result calendarUpdate(String update){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        DiarySettings diarySettings = Application.listOfDiaries.get(session().get("email"));
        User user = User.byEmail(session().get("email"));
        user.addLogAction(LogActionType.BUTTONPRESS);
        user.update();

        if(update.contentEquals("-")){
            diarySettings.dateMinusOne();
        } else if (update.contentEquals("+")){
            diarySettings.datePlusOne();
        } else {
            return forbidden();
        }
        List<DiaryActivity> diaryActivities = DiaryActivity.find.where().eq("date",Date.valueOf(diarySettings.getCalendarDate())).findList();
        return ok(diary_calendar.render(diarySettings.getDateString(true), diarySettings.getDateString(false), DiaryActivityToHTML.fromListToList(diaryActivities)));
    }

    public static Result calendarSet(String day, String month, String year){
        if(session().isEmpty() || session().get("email") == null){
            return redirect(routes.Application.login());
        }
        DiarySettings diarySettings = Application.listOfDiaries.get(session().get("email"));
        diarySettings.dateUpdate(day, month, year);
        List<DiaryActivity> diaryActivities = DiaryActivity.find.where().eq("date",Date.valueOf(diarySettings.getCalendarDate())).findList();
        return ok(diary_calendar.render(diarySettings.getDateString(true), diarySettings.getDateString(false), DiaryActivityToHTML.fromListToList(diaryActivities)));
    }
}
