package models;

import models.logging.LogAction;
import models.logging.LogActionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.ebean.Model;
import play.i18n.Messages;
import util.AppException;
import util.HashHelper;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    @Formats.DateTime(pattern="dd/MM/yyyy")
    private Date birthdate;

    @Constraints.Required
    private String password;

    @Constraints.Required
    @OneToMany
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<LogAction> logActions;

    private Timestamp lastActivity;

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

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        if (User.byEmail(email) != null) {
            errors.add(new ValidationError("email", Messages.get("model.user.emailregisteredalready")));
        }
        return errors.isEmpty() ? null : errors;
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
            if(HashHelper.checkPassword(password, user.getPassword())){
                return true;
            }
        }
        return false;
    }

    public void addLogAction(LogActionType logActionType){
        LogAction logAction = new LogAction(logActionType);
        lastActivity = logAction.getTimestamp();
        logActions.add(logAction);
        logAction.save();
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

    public Date getBirthdate() {
        return birthdate;
    }

    /*
    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }
    */

    public void setBirthdate(String birthdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.birthdate = new Date(sdf.parse(birthdate).getTime());
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws AppException {
        this.password = HashHelper.createPassword(password); ;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType){
            this.userType = userType;
    }

    public List<LogAction> getLogActions() {
        return logActions;
    }

    public void setLogActions(List<LogAction> logActions) {
        this.logActions = logActions;
    }


    public Timestamp getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Timestamp lastActivity) {
        this.lastActivity = lastActivity;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User))
            return false;
        if (obj == this)
            return true;

        User rhs = (User) obj;
        return new EqualsBuilder().
                append(email, rhs.email).
                append(lastActivity, rhs.lastActivity).
                isEquals();
    }
}
