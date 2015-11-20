package controllers.goals;

import models.UserMyPAL;
import models.diary.activity.DiaryActivity;
import models.diary.activity.Picture;
import models.diary.measurement.DiaryMeasurement;
import models.goals.GoalTarget;
import models.logging.LogAction;
import play.i18n.Messages;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;

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
 * @version 1.0 20-11-2015
 */
public class GoalFactory {

    public static int getCurrentValue(GoalTarget target, UserMyPAL user, Date startDate, Date deadline){
        java.sql.Date start = new java.sql.Date(startDate.getTime());
        java.sql.Date end = new java.sql.Date(deadline.getTime());
        LocalDate localConStart = start.toLocalDate();

        switch(target){
            case ADDxACTIVITIES:
                return DiaryActivity.countByUserAndDates(user, start, end);
            case ADDxMEASUREMENTS:
                return DiaryMeasurement.countByUserAndDates(user, start, end);
            case ADDxPICTURES:
                return Picture.countByUserAndDates(user, start, end);
            case ADDxYESTERDAY:
                return DiaryActivity.addedFromYesterday(user, start, end) +
                        DiaryMeasurement.addedFromYesterday(user, start, end) +
                        Picture.addedFromYesterday(user, start, end);
            case LOGINS:
                LocalDate localStart = start.toLocalDate();
                int nLogins = 0;
                for(int day = 0; day < Duration.between(localStart, end.toLocalDate()).toDays(); day++){
                    if(LogAction.logInByUserAndDate(user, java.sql.Date.valueOf(localStart.plusDays(day)))){
                        nLogins++;
                    }
                }
                return nLogins;
            case CONACTIVITIES:
                int nConActivities = 0;
                int nConActivitiesHighest = 0;
                for(int day = 0; day < Duration.between(localConStart, end.toLocalDate()).toDays(); day++){
                    if(DiaryActivity.addedAnythingOnDate(user, java.sql.Date.valueOf(localConStart.plusDays(day)))){
                        nConActivities++;
                        if(nConActivities > nConActivitiesHighest){
                            nConActivitiesHighest = nConActivities;
                        }
                    } else {
                        nConActivitiesHighest = 0;
                    }
                }
                return nConActivitiesHighest;
            case CONMEASUREMENTS:
                int nConMeasurements = 0;
                int nConMeasurementsHighest = 0;
                for(int day = 0; day < Duration.between(localConStart, end.toLocalDate()).toDays(); day++){
                    if(DiaryMeasurement.addedAnythingOnDate(user, java.sql.Date.valueOf(localConStart.plusDays(day)))){
                        nConMeasurements++;
                        if(nConMeasurements > nConMeasurementsHighest){
                            nConMeasurementsHighest = nConMeasurements;
                        }
                    } else {
                        nConMeasurementsHighest = 0;
                    }
                }
                return nConMeasurementsHighest;
            case CONPICTURES:
                int nConPictures = 0;
                int nConPicturesHighest = 0;
                for(int day = 0; day < Duration.between(localConStart, end.toLocalDate()).toDays(); day++){
                    if(Picture.addedAnythingOnDate(user, java.sql.Date.valueOf(localConStart.plusDays(day)))){
                        nConPictures++;
                        if(nConPictures > nConPicturesHighest){
                            nConPicturesHighest = nConPictures;
                        }
                    } else {
                        nConPicturesHighest = 0;
                    }
                }
                return nConPicturesHighest;
            case CONLOGINS:
                int nConLogins = 0;
                int nConLoginsHighest = 0;
                for(int day = 0; day < Duration.between(localConStart, end.toLocalDate()).toDays(); day++){
                    if(LogAction.logInByUserAndDate(user, java.sql.Date.valueOf(localConStart.plusDays(day)))){
                        nConLogins++;
                        if(nConLogins > nConLoginsHighest){
                            nConLoginsHighest = nConLogins;
                        }
                    } else {
                        nConLogins = 0;
                    }
                }
                return nConLoginsHighest;
            default:
                return -1;
        }
    }

    public static String getDescription(GoalTarget target){
        switch(target){

            case ADDxACTIVITIES:
                return Messages.get("model.goal.addXActivities");
            case ADDxMEASUREMENTS:
                return Messages.get("model.goal.addXMeasurements");
            case ADDxPICTURES:
                return Messages.get("model.goal.addXPictures");
            case ADDxYESTERDAY:
                return Messages.get("model.goal.addXYesterday");
            case LOGINS:
                return Messages.get("model.goal.addXLogins");
            case CONACTIVITIES:
                return Messages.get("model.goal.addConActivities");
            case CONMEASUREMENTS:
                return Messages.get("model.goal.addConMeasurements");
            case CONPICTURES:
                return Messages.get("model.goal.addConPictures");
            case CONLOGINS:
                return Messages.get("model.goal.addConLogins");
            default:
                return Messages.get("model.goal.noTargetFound");
        }
    }
}
