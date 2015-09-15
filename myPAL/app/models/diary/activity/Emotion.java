package models.diary.activity;

import play.i18n.Messages;

/**
 * myPAL
 * Purpose: Models the concept of the emotional state of the child
 *
 * Developped for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 21-8-2015
 */
public enum Emotion {
    HAPPY, NEUTRAL, SAD;

    public static String toString(Emotion emotion){
        switch (emotion){
            case HAPPY:
                return Messages.get("page.diary.calendar.emotion.HAPPY");
            case SAD:
                return Messages.get("page.diary.calendar.emotion.SAD");
            case NEUTRAL:
            default:
               return Messages.get("page.diary.calendar.emotion.NEUTRAL");
        }
    }

    public static Emotion fromString(String emotion){
        if(emotion.equalsIgnoreCase(Messages.get("page.diary.calendar.emotion.HAPPY"))){
            return Emotion.HAPPY;
        } else if(emotion.equalsIgnoreCase(Messages.get("page.diary.calendar.emotion.NEUTRAL"))){
            return Emotion.NEUTRAL;
        } else if(emotion.equalsIgnoreCase(Messages.get("page.diary.calendar.emotion.SAD"))){
            return Emotion.SAD;
        } else{
            return Emotion.NEUTRAL;
        }
    }
}
