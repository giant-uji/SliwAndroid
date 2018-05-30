package es.uji.al259348.sliwandroid.core.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;
import java.util.UUID;

import es.uji.al259348.sliwandroid.core.services.DeviceService;
import es.uji.al259348.sliwandroid.core.services.DeviceServiceImpl;
import es.uji.al259348.sliwandroid.core.services.SampleService;
import es.uji.al259348.sliwandroid.core.services.SampleServiceImpl;
import es.uji.al259348.sliwandroid.core.services.UserService;
import es.uji.al259348.sliwandroid.core.services.UserServiceImpl;
import rx.schedulers.Schedulers;

public class TakeSampleReceiver extends BroadcastReceiver {

    public TakeSampleReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TakeSampleReceiver", "onReceive: " + (new Date()).toString());

        SampleService sampleService = new SampleServiceImpl(context.getApplicationContext());
        DeviceService deviceService = new DeviceServiceImpl(context.getApplicationContext());
        UserService userService = new UserServiceImpl(context.getApplicationContext());

        sampleService.take()
                .doOnError(Throwable::printStackTrace)
                .doOnNext(sample -> {

                    String sampleId = UUID.randomUUID().toString();
                    String deviceId = deviceService.getId();
                    String userId = userService.getCurrentLinkedUserId();

                    sample.setId(sampleId);
                    sample.setUserId(userId);
                    sample.setDeviceId(deviceId);

                    Log.d("TakeSampleReceiver", "Sample: " + sample);

                    sampleService.publish(sample)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(Schedulers.newThread())
                            .subscribe(
                                    response -> {
                                        Log.d("TakeSampleReceiver", "The sample has been published (next)");
                                    },
                                    throwable -> {
                                        Log.d("TakeSampleReceiver", "The sample couldn't be published.");
                                        Log.d("TakeSampleReceiver", "Storing the sample locally...");
                                        sampleService.save(sample);
                                    },
                                    () -> Log.d("TakeSampleReceiver", "The sample has been published (completed)")
                            );

                })
                .subscribe();
    }

}
