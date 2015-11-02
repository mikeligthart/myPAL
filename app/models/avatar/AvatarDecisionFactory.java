package models.avatar;

import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.ConfigFactory;
import models.UserMyPAL;
import models.avatar.behaviorDefenition.AvatarBehavior;
import models.avatar.behaviorSelection.AvatarDecisionNode;
import models.logging.LogAction;
import models.logging.LogActionType;
import play.Logger;
import play.libs.Json;
import sun.security.x509.AVA;
import util.AppException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
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
public class AvatarDecisionFactory {

    //Factory management attributes
    private static Map<UserMyPAL, AvatarDecisionFactory> avatarDecisionFactories;


    //Factory management
    public static AvatarDecisionFactory getFactory(UserMyPAL user){
        AvatarDecisionFactory factory;
        if(avatarDecisionFactories == null){
            avatarDecisionFactories = new HashMap<>();
            factory = new AvatarDecisionFactory(user);
            avatarDecisionFactories.put(user, factory);
        } else {
            if(avatarDecisionFactories.containsKey(user)){
                factory = avatarDecisionFactories.get(user);
            } else {
                factory = new AvatarDecisionFactory(user);
                avatarDecisionFactories.put(user, factory);
            }
        }
        return factory;
    }

    //Information sources
    private UserMyPAL user;
    private String decisionModelLocation;
    private String activeDecisionModel;
    private JsonNode model;

    private AvatarDecisionFactory(UserMyPAL user) {
        this.user = user;
        model = null;
        decisionModelLocation = ConfigFactory.load().getString("private.decisionmodels.location");
        activeDecisionModel = ConfigFactory.load().getString("private.decisionmodels.active");
        loadModelFromFile();
    }

    private void refreshModel(){
        String potentialNewModelLocation = ConfigFactory.load().getString("private.decisionmodels.location");
        String potentialNewActiveModel = ConfigFactory.load().getString("private.decisionmodels.active");
        if(!decisionModelLocation.equals(potentialNewModelLocation) || !activeDecisionModel.equals(potentialNewActiveModel)) {
            decisionModelLocation = potentialNewModelLocation;
            activeDecisionModel = potentialNewActiveModel;
            loadModelFromFile();
        }
    }

    private void loadModelFromFile(){
        File modelFile = new File(decisionModelLocation + activeDecisionModel);
        try {
            model = Json.toJson(new String(Files.readAllBytes(Paths.get(modelFile.getAbsolutePath()))));
        } catch (IOException e) {
            Logger.error("IOException in AvatarDecisionFactory while trying to refresh the decision model from this location: " + modelFile.getAbsolutePath());
        }
    }

    public List<Integer> getAvatarBehaviorIds(){
        AvatarDecisionNode rootNode = generateRootNode();
        if (rootNode != null) {
            return rootNode.getAvatarBehaviors();
        } else {
            return null;
        }
        /*
        List<LogAction> userHistory = user.getLogActions();
        List<Integer> output = new LinkedList<>();
        if(userHistory.get(userHistory.size()-1).getType() == LogActionType.ADDEDACTIVITY || userHistory.get(userHistory.size()-2).getType() == LogActionType.ADDEDACTIVITY) {
            for(int index = 0; index < 13; index++){
                output.add(index);
            }
            output.add(null);
        } else {
            output.add(null);
        }
        return output;
        */
    }

    private AvatarDecisionNode generateRootNode(){
        refreshModel();
        //TODO: Parse model to java objects
        return null;
    }

}
