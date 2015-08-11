package models.diary;

import controllers.Diary;
import models.User;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ligthartmeu on 8-7-2015.
 */
@Entity
public class DiaryActivity extends DiaryItem {

    @Enumerated(EnumType.STRING)
    @Constraints.Required
    private DiaryActivityType type;

    @Constraints.Required(message = "Dit moet ook nog ingevuld worden")
    private String description;

    @OneToOne(cascade = CascadeType.ALL, optional = true)
    private Picture picture;

    @OneToOne
    @Enumerated(EnumType.STRING)
    @Constraints.Required
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

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public Emotion getEmotion() {
        return emotion;
    }

    public void setEmotion(Emotion emotion) {
        this.emotion = emotion;
    }

    public static Finder<Integer, DiaryActivity> find = new Finder<Integer, DiaryActivity>(Integer.class, DiaryActivity.class);

    public static List<DiaryActivity> byUser(User user){
        return find.where().eq("user", user).findList();
    }

    public static List<DiaryActivity> byDate(Date date){
        return find.where().eq("date", date).findList();
    }

    public static List<DiaryActivity> byUserAndDate(User user, Date date){
        return find.where().eq("user", user).eq("date", date).findList();
    }

}
