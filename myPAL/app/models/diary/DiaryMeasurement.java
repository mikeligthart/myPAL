package models.diary;

import javax.persistence.Entity;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Created by ligthartmeu on 15-7-2015.
 */
public class DiaryMeasurement extends DiaryItem {

    private DiaryMeasurementType type;
    private float value;

    public DiaryMeasurement(int id){
        super(id);
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
