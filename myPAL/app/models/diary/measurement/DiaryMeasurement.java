package models.diary.measurement;

import models.diary.DiaryItem;

import javax.persistence.*;

/**
 * myPAL
 * Purpose: models the concept of diabetes type I related measurements
 *
 * Developped for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 21-8-2015
 */
@Entity
public abstract class DiaryMeasurement extends DiaryItem {

    private boolean value;
    private DayPart daypart;

    public DiaryMeasurement(){
        super();
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean isValue() {
        return value;
    }

    public DayPart getDaypart() {
        return daypart;
    }

    public void setDaypart(DayPart daypart) {
        this.daypart = daypart;
    }

    public static Finder<Integer, DiaryMeasurement> find = new Finder<Integer, DiaryMeasurement>(Integer.class, DiaryMeasurement.class);

}
