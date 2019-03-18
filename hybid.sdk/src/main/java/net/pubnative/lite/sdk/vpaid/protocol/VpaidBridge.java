package net.pubnative.lite.sdk.vpaid.protocol;

public interface VpaidBridge {

    void prepare();

    void startAd();

    void stopAd();

    void pauseAd();

    void resumeAd();

    void getAdSkippableState();

}
