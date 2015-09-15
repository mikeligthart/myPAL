import play.*;
import com.avaje.ebean.Ebean;
import models.*;

import java.sql.Date;
import java.time.LocalDate;

public class Global extends GlobalSettings {

    public void onStart(Application app) {
        InitialData.insert(app);
    }

    static class InitialData {

        public static void insert(Application app) {
            if(Ebean.find(User.class).findRowCount() == 0) {
                User originalUser = new User("mike.ligthart@gmail.com", "Mike", "Ligthart", Date.valueOf(LocalDate.now()), "mike", UserType.ADMIN);
                Ebean.save(originalUser);
                User testAdmin = new User("test@email.com", "Test", "Admin", Date.valueOf(LocalDate.now()), "secret", UserType.ADMIN);
                Ebean.save(testAdmin);
            }
        }

    }
}