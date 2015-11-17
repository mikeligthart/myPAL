package models.avatar.behaviorDefinition;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import controllers.routes;
import play.Logger;
import play.db.ebean.Model;
import play.twirl.api.Html;
import util.AppException;

import javax.persistence.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
@Entity
public class AvatarBehavior extends Model {

    //Behavior attributes that can be loaded using json
    @Id
    private int id;
    private int gestureId;
    @OneToMany
    @Enumerated(EnumType.STRING)
    private AvatarHtmlType avatarHtmlType;
    private List<String> lines;

    //Process attributes
    //For database
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "behavior")
    @JsonManagedReference
    private List<AvatarLine> avatarLines;
    private long lastModified;

    //Not for database
    private int version;
    private AvatarLineVariables variables;
    private AvatarHtml avatarHtml;
    private AvatarGesture avatarGesture;
    private AvatarLine avatarLine;


    public AvatarBehavior() {
    }

    public void load(AvatarLineVariables variables) {
        Random rand = new Random();
        lines = this.getLines();
        if (lines.size() > 0) {
            version = rand.nextInt(lines.size());
        } else {
            version = 0;
        }
        avatarGesture = AvatarGesture.byID(gestureId);
        avatarLine = avatarLines.get(version);
        avatarHtml = AvatarHtml.getAvatarHtml(avatarHtmlType);
        this.variables = variables;
    }

    @Transient
    public String getLine() {
        return variables.processLine(lines.get(version));
    }

    @Transient
    public String getSpeech() {
        return avatarLine.getSpeechSource();
    }

    @Transient
    public int getTimer() {
        if (!avatarHtml.isActiveHtml()) {
            int lineDuration = avatarLines.get(version).getTimer(variables.getUser().getBirthdate());
            int gestureDuration = avatarGesture.getDuration();
            return Math.max(lineDuration, gestureDuration);
        } else {
            return 0;
        }
    }

    @Transient
    public boolean isGestureAVideo() {
        return avatarGesture.isVideo();
    }

    @Transient
    public Html getHtml(int index) {
        return avatarHtml.render(index);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Transient
    public List<String> getLines() {
        List<String> output = new LinkedList<>();
        for(AvatarLine line : AvatarLine.byBehavior(this)){
            output.add(line.getLine());
        }
        return output;
    }

    @Transient
    public void setLines(List<String> lines) {
        this.lines = lines;
        avatarLines = new LinkedList<>();
        int index = 0;
        for(String line : lines){
            avatarLines.add(new AvatarLine(this, line, index));
            index++;
        }
    }

    public int getGestureId() {
        return gestureId;
    }

    public void setGestureId(int gestureId) {
        this.gestureId = gestureId;
    }

    public AvatarHtmlType getAvatarHtmlType() {
        return avatarHtmlType;
    }

    public void setAvatarHtmlType(AvatarHtmlType avatarHtmlType) {
        this.avatarHtmlType = avatarHtmlType;
    }

    @Transient
    public String getGesture() {
        try {
            return avatarGesture.getGesture();
        } catch (AppException e) {
            Logger.error("Gesture could not be retrieved: " + e.getLocalizedMessage());
            return AvatarGesture.DEFAULTGESTURE;
        }
    }

    @Transient
    public AvatarLineVariables getVariables() {
        return variables;
    }

    @Transient
    public void setVariables(AvatarLineVariables variables) {
        this.variables = variables;
    }

    @Transient
    public int getVersion() {
        return version;
    }

    @Transient
    public void setVersion(int version) {
        this.version = version;
    }

    @Transient
    public AvatarHtml getAvatarHtml() {
        return avatarHtml;
    }

    @Transient
    public void setAvatarHtml(AvatarHtml html) {
        this.avatarHtml = html;
    }

    @Transient
    public AvatarGesture getAvatarGesture() {
        return avatarGesture;
    }

    @Transient
    public void setAvatarGesture(AvatarGesture avatarGesture) {
        this.avatarGesture = avatarGesture;
    }

    public List<AvatarLine> getAvatarLines() {
        return avatarLines;
    }

    public void setAvatarLines(List<AvatarLine> avatarLines) {
        this.avatarLines = avatarLines;
    }

    @Transient
    public AvatarLine getAvatarLine() {
        return avatarLine;
    }

    @Transient
    public void setAvatarLine(AvatarLine avatarLine) {
        this.avatarLine = avatarLine;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public void saveBehavior(){
        this.save();
        for(AvatarLine line : avatarLines){
            line.save();
        }
    }

    public void deleteBehavior(){
        for(AvatarLine line : avatarLines){
            line.delete();
        }
        this.delete();
    }

    public static Finder<Integer, AvatarBehavior> find = new Finder<Integer, AvatarBehavior>(Integer.class, AvatarBehavior.class);

    public static AvatarBehavior byID(int id) {
        return find.byId(id);
    }

    public static boolean exists(int id) {
        return (byID(id) != null);
    }

    public static int getCount(){
        return find.all().size();
    }

    public static int getHighestId(){
        List<AvatarBehavior> behaviors = find.where().orderBy("id desc").findList();
        return behaviors.get(0).getId();
    }
}


