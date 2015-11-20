package models.goals;

import com.fasterxml.jackson.annotation.JsonBackReference;
import controllers.goals.GoalFactory;
import models.UserMyPAL;
import play.db.ebean.Model;
import play.i18n.Messages;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.util.Date;
import java.time.Instant;

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

@Entity
public class Goal extends Model {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @OneToMany
    @Enumerated(EnumType.STRING)
    private GoalTarget target;

    private int targetValue;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date deadline;

    private boolean met;

    @OneToMany
    @Enumerated(EnumType.STRING)
    private GoalType goalType;

    @ManyToOne()
    @JsonBackReference
    private UserMyPAL user;

    public Goal(){
        startDate = Date.from(Instant.now());
        met = false;
    }

    public String getProgress(){
        if(isMet()){
            return "100%";
        }
        else if(isDeadlinePassed()){
            return Messages.get("model.goal.failed");
        } else {
            DecimalFormat numberFormat = new DecimalFormat("#.0");
            return numberFormat.format(((double) GoalFactory.getCurrentValue(target, user, startDate, deadline) / targetValue) * 100.0) + "%";
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GoalTarget getTarget() {
        return target;
    }

    public void setTarget(GoalTarget target) {
        this.target = target;
    }

    public int getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(int targetValue) {
        this.targetValue = targetValue;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public GoalType getGoalType() {
        return goalType;
    }

    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
        Instant deadlineInstant;
        if(goalType == GoalType.DAILY){
            deadlineInstant = Instant.now().plusSeconds(86400);
        } else {
            deadlineInstant = Instant.now().plusSeconds(1209600);
        }
        deadline = Date.from(deadlineInstant);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public boolean isMet() {
        checkIfGoalIsMet();
        return met;
    }

    public void setMet(boolean met) {
        this.met = met;
    }

    public UserMyPAL getUser() {
        return user;
    }

    public void setUser(UserMyPAL user) {
        this.user = user;
    }

    private boolean isDeadlinePassed(){
        return (Instant.now().compareTo(deadline.toInstant()) > 0);
    }


    private void checkIfGoalIsMet(){
        if(!met) {
            met = (GoalFactory.getCurrentValue(target, user, startDate, deadline) == targetValue);
        }
    }
}
