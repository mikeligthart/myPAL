package models.avatar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.ConfigFactory;
import models.UserMyPAL;
import models.avatar.behaviorDefinition.*;
import org.apache.commons.io.FilenameUtils;
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
                    behavior.delete();
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
                       newBehavior.save();
                       Logger.debug("Added new behavior with id: " + newBehavior.getId() + ", with lines: " + newBehavior.getLines());
                       Logger.debug("Added behavior retrieved from db: lines: " + AvatarBehavior.byID(newBehavior.getId()).getLines());
                       lastModified.put(behaviorId, file.lastModified());
                       numberOfBehaviors++;
                   } catch (IOException e) {
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
                            oldBehavior.delete();
                            updatedBehavior.save();
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

        Logger.debug("gestureManagement -> numberOfGestures: " + numberOfGestures + ", currentNumberOfGestures:" + currentNumberOfGestures);
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
                Logger.debug("gestureId: " + gestureId + ", gestureLength: " + gestureLength);
                Logger.debug("Gesture exists: " + AvatarGesture.exists(gestureId));
                if(!AvatarGesture.exists(gestureId)) {
                    try {
                        Logger.debug("File: " + file.getAbsolutePath());
                        AvatarGesture gesture = new AvatarGesture(gestureId, file, gestureLength);
                        gesture.save();
                        numberOfGestures++;
                    } catch (AppException e) {
                        Logger.error("[AvatarBehaviorFactory > gestureManagement] " + e.getLocalizedMessage());
                    }
                }
            }
        }
    }

    /*
    //Retrieve and build AvatarBehavior from file
    public AvatarBehavior getAvatarBehavior(int id) throws AppException {
        AvatarBehavior behavior = new AvatarBehavior(id);

        //Retrieve gesture
        String gestureSource = GESTUREROOT + Messages.get("dialogue.id." + id + ".gesture");
        if(gestureSource.contains(".mp4")){
            behavior.setGestureType(AvatarGestureType.VIDEO);
        } else {
            behavior.setGestureType(AvatarGestureType.IMAGE);
        }
        behavior.setGesture(gestureSource);

        //Retrieve line and speech
        //Detect and randomly select a version of a line
        Random rand = new Random();
        int lineVersions = Integer.parseInt(Messages.get("dialogue.id." + id + ".versions"));
        String selectedVersion = "0";
        if(lineVersions > 0){
            selectedVersion = Integer.toString(rand.nextInt(lineVersions));
        }

        //Detect the number of variables that need to be inserted into the line
        int variableCount = Integer.parseInt(Messages.get("dialogue.id." + id + "." + selectedVersion + ".variables"));
        String line = insertVariableIntoLine(id, selectedVersion, variableCount);
        behavior.setLine(line);


        //Retrieve the right speechSource for the line
        String speechSourceEnd = id + "." + selectedVersion + SPEECHEXTENSION;
        String speechSource = SPEECHROOT + speechSourceEnd;
        behavior.setSpeech(speechSource);
        File audioFile = new File(SPEECHFILEROOT + speechSourceEnd);

        //Retrieve html source
        AvatarHtml avatarHtml = retrieveAvatarHtml(Messages.get("dialogue.id." + id + ".html"));
        behavior.setAvatarHtml(avatarHtml);
        if(!avatarHtml.isActiveHtml()) {
            //Retrieve the right timing of the line
            AudioInputStream audioInputStream = null;
            try {
                audioInputStream = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat format = audioInputStream.getFormat();
                long frames = audioInputStream.getFrameLength();
                int durationInMilliSeconds = (Math.round((frames) / format.getFrameRate()) * 1000) + WAITINGTIME;
                behavior.setTimer(durationInMilliSeconds);
            } catch (UnsupportedAudioFileException e) {
                Logger.error("[AvatarBehaviorFactory > retrieveAvatarBehavior] UnsupportedAudioFileException " + e.getMessage());
                behavior.setTimer(0);
            } catch (IOException e) {
                Logger.error("[AvatarBehaviorFactory > retrieveAvatarBehavior] IOException " + e.getMessage());
                behavior.setTimer(0);
            }
        } else {
            behavior.setTimer(0);
        }

        return behavior;
    }

    //Retrieve and build Html belonging to a certain behavior from file
    private AvatarHtml retrieveAvatarHtml(String htmlType) {
        for(AvatarHtmlType type : AvatarHtmlType.values()){
            if(htmlType.equalsIgnoreCase(type.name())){
                return new AvatarHtmlFactory(user).getAvatarHtml(type);
            }
        }
        return new AvatarHtml(null);
    }
     */
}
