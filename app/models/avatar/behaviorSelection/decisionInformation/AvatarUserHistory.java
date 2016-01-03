package models.avatar.behaviorSelection.decisionInformation;

import models.logging.LogActionType;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * myPAL
 * Purpose: represents decision information on the activity of the user in the diary
 * <p>
 * Developed for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 3-11-2015
 */
public class AvatarUserHistory implements AvatarDecisionInformation {

    private LogActionType last, beforeLast;

    public AvatarUserHistory(LogActionType last, LogActionType beforeLast){
        this.last = last;
        this.beforeLast = beforeLast;
    }

    public LogActionType getLast() {
        return last;
    }

    public void setLast(LogActionType last) {
        this.last = last;
    }

    public LogActionType getBeforeLast() {
        return beforeLast;
    }

    public void setBeforeLast(LogActionType beforeLast) {
        this.beforeLast = beforeLast;
    }

    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof AvatarUserHistory))
            return false;
        if (obj == this)
            return true;


        AvatarUserHistory auh = (AvatarUserHistory) obj;
        return new EqualsBuilder().
                append(last, auh.last).
                append(beforeLast, auh.beforeLast).
                isEquals();
    }
}
