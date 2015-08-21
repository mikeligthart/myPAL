package models.diary;

import com.fasterxml.jackson.annotation.JsonBackReference;
import models.User;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

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
public class DiaryMeasurement extends DiaryItem {

    private DiaryMeasurementType type;
    private float value;

    public DiaryMeasurement(){
        super();
    }

    public DiaryMeasurementType getType() {
        return type;
    }

    public void setType(DiaryMeasurementType type) {
        this.type = type;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public static Finder<Integer, DiaryMeasurement> find = new Finder<Integer, DiaryMeasurement>(Integer.class, DiaryMeasurement.class);

}
