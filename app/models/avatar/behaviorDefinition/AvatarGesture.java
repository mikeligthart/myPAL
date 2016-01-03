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
import java.util.List;

/**
 * myPAL
 * Purpose: models the gestures of the avatar
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
    public static final String DEFAULTGESTURE = routes.Assets.at("avatar/gestures/gesture_1_length-0.png").url();

    @Id
    private int id;
    private String fileName;
    private boolean isVideo;
    private int duration;

    public AvatarGesture(int id, String fileName, int duration) throws AppException {
        this.id = id;
        this.duration = duration;
        this.fileName = fileName;
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
        if (!new File(GESTUREFILEROOT + fileName).exists()){
            File newFile;
            if(isVideo){
                newFile = new File(GESTUREFILEROOT + id + ".png");
            } else {
                newFile = new File(GESTUREFILEROOT + id + ".mp4");
            }
            if(newFile.exists()){
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public static int getCount() {
        return find.all().size();
    }

    public static int getHighestId(){
        List<AvatarGesture> gestures = find.where().orderBy("id desc").findList();
        return gestures.get(0).getId();
    }
}
