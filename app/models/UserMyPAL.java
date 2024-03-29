package models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.typesafe.config.ConfigFactory;
import models.diary.activity.DiaryActivity;
import models.diary.activity.DiaryActivityType;
import models.diary.activity.Picture;
import models.diary.measurement.DiaryMeasurement;
import models.goals.Goal;
import models.logging.LogAction;
import models.logging.LogActionType;
import models.logging.LogAvatar;
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
public class UserMyPAL extends Model {

    @Id
    @Constraints.Required
    private String userName;

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
    private List<LogAvatar> logAvatars;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonManagedReference
    private List<Goal> goals;

    private String gluconlineID = "";

    //The attributes needed to login with a user
    public static class Login {
        public String userName;
        public String password;

        public Login(){}

        public String validate() {
            if (!UserMyPAL.authenticate(userName, password)) {
                return Messages.get("model.user.invalid");
            }
            return null;
        }
    }

    public UserMyPAL(){

    }

    public UserMyPAL(String userName, String firstName, String lastName, Date birthdate, String password, UserType userType, String gluconlineID){
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.userType = userType;
        this.gluconlineID = gluconlineID;
        try {
            this.password = HashHelper.createPassword(password);
        } catch (AppException e) {
            Logger.error(e.getLocalizedMessage());
        }
    }

    public static Finder<String, UserMyPAL> find = new Finder<String, UserMyPAL>(String.class, UserMyPAL.class);

    public static UserMyPAL byUserName(String userName){
        return find.byId(userName);
    }

    public static boolean authenticate(String userName, String password) {
        UserMyPAL user = byUserName(userName);
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
        this.password = HashHelper.createPassword(password);
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

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    public List<LogAvatar> getLogAvatars() {
        return logAvatars;
    }

    public void setLogAvatars(List<LogAvatar> logAvatars) {
        this.logAvatars = logAvatars;
    }

    public String getGluconlineID() {
        return gluconlineID;
    }

    public void setGluconlineID(String gluconlineID) {
        this.gluconlineID = gluconlineID;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserMyPAL))
            return false;
        if (obj == this)
            return true;

        UserMyPAL rhs = (UserMyPAL) obj;
        return new EqualsBuilder().
                append(userName, rhs.userName).
                append(lastActivity, rhs.lastActivity).
                isEquals();
    }
}
