package models.avatar.behaviorSelection.decisionInformation;

import models.goals.GoalStatus;

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
 * @version 1.0 26-11-2015
 */
public class AvatarGoalStatus implements AvatarDecisionFunction {

    private GoalStatus goalStatus;

    public AvatarGoalStatus(GoalStatus goalStatus) {
        this.goalStatus = goalStatus;
    }

    public GoalStatus getGoalStatus() {
        return goalStatus;
    }

    public void setGoalStatus(GoalStatus goalStatus) {
        this.goalStatus = goalStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AvatarGoalStatus that = (AvatarGoalStatus) o;

        return getGoalStatus() == that.getGoalStatus();

    }

    @Override
    public int hashCode() {
        return getGoalStatus().hashCode();
    }
}
