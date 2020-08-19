package net.pubnative.lite.sdk.viewability;

import android.view.View;

import com.iab.omid.library.pubnativenet.adsession.AdSession;
import com.iab.omid.library.pubnativenet.adsession.AdSessionConfiguration;
import com.iab.omid.library.pubnativenet.adsession.AdSessionContext;
import com.iab.omid.library.pubnativenet.adsession.CreativeType;
import com.iab.omid.library.pubnativenet.adsession.ImpressionType;
import com.iab.omid.library.pubnativenet.adsession.Owner;
import com.iab.omid.library.pubnativenet.adsession.media.InteractionType;
import com.iab.omid.library.pubnativenet.adsession.media.MediaEvents;
import com.iab.omid.library.pubnativenet.adsession.media.Position;
import com.iab.omid.library.pubnativenet.adsession.media.VastProperties;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.response.AdParams;

public class HyBidViewabilityNativeVideoAdSession extends HyBidViewabilityNativeAdSession {
    private static final String TAG = HyBidViewabilityNativeVideoAdSession.class.getSimpleName();

    private MediaEvents mMediaEvents;

    private boolean startFired = false;
    private boolean firstQuartileFired = false;
    private boolean midpointFired = false;
    private boolean thirdQuartileFired = false;
    private boolean completeFired = false;

    public void initAdSession(View view, AdParams adParams) {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        mVerificationScriptResources.addAll(adParams.getVerificationScriptResources());

        try {
            AdSessionContext adSessionContext = AdSessionContext.createNativeAdSessionContext(HyBid.getViewabilityManager().getPartner(),
                    HyBid.getViewabilityManager().getServiceJs(), mVerificationScriptResources, "", "");


            AdSessionConfiguration adSessionConfiguration =
                    AdSessionConfiguration.createAdSessionConfiguration(
                            CreativeType.VIDEO,
                            ImpressionType.BEGIN_TO_RENDER,
                            Owner.NATIVE, Owner.NATIVE, false);
            mAdSession = AdSession.createAdSession(adSessionConfiguration, adSessionContext);
            mAdSession.registerAdView(view);
            createAdEvents();
            createMediaEvents();
            mAdSession.start();
        } catch (IllegalArgumentException e) {
            Logger.e("", e.getMessage());
        } catch (NullPointerException exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    protected void createMediaEvents() {
        if (mAdSession != null) {
            mMediaEvents = MediaEvents.createMediaEvents(mAdSession);
        }
    }

    public void fireLoaded() {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        VastProperties vastProperties = VastProperties.createVastPropertiesForNonSkippableMedia(false, Position.STANDALONE);

        if (mAdEvents != null) {
            mAdEvents.loaded(vastProperties);
        }
    }

    public void fireStart(float duration, boolean mute) {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        if (mMediaEvents != null && !startFired) {
            mMediaEvents.start(duration, mute ? 0 : 1);
            startFired = true;
        }
    }

    public void fireFirstQuartile() {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        if (mMediaEvents != null && !firstQuartileFired) {
            mMediaEvents.firstQuartile();
            firstQuartileFired = true;
        }
    }

    public void fireMidpoint() {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        if (mMediaEvents != null && !midpointFired) {
            mMediaEvents.midpoint();
            midpointFired = true;
        }
    }

    public void fireThirdQuartile() {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        if (mMediaEvents != null && !thirdQuartileFired) {
            mMediaEvents.thirdQuartile();
            thirdQuartileFired = true;
        }
    }

    public void fireComplete() {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        if (mMediaEvents != null && !completeFired) {
            mMediaEvents.complete();
            completeFired = true;
        }
    }

    public void firePause() {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        if (mMediaEvents != null) {
            mMediaEvents.pause();
        }
    }

    public void fireResume() {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        if (mMediaEvents != null) {
            mMediaEvents.resume();
        }
    }

    /**
     * playback paused due to buffering
     */
    public void fireBufferStart() {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        if (mMediaEvents != null) {
            mMediaEvents.bufferStart();
        }
    }

    /**
     * playback resumes after buffering
     */
    public void fireBufferFinish() {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        if (mMediaEvents != null) {
            mMediaEvents.bufferFinish();
        }
    }

    public void fireVolumeChange(boolean mute) {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        if (mMediaEvents != null) {
            mMediaEvents.volumeChange(mute ? 0 : 1);
        }
    }

    /**
     * any early termination of playback
     */
    public void fireSkipped() {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        if (mMediaEvents != null) {
            mMediaEvents.skipped();
        }
    }

    public void fireClick() {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        if (mMediaEvents != null) {
            mMediaEvents.adUserInteraction(InteractionType.CLICK);
        }
    }
}