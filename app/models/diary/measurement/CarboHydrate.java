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
 * Purpose: [ENTER PURPOSE]
 * <p>
 * Developed for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 29-9-2015
 */
@Entity
public class CarboHydrate extends DiaryMeasurement {


    private String comment;

    public CarboHydrate(){
        super();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static Finder<Integer, CarboHydrate> find = new Finder<Integer, CarboHydrate>(Integer.class, CarboHydrate.class);

    public static List<CarboHydrate> byUser(UserMyPAL user){
        return find.where().eq("user", user).findList();
    }

    public static List<CarboHydrate> byDate(Date date){
        return find.where().eq("date", date).findList();
    }

    public static List<CarboHydrate> byUserAndDate(UserMyPAL user, Date date){
        return find.where().eq("user", user).eq("date", date).setOrderBy("starttime asc").findList();
    }

    public static CarboHydrate byID(int id){
        return find.byId(id);
    }
}
