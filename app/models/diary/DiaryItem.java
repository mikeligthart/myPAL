package models.diary;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.typesafe.config.ConfigFactory;
import models.UserMyPAL;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import scala.App;
import util.AppException;

import javax.persistence.*;
import java.applet.AppletStub;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
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
@MappedSuperclass
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

    private String userName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Object date) throws AppException {
        if (date instanceof String){
            SimpleDateFormat sdf = new SimpleDateFormat(ConfigFactory.load().getString("date.format"));
            try {
                this.date = new Date(sdf.parse((String) date).getTime());
            } catch (ParseException e) {
                throw new AppException(e.getMessage(), e.getCause());
            }
        } else if (date instanceof Date){
            this.date = (Date) date;
        } else if (date instanceof java.util.Date){
            this.date = new Date(((java.util.Date) date).getTime());
        } else{
            throw new AppException("date object must be either String sql.Date or java.util.Date");
        }
    }

    public Time getStarttime() {
        return starttime;
    }

    public void setStarttime(Object starttime) throws AppException  {
        if (starttime instanceof String){
            SimpleDateFormat sdf = new SimpleDateFormat(ConfigFactory.load().getString("time.format"));
            try {
                this.starttime = new Time(sdf.parse((String) starttime).getTime());
            } catch (ParseException e) {
                throw new AppException(e.getMessage(), e.getCause());
            }
        } else if (starttime instanceof Time){
            this.starttime = (Time) starttime;
        } else if (starttime instanceof java.util.Date){
            this.starttime = new Time(((java.util.Date) starttime).getTime());
        } else{
            throw new AppException("Time object must be either String sql.Time or java.util.Date");
        }
    }

    public Time getEndtime() {
        return endtime;
    }

    public void setEndtime(Object endtime) throws AppException  {
        if (endtime instanceof String){
            SimpleDateFormat sdf = new SimpleDateFormat(ConfigFactory.load().getString("time.format"));
            try {
                this.endtime = new Time(sdf.parse((String) endtime).getTime());
            } catch (ParseException e) {
                throw new AppException(e.getMessage(), e.getCause());
            }
        } else if (endtime instanceof Time){
            this.endtime = (Time) endtime;
        } else if (endtime instanceof java.util.Date){
            this.endtime = new Time(((java.util.Date) endtime).getTime());
        } else{
            throw new AppException("Time object must be either String sql.Time or java.util.Date");
        }
    }

    public UserMyPAL getUser() {
        return user;
    }

    public void setUser(UserMyPAL user) {
        this.user = user;
        userName = user.getUserName();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
