package es.uji.al259348.sliwandroid.core.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import es.uji.al259348.sliwandroid.core.android.services.PublishSamplesService;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    public ConnectivityChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            if (isOnline(context)) {

                Log.d("ConnectivityChangeRecei", "Se ha conectado.");
                context.startService(new Intent(context, PublishSamplesService.class));

            } else {

                Log.d("ConnectivityChangeRecei", "Se ha desconectado.");
                context.stopService(new Intent(context, PublishSamplesService.class));

            }

        }

    }

    private boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }

}
