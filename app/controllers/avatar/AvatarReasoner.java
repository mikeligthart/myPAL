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
    private static final int DEFAULT_BEHAVIOR_ID = 1;

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
        List<Integer> behaviorIds = decisionFactory.getAvatarBehaviors(trigger);
        List<AvatarBehavior> behaviors = new LinkedList<>();
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
            if(behaviorIds.size() > 1){
                try {
                    behaviors.add(defaultBehavior());
                } catch (AppException e) {
                    Logger.error("[AvatarReasoner > selectAvatarBehaviors] (default) AvatarBehavior id=" + DEFAULT_BEHAVIOR_ID + " could not be retrieved. AppException: " + e.getMessage());
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

    private AvatarBehavior defaultBehavior() throws AppException {
        AvatarBehavior defaultBehavior = behaviorFactory.getAvatarBehavior(DEFAULT_BEHAVIOR_ID);
        defaultBehavior.setKeep(true);
        return defaultBehavior;
    }

}
