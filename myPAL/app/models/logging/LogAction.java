package models.logging;

import com.fasterxml.jackson.annotation.JsonBackReference;
import models.User;
import play.db.ebean.Model;

import javax.persistence.*;
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

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonBackReference
    private User user;

    public LogAction(User user, LogActionType type) {
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
}
