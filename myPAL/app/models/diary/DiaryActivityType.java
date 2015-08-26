package models.diary;

import com.fasterxml.jackson.annotation.JsonBackReference;
import controllers.routes;
import models.User;
import play.db.ebean.Model;

import javax.persistence.*;

/**
 * myPAL
 * Purpose: [ENTER PURPOSE]
 * <p>
 * Developped for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 26-8-2015
 */
@Entity
public class DiaryActivityType extends Model {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    //@Constraints.Required(message = " ")
    private String name;

    //@Constraints.Required(message = " ")
    private String iconLocation;

    private String color;

    @OneToOne(optional = true)
    private DiaryActivity activity;

    @ManyToOne(optional = true)
    @JsonBackReference
    private User user;

    public static String OTHERICONLOCATION = routes.Assets.at("images/other_icon.png").url();
    public static String OTHERCOLOR = "#d47730";

    public DiaryActivityType(){

    }

    public DiaryActivityType(DiaryActivityType copy){
        this.name = copy.getName();
        this.iconLocation = copy.getIconLocation();
        this.activity = copy.getActivity();
        this.user = copy.getUser();
        this.color = copy.getColor();
    }

    public static Finder<Integer, DiaryActivityType> find = new Finder<Integer, DiaryActivityType>(Integer.class, DiaryActivityType.class);

    public static DiaryActivityType byName(String name, User user){
            return find.where().eq("user", user).eq("name", name).findUnique();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconLocation() {
        return iconLocation;
    }

    public void setIconLocation(String iconLocation) {
        this.iconLocation = iconLocation;
    }

    public DiaryActivity getActivity() {
        return activity;
    }

    public void setActivity(DiaryActivity activity) {
        this.activity = activity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
