package views.interfaces;

import com.typesafe.config.ConfigFactory;
import controllers.routes;
import models.diary.DiaryActivity;
import models.diary.DiaryActivityType;
import models.diary.Emotion;
import models.diary.Picture;
import play.Logger;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ligthartmeu on 17-7-2015.
 */
public class DiaryActivityToHTML {

    private int id;
    private int startHour, startMin, endHour, endMin;
    private String type, description, emotion, date, emotionPicture, picture, color, viewURL, startTime, endTime;
    private boolean hasPicture;

    private static SimpleDateFormat formatter = new SimpleDateFormat(ConfigFactory.load().getString("date.format"));

    public DiaryActivityToHTML(DiaryActivity diaryActivity){
        this.id = diaryActivity.getId();
        this.startHour = diaryActivity.getStarttime().toLocalTime().getHour();
        this.startMin = diaryActivity.getStarttime().toLocalTime().getMinute();
        this.endHour = diaryActivity.getEndtime().toLocalTime().getHour();
        this.endMin = diaryActivity.getEndtime().toLocalTime().getMinute();
        this.type = diaryActivity.getType().toString();
        this.description = diaryActivity.getDescription();
        this.emotion = diaryActivity.getEmotion().name();
        this.emotionPicture = emotionToPicture(diaryActivity.getEmotion());
        this.date = formatter.format(diaryActivity.getDate());
        this.picture = retrievePictureURL(diaryActivity.getPicture());
        this.color = diaryActivityTypeToColor(diaryActivity.getType());
        this.viewURL = routes.Diary.viewActivity(diaryActivity.getId()).url();
        this.startTime = diaryActivity.getStarttime().toString();
        this.endTime = diaryActivity.getEndtime().toString();
        this.hasPicture = diaryActivity.hasPicture();
    }

    public static List<DiaryActivityToHTML> fromListToList(List<DiaryActivity> activities){
        List<DiaryActivityToHTML> htmlReadyAcitivies = new ArrayList<>();
        for(Iterator<DiaryActivity> it = activities.iterator(); it.hasNext();){
            htmlReadyAcitivies.add(new DiaryActivityToHTML(it.next()));
        }
        return htmlReadyAcitivies;
    }

    public static String dateToFormattedString(Date date){
        return formatter.format(date);
    }

    public static String dateToDay(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
    }

    public static String dateToMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return Integer.toString(cal.get(Calendar.MONTH) + 1);
    }

    public static String dateToYear(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return Integer.toString(cal.get(Calendar.YEAR));
    }

    public static String diaryActivityTypeToColor(DiaryActivityType diaryActivityType){
        switch (diaryActivityType){
            case SCHOOL:
                return "#308dd4";
            case MEAL:
                return "#8dd430";
            case SPORT:
                return "#d4308d";
            case OTHER:
            default:
                return "#d47730";
        }
    }

    public static String diaryActivityTypeToColor(String diaryActivityType){
        return diaryActivityTypeToColor(DiaryActivityType.fromString(diaryActivityType));
    }

    public static String emotionToPicture(String emotion){
        return emotionToPicture(Emotion.fromString(emotion));
    }

    public static String emotionToPicture(Emotion emotion){
        switch (emotion){
            case HAPPY:
                return routes.Assets.at("images/smiley1_happy.png").url();
            case SAD:
                return routes.Assets.at("images/smiley1_sad.png").url();
            case NEUTRAL:
            default:
                return routes.Assets.at("images/smiley1_neutral.png").url();
        }
    }

    public int getId() {
        return id;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMin() {
        return endMin;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getEmotion() {
        return emotion;
    }

    public String getEmotionPicture() {
        return emotionPicture;
    }

    public void setEmotionPicture(String emotionPicture) {
        this.emotionPicture = emotionPicture;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean hasPicture() {
        return hasPicture;
    }

    public void setHasPicture(boolean hasPicture) {
        this.hasPicture = hasPicture;
    }

    private String retrievePictureURL(Picture picture){
        String url = routes.Assets.at("images/no_picture.png").url();;
        if (picture != null)
            url = routes.Application.getPicture(picture.getThumbnail()).url();
        return url;
    }
}
