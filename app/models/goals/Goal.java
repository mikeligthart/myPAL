package models.goals;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.typesafe.config.ConfigFactory;
import controllers.goals.GoalFactory;
import models.UserMyPAL;
import play.Logger;
import play.db.ebean.Model;
import play.i18n.Messages;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.time.Instant;
import java.util.List;
import java.util.Random;

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

    private Timestamp added, metAtTimestamp;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date deadline;

    @Temporal(TemporalType.DATE)
    private Date metAt;

    private boolean met;

    @OneToMany
    @Enumerated(EnumType.STRING)
    private GoalType goalType;

    @ManyToOne()
    @JsonBackReference
    private UserMyPAL user;

    public Goal(){
        startDate = Date.from(Instant.now());
        added = Timestamp.from(Instant.now());
        met = false;
        metAt = null;
    }

    public int getProgress(){

            return Math.round(((float) GoalFactory.getCurrentValue(target, user, startDate, deadline) / (float) targetValue) * 100);
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

    public Date getMetAt() {
        return metAt;
    }

    public String stringifyMetAt(){
        SimpleDateFormat sdf = new SimpleDateFormat(ConfigFactory.load().getString("date.format"));
        return sdf.format(metAt);
    }

    public String stringifyStartDate(){
        SimpleDateFormat sdf = new SimpleDateFormat(ConfigFactory.load().getString("date.format"));
        return sdf.format(startDate);
    }

    public void setMetAt(Date metAt) {
        this.metAt = metAt;
    }


    private boolean isDeadlinePassed(){
        return (Instant.now().compareTo(deadline.toInstant()) > 0);
    }


    public Timestamp getAdded() {
        return added;
    }

    public void setAdded(Timestamp added) {
        this.added = added;
    }

    public Timestamp getMetAtTimestamp() {
        return metAtTimestamp;
    }

    public void setMetAtTimestamp(Timestamp metAtTimestamp) {
        this.metAtTimestamp = metAtTimestamp;
    }

    private void checkIfGoalIsMet(){
        if(!met) {
            met = (GoalFactory.getCurrentValue(target, user, startDate, deadline) >= targetValue);
            if(met){
                metAt = new Date();
                metAtTimestamp = Timestamp.from(Instant.now());
            }
        }
    }

    public static Finder<Integer, Goal> find = new Finder<Integer, Goal>(Integer.class, Goal.class);

    public static Goal byID(int id) {
        return find.byId(id);
    }

    public static List<Goal> getGoalsPerType(UserMyPAL user, GoalType type){
        return find.where().eq("user", user).eq("goalType", type).findList();
    }

    public static Goal lastByUser(UserMyPAL user){
        List<Goal> goals = find.where().eq("user", user).setOrderBy("added desc").findList();
        if(goals != null){
            if(!goals.isEmpty()){
                return goals.get(0);
            }
        }
        return null;
    }

    public static Goal randomActiveGoal(UserMyPAL user){
        List<Goal> goals = find.where().eq("user", user).eq("met", false).gt("deadline", new Date()).findList();
        if(goals != null){
            if(!goals.isEmpty()){
                Random rand = new Random();
                Goal goal = goals.get(rand.nextInt(goals.size()));
                return goal;
            }
        }
        return null;
    }

    public static Goal randomGoalMetAfter(UserMyPAL user, Timestamp after){
        List<Goal> goals = find.where().eq("user", user).eq("met", true).ge("metAtTimestamp", after).findList();
        if(goals != null){
            if(!goals.isEmpty()){
                Random rand = new Random();
                Goal goal = goals.get(rand.nextInt(goals.size()));
                return goal;
            }
        }
        return null;
    }

    public static Goal randomGoalAddedAfter(UserMyPAL user, Timestamp after){
        List<Goal> goals = find.where().eq("user", user).eq("met", false).ge("added", after).findList();
        if(goals != null){
            if(!goals.isEmpty()){
                Random rand = new Random();
                Goal goal = goals.get(rand.nextInt(goals.size()));
                return goal;
            }
        }
        return null;
    }
}
