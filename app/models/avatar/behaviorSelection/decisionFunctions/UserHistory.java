package models.avatar.behaviorSelection.decisionFunctions;

import models.UserMyPAL;
import models.avatar.behaviorSelection.AvatarDecisionFunction;
import models.avatar.behaviorSelection.AvatarDecisionNode;
import models.avatar.behaviorSelection.AvatarDecisionPacket;
import models.logging.LogAction;
import sun.rmi.runtime.Log;

import java.util.Collection;
import java.util.LinkedList;
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
public class UserHistory extends AvatarDecisionFunction {

    private List<UserHistoryPacket> userHistoryPackets;

    public UserHistory(UserMyPAL user, List<AvatarDecisionPacket> decisionPackets) {
        super(user, decisionPackets);
        userHistoryPackets = new LinkedList<>();
        for(AvatarDecisionPacket decisionPacket : decisionPackets){
            if(decisionPacket.getClass() == UserHistoryPacket.class) {
                userHistoryPackets.add((UserHistoryPacket) decisionPacket);
            }
        }
    }

    @Override
    public AvatarDecisionNode selectNextAvatarDecisionNode() {
        updateInformation();
        List<LogAction> userHistory = decisionInformation.getUserHistory();
        LogAction lastLogAction = userHistory.get(userHistory.size()-1);
        LogAction beforeLastLogAction = userHistory.get(userHistory.size()-2);

        for(UserHistoryPacket userHistoryPacket : userHistoryPackets){
            if(userHistoryPacket.compare(lastLogAction, beforeLastLogAction)){
                return userHistoryPacket.getNextNode();
            }
        }
        return null;
    }
}
