package models.diary.activity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import controllers.routes;
import models.UserMyPAL;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

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

    @Constraints.Required(message = " ")
    private String name;

    @Constraints.Required(message = " ")
    private String iconLocation;

    @Constraints.Required(message = " ")
    private String color;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "type")
    @JsonManagedReference
    private List<DiaryActivity> activities;

    @ManyToOne
    @JsonBackReference
    @Constraints.Required(message = " ")
    private UserMyPAL user;

    public static String OTHERICONLOCATION = routes.Assets.at("images/other_icon.png").url();
    public static String OTHERCOLOR = "#d47730";

    public static Finder<Integer, DiaryActivityType> find = new Finder<Integer, DiaryActivityType>(Integer.class, DiaryActivityType.class);

    public DiaryActivityType(){

    }

    public DiaryActivityType(String name, String iconLocation, String color, UserMyPAL user){
        this.name = name;
        this.iconLocation = iconLocation;
        this.color = color;
        this.user = user;
    }

    public static DiaryActivityType byId(int id){
        return find.byId(id);
    }
    public static DiaryActivityType byNameAndUser(String name, UserMyPAL user){
            return find.where().eq("user", user).eq("name", name).findUnique();
    }
    public static List<DiaryActivityType> byUser(UserMyPAL user){
        return find.where().eq("user", user).findList();
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

    public List<DiaryActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<DiaryActivity> activities) {
        this.activities = activities;
    }

    public UserMyPAL getUser() {
        return user;
    }

    public void setUser(UserMyPAL user) {
        this.user = user;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString(){
        return name;
    }

    public void removeDiaryActivity(DiaryActivity activity) {
        activities.remove(activity);
    }
}
