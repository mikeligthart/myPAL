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
    private Map<Double, Integer> behaviorsBundles;
    private AvatarDecisionFunction currentInformation;
    private Map<AvatarDecisionFunction, AvatarDecisionNode> children;

    public AvatarDecisionNode(Map<Double, Integer> behaviorsBundles, AvatarDecisionFunction currentInformation, Map<AvatarDecisionFunction, AvatarDecisionNode> children){
        this.behaviorsBundles = behaviorsBundles;
        this.currentInformation = currentInformation;
        this.children = children;
    }

    public List<AvatarBehavior> getAvatarBehaviors(){
        if(behaviorsBundles != null && !behaviorsBundles.isEmpty()){
            double select = new Random().nextDouble();
            SortedSet<Double> cumulativeChances = new TreeSet<>(behaviorsBundles.keySet());
            for(Iterator<Double> chance = cumulativeChances.iterator(); chance.hasNext();){
                double thisChance = chance.next();
                if(select <= thisChance){
                     return getAvatarBehaviorsFromBundle(behaviorsBundles.get(thisChance));
                }
            }
            return getAvatarBehaviorsFromBundle(behaviorsBundles.get(cumulativeChances.last()));
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

    private List<AvatarBehavior> getAvatarBehaviorsFromBundle(int bundleId){
        AvatarReasoner.refresh();
        AvatarBehaviorBundle bundle = AvatarBehaviorBundle.byID(bundleId);
        return bundle.getBehaviors();
    }

}
