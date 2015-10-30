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
    private static final String SPEECHEXTENSION = ".wav";
    private static final int WAITINGTIME = 250;

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
    public AvatarBehavior reason(){
        updateInformation();
        Logger.debug("[AvatarBehaviorFactory > reason()] reason function call");
        AvatarBehavior root = null;
        if(userHistory.get(userHistory.size()-1).getType() == LogActionType.ADDEDACTIVITY || userHistory.get(userHistory.size()-2).getType() == LogActionType.ADDEDACTIVITY) {
            Logger.debug("[AvatarBehaviorFactory > reason()] loaded special behavior");
            try {
                AvatarBehavior b12 = retrieveAvatarBehavior(12);
                AvatarBehavior b11 = retrieveAvatarBehavior(11);
                b11.addChild(b12);
                AvatarBehavior b10 = retrieveAvatarBehavior(10);
                b10.addChild(b11);
                AvatarBehavior b9 = retrieveAvatarBehavior(9);
                b9.addChild(b10);
                AvatarBehavior b8 = retrieveAvatarBehavior(8);
                b8.addChild(b9);
                AvatarBehavior b7 = retrieveAvatarBehavior(7);
                b7.addChild(b8);
                AvatarBehavior b6 = retrieveAvatarBehavior(6);
                b6.addChild(b8);
                AvatarBehavior b5 = retrieveAvatarBehavior(5);
                b5.addChild(b6);
                b5.addChild(b7);
                AvatarBehavior b4 = retrieveAvatarBehavior(4);
                b4.addChild(b5);
                AvatarBehavior b3 = retrieveAvatarBehavior(3);
                b3.addChild(b4);
                AvatarBehavior b2 = retrieveAvatarBehavior(2);
                b2.addChild(b3);
                AvatarBehavior b1 = retrieveAvatarBehavior(1);
                b1.addChild(b2);
                root = retrieveAvatarBehavior(0);
                root.addChild(b1);
            } catch (AppException e) {
                Logger.error("[AvatarBehaviorFactory > reason()] AppException while retrievingAvatarBehavior " + e.getMessage());
            }
        }
        return root;
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
        /*
        if(!Messages.isDefined(gestureDef)){
            throw new AppException(gestureDef + " is not defined");
        }
        */

        String gestureFilename = Messages.get(gestureDef);
        if(gestureFilename.contains(".mp4")){
            behavior.setGestureType(AvatarGestureType.VIDEO);
        } else {
            behavior.setGestureType(AvatarGestureType.IMAGE);
        }
        String gestureSource = GESTUREROOT + gestureFilename;

        /*
        if(!new File(gestureSource).exists())
            throw new AppException(gestureSource + " is not defined");
        */
        behavior.setGesture(gestureSource);

        //Retrieve line and speech
        //Detect and randomly select a version of a line
        Random rand = new Random();
        String lineVersionsDef = "dialogue.id." + id + ".versions";
        /*
        if(!Messages.isDefined(lineVersionsDef))
            throw new AppException(lineVersionsDef + " is not defined");
        */
        int lineVersions = Integer.parseInt(Messages.get(lineVersionsDef));
        String selectedVersion = "0";
        if(lineVersions > 0){
            selectedVersion = Integer.toString(rand.nextInt(lineVersions));
        }

        //Detect the number of variables that need to be inserted into the line
        String variableCountDef = "dialogue.id." + id + "." + selectedVersion + ".variables";
        /*
        if(!Messages.isDefined(variableCountDef))
            throw new AppException(variableCountDef + " is not defined");
        */
        int variableCount = Integer.parseInt(Messages.get(variableCountDef));
        String line = insertVariableIntoLine(id, selectedVersion, variableCount);
        behavior.setLine(line);

        //Retrieve the right speechSource for the line
        String speechSource = SPEECHROOT + id + "." + selectedVersion + SPEECHEXTENSION;
        File audioFile = new File(speechSource);
        /*
        if(!audioFile.exists())
            throw new AppException(speechSource + " is not defined");
        */
        behavior.setSpeech(speechSource);

        //Retrieve the right timing of the line
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            int durationInMilliSeconds = (Math.round((frames) / format.getFrameRate())*1000) + WAITINGTIME;
            behavior.setTimer(durationInMilliSeconds);
        } catch (UnsupportedAudioFileException e) {
            Logger.error("[AvatarBehaviorFactory > retrieveAvatarBehavior] UnsupportedAudioFileException " + e.getMessage());
            behavior.setTimer(0);
        } catch (IOException e) {
            Logger.error("[AvatarBehaviorFactory > retrieveAvatarBehavior] IOException " + e.getMessage());
            behavior.setTimer(0);
        }


        //Retrieve html source
        String htmlDef = "dialogue.id." + id + "." + selectedVersion + ".html";
        /*
        if(!Messages.isDefined(htmlDef))
            throw new AppException(htmlDef + " is not defined");
        */
        Html html = retrieveHtml(Messages.get(htmlDef));
        behavior.setHtml(html);

        return behavior;
    }

    //Retrieve and build Html belonging to a certain behavior from file
    private Html retrieveHtml(String htmlType) {
        if(htmlType.equalsIgnoreCase("null"))
            return null;

        for(AvatarHtmlType type : AvatarHtmlType.values()){
            if(htmlType.equalsIgnoreCase(type.name())){
                return new AvatarHtmlFactory(user).getHtml(type);
            }
        }
        return null;
    }

    //Up till three different variables can be inserted into a line.
    private String insertVariableIntoLine(int id, String selectedVersion, int variableCount) throws AppException {
        switch(variableCount){
            case 0:
                String lineDef = "dialogue.id." + id + "." + selectedVersion + ".line";
                /*
                if(!Messages.isDefined(lineDef))
                    throw new AppException(lineDef + " is not defined");
                */
                return Messages.get(lineDef);
            case 1:
                String variable1_1 = Messages.get("dialogue.id." + id + "." + selectedVersion + ".variable0");
                /*
                if(!Messages.isDefined(variable1_1))
                    throw new AppException(variable1_1 + " is not defined");
                */
                String lineDef1 = "dialogue.id." + id + "." + selectedVersion + ".line";

                /*
                if(!Messages.isDefined(lineDef1))
                    throw new AppException(lineDef1 + " is not defined");
                */
                return Messages.get(lineDef1, extractVariableFromIndicator(variable1_1));
            case 2:
                String variable2_1 = Messages.get("dialogue.id." + id + "." + selectedVersion + ".variable0");
                /*
                if(!Messages.isDefined(variable2_1))
                    throw new AppException(variable2_1 + " is not defined");
                */

                String variable2_2 = Messages.get("dialogue.id." + id + "." + selectedVersion + ".variable1");
                /*
                if(!Messages.isDefined(variable2_2))
                    throw new AppException(variable2_2 + " is not defined");
                */

                String lineDef2 = "dialogue.id." + id + "." + selectedVersion + ".line";
                /*
                if(!Messages.isDefined(lineDef2))
                    throw new AppException(lineDef2 + " is not defined");
                */
                return Messages.get(lineDef2, extractVariableFromIndicator(variable2_1), extractVariableFromIndicator(variable2_2));
            case 3:
            default:
                String variable3_1 = Messages.get("dialogue.id." + id + "." + selectedVersion + ".variable0");
                /*
                if(!Messages.isDefined(variable3_1))
                    throw new AppException(variable3_1 + " is not defined");
                */

                String variable3_2 = Messages.get("dialogue.id." + id + "." + selectedVersion + ".variable1");
                /*
                if(!Messages.isDefined(variable3_2))
                    throw new AppException(variable3_2 + " is not defined");
                */

                String variable3_3 = Messages.get("dialogue.id." + id + "." + selectedVersion + ".variable2");
                /*
                if(!Messages.isDefined(variable3_3))
                    throw new AppException(variable3_3 + " is not defined");
                */

                String lineDef3 = "dialogue.id." + id + "." + selectedVersion + ".line";
                /*
                if(!Messages.isDefined(lineDef3))
                    throw new AppException(lineDef3 + " is not defined");
                */
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
