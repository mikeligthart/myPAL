package models.diary.measurement;

import controllers.Diary;
import models.UserMyPAL;
import models.diary.DiaryItem;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * myPAL
 * Purpose: models the concept of diabetes type I related measurements
 *
 * Developped for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 21-8-2015
 */
@Entity
public abstract class DiaryMeasurement extends DiaryItem {

    @Constraints.Required(message = " ")
    private double value;

    @Constraints.Required
    @OneToMany
    @Enumerated
    private DayPart daypart;

    public DiaryMeasurement(){
        super();
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public DayPart getDaypart() {
        return daypart;
    }

    public void setDaypart(DayPart daypart) {
        this.daypart = daypart;
    }

    public static int countByUserAndDates(UserMyPAL user, Date start, Date end){
        return Glucose.find.where().eq("user", user).between("added", new Timestamp(start.getTime()), new Timestamp(end.getTime())).findRowCount() +
                Insulin.find.where().eq("user", user).between("added", new Timestamp(start.getTime()), new Timestamp(end.getTime())).findRowCount();
    }

    public static int addedFromYesterday(UserMyPAL user, Date start, Date end){
        Date yesterday = Date.valueOf(end.toLocalDate().minusDays(1));
        return Glucose.find.where().eq("user", user).between("added", new Timestamp(start.getTime()), new Timestamp(end.getTime())).eq("date", yesterday).findRowCount() +
                Insulin.find.where().eq("user", user).between("added", new Timestamp(start.getTime()), new Timestamp(end.getTime())).eq("date", yesterday).findRowCount();
    }

    public static boolean addedAnythingOnDate(UserMyPAL user, Date date){
        LocalDateTime dateMidnight = date.toLocalDate().atStartOfDay();
        Timestamp from = new Timestamp(java.util.Date.from(dateMidnight.atZone(ZoneId.systemDefault()).toInstant()).getTime());
        LocalDateTime nextDay = dateMidnight.plusDays(1);
        Timestamp till = new Timestamp(java.util.Date.from(nextDay.atZone(ZoneId.systemDefault()).toInstant()).getTime());

        return ((Glucose.find.where().eq("user", user).between("added", from, till).findRowCount() > 0) || Insulin.find.where().eq("user", user).between("added", from, till).findRowCount() > 0);
    }

}
