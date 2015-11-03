package models.avatar.behaviorSelection;

import models.avatar.behaviorDefenition.AvatarBehavior;

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
public class AvatarDecisionNode<E> {

    private List<Integer> behaviors;
    private E currentInformation;
    private Map<E, AvatarDecisionNode> children;

    public AvatarDecisionNode(List<Integer> behaviors, E currentInformation, Map<E, AvatarDecisionNode> children){
        this.behaviors = behaviors;
        this.currentInformation = currentInformation;
        this.children = children;
    }

    public List<Integer> getAvatarBehaviors(){
        if(behaviors != null){
            return behaviors;
        } else if (children != null) {
            AvatarDecisionNode selectedChild = children.getOrDefault(currentInformation, null);
            if(selectedChild != null){
                return selectedChild.getAvatarBehaviors();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
