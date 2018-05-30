package es.uji.al259348.sliwandroid.core.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

import es.uji.al259348.sliwandroid.core.model.Sample;
import es.uji.al259348.sliwandroid.core.services.SampleService;
import es.uji.al259348.sliwandroid.core.services.SampleServiceImpl;
import rx.schedulers.Schedulers;

public class PublishSamplesService extends Service {

    private SampleService sampleService;
    private Queue<Sample> samples;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("PublishSamplesService", "PublishSampleService onCreate");
        sampleService = new SampleServiceImpl(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("PublishSamplesService", "PublishSampleService onDestroy");
        sampleService.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        long numberOfSamples = sampleService.countLocalSamples();
        if (numberOfSamples > 0) {
            Log.d("PublishSamplesService", "Hay " + numberOfSamples + " muestras guardadas localmente.");
            publishSamples();
        } else {
            Log.d("PublishSamplesService", "No hay muestras guardadas localmente.");
            stopSelf();
        }

        return START_STICKY;
    }

    private void publishSamples() {
        Log.d("PublishSamplesService", "Procedemos a publicar las muestras.");
        samples = new LinkedList<>(sampleService.getLocalSamples());
        publishNextSample();
    }

    private void publishNextSample() {
        Sample sample = samples.poll();
        if (sample != null)
            publishSample(sample);
        else
            stopSelf();
    }

    private void publishSample(Sample sample) {
        Log.d("PublishSamplesService", "Publicando muestra: " + sample.toString());
        sampleService.publish(sample)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(
                        response -> {
                            Log.d("PublishSamplesService", "Muestra publicada.");
                            Log.d("PublishSamplesService", "Eliminando muestra local...");
                            sampleService.remove(sample);
                        },
                        throwable -> {
                            Log.d("PublishSamplesService", "La muestra no ha podido publicarse.");
                            throwable.printStackTrace();
                        },
                        this::publishNextSample
                );
    }

}
