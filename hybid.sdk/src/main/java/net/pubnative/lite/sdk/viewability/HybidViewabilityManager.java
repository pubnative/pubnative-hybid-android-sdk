package net.pubnative.lite.sdk.viewability;

import android.app.Application;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.webkit.WebView;

import com.iab.omid.library.pubnativenet.Omid;
import com.iab.omid.library.pubnativenet.adsession.AdEvents;
import com.iab.omid.library.pubnativenet.adsession.AdSession;
import com.iab.omid.library.pubnativenet.adsession.AdSessionConfiguration;
import com.iab.omid.library.pubnativenet.adsession.AdSessionContext;
import com.iab.omid.library.pubnativenet.adsession.CreativeType;
import com.iab.omid.library.pubnativenet.adsession.FriendlyObstructionPurpose;
import com.iab.omid.library.pubnativenet.adsession.ImpressionType;
import com.iab.omid.library.pubnativenet.adsession.Owner;
import com.iab.omid.library.pubnativenet.adsession.Partner;
import com.iab.omid.library.pubnativenet.adsession.VerificationScriptResource;
import com.iab.omid.library.pubnativenet.adsession.media.InteractionType;
import com.iab.omid.library.pubnativenet.adsession.media.MediaEvents;
import com.iab.omid.library.pubnativenet.adsession.media.Position;
import com.iab.omid.library.pubnativenet.adsession.media.VastProperties;

import net.pubnative.lite.sdk.BuildConfig;
import net.pubnative.lite.sdk.viewability.baseom.BaseFriendlyObstructionPurpose;
import net.pubnative.lite.sdk.viewability.baseom.BaseVerificationScriptResource;
import net.pubnative.lite.sdk.viewability.baseom.BaseViewabilityManager;
import net.pubnative.lite.sdk.viewability.baseom.MediaEventType;

import java.util.ArrayList;
import java.util.List;

public class HybidViewabilityManager extends BaseViewabilityManager {

    private static final String TAG = HybidViewabilityManager.class.getSimpleName();
    private static final String VIEWABILITY_PARTNER_NAME = "Pubnativenet";
    private static String VIEWABILITY_JS_SERVICE_CONTENT;

    private Partner mPubNativePartner = null;
    private boolean mShouldMeasureViewability = true;

    private String customReferenceData = "";
    private String contentUrl = "";

    public HybidViewabilityManager(final Application application) {
        super(application);
    }

    @Override
    public <T> T createAdSession(T adSessionConfiguration, T adSessionContext) {
        return (T) AdSession.createAdSession((AdSessionConfiguration) adSessionConfiguration, (AdSessionContext) adSessionContext);
    }

    @Override
    public void startAdSession(Object mAdSession) {
        if (mAdSession instanceof AdSession) {
            ((AdSession) mAdSession).start();
        }
    }

    @Override
    public void stopAdSession(Object mAdSession) {
        if (mAdSession instanceof AdSession) {
            ((AdSession) mAdSession).finish();
        }
    }

    @Override
    public void addFriendlyObstruction(Object mAdSession, View friendlyObstructionView, Enum purpose, String reason) {
        if (mAdSession instanceof AdSession) {
            FriendlyObstructionPurpose friendlyObstructionPurpose;
            if (purpose == BaseFriendlyObstructionPurpose.VIDEO_CONTROLS)
                friendlyObstructionPurpose = FriendlyObstructionPurpose.VIDEO_CONTROLS;
            else
                friendlyObstructionPurpose = FriendlyObstructionPurpose.OTHER;

            ((AdSession) mAdSession).addFriendlyObstruction(friendlyObstructionView, friendlyObstructionPurpose, reason);
        }
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public <T> T createNativeAdSessionContext(List<BaseVerificationScriptResource> mVerificationScriptResources) {
        List<VerificationScriptResource> verificationScriptResources = new ArrayList<>();
        for (BaseVerificationScriptResource resource : mVerificationScriptResources) {
            VerificationScriptResource item = VerificationScriptResource.createVerificationScriptResourceWithParameters(resource.getVendorKey(), resource.getResourceUrl(), resource.getVerificationParameters());
            verificationScriptResources.add(item);
        }

        return (T) AdSessionContext.createNativeAdSessionContext(createPartner(),
                getServiceJS(),
                verificationScriptResources,
                customReferenceData,
                contentUrl);
    }

    @Override
    public <T> T getWebAdSessionConfiguration(boolean isVideoAd, T owner) {
        return (T) AdSessionConfiguration.createAdSessionConfiguration(
                isVideoAd ? CreativeType.DEFINED_BY_JAVASCRIPT : CreativeType.HTML_DISPLAY,
                isVideoAd ? ImpressionType.DEFINED_BY_JAVASCRIPT : ImpressionType.BEGIN_TO_RENDER,
                (Owner) owner,
                isVideoAd ? (Owner) owner : Owner.NONE, false);
    }

    @Override
    public <T> T createHtmlAdSessionContext(WebView webView) {
        return (T) AdSessionContext.createHtmlAdSessionContext(
                getPartner(),
                webView,
                contentUrl, customReferenceData);
    }

    @Override
    public String getServiceJS() {
        if (TextUtils.isEmpty(VIEWABILITY_JS_SERVICE_CONTENT)) {
            String omsdkStr = Assets.omsdkjs;
            byte[] omsdkBytes = Base64.decode(omsdkStr, Base64.DEFAULT);
            VIEWABILITY_JS_SERVICE_CONTENT = new String(omsdkBytes);
        }
        return VIEWABILITY_JS_SERVICE_CONTENT;
    }

    @Override
    public <T> T createAdEvents(Object adSession) {
        return (T) AdEvents.createAdEvents((AdSession) adSession);
    }

    @Override
    public void fireLoaded(Object mAdEvents) {
        if (mAdEvents instanceof AdEvents) {
            ((AdEvents) mAdEvents).loaded();
        }
    }

    @Override
    public void fireEventProperties(Object mAdEvents, Object properties) {
        if (mAdEvents instanceof AdEvents && properties instanceof VastProperties) {
            ((AdEvents) mAdEvents).loaded((VastProperties) properties);
        }
    }

    @Override
    public void fireImpression(Object mAdEvents) {
        if (mAdEvents instanceof AdEvents) {
            ((AdEvents) mAdEvents).impressionOccurred();
        }
    }

    @Override
    public boolean isOmActive() {
        return Omid.isActive();
    }

    @Override
    public void activateOmId(Application application) {
        Omid.activate(application);
    }

    @Override
    public void registerAdView(Object adSession, View adView) {
        if (adSession instanceof AdSession) {
            ((AdSession) adSession).registerAdView(adView);
        }
    }

    @Override
    public <T> T getNativeAdSessionConfiguration() {
        return (T) AdSessionConfiguration.createAdSessionConfiguration(
                CreativeType.VIDEO,
                ImpressionType.BEGIN_TO_RENDER,
                Owner.NATIVE, Owner.NATIVE, false);
    }

    @Override
    public <T> T createPartner() {
        mPubNativePartner = Partner.createPartner(getPartnerName(), getSdkVersion());
        return (T) mPubNativePartner;
    }

    @Override
    public <T> T getOwner(boolean isVideo) {
        return (T) (isVideo ? Owner.JAVASCRIPT : Owner.NATIVE);
    }

    @Override
    public <T> T getPartner() {
        return (T) mPubNativePartner;
    }

    @Override
    public String getPartnerName() {
        return VIEWABILITY_PARTNER_NAME;
    }

    @Override
    public String getPartnerVersion() {
        return Omid.getVersion();
    }

    @Override
    public String getSdkVersion() {
        return BuildConfig.SDK_VERSION;
    }

    public boolean isViewabilityMeasurementActivated() {
        return Omid.isActive() && mShouldMeasureViewability;
    }

    public void setViewabilityMeasurementEnabled(boolean shouldMeasureVisibility) {
        this.mShouldMeasureViewability = shouldMeasureVisibility;
    }

    public boolean isViewabilityMeasurementEnabled() {
        return mShouldMeasureViewability;
    }

    @Override
    public <T> T createMediaEvents(T mAdSession) {
        return (T) MediaEvents.createMediaEvents((AdSession) mAdSession);
    }

    @Override
    public void fireMediaEvents(Enum event, Object mMediaEvents) {
        if (!(mMediaEvents instanceof MediaEvents)) {
            return;
        }
        switch ((MediaEventType) event) {
            case FIRST_QUARTILE:
                ((MediaEvents) mMediaEvents).firstQuartile();
                break;
            case MIDPOINT:
                ((MediaEvents) mMediaEvents).midpoint();
                break;
            case THIRD_QUARTILE:
                ((MediaEvents) mMediaEvents).thirdQuartile();
                break;
            case COMPLETE:
                ((MediaEvents) mMediaEvents).complete();
                break;
            case PAUSE:
                ((MediaEvents) mMediaEvents).pause();
                break;
            case RESUME:
                ((MediaEvents) mMediaEvents).resume();
                break;
            case BUFFER_START:
                ((MediaEvents) mMediaEvents).bufferStart();
                break;
            case BUFFER_FINISH:
                ((MediaEvents) mMediaEvents).bufferFinish();
                break;
            case SKIPPED:
                ((MediaEvents) mMediaEvents).skipped();
                break;
            case CLICK:
                ((MediaEvents) mMediaEvents).adUserInteraction(InteractionType.CLICK);
                break;
        }
    }

    @Override
    public void fireMediaEventStart(Object mMediaEvents, float duration, float mute) {
        if (mMediaEvents instanceof MediaEvents) {
            ((MediaEvents) mMediaEvents).start(duration, mute);
        }
    }

    @Override
    public void fireMediaEventVolumeChange(Object mMediaEvents, float mute) {
        if (mMediaEvents instanceof MediaEvents) {
            ((MediaEvents) mMediaEvents).volumeChange(mute);
        }
    }

    @Override
    public <T> T createVastPropertiesForNonSkippableMedia() {
        return (T) VastProperties.createVastPropertiesForNonSkippableMedia(false, Position.STANDALONE);
    }
}
