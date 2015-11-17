package models.avatar;

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
            avatarReasoners.put(user.getEmail(), reasoner);
        } else {
            if(avatarReasoners.containsKey(user.getEmail())){
                reasoner = avatarReasoners.get(user.getEmail());
            } else {
                reasoner = new AvatarReasoner(user);
                avatarReasoners.put(user.getEmail(), reasoner);
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
        List<AvatarBehavior> behaviors = new LinkedList<>();
        List<Integer> behaviorIds = decisionFactory.getAvatarBehaviorIds(trigger);
        if(behaviorIds != null) {
            for (Integer id : behaviorIds) {
                if (id == null) {
                    behaviors.add(null);
                } else {
                    try {
                        behaviors.add(behaviorFactory.getAvatarBehavior(id));
                    } catch (AppException e) {
                        Logger.error("[AvatarReasoner > selectAvatarBehaviors] AvatarBehavior id=" + id + " could not be retrieved. AppException: " + e.getMessage());
                    }
                }
            }
        } else {
            behaviors.add(null);
        }
        return behaviors;
    }

    public static void refresh(){
        AvatarBehaviorFactory.refresh();
    }

}
