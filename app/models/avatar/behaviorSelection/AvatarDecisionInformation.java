package models.avatar.behaviorSelection;

import models.UserMyPAL;
import models.logging.LogAction;

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
 * @version 1.0 2-11-2015
 */
public class AvatarDecisionInformation {

    private UserMyPAL user;
    private List<LogAction> userHistory;

    public AvatarDecisionInformation(UserMyPAL user){
        this.user = user;
        userHistory = user.getLogActions();
    }

    public void update(){
        userHistory = user.getLogActions();
    }

    public UserMyPAL getUser() {
        return user;
    }

    public void setUser(UserMyPAL user) {
        this.user = user;
    }

    public List<LogAction> getUserHistory() {
        return userHistory;
    }

    public void setUserHistory(List<LogAction> userHistory) {
        this.userHistory = userHistory;
    }
}
