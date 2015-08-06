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
