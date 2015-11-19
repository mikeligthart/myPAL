package controllers.avatar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.ConfigFactory;
import models.avatar.behaviorDefinition.AvatarBehavior;
import models.avatar.behaviorDefinition.AvatarBehaviorBundle;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import play.Logger;
import play.i18n.Messages;
import play.libs.Json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
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
    private static ObjectMapper MAPPER = new ObjectMapper();

    public String latestError;

    public static void refresh(){
        manageBehaviorBundles();
    }


    public boolean deleteBehaviorBundle(int id){
        if(!AvatarBehaviorBundle.exists(id)){
            Logger.error(Messages.get("error.behaviorBundleDoesNotExistsInDatabase", id));
            latestError = Messages.get("error.behaviorBundleDoesNotExistsInDatabase", id);
            return false;
        }

        deleteBundleFromDB(id);

        try {
            Files.deleteIfExists(new File(BEHAVIORBUNDLEROOT + BEHAVIORBUNDLEFILENAME + id + BEHAVIORBUNDLEFILETYPE).toPath());
        } catch (IOException e) {
            Logger.error("[AvatarBehaviorBundleFactory > deleteBehavior] IOException while deleting file " + e.getLocalizedMessage());
        }
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

        int bundleId = AvatarBehaviorBundle.getHighestId() + 1;
        AvatarBehaviorBundle newBundle = new AvatarBehaviorBundle();
        newBundle.setId(bundleId);
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

        //Create new JsonFile
        JSONObject obj = new JSONObject();
        obj.put("id", bundleId);
        obj.put("isValid", newBundle.isValid());

        JSONArray lineArray = new JSONArray();
        for(Integer behaviorId: behaviorIds){
            lineArray.add(String.valueOf(behaviorId));
        }
        obj.put("lines", lineArray);
        obj.put("description", newBundle.getDescription());

        String fileName = BEHAVIORBUNDLEROOT + BEHAVIORBUNDLEFILENAME + bundleId + BEHAVIORBUNDLEFILETYPE;
        try{
            FileWriter file = new FileWriter(fileName);
            file.write(obj.toJSONString());
            file.close();
            newBundle.setLastModified(new File(fileName).lastModified());
            newBundle.update();
        } catch (IOException e) {
            Logger.error("[AvatarBehaviorFactory > addBehavior] IOException: behavior with id " + bundleId + " not writen to file - " + e.getMessage());
        }
        return true;
    }

    public String getLatestError() {
        return latestError;
    }

    private static void manageBehaviorBundles(){
        int bundleFileCount = new File(BEHAVIORBUNDLEROOT).list().length;
        int bundleDBCount = AvatarBehaviorBundle.getCount();

        //Remove behaviors from database if they have been removed from file
        if (bundleDBCount > bundleFileCount){
            List<AvatarBehaviorBundle> bundles = AvatarBehaviorBundle.find.all();
            for(AvatarBehaviorBundle bundle : bundles){
                if(!new File(BEHAVIORBUNDLEROOT + BEHAVIORBUNDLEFILENAME + bundle.getId() + BEHAVIORBUNDLEFILETYPE).exists()){
                    deleteBundleFromDB(bundle.getId());
                }
            }
        }

        //Add new behaviors or update changed behaviors

        for(File file : new File(BEHAVIORBUNDLEROOT).listFiles()){
            int bundleId = Integer.valueOf(file.getName().replace(BEHAVIORBUNDLEFILENAME, "").replace(BEHAVIORBUNDLEFILETYPE, ""));

            if(bundleDBCount < bundleFileCount) {
                //If behavior does not exists create a new one and store it in de the database;
                if (!AvatarBehaviorBundle.exists(bundleId)) {
                    try {
                        JsonNode bundleNode = Json.parse(new FileInputStream(file));

                        AvatarBehaviorBundle newBundle = new AvatarBehaviorBundle();
                        newBundle.setId(bundleId);
                        newBundle.setLastModified(file.lastModified());

                        List<AvatarBehavior> behaviors = new LinkedList<>();
                        JsonNode linesNode = bundleNode.get("lines");
                        for (int i = 0; i < linesNode.size(); i++) {
                            int behaviorIdInLine = linesNode.get(i).asInt();
                            AvatarBehavior behavior = AvatarBehavior.byID(behaviorIdInLine);
                            if (behavior == null) {
                                Logger.error(Messages.get("error.behaviorBundleDoesNotExistsInDatabase", behaviorIdInLine));
                            }
                            newBundle.addAvatarBehavior(behavior);
                            behaviors.add(behavior);
                        }
                        newBundle.setDescription(bundleNode.get("description").asText());
                        newBundle.setValid(bundleNode.get("isValid").asBoolean());
                        newBundle.save();

                        for (AvatarBehavior behavior : behaviors) {
                            behavior.setBehaviorBundle(newBundle);
                            behavior.update();
                        }
                    } catch (IOException e) {
                        Logger.error("[AvatarBehaviorBundleFactory > manageBehaviorBundles] IOException Could not read behaviorFile during creation: " + file.getAbsolutePath());
                    }
                }
            } else {
                //check if bundles need to be updated
                updateBundle(file, bundleId);
            }
        }
    }

    private static void updateBundle(File file, int bundleId){
        AvatarBehaviorBundle bundle = AvatarBehaviorBundle.byID(bundleId);
        if(bundle.getLastModified() != file.lastModified()){
            try {
                JsonNode bundleNode = Json.parse(new FileInputStream(file));
                if(bundleNode != null) {
                    bundle.setLastModified(file.lastModified());

                    for(AvatarBehavior nullefyBehavior : bundle.getBehaviors()){
                        nullefyBehavior.setBehaviorBundle(null);
                        nullefyBehavior.update();
                    }

                    bundle.setBehavior(new LinkedList<>());

                    List<AvatarBehavior> behaviors = new LinkedList<>();
                    JsonNode linesNode = bundleNode.get("lines");
                    for(int i = 0; i <  linesNode.size(); i++){
                        int behaviorIdInLine = linesNode.get(i).asInt();
                        AvatarBehavior behavior = AvatarBehavior.byID(behaviorIdInLine);
                        if(behavior == null) {
                            Logger.error(Messages.get("error.behaviorBundleDoesNotExistsInDatabase", behaviorIdInLine));
                        }
                        bundle.addAvatarBehavior(behavior);
                        behaviors.add(behavior);
                    }
                    bundle.setDescription(bundleNode.get("description").asText());
                    bundle.setValid(bundleNode.get("isValid").asBoolean());
                    bundle.update();

                    for(AvatarBehavior behavior : behaviors){
                        behavior.setBehaviorBundle(bundle);
                        behavior.update();
                    }
                }
            } catch (IOException e) {
                Logger.error("[AvatarBehaviorBundleFactory > manageBehaviorBundles] IOException could not read behaviorFile during updating: " + file.getAbsolutePath());
            }

        }

    }
    private static void deleteBundleFromDB(int id){
        if(AvatarBehaviorBundle.exists(id)) {
            AvatarBehaviorBundle behaviorBundle = AvatarBehaviorBundle.byID(id);
            for (AvatarBehavior behavior : behaviorBundle.getBehaviors()) {
                behavior.setBehaviorBundle(null);
            }
            behaviorBundle.delete();
        }
    }

}
