package models.diary.measurement;

/**
 * Created by ligthartmeu on 25-9-2015.
 */
public enum DiaryMeasurementType {
    GLUCOSE, INSULIN, CARBOHYDRATE, OTHER;

    public static DiaryMeasurementType fromInteger(int type){
        switch (type){
            case 0: return GLUCOSE;
            case 1: return INSULIN;
            case 2: return CARBOHYDRATE;
            case 3:
            default: return OTHER;
        }
    }
}
