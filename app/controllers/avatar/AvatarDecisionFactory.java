package controllers.avatar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.ConfigFactory;
import models.UserMyPAL;
import models.avatar.behaviorSelection.AvatarDecisionNode;
import models.avatar.behaviorSelection.AvatarLeafTip;
import models.avatar.behaviorSelection.decisionInformation.*;
import models.diary.activity.DiaryActivity;
import models.diary.activity.DiaryActivityType;
import models.diary.activity.DiaryActivityTypeManager;
import models.diary.activity.Emotion;
import models.goals.Goal;
import models.goals.GoalStatus;
import models.goals.GoalType;
import models.logging.LogAction;
import models.logging.LogActionType;
import models.logging.LogAvatar;
import models.logging.LogAvatarType;
import play.Logger;
import play.libs.Json;

import util.ParseAvatarDecisionModelException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;


/**
 * myPAL
 * Purpose: maintain and control all the AvatarDecisionNode (related) objects
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
    private AvatarEmotion currentEmotion;
    private AvatarActivityType currentActivityType;
    private AvatarGoalStatus currentGoalStatus;
    private AvatarGoalType currentGoalType;

    //Model
    private String decisionModelLocation;
    private String activeDecisionModel;
    private JsonNode model;
    private long modelLastModified;

    public AvatarDecisionFactory(UserMyPAL user) {
        //Load initial information
        this.user = user;
        currentTrigger = null;
        currentUserHistory = new AvatarUserHistory(null, null);
        currentEmotion = null;
        currentActivityType = null;
        currentGoalStatus = null;
        currentGoalType = null;

        //Load initial model
        model = null;
        decisionModelLocation = ConfigFactory.load().getString("private.decisionmodels.location");
        activeDecisionModel = ConfigFactory.load().getString("private.decisionmodels.active");
        loadModelFromFile();
    }

    public List<Integer> getAvatarBehaviors(AvatarTrigger trigger){
        refreshModel();
        refreshInformation(trigger);

        AvatarDecisionNode rootNode = null;
        try {
            rootNode = generateRootNode();
        } catch (ParseAvatarDecisionModelException e) {
            Logger.error("[AvatarDecisionFactory > getAvatarBehaviors] ParseAvatarDecisionModelException: " + e.getMessage());
        }

        if (rootNode != null) {
            AvatarLeafTip leafTip = rootNode.getAvatarBehaviors();
            if(leafTip == null){
                return null;
            }
            LogAvatar.log(user, leafTip.getLogAvatarType());
            return leafTip.getBehaviorIds();
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
        JsonNode behaviorsNode = node.get("behaviors");
        Map<Double, AvatarLeafTip> leafNodes = new LinkedHashMap<>();
        double cumProb = 0.0;
        if(behaviorsNode.isArray() && !behaviorsNode.isNull()) {
            for(int i = 0; i < behaviorsNode.size(); i++){
                JsonNode probNode = behaviorsNode.get(i).get(0);
                JsonNode logNode = behaviorsNode.get(i).get(1);
                JsonNode idsNode = behaviorsNode.get(i).get(2);

                if(!probNode.isDouble()){
                    throw new ParseAvatarDecisionModelException("First element of inner list in a leafNode should be a double indicating the probablity");
                }
                cumProb += probNode.asDouble();

                LogAvatarType avatarLog = LogAvatarType.forName(logNode.asText());
                if(avatarLog == null){
                    throw new ParseAvatarDecisionModelException("Second element of inner list in a leafNode should be a valid LogAvatarType. " + logNode.asText() + " isn't");
                }

                LinkedList<Integer> ids = null;
                if(idsNode.isArray() && !idsNode.isNull()) {
                    ids = mapper.convertValue(idsNode, LinkedList.class);
                } else {
                    throw new ParseAvatarDecisionModelException("Third element of inner list in a leafNode should be list of behavior ids (ints)");
                }
                if(ids == null){
                    throw new ParseAvatarDecisionModelException("Behavior ids could not be retrieved");
                }

                leafNodes.put(cumProb, new AvatarLeafTip(avatarLog, ids));
            }
        }

        //If it is a leaf node return it
        if(leafNodes != null && !leafNodes.isEmpty()){
            return new AvatarDecisionNode(leafNodes, null, null);
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
        } else if(nodeClass.equalsIgnoreCase(AvatarEmotion.class.getSimpleName())){
            return parseAvatarEmotionNode(childrenNode);
        } else if(nodeClass.equalsIgnoreCase(AvatarActivityType.class.getSimpleName())){
            return parseAvatarActivityTypeNode(childrenNode);
        } else if(nodeClass.equalsIgnoreCase(AvatarGoalStatus.class.getSimpleName())){
            return parseAvatarGoalStatusNode(childrenNode);
        } else if(nodeClass.equalsIgnoreCase(AvatarGoalType.class.getSimpleName())){
            return parseAvatarGoalTypeNode(childrenNode);
        }
        return null;
    }

    private AvatarDecisionNode parseAvatarGoalTypeNode(JsonNode node) throws ParseAvatarDecisionModelException, IOException {
        Map<AvatarDecisionInformation, AvatarDecisionNode> children = new HashMap<>();
        for(int index = 0; index < node.size(); index++){
            JsonNode avatarNode = node.get(index).get(Integer.toString(index));
            if(!avatarNode.has("type") || !avatarNode.has("child")){
                throw new ParseAvatarDecisionModelException("AvatarTriggerNode check for 'status' and 'child' failed");
            } else {
                GoalType type = GoalType.forName(avatarNode.get("type").asText());
                if(type == null){
                    throw new ParseAvatarDecisionModelException(avatarNode.get("type").asText() + " not recognized as an GoalType");
                }
                AvatarDecisionNode child = parseNode(avatarNode.get("child"));
                AvatarGoalType avatarGoaltype = new AvatarGoalType(type);
                children.put(avatarGoaltype, child);
            }
        }
        return new AvatarDecisionNode(null, currentGoalType, children);
    }

    private AvatarDecisionNode parseAvatarGoalStatusNode(JsonNode node) throws ParseAvatarDecisionModelException, IOException {
        Map<AvatarDecisionInformation, AvatarDecisionNode> children = new HashMap<>();
        for(int index = 0; index < node.size(); index++){
            JsonNode avatarNode = node.get(index).get(Integer.toString(index));
            if(!avatarNode.has("status") || !avatarNode.has("child")){
                throw new ParseAvatarDecisionModelException("AvatarTriggerNode check for 'status' and 'child' failed");
            } else {
                GoalStatus status = GoalStatus.forName(avatarNode.get("status").asText());
                if(status == null){
                    throw new ParseAvatarDecisionModelException(avatarNode.get("status").asText() + " not recognized as an GoalStatus");
                }
                AvatarDecisionNode child = parseNode(avatarNode.get("child"));
                AvatarGoalStatus avatarGoalStatus = new AvatarGoalStatus(status);
                children.put(avatarGoalStatus, child);
            }
        }
        return new AvatarDecisionNode(null, currentGoalStatus, children);
    }

    private AvatarDecisionNode parseAvatarActivityTypeNode(JsonNode node) throws ParseAvatarDecisionModelException, IOException {
        Map<AvatarDecisionInformation, AvatarDecisionNode> children = new HashMap<>();
        for(int index = 0; index < node.size(); index++){
            JsonNode avatarActivityTypeNode = node.get(index).get(Integer.toString(index));
            if(!avatarActivityTypeNode.has("type") || !avatarActivityTypeNode.has("child")){
                throw new ParseAvatarDecisionModelException("AvatarTriggerNode check for 'type' and 'child' failed");
            } else {
                String proposedActivityType = avatarActivityTypeNode.get("type").asText();
                DiaryActivityType activityType = DiaryActivityTypeManager.getType(user, proposedActivityType);
                if(activityType == null){
                    throw new ParseAvatarDecisionModelException(proposedActivityType + " not recognized as an AvatarActivityType");
                }
                AvatarActivityType avatarActivityType = new AvatarActivityType(activityType);
                AvatarDecisionNode child = parseNode(avatarActivityTypeNode.get("child"));
                children.put(avatarActivityType, child);
            }
        }
        return new AvatarDecisionNode(null, currentActivityType, children);
    }

    private AvatarDecisionNode parseAvatarEmotionNode(JsonNode node) throws ParseAvatarDecisionModelException, IOException {
        Map<AvatarDecisionInformation, AvatarDecisionNode> children = new HashMap<>();
        for(int index = 0; index < node.size(); index++){
            JsonNode avatarEmotionNode = node.get(index).get(Integer.toString(index));
            if(!avatarEmotionNode.has("emotion") || !avatarEmotionNode.has("child")){
                throw new ParseAvatarDecisionModelException("AvatarTriggerNode check for 'emotion' and 'child' failed");
            } else {
                String proposedEmotion = avatarEmotionNode.get("emotion").asText().toUpperCase();
                if(!AvatarEmotion.validEmotion(proposedEmotion)){
                    throw new ParseAvatarDecisionModelException(proposedEmotion + " not recognized as an AvatarEmotion");
                }
                AvatarEmotion emotion = new AvatarEmotion(Emotion.valueOf(proposedEmotion));
                AvatarDecisionNode child = parseNode(avatarEmotionNode.get("child"));
                children.put(emotion, child);
            }
        }
        return new AvatarDecisionNode(null, currentEmotion, children);
    }

    private AvatarDecisionNode parseAvatarTriggerNode(JsonNode node) throws ParseAvatarDecisionModelException, IOException{
        Map<AvatarDecisionInformation, AvatarDecisionNode> children = new HashMap<>();
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
        Map<AvatarDecisionInformation, AvatarDecisionNode> children = new HashMap<>();
        for(int index = 0; index < node.size(); index++){
            JsonNode userHistoryNode = node.get(index).get(Integer.toString(index));
            if(!userHistoryNode.has("last") || !userHistoryNode.has("beforeLast") || !userHistoryNode.has("child")){
                throw new ParseAvatarDecisionModelException("AvatarTriggerNode check for 'last', 'beforeLast' and 'child' failed");
            } else {
                LogActionType last = LogActionType.forName(userHistoryNode.get("last").asText().toUpperCase());
                LogActionType beforeLast = LogActionType.forName(userHistoryNode.get("beforeLast").asText().toUpperCase());
                AvatarUserHistory userHistory = new AvatarUserHistory(last, beforeLast);
                AvatarDecisionNode child = parseNode(userHistoryNode.get("child"));
                children.put(userHistory, child);
            }
        }
        return new AvatarDecisionNode(null, currentUserHistory, children);
    }

    private void refreshInformation(AvatarTrigger trigger){
        List<LogAction> logActions = UserMyPAL.byUserName(user.getUserName()).getLogActions();
        int logActionsSize = logActions.size();
        if(logActionsSize >= 2){
            currentUserHistory = new AvatarUserHistory(logActions.get(logActionsSize-1).getType(), logActions.get(logActionsSize-2).getType());
        } else if(logActionsSize == 1){
            currentUserHistory = new AvatarUserHistory(logActions.get(0).getType(), null);
        } else {
            currentUserHistory = new AvatarUserHistory(null, null);
        }
        this.currentTrigger = trigger;

        DiaryActivity activity = DiaryActivity.lastByUser(user);
        if(activity != null){
            currentEmotion = new AvatarEmotion(activity.getEmotion());
            currentActivityType = new AvatarActivityType(activity.getType());
        }


        Goal goal = Goal.randomActiveGoal(user);
        if(goal == null) {
            currentGoalStatus = new AvatarGoalStatus(GoalStatus.NO_GOAL_ACTIVE);
            currentGoalType = null;
        } else {
            currentGoalStatus = new AvatarGoalStatus(GoalStatus.GOAL_ACTIVE);
            currentGoalType = new AvatarGoalType(goal.getGoalType());
        }

        Timestamp lastVisited = LogAvatar.lastGoalVisit(user);
        if(lastVisited != null){
            goal = Goal.randomGoalAddedAfter(user, lastVisited);
            if(goal != null){
                currentGoalStatus = new AvatarGoalStatus(GoalStatus.GOAL_ADDED);
                currentGoalType = new AvatarGoalType(goal.getGoalType());
            }
            goal = Goal.randomGoalMetAfter(user, lastVisited);
            if(goal != null){
                currentGoalStatus = new AvatarGoalStatus(GoalStatus.GOAL_MET_AFTER_LAST_VISIT);
                currentGoalType = new AvatarGoalType(goal.getGoalType());
            }
        }
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
            Logger.debug("AvatarDecisionFactory refreshModel: file changed load from file");
            loadModelFromFile();
        }
    }



}
