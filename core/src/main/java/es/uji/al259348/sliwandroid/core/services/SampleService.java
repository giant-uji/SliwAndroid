package es.uji.al259348.sliwandroid.core.services;

import java.util.List;

import es.uji.al259348.sliwandroid.core.model.Sample;
import rx.Observable;

public interface SampleService extends Service {

    Observable<Sample> take();

    Sample save(Sample sample);
    void remove(Sample sample);

    long countLocalSamples();
    List<Sample> getLocalSamples();

    Observable<String> publish(Sample sample);
}
