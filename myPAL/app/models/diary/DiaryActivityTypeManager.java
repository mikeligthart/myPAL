package models.diary;

import com.avaje.ebean.Ebean;
import controllers.Diary;
import controllers.routes;
import models.User;
import play.Logger;
import play.i18n.Messages;

import java.util.ArrayList;
import java.util.List;

/**
 * myPAL
 * Purpose: [ENTER PURPOSE]
 * <p>
 * Developped for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 26-8-2015
 */
public class DiaryActivityTypeManager {

    public static List<DiaryActivityType> retrieveDiaryActivityTypes(User user){
        List<DiaryActivityType> diaryActivityTypes = DiaryActivityType.byUser(user);
        if (diaryActivityTypes == null || diaryActivityTypes.isEmpty()){
            loadStandardTypes(user);
            diaryActivityTypes = DiaryActivityType.byUser(user);
        }
        return diaryActivityTypes;
    }

    public static void loadStandardTypes(User user){
        DiaryActivityType school = new DiaryActivityType(Messages.get("page.diary.calendar.activitytype.SCHOOL"), routes.Assets.at("images/school_icon.png").url(), "#308dd4", user);
        school.save();
        DiaryActivityType sport = new DiaryActivityType(Messages.get("page.diary.calendar.activitytype.SPORT"), routes.Assets.at("images/sport_icon.png").url(), "#8dd430", user);
        sport.save();
        DiaryActivityType meal = new DiaryActivityType(Messages.get("page.diary.calendar.activitytype.MEAL"), routes.Assets.at("images/meal_icon.png").url(), "#d4308d", user);
        meal.save();
        DiaryActivityType other = new DiaryActivityType(Messages.get("page.diary.calendar.activitytype.OTHER"), routes.Assets.at("images/other_icon.png").url(), "#d47730", user);
        other.save();
    }
}
