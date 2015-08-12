package views.interfaces;

import com.typesafe.config.ConfigFactory;
import models.diary.DiaryActivity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ligthartmeu on 17-7-2015.
 */
public class DiaryActivityToHTML {

    private int startHour, startMin, endHour, endMin;
    private String type, description, emotion, date;

    private static SimpleDateFormat formatter = new SimpleDateFormat(ConfigFactory.load().getString("date.format"));

    public DiaryActivityToHTML(DiaryActivity diaryActivity){
        this.startHour = diaryActivity.getStarttime().toLocalTime().getHour();
        this.startMin = diaryActivity.getStarttime().toLocalTime().getMinute();
        this.endHour = diaryActivity.getEndtime().toLocalTime().getHour();
        this.endMin = diaryActivity.getEndtime().toLocalTime().getMinute();
        this.type = diaryActivity.getType().toString();
        this.description = diaryActivity.getDescription();
        this.emotion = diaryActivity.getEmotion().name();
        this.date = formatter.format(diaryActivity.getDate());
    }

    public static List<DiaryActivityToHTML> fromListToList(List<DiaryActivity> activities){
        List<DiaryActivityToHTML> htmlReadyAcitivies = new ArrayList<>();
        for(Iterator<DiaryActivity> it = activities.iterator(); it.hasNext();){
            htmlReadyAcitivies.add(new DiaryActivityToHTML(it.next()));
        }
        return htmlReadyAcitivies;
    }

    public static String DateToFormattedString(Date date){
        return formatter.format(date);
    }

    public static String DateToDay(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
    }

    public static String DateToMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return Integer.toString(cal.get(Calendar.MONTH)+1);
    }

    public static String DateToYear(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return Integer.toString(cal.get(Calendar.YEAR));
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMin() {
        return endMin;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getEmotion() {
        return emotion;
    }

}
