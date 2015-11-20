package views.interfaces;

import com.typesafe.config.ConfigFactory;
import models.UserMyPAL;
import models.logging.LogAction;
import org.joda.time.Instant;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import play.i18n.Messages;
import util.GluconlineClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * myPAL
 * Purpose: Interface class between a UserMyPAL and data suitable for HTML
 *
 * Developped for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 17-7-2015
 */
public class UserToHTML {


    private String userName,firstName,lastName, birthdate, userType, lastActivity, gluconlineID;
    int age;
    private List<LogAction> logActions;
    private boolean validGluconlineID;

    public UserToHTML(UserMyPAL user){
        userName = user.getUserName();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        birthdate = new SimpleDateFormat(ConfigFactory.load().getString("date.format")).format(user.getBirthdate());
        age = Years.yearsBetween(Instant.parse(birthdate, DateTimeFormat.forPattern(ConfigFactory.load().getString("date.format"))), Instant.now()).getYears();
        userType = user.getUserType().toString();
        gluconlineID = user.getGluconlineID();
        validGluconlineID = GluconlineClient.validateGluconlineID(gluconlineID);
        if(user.getLastActivity() == null){
            lastActivity = Messages.get("model.user.lastActivityNever");
        }
        else {
            lastActivity = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(user.getLastActivity());
        }
        logActions = user.getLogActions();
    }

    public static List<UserToHTML> fromListToList(List<UserMyPAL> users){
        List<UserToHTML> htmlReadyUsers = new ArrayList<>();
        for(Iterator<UserMyPAL> it = users.iterator(); it.hasNext();){
            htmlReadyUsers.add(new UserToHTML(it.next()));
        }
        return htmlReadyUsers;
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

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }

    public List<LogAction> getLogActions() {
        return logActions;
    }

    public void setLogActions(List<LogAction> logActions) {
        this.logActions = logActions;
    }

    public String getGluconlineID() {
        return gluconlineID;
    }

    public void setGluconlineID(String gluconlineID) {
        this.gluconlineID = gluconlineID;
    }

    public boolean isValidGluconlineID() {
        return validGluconlineID;
    }

    public void setValidGluconlineID(boolean validGluconlineID) {
        this.validGluconlineID = validGluconlineID;
    }
}
