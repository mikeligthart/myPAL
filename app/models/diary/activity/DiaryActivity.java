package models.diary.activity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.UserMyPAL;
import models.diary.DiaryItem;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * myPAL
 * Purpose: models the real world concept of an activity that someone needs to report in the diary.
 *
 * Developped for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 21-8-2015
 */
@Entity
public class DiaryActivity extends DiaryItem {

    @ManyToOne
    @JsonBackReference
    private DiaryActivityType type;

    @Constraints.Required(message = " ")
    private String name;

    @Constraints.MaxLength(1200)
    @Column(length = 1200)
    @Constraints.Required(message = " ")
    private String description;

    @OneToOne(cascade = CascadeType.ALL, optional = true)
    @JsonManagedReference
    private Picture picture;

    @OneToOne
    @Enumerated(EnumType.STRING)
    @Constraints.Required(message = " ")
    private Emotion emotion;

    @Constraints.Required(message = " ")
    private double carbohydrateValue;

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

    public boolean hasPicture(){
        return (picture != null);
    }

    public Emotion getEmotion() {
        return emotion;
    }

    public void setEmotion(Emotion emotion) {
        this.emotion = emotion;
    }

    public double getCarbohydrateValue() {
        return carbohydrateValue;
    }

    public void setCarbohydrateValue(double carbohydrateValue) {
        this.carbohydrateValue = carbohydrateValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Finder<Integer, DiaryActivity> find = new Finder<Integer, DiaryActivity>(Integer.class, DiaryActivity.class);

    public static List<DiaryActivity> byUser(UserMyPAL user){
        return find.where().eq("user", user).findList();
    }

    public static List<DiaryActivity> byDate(Date date){
        return find.where().eq("date", date).findList();
    }

    public static List<DiaryActivity> byUserAndDate(UserMyPAL user, Date date){
        return find.where().eq("user", user).eq("date", date).setOrderBy("starttime asc").findList();
    }

    public static DiaryActivity byID(int id){
        return find.byId(id);
    }

    public static int countByUserAndDates(UserMyPAL user, Date start, Date end){
        return find.where().eq("user", user).between("added", new Timestamp(start.getTime()), new Timestamp(end.getTime())).findRowCount();
    }

    public static int addedFromYesterday(UserMyPAL user, Date start, Date end){
        Date yesterday = Date.valueOf(end.toLocalDate().minusDays(1));
        return find.where().eq("user", user).between("added", new Timestamp(start.getTime()), new Timestamp(end.getTime())).eq("date", yesterday).findRowCount();
    }

    public static boolean addedAnythingOnDate(UserMyPAL user, Date date){
        LocalDateTime dateMidnight = date.toLocalDate().atStartOfDay();
        Timestamp from = new Timestamp(java.util.Date.from(dateMidnight.atZone(ZoneId.systemDefault()).toInstant()).getTime());
        LocalDateTime nextDay = dateMidnight.plusDays(1);
        Timestamp till = new Timestamp(java.util.Date.from(nextDay.atZone(ZoneId.systemDefault()).toInstant()).getTime());

        return (find.where().eq("user", user).between("added", from, till).findRowCount() > 0);
    }

}
