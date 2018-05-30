package es.uji.al259348.sliwandroid.core.services;

import es.uji.al259348.sliwandroid.core.model.Sample;
import rx.Observable;

public interface WifiService extends Service {

    String getMacAddress();

    Observable<Void> enableWifi();
    Observable<Sample> takeSample();

}
