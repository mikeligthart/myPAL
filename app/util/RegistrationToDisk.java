package util;

import com.typesafe.config.ConfigFactory;
import play.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
 * @version 1.0 20-10-2015
 */
public class RegistrationToDisk {

    private static final String FILENAME = "registration_diabetes_camp.txt";

    public static void writeToDisk(String name, String opinion1a, String opinion1b, String opinion2){
        try {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(name).append(",");
            stringBuilder.append(opinion1a).append(",");
            stringBuilder.append(opinion1b).append(",");
            stringBuilder.append(opinion2).append("\n");

            File file = new File(ConfigFactory.load().getString("private.data.location") + FILENAME);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(stringBuilder.toString());
            bw.close();

        } catch (IOException e) {
            Logger.error("[RegistrationToDisk > writeToDisk] " + e.getMessage());
        }
    }

}
