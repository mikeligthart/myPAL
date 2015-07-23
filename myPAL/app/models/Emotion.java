package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by ligthartmeu on 8-7-2015.
 */
@Entity
public class Emotion extends Model {


    @Id
    @GeneratedValue
    private int id;

    private double pleased;
    private double aroused;
    private double dominant;

    public Emotion(double pleased, double aroused, double dominant) {
        this.pleased = pleased;
        this.aroused = aroused;
        this.dominant = dominant;
    }

    public double getPleased() {
        return pleased;
    }

    public void setPleased(double pleased) {
        this.pleased = pleased;
    }

    public double getAroused() {
        return aroused;
    }

    public void setAroused(double aroused) {
        this.aroused = aroused;
    }

    public double getDominant() {
        return dominant;
    }

    public void setDominant(double dominant) {
        this.dominant = dominant;
    }
}
