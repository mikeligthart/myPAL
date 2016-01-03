package models.avatar.behaviorDefinition;

import models.UserMyPAL;
import models.diary.activity.DiaryActivity;
import models.goals.Goal;
import play.i18n.Messages;

import java.util.ArrayList;
import java.util.List;

/**
 * myPAL
 * Purpose: [ENTER PURPOSE]
 * <p>
 * Developed for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 9-11-2015
 */
public class AvatarLineVariables {

    private UserMyPAL user;
    private DiaryActivity latestActivity;

    public AvatarLineVariables(UserMyPAL user){
        this.user = user;
        latestActivity = DiaryActivity.lastByUser(user);
    }

    public UserMyPAL getUser() {
        return user;
    }

    public void setUser(UserMyPAL user) {
        this.user = user;
    }

    public String processLine(String line){
        line = line.replace("#firstName#", user.getFirstName());
        if(latestActivity != null) {
            line = line.replace("#activityName#", latestActivity.getName().toLowerCase());
        } else {
            line = line.replace("#activityName#", "");
        }
        return line;
    }

    public static String lineVariablesToString(){
        StringBuilder builder = new StringBuilder();
        builder.append("* #firstName#: ").append(Messages.get("model.avatarLineVariables.firstName"));
        builder.append("* #activityName#: ").append(Messages.get("model.avatarLineVariables.activityName"));
        return builder.toString();
    }

    public static boolean isLineVariable(String potentialLineVariable){
        if(potentialLineVariable.equalsIgnoreCase("#firstName#"))
            return true;
        else return potentialLineVariable.equalsIgnoreCase("#activityName#");
    }
}
