package models.avatar;

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
public class AvatarState {

    private AvatarBehavior behavior;
    private List<AvatarState> nextStates;

    public AvatarState nextState(int choice){
        if(hasNextState()) {
            if (behavior.getTimer() > 0) {
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

    public String getLine(){
        return behavior.getLine();
    }

    public String getSpeech(){
        return behavior.getSpeech();
    }

    public String getGesture(){
        return behavior.getGesture();
    }

    public int getTimer() {
        return behavior.getTimer();
    }

    public Html getHtml(){
        return behavior.getHtml();
    }

    public void setBehavior(AvatarBehavior behavior) {
        this.behavior = behavior;
    }

    public List<AvatarState> getNextStates() {
        return nextStates;
    }

    public void setNextStates(List<AvatarState> nextStates) {
        this.nextStates = nextStates;
    }
}
