package models.diary;

import java.time.Instant;

/**
 * Created by ligthartmeu on 8-7-2015.
 */
public abstract class DiaryItem {

    private Instant starttime, endtime;

    public DiaryItem(Instant starttime, Instant endtime) {
        this.starttime = starttime;
        this.endtime = endtime;
    }

    public Instant getStarttime() {
        return starttime;
    }

    public void setStarttime(Instant starttime) {
        this.starttime = starttime;
    }

    public Instant getEndtime() {
        return endtime;
    }

    public void setEndtime(Instant endtime) {
        this.endtime = endtime;
    }
}
