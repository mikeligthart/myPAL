package models.logging;

import com.fasterxml.jackson.annotation.JsonBackReference;
import models.UserMyPAL;
import play.db.ebean.Model;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * myPAL
 * Purpose: models a user behavior log element
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
public class LogAction extends Model {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    private Timestamp timestamp;
    private LogActionType type;

    @ManyToOne
    @JsonBackReference
    private UserMyPAL user;

    public LogAction(UserMyPAL user, LogActionType type) {
        this.user = user;
        this.type = type;
        this.timestamp = Timestamp.valueOf(LocalDateTime.now());
    }

    public static Finder<Integer, LogAction> find = new Finder<Integer, LogAction>(Integer.class, LogAction.class);


    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public LogActionType getType() {
        return type;
    }

    public void setType(LogActionType type) {
        this.type = type;
    }

    public static void log(String userName, LogActionType type){
        UserMyPAL user = UserMyPAL.byUserName(userName);
        user.addLogAction(type);
        user.update();
    }

    public static boolean logInByUserAndDate(UserMyPAL user, Date date){
        LocalDateTime dateMidnight = date.toLocalDate().atStartOfDay();
        Timestamp from = new Timestamp(java.util.Date.from(dateMidnight.atZone(ZoneId.systemDefault()).toInstant()).getTime());
        LocalDateTime nextDay = dateMidnight.plusDays(1);
        Timestamp till = new Timestamp(java.util.Date.from(nextDay.atZone(ZoneId.systemDefault()).toInstant()).getTime());

        return (find.where().eq("user", user).eq("type", LogActionType.LOGIN).between("timestamp", from, till).findRowCount() > 0);
    }

}
