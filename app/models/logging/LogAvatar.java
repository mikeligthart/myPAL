package models.logging;

import com.fasterxml.jackson.annotation.JsonBackReference;
import models.UserMyPAL;
import play.db.ebean.Model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
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
 * @version 1.0 23-11-2015
 */
@Entity
public class LogAvatar extends Model {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    private Timestamp timestamp;
    private LogAvatarType type;

    @ManyToOne
    @JsonBackReference
    private UserMyPAL user;

    public LogAvatar(UserMyPAL user, LogAvatarType type) {
        this.type = type;
        this.user = user;
        timestamp = Timestamp.valueOf(LocalDateTime.now());
    }

    public static void log(UserMyPAL user, LogAvatarType type){
        LogAvatar log = new LogAvatar(user, type);
        log.save();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public LogAvatarType getType() {
        return type;
    }

    public void setType(LogAvatarType type) {
        this.type = type;
    }

    public UserMyPAL getUser() {
        return user;
    }

    public void setUser(UserMyPAL user) {
        this.user = user;
    }

    public static Finder<Integer, LogAvatar> find = new Finder<Integer, LogAvatar>(Integer.class, LogAvatar.class);

    public static List<LogAvatar> byUser(UserMyPAL user) {
        return find.where().eq("user", user).findList();
    }
}
