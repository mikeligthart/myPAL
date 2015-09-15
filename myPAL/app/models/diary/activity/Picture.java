package models.diary.activity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import models.User;
import play.db.ebean.Model;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;
/**
 * myPAL
 * Purpose: Container class for pictures
 *
 * Developped for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 11-8-2015
 */
@Entity
public class Picture extends Model {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    private String name, thumbnail;

    @OneToOne(optional = true)
    private DiaryActivity diaryActivity;

    @ManyToOne
    @JsonBackReference
    private User user;

    private Date date;

    public Picture(){

    }

    public Picture(String name, String thumbnail, DiaryActivity diaryActivity){
        this.name = name;
        this.thumbnail = thumbnail;
        this.diaryActivity = diaryActivity;
        this.user = diaryActivity.getUser();
        this.date = diaryActivity.getDate();
    }

    public Picture(String name, String thumbnail, User user, Date date){
        this.name = name;
        this.thumbnail = thumbnail;
        this.user = user;
        this.date = date;
        this.diaryActivity = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
        return find.where().eq("user", user).findList();
    }

    public static List<Picture> byUser(User user, PictureSort sort){
        switch(sort){
            case DATEASC:
                return find.where().eq("user", user).setOrderBy("date asc").findList();
            case DATEDESC:
                return find.where().eq("user", user).setOrderBy("date desc").findList();
            case USERASC:
                return find.where().eq("user", user).setOrderBy("user asc").findList();
            case USERDESC:
                return find.where().eq("user", user).setOrderBy("date desc").findList();
            default:
                return byUser(user);
        }

    }

    public static List<Picture> byUserOnlyUnlinked(User user, PictureSort sort){
        switch(sort){
            case DATEASC:
                return find.where().eq("user", user).eq("diary_activity_id", null).setOrderBy("date asc").findList();
            case DATEDESC:
                return find.where().eq("user", user).eq("diary_activity_id", null).setOrderBy("date desc").findList();
            case USERASC:
                return find.where().eq("user", user).eq("diary_activity_id", null).setOrderBy("user asc").findList();
            case USERDESC:
                return find.where().eq("user", user).eq("diary_activity_id", null).setOrderBy("date desc").findList();
            default:
                return find.where().eq("user", user).eq("diary_activity_id", null).findList();
        }
    }

    public static Picture byID(int id){
        return find.byId(id);
    }
}
