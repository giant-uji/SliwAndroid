package es.uji.al259348.sliwandroid.core.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import es.uji.al259348.sliwandroid.core.model.User;
import es.uji.al259348.sliwandroid.core.services.AlarmService;
import es.uji.al259348.sliwandroid.core.services.AlarmServiceImpl;
import es.uji.al259348.sliwandroid.core.services.UserService;
import es.uji.al259348.sliwandroid.core.services.UserServiceImpl;

public class BootCompletedReceiver extends BroadcastReceiver {

    public BootCompletedReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("BootCompletedReceiver", "Boot completed...");

            UserService userService = new UserServiceImpl(context.getApplicationContext());
            User user = userService.getCurrentLinkedUser();

            if (user != null) {
                if (user.isConfigured() || user.isSavedConfig()) {
                    Log.d("BootCompletedReceiver", "Setting alarm for TakeSampleReceiver...");
                    AlarmService alarmService = new AlarmServiceImpl(context.getApplicationContext());
                    alarmService.setTakeSampleAlarm();
                } else {
                    Log.d("BootCompletedReceiver", "The user isn't configured yet, so the alarm is not necessary.");
                }
            } else {
                Log.d("BootCompletedReceiver", "There isn't a linked user, so the alarm is not necessary.");
            }

        }
    }

}
