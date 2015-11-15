package views.interfaces;

import controllers.routes;
import models.avatar.behaviorDefinition.AvatarBehavior;

import java.util.ArrayList;
import java.util.Iterator;
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
 * @version 1.0 15-11-2015
 */
public class AvatarBehaviorToHTML {

    private int id;
    private String gesture, lines;

    public AvatarBehaviorToHTML(AvatarBehavior behavior){
        behavior.load(null);
        id = behavior.getId();
        String gestureUrl = behavior.getGesture();
        String gestureText = String.valueOf(behavior.getGestureId());
        if(behavior.isGestureAVideo()){
            gestureText += " (mp4)";
            gesture = "<a href='" + routes.Application.showGesture(gestureUrl).url() + "' data-toggle='lightbox' data-title='" + gestureText + "'>" + gestureText + "</a>";
        } else {
            gestureText += " (png)";
            gesture = "<a href='" + gestureUrl + "' data-toggle='lightbox' data-title='" + gestureText + "'>" + gestureText + "</a>";
        }

        StringBuilder lineBuilder = new StringBuilder();
        for(Iterator<String> line = behavior.getLines().iterator(); line.hasNext();){
            lineBuilder.append(line.next());
            if(line.hasNext()){
                lineBuilder.append(" <br /> ");
            }
        }
        lines = lineBuilder.toString();

    }

    public static List<AvatarBehaviorToHTML> fromListToList(List<AvatarBehavior> behaviors){
        List<AvatarBehaviorToHTML> htmlReadyBehaviors = new ArrayList<>();
        for(Iterator<AvatarBehavior> it = behaviors.iterator(); it.hasNext();){
            htmlReadyBehaviors.add(new AvatarBehaviorToHTML(it.next()));
        }
        return htmlReadyBehaviors;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGesture() {
        return gesture;
    }

    public void setGesture(String gesture) {
        this.gesture = gesture;
    }

    public String getLines() {
        return lines;
    }

    public void setLines(String lines) {
        this.lines = lines;
    }
}
