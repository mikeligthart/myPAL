package models.diary;

import com.typesafe.config.ConfigFactory;
import play.i18n.Messages;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Created by ligthartmeu on 15-7-2015.
 */
public class DiarySettings {

    private LocalDate calendarDate;
    public static DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern(ConfigFactory.load().getString("date.format"));

    public DiarySettings(){
        calendarDate = LocalDate.now();
    }

    public void datePlusOne(){
        calendarDate = calendarDate.plusDays(1);
    }

    public void dateMinusOne(){
        calendarDate = calendarDate.minusDays(1);
    }

    public void dateUpdate(String day, String month, String year){
        String delimiter = ConfigFactory.load().getString("date.delimiter");
        if(day.length() == 1){
            day = "0" + day;
        }
        if(month.length() == 1){
            month = "0" + month;
        }
        calendarDate = LocalDate.parse(day + delimiter + month + delimiter + year, DATEFORMATTER);
    }

    public String getDateString(Boolean beautified){
        String dateDisplayButton = calendarDate.format(DATEFORMATTER);
        if(beautified) {
            if (calendarDate.isEqual(LocalDate.now())) {
                dateDisplayButton = Messages.get("page.diary.calendar.today");
            } else if (ChronoUnit.DAYS.between(LocalDate.now(), calendarDate) == 1) {
                dateDisplayButton = Messages.get("page.diary.calendar.tomorrow");
            } else if (ChronoUnit.DAYS.between(LocalDate.now(), calendarDate) == 2) {
                dateDisplayButton = Messages.get("page.diary.calendar.dayaftertomorrow");
            } else if (ChronoUnit.DAYS.between(LocalDate.now(), calendarDate) == -1) {
                dateDisplayButton = Messages.get("page.diary.calendar.yesterday");
            } else if (ChronoUnit.DAYS.between(LocalDate.now(), calendarDate) == -2) {
                dateDisplayButton = Messages.get("page.diary.calendar.daybeforeyesterday");
            }
        }
        return dateDisplayButton;
    }

    public LocalDate getCalendarDate(){
        return calendarDate;
    }

}
