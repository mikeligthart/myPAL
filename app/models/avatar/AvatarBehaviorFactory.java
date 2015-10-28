package models.avatar;

import controllers.routes;
import models.UserMyPAL;
import play.i18n.Messages;
import sun.security.x509.AVA;

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

    private static Map<UserMyPAL, AvatarBehaviorFactory> avatarBehaviorFactories;

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

    private UserMyPAL user;

    private AvatarBehaviorFactory(UserMyPAL user){
        this.user = user;
    }

    public AvatarBehavior getAvatarBehavior(int id) {
        //TODO throw exceptions if Message.get() does not supply anything.
        //TODO add routes to correct assets
        AvatarBehavior behavior = new AvatarBehavior(id, user);

        String gestureSource = Messages.get("gestures.id." + id);
        behavior.setGestureSource(gestureSource);
        if(gestureSource.contains(".mp4")){
            behavior.setGestureType(AvatarGestureType.VIDEO);
        } else {
            behavior.setGestureType(AvatarGestureType.IMAGE);
        }
        behavior.setAudioSource(Messages.get("speech.id." + id));

        //Get lines
        Random rand = new Random();
        int lineCount = Integer.parseInt(Messages.get("dialogue.id." + id + ".count"));
        for(int lineIndex = 0; lineIndex < lineCount; lineIndex++){
            int lineVersions = Integer.parseInt(Messages.get("dialogue.id." + id + "." + lineIndex +".versions"));
            String selectedVersion = Integer.toString(rand.nextInt(lineVersions));
            int variableCount = Integer.parseInt(Messages.get("dialogue.id." + id + "." + lineIndex + "." + selectedVersion + ".variableCount"));
            String line = insertVariableIntoLine(id, lineIndex, selectedVersion, variableCount);
            int timing = Integer.parseInt(Messages.get("dialogue.id." + id + "." + lineIndex + "." + selectedVersion + ".timing"));
            behavior.addLine(line, timing);
        }

        return behavior;
    }

    private String insertVariableIntoLine(int id, int lineIndex, String selectedVersion, int variableCount) {
        switch(variableCount){
            case 0:
                return Messages.get("dialogue.id." + id + "." + lineIndex + "." + selectedVersion + ".line");
            case 1:
                String variable1_1 = extractVariableFromIndicator(Messages.get("dialogue.id." + id + "." + lineIndex + "." + selectedVersion + ".variable1"));
                return Messages.get("dialogue.id." + id + "." + lineIndex + "." + selectedVersion + ".line", variable1_1);
            case 2:
                String variable2_1 = extractVariableFromIndicator(Messages.get("dialogue.id." + id + "." + lineIndex + "." + selectedVersion + ".variable1"));
                String variable2_2 = extractVariableFromIndicator(Messages.get("dialogue.id." + id + "." + lineIndex + "." + selectedVersion + ".variable2"));
                return Messages.get("dialogue.id." + id + "." + lineIndex + "." + selectedVersion + ".line", variable2_1, variable2_2);
            case 3:
            default:
                String variable3_1 = extractVariableFromIndicator(Messages.get("dialogue.id." + id + "." + lineIndex + "." + selectedVersion + ".variable1"));
                String variable3_2 = extractVariableFromIndicator(Messages.get("dialogue.id." + id + "." + lineIndex + "." + selectedVersion + ".variable2"));
                String variable3_3 = extractVariableFromIndicator(Messages.get("dialogue.id." + id + "." + lineIndex + "." + selectedVersion + ".variable3"));
                return Messages.get("dialogue.id." + id + "." + lineIndex + "." + selectedVersion + ".line", variable3_1, variable3_2, variable3_3);
        }
    }

    private String extractVariableFromIndicator(String variableIndicator) {
        if(variableIndicator.equalsIgnoreCase("firstName"))
            return user.getFirstName();
        ///else if(insertVariableIndicator.equalsIgnoreCase(""))
        return "";
    }


}
