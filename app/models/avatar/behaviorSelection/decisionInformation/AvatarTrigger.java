package models.avatar.behaviorSelection.decisionInformation;

import com.fasterxml.jackson.databind.JsonNode;
import play.Logger;
import sun.rmi.runtime.Log;

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
 * @version 1.0 3-11-2015
 */
public class AvatarTrigger implements AvatarDecisionFunction {

    public static final String PAGE = "PAGE";
    public static final String OTHER = "OTHER";

    private String trigger;

    public AvatarTrigger(String trigger){
        this.trigger = trigger.toUpperCase();
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AvatarTrigger))
            return false;
        if (obj == this)
            return true;

        AvatarTrigger rhs = (AvatarTrigger) obj;
        if(rhs.getTrigger().equals(this.trigger))
            return true;
        return false;
    }

    public static boolean isLegalTrigger(String proposedTrigger) {
        if(proposedTrigger.equalsIgnoreCase(PAGE))
            return true;
        else if(proposedTrigger.equalsIgnoreCase(OTHER))
            return true;
        return false;

    }
}
