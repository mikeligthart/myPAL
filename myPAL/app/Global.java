import controllers.routes;
import models.diary.DiaryActivityType;
import models.diary.DiaryActivityTypeManager;
import play.*;
import com.avaje.ebean.Ebean;
import models.*;
import play.i18n.Messages;

import java.sql.Date;
import java.time.LocalDate;

public class Global extends GlobalSettings {
    //https://github.com/jamesward/zentasks/blob/master/app/Global.java

    public void onStart(Application app) {
        InitialData.insert(app);
    }

    static class InitialData {

        public static void insert(Application app) {
            if(Ebean.find(User.class).findRowCount() == 0) {
                User originalUser = new User("mike.ligthart@gmail.com", "Mike", "Ligthart", Date.valueOf(LocalDate.now()), "mike", UserType.ADMIN);
                Ebean.save(originalUser);
            }
        }

    }
}