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
            longDescription, name;
    private double value;
    private boolean hasPicture, isMeal;

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(ConfigFactory.load().getString("date.format"));
    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat(ConfigFactory.load().getString("time.format"));
    private static final int shortDescriptionMaxLength = 25;
    private static final int longDescriptionMaxLength = 144;

    public DiaryActivityToHTML(DiaryActivity diaryActivity){
        id = diaryActivity.getId();
        startHour = diaryActivity.getStarttime().toLocalTime().getHour();
        startMin = diaryActivity.getStarttime().toLocalTime().getMinute();
        endHour = diaryActivity.getEndtime().toLocalTime().getHour();
        endMin = diaryActivity.getEndtime().toLocalTime().getMinute();
        type = diaryActivity.getType().getName();
        description = diaryActivity.getDescription();
        emotion = diaryActivity.getEmotion().name();
        emotionPicture = emotionToPicture(diaryActivity.getEmotion());
        date = dateFormatter.format(diaryActivity.getDate());
        picture = retrievePictureURL(diaryActivity.getPicture(), false);
        fullPicture =retrievePictureURL(diaryActivity.getPicture(), true);
        color = diaryActivity.getType().getColor();
        viewURL = routes.Diary.viewActivity(diaryActivity.getId()).url();
        startTime = timeFormatter.format(diaryActivity.getStarttime());
        endTime = timeFormatter.format(diaryActivity.getEndtime());
        hasPicture = diaryActivity.hasPicture();
        value = diaryActivity.getCarbohydrateValue();
        if(value > -1.0){
            isMeal = true;
        } else {
            isMeal = false;
        }
        if(hasPicture) {
            hasPictureString = "<a href='" + fullPicture + "' data-toggle='lightbox' data-title='" + type +" - " + date + "'>" + Messages.get("page.general.yes") + "</a>";
        } else {
            hasPictureString = Messages.get("page.general.no");
        }
        if(description.length() <= shortDescriptionMaxLength){
            shortDescription = description;
        } else {
            shortDescription = "<a href='" + routes.Application.contentBox(description) +"' data-toggle='lightbox' data-title='" + Messages.get("page.diary.calendar.description") + "'>" + description.substring(0, shortDescriptionMaxLength) + "...</a>";
        }
        firstName = diaryActivity.getUser().getFirstName();
        lastName = diaryActivity.getUser().getLastName();
        email = diaryActivity.getUser().getUserName();
        if(description.length() <= longDescriptionMaxLength){
            longDescription = description;
        } else {
            longDescription = description.substring(0, longDescriptionMaxLength) + "...";
        }
        name = diaryActivity.getName();
    }

    public static List<DiaryActivityToHTML> fromListToList(List<DiaryActivity> activities){
        List<DiaryActivityToHTML> htmlReadyActivities = new ArrayList<>();
        for(Iterator<DiaryActivity> it = activities.iterator(); it.hasNext();){
            htmlReadyActivities.add(new DiaryActivityToHTML(it.next()));
        }
        return htmlReadyActivities;
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean isMeal() {
        return isMeal;
    }

    public void setIsMeal(boolean isMeal) {
        this.isMeal = isMeal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
