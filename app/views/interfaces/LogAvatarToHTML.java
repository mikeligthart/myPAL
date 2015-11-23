package views.interfaces;

import models.logging.LogAction;
import models.logging.LogAvatar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
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
 * @version 1.0 23-11-2015
 */
public class LogAvatarToHTML {

    private String timestamp, type;

    private static final SimpleDateFormat timestampFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public LogAvatarToHTML(LogAvatar logAvatar){
        this.timestamp = timestampFormatter.format(logAvatar.getTimestamp());
        this.type = logAvatar.getType().name();
    }

    public static List<LogAvatarToHTML> fromListToList(List<LogAvatar> logAvatars){
        List<LogAvatarToHTML> htmlReadyLogAvatars = new ArrayList<>();
        for(Iterator<LogAvatar> it = logAvatars.iterator(); it.hasNext();){
            htmlReadyLogAvatars.add(new LogAvatarToHTML(it.next()));
        }
        return htmlReadyLogAvatars;
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
