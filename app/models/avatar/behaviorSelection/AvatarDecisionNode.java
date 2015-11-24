package models.avatar.behaviorSelection;

import models.avatar.behaviorSelection.decisionInformation.AvatarDecisionFunction;
import play.Logger;
import sun.rmi.runtime.Log;

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
    private Map<Double, AvatarLeafTip> leafTips;
    private AvatarDecisionFunction currentInformation;
    private Map<AvatarDecisionFunction, AvatarDecisionNode> children;

    public AvatarDecisionNode(Map<Double, AvatarLeafTip> leafTips, AvatarDecisionFunction currentInformation, Map<AvatarDecisionFunction, AvatarDecisionNode> children){
        this.leafTips = leafTips;
        this.currentInformation = currentInformation;
        this.children = children;
    }

    public AvatarLeafTip getAvatarBehaviors(){
        Logger.debug("leafTips == null: " + (leafTips == null) + ", children == null: " + (children == null));
        if(leafTips != null){
            Random rand = new Random();
            double select = rand.nextDouble();
            for(Iterator<Double> it = leafTips.keySet().iterator(); it.hasNext();){
                double prob = it.next();
                if(select <= prob){
                    Logger.debug("Return leaftips");
                    return leafTips.get(prob);
                }
            }
            Logger.debug("Return leaftips null");
            return null;
        } else if (children != null) {
            for(AvatarDecisionFunction df : children.keySet()){
                Logger.debug("Currentinformation: " + currentInformation.getClass().getCanonicalName() + ", df class: " + df.getClass().getCanonicalName());
                if(df.equals(currentInformation)){
                    Logger.debug("Return children");
                    return children.get(df).getAvatarBehaviors();
                }
            }
            Logger.debug("Return children null");
            return null;
        } else {
            Logger.debug("Return null null");
            return null;
        }
    }
}
