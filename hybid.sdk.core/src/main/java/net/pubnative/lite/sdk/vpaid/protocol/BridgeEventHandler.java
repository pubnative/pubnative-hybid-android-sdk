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

    void postEvent(String eventType, boolean ignoreIfExist);

    void postEvent(String eventType, int value, boolean ignoreIfExist);

    void onDurationChanged();

    void onAdLinearChange();

    void onAdVolumeChange();

    void onAdImpression();
}
