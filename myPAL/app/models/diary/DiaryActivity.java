package models.diary;

import javax.persistence.*;

/**
 * Created by ligthartmeu on 8-7-2015.
 */
@Entity
public class DiaryActivity extends DiaryItem {

    @Enumerated(EnumType.STRING)
    //@Constraints.Required
    private DiaryActivityType type;

    //@Constraints.Required
    private String description;

    //private String picture;

    //@Constraints.Required
    @OneToOne
    @Enumerated(EnumType.STRING)
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

    /*
    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
    */

    public Emotion getEmotion() {
        return emotion;
    }

    public void setEmotion(Emotion emotion) {
        this.emotion = emotion;
    }

    public static Finder<Integer, DiaryActivity> find = new Finder<Integer, DiaryActivity>(Integer.class, DiaryActivity.class);
}
