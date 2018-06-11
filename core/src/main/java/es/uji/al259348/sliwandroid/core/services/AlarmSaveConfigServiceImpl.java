package es.uji.al259348.sliwandroid.core.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import es.uji.al259348.sliwandroid.core.R;
import es.uji.al259348.sliwandroid.core.android.receivers.SaveConfigReceiver;

public class AlarmSaveConfigServiceImpl extends AbstractService implements AlarmSaveConfigService{

    private AlarmManager alarmManager;

    private PendingIntent pendingIntentShowToast;

    public AlarmSaveConfigServiceImpl(Context context) {
        super(context);

        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intentSaveConfigReceiver = new Intent(context, SaveConfigReceiver.class);
        pendingIntentShowToast = PendingIntent.getBroadcast(context, 0, intentSaveConfigReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void showToast() {
        Log.d("AlarmSaveConfigService", "Setting SaveConfigAlarm");
        long triggerAtMillis = SystemClock.elapsedRealtime();
        long intervalMillis = 100;

        alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerAtMillis,
                intervalMillis,
                pendingIntentShowToast);
    }

    @Override
    public void cancelSaveConfigAlarm() {
        Log.d("AlarmSaveConfigService", "Canceling SaveConfigAlarm");
        alarmManager.cancel(pendingIntentShowToast);
    }
}
