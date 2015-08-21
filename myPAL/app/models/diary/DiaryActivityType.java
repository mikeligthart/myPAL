package models.diary;

import org.apache.commons.lang3.builder.EqualsBuilder;
import play.Logger;
import play.i18n.Messages;

/**
 * myPAL
 * Purpose: enum that contains the different types a DiaryActivity can have
 *
 * Developped for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 21-8-2015
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
