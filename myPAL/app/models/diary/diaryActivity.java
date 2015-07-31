package models.diary;

import com.fasterxml.jackson.annotation.JsonBackReference;
import models.Emotion;
import models.User;
import play.Logger;

import javax.persistence.*;

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

    public DiaryActivity(){
        super();
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
