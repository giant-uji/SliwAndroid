package es.uji.al259348.sliwandroid.core.services;

import es.uji.al259348.sliwandroid.core.model.Device;
import rx.Observable;

public interface DeviceService extends Service {

    String getId();

    Device getCurrentDevice();
    boolean setCurrentDevice(Device device);

    boolean isCurrentDeviceRegistered();
    Observable<Device> registerCurrentDevice();

}
