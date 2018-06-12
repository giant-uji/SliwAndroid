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
        context.sendBroadcast(new Intent("SAVE_CONFIG"));
    }
}
