package models.diary.activity;

import controllers.routes;
import models.UserMyPAL;
import play.Logger;
import play.i18n.Messages;

import java.util.Iterator;
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

    public static List<DiaryActivityType> retrieveDiaryActivityTypes(UserMyPAL user){
        List<DiaryActivityType> diaryActivityTypes = DiaryActivityType.byUser(user);
        if (diaryActivityTypes == null || diaryActivityTypes.isEmpty()){
            loadStandardTypes(user);
            diaryActivityTypes = DiaryActivityType.byUser(user);
        }
        return diaryActivityTypes;
    }

    public static void loadStandardTypes(UserMyPAL user){
        DiaryActivityType school = DiaryActivityType.byNameAndUser("School", user);
        if(school == null) {
            school = new DiaryActivityType("School", routes.Assets.at("images/school_icon.png").url(), "#308dd4", user);
            school.save();
        }

        DiaryActivityType sport = DiaryActivityType.byNameAndUser("Sport", user);
        if(sport == null){
            sport = new DiaryActivityType("Sport", routes.Assets.at("images/sport_icon.png").url(), "#8dd430", user);
            sport.save();
        }

        DiaryActivityType meal = DiaryActivityType.byNameAndUser("Maaltijd", user);
        if(meal == null) {
            meal = new DiaryActivityType("Maaltijd", routes.Assets.at("images/meal_icon.png").url(), "#d4308d", user);
            meal.save();
        }

        DiaryActivityType other = DiaryActivityType.byNameAndUser("Overig", user);
        if(other == null){
            other = new DiaryActivityType("Overig", routes.Assets.at("images/other_icon.png").url(), "#d47730", user);
            other.save();
        }

    }

    public static void createDiaryActivityType(UserMyPAL user, String name, String color){
        name = capitalise(name);
        if(DiaryActivityType.byNameAndUser(name, user) == null){
            DiaryActivityType newDiaryActivityType = new DiaryActivityType(name, routes.Assets.at("images/other_icon.png").url(), color, user);
            newDiaryActivityType.save();
        }
    }

    public static boolean removeDiaryActivity(UserMyPAL user, int id){
        if(canBeRemoved(user, id)){
            DiaryActivityType type = DiaryActivityType.byId(id);
            List<DiaryActivity> diaryActivities = type.getActivities();
            for(Iterator<DiaryActivity> it = diaryActivities.iterator(); it.hasNext();){
                DiaryActivity activity = it.next();
                activity.setType(DiaryActivityType.byNameAndUser(Messages.get("page.diary.calendar.activitytype.other"), user));
                activity.update();
            }
            type.setUser(null);
            type.update();
            type.delete();
            return true;
        }
        return false;
    }

    public static boolean canBeRemoved(UserMyPAL user, int id){
        DiaryActivityType type = DiaryActivityType.byId(id);
        if(type == null){
            return false;
        }
        if(!user.equals(type.getUser()))
            return false;
        if(type.getName().equalsIgnoreCase(Messages.get("page.diary.calendar.activitytype.school")))
            return false;
        if(type.getName().equalsIgnoreCase(Messages.get("page.diary.calendar.activitytype.sport")))
            return false;
        if(type.getName().equalsIgnoreCase(Messages.get("page.diary.calendar.activitytype.meal")))
            return false;
        return !type.getName().equalsIgnoreCase(Messages.get("page.diary.calendar.activitytype.other"));

    }

    private static String capitalise(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public static DiaryActivityType getType(UserMyPAL user, String name){
        return DiaryActivityType.byNameAndUser(capitalise(name), user);
    }
}
