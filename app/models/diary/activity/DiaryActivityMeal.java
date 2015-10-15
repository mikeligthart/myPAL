package models.diary.activity;

import javax.persistence.Entity;

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
 * @version 1.0 15-10-2015
 */

@Entity
public class DiaryActivityMeal extends DiaryActivity {


    private double carbohydrateValue;

    public double getCarbohydrateValue() {
        return carbohydrateValue;
    }

    public void setCarbohydrateValue(double carbohydrateValue) {
        this.carbohydrateValue = carbohydrateValue;
    }
}
