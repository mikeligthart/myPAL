package models.diary;

import play.i18n.Messages;

/**
 * Created by ligthartmeu on 8-7-2015.
 */
public enum DiaryActivityType {
    SCHOOL, MEAL, SPORT, OTHER;

    public String toString(){
        switch(this) {
            case SCHOOL:
                return Messages.get("page.diary.calendar.activitytype.school");
            case MEAL:
                return Messages.get("page.diary.calendar.activitytype.meal");
            case SPORT:
                return Messages.get("page.diary.calendar.activitytype.sport");
            case OTHER:
            default:
                return Messages.get("page.diary.calendar.activitytype.other");
        }
    }

    public static DiaryActivityType fromString(String type){
        if(type.equalsIgnoreCase("school")){
            return DiaryActivityType.SCHOOL;
        } else if(type.equalsIgnoreCase("meal")){
            return DiaryActivityType.MEAL;
        } else if(type.equalsIgnoreCase("sport")){
            return DiaryActivityType.SPORT;
        } else{
            return DiaryActivityType.OTHER;
        }
    }
}
