package util;

import com.typesafe.config.ConfigFactory;
import models.UserMyPAL;
import models.diary.activity.DiaryActivity;
import models.diary.activity.Picture;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import play.Logger;
import play.i18n.Messages;
import play.mvc.Http;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * myPAL
 * Purpose: factory class saves pictures to the disk, links them to a Picture object and returns that object
 *
 * Developed for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 21-8-2015
 */
public class PictureFactory {

    private int width, height, thumbnailWidth, thumbnailHeight;

    private String latestError;

    public PictureFactory() {
        width = 1024;
        height = 1024;
        thumbnailWidth = 400;
        thumbnailHeight = 400;
    }


    public Picture processUploadedFile(Http.MultipartFormData.FilePart filePart, DiaryActivity newDiaryActivity) {
        Picture picture = processUploadedFile(filePart, newDiaryActivity.getUser(), newDiaryActivity.getDate());
        if (picture != null)
            picture.setDiaryActivity(newDiaryActivity);
        return picture;
    }

    public Picture processUploadedFile(Http.MultipartFormData.FilePart filePart, UserMyPAL user, Date date) {
        //Check if it has the right extension
        String extension = FilenameUtils.getExtension(filePart.getFilename());
        if(!hasSupportedExtension(extension)){
            latestError = Messages.get("error.fileIsNotSupported");
            return null;
        }

        //Load the image
        File file = filePart.getFile();
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            latestError = Messages.get("error.pictureCannotBeStored");
            return null;
        }

        //Resize image if necessary and create a thumbnail
        image = Scalr.resize(image, width, height);
        BufferedImage thumbnail = Scalr.resize(image, thumbnailWidth, thumbnailHeight);

        //Create the random unique new filenames for the image and thumbnail
        String pictureName = "";
        try {
            pictureName = File.createTempFile("picture_", "." + extension).getName();
        } catch (IOException e) {
            latestError = Messages.get("error.pictureCannotBeStored");
            return null;
        }
        Pattern extensionPattern = Pattern.compile("\\.[a-zA-Z]+");
        String thumbnailName = pictureName.replace("picture_", "thumbnail_").replaceAll(extensionPattern.pattern(), ".png");

        //Save the image and thumbnail to disk

        File pictureFile = new File(ConfigFactory.load().getString("private.data.location") + pictureName);
        File thumbnailFile = new File(ConfigFactory.load().getString("private.data.location") + thumbnailName);

        try {
            ImageIO.write(image, extension, pictureFile);
            ImageIO.write(thumbnail, "png", thumbnailFile);
        } catch (IOException e) {
            latestError = Messages.get("error.pictureCannotBeStored");
            return null;
        }

        //Return the new picture
        return new Picture(pictureName, thumbnailName, user, date);
    }

    public static void deletePictureFromActivity(DiaryActivity activity){
        Picture picture = activity.getPicture();
        activity.setPicture(null);
        picture.setDiaryActivity(null);
        picture.setUser(null);
        picture.update();
        activity.update();
        try {
            Files.deleteIfExists(Paths.get(ConfigFactory.load().getString("private.data.location") + picture.getName()));
            Files.deleteIfExists(Paths.get(ConfigFactory.load().getString("private.data.location") + picture.getThumbnail()));
        } catch (IOException e) {
            Logger.error(e.getLocalizedMessage());
        }
        picture.delete();
    }

    public static void deletePictureFromGallery(Picture picture){
        DiaryActivity diaryActivity = picture.getDiaryActivity();
        if(diaryActivity != null){
            diaryActivity.setPicture(null);
            diaryActivity.update();
        }
        picture.setUser(null);
        picture.setDiaryActivity(null);
        try {
            Files.deleteIfExists(Paths.get(ConfigFactory.load().getString("private.data.location") + picture.getName()));
            Files.deleteIfExists(Paths.get(ConfigFactory.load().getString("private.data.location") + picture.getThumbnail()));
        } catch (IOException e) {
            Logger.error(e.getLocalizedMessage());
        }
        picture.delete();
    }

    private boolean hasSupportedExtension(String extension){
        if(extension.equalsIgnoreCase("jpg"))
            return true;
        if(extension.equalsIgnoreCase("jpeg"))
            return true;
        if(extension.equalsIgnoreCase("png"))
            return true;
        if(extension.equalsIgnoreCase("bmp"))
            return true;
        return extension.equalsIgnoreCase("gif");
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    public void setThumbnailWidth(int thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }

    public int getThumbnailHeight() {
        return thumbnailHeight;
    }

    public void setThumbnailHeight(int thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }

    public String getLatestError(){
        return latestError;
    }
}
