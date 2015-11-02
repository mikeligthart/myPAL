package models.avatar.behaviorSelection;

import models.UserMyPAL;

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
public abstract class AvatarDecisionFunction {

    protected AvatarDecisionInformation decisionInformation;

    public AvatarDecisionFunction(UserMyPAL user, List<AvatarDecisionPacket> decisionPackets){
        decisionInformation = new AvatarDecisionInformation(user);
    }

    protected void updateInformation(){
        decisionInformation.update();
    }

    public abstract AvatarDecisionNode selectNextAvatarDecisionNode();

}
