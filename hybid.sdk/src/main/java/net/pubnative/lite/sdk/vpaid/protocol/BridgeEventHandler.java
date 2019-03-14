package net.pubnative.lite.sdk.vpaid.protocol;

public interface BridgeEventHandler {

    void runOnUiThread(Runnable runnable);

    void callJsMethod(final String url);

    void onPrepared();

    void onAdSkipped();

    void onAdStopped();

    void setSkippableState(boolean skippable);

    void openUrl(String url);

    void trackError(String message);

    void postEvent(String eventType);

    void postEvent(String eventType, int value);

    void onDurationChanged();

    void onAdLinearChange();

    void onAdVolumeChange();

    void onAdImpression();
}
