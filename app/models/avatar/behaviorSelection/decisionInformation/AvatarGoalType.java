package models.avatar.behaviorSelection.decisionInformation;

import models.goals.GoalType;

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
public class AvatarGoalType implements AvatarDecisionFunction {

    private GoalType goalType;

    public AvatarGoalType(GoalType goalType) {
        this.goalType = goalType;
    }

    public GoalType getGoalType() {
        return goalType;
    }

    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AvatarGoalType that = (AvatarGoalType) o;

        return getGoalType() == that.getGoalType();

    }

    @Override
    public int hashCode() {
        return getGoalType().hashCode();
    }
}
