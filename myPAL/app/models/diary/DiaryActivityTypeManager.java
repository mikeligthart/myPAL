package models.diary;

import models.User;
import play.Logger;

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

    public static List<DiaryActivityType> getAllUniqueTypes(User user){
        Logger.debug("[DiaryActivityTypeManager > getAllUniqueTypes] " + user.getEmail());
        List<DiaryActivityType> uniques = DiaryActivityType.find.where().eq("user", null).findList();
        Logger.debug("[DiaryActivityTypeManager > getAllUniqueTypes] " + uniques.size());
        if(user != null) {
            uniques.addAll(DiaryActivityType.find.where().eq("user", user).eq("activity", null).findList());
        }
        return uniques;
    }

    public static DiaryActivityType getDiaryActivityType(String name, User creator){
        DiaryActivityType diaryActivityType;
        if(DiaryActivityTypeExists(name, creator)) {
            diaryActivityType = new DiaryActivityType(DiaryActivityType.byName(name, creator));
        } else if (DiaryActivityTypeExists(name, null)){
            diaryActivityType = new DiaryActivityType(DiaryActivityType.byName(name, null));
        } else {
            diaryActivityType = new DiaryActivityType(createNewActivityType(name, creator));
        }
        return diaryActivityType;
    }

    private static DiaryActivityType createNewActivityType(String name, User creator){
        DiaryActivityType diaryActivityArchType = new DiaryActivityType();
        diaryActivityArchType.setName(name);
        diaryActivityArchType.setIconLocation(DiaryActivityType.OTHERICONLOCATION);
        diaryActivityArchType.setActivity(null);
        diaryActivityArchType.setUser(creator);
        diaryActivityArchType.setColor(DiaryActivityType.OTHERCOLOR);
        diaryActivityArchType.save();
        return diaryActivityArchType;
    }

    public static boolean DiaryActivityTypeExists(DiaryActivityType activity){
        if(DiaryActivityType.find.byId(activity.getId()) != null){
            return true;
        }
        if(DiaryActivityType.byName(activity.getName(), activity.getUser()) != null){
            return true;
        }
        return false;
    }

    public static boolean DiaryActivityTypeExists(String name, User creator){
        if(DiaryActivityType.byName(name, creator) != null){
            return true;
        }
        return false;
    }
}
