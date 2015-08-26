import controllers.routes;
import models.diary.DiaryActivityType;
import models.diary.DiaryActivityTypeManager;
import play.*;
import com.avaje.ebean.Ebean;
import models.*;

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
            if(Ebean.find(DiaryActivityType.class).findRowCount() == 0){
                DiaryActivityType school = DiaryActivityTypeManager.getDiaryActivityType("School", null);
                school.setIconLocation(routes.Assets.at("images/school_icon.png").url());
                school.setColor("#308dd4");
                Ebean.save(school);
                DiaryActivityType sport = DiaryActivityTypeManager.getDiaryActivityType("Sport", null);
                sport.setIconLocation(routes.Assets.at("images/sport_icon.png").url());
                sport.setColor("#8dd430");
                Ebean.save(sport);
                DiaryActivityType meal = DiaryActivityTypeManager.getDiaryActivityType("Meal", null);
                meal.setIconLocation(routes.Assets.at("images/meal_icon.png").url());
                meal.setColor("#d4308d");
                Ebean.save(meal);
            }
        }

    }
}