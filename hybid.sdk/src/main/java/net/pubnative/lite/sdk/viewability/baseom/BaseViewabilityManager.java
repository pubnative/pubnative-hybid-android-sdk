package net.pubnative.lite.sdk.viewability.baseom;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebView;

import net.pubnative.lite.sdk.utils.Logger;

import java.util.List;

public abstract class BaseViewabilityManager {

    private Object mPartner = null;
    private boolean mShouldMeasureViewability = true;

    public abstract <T> T createAdSession(T adSessionConfiguration, T adSessionContext);

    public abstract void registerAdView(Object adSession, View adView);

    public abstract void startAdSession(Object mAdSession);

    public abstract void stopAdSession(Object mAdSession);

    public abstract void addFriendlyObstruction(Object mAdSession, View friendlyObstructionView, Enum purpose, String reason);

    public abstract String getTag();

    public abstract <T> T getNativeAdSessionConfiguration();

    public abstract <T> T createNativeAdSessionContext(List<BaseVerificationScriptResource> mVerificationScriptResources);

    public abstract <T> T getWebAdSessionConfiguration(boolean isVideoAd, T owner);

    public abstract <T> T createHtmlAdSessionContext(WebView webView);

    public abstract <T> T createPartner();

    public abstract <T> T getOwner(boolean isVideo);

    public abstract <T> T getPartner();

    public abstract String getPartnerName();

    public abstract String getPartnerVersion();

    public abstract String getSdkVersion();

    public abstract String getServiceJS();

    public abstract <T> T createAdEvents(Object adSession);

    public abstract void fireLoaded(Object mAdEvents);

    public abstract void fireEventProperties(Object mAdEvents, Object properties);

    public abstract void fireImpression(Object mAdEvents);

    public abstract boolean isOmActive();

    public abstract void activateOmId(Application application);

    public BaseViewabilityManager(final Application application) {
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                if (!isOmActive()) {
                    activateOmId(application);
                }
            } catch (IllegalArgumentException e) {
                Logger.e(getTag(), "Could not initialise Omid");
            }

            if (isOmActive() && mPartner == null) {
                try {
                    mPartner = createPartner();
                } catch (IllegalArgumentException e) {
                    Logger.e(getTag(), "Could not initialise Omid");
                }
            }
        });
    }

    public boolean isViewabilityMeasurementActivated() {
        return isOmActive() && mShouldMeasureViewability;
    }

    public void setViewabilityMeasurementEnabled(boolean shouldMeasureVisibility) {
        this.mShouldMeasureViewability = shouldMeasureVisibility;
    }

    public boolean isViewabilityMeasurementEnabled() {
        return mShouldMeasureViewability;
    }

    public abstract <T> T createMediaEvents(T mAdSession);

    public abstract void fireMediaEvents(Enum event, Object mMediaEvents);

    public abstract void fireMediaEventStart(Object mMediaEvents, float duration, float mute);

    public abstract void fireMediaEventVolumeChange(Object mMediaEvents, float mute);

    public abstract <T> T createVastPropertiesForNonSkippableMedia();
}
