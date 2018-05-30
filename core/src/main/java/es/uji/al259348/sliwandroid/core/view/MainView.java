package es.uji.al259348.sliwandroid.core.view;

import es.uji.al259348.sliwandroid.core.model.Device;
import es.uji.al259348.sliwandroid.core.model.User;

public interface MainView extends View {

    void onError(Throwable throwable);

    void hasToRegisterDevice();
    void onDeviceRegistered(Device device);
    void hasToLink();
    void onUserLinked(User user);

    void hasToConfigure();

    void isOk();

    void onTakeSampleCompleted();
    void onSampleClassified(String location);
    void onSampleSavedLocally();
}
