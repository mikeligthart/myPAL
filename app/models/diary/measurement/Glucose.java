package models.diary.measurement;

import models.UserMyPAL;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;
import java.util.List;

/**
 * myPAL
 * Purpose: models the concept of a blood glucose measurement
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
public class Glucose extends DiaryMeasurement {


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    private String comment;

    public Glucose(){
        super();
    }

    public String getComment() {
        return comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static Finder<Integer, Glucose> find = new Finder<Integer, Glucose>(Integer.class, Glucose.class);

    public static List<Glucose> byUser(UserMyPAL user){
        return find.where().eq("user", user).findList();
    }

    public static List<Glucose> byDate(Date date){
        return find.where().eq("date", date).findList();
    }

    public static List<Glucose> byUserAndDate(UserMyPAL user, Date date){
        return find.where().eq("user", user).eq("date", date).setOrderBy("starttime asc").findList();
    }

    public static Glucose byID(int id){
        return find.byId(id);
    }

}
