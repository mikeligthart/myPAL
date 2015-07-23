package models.diary;

import models.Emotion;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;

/**
 * Created by ligthartmeu on 8-7-2015.
 */
@Entity
public class DiaryActivity extends DiaryItem {

    @Enumerated(EnumType.STRING)
    private DiaryActivityType type;

    private String description;
    private String picture;

    @OneToOne
    private Emotion emotion;

    public DiaryActivity(int id){
        super(id);
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Emotion getEmotion() {
        return emotion;
    }

    public void setEmotion(Emotion emotion) {
        this.emotion = emotion;
    }

    public static Finder<Integer, DiaryActivity> find = new Finder<Integer, DiaryActivity>(Integer.class, DiaryActivity.class);
}
