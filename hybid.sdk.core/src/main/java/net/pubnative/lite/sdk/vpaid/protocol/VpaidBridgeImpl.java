package net.pubnative.lite.sdk.vpaid.protocol;

import android.webkit.JavascriptInterface;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.enums.EventConstants;
import net.pubnative.lite.sdk.vpaid.models.vpaid.CreativeParams;

import java.util.Locale;

@SuppressWarnings("unused")
public class VpaidBridgeImpl implements VpaidBridge {

    private static final String LOG_TAG = VpaidBridgeImpl.class.getSimpleName();

    private final BridgeEventHandler mBridge;
    private final CreativeParams mCreativeParams;

    public VpaidBridgeImpl(BridgeEventHandler eventHandler, CreativeParams creativeParams) {
        mBridge = eventHandler;
        mCreativeParams = creativeParams;
    }

    //region VpaidBridge methods
    @Override
    public void prepare() {
        Logger.d(LOG_TAG, "call initVpaidWrapper()");
        callJsMethod("initVpaidWrapper()");
    }

    @Override
    public void startAd() {
        Logger.d(LOG_TAG, "call startAd()");
        callWrapper("startAd()");
    }

    @Override
    public void stopAd() {
        Logger.d(LOG_TAG, "call stopAd()");
        callWrapper("stopAd()");
    }

    @Override
    public void pauseAd() {
        Logger.d(LOG_TAG, "call pauseAd()");
        callWrapper("pauseAd()");
    }

    @Override
    public void resumeAd() {
        Logger.d(LOG_TAG, "call resumeAd()");
        callWrapper("resumeAd()");
    }

    @Override
    public void getAdSkippableState() {
        Logger.d(LOG_TAG, "call getAdSkippableState()");
        callWrapper("getAdSkippableState()");
    }
    //endregion

    //region Helpers
    private void runOnUiThread(Runnable runnable) {
        mBridge.runOnUiThread(runnable);
    }

    private void callJsMethod(final String url) {
        mBridge.callJsMethod(url);
    }

    private void callWrapper(String method) {
        callJsMethod("vapidWrapperInstance." + method);
    }
    //endregion

    //region JsCallbacks
    @JavascriptInterface
    public void wrapperReady() {
        initAd();
    }

    private void initAd() {
        Logger.d(LOG_TAG, "JS: call initAd()");
        String requestTemplate = "initAd(" +
                "%1$d," + // width
                "%2$d," + // height
                "%3$s," + // viewMode
                "%4$s," + // desiredBitrate
                "%5$s," + // creativeData
                "%6$s)"; // environmentVars
        String requestFinal = String.format(Locale.ENGLISH, requestTemplate,
                mCreativeParams.getWidth(),
                mCreativeParams.getHeight(),
                mCreativeParams.getViewMode(),
                mCreativeParams.getDesiredBitrate(),
                mCreativeParams.getCreativeData(),
                mCreativeParams.getEnvironmentVars()
        );
        callWrapper(requestFinal);
    }

    @JavascriptInterface
    public String handshakeVersionResult(String result) {
        Logger.d(LOG_TAG, "JS: handshakeVersion()");
        return result;
    }

    @JavascriptInterface
    public void vpaidAdLoaded() {
        Logger.d(LOG_TAG, "JS: vpaidAdLoaded");
        mBridge.onPrepared();
    }

    @JavascriptInterface
    public void vpaidAdStarted() {
        Logger.d(LOG_TAG, "JS: vpaidAdStarted");
    }

    @JavascriptInterface
    public void initAdResult() {
        Logger.d(LOG_TAG, "JS: Init ad done");
    }

    @JavascriptInterface
    public void vpaidAdError(String message) {
        Logger.d(LOG_TAG, "JS: vpaidAdError" + message);
        mBridge.trackError(message);
    }

    @JavascriptInterface
    public void vpaidAdLog(String message) {
        Logger.d(LOG_TAG, "JS: vpaidAdLog " + message);
    }

    @JavascriptInterface
    public void vpaidAdUserAcceptInvitation() {
        Logger.d(LOG_TAG, "JS: vpaidAdUserAcceptInvitation");
    }

    @JavascriptInterface
    public void vpaidAdUserMinimize() {
        Logger.d(LOG_TAG, "JS: vpaidAdUserMinimize");
    }

    @JavascriptInterface
    public void vpaidAdUserClose() {
        Logger.d(LOG_TAG, "JS: vpaidAdUserClose");
    }

    @JavascriptInterface
    public void vpaidAdSkippableStateChange() {
        Logger.d(LOG_TAG, "JS: vpaidAdSkippableStateChange");
    }

    @JavascriptInterface
    public void vpaidAdExpandedChange() {
        Logger.d(LOG_TAG, "JS: vpaidAdExpandedChange");
    }

    @JavascriptInterface
    public void getAdExpandedResult(String result) {
        Logger.d(LOG_TAG, "JS: getAdExpandedResult");
    }

    @JavascriptInterface
    public void vpaidAdSizeChange() {
        Logger.d(LOG_TAG, "JS: vpaidAdSizeChange");
    }

    @JavascriptInterface
    public void vpaidAdDurationChange() {
        Logger.d(LOG_TAG, "JS: vpaidAdDurationChange");
        callWrapper("getAdDurationResult");
        mBridge.onDurationChanged();
    }

    @JavascriptInterface
    public void vpaidAdRemainingTimeChange() {
        Logger.d(LOG_TAG, "JS: vpaidAdRemainingTimeChange");
        callWrapper("getAdRemainingTime()");
    }

    @JavascriptInterface
    public void vpaidAdLinearChange() {
        Logger.d(LOG_TAG, "JS: vpaidAdLinearChange");
        mBridge.onAdLinearChange();
    }

    @JavascriptInterface
    public void vpaidAdPaused() {
        Logger.d(LOG_TAG, "JS: vpaidAdPaused");
        mBridge.postEvent(EventConstants.PAUSE, false);
    }

    @JavascriptInterface
    public void vpaidAdVideoStart() {
        Logger.d(LOG_TAG, "JS: vpaidAdVideoStart");
        mBridge.postEvent(EventConstants.START, true);
    }

    @JavascriptInterface
    public void vpaidAdPlaying() {
        Logger.d(LOG_TAG, "JS: vpaidAdPlaying");
        mBridge.postEvent(EventConstants.RESUME, false);
    }

    @JavascriptInterface
    public void vpaidAdClickThruIdPlayerHandles(String url, String id, boolean playerHandles) {
        if (playerHandles) {
            mBridge.openUrl(url);
        }
    }

    @JavascriptInterface
    public void vpaidAdVideoFirstQuartile() {
        mBridge.postEvent(EventConstants.FIRST_QUARTILE, true);
    }

    @JavascriptInterface
    public void vpaidAdVideoMidpoint() {
        Logger.d(LOG_TAG, "JS: vpaidAdVideoMidpoint");
        mBridge.postEvent(EventConstants.MIDPOINT, true);
    }

    @JavascriptInterface
    public void vpaidAdVideoThirdQuartile() {
        Logger.d(LOG_TAG, "JS: vpaidAdVideoThirdQuartile");
        mBridge.postEvent(EventConstants.THIRD_QUARTILE, true);
    }

    @JavascriptInterface
    public void vpaidAdVideoComplete() {
        Logger.d(LOG_TAG, "JS: vpaidAdVideoComplete");
    }

    @JavascriptInterface
    public void getAdSkippableStateResult(boolean value) {
        Logger.d(LOG_TAG, "JS: SkippableState: " + value);
        mBridge.setSkippableState(value);
    }

    @JavascriptInterface
    public void getAdRemainingTimeResult(int value) {
        Logger.d(LOG_TAG, "JS: getAdRemainingTimeResult: " + value);
        if (value == 0) {
            mBridge.postEvent(EventConstants.COMPLETE, true);
        } else {
            mBridge.postEvent(EventConstants.PROGRESS, value, false);
        }
    }

    @JavascriptInterface
    public void getAdDurationResult(int value) {
        Logger.d(LOG_TAG, "JS: getAdDurationResult: " + value);
    }

    @JavascriptInterface
    public void getAdLinearResult(boolean value) {
        Logger.d(LOG_TAG, "getAdLinearResult: " + value);
    }

    @JavascriptInterface
    public void vpaidAdSkipped() {
        Logger.d(LOG_TAG, "JS: vpaidAdSkipped");
        runOnUiThread(mBridge::onAdSkipped);
    }

    @JavascriptInterface
    public void vpaidAdStopped() {
        Logger.d(LOG_TAG, "JS: vpaidAdStopped");
        runOnUiThread(mBridge::onAdStopped);
    }

    @JavascriptInterface
    public void vpaidAdImpression() {
        Logger.d(LOG_TAG, "JS: vpaidAdImpression");
        mBridge.onAdImpression();
    }

    @JavascriptInterface
    public void vpaidAdInteraction() {
        Logger.d(LOG_TAG, "JS: vpaidAdInteraction");
    }

    @JavascriptInterface
    public void vpaidAdVolumeChanged() {
        Logger.d(LOG_TAG, "JS: vpaidAdVolumeChanged");
        mBridge.onAdVolumeChange();
    }

    @JavascriptInterface
    public void getAdVolumeResult() {
        Logger.d(LOG_TAG, "JS: getAdVolumeResult");
    }

}
