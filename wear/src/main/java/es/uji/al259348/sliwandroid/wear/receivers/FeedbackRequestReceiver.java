package es.uji.al259348.sliwandroid.wear.receivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.uji.al259348.sliwandroid.wear.R;
import es.uji.al259348.sliwandroid.wear.activities.FeedbackActivity;

public class FeedbackRequestReceiver extends BroadcastReceiver {

    public FeedbackRequestReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("FeedbackRequestReceiver", "Se ha recibido una petición para llevar a cabo el proceso de retroalimentación.");

        if (intent.getAction().equals("es.uji.al259348.sliwandroid.FEEDBACK_REQUEST_ACTION")) {

            Log.d("FeedbackRequestReceiver", "La acción recibida es correcta.");

            String texto = intent.getStringExtra("asdf");

            Intent notificationIntent = new Intent(context, FeedbackActivity.class);
//            notificationIntent.putExtra(NotificationActivity.EXTRA_TITLE, title);
//            notificationIntent.putExtra(NotificationActivity.EXTRA_IMAGE, asset);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            List<Notification.Action> actions = new ArrayList<>();
            actions.add(buildSelectLocationAction(context, "Sitio 1"));
            actions.add(buildSelectLocationAction(context, "Sitio 2"));
            actions.add(buildSelectLocationAction(context, "Sitio 3"));

            Notification.Builder notificationBuilder = new Notification.Builder(context)
                    .setSmallIcon(android.R.drawable.ic_delete)
                    .setContentTitle("")
                    .setContentText(new Date().toString())
                    .setPriority(Notification.PRIORITY_MAX)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .extend(new Notification.WearableExtender()
                        .addActions(actions)
                    );
//                    .extend(new Notification.WearableExtender()
//                        .setDisplayIntent(pendingIntent)
//                        .setCustomSizePreset(Notification.WearableExtender.SIZE_MEDIUM));

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(8, notificationBuilder.build());

            long cancelNotificationAt = SystemClock.elapsedRealtime() + 1000 * 30;
            Intent cancelNotificationIntent = new Intent("es.uji.al259348.sliwandroid.FEEDBACK_REQUEST_CANCEL_ACTION");
            PendingIntent cancelNotificationPendintIntent = PendingIntent.getBroadcast(context, 0, cancelNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, cancelNotificationAt, cancelNotificationPendintIntent);

        } else if (intent.getAction().equals("es.uji.al259348.sliwandroid.FEEDBACK_REQUEST_CANCEL_ACTION")) {

            Log.d("FeedbackRequestReceiver", "Cancelando la notificación..");

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(8);

        } else if (intent.getAction().equals("es.uji.al259348.sliwandroid.FEEDBACK_REQUEST_SELECT_ACTION")) {

            String location = intent.getStringExtra("location");
            Log.d("FeedbackRequestReceiver", "Se ha seleccionado la localización: " + location);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(8);

        }

    }

    private Notification.Action buildSelectLocationAction(Context context, String location) {

        Intent intent = new Intent("es.uji.al259348.sliwandroid.FEEDBACK_REQUEST_SELECT_ACTION");
        intent.putExtra("location", location);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);

        return new Notification.Action.Builder(R.drawable.generic_confirmation, location, pendingIntent).build();
    }

}
