package models.avatar;

import models.UserMyPAL;
import play.twirl.api.Html;

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
 * @version 1.0 28-10-2015
 */
public class AvatarBehavior {

    //Identifying attributes
    private int id;
    private UserMyPAL user;

    //Behavior attributes
    private AvatarGestureType gestureType;
    private String gesture;
    private String speech;
    private String line;
    private int timer;
    private Html html;

    //State attribute
    private List<AvatarBehavior> nextStates;

    public AvatarBehavior(int id, UserMyPAL user){
        this.id = id;
        this.user = user;
    }

    public AvatarBehavior nextState(int choice){
        if(hasNextState()) {
            if (timer > 0) {
                return nextStates.get(0);
            } else {
                if(nextStates.size() > choice){
                    return nextStates.get(choice);
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    public boolean hasNextState() {
        if(nextStates != null)
            if(!nextStates.isEmpty())
                return true;
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserMyPAL getUser() {
        return user;
    }

    public void setUser(UserMyPAL user) {
        this.user = user;
    }

    public AvatarGestureType getGestureType() {
        return gestureType;
    }

    public void setGestureType(AvatarGestureType gestureType) {
        this.gestureType = gestureType;
    }

    public String getGesture() {
        return gesture;
    }

    public void setGesture(String gesture) {
        this.gesture = gesture;
    }

    public String getSpeech() {
        return speech;
    }

    public void setSpeech(String speech) {
        this.speech = speech;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public Html getHtml() {
        return html;
    }

    public void setHtml(Html html) {
        this.html = html;
    }
}
