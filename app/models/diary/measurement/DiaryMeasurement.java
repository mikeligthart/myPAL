package models.diary.measurement;

import controllers.Diary;
import models.diary.DiaryItem;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

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

    @Constraints.Required(message = " ")
    private double value;

    @Constraints.Required
    @OneToMany
    @Enumerated
    private DayPart daypart;

    public DiaryMeasurement(){
        super();
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public DayPart getDaypart() {
        return daypart;
    }

    public void setDaypart(DayPart daypart) {
        this.daypart = daypart;
    }

}
