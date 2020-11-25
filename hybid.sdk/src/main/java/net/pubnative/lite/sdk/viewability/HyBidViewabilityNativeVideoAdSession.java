package net.pubnative.lite.sdk.viewability;

import android.util.Log;
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
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    protected void createMediaEvents() {
        try {
            if (mAdSession != null) {
                mMediaEvents = MediaEvents.createMediaEvents(mAdSession);
            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void fireLoaded() {
        try {
            if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
                return;

            VastProperties vastProperties = VastProperties.createVastPropertiesForNonSkippableMedia(false, Position.STANDALONE);

            if (mAdEvents != null) {
                mAdEvents.loaded(vastProperties);
            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void fireStart(float duration, boolean mute) {
        try {
            if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null && !startFired) {
                mMediaEvents.start(duration, mute ? 0 : 1);
                startFired = true;
            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void fireFirstQuartile() {
        try {
            if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null && !firstQuartileFired) {
                mMediaEvents.firstQuartile();
                firstQuartileFired = true;
            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void fireMidpoint() {
        try {
            if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null && !midpointFired) {
                mMediaEvents.midpoint();
                midpointFired = true;
            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void fireThirdQuartile() {
        try {
            if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null && !thirdQuartileFired) {
                mMediaEvents.thirdQuartile();
                thirdQuartileFired = true;
            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void fireComplete() {
        try {
            if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null && !completeFired) {
                mMediaEvents.complete();
                completeFired = true;
            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void firePause() {
        try {
            if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null) {
                mMediaEvents.pause();
            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void fireResume() {
        try {
            if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null) {
                mMediaEvents.resume();
            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    /**
     * playback paused due to buffering
     */
    public void fireBufferStart() {
        try {
            if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null) {
                mMediaEvents.bufferStart();
            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    /**
     * playback resumes after buffering
     */
    public void fireBufferFinish() {
        try {
            if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null) {
                mMediaEvents.bufferFinish();
            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void fireVolumeChange(boolean mute) {
        try {
            if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null && !completeFired) {
                mMediaEvents.volumeChange(mute ? 0 : 1);
            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    /**
     * any early termination of playback
     */
    public void fireSkipped() {
        try {
            if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null) {
                mMediaEvents.skipped();
            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void fireClick() {
        try {
            if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null) {
                mMediaEvents.adUserInteraction(InteractionType.CLICK);
            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }
}
