package controllers.goals;

import controllers.routes;
import models.UserMyPAL;
import models.diary.activity.DiaryActivity;
import models.diary.activity.Picture;
import models.diary.measurement.DiaryMeasurement;
import models.goals.Goal;
import models.goals.GoalTarget;
import models.goals.GoalType;
import models.logging.LogAction;
import play.Logger;
import play.i18n.Messages;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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

    private  static final String GOALICONROOT = routes.Assets.at("images/goalIcons/").url();

    public static int getCurrentValue(GoalTarget target, UserMyPAL user, Date startDate, Date deadline){
        java.sql.Date start = new java.sql.Date(startDate.getTime());
        java.sql.Date end = new java.sql.Date(deadline.getTime());
        LocalDateTime localConStart = start.toLocalDate().atStartOfDay();
        LocalDateTime localConEnd = end.toLocalDate().atStartOfDay();

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
            case CONACTIVITIES:
                int nConActivities = 0;
                int nConActivitiesHighest = 0;
                for(int day = 0; day < Duration.between(localConStart, localConEnd).toDays(); day++){
                    if(DiaryActivity.addedAnythingOnDate(user, java.sql.Date.valueOf(localConStart.plusDays(day).toLocalDate()))){
                        nConActivities++;
                        if(nConActivities > nConActivitiesHighest){
                            nConActivitiesHighest = nConActivities;
                        }
                    } else {
                        nConActivities = 0;
                    }
                }
                return nConActivitiesHighest;
            case CONMEASUREMENTS:
                int nConMeasurements = 0;
                int nConMeasurementsHighest = 0;
                for(int day = 0; day < Duration.between(localConStart, localConEnd).toDays(); day++){
                    if(DiaryMeasurement.addedAnythingOnDate(user, java.sql.Date.valueOf(localConStart.plusDays(day).toLocalDate()))){
                        nConMeasurements++;
                        if(nConMeasurements > nConMeasurementsHighest){
                            nConMeasurementsHighest = nConMeasurements;
                        }
                    } else {
                        nConMeasurements = 0;
                    }
                }
                return nConMeasurementsHighest;
            case CONPICTURES:
                int nConPictures = 0;
                int nConPicturesHighest = 0;
                for(int day = 0; day < Duration.between(localConStart, localConEnd).toDays(); day++){
                    if(Picture.addedAnythingOnDate(user, java.sql.Date.valueOf(localConStart.plusDays(day).toLocalDate()))){
                        nConPictures++;
                        if(nConPictures > nConPicturesHighest){
                            nConPicturesHighest = nConPictures;
                        }
                    } else {
                        nConPictures = 0;
                    }
                }
                return nConPicturesHighest;
            case CONLOGINS:
                int nConLogins = 0;
                int nConLoginsHighest = 0;

                for(int day = 0; day < Duration.between(localConStart, localConEnd).toDays(); day++){
                    if(LogAction.logInByUserAndDate(user, java.sql.Date.valueOf(localConStart.plusDays(day).toLocalDate()))){
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

    public static String getName(GoalTarget target){
        switch(target){

            case ADDxACTIVITIES:
                return Messages.get("model.goal.activities");
            case ADDxMEASUREMENTS:
                return Messages.get("model.goal.measurements");
            case ADDxPICTURES:
                return Messages.get("model.goal.pictures");
            case ADDxYESTERDAY:
                return Messages.get("model.goal.yesterday");
            case CONACTIVITIES:
                return Messages.get("model.goal.conActivities");
            case CONMEASUREMENTS:
                return Messages.get("model.goal.conMeasurements");
            case CONPICTURES:
                return Messages.get("model.goal.conPictures");
            case CONLOGINS:
                return Messages.get("model.goal.conLogins");
            default:
                return Messages.get("model.goal.noTargetFound");
        }
    }

    public static String getIcon(GoalTarget target){
        switch(target){
            case ADDxACTIVITIES:
                return GOALICONROOT + "activitiesX.png";
            case ADDxMEASUREMENTS:
                return GOALICONROOT + "measurementsX.png";
            case ADDxPICTURES:
                return GOALICONROOT + "picturesX.png";
            case ADDxYESTERDAY:
                return GOALICONROOT + "yesterdayX.png";
            case CONACTIVITIES:
                return GOALICONROOT + "activitiesCon.png";
            case CONMEASUREMENTS:
                return GOALICONROOT + "measurementsCon.png";
            case CONPICTURES:
                return GOALICONROOT + "picturesCon.png";
            case CONLOGINS:
                return GOALICONROOT + "loginCon.png";
            default:
                return GOALICONROOT + "noGoal.png";
        }
    }

    public static String getIconWhenMet(GoalTarget target){
        switch(target){
            case ADDxACTIVITIES:
                return GOALICONROOT + "activitiesX-met.png";
            case ADDxMEASUREMENTS:
                return GOALICONROOT + "measurementsX-met.png";
            case ADDxPICTURES:
                return GOALICONROOT + "picturesX-met.png";
            case ADDxYESTERDAY:
                return GOALICONROOT + "yesterdayX-met.png";
            case CONACTIVITIES:
                return GOALICONROOT + "activitiesCon-met.png";
            case CONMEASUREMENTS:
                return GOALICONROOT + "measurementsCon-met.png";
            case CONPICTURES:
                return GOALICONROOT + "picturesCon-met.png";
            case CONLOGINS:
                return GOALICONROOT + "loginCon-met.png";
            default:
                return GOALICONROOT + "noGoal-met.png";
        }
    }

    public static List<GoalTarget> getDailyTargets(){
        List<GoalTarget> dailyTargets = new LinkedList<>();
        dailyTargets.add(GoalTarget.ADDxACTIVITIES);
        dailyTargets.add(GoalTarget.ADDxMEASUREMENTS);
        dailyTargets.add(GoalTarget.ADDxPICTURES);
        dailyTargets.add(GoalTarget.ADDxYESTERDAY);
        return dailyTargets;
    }

    public static List<GoalTarget> getTotalTargets(){
        List<GoalTarget> totalTargets = new LinkedList<>();
        totalTargets.add(GoalTarget.CONLOGINS);
        totalTargets.add(GoalTarget.CONACTIVITIES);
        totalTargets.add(GoalTarget.CONMEASUREMENTS);
        totalTargets.add(GoalTarget.CONPICTURES);
        return totalTargets;
    }

    public static int dailyTargetsSize(){
        return getDailyTargets().size();
    }

    public static int totalTargetsSize(){
        return getTotalTargets().size();
    }

    public static List<Goal> getGoals(UserMyPAL user, boolean met, GoalType type){
        List<Goal> goals = Goal.getGoalsPerType(user, type);
        List<Goal> filteredGoals = new LinkedList<>();

        for(Goal goal : goals){
            if(goal.isMet() == met){
                filteredGoals.add(goal);
            }
            goal.update();
        }

        return filteredGoals;
    }
}
