package controllers.avatar;

import models.avatar.behaviorDefinition.AvatarGesture;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import play.Logger;
import play.i18n.Messages;
import play.mvc.Http;
import util.AppException;

import java.io.*;
import java.nio.file.Files;

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
 * @version 1.0 18-11-2015
 */
public class AvatarGestureFactory {

    private static final int MAXDURATION = 60000;
    private String latestError;

    public boolean addGesture(Http.MultipartFormData.FilePart filePart, int duration){
        if(filePart == null){
            latestError = Messages.get("error.noGestureFileSelected");
            return false;
        }

        //Check whether the file has a valid extension
        String extension = FilenameUtils.getExtension(filePart.getFilename());
        if(!hasSupportedExtension(extension)){
            latestError = Messages.get("error.gestureFileIsNotSupported");
            return false;
        }

        //Determine the gesture variables and there validity
        duration *= 1000;
        if(duration < 0 || duration > MAXDURATION){
            latestError = Messages.get("error.gestureDurationIsNotValid");
            return false;
        }
        int id = AvatarGesture.getHighestId() +  1;
        String gestureFileName = "gesture_" + id + "_length-" + duration + "." + extension;

        //Try to store the gestureFile in the right place on the disk
        try {
            FileInputStream gestureInputStream = new FileInputStream(filePart.getFile());
            FileOutputStream gestureOutputStream = new FileOutputStream(new File(AvatarGesture.GESTUREFILEROOT + gestureFileName));
            IOUtils.copy(gestureInputStream, gestureOutputStream);
        } catch (FileNotFoundException e) {
            Logger.error("[AvatarGestureFactory > addGesture] FileNotFoundException: " + e.getLocalizedMessage());
            latestError = Messages.get("error.gestureCannotBeStored");
            return false;
        } catch (IOException e) {
            Logger.error("[AvatarGestureFactory > addGesture] IOException: " + e.getLocalizedMessage());
            latestError = Messages.get("error.gestureCannotBeStored");
            return false;
        }

        //Add created store gesture to the database
        try {
            AvatarGesture newGesture = new AvatarGesture(id, gestureFileName, duration);
            newGesture.save();
            return true;
        } catch (AppException e) {
            Logger.error("[AvatarGestureFactory > addGesture] AppException: " + e.getLocalizedMessage());
            latestError = Messages.get("error.gestureCannotBeSavedToDatabase");
            return false;
        }
    }

    public boolean deleteGesture(int id){
        if(!AvatarGesture.exists(id)){
            Logger.error(Messages.get("error.gestureDoesNotExistsInDatabase", id));
            latestError = Messages.get("error.gestureDoesNotExistsInDatabase", id);
            return false;
        }

        AvatarGesture gesture = AvatarGesture.byID(id);
        File gestureFile = new File(AvatarGesture.GESTUREFILEROOT + gesture.getFileName());
        try {
            if(!Files.deleteIfExists(gestureFile.toPath())){
                Logger.error("GestureFile (id: " + id + ") does not exist: " + gestureFile.getAbsolutePath());
            }
        } catch (IOException e) {
            Logger.error("GestureFile (id: " + id + ") does not exist: " + gestureFile.getAbsolutePath());
        }
        gesture.delete();
        return true;
    }

    private boolean hasSupportedExtension(String extension) {
        return (extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("mp4"));
    }

    public String getLatestError() {
        return latestError;
    }

    public void setLatestError(String latestError) {
        this.latestError = latestError;
    }
}
