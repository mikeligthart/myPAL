package models.diary;

import controllers.Diary;
import models.User;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Mike on 11-8-2015.
 */
@Entity
public class Picture extends Model {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private String name, thumbnail;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private DiaryActivity diaryActivity;

    public Picture(){

    }

    public Picture(String name, String thumbnail, DiaryActivity diaryActivity){
        this.name = name;
        this.thumbnail = thumbnail;
        this.diaryActivity = diaryActivity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public DiaryActivity getDiaryActivity() {
        return diaryActivity;
    }

    public void setDiaryActivity(DiaryActivity diaryActivity) {
        this.diaryActivity = diaryActivity;
    }

    public User getUser(){
        return diaryActivity.getUser();
    }

    public static Finder<Integer, Picture> find = new Finder<Integer, Picture>(Integer.class, Picture.class);

    public static Picture byName(String name){
        return find.where().eq("name", name).findUnique();
    }

    public static Picture byThumbnail(String name){
        return find.where().eq("thumbnail", name).findUnique();
    }

    public static Picture byDiaryActivity(DiaryActivity diaryActivity){
        return find.where().eq("diaryActivity", diaryActivity).findUnique();
    }

    public static List<Picture> byUser(User user){
        ArrayList<Picture> pictures = new ArrayList<>();
        for(Iterator<DiaryActivity> it = DiaryActivity.byUser(user).iterator(); it.hasNext();){
            DiaryActivity diaryActivity = it.next();
            if(diaryActivity.hasPicture()) {
                pictures.add(byDiaryActivity(diaryActivity));
            }
        }
        return pictures;
    }

    /*
    public static List<Picture> byUser(User user, PictureSort sort){
        ArrayList<Picture> pictures = new ArrayList<>();
        for(Iterator<DiaryActivity> it = DiaryActivity.byUser(user).iterator(); it.hasNext();){
            pictures.add(byDiaryActivity(it.next()));
        }
        return pictures;
    }
    */
}
