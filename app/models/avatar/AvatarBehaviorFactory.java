package models.avatar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.ConfigFactory;
import models.UserMyPAL;
import models.avatar.behaviorDefinition.*;
import play.Logger;
import util.AppException;

import java.io.File;
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
 * @version 1.0 28-10-2015
 */
public class AvatarBehaviorFactory {

    //General attributes
    private static final File BEHAVIORROOT = new File(ConfigFactory.load().getString("private.avatarbehaviors.location"));
    private static final String BEHAVIORFILENAME = "behavior.";
    private static final String BEHAVIORFILETYPE = ".json";

    //Factory management attributes
    private static Map<UserMyPAL, AvatarBehaviorFactory> avatarBehaviorFactories;
    private static int numberOfBehaviors = 0;
    private static int numberOfGestures = 0;
    private static Map<Integer, Long> lastModified;
    private static ObjectMapper MAPPER = new ObjectMapper();

    //Factory management
    public static AvatarBehaviorFactory getFactory(UserMyPAL user){
        AvatarBehaviorFactory factory;
        if(avatarBehaviorFactories == null){
            avatarBehaviorFactories = new HashMap<>();
            lastModified = new HashMap<>();
            factory = new AvatarBehaviorFactory(user);
            avatarBehaviorFactories.put(user, factory);
        } else {
            if(avatarBehaviorFactories.containsKey(user)){
                factory = avatarBehaviorFactories.get(user);
            } else {
                factory = new AvatarBehaviorFactory(user);
                avatarBehaviorFactories.put(user, factory);
            }
        }
        return factory;
    }

    //Object attributes
    private UserMyPAL user;

    //AvatarBehaviorFactory object definition
    private AvatarBehaviorFactory(UserMyPAL user){
        this.user = user;
    }

    public AvatarBehavior getAvatarBehavior(int id) throws AppException {
        gestureManagement();
        behaviorManagement();
        AvatarBehavior behavior = AvatarBehavior.byID(id);
        if(behavior == null){
            throw new AppException("Behavior with id " + id + " does not exists in database");
        } else {
            AvatarLineVariables variables = new AvatarLineVariables(user);
            behavior.load(variables);
        }
        return behavior;

    }

    private void behaviorManagement(){
        int currentNumberOfBehaviors = BEHAVIORROOT.list().length;
        //Remove behaviors from database if they have been removed from file
        if (numberOfBehaviors > currentNumberOfBehaviors){
            List<AvatarBehavior> behaviors = AvatarBehavior.find.all();
            for(AvatarBehavior behavior : behaviors){
                if(!new File(BEHAVIORROOT + BEHAVIORFILENAME + behavior.getId() + BEHAVIORFILETYPE).exists()){
                    lastModified.remove(behavior.getId());
                    behavior.deleteBehavior();
                    numberOfBehaviors--;
                }
            }
            numberOfBehaviors = currentNumberOfBehaviors;
        }

        //Add new behaviors or update changed behaviors
        if(numberOfBehaviors < currentNumberOfBehaviors){
            for(File file : BEHAVIORROOT.listFiles()){
                int behaviorId = Integer.valueOf(file.getName().replace(BEHAVIORFILENAME, "").replace(BEHAVIORFILETYPE, ""));
                //If behavior does not exists create a new one and store it in de the database;
                if(!AvatarBehavior.exists(behaviorId)){
                   try {
                        AvatarBehavior newBehavior = MAPPER.readValue(file, AvatarBehavior.class);
                        newBehavior.saveBehavior();
                        lastModified.put(behaviorId, file.lastModified());
                        numberOfBehaviors++;
                   }
                    catch (IOException e) {
                       Logger.error("[AvatarBehaviorFactory > behaviorManagement] Could not read behaviorFile " + file.getAbsolutePath());
                   }
                }
                //if behavior exists, check whether the file is updated and if so update the database entry.
                else {
                    if(lastModified.get(behaviorId) != file.lastModified()){
                        AvatarBehavior updatedBehavior = null;
                        try {
                            updatedBehavior = MAPPER.readValue(file, AvatarBehavior.class);

                        } catch (IOException e) {
                            Logger.error("[AvatarBehaviorFactory > behaviorManagement] Could not read behaviorFile " + file.getAbsolutePath());
                        }
                        if(updatedBehavior != null){
                            AvatarBehavior oldBehavior = AvatarBehavior.byID(behaviorId);
                            oldBehavior.deleteBehavior();
                            updatedBehavior.saveBehavior();
                            lastModified.put(behaviorId, file.lastModified());
                        }

                    }
                }
            }
            numberOfBehaviors = currentNumberOfBehaviors;
        }
    }

    private void gestureManagement(){
        File gestureFileRoot = new File(AvatarGesture.GESTUREFILEROOT);
        int currentNumberOfGestures = gestureFileRoot.list().length;

        //Remove gesture from database that have been removed from file
        if(numberOfGestures > currentNumberOfGestures){
            List<AvatarGesture> gestures = AvatarGesture.find.all();
            for(AvatarGesture gesture : gestures){
                if(!gesture.refreshGesture()){
                    gesture.delete();
                    numberOfGestures--;
                }
            }
        }

        //Add new gestures
        if(numberOfGestures < currentNumberOfGestures){
            for(File file : gestureFileRoot.listFiles()){
                String fileName = file.getName();
                int gestureId = Integer.valueOf(fileName.substring(fileName.indexOf("_")+1, fileName.lastIndexOf("_")));
                int gestureLength = Integer.valueOf(fileName.substring(fileName.indexOf("-")+1, fileName.lastIndexOf(".")));
                if(!AvatarGesture.exists(gestureId)) {
                    try {
                        AvatarGesture gesture = new AvatarGesture(gestureId, fileName, gestureLength);
                        gesture.save();
                        numberOfGestures++;
                    } catch (AppException e) {
                        Logger.error("[AvatarBehaviorFactory > gestureManagement] " + e.getLocalizedMessage());
                    }
                }
            }
        }
    }
}
