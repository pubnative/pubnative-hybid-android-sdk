package net.pubnative.lite.sdk.viewability;

import android.webkit.WebView;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.viewability.baseom.BaseViewabilityManager;

public class HyBidViewabilityWebAdSession extends HyBidViewabilityAdSession {
    private static final String TAG = HyBidViewabilityWebAdSession.class.getSimpleName();

    public HyBidViewabilityWebAdSession(BaseViewabilityManager viewabilityManager) {
        super(viewabilityManager);
    }

    public void initAdSession(WebView webView, boolean isVideoAd) {
        if (viewabilityManager != null && !viewabilityManager.isViewabilityMeasurementEnabled())
            return;

        try {
            Object adSessionContext = viewabilityManager.createHtmlAdSessionContext(webView);
            Object adSessionConfiguration = viewabilityManager.getWebAdSessionConfiguration(isVideoAd, viewabilityManager.getOwner(isVideoAd));

            mAdSession = viewabilityManager.createAdSession(adSessionConfiguration, adSessionContext);
            viewabilityManager.registerAdView(mAdSession, webView);
            createAdEvents();
            viewabilityManager.startAdSession(mAdSession);

        } catch (IllegalArgumentException e) {
            Logger.e("", e.getMessage());
        } catch (NullPointerException exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    protected void createAdEvents() {
        if (mAdSession != null) {
            mAdEvents = viewabilityManager.createAdEvents(mAdSession);
        }
    }
}
