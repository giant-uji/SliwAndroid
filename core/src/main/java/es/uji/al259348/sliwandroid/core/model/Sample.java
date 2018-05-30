package es.uji.al259348.sliwandroid.core.model;

import android.net.wifi.ScanResult;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@DatabaseTable(tableName = "samples")
public class Sample {

    @DatabaseTable(tableName = "wifiScanResults")
    public static class WifiScanResult {

        @DatabaseField(generatedId = true)
        @JsonIgnore
        public int id;
        @DatabaseField
        public String SSID;
        @DatabaseField
        public String BSSID;
        @DatabaseField
        public int level;
        @DatabaseField(foreign = true)
        @JsonIgnore
        public Sample sample;

        public WifiScanResult() {
            super();
        }

        public WifiScanResult(ScanResult scanResult) {
            super();
            this.SSID = scanResult.SSID;
            this.BSSID = scanResult.BSSID;
            this.level = scanResult.level;
        }

        @Override
        public String toString() {
            return "WifiScanResult{" +
                    "SSID='" + SSID + '\'' +
                    ", BSSID='" + BSSID + '\'' +
                    ", level=" + level +
                    '}';
        }

    }

    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    private String userId;
    @DatabaseField
    private String deviceId;
    @DatabaseField
    private String location;
    @DatabaseField
    private Date date;
    @ForeignCollectionField(eager = true)
    private Collection<WifiScanResult> scanResults;
    @DatabaseField
    private boolean valid;

    public Sample() {
        super();
        this.date = new Date();
        this.scanResults = new ArrayList<>();
    }

    public Sample(List<ScanResult> scanResults) {
        super();
        this.date = new Date();
        this.scanResults = new ArrayList<>();
        for (ScanResult scanResult : scanResults) {
            WifiScanResult wifiScanResult = new WifiScanResult(scanResult);
            this.scanResults.add(wifiScanResult);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Collection<WifiScanResult> getScanResults() {
        return scanResults;
    }

    public void setScanResults(Collection<WifiScanResult> scanResults) {
        this.scanResults = scanResults;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "Sample{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", location='" + location + '\'' +
                ", date=" + date +
                ", scanResults=" + Arrays.toString(scanResults.toArray()) +
                ", valid=" + valid +
                '}';
    }

}
