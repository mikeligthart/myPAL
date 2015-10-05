package views.interfaces;

import com.typesafe.config.ConfigFactory;
import models.Usermypal;
import models.logging.LogAction;
import org.joda.time.Instant;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import play.i18n.Messages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * myPAL
 * Purpose: Interface class between a Usermypal and data suitable for HTML
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


    private String email,firstName,lastName, birthdate, userType, lastActivity;
    private List<LogAction> logActions;

    public UserToHTML(Usermypal user){
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        String birthdateString = new SimpleDateFormat(ConfigFactory.load().getString("date.format")).format(user.getBirthdate());
        int age = Years.yearsBetween(Instant.parse(birthdateString, DateTimeFormat.forPattern(ConfigFactory.load().getString("date.format"))), Instant.now()).getYears();
        this.birthdate = age + " - " + birthdateString;
        this.userType = user.getUserType().toString();
        if(user.getLastActivity() == null){
            this.lastActivity = Messages.get("model.user.lastActivityNever");
        }
        else {
            this.lastActivity = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(user.getLastActivity());
        }
        this.logActions = user.getLogActions();
    }

    public static List<UserToHTML> fromListToList(List<Usermypal> users){
        List<UserToHTML> htmlReadyUsers = new ArrayList<>();
        for(Iterator<Usermypal> it = users.iterator(); it.hasNext();){
            htmlReadyUsers.add(new UserToHTML(it.next()));
        }
        return htmlReadyUsers;
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

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
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
}
