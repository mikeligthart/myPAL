package util;

import models.UserMyPAL;
import play.Logger;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by ligthartmeu on 3-12-2015.
 */
public class Questions {

    public static final String ANSWERPATH = "privateData/antwoorden/";

    public static boolean aksQuestion(UserMyPAL user){
        return !(new File(ANSWERPATH + user.getUserName() + ".txt").exists());
    }

    public static void saveAnswers(UserMyPAL user, List<String> answers){
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(ANSWERPATH + user.getUserName() + ".txt"), "utf-8"))) {
            for (String ans: answers){
                writer.write(ans + ", ");
            }
            writer.write(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        } catch (IOException e) {
            Logger.error("[Questions > saveAnswers] Writing answers to file failed! " + e.getLocalizedMessage());
        }
    }
}
