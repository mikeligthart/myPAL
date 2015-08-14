package models.diary;

import play.i18n.Messages;

/**
 * Created by ligthartmeu on 6-8-2015.
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
