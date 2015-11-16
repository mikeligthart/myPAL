package models.avatar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.ConfigFactory;
import models.UserMyPAL;
import models.avatar.behaviorSelection.AvatarDecisionNode;
import models.avatar.behaviorSelection.decisionInformation.AvatarDecisionFunction;
import models.avatar.behaviorSelection.decisionInformation.AvatarTrigger;
import models.avatar.behaviorSelection.decisionInformation.AvatarUserHistory;
import models.logging.LogAction;
import models.logging.LogActionType;
import play.Logger;
import play.libs.Json;

import util.ParseAvatarDecisionModelException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
public class AvatarDecisionFactory {

    private static ObjectMapper mapper = new ObjectMapper();
    //Information sources
    private UserMyPAL user;
    private AvatarTrigger currentTrigger;
    private AvatarUserHistory currentUserHistory;

    //Model
    private String decisionModelLocation;
    private String activeDecisionModel;
    private JsonNode model;
    private long modelLastModified;

    public AvatarDecisionFactory(UserMyPAL user) {
        //Load initial information
        this.user = user;
        this.currentTrigger = null;
        this.currentUserHistory = new AvatarUserHistory(null, null);

        //Load initial model
        model = null;
        decisionModelLocation = ConfigFactory.load().getString("private.decisionmodels.location");
        activeDecisionModel = ConfigFactory.load().getString("private.decisionmodels.active");
        loadModelFromFile();
    }

    public List<Integer> getAvatarBehaviorIds(AvatarTrigger trigger){
        refreshModel();
        refreshInformation(trigger);

        AvatarDecisionNode rootNode = null;
        try {
            rootNode = generateRootNode();
        } catch (ParseAvatarDecisionModelException e) {
            Logger.error("[AvatarDecisionFactory > getAvatarBehaviorIds] ParseAvatarDecisionModelException: " + e.getMessage());
        }

        if (rootNode != null) {
            List<Integer> behaviors = rootNode.getAvatarBehaviors();
            return behaviors;
        } else {
            return null;
        }
    }

    private AvatarDecisionNode generateRootNode() throws ParseAvatarDecisionModelException {
        if(model.has("root")) {
            JsonNode rootNode = model.get("root");
            try {
                return parseNode(rootNode);
            } catch (IOException e) {
                throw new ParseAvatarDecisionModelException("IOException - " + e.getMessage(), e.getCause());
            }
        } else {
            throw new ParseAvatarDecisionModelException("'root' is missing in the model structure");
        }
    }

    private AvatarDecisionNode parseNode(JsonNode node) throws ParseAvatarDecisionModelException, IOException {
        if(!node.has("class") || !node.has("behaviors") || !node.has("children")){
            throw new ParseAvatarDecisionModelException("Node structure check for 'class', 'behaviors' and 'children' failed");
        }

        //Check if node is a leaf node and if so retrieve the behaviors
        List<Integer> behavior = null;
        JsonNode behaviorNode = node.get("behaviors");
        if(behaviorNode.isArray() && !behaviorNode.isNull()) {
            behavior = mapper.convertValue(node.get("behaviors"), List.class);
        }

        //If it is a leaf node return it
        if(behavior != null){
            return new AvatarDecisionNode(behavior, null, null);
        }

        //Else search for child nodes
        JsonNode childrenNode = node.get("children");
        if(!childrenNode.isArray() || childrenNode.isNull()) {
            throw new ParseAvatarDecisionModelException("The attribute 'children' of AvatarTrigger is either empty or not in the right format");
        }

        String nodeClass = node.get("class").asText();
        if(nodeClass.equalsIgnoreCase(AvatarTrigger.class.getSimpleName())){
            return parseAvatarTriggerNode(childrenNode);
        } else if(nodeClass.equalsIgnoreCase(AvatarUserHistory.class.getSimpleName())){
            return parseAvatarUserHistoryNode(childrenNode);
        }
        return null;
    }

    private AvatarDecisionNode parseAvatarTriggerNode(JsonNode node) throws ParseAvatarDecisionModelException, IOException{
        Map<AvatarDecisionFunction, AvatarDecisionNode> children = new HashMap<>();
        for(int index = 0; index < node.size(); index++){
            JsonNode avatarTriggerNode = node.get(index).get(Integer.toString(index));
            if(!avatarTriggerNode.has("trigger") || !avatarTriggerNode.has("child")){
                throw new ParseAvatarDecisionModelException("AvatarTriggerNode check for 'trigger' and 'child' failed");
            } else {
                String proposedTrigger = avatarTriggerNode.get("trigger").asText().toUpperCase();
                if(!AvatarTrigger.isLegalTrigger(proposedTrigger)){
                    throw new ParseAvatarDecisionModelException(proposedTrigger + " not recognized as AvatarTrigger");
                }
                AvatarTrigger trigger = new AvatarTrigger(proposedTrigger);
                AvatarDecisionNode child = parseNode(avatarTriggerNode.get("child"));
                children.put(trigger, child);
            }
        }
        return new AvatarDecisionNode(null, currentTrigger, children);
    }

    private AvatarDecisionNode parseAvatarUserHistoryNode(JsonNode node) throws ParseAvatarDecisionModelException, IOException{
        Map<AvatarDecisionFunction, AvatarDecisionNode> children = new HashMap<>();
        for(int index = 0; index < node.size(); index++){
            JsonNode userHistoryNode = node.get(index).get(Integer.toString(index));
            if(!userHistoryNode.has("last") || !userHistoryNode.has("beforeLast") || !userHistoryNode.has("child")){
                throw new ParseAvatarDecisionModelException("AvatarTriggerNode check for 'last', 'beforeLast' and 'child' failed");
            } else {
                LogActionType last = LogActionType.valueOf(userHistoryNode.get("last").asText().toUpperCase());
                LogActionType beforeLast = LogActionType.valueOf(userHistoryNode.get("beforeLast").asText().toUpperCase());
                AvatarUserHistory userHistory = new AvatarUserHistory(last, beforeLast);
                AvatarDecisionNode child = parseNode(userHistoryNode.get("child"));
                children.put(userHistory, child);
            }
        }
        return new AvatarDecisionNode(null, currentUserHistory, children);
    }

    private void refreshInformation(AvatarTrigger trigger){
        List<LogAction> logActions = UserMyPAL.byEmail(user.getEmail()).getLogActions();
        int logActionsSize = logActions.size();
        if(logActionsSize >= 2){
            currentUserHistory = new AvatarUserHistory(logActions.get(logActionsSize-1).getType(), logActions.get(logActionsSize-2).getType());
        } else if(logActionsSize == 1){
            currentUserHistory = new AvatarUserHistory(logActions.get(0).getType(), null);
        } else {
            currentUserHistory = new AvatarUserHistory(null, null);
        }
        this.currentTrigger = trigger;
    }

    private void loadModelFromFile(){
        File modelFile = new File(decisionModelLocation + activeDecisionModel);
        try {
            model = Json.parse(new FileInputStream(modelFile));
            modelLastModified = modelFile.lastModified();
        } catch (IOException e) {
            Logger.error("IOException in AvatarDecisionFactory while trying to refresh the decision model from this location: " + modelFile.getAbsolutePath());
        }
    }

    private void refreshModel(){
        String potentialNewModelLocation = ConfigFactory.load().getString("private.decisionmodels.location");
        String potentialNewActiveModel = ConfigFactory.load().getString("private.decisionmodels.active");
        if(!decisionModelLocation.equals(potentialNewModelLocation) || !activeDecisionModel.equals(potentialNewActiveModel)) {
            decisionModelLocation = potentialNewModelLocation;
            activeDecisionModel = potentialNewActiveModel;
            loadModelFromFile();
        }

        if(modelLastModified != new File(decisionModelLocation + activeDecisionModel).lastModified()){
            loadModelFromFile();
        }
    }



}
