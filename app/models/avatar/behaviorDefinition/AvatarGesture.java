package models.avatar.behaviorDefinition;

import controllers.routes;
import org.apache.commons.io.FilenameUtils;
import play.Logger;
import play.db.ebean.Model;
import scala.App;
import util.AppException;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.File;

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
 * @version 1.0 9-11-2015
 */
@Entity
public class AvatarGesture extends Model {

    private static final String GESTUREROOT = routes.Assets.at("avatar/gestures/").url();
    public static final String GESTUREFILEROOT = "public/avatar/gestures/";
    public static final String DEFAULTGESTURE = routes.Assets.at("gestures/gesture_1_length-0.png").url();

    @Id
    private int id;

    private File file;
    private String fileName;
    private boolean isVideo;
    private int duration;

    public AvatarGesture(){

    }

    public AvatarGesture(int id, File file, int duration) throws AppException {
        if(file == null){
            throw new AppException("[AvatarGesture > constructor] File cannot be null");
        }
        if(!file.exists()){
            throw new AppException("Gesture file: " + file.getAbsolutePath() + " does not exist");
        }
        this.id = id;
        this.file = file;
        this.duration = duration;
        fileName = file.getName();
        String fileExtension = FilenameUtils.getExtension(fileName);
        if(fileExtension.equalsIgnoreCase("mp4")){
            isVideo = true;
        } else if(fileExtension.equalsIgnoreCase("png")) {
            isVideo = false;
        } else {
            throw new AppException("GestureFiles with extension: " + fileExtension + " is not supported");
        }
    }

    public String getGesture() throws AppException {
        if(!refreshGesture()){
            throw new AppException("GestureFile with id " + id + " has been removed");
        }
        return GESTUREROOT + fileName;
    }

    public boolean refreshGesture() {
        if(file == null){
            return false;
        }
        //TODO: file == null propably not fixed.
        if (!file.exists()){
            File newFile;
            if(isVideo){
                newFile = new File(GESTUREFILEROOT + id + ".png");
            } else {
                newFile = new File(GESTUREFILEROOT + id + ".mp4");
            }
            if(newFile.exists()){
                file = newFile;
                fileName = newFile.getName();
                isVideo = !isVideo;
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean isVideo(){
        return isVideo;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public static Finder<Integer, AvatarGesture> find = new Finder<Integer, AvatarGesture>(Integer.class, AvatarGesture.class);

    public static AvatarGesture byID(int id){
        return find.byId(id);
    }

    public static boolean exists(int id){
        return (byID(id) != null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }
}
