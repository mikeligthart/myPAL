package models.diary;

import models.Emotion;

import java.time.Instant;

/**
 * Created by ligthartmeu on 8-7-2015.
 */
public class DiaryActivity extends DiaryItem {

    private DiaryActivityType type;
    private String description;
    private Emotion emotion;
    private String picture;

    public DiaryActivity(Instant starttime, Instant endtime, DiaryActivityType type) {
        super(starttime, endtime);
        this.type = type;
    }

    public DiaryActivity(Instant starttime, Instant endtime, DiaryActivityType type, String description, Emotion emotion, String picture) {
        super(starttime, endtime);
        this.type = type;
        this.description = description;
        this.emotion = emotion;
        this.picture = picture;
    }

    public DiaryActivityType getType() {
        return type;
    }

    public void setType(DiaryActivityType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Emotion getEmotion() {
        return emotion;
    }

    public void setEmotion(Emotion emotion) {
        this.emotion = emotion;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
