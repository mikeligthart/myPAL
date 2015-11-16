package models.avatar.behaviorDefinition;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.typesafe.config.ConfigFactory;
import controllers.routes;
import org.joda.time.Instant;
import org.joda.time.Years;
import play.Logger;
import play.db.ebean.Model;

import javax.persistence.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.sql.Date;
import java.util.Map;
import java.util.StringTokenizer;

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
 * @version 1.0 12-11-2015
 */
@Entity
public class AvatarLine extends Model {

    private static final String SPEECHFILEROOT = "public/avatar/speech/speech.";
    private static final String SPEECHROOT = routes.Assets.at("avatar/speech/speech.").url();
    private static Map<Integer, Integer> WORDSPERMINUTE;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JsonBackReference
    private AvatarBehavior behavior;

    private String line;
    private int version;
    private String speechSource, speechFileSource;
    private boolean isComplete;

    public AvatarLine(AvatarBehavior behavior, String line, int version){
        this.behavior = behavior;
        this.line = line;
        this.version = version;
        speechSource = SPEECHROOT + behavior.getId() + "." + version + ".wav";
        speechFileSource = SPEECHFILEROOT + behavior.getId() + "." + version + ".wav";
        isComplete = new File(speechFileSource).exists();
    }

    public static void fillWordsPerMinute(){
        WORDSPERMINUTE = new HashMap<>();
        WORDSPERMINUTE.put(7, 72);
        WORDSPERMINUTE.put(8, 92);
        WORDSPERMINUTE.put(9, 112);
        WORDSPERMINUTE.put(10, 127);
        WORDSPERMINUTE.put(11, 140);
        WORDSPERMINUTE.put(12, 151);
    }

    public boolean checkIfComplete(){
        boolean newIsComplete = new File(speechFileSource).exists();
        if(newIsComplete == isComplete){
            return false;
        } else {
            isComplete = newIsComplete;
            return true;
        }
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    @Override
    public String toString(){
        return line;
    }

    public AvatarBehavior getBehavior() {
        return behavior;
    }

    public void setBehavior(AvatarBehavior behavior) {
        this.behavior = behavior;
    }

    public static Finder<Integer, AvatarLine> find = new Finder<Integer, AvatarLine>(Integer.class, AvatarLine.class);

    public static AvatarLine byID(int id) {
        return find.byId(id);
    }

    public static List<AvatarLine> byBehavior(AvatarBehavior behavior){
        return find.where().eq("behavior", behavior).findList();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getSpeechSource() {
        return speechSource;
    }

    public void setSpeechSource(String speechSource) {
        this.speechSource = speechSource;
    }

    public int getTimer(Date birthdate) {
        int age = Years.yearsBetween(new Instant(birthdate.getTime()), Instant.now()).getYears();
        int readingTime = getReadingTime(line, age);
        File speechFile = new File(speechFileSource);
        if (speechFile.exists()) {
            AudioInputStream audioInputStream;
            try {
                audioInputStream = AudioSystem.getAudioInputStream(speechFile);
                AudioFormat format = audioInputStream.getFormat();
                long frames = audioInputStream.getFrameLength();
                int speechDuration = (Math.round((frames) / format.getFrameRate()) * 1000);
                return Math.max(speechDuration, readingTime);
            } catch (UnsupportedAudioFileException e) {
                Logger.error("[AvatarLine > calculateTimer()] UnsupportedAudioFileException " + e.getMessage());
                return readingTime;
            } catch (IOException e) {
                Logger.error("[AvatarLine > calculateTimer()] IOException " + e.getMessage());
                return readingTime;
            }
        } else {
            Logger.error("[AvatarLine > calculateTimer()] speechfile: " + speechFile.getName() + " cannot be found");
            return readingTime;
        }
    }

    private static int getWordsPerMinute(int age){
        if(WORDSPERMINUTE == null){
            fillWordsPerMinute();
        }
        if(age <= 7){
            return WORDSPERMINUTE.get(7);
        } else if(age >= 12){
            return WORDSPERMINUTE.get(12);
        } else {
            return WORDSPERMINUTE.getOrDefault(age, 112);
        }
    }

    private static int getReadingTime(String line, int age){
        int millisecondsPerWord = 60000 / getWordsPerMinute(age);
        int numberOfWords = new StringTokenizer(line).countTokens();
        return numberOfWords * millisecondsPerWord;
    }
}
