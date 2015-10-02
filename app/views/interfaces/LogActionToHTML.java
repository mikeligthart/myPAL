package views.interfaces;

import models.logging.LogAction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * myPAL
 * Purpose: Interface class between a LogAction and data suitable for HTML
 *
 * Developped for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 17-7-2015
 */
public class LogActionToHTML {

    private String timestamp, type;

    private static final SimpleDateFormat timestampFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public LogActionToHTML(LogAction logAction){
        this.timestamp = timestampFormatter.format(logAction.getTimestamp());
        this.type = logAction.getType().name();
    }

    public static List<LogActionToHTML> fromListToList(List<LogAction> logActions){
        List<LogActionToHTML> htmlReadyLogActions = new ArrayList<>();
        for(Iterator<LogAction> it = logActions.iterator(); it.hasNext();){
            htmlReadyLogActions.add(new LogActionToHTML(it.next()));
        }
        return htmlReadyLogActions;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
