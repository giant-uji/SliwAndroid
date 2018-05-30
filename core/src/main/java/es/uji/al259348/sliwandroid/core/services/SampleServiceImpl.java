package es.uji.al259348.sliwandroid.core.services;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import es.uji.al259348.sliwandroid.core.model.Sample;
import es.uji.al259348.sliwandroid.core.repositories.sqlite.SQLiteSampleRepository;
import rx.Observable;
import rx.schedulers.Schedulers;

public class SampleServiceImpl extends AbstractService implements SampleService {

    private static final String MESSAGING_PUBLISH_SAMPLE_REQUEST_TOPIC = "samples/%s/save";

    private WifiService wifiService;
    private MessagingService messagingService;

    private SQLiteSampleRepository sampleRepository;

    public SampleServiceImpl(Context context) {
        super(context);
        this.wifiService = new WifiServiceImpl(context);
        this.messagingService = new MessagingServiceImpl(context);
        this.sampleRepository = new SQLiteSampleRepository(context);
    }

    @Override
    public void onDestroy() {
        this.sampleRepository.onDestroy();
    }

    @Override
    public Observable<Sample> take() {
        return wifiService.takeSample();
    }

    @Override
    public Sample save(Sample sample) {

        Log.d("SampleService", "Saving the sample: " + sample + " ...");
        sampleRepository.save(sample);

        return sample;
    }

    @Override
    public void remove(Sample sample) {
        sampleRepository.remove(sample);
    }

    @Override
    public long countLocalSamples() {
        return sampleRepository.count();
    }

    @Override
    public List<Sample> getLocalSamples() {
        return sampleRepository.findAll();
    }

    @Override
    public Observable<String> publish(Sample sample) {
        return Observable.create(subscriber -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();

                String topic = String.format(MESSAGING_PUBLISH_SAMPLE_REQUEST_TOPIC, sample.getId());
                String msg = objectMapper.writeValueAsString(sample);

                Log.d("SampleService", "Trying to publish the sample.");
                messagingService.request(topic, msg)
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(
                                response -> {
                                    subscriber.onNext(response);
                                },
                                subscriber::onError,
                                subscriber::onCompleted
                        );

            } catch (JsonProcessingException e) {
                subscriber.onError(e);
            }
        });
    }

}
