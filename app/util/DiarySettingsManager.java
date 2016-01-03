package util;

import java.util.HashMap;
import java.util.Map;

/**
 * myPAL
 * Purpose: manager that retrieves and manages the DiarySettings
 *
 * Developed for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 21-8-2015
 */
public class DiarySettingsManager {
    private static DiarySettingsManager ourInstance = new DiarySettingsManager();
    private Map<String, DiarySettings> collectionOfDiarySettings;

    public static DiarySettingsManager getInstance() {
        return ourInstance;
    }

    private DiarySettingsManager() {
        collectionOfDiarySettings = new HashMap<>();
    }

    public void login(String email){
        collectionOfDiarySettings.replace(email, new DiarySettings());
    }

    public void logoff(String email){
        collectionOfDiarySettings.remove(email);
    }

    public DiarySettings retrieve(String email){
        DiarySettings diarySettings = collectionOfDiarySettings.get(email);
        if(diarySettings == null){
            diarySettings = new DiarySettings();
            collectionOfDiarySettings.put(email, diarySettings);
        }
        return diarySettings;
    }
}
