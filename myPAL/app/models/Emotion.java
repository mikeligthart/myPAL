package models;

/**
 * Created by ligthartmeu on 8-7-2015.
 */
public class Emotion {

    private float pleased, aroused, dominant;

    public Emotion(float pleased, float aroused, float dominant) {
        this.pleased = pleased;
        this.aroused = aroused;
        this.dominant = dominant;
    }

    public float getPleased() {
        return pleased;
    }

    public void setPleased(float pleased) {
        this.pleased = pleased;
    }

    public float getAroused() {
        return aroused;
    }

    public void setAroused(float aroused) {
        this.aroused = aroused;
    }

    public float getDominant() {
        return dominant;
    }

    public void setDominant(float dominant) {
        this.dominant = dominant;
    }
}
