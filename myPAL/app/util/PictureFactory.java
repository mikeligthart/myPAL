package util;

import com.typesafe.config.ConfigFactory;
import models.diary.DiaryActivity;
import models.diary.Picture;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import play.Logger;
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

    private static int width, height, thumpnailSize;

    private static PictureFactory ourInstance = new PictureFactory();

    public static PictureFactory getInstance() {
        return ourInstance;
    }


    private PictureFactory() {
        width = 1024;
        height = 1024;
        thumpnailSize = 400;
    }

    public static int getWidth() {
        return width;
    }

    public static void setWidth(int width) {
        PictureFactory.width = width;
    }

    public static int getHeight() {
        return height;
    }

    public static void setHeight(int height) {
        PictureFactory.height = height;
    }

    public static Picture processUploadedFile(Http.MultipartFormData.FilePart filePart, DiaryActivity newDiaryActivity) {
        try {
            //Resize image if necessary and create a thumbnail
            File file = filePart.getFile();
            BufferedImage image = ImageIO.read(file);
            if(image.getWidth() > width || image.getHeight() > height){
                image = Scalr.resize(image, width, height);
            }
            BufferedImage thumbnail = Scalr.resize(image, thumpnailSize);

            //Create the random unique new filenames for the image and thumbnail
            String extension = FilenameUtils.getExtension(filePart.getFilename());
            String pictureName = File.createTempFile("picture_", "." + extension).getName();
            Pattern extensionPattern = Pattern.compile("\\.[a-zA-Z]+");
            String thumbnailName = pictureName.replace("picture_", "thumbnail_").replaceAll(extensionPattern.pattern(), ".png");
            Logger.debug("[PictureFactory > processUploadedFile] thumbnailName: " + thumbnailName);

            //Save the image and thumbnail to disk
            File pictureFile = new File(ConfigFactory.load().getString("private.data.location") + pictureName);
            File thumbnailFile = new File(ConfigFactory.load().getString("private.data.location") + thumbnailName);
            ImageIO.write(image, extension, pictureFile);
            ImageIO.write(thumbnail, "png", thumbnailFile);

            //Return the new picture
            return new Picture(pictureName, thumbnailName, newDiaryActivity);
        } catch (IOException e) {
            return null;
        }
    }
}
