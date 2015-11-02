package models.avatar.behaviorSelection.decisionFunctions;

import models.avatar.behaviorSelection.AvatarDecisionNode;
import models.avatar.behaviorSelection.AvatarDecisionPacket;
import models.logging.LogAction;

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
public class UserHistoryPacket implements AvatarDecisionPacket {

    private LogAction last;
    private LogAction beforeLast;
    private AvatarDecisionNode nextNode;

    public UserHistoryPacket(LogAction last, LogAction beforeLast, AvatarDecisionNode nextNode) {
        this.last = last;
        this.beforeLast = beforeLast;
        this.nextNode = nextNode;
    }

    public boolean compare(LogAction last, LogAction beforeLast){
        if(this.last == last && this.beforeLast == beforeLast)
            return true;
        return false;
    }

    public AvatarDecisionNode getNextNode(){
        return nextNode;
    }
}
