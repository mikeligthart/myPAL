package models.avatar;

import controllers.routes;
import models.UserMyPAL;
import models.logging.LogAction;
import models.logging.LogActionType;
import play.Logger;
import play.i18n.Messages;
import play.twirl.api.Html;
import scala.App;
import util.AppException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
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
    private static final String GESTUREROOT = routes.Assets.at("robot_animations/").url();
    private static final String SPEECHROOT = routes.Assets.at("robot_speech/dialogue.id.").url();
    private static final String SPEECHFILEROOT = "public/robot_speech/dialogue.id.";
    private static final String SPEECHEXTENSION = ".wav";
    private static final int WAITINGTIME = 500;

    //Factory management attributes
    private static Map<UserMyPAL, AvatarBehaviorFactory> avatarBehaviorFactories;

    //Factory management
    public static AvatarBehaviorFactory getFactory(UserMyPAL user){
        AvatarBehaviorFactory factory;
        if(avatarBehaviorFactories == null){
            avatarBehaviorFactories = new HashMap<>();
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
    private List<LogAction> userHistory;

    //AvatarBehaviorFactory object definition
    public List<AvatarBehavior> reason(){
        updateInformation();
        List<AvatarBehavior> output = new LinkedList<>();
        if(userHistory.get(userHistory.size()-1).getType() == LogActionType.ADDEDACTIVITY || userHistory.get(userHistory.size()-2).getType() == LogActionType.ADDEDACTIVITY) {
            try {
                for(int index = 0; index < 13; index++){
                    output.add(retrieveAvatarBehavior(index));
                }
                output.add(null);
            } catch (AppException e) {
                Logger.error("[AvatarBehaviorFactory > reason()] AppException while retrievingAvatarBehavior " + e.getMessage());
            }
        } else {
            output.add(null);
        }
        return output;
    }

    private AvatarBehaviorFactory(UserMyPAL user){
        this.user = user;
        userHistory = user.getLogActions();
    }

    private void updateInformation(){
        userHistory = user.getLogActions();
    }

    //Retrieve and build AvatarBehavior from file
    private AvatarBehavior retrieveAvatarBehavior(int id) throws AppException {
        AvatarBehavior behavior = new AvatarBehavior(id, user);

        //Retrieve gesture
        String gestureDef = "dialogue.id." + id + ".gesture";
        String gestureFilename = Messages.get(gestureDef);
        if(gestureFilename.contains(".mp4")){
            behavior.setGestureType(AvatarGestureType.VIDEO);
        } else {
            behavior.setGestureType(AvatarGestureType.IMAGE);
        }
        String gestureSource = GESTUREROOT + gestureFilename;

        behavior.setGesture(gestureSource);

        //Retrieve line and speech
        //Detect and randomly select a version of a line
        Random rand = new Random();
        String lineVersionsDef = "dialogue.id." + id + ".versions";
        int lineVersions = Integer.parseInt(Messages.get(lineVersionsDef));
        String selectedVersion = "0";
        if(lineVersions > 0){
            selectedVersion = Integer.toString(rand.nextInt(lineVersions));
        }

        //Detect the number of variables that need to be inserted into the line
        String variableCountDef = "dialogue.id." + id + "." + selectedVersion + ".variables";
        int variableCount = Integer.parseInt(Messages.get(variableCountDef));
        String line = insertVariableIntoLine(id, selectedVersion, variableCount);
        behavior.setLine(line);

        //Retrieve the right speechSource for the line
        String speechSourceEnd = id + "." + selectedVersion + SPEECHEXTENSION;
        String speechSource = SPEECHROOT + speechSourceEnd;
        behavior.setSpeech(speechSource);
        File audioFile = new File(SPEECHFILEROOT + speechSourceEnd);

        //Retrieve html source
        String htmlDef = "dialogue.id." + id + ".html";
        AvatarHtml avatarHtml = retrieveAvatarHtml(Messages.get(htmlDef));
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
        Logger.debug("AvatarBehaviorFactory > retrieveHtml no match found returning null");
        return new AvatarHtml(1, null);
    }

    //Up till three different variables can be inserted into a line.
    private String insertVariableIntoLine(int id, String selectedVersion, int variableCount) throws AppException {
        switch(variableCount){
            case 0:
                String lineDef = "dialogue.id." + id + "." + selectedVersion + ".line";
                return Messages.get(lineDef);
            case 1:
                String variable1_1 = Messages.get("dialogue.id." + id + "." + selectedVersion + ".variable0");
                String lineDef1 = "dialogue.id." + id + "." + selectedVersion + ".line";
                return Messages.get(lineDef1, extractVariableFromIndicator(variable1_1));
            case 2:
                String variable2_1 = Messages.get("dialogue.id." + id + "." + selectedVersion + ".variable0");
                String variable2_2 = Messages.get("dialogue.id." + id + "." + selectedVersion + ".variable1");
                String lineDef2 = "dialogue.id." + id + "." + selectedVersion + ".line";
                return Messages.get(lineDef2, extractVariableFromIndicator(variable2_1), extractVariableFromIndicator(variable2_2));
            case 3:
            default:
                String variable3_1 = Messages.get("dialogue.id." + id + "." + selectedVersion + ".variable0");
                String variable3_2 = Messages.get("dialogue.id." + id + "." + selectedVersion + ".variable1");
                String variable3_3 = Messages.get("dialogue.id." + id + "." + selectedVersion + ".variable2");
                String lineDef3 = "dialogue.id." + id + "." + selectedVersion + ".line";
                return Messages.get(lineDef3, extractVariableFromIndicator(variable3_1), extractVariableFromIndicator(variable3_2), extractVariableFromIndicator(variable3_3));
        }
    }

    private String extractVariableFromIndicator(String variableIndicator) {
        if(variableIndicator.equalsIgnoreCase("firstName"))
            return user.getFirstName();
        else if(variableIndicator.equalsIgnoreCase("lastName"))
            return user.getLastName();
        return "";
    }


}
