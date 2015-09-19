package views.interfaces;

import models.diary.measurement.DiaryMeasurement;
import models.diary.measurement.Glucose;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * myPAL
 * Purpose: [ENTER PURPOSE]
 * Developed for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 18-9-2015
 */
public class MeasurementToHTML {

    private static final String defaultColor = "#006400";
    private static final String glucoseColor = "#006400";
    private static final String insulinColor = "#ff8c00";
    private static final String carbohydrateColor = "#9932cc";

    private int id;
    private int startHour, startMin, endHour, endMin;
    private String type, value, daypart, comment, color;

    private boolean isGlusose;

    public MeasurementToHTML(DiaryMeasurement measurement){
        id = measurement.getId();
        startHour = measurement.getStarttime().toLocalTime().getHour();
        startMin = measurement.getStarttime().toLocalTime().getMinute();
        endHour = measurement.getEndtime().toLocalTime().getHour();
        endMin = measurement.getEndtime().toLocalTime().getMinute();
        type = measurement.getClass().getSimpleName();
        value = Double.toString(measurement.getValue());
        daypart = measurement.getDaypart().toString();

        if (measurement instanceof Glucose){
            isGlusose = true;
            Glucose glucose = (Glucose) measurement;
            comment = glucose.getComment();
            color = glucoseColor;
        } else {
            isGlusose = false;
            comment = "";
            color = defaultColor;
        }
    }

    public static List<MeasurementToHTML> fromListToList(List<DiaryMeasurement> measurements){
        List<MeasurementToHTML> htmlReadyMeasurements = new ArrayList<>();
        for(Iterator<DiaryMeasurement> it = measurements.iterator(); it.hasNext();){
            htmlReadyMeasurements.add(new MeasurementToHTML(it.next()));
        }
        return htmlReadyMeasurements;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMin() {
        return endMin;
    }

    public void setEndMin(int endMin) {
        this.endMin = endMin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDaypart() {
        return daypart;
    }

    public void setDaypart(String daypart) {
        this.daypart = daypart;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isGlusose() {
        return isGlusose;
    }

    public void setIsGlusose(boolean isGlusose) {
        this.isGlusose = isGlusose;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
