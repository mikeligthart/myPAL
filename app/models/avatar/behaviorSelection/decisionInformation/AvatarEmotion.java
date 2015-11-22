package models.avatar.behaviorSelection.decisionInformation;

import models.diary.activity.Emotion;

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
 * @version 1.0 22-11-2015
 */
public class AvatarEmotion implements AvatarDecisionFunction {

    private Emotion emotion;

    public AvatarEmotion(Emotion emotion){
        this.emotion = emotion;
    }

    public Emotion getEmotion() {
        return emotion;
    }

    public void setEmotion(Emotion emotion) {
        this.emotion = emotion;
    }

    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof AvatarEmotion))
            return false;
        if (obj == this)
            return true;

        AvatarEmotion em = (AvatarEmotion) obj;
        return (em.getEmotion() == emotion);
    }

    public static boolean validEmotion(String proposedEmotion){
        for(Emotion emotion : Emotion.values()){
            if(emotion.name().equalsIgnoreCase(proposedEmotion)){
                return true;
            }
        }
        return false;
    }
}
