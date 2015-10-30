package models.avatar;

import models.UserMyPAL;
import play.twirl.api.Html;

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

    private int id;
    private UserMyPAL user;

    private AvatarGestureType gestureType;
    private String gestureSource;
    private String audioSource;
    private String line;
    private int timer;
    private Html html;

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

    public String getGestureSource() {
        return gestureSource;
    }

    public void setGestureSource(String gestureSource) {
        this.gestureSource = gestureSource;
    }

    public String getAudioSource() {
        return audioSource;
    }

    public void setAudioSource(String audioSource) {
        this.audioSource = audioSource;
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
