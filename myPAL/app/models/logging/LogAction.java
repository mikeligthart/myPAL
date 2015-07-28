package models.logging;

import models.User;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Created by Mike on 22-7-2015.
 */
@Entity
public class LogAction extends Model {

    @Id
    @GeneratedValue
    private long id;

    private Timestamp timestamp;
    private LogActionType type;

    @ManyToOne
    private User user;

    public LogAction(LogActionType type) {
        this.type = type;
        this.timestamp = Timestamp.valueOf(LocalDateTime.now());
    }

    public static Finder<Integer, LogAction> find = new Finder<Integer, LogAction>(Integer.class, LogAction.class);

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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
}
