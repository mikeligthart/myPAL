import com.avaje.ebean.Ebean;
import models.UserMyPAL;
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
            if(Ebean.find(UserMyPAL.class).findRowCount() == 0) {
                UserMyPAL originalUser = new UserMyPAL("mike.ligthart", "Mike", "Ligthart", Date.valueOf(LocalDate.now()), "mike", UserType.ADMIN, "067835880");
                Ebean.save(originalUser);
                UserMyPAL testAdmin = new UserMyPAL("test@email.com", "Test", "Admin", Date.valueOf(LocalDate.now()), "secret", UserType.ADMIN, "");
                Ebean.save(testAdmin);
            }
        }

    }
}