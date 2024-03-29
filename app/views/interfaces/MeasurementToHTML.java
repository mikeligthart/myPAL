package views.interfaces;

import com.typesafe.config.ConfigFactory;
import controllers.routes;
import models.diary.measurement.*;
import play.i18n.Messages;

import java.text.SimpleDateFormat;
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

    private int id;
    private int startHour, startMin, endHour, endMin;
    private String barDisplay, value, daypart, comment, color, viewURL, startTime, unit, displayName, date, user;
    private DiaryMeasurementType diaryMeasurementType;

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(ConfigFactory.load().getString("date.format"));
    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat(ConfigFactory.load().getString("time.format"));

    @Override
    public String toString(){
        StringBuilder output = new StringBuilder();
        output.append(displayName).append("(" + id + ")\n").append("====").append("\n");
        output.append("user: ").append(user).append("\n");
        output.append("date: ").append(date).append("\n");
        output.append("from: ").append(startTime).append(" till: ").append(endHour).append(":").append(endMin).append("\n");
        output.append("daypart: ").append(daypart).append("\n");
        output.append("value: ").append(value).append(" ").append(unit).append("\n");
        output.append("comment: ").append(comment);
        return output.toString();
    }

    public MeasurementToHTML(DiaryMeasurement measurement){
        startTime = timeFormatter.format(measurement.getStarttime());
        startHour = measurement.getStarttime().toLocalTime().getHour();
        startMin = measurement.getStarttime().toLocalTime().getMinute();
        endHour = measurement.getEndtime().toLocalTime().getHour();
        endMin = measurement.getEndtime().toLocalTime().getMinute();
        value = Double.toString(measurement.getValue());
        daypart = measurement.getDaypart().toString();
        date = dateFormatter.format(measurement.getDate());
        user=measurement.getUser().getUserName();

        if (measurement instanceof Glucose){
            diaryMeasurementType = DiaryMeasurementType.GLUCOSE;
            displayName = Messages.get("page.diary.calendar.measurement.glucose");
            barDisplay = displayName.substring(0,1).toUpperCase();
            Glucose glucose = (Glucose) measurement;
            id = glucose.getId();
            comment = glucose.getComment();
            color = glucoseColor;
            unit = Messages.get("page.diary.calendar.measurement.glucoseunit");
        } else if (measurement instanceof Insulin) {
            diaryMeasurementType = DiaryMeasurementType.INSULIN;
            displayName = Messages.get("page.diary.calendar.measurement.insulin");
            barDisplay = displayName.substring(0,1).toUpperCase();
            Insulin insulin = (Insulin) measurement;
            id = insulin.getId();
            comment = insulin.getComment();
            color = insulinColor;
            unit = Messages.get("page.diary.calendar.measurement.insulinunit");
        } else {
            diaryMeasurementType = DiaryMeasurementType.OTHER;
            comment = "";
            color = defaultColor;
            unit = "";
            id=-1;
        }
        viewURL = routes.Diary.viewMeasurement(id, diaryMeasurementType.ordinal()).url();
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getBarDisplay() {
        return barDisplay;
    }

    public void setBarDisplay(String barDisplay) {
        this.barDisplay = barDisplay;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public DiaryMeasurementType getDiaryMeasurementType() {
        return diaryMeasurementType;
    }

    public void setDiaryMeasurementType(DiaryMeasurementType diaryMeasurementType) {
        this.diaryMeasurementType = diaryMeasurementType;
    }

    public String getViewURL() {
        return viewURL;
    }

    public void setViewURL(String viewURL) {
        this.viewURL = viewURL;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
