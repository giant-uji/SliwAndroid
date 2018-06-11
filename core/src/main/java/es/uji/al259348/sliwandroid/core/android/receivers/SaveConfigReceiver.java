package es.uji.al259348.sliwandroid.core.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import es.uji.al259348.sliwandroid.core.controller.ConfigController;

public class SaveConfigReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm!", Toast.LENGTH_LONG);
        //((ConfigController)context).saveConfig();
        Log.e("AlarmSaveConfigService", "ALARM RINGING");
    }
}
