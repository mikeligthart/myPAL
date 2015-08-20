package models.diary;

import org.apache.commons.lang3.builder.EqualsBuilder;
import play.Logger;
import play.i18n.Messages;

/**
 * Created by ligthartmeu on 8-7-2015.
 */
public enum DiaryActivityType {
    SCHOOL, MEAL, SPORT, OTHER;

    public String toString(){
        switch(this) {
            case SCHOOL:
                return Messages.get("page.diary.calendar.activitytype.SCHOOL");
            case MEAL:
                return Messages.get("page.diary.calendar.activitytype.MEAL");
            case SPORT:
                return Messages.get("page.diary.calendar.activitytype.SPORT");
            case OTHER:
            default:
                return Messages.get("page.diary.calendar.activitytype.OTHER");
        }
    }

    public static DiaryActivityType fromString(String type){
        Logger.debug("[DiaryActivityType > fromString] input = " + type);
        if(type.equalsIgnoreCase(Messages.get("page.diary.calendar.activitytype.SCHOOL"))){
            return DiaryActivityType.SCHOOL;
        } else if(type.equalsIgnoreCase(Messages.get("page.diary.calendar.activitytype.MEAL"))){
            return DiaryActivityType.MEAL;
        } else if(type.equalsIgnoreCase(Messages.get("page.diary.calendar.activitytype.SPORT"))){
            return DiaryActivityType.SPORT;
        } else{
            return null;
        }
    }
}
