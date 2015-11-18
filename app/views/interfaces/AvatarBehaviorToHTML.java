package views.interfaces;

import controllers.routes;
import models.avatar.behaviorDefinition.*;
import play.Logger;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * @version 1.0 15-11-2015
 */
public class AvatarBehaviorToHTML {

    private int id;
    private String gesture, lines, avatarHtmlType;

    public static Iterable<MatchResult> allMatches(
            final Pattern p, final CharSequence input) {
        return new Iterable<MatchResult>() {
            public Iterator<MatchResult> iterator() {
                return new Iterator<MatchResult>() {
                    // Use a matcher internally.
                    final Matcher matcher = p.matcher(input);
                    // Keep a match around that supports any interleaving of hasNext/next calls.
                    MatchResult pending;

                    public boolean hasNext() {
                        // Lazily fill pending, and avoid calling find() multiple times if the
                        // clients call hasNext() repeatedly before sampling via next().
                        if (pending == null && matcher.find()) {
                            pending = matcher.toMatchResult();
                        }
                        return pending != null;
                    }

                    public MatchResult next() {
                        // Fill pending if necessary (as when clients call next() without
                        // checking hasNext()), throw if not possible.
                        if (!hasNext()) { throw new NoSuchElementException(); }
                        // Consume pending so next call to hasNext() does a find().
                        MatchResult next = pending;
                        pending = null;
                        return next;
                    }

                    /** Required to satisfy the interface, but unsupported. */
                    public void remove() { throw new UnsupportedOperationException(); }
                };
            }
        };
    }

    public AvatarBehaviorToHTML(AvatarBehavior behavior){
        behavior.load(null);
        id = behavior.getId();
        String gestureUrl = behavior.getGesture();
        String gestureText = String.valueOf(behavior.getGestureId());
        if(behavior.isGestureAVideo()){
            gestureText += " (mp4)";
            gesture = "<a href='" + routes.Application.showGesture(gestureUrl).url() + "' data-toggle='lightbox' data-title='" + gestureText + "'>" + gestureText + "</a>";
        } else {
            gestureText += " (png)";
            gesture = "<a href='" + gestureUrl + "' data-toggle='lightbox' data-title='" + gestureText + "'>" + gestureText + "</a>";
        }

        StringBuilder lineBuilder = new StringBuilder();
        for(Iterator<AvatarLine> avatarLines = behavior.getAvatarLines().iterator(); avatarLines.hasNext();){
            AvatarLine avatarLine = avatarLines.next();
            avatarLine.checkIfComplete();
            String line = avatarLine.getLine();

            for (MatchResult match : allMatches(Pattern.compile("#\\S*#"), line)) {
                String potentialLineVariable = match.group();
                if(AvatarLineVariables.isLineVariable(potentialLineVariable)){
                    line = line.replace(potentialLineVariable, "<span class='validatedLineVariable'>" + potentialLineVariable + "</span>");
                } else {
                    line = line.replace(potentialLineVariable, "<span class='unvalidatedLineVariable'>" + potentialLineVariable + "</span>");
                }
            }

            if(!avatarLine.isComplete()){
                lineBuilder.append("<p style='color:#FF0000;'>" + line + "</p>");
            } else {
                lineBuilder.append("<p>" + line + "</p>");
            }
        }
        lines = lineBuilder.toString();

        AvatarHtmlType type = behavior.getAvatarHtmlType();
        avatarHtmlType = "<a href='" + AvatarHtml.getAvatarHtmlImage(type) + "' data-toggle='lightbox' data-title='" + type.name() + "'>" + type.name() + "</a>";
    }

    public static List<AvatarBehaviorToHTML> fromListToList(List<AvatarBehavior> behaviors){
        List<AvatarBehaviorToHTML> htmlReadyBehaviors = new LinkedList<>();
        for(Iterator<AvatarBehavior> it = behaviors.iterator(); it.hasNext();){
            htmlReadyBehaviors.add(new AvatarBehaviorToHTML(it.next()));
        }
        return htmlReadyBehaviors;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGesture() {
        return gesture;
    }

    public void setGesture(String gesture) {
        this.gesture = gesture;
    }

    public String getLines() {
        return lines;
    }

    public void setLines(String lines) {
        this.lines = lines;
    }

    public String getAvatarHtmlType() {
        return avatarHtmlType;
    }

    public void setAvatarHtmlType(String avatarHtmlType) {
        this.avatarHtmlType = avatarHtmlType;
    }
}
