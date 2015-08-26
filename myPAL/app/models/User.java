package models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.typesafe.config.ConfigFactory;
import models.diary.DiaryActivity;
import models.diary.DiaryActivityType;
import models.diary.DiaryMeasurement;
import models.diary.Picture;
import models.logging.LogAction;
import models.logging.LogActionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.i18n.Messages;
import util.AppException;
import util.HashHelper;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * myPAL
 * Purpose: models the user of the application
 *
 * Developped for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 21-8-2015
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
    @Formats.DateTime(pattern="dd/MM/yyyy") //Is this still up to date with application.conf -> date.format
    private Date birthdate;

    @Constraints.Required
    private String password;

    @Constraints.Required
    @OneToMany
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonManagedReference
    private List<LogAction> logActions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonManagedReference
    private List<DiaryActivityType> createdDiaryActivityTypes;

    private Timestamp lastActivity;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonManagedReference
    private List<DiaryActivity> diaryActivities;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonManagedReference
    private List<DiaryMeasurement> diaryMeasurements;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonManagedReference
    private List<Picture> pictures;

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

    public User(){

    }

    public User(String email, String firstName, String lastName, Date birthdate, String password, UserType userType){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.userType = userType;
        try {
            this.password = HashHelper.createPassword(password);
        } catch (AppException e) {
            Logger.error(e.getLocalizedMessage());
        }
    }

    public static Finder<String, User> find = new Finder<String, User>(String.class, User.class);

    public static User byEmail(String email){
        return find.byId(email);
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
        LogAction logAction = new LogAction(this, logActionType);
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

    public void setBirthdate(Date birthdate){
        this.birthdate = birthdate;
    }

    public void setBirthdate(Object birthdate) {
        if (birthdate instanceof String){
            SimpleDateFormat sdf = new SimpleDateFormat(ConfigFactory.load().getString("date.format"));
            try {
                this.birthdate = new Date(sdf.parse((String) birthdate).getTime());
            } catch (ParseException e) {
                Logger.error(e.getLocalizedMessage());
            }
        } else if (birthdate instanceof Date){
            this.birthdate = (Date) birthdate;
        } else if (birthdate instanceof java.util.Date){
            this.birthdate = new Date(((java.util.Date) birthdate).getTime());
        }
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws AppException {
        this.password = HashHelper.createPassword(password); ;
    }

    public void setHashedPassword(String hashedPassword)
    {
        this.password = hashedPassword;
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

    public List<DiaryActivity> getDiaryActivities() {
        return diaryActivities;
    }

    public void setDiaryActivities(List<DiaryActivity> diaryActivities) {
        this.diaryActivities = diaryActivities;
    }

    public List<DiaryMeasurement> getDiaryMeasurements() {
        return diaryMeasurements;
    }

    public void setDiaryMeasurements(List<DiaryMeasurement> diaryMeasurements) {
        this.diaryMeasurements = diaryMeasurements;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public void removeDiaryActivity(DiaryActivity diaryActivity){
        Picture picture = diaryActivity.getPicture();
        if (picture != null) {
            pictures.remove(picture);
        }
        diaryActivities.remove(diaryActivity);
    }

    public List<DiaryActivityType> getCreatedDiaryActivityTypes() {
        return createdDiaryActivityTypes;
    }

    public void setCreatedDiaryActivityTypes(List<DiaryActivityType> createdDiaryActivityTypes) {
        this.createdDiaryActivityTypes = createdDiaryActivityTypes;
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
