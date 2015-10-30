package models.avatar;

import controllers.routes;
import models.UserMyPAL;
import play.i18n.Messages;
import play.twirl.api.Html;
import util.AppException;

import java.io.File;
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
    private static final String GESTUREROOT = routes.Assets.at("robot_animations/gesture.").url();
    private static final String SPEECHROOT = routes.Assets.at("robot_speech/dialogue.id.").url();
    private static final String SPEECHEXTENSION = ".wav";

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
    

    //AvatarBehaviorFactory object definition
    private AvatarBehaviorFactory(UserMyPAL user){
        this.user = user;
    }

    //Retrieve and build AvatarBehavior from file
    private AvatarBehavior retrieveAvatarBehavior(int id) throws AppException {
        AvatarBehavior behavior = new AvatarBehavior(id, user);

        //Retrieve gesture
        String gestureDef = "dialogue.id." + id + ".gestureExtension";
        if(!Messages.isDefined(gestureDef)){
            throw new AppException(gestureDef + " is not defined");
        }
        String gestureExtension = Messages.get(gestureDef);
        if(gestureExtension.equalsIgnoreCase("mp4")){
            behavior.setGestureType(AvatarGestureType.VIDEO);
        } else {
            behavior.setGestureType(AvatarGestureType.IMAGE);
        }
        String gestureSource = GESTUREROOT + id + "." + gestureExtension;
        if(!new File(gestureSource).exists())
            throw new AppException(gestureDef + " is not defined");
        behavior.setGesture(gestureSource);

        //Retrieve line and speech
        //Detect and randomly select a version of a line
        Random rand = new Random();
        String lineVersionsDef = "dialogue.id." + id + ".versions";
        if(!Messages.isDefined(lineVersionsDef))
            throw new AppException(lineVersionsDef + " is not defined");
        int lineVersions = Integer.parseInt(Messages.get(lineVersionsDef));
        String selectedVersion = Integer.toString(rand.nextInt(lineVersions));
    
        //Detect the number of variables that need to be inserted into the line
        String variableCountDef = "dialogue.id." + id + "." + selectedVersion + ".variables";
        if(!Messages.isDefined(variableCountDef))
            throw new AppException(variableCountDef + " is not defined");
        int variableCount = Integer.parseInt(Messages.get(variableCountDef));
        String line = insertVariableIntoLine(id, selectedVersion, variableCount);
        behavior.setLine(line);

        //Retrieve the right speechSource for the line
        String speechSource = SPEECHROOT + id + "." + selectedVersion + SPEECHEXTENSION;
        if(!new File(speechSource).exists())
            throw new AppException(speechSource + " is not defined");
        behavior.setSpeech(speechSource);

        //Retrieve the right timing of the line
        String timingDef = "dialogue.id." + id + "." + selectedVersion + ".timer-";
        if(!Messages.isDefined(timingDef))
            throw new AppException(timingDef + " is not defined");
        int timing = Integer.parseInt(Messages.get(timingDef));
        behavior.setTimer(timing);

        //Retrieve html source
        String htmlDef = "dialogue.id." + id + "." + selectedVersion + ".html";
        if(!Messages.isDefined(htmlDef))
            throw new AppException(htmlDef + " is not defined");
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
                if(!Messages.isDefined(lineDef))
                    throw new AppException(lineDef + " is not defined");
                return Messages.get(lineDef);
            case 1:
                String variable1_1 = Messages.get("dialogue.id." + id + "." + selectedVersion + ".variable0");
                if(!Messages.isDefined(variable1_1))
                    throw new AppException(variable1_1 + " is not defined");

                String lineDef1 = "dialogue.id." + id + "." + selectedVersion + ".line";
                if(!Messages.isDefined(lineDef1))
                    throw new AppException(lineDef1 + " is not defined");
                return Messages.get(lineDef1, extractVariableFromIndicator(variable1_1));
            case 2:
                String variable2_1 = Messages.get("dialogue.id." + id + "." + selectedVersion + ".variable0");
                if(!Messages.isDefined(variable2_1))
                    throw new AppException(variable2_1 + " is not defined");

                String variable2_2 = Messages.get("dialogue.id." + id + "." + selectedVersion + ".variable1");
                if(!Messages.isDefined(variable2_2))
                    throw new AppException(variable2_2 + " is not defined");

                String lineDef2 = "dialogue.id." + id + "." + selectedVersion + ".line";
                if(!Messages.isDefined(lineDef2))
                    throw new AppException(lineDef2 + " is not defined");
                return Messages.get(lineDef2, extractVariableFromIndicator(variable2_1), extractVariableFromIndicator(variable2_2));
            case 3:
            default:
                String variable3_1 = Messages.get("dialogue.id." + id + "." + selectedVersion + ".variable0");
                if(!Messages.isDefined(variable3_1))
                    throw new AppException(variable3_1 + " is not defined");

                String variable3_2 = Messages.get("dialogue.id." + id + "." + selectedVersion + ".variable1");
                if(!Messages.isDefined(variable3_2))
                    throw new AppException(variable3_2 + " is not defined");

                String variable3_3 = Messages.get("dialogue.id." + id + "." + selectedVersion + ".variable2");
                if(!Messages.isDefined(variable3_3))
                    throw new AppException(variable3_3 + " is not defined");

                String lineDef3 = "dialogue.id." + id + "." + selectedVersion + ".line";
                if(!Messages.isDefined(lineDef3))
                    throw new AppException(lineDef3 + " is not defined");
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
