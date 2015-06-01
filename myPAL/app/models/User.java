package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by ligthartmeu on 29-5-2015.
 */
@Entity
public class User extends Model {

    @Id
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private UserType userType;

    public enum UserType{
        CHILD, PARENT, VIEWER, MODERATOR, ADMIN
    }

    public User(String email, String firstName, String lastName, String password, UserType userType){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.userType = userType;
    }

    public User(String email, String firstName, String lastName, String password, int userType){
        this(email, firstName, lastName, password, UserType.values()[userType]);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserType() {
        return userType.ordinal();
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public void setUserType(int userType){
        this.userType = UserType.values()[userType];
    }

    public static Finder<String, User> find = new Finder<String, User>(String.class, User.class);

}
