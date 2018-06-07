package es.uji.al259348.sliwandroid.wear.application;

import android.app.Application;
import android.content.Context;

public class SliwAndroid extends Application {
    public static SliwAndroid instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static SliwAndroid getInstance() {
        return instance;
    }
}
