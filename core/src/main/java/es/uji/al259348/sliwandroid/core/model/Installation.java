package es.uji.al259348.sliwandroid.core.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class Installation {

    private static final String SHARED_PREFERENCES_FILENAME = "InstallationSharedPreferences";
    private static final String SHARED_PREFERENCES_KEY_INSTALLATION_ID = "installationId";

    private static String id;

    public synchronized static String getId(Context context) {

        if (context == null)
            throw new IllegalArgumentException();

        if (id == null || id.isEmpty()) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILENAME, Context.MODE_PRIVATE);
            id = sharedPreferences.getString(SHARED_PREFERENCES_KEY_INSTALLATION_ID, "");
            if (id.isEmpty()) {
                //id = "e41eef87-4e92-40b0-a01c-db32413184f4"; // Dispositivo "test" para CONTROL MONITORING
                id = UUID.randomUUID().toString();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SHARED_PREFERENCES_KEY_INSTALLATION_ID, id);
                editor.apply();
            }
        }
        return id;
    }

}
