// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.viewability;

import android.view.View;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.viewability.baseom.BaseVerificationScriptResource;
import net.pubnative.lite.sdk.viewability.baseom.BaseViewabilityManager;
import net.pubnative.lite.sdk.viewability.baseom.MediaEventType;

import java.util.List;

public class HyBidViewabilityNativeVideoAdSession extends HyBidViewabilityAdSession {
    private static final String TAG = HyBidViewabilityNativeVideoAdSession.class.getSimpleName();
    private static final String OM_EXCEPTION = "OM SDK Ad Session - Exception";

    private Object mMediaEvents;

    private boolean startFired = false;
    private boolean firstQuartileFired = false;
    private boolean midpointFired = false;
    private boolean thirdQuartileFired = false;
    private boolean completeFired = false;

    private boolean muted = true;

    public HyBidViewabilityNativeVideoAdSession(BaseViewabilityManager viewabilityManager) {
        super(viewabilityManager);
    }

    public void initAdSession(View view, List<BaseVerificationScriptResource> verificationScriptResources) {
        if (viewabilityManager != null && !viewabilityManager.isViewabilityMeasurementEnabled())
            return;

        mVerificationScriptResources.addAll(verificationScriptResources);

        try {
            Object adSessionContext = viewabilityManager.createNativeAdSessionContext(mVerificationScriptResources);
            Object adSessionConfiguration = viewabilityManager.getNativeAdSessionConfiguration();

            mAdSession = viewabilityManager.createAdSession(adSessionConfiguration, adSessionContext);
            viewabilityManager.registerAdView(mAdSession, view);
            createAdEvents();
            createMediaEvents();
            viewabilityManager.startAdSession(mAdSession);
        } catch (Exception exception) {
            Logger.e(TAG, OM_EXCEPTION, exception);
        }
    }

    protected void createAdEvents() {
        if (mAdSession != null) {
            mAdEvents = viewabilityManager.createAdEvents(mAdSession);
        }
    }

    protected void createMediaEvents() {
        try {
            if (mAdSession != null) {
                mMediaEvents = viewabilityManager.createMediaEvents(mAdSession);
            }
        } catch (Exception exception) {
            Logger.e(TAG, OM_EXCEPTION, exception);
        }
    }

    @Override
    public void fireLoaded() {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            Object vastProperties = viewabilityManager.createVastPropertiesForNonSkippableMedia();

            if (mAdEvents != null) {
                viewabilityManager.fireEventProperties(mAdEvents, vastProperties);
            }
        } catch (Exception exception) {
            Logger.e(TAG, OM_EXCEPTION, exception);
        }
    }

    public void fireStart(float duration, boolean mute) {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null && !startFired) {
                viewabilityManager.fireMediaEventStart(mMediaEvents, duration, mute ? 0 : 1);
                startFired = true;
            }
        } catch (Exception exception) {
            Logger.e(TAG, OM_EXCEPTION, exception);
        }
    }

    public void fireFirstQuartile() {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null && !firstQuartileFired) {
                viewabilityManager.fireMediaEvents(MediaEventType.FIRST_QUARTILE, mMediaEvents);
                firstQuartileFired = true;
            }
        } catch (Exception exception) {
            Logger.e(TAG, OM_EXCEPTION, exception);
        }
    }

    public void fireMidpoint() {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null && !midpointFired) {
                viewabilityManager.fireMediaEvents(MediaEventType.MIDPOINT, mMediaEvents);
                midpointFired = true;
            }
        } catch (Exception exception) {
            Logger.e(TAG, OM_EXCEPTION, exception);
        }
    }

    public void fireThirdQuartile() {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null && !thirdQuartileFired) {
                viewabilityManager.fireMediaEvents(MediaEventType.THIRD_QUARTILE, mMediaEvents);
                thirdQuartileFired = true;
            }
        } catch (Exception exception) {
            Logger.e(TAG, OM_EXCEPTION, exception);
        }
    }

    public void fireComplete() {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null && !completeFired) {
                viewabilityManager.fireMediaEvents(MediaEventType.COMPLETE, mMediaEvents);
                completeFired = true;
            }
        } catch (Exception exception) {
            Logger.e(TAG, OM_EXCEPTION, exception);
        }
    }

    public void firePause() {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null) {
                viewabilityManager.fireMediaEvents(MediaEventType.PAUSE, mMediaEvents);
            }
        } catch (Exception exception) {
            Logger.e(TAG, OM_EXCEPTION, exception);
        }
    }

    public void fireResume() {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null) {
                viewabilityManager.fireMediaEvents(MediaEventType.RESUME, mMediaEvents);
            }
        } catch (Exception exception) {
            Logger.e(TAG, OM_EXCEPTION, exception);
        }
    }

    /**
     * playback paused due to buffering
     */
    public void fireBufferStart() {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null) {
                viewabilityManager.fireMediaEvents(MediaEventType.BUFFER_START, mMediaEvents);
            }
        } catch (Exception exception) {
            Logger.e(TAG, OM_EXCEPTION, exception);
        }
    }

    /**
     * playback resumes after buffering
     */
    public void fireBufferFinish() {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null) {
                viewabilityManager.fireMediaEvents(MediaEventType.BUFFER_FINISH, mMediaEvents);
            }
        } catch (Exception exception) {
            Logger.e(TAG, OM_EXCEPTION, exception);
        }
    }

    public void fireVolumeChange(boolean mute) {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled() || mute == muted)
                return;

            muted = mute;
            if (mMediaEvents != null && !completeFired) {
                viewabilityManager.fireMediaEventVolumeChange(mMediaEvents, mute ? 0 : 1);
            }
        } catch (Exception exception) {
            Logger.e(TAG, OM_EXCEPTION, exception);
        }
    }

    /**
     * any early termination of playback
     */
    public void fireSkipped() {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null) {
                viewabilityManager.fireMediaEvents(MediaEventType.SKIPPED, mMediaEvents);
            }
        } catch (Exception exception) {
            Logger.e(TAG, OM_EXCEPTION, exception);
        }
    }

    public void fireClick() {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null) {
                viewabilityManager.fireMediaEvents(MediaEventType.CLICK, mMediaEvents);
            }
        } catch (Exception exception) {
            Logger.e(TAG, OM_EXCEPTION, exception);
        }
    }
}
