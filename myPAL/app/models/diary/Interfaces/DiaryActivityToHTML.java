package models.diary.interfaces;

import models.diary.DiaryActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ligthartmeu on 17-7-2015.
 */
public class DiaryActivityToHTML {

    private int startHour, startMin, endHour, endMin;
    private String type;
    private String description;
    private String picture;
    private double pleased, aroused, dominant;

    public DiaryActivityToHTML(DiaryActivity diaryActivity){
        this.startHour = diaryActivity.getStarttime().toLocalTime().getHour();
        this.startMin = diaryActivity.getStarttime().toLocalTime().getMinute();
        this.endHour = diaryActivity.getEndtime().toLocalTime().getHour();
        this.endMin = diaryActivity.getEndtime().toLocalTime().getMinute();
        this.type = diaryActivity.getType().toString();
        this.description = diaryActivity.getDescription();
        this.picture = diaryActivity.getPicture();
        this.pleased = diaryActivity.getEmotion().getPleased();
        this.aroused = diaryActivity.getEmotion().getAroused();
        this.dominant = diaryActivity.getEmotion().getDominant();
    }

    public static List<DiaryActivityToHTML> fromListToList(List<DiaryActivity> activities){
        List<DiaryActivityToHTML> htmlReadyAcitivies = new ArrayList<>();
        for(Iterator<DiaryActivity> it = activities.iterator(); it.hasNext();){
            htmlReadyAcitivies.add(new DiaryActivityToHTML(it.next()));
        }
        return htmlReadyAcitivies;
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

    public String getPicture() {
        return picture;
    }

    public double getPleased() {
        return pleased;
    }

    public double getAroused() {
        return aroused;
    }

    public double getDominant() {
        return dominant;
    }
}
