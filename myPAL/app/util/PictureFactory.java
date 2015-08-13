package util;

import com.typesafe.config.ConfigFactory;
import models.diary.DiaryActivity;
import models.diary.Picture;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import play.Logger;
import play.i18n.Messages;
import play.mvc.Http;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by ligthartmeu on 12-8-2015.
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
        return new Picture(pictureName, thumbnailName, newDiaryActivity);
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
        if(extension.equalsIgnoreCase("gif"))
            return true;
        return false;
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
