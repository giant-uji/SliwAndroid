package es.uji.al259348.sliwandroid.wear.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import es.uji.al259348.sliwandroid.core.controller.ConfigController;
import es.uji.al259348.sliwandroid.core.controller.ConfigControllerImpl;
import es.uji.al259348.sliwandroid.core.view.ConfigView;
import es.uji.al259348.sliwandroid.wear.R;
import es.uji.al259348.sliwandroid.wear.fragments.LoadingFragment;
import es.uji.al259348.sliwandroid.wear.fragments.ProgressBarFragment;
import es.uji.al259348.sliwandroid.wear.fragments.ConfirmFragment;
import es.uji.al259348.sliwandroid.wear.receivers.AlarmReceiver;

public class ConfigActivity extends Activity implements
        ConfigView,
        ConfirmFragment.OnFragmentInteractionListener {

    //private static final String STEP_CONFIRM_WIFI_OFF = "wifiOff";
    private static final String STEP_CONFIRM_START_CONFIG = "startConfig";
    private static final String STEP_CONFIRM_START_STEP = "startStep";
    private static final String STEP_CONFIRM_SAVE_CONFIG = "saveConfig";

    private ConfigController configController;

    private View fragmentContent;
    private ProgressBarFragment progressBarFragment;

    private String step;

    private AlarmManager saveConfigAlarm;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        configController = new ConfigControllerImpl(this);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                fragmentContent = stub.findViewById(R.id.fragmentContent);

                step = STEP_CONFIRM_START_CONFIG;
                String msg = getString(R.string.configStartText);
                String btnText = getString(R.string.configStartBtnText);
                setFragment(ConfirmFragment.newInstance(msg, btnText));
            }
        });

        lockScreen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        configController.onDestroy();
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(fragmentContent.getId(), fragment);
        transaction.commit();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onError(Throwable throwable) {
        Toast.makeText(ConfigActivity.this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        if (step.equals(STEP_CONFIRM_SAVE_CONFIG))
            onAllStepsFinished();
        unlockScreen();
    }

    @Override
    public void onNextStep(String msg) {
        step = STEP_CONFIRM_START_STEP;
        String btnText = getString(R.string.configStartStepBtnText);
        setFragment(ConfirmFragment.newInstance(msg, btnText));
    }

    @Override
    public void onStepProgressUpdated(int progress) {
        if (progressBarFragment != null) {
            progressBarFragment.updateProgress(progress);
        }
    }

    @Override
    public void onAllStepsFinished() {
        step = STEP_CONFIRM_SAVE_CONFIG;

        saveConfigAlarm = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        saveConfigAlarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, alarmIntent);
        Log.e("ALARMTAG", "ALARM SET");
    }

    @Override
    public void onConfigFinished() {
        Intent i = getIntent();
        setResult(RESULT_OK, i);
        unlockScreen();
        saveConfigAlarm.cancel(alarmIntent);
        Log.e("ALARMTAG", "ALARM OFF");
        finish();
    }

    @Override
    public void onConfirm() {
        Log.e("Step", step);
        switch (step) {

            case STEP_CONFIRM_START_CONFIG:
                configController.startConfig();
                break;

            case STEP_CONFIRM_START_STEP:
                progressBarFragment = ProgressBarFragment.newInstance();
                setFragment(progressBarFragment);
                configController.startStep();
                break;

            case STEP_CONFIRM_SAVE_CONFIG:
                setFragment(LoadingFragment.newInstance("Guardando configuración..."));
                configController.saveConfig();
                break;
        }
    }

    public void lockScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void unlockScreen() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


}
