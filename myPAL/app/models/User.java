package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.ebean.Model;
import scala.Int;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ligthartmeu on 29-5-2015.
 */
@Entity
public class User extends Model {

    @Id
    @Constraints.Required(message = "This is field required")
    @Constraints.Email
    private String email;

    @Constraints.Required(message = "This is field required")
    private String firstName;

    @Constraints.Required(message = "This is field required")
    private String lastName;

    @Constraints.Required(message = "This is field required")
    private String password;

    @Constraints.Required(message = "This is field required")
    private String userType;

    public User(String email, String firstName, String lastName, String password, String userType){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.userType = userType;
    }

    /**
    public User(String email, String firstName, String lastName, String password, int userType){
        this(email, firstName, lastName, password, UserType.values()[userType]);
    }
     */

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        if (User.byEmail(email) != null) {
            errors.add(new ValidationError("email", "This e-mail is already registered."));
        }
        return errors.isEmpty() ? null : errors;
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType){
            this.userType = userType;
    }

    public static Finder<String, User> find = new Finder<String, User>(String.class, User.class);

    public static User byEmail(String email){
        return find.byId(email);
    }

    public UserMutable getMutables(){
        return new UserMutable(firstName, lastName, userType);
    }

    public void updateFromMutables(UserMutable mutables){
        firstName = mutables.getFirstName();
        lastName = mutables.getLastName();
        userType = mutables.getUserType();
    }

    public static boolean authenticate(String email, String password) {
        User user = byEmail(email);
        if(user != null){
            if(user.getPassword().equals(password)){
                return true;
            }
        }
        return false;
    }
}
