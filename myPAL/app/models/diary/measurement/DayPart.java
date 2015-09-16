package models.diary.measurement;

import play.i18n.Messages;

/**
 * Created by ligthartmeu on 15-9-2015.
 */
public enum DayPart {
    SOBER, AFTERBREAKFAST, BEFORELUNCH, AFTERLUNCH, BEFOREDINNER, AFTERDINNER;

    public String toString(){
        switch (this){
            case SOBER: return Messages.get("page.diary.calendar.measurement.daypart.sober");
            case AFTERBREAKFAST: return Messages.get("page.diary.calendar.measurement.daypart.afterbreakfast");
            case BEFORELUNCH: return Messages.get("page.diary.calendar.measurement.daypart.beforelunch");
            case AFTERLUNCH: return Messages.get("page.diary.calendar.measurement.daypart.afterlunch");
            case BEFOREDINNER: return Messages.get("page.diary.calendar.measurement.daypart.beforedinner");
            case AFTERDINNER:
            default: return Messages.get("page.diary.calendar.measurement.daypart.afterdinner");
        }
    }
}
