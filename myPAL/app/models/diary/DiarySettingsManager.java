package models.diary;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mike on 30-7-2015.
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
