package models.avatar.behaviorSelection;

import controllers.avatar.AvatarBehaviorBundleFactory;
import controllers.avatar.AvatarBehaviorFactory;
import controllers.avatar.AvatarReasoner;
import models.avatar.behaviorDefinition.AvatarBehavior;
import models.avatar.behaviorDefinition.AvatarBehaviorBundle;
import models.avatar.behaviorSelection.decisionInformation.AvatarDecisionFunction;
import play.Logger;

import java.util.*;

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

    //BehaviorBundle ids with their cumulative changes of getting selected
    private List<Integer> behaviorIds;
    private AvatarDecisionFunction currentInformation;
    private Map<AvatarDecisionFunction, AvatarDecisionNode> children;

    public AvatarDecisionNode(List<Integer> behaviorIds, AvatarDecisionFunction currentInformation, Map<AvatarDecisionFunction, AvatarDecisionNode> children){
        this.behaviorIds = behaviorIds;
        this.currentInformation = currentInformation;
        this.children = children;
    }

    public List<Integer> getAvatarBehaviors(){
        if(behaviorIds != null){
            return behaviorIds;
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
