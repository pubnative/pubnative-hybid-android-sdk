// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.protocol;

public interface VpaidBridge {

    void prepare();

    void startAd();

    void stopAd();

    void pauseAd();

    void resumeAd();

    void getAdSkippableState();

}
