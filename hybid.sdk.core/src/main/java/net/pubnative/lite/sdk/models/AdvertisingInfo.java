package net.pubnative.lite.sdk.models;

public class AdvertisingInfo {
    private final String advertisingId;
    private final Boolean limitTrackingEnabled;

    public AdvertisingInfo(String advertisingId, Boolean limitTracking) {
        this.advertisingId = advertisingId;
        this.limitTrackingEnabled = limitTracking;
    }

    public String getAdvertisingId() {
        return advertisingId;
    }

    public Boolean isLimitTrackingEnabled() {
        return limitTrackingEnabled;
    }
}
