package models.diary.measurement;

import javax.persistence.Entity;

/**
 * myPAL
 * Purpose: models the concept of a blood glucose measurement
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
public class Glucose extends DiaryMeasurement {

    private String comment;

    public Glucose(){
        super();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
