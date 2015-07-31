package models.diary;

import com.fasterxml.jackson.annotation.JsonBackReference;
import models.User;
import play.Logger;
import play.db.ebean.Model;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

/**
 * Created by ligthartmeu on 8-7-2015.
 */
@Entity
public abstract class DiaryItem extends Model {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @Temporal(TemporalType.DATE)
    private Date date;

    @Temporal(TemporalType.TIME)
    private Time starttime, endtime;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonBackReference
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getStarttime() {
        return starttime;
    }

    public void setStarttime(Time starttime) {
        Logger.debug("Starttime = " + starttime);
        this.starttime = starttime;
    }

    public Time getEndtime() {
        return endtime;
    }

    public void setEndtime(Time endtime) {
        this.endtime = endtime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        Logger.debug("[DiaryItem < setUser] The user's name is is " + user.getFirstName());
    }
}
