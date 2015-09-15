package views.interfaces;

import com.typesafe.config.ConfigFactory;
import controllers.routes;
import models.diary.activity.DiaryActivity;
import models.diary.activity.Emotion;
import models.diary.activity.Picture;
import play.i18n.Messages;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * myPAL
 * Purpose: Interface class between a DiaryActivity and data suitable for HTML
 *
 * Developped for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 17-7-2015
 */
public class DiaryActivityToHTML {

    private int id;
    private int startHour, startMin, endHour, endMin;
    private String type, description, emotion, date, emotionPicture, picture, color, viewURL,
            startTime, endTime, hasPictureString, shortDescription, firstName, lastName, fullPicture, email,
            longDescription;
    private boolean hasPicture;

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(ConfigFactory.load().getString("date.format"));
    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat(ConfigFactory.load().getString("time.format"));
    private static final int shortDescriptionMaxLength = 25;
    private static final int longDescriptionMaxLength = 144;

    public DiaryActivityToHTML(DiaryActivity diaryActivity){
        this.id = diaryActivity.getId();
        this.startHour = diaryActivity.getStarttime().toLocalTime().getHour();
        this.startMin = diaryActivity.getStarttime().toLocalTime().getMinute();
        this.endHour = diaryActivity.getEndtime().toLocalTime().getHour();
        this.endMin = diaryActivity.getEndtime().toLocalTime().getMinute();
        this.type = diaryActivity.getType().getName();
        this.description = diaryActivity.getDescription();
        this.emotion = diaryActivity.getEmotion().name();
        this.emotionPicture = emotionToPicture(diaryActivity.getEmotion());
        this.date = dateFormatter.format(diaryActivity.getDate());
        this.picture = retrievePictureURL(diaryActivity.getPicture(), false);
        this.fullPicture =retrievePictureURL(diaryActivity.getPicture(), true);
        this.color = diaryActivity.getType().getColor();
        this.viewURL = routes.Diary.viewActivity(diaryActivity.getId()).url();
        this.startTime = timeFormatter.format(diaryActivity.getStarttime());
        this.endTime = timeFormatter.format(diaryActivity.getEndtime());
        this.hasPicture = diaryActivity.hasPicture();
        if(hasPicture) {
            hasPictureString = "<a href='" + fullPicture + "' data-toggle='lightbox' data-title='" + type +" - " + date + "'>" + Messages.get("page.general.yes") + "</a>";
        } else {
            hasPictureString = Messages.get("page.general.no");
        }
        if(description.length() <= shortDescriptionMaxLength){
            this.shortDescription = description;
        } else {
            this.shortDescription = "<a href='" + routes.Application.contentBox(description) +"' data-toggle='lightbox' data-title='" + Messages.get("page.diary.calendar.description") + "'>" + description.substring(0, shortDescriptionMaxLength) + "...</a>";
        }
        this.firstName = diaryActivity.getUser().getFirstName();
        this.lastName = diaryActivity.getUser().getLastName();
        this.email = diaryActivity.getUser().getEmail();
        if(description.length() <= longDescriptionMaxLength){
            this.longDescription = description;
        } else {
            this.longDescription = description.substring(0, longDescriptionMaxLength) + "...";
        }

    }

    public static List<DiaryActivityToHTML> fromListToList(List<DiaryActivity> activities){
        List<DiaryActivityToHTML> htmlReadyAcitivies = new ArrayList<>();
        for(Iterator<DiaryActivity> it = activities.iterator(); it.hasNext();){
            htmlReadyAcitivies.add(new DiaryActivityToHTML(it.next()));
        }
        return htmlReadyAcitivies;
    }

    public static String dateToFormattedString(Date date){
        return dateFormatter.format(date);
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

    public boolean isHasPicture() {
        return hasPicture;
    }

    public void setHasPicture(boolean hasPicture) {
        this.hasPicture = hasPicture;
    }

    public String getHasPictureString() {
        return hasPictureString;
    }

    public void setHasPictureString(String hasPictureString) {
        this.hasPictureString = hasPictureString;
    }

    private String retrievePictureURL(Picture picture, boolean full){
        String url = routes.Assets.at("images/no_picture.png").url();;
        if (picture != null)
            if(full){
                url = routes.Application.getPicture(picture.getName()).url();
            } else {
                url = routes.Application.getPicture(picture.getThumbnail()).url();
            }
        return url;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullPicture() {
        return fullPicture;
    }

    public void setFullPicture(String fullPicture) {
        this.fullPicture = fullPicture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
}
