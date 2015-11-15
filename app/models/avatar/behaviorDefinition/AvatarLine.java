package models.avatar.behaviorDefinition;

import com.fasterxml.jackson.annotation.JsonBackReference;
import play.db.ebean.Model;

import javax.persistence.*;
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
 * @version 1.0 12-11-2015
 */
@Entity
public class AvatarLine extends Model {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    private String line;

    @ManyToOne
    @JsonBackReference
    private AvatarBehavior behavior;

    public AvatarLine(AvatarBehavior behavior, String line){
        this.behavior = behavior;
        this.line = line;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    @Override
    public String toString(){
        return line;
    }

    public AvatarBehavior getBehavior() {
        return behavior;
    }

    public void setBehavior(AvatarBehavior behavior) {
        this.behavior = behavior;
    }

    public static Finder<Integer, AvatarLine> find = new Finder<Integer, AvatarLine>(Integer.class, AvatarLine.class);

    public static AvatarLine byID(int id) {
        return find.byId(id);
    }

    public static List<AvatarLine> byBehavior(AvatarBehavior behavior){
        return find.where().eq("behavior", behavior).findList();
    }
}
