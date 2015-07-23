package models.diary;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Created by ligthartmeu on 8-7-2015.
 */
@Entity
public abstract class DiaryItem extends Model {

    @Id
    private int id;

    @Temporal(TemporalType.DATE)
    private Date date;

    @Temporal(TemporalType.TIME)
    private Time starttime, endtime;

    public DiaryItem(int id){
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
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
        this.starttime = starttime;
    }

    public Time getEndtime() {
        return endtime;
    }

    public void setEndtime(Time endtime) {
        this.endtime = endtime;
    }
}
