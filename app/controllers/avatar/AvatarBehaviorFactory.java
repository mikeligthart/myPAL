package controllers.avatar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.ConfigFactory;
import controllers.Admin;
import models.UserMyPAL;
import models.avatar.behaviorDefinition.*;
import play.Logger;
import play.libs.Json;
import util.AppException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import java.io.FileWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
 * @version 1.0 28-10-2015
 */
public class AvatarBehaviorFactory {

    //General attributes
    public static final String BEHAVIORROOT = ConfigFactory.load().getString("private.avatarbehaviors.location");
    public static final String BEHAVIORFILENAME = "behavior.";
    public static final String BEHAVIORFILETYPE = ".json";
    private static ObjectMapper MAPPER = new ObjectMapper();

    //Object attributes
    private UserMyPAL user;

    //AvatarBehaviorFactory object definition
    public AvatarBehaviorFactory(UserMyPAL user){
        this.user = user;
    }

    public static void refresh(){
        gestureManagement();
        behaviorManagement();
    }

    public AvatarBehavior getAvatarBehavior(int id) throws AppException {
        refresh();
        AvatarBehavior behavior = AvatarBehavior.byID(id);
        if(behavior == null){
            throw new AppException("Behavior with id " + id + " does not exists in database");
        } else {
            AvatarLineVariables variables = new AvatarLineVariables(user);
            behavior.load(variables);
        }
        return behavior;
    }

    @SuppressWarnings("unchecked")
    public static void addBehavior(int gestureId, List<String> lines, AvatarHtmlType avatarHtmlType){
        refresh();
        int id = AvatarBehavior.getHighestId()+1;

        //Create new JsonFile
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("gestureId", gestureId);

        JSONArray lineArray = new JSONArray();
        for(String line : lines){
            lineArray.add(line);
        }
        obj.put("lines", lineArray);
        obj.put("avatarHtmlType", avatarHtmlType.name());

        String fileName = BEHAVIORROOT + BEHAVIORFILENAME + id + BEHAVIORFILETYPE;
        try{
            FileWriter file = new FileWriter(fileName);
            file.write(obj.toJSONString());
            file.close();
        } catch (IOException e) {
            Logger.error("[AvatarBehaviorFactory > addBehavior] IOException: behavior with id " + id + " not writen to file - " + e.getMessage());
        }
        //Save behavior to database
        AvatarBehavior behavior = new AvatarBehavior();
        behavior.setId(id);
        behavior.setGestureId(gestureId);
        behavior.setLines(lines);
        behavior.setAvatarHtmlType(avatarHtmlType);
        behavior.setLastModified(new File(fileName).lastModified());
        behavior.saveBehavior();
    }

    public static boolean deleteBehavior(int id){
        try {
            Files.deleteIfExists(new File(BEHAVIORROOT + BEHAVIORFILENAME + id + BEHAVIORFILETYPE).toPath());
        } catch (IOException e) {
            Logger.error("[AvatarBehaviorFactory > deleteBehavior] IOException while deleting file " + e.getLocalizedMessage());
            return false;
        }

        AvatarBehavior deleteThisBehavior = AvatarBehavior.byID(id);
        if(deleteThisBehavior != null){
            deleteThisBehavior.deleteBehavior();
            return true;
        } else {
            return false;
        }
    }

    private static void behaviorManagement(){
        int behaviorFileCount = new File(BEHAVIORROOT).list().length;
        int behaviorDBCount = AvatarBehavior.getCount();
        //Remove behaviors from database if they have been removed from file
        if (behaviorDBCount > behaviorFileCount){
            List<AvatarBehavior> behaviors = AvatarBehavior.find.all();
            for(AvatarBehavior behavior : behaviors){
                if(!new File(BEHAVIORROOT + BEHAVIORFILENAME + behavior.getId() + BEHAVIORFILETYPE).exists()){
                    behavior.deleteBehavior();
                }
            }
        }

        //Add new behaviors or update changed behaviors
        for(File file : new File(BEHAVIORROOT).listFiles()){
            int behaviorId = Integer.valueOf(file.getName().replace(BEHAVIORFILENAME, "").replace(BEHAVIORFILETYPE, ""));

            if(behaviorDBCount < behaviorFileCount) {
                //If behavior does not exists create a new one and store it in de the database;
                if (!AvatarBehavior.exists(behaviorId)) {
                    try {
                        //AvatarBehavior newBehavior = MAPPER.readValue(file, AvatarBehavior.class);
                        JsonNode behaviorNode = Json.parse(new FileInputStream(file));
                        AvatarBehavior newBehavior = new AvatarBehavior();
                        newBehavior.setId(behaviorId);
                        newBehavior.setGestureId(behaviorNode.get("gestureId").asInt());
                        newBehavior.setAvatarHtmlType(AvatarHtmlType.valueOf(behaviorNode.get("avatarHtmlType").asText()));

                        JsonNode linesNode = behaviorNode.get("lines");
                        List<String> lines = new LinkedList<>();
                        for (int i = 0; i < linesNode.size(); i++) {
                            lines.add(linesNode.get(i).asText());
                        }
                        newBehavior.setLines(lines);
                        newBehavior.setLastModified(file.lastModified());
                        newBehavior.saveBehavior();
                    } catch (IOException e) {
                        Logger.error("[AvatarBehaviorFactory > behaviorManagement] IOException Could not read behaviorFile " + file.getAbsolutePath());
                    }
                }
            }
            //if behavior exists, check whether the file is updated and if so update the database entry.
            AvatarBehavior behavior = AvatarBehavior.byID(behaviorId);
            if(behavior.getLastModified() != file.lastModified()){
                Admin.clearDatabaseOfBehaviors();
                try {
                    //updatedBehavior = MAPPER.readValue(file, AvatarBehavior.class);
                    JsonNode behaviorNode = Json.parse(new FileInputStream(file));
                    AvatarBehavior updateBehavior = new AvatarBehavior();
                    updateBehavior.setId(behaviorId);
                    updateBehavior.setGestureId(behaviorNode.get("gestureId").asInt());
                    behavior.setAvatarHtmlType(AvatarHtmlType.valueOf(behaviorNode.get("avatarHtmlType").asText()));

                    JsonNode linesNode = behaviorNode.get("lines");
                    List<String> lines = new LinkedList<>();
                    for(int i = 0; i < linesNode.size(); i++){
                        lines.add(linesNode.get(i).asText());
                    }
                    updateBehavior.setLines(lines);
                    updateBehavior.setLastModified(file.lastModified());
                    behavior.deleteBehavior();
                    updateBehavior.saveBehavior();
                } catch (IOException e) {
                    Logger.error("[AvatarBehaviorFactory > behaviorManagement] Could not read behaviorFile " + file.getAbsolutePath());
                }

            }
        }
    }

    private static void gestureManagement(){
        File gestureFileRoot = new File(AvatarGesture.GESTUREFILEROOT);
        int gestureFileCount = gestureFileRoot.list().length;
        int gestureDBCount = AvatarGesture.getCount();

        //Remove gesture from database that have been removed from file
        if(gestureDBCount > gestureFileCount){
            List<AvatarGesture> gestures = AvatarGesture.find.all();
            for(AvatarGesture gesture : gestures){
                if(!gesture.refreshGesture()){
                    gesture.delete();
                }
            }
        }

        //Add new gestures
        if(gestureDBCount < gestureFileCount){
            for(File file : gestureFileRoot.listFiles()){
                String fileName = file.getName();
                int gestureId = Integer.valueOf(fileName.substring(fileName.indexOf("_")+1, fileName.lastIndexOf("_")));
                int gestureLength = Integer.valueOf(fileName.substring(fileName.indexOf("-")+1, fileName.lastIndexOf(".")));
                if(!AvatarGesture.exists(gestureId)) {
                    try {
                        AvatarGesture gesture = new AvatarGesture(gestureId, fileName, gestureLength);
                        gesture.save();
                    } catch (AppException e) {
                        Logger.error("[AvatarBehaviorFactory > gestureManagement] " + e.getLocalizedMessage());
                    }
                }
            }
        }
    }

    public static void clearDatabase(){
        List<AvatarBehavior> behaviors = AvatarBehavior.find.all();
        for(AvatarBehavior behavior : behaviors){
            behavior.deleteBehavior();
        }
    }

}
