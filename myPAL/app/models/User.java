package models;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.ebean.Model;
import play.i18n.Messages;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ligthartmeu on 29-5-2015.
 */
@Entity
public class User extends Model {

    @Id
    @Constraints.Required
    @Constraints.Email
    private String email;

    @Constraints.Required
    private String firstName;

    @Constraints.Required
    private String lastName;

    @Constraints.Required
    private String password;

    @Constraints.Required
    @OneToMany
    @Enumerated(EnumType.STRING)
    private UserType userType;

    //The attributes needed to login with a user
    public static class Login {
        public String email;
        public String password;

        public Login(){};

        public String validate() {
            if (!User.authenticate(email, password)) {
                return Messages.get("model.user.invalid");
            }
            return null;
        }
    }

    //The attributes that are mutable

    public static class UserMutable{

        @Constraints.Required
        public String firstName;

        @Constraints.Required
        public String lastName;

        @Constraints.Required
        @OneToMany
        @Enumerated(EnumType.STRING)
        public UserType userType;

        public UserMutable(String firstName, String lastName, UserType userType){
            this.firstName = firstName;
            this.lastName = lastName;
            this.userType = userType;
        }

        public UserMutable(){}
    }

    /*
    public User(String email, String firstName, String lastName, String password, UserType userType){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.userType = userType;
    }
    */

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        if (User.byEmail(email) != null) {
            errors.add(new ValidationError("email", Messages.get("model.user.emailregisteredalready")));
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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType){
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
        firstName = mutables.firstName;
        lastName = mutables.lastName;
        userType = mutables.userType;
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
