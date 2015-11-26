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
 * @version 1.0 26-11-2015
 */
public enum GoalStatus {
    GOAL_MET_AFTER_LAST_VISIT, GOAL_ADDED, GOAL_ACTIVE, NO_GOAL_ACTIVE;

    private static final Map<String, GoalStatus> nameToValueMap =
            new HashMap<String, GoalStatus>();

    static {
        for (GoalStatus value : EnumSet.allOf(GoalStatus.class)) {
            nameToValueMap.put(value.name(), value);
        }
    }

    public static GoalStatus forName(String name) {
        return nameToValueMap.get(name);
    }
}
