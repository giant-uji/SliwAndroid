package es.uji.al259348.sliwandroid.core.services;

public interface AlarmSaveConfigService extends Service {
    void saveConfig();
    void cancelSaveConfigAlarm();
}
