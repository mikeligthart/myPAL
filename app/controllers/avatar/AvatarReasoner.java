package controllers.avatar;

import models.UserMyPAL;
import models.avatar.behaviorDefinition.AvatarBehavior;
import models.avatar.behaviorSelection.decisionInformation.AvatarTrigger;
import play.Logger;
import util.AppException;

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
public class AvatarReasoner {

    //Factory management attributes
    private static Map<String, AvatarReasoner> avatarReasoners;

    //Factory management
    public static AvatarReasoner getReasoner(UserMyPAL user){
        AvatarReasoner reasoner;
        if(avatarReasoners == null){
            avatarReasoners = new HashMap<>();
            reasoner = new AvatarReasoner(user);
            avatarReasoners.put(user.getUserName(), reasoner);
        } else {
            if(avatarReasoners.containsKey(user.getUserName())){
                reasoner = avatarReasoners.get(user.getUserName());
            } else {
                reasoner = new AvatarReasoner(user);
                avatarReasoners.put(user.getUserName(), reasoner);
            }
        }
        return reasoner;
    }

    private AvatarBehaviorFactory behaviorFactory;
    private AvatarDecisionFactory decisionFactory;

    private AvatarReasoner(UserMyPAL user) {
        behaviorFactory = new AvatarBehaviorFactory(user);
        decisionFactory = new AvatarDecisionFactory(user);
    }

    public List<AvatarBehavior> selectAvatarBehaviors(AvatarTrigger trigger){
        List<AvatarBehavior> behaviors = behaviorFactory.loadAvatarBehaviors(decisionFactory.getAvatarBehaviors(trigger));
        if(behaviors == null){
            behaviors = new LinkedList<>();
            behaviors.add(null);
        }
        Logger.debug("[AvatarReasoner > selectAvatarBehaviors] triggered by " + trigger.getTrigger() + ", number of selected behaviors" + behaviors.size());
        return behaviors;
    }

    public static void refresh(){
        AvatarBehaviorFactory.refresh();
        AvatarBehaviorBundleFactory.refresh();
    }

}
