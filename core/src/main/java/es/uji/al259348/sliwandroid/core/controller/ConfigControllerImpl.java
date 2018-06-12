package es.uji.al259348.sliwandroid.core.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ListIterator;
import java.util.UUID;

import es.uji.al259348.sliwandroid.core.model.Config;
import es.uji.al259348.sliwandroid.core.model.User;
import es.uji.al259348.sliwandroid.core.model.Sample;
import es.uji.al259348.sliwandroid.core.services.AlarmSaveConfigService;
import es.uji.al259348.sliwandroid.core.services.AlarmSaveConfigServiceImpl;
import es.uji.al259348.sliwandroid.core.services.MessagingService;
import es.uji.al259348.sliwandroid.core.services.MessagingServiceImpl;
import es.uji.al259348.sliwandroid.core.services.UserService;
import es.uji.al259348.sliwandroid.core.services.UserServiceImpl;
import es.uji.al259348.sliwandroid.core.services.WifiService;
import es.uji.al259348.sliwandroid.core.services.WifiServiceImpl;
import es.uji.al259348.sliwandroid.core.view.ConfigView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ConfigControllerImpl implements ConfigController {

    private ConfigView configView;

    private MessagingService messagingService;
    private UserService userService;
    private WifiService wifiService;

    private User user;
    private Config config;

    private ListIterator<Config.ConfigStep> configStepsIter;
    private Config.ConfigStep currentStep;

    private AlarmSaveConfigService alarm;
    private Context context;
    private BroadcastReceiver broadcastReceiver;

    public ConfigControllerImpl(ConfigView configView) {
        this.configView = configView;

        context = configView.getContext();
        this.messagingService = new MessagingServiceImpl(context);
        this.userService = new UserServiceImpl(context, messagingService);
        this.wifiService = new WifiServiceImpl(context);

        this.user = userService.getCurrentLinkedUser();
        this.config = new Config(user);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context1, Intent intent) {
                Log.e("ALARMTAG", "ring");
                saveConfig();
            }
        };

        try {
            context.registerReceiver(broadcastReceiver, new IntentFilter("SAVE_CONFIG"));
        } catch (IllegalArgumentException e) {
            Log.e("BroadCastReceiver", "Already registered");
        }
            alarm = null;
    }

    @Override
    public void onDestroy() {
        //context.unregisterReceiver(broadcastReceiver);
        messagingService.onDestroy();
    }

    @Override
    public void startConfig() {
        configStepsIter = config.getSteps().listIterator();
        if (configStepsIter.hasNext()) {
            currentStep = configStepsIter.next();
            configView.onNextStep(currentStep.getLocation().getConfigMsg());
        }
    }

    @Override
    public void startStep() {
        performScan();
    }

    @Override
    public void saveConfig() {
        userService.configureUser(user, config)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                }, this::handleError, this::onConfigFinished);
    }

    private void performScan() {
        wifiService.takeSample()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onScanPerformed);
    }

    private void onScanPerformed(Sample sample) {

        sample.setId(UUID.randomUUID().toString());
        sample.setUserId(user.getId());
        sample.setDeviceId(wifiService.getMacAddress());
        sample.setLocation(currentStep.getLocation().getName());
        sample.setValid(true);

        currentStep.addSample(sample);

        int progress = currentStep.getProgress();
        configView.onStepProgressUpdated(progress);

        if (currentStep.isCompleted()) {
            onStepFinished();
        } else {
            performScan();
        }
    }

    private void onStepFinished() {
        if (configStepsIter.hasNext()) {
            currentStep = configStepsIter.next();
            configView.onNextStep(currentStep.getLocation().getConfigMsg());
        } else {
            configView.onAllStepsFinished();
        }
    }

    private void onConfigFinished() {
        user.setConfigured(true);
        user.setSavedConfig(true);
        userService.setCurrentLinkedUser(user);

        if (alarm != null) {
            alarm.cancelSaveConfigAlarm();
            context.unregisterReceiver(broadcastReceiver);
            Log.e("ALARMTAG", "Alarm canceled");
        }
        Log.e("ConfigControllerImpl", "Configuration saved");
        configView.onConfigFinished();
    }

    private void handleError(Throwable throwable) {
        user.setSavedConfig(true);
        user.setConfigured(false);
        userService.setCurrentLinkedUser(user);

        if (alarm == null) {
            alarm = new AlarmSaveConfigServiceImpl(configView.getContext());
            alarm.saveConfig();
            Log.e("ALARMTAG", "Alarm set");
        }
        Log.e("ConfigControllerImpl", "Couldn't save the configuration");
    }
}
