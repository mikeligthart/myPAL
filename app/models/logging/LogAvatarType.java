package models.logging;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

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
 * @version 1.0 23-11-2015
 */
public enum LogAvatarType {
    GREETING, COMPLIMENTED_MET_GOAL, ENCOURAGE_ACTIVE_GOAL, ENCOURAGE_ADDING_GOAL, REACT_HAPPY_SCHOOL, REACT_NEUTRAL_SCHOOL, REACT_SAD_SCHOOL,
    REACT_HAPPY_SPORT, REACT_NEUTRAL_SPORT, REACT_SAD_SPORT, REACT_HAPPY_MEAL, REACT_NEUTRAL_MEAL, REACT_SAD_MEAL,
    REACT_HAPPY_OTHER, REACT_NEUTRAL_OTHER, REACT_SAD_OTHER;

    private static final Map<String, LogAvatarType> nameToValueMap =
            new HashMap<String, LogAvatarType>();

    static {
        for (LogAvatarType value : EnumSet.allOf(LogAvatarType.class)) {
            nameToValueMap.put(value.name(), value);
        }
    }

    public static LogAvatarType forName(String name) {
        return nameToValueMap.get(name);
    }
}
