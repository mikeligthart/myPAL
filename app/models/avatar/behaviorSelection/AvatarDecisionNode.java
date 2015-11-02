package models.avatar.behaviorSelection;

import models.avatar.behaviorDefenition.AvatarBehavior;

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
public class AvatarDecisionNode {

    private List<Integer> behaviors;
    private AvatarDecisionFunction decisionFunction;

    public AvatarDecisionNode(List<Integer> behaviors, AvatarDecisionFunction decisionFunction){
        this.behaviors = behaviors;
        this.decisionFunction = decisionFunction;
    }

    public List<Integer> getAvatarBehaviors(){
        if(behaviors != null){
            return behaviors;
        } else if (decisionFunction != null) {
            return decisionFunction.selectNextAvatarDecisionNode().getAvatarBehaviors();
        } else {
            return null;
        }
    }

}
