package models.avatar.behaviorDefinition;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.LinkedList;
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
 * @version 1.0 18-11-2015
 */
@Entity
public class AvatarBehaviorBundle extends Model {

    @Id
    private int id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "behaviorBundle")
    @JsonManagedReference
    private List<AvatarBehavior> behaviors;

    private boolean isValid;
    private String description;
    private long lastModified;

    public AvatarBehaviorBundle(){
        behaviors = new LinkedList<>();
    }

    public void addAvatarBehavior(AvatarBehavior behavior){
        behaviors.add(behavior);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<AvatarBehavior> getBehaviors() {
        return behaviors;
    }

    public void setBehavior(List<AvatarBehavior> behaviors) {
        this.behaviors = behaviors;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public static Finder<Integer, AvatarBehaviorBundle> find = new Finder<Integer, AvatarBehaviorBundle>(Integer.class, AvatarBehaviorBundle.class);

    public static AvatarBehaviorBundle byID(int id) {
        return find.byId(id);
    }

    public static boolean exists(int id) {
        return (byID(id) != null);
    }

    public static int getCount(){
        return find.all().size();
    }

    public static int getHighestId(){
        List<AvatarBehaviorBundle> behaviorBundles = find.where().orderBy("id desc").findList();
        return behaviorBundles.get(0).getId();
    }
}
