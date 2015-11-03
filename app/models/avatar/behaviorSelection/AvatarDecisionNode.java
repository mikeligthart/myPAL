package models.avatar.behaviorSelection;

import models.avatar.behaviorDefenition.AvatarBehavior;
import models.avatar.behaviorSelection.decisionInformation.AvatarDecisionFunction;
import models.avatar.behaviorSelection.decisionInformation.AvatarUserHistory;
import play.Logger;

import java.util.List;
import java.util.Map;

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
    private AvatarDecisionFunction currentInformation;
    private Map<AvatarDecisionFunction, AvatarDecisionNode> children;

    public AvatarDecisionNode(List<Integer> behaviors, AvatarDecisionFunction currentInformation, Map<AvatarDecisionFunction, AvatarDecisionNode> children){
        this.behaviors = behaviors;
        this.currentInformation = currentInformation;
        this.children = children;
    }

    public List<Integer> getAvatarBehaviors(){
        if(behaviors != null){
            return behaviors;
        } else if (children != null) {
            for(AvatarDecisionFunction df : children.keySet()){
                if(df.equals(currentInformation)){
                    return children.get(df).getAvatarBehaviors();
                }
            }
            return null;
        } else {
            return null;
        }
    }

}
