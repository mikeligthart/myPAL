package models.avatar;

import models.UserMyPAL;

import java.util.*;

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
public class AvatarBehavior {

    private int id;
    private UserMyPAL user;

    private AvatarGestureType gestureType;
    private String gestureSource;
    private List<String> audioSource;
    private Map<Integer, String> linesAndTiming;
    private int lineIndex, numberOfLines;

    public AvatarBehavior(int id, UserMyPAL user){
        this.id = id;
        this.user = user;
        linesAndTiming = new LinkedHashMap<>();
        audioSource = new LinkedList<>();
        lineIndex = 0;
        numberOfLines = 0;
    }

    public void addLine(String line, String source, int timing){
        linesAndTiming.put(timing, line);
        audioSource.add(source);
        numberOfLines += 1;
    }

    public boolean hasNextLine(){
        return (lineIndex < numberOfLines-1);
    }

    public String nextLine() {
        if(hasNextLine()){
            String line = linesAndTiming.get(lineIndex);
            lineIndex += 1;
            return line;
        }
        else
            throw new NoSuchElementException();
    }

    public void reset(){
        lineIndex = 0;
    }

    public Map<Integer, String> getLinesAndTiming() {
        return linesAndTiming;
    }

    public int getNumberOfLines() {
        return numberOfLines;
    }

    public AvatarGestureType getGestureType() {
        return gestureType;
    }

    public void setGestureType(AvatarGestureType gestureType) {
        this.gestureType = gestureType;
    }

    public String getGestureSource() {
        return gestureSource;
    }

    public void setGestureSource(String gestureSource) {
        this.gestureSource = gestureSource;
    }

    public List<String> getAudioSource() {
        return audioSource;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserMyPAL getUser() {
        return user;
    }

    public void setUser(UserMyPAL user) {
        this.user = user;
    }
}
