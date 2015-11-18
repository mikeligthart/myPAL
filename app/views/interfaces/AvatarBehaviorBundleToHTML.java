package views.interfaces;

import models.avatar.behaviorDefinition.AvatarBehavior;
import models.avatar.behaviorDefinition.AvatarBehaviorBundle;
import play.i18n.Messages;

import java.util.ArrayList;
import java.util.Iterator;
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
public class AvatarBehaviorBundleToHTML {

    private int id;
    private String behaviors, isValid, description;

    public AvatarBehaviorBundleToHTML(AvatarBehaviorBundle behaviorBundle){

        id = behaviorBundle.getId();
        if(behaviorBundle.isValid()){
            isValid = Messages.get("page.general.yes");
        } else {
            isValid = Messages.get("page.general.no");
        }
        description = behaviorBundle.getDescription();

        StringBuilder behaviorStringBuilder = new StringBuilder();
        for(AvatarBehavior behavior : behaviorBundle.getBehaviors()){
            behaviorStringBuilder.append("<button class='btn bnt-default behaviorButton' value='").append(behavior.getId()).append("'>").append(behavior.getId()).append("</button>").append(", ");
        }
        behaviorStringBuilder.deleteCharAt(behaviorStringBuilder.length()-1);
        behaviorStringBuilder.deleteCharAt(behaviorStringBuilder.length()-1);
        behaviors = behaviorStringBuilder.toString();

    }

    public int getId() {
        return id;
    }

    public String getBehaviors() {
        return behaviors;
    }


    public String getIsValid() {
        return isValid;
    }

    public String getDescription() {
        return description;
    }

    public static List<AvatarBehaviorBundleToHTML> fromListToList(List<AvatarBehaviorBundle> behaviorBundles){
        List<AvatarBehaviorBundleToHTML> htmlReadyBehaviorBundles = new LinkedList<>();
        for(Iterator<AvatarBehaviorBundle> it = behaviorBundles.iterator(); it.hasNext();){
            htmlReadyBehaviorBundles.add(new AvatarBehaviorBundleToHTML(it.next()));
        }
        return htmlReadyBehaviorBundles;
    }
}
