package models.diary;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.typesafe.config.ConfigFactory;
import models.UserMyPAL;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

/**
 * myPAL
 * Purpose: base class that models the concept of a diary entry
 *
 * Developed for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 21-8-2015
 */
@Entity
public abstract class DiaryItem extends Model {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;

    @Constraints.Required(message = " ")
    @Formats.DateTime(pattern="dd/MM/yyyy")
    private Date date;

    @Constraints.Required(message = " ")
    @Formats.DateTime(pattern = "HH:mm")
    private Time starttime;

    @Constraints.Required(message = " ")
    @Formats.DateTime(pattern = "HH:mm")
    private Time endtime;

    @ManyToOne()
    @JsonBackReference
    private UserMyPAL user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Object date) throws Exception {
        if (date instanceof String){
            SimpleDateFormat sdf = new SimpleDateFormat(ConfigFactory.load().getString("date.format"));
            this.date = new Date(sdf.parse((String) date).getTime());
        } else if (date instanceof Date){
            this.date = (Date) date;
        } else if (date instanceof java.util.Date){
            this.date = new Date(((java.util.Date) date).getTime());
        } else{
            throw new Exception("date object must be either String sql.Date or java.util.Date");
        }
    }

    public Time getStarttime() {
        return starttime;
    }

    public void setStarttime(Object starttime) throws Exception  {
        if (starttime instanceof String){
            SimpleDateFormat sdf = new SimpleDateFormat(ConfigFactory.load().getString("time.format"));
            this.starttime = new Time(sdf.parse((String) starttime).getTime());
        } else if (starttime instanceof Time){
            this.starttime = (Time) starttime;
        } else if (starttime instanceof java.util.Date){
            this.starttime = new Time(((java.util.Date) starttime).getTime());
        } else{
            throw new Exception("Time object must be either String sql.Time or java.util.Date");
        }
    }

    public Time getEndtime() {
        return endtime;
    }

    public void setEndtime(Object endtime) throws Exception  {
        if (endtime instanceof String){
            SimpleDateFormat sdf = new SimpleDateFormat(ConfigFactory.load().getString("time.format"));
            this.endtime = new Time(sdf.parse((String) endtime).getTime());
        } else if (endtime instanceof Time){
            this.endtime = (Time) endtime;
        } else if (endtime instanceof java.util.Date){
            this.endtime = new Time(((java.util.Date) endtime).getTime());
        } else{
            throw new Exception("Time object must be either String sql.Time or java.util.Date");
        }
    }

    public UserMyPAL getUser() {
        return user;
    }

    public void setUser(UserMyPAL user) {
        this.user = user;
    }
}
