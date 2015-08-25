import models.diary.DiaryActivity;
import play.*;
import play.libs.*;
import com.avaje.ebean.Ebean;
import models.*;

import java.sql.*;
import java.sql.Time;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.*;
import views.html.*;

import play.*;
import play.mvc.*;

import static play.mvc.Results.*;

public class Global extends GlobalSettings {
    //https://github.com/jamesward/zentasks/blob/master/app/Global.java

    public void onStart(Application app) {
        InitialData.insert(app);
    }

    static class InitialData {

        public static void insert(Application app) {
            if(Ebean.find(User.class).findRowCount() == 0) {

                Map<String,List<Object>> all = (Map<String,List<Object>>)Yaml.load("initial-data.yml");

                // Insert users first
                Ebean.save(all.get("users"));
            }
        }

    }
}