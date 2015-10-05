import com.avaje.ebean.Ebean;
import models.Usermypal;
import models.UserType;
import play.Application;
import play.GlobalSettings;

import java.sql.Date;
import java.time.LocalDate;

public class Global extends GlobalSettings {

    public void onStart(Application app) {
        InitialData.insert(app);
    }

    static class InitialData {

        public static void insert(Application app) {
            if(Ebean.find(Usermypal.class).findRowCount() == 0) {
                Usermypal originalUser = new Usermypal("mike.ligthart@gmail.com", "Mike", "Ligthart", Date.valueOf(LocalDate.now()), "mike", UserType.ADMIN);
                Ebean.save(originalUser);
                Usermypal testAdmin = new Usermypal("test@email.com", "Test", "Admin", Date.valueOf(LocalDate.now()), "secret", UserType.ADMIN);
                Ebean.save(testAdmin);
            }
        }

    }
}