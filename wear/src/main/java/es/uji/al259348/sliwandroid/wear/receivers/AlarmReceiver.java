package es.uji.al259348.sliwandroid.wear.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import es.uji.al259348.sliwandroid.wear.activities.ConfigActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Hey its Your turn", Toast.LENGTH_LONG).show();
        Log.e("ALARMTAG", "ALARM CALLED ONCE OR AGAIN");
        ((ConfigActivity) context).onConfirm();
    }
}