package models.avatar.behaviorSelection.decisionInformation;

import models.diary.activity.DiaryActivityType;
import org.apache.commons.lang3.builder.EqualsBuilder;

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
 * @version 1.0 22-11-2015
 */
public class AvatarActivityType implements AvatarDecisionFunction {

    private DiaryActivityType activityType;

    public AvatarActivityType(DiaryActivityType activityType) {
        this.activityType = activityType;
    }

    public DiaryActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(DiaryActivityType activityType) {
        this.activityType = activityType;
    }

    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof AvatarActivityType))
            return false;
        if (obj == this)
            return true;

        AvatarActivityType rhs = (AvatarActivityType) obj;
        return new EqualsBuilder().
                append(activityType, rhs.getActivityType()).
                isEquals();
    }
}
