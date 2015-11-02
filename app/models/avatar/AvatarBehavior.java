package models.avatar;

import models.UserMyPAL;
import play.Logger;
import play.twirl.api.Html;

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
    private AvatarHtml avatarHtml;

    public AvatarBehavior(int id, UserMyPAL user){
        this.id = id;
        this.user = user;
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

    public AvatarHtml getAvatarHtml() {
        return avatarHtml;
    }

    public void setAvatarHtml(AvatarHtml html) {
        this.avatarHtml = html;
    }

    public Html getHtml(int index){
        return avatarHtml.getHtml(index);
    }
}
