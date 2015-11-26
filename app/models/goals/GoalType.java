package models.goals;

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
 * @version 1.0 20-11-2015
 */
public enum GoalType {
    DAILY, TOTAL;

    private static final Map<String, GoalType> nameToValueMap =
            new HashMap<String, GoalType>();

    static {
        for (GoalType value : EnumSet.allOf(GoalType.class)) {
            nameToValueMap.put(value.name(), value);
        }
    }

    public static GoalType forName(String name) {
        return nameToValueMap.get(name);
    }
}
