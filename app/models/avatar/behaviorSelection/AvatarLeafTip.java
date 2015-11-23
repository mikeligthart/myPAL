package models.avatar.behaviorSelection;

import models.logging.LogAvatarType;

import java.util.LinkedList;

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
 * @version 1.0 23-11-2015
 */
public class AvatarLeafTip {

    private LogAvatarType logAvatarType;
    private LinkedList<Integer> behaviorIds;

    public AvatarLeafTip(LogAvatarType logAvatarType, LinkedList<Integer> behaviorIds) {
        this.logAvatarType = logAvatarType;
        this.behaviorIds = behaviorIds;
    }

    public LogAvatarType getLogAvatarType() {
        return logAvatarType;
    }

    public void setLogAvatarType(LogAvatarType logAvatarType) {
        this.logAvatarType = logAvatarType;
    }

    public LinkedList<Integer> getBehaviorIds() {
        return behaviorIds;
    }

    public void setBehaviorIds(LinkedList<Integer> behaviorIds) {
        this.behaviorIds = behaviorIds;
    }
}
