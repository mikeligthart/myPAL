package models.avatar;

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
 * @version 1.0 28-10-2015
 */
public class AvatarBehavior {

    private AvatarGestureType gestureType;
    private String gestureSource;
    private String audioSource;
    private List<String> text;
    private Iterator<String> lines;

    public int numberOfLines(){
        if(text != null){
            return text.size();
        }
        return 0;
    }

    public boolean hasNextLine(){
        if(lines == null){
            reset();
        }
        return lines.hasNext();
    }

    public String nextLine(){
        if(lines == null){
            reset();
        }
        return lines.next();
    }

    public void reset(){
        lines = text.iterator();
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

    public String getAudioSource() {
        return audioSource;
    }

    public void setAudioSource(String audioSource) {
        this.audioSource = audioSource;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    public Iterator<String> getLines() {
        return lines;
    }

    public void setLines(Iterator<String> lines) {
        this.lines = lines;
    }
}
