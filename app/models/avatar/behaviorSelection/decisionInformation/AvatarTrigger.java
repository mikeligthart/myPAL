package models.avatar.behaviorSelection.decisionInformation;

/**
 * myPAL
 * Purpose: represents decision information on what triggered the call to find the most fitting behavior
 * <p>
 * Developed for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 3-11-2015
 */
public class AvatarTrigger implements AvatarDecisionInformation {

    public static final String PAGE = "PAGE";
    public static final String OTHER = "OTHER";
    public static final String TOGETHERORSELF = "TOGETHERORSELF";

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
        return rhs.getTrigger().equals(this.trigger);
    }

    public static boolean isLegalTrigger(String proposedTrigger) {
        if(proposedTrigger.equalsIgnoreCase(PAGE))
            return true;
        else if(proposedTrigger.equalsIgnoreCase(OTHER))
            return true;
        else if(proposedTrigger.equalsIgnoreCase(TOGETHERORSELF))
            return true;
        return false;

    }
}
