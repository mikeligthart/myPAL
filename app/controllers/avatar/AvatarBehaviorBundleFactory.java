package controllers.avatar;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.typesafe.config.ConfigFactory;
import models.avatar.behaviorDefinition.AvatarBehavior;
import models.avatar.behaviorDefinition.AvatarBehaviorBundle;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import play.Logger;
import play.i18n.Messages;

import java.io.FileWriter;
import java.io.IOException;
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
 * @version 1.0 18-11-2015
 */
public class AvatarBehaviorBundleFactory {

    public static final String BEHAVIORBUNDLEROOT = ConfigFactory.load().getString("private.avatarbehaviorbundles.location");
    public static final String BEHAVIORBUNDLEFILENAME = "bundle.";
    public static final String BEHAVIORBUNDLEFILETYPE = ".json";

    public String latestError;

    public boolean deleteBehaviorBundle(int id){
        if(!AvatarBehaviorBundle.exists(id)){
            Logger.error(Messages.get("error.behaviorBundleDoesNotExistsInDatabase", id));
            latestError = Messages.get("error.behaviorBundleDoesNotExistsInDatabase", id);
            return false;
        }

        AvatarBehaviorBundle behaviorBundle = AvatarBehaviorBundle.byID(id);
        for(AvatarBehavior behavior : behaviorBundle.getBehaviors()){
            behavior.setBehaviorBundle(null);
        }
        behaviorBundle.delete();
        return true;
    }

    public boolean addBehaviorBundle(List<Integer> behaviorIds, String description){
        if(behaviorIds == null){
            Logger.error(Messages.get("error.behaviorBundleWentWrong"));
            latestError = Messages.get("error.behaviorBundleWentWrong");
            return false;
        }
        if(behaviorIds.isEmpty()){
            Logger.error(Messages.get("error.behaviorBundleWentWrong"));
            latestError = Messages.get("error.behaviorBundleWentWrong");
            return false;
        }
        if(description.isEmpty()){
            Logger.error(Messages.get("error.behaviorBundleDescriptionEmpty"));
            latestError = Messages.get("error.behaviorBundleDescriptionEmpty");
            return false;
        }

        AvatarBehaviorBundle newBundle = new AvatarBehaviorBundle();
        List<AvatarBehavior> behaviors = new LinkedList<>();
        for(int id : behaviorIds){
            AvatarBehavior behavior = AvatarBehavior.byID(id);
            if(behavior == null) {
                Logger.error(Messages.get("error.behaviorBundleDoesNotExistsInDatabase", id));
                latestError = Messages.get("error.behaviorBundleDoesNotExistsInDatabase", id);
                return false;
            }
            newBundle.addAvatarBehavior(behavior);
            behaviors.add(behavior);
        }
        newBundle.setDescription(description);
        newBundle.setValid(true);
        newBundle.save();

        for(AvatarBehavior behavior : behaviors){
            behavior.setBehaviorBundle(newBundle);
            behavior.update();
        }

        int id = newBundle.getId();
        //Create new JsonFile
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("isValid", newBundle.isValid());

        JSONArray lineArray = new JSONArray();
        for(Integer behaviorId: behaviorIds){
            lineArray.add(String.valueOf(behaviorId));
        }
        obj.put("lines", lineArray);
        obj.put("description", newBundle.getDescription());

        String fileName = BEHAVIORBUNDLEROOT + BEHAVIORBUNDLEFILENAME + id + BEHAVIORBUNDLEFILETYPE;
        try{
            FileWriter file = new FileWriter(fileName);
            file.write(obj.toJSONString());
            file.close();
        } catch (IOException e) {
            Logger.error("[AvatarBehaviorFactory > addBehavior] IOException: behavior with id " + id + " not writen to file - " + e.getMessage());
        }
        return true;
    }

    public String getLatestError() {
        return latestError;
    }

    public void setLatestError(String latestError) {
        this.latestError = latestError;
    }
}
