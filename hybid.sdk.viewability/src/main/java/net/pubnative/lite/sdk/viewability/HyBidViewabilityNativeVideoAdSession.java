package net.pubnative.lite.sdk.viewability;

import android.view.View;

import com.iab.omid.library.pubnativenet.adsession.AdSession;
import com.iab.omid.library.pubnativenet.adsession.AdSessionConfiguration;
import com.iab.omid.library.pubnativenet.adsession.AdSessionContext;
import com.iab.omid.library.pubnativenet.adsession.CreativeType;
import com.iab.omid.library.pubnativenet.adsession.ImpressionType;
import com.iab.omid.library.pubnativenet.adsession.Owner;
import com.iab.omid.library.pubnativenet.adsession.VerificationScriptResource;
import com.iab.omid.library.pubnativenet.adsession.media.InteractionType;
import com.iab.omid.library.pubnativenet.adsession.media.MediaEvents;
import com.iab.omid.library.pubnativenet.adsession.media.Position;
import com.iab.omid.library.pubnativenet.adsession.media.VastProperties;

import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.utils.Logger;

import org.json.JSONException;

import java.util.List;

public class HyBidViewabilityNativeVideoAdSession extends HyBidViewabilityNativeAdSession {
    private static final String TAG = HyBidViewabilityNativeVideoAdSession.class.getSimpleName();

    private MediaEvents mMediaEvents;

    private boolean startFired = false;
    private boolean firstQuartileFired = false;
    private boolean midpointFired = false;
    private boolean thirdQuartileFired = false;
    private boolean completeFired = false;

    public HyBidViewabilityNativeVideoAdSession(ViewabilityManager viewabilityManager) {
        super(viewabilityManager);
    }

    public void initAdSession(View view, List<VerificationScriptResource> verificationScriptResources) {
        if (!viewabilityManager.isViewabilityMeasurementEnabled())
            return;

        mVerificationScriptResources.addAll(verificationScriptResources);

        try {
            AdSessionContext adSessionContext = AdSessionContext.createNativeAdSessionContext(viewabilityManager.getPartner(),
                    viewabilityManager.getServiceJs(), mVerificationScriptResources, "", "");


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

            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.VIDEO_AD_SESSION_STARTED);
            viewabilityManager.getReportingController().reportEvent(reportingEvent);

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
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            VastProperties vastProperties = VastProperties.createVastPropertiesForNonSkippableMedia(false, Position.STANDALONE);

            if (mAdEvents != null) {
                mAdEvents.loaded(vastProperties);
            }

            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.VIDEO_AD_SESSION_LOADED);
            viewabilityManager.getReportingController().reportEvent(reportingEvent);

        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void fireStart(float duration, boolean mute) {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null && !startFired) {
                mMediaEvents.start(duration, mute ? 0 : 1);
                startFired = true;

                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setEventType(Reporting.EventType.VIDEO_AD_SESSION_STARTED);
                viewabilityManager.getReportingController().reportEvent(reportingEvent);
            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void fireFirstQuartile() {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null && !firstQuartileFired) {
                mMediaEvents.firstQuartile();
                firstQuartileFired = true;

                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setEventType(Reporting.EventType.VIDEO_AD_FIRST_QUARTILE);
                viewabilityManager.getReportingController().reportEvent(reportingEvent);

            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void fireMidpoint() {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null && !midpointFired) {
                mMediaEvents.midpoint();
                midpointFired = true;

                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setEventType(Reporting.EventType.VIDEO_AD_MIDPOINT);
                viewabilityManager.getReportingController().reportEvent(reportingEvent);

            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void fireThirdQuartile() {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null && !thirdQuartileFired) {
                mMediaEvents.thirdQuartile();
                thirdQuartileFired = true;

                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setEventType(Reporting.EventType.VIDEO_AD_THIRD_QUARTILE);
                viewabilityManager.getReportingController().reportEvent(reportingEvent);

            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void fireComplete() {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null && !completeFired) {
                mMediaEvents.complete();
                completeFired = true;

                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setEventType(Reporting.EventType.VIDEO_AD_COMPLETE);
                viewabilityManager.getReportingController().reportEvent(reportingEvent);

            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void firePause() {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null) {
                mMediaEvents.pause();

                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setEventType(Reporting.EventType.VIDEO_AD_PAUSE);
                viewabilityManager.getReportingController().reportEvent(reportingEvent);

            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void fireResume() {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null) {
                mMediaEvents.resume();

                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setEventType(Reporting.EventType.VIDEO_AD_RESUME);
                viewabilityManager.getReportingController().reportEvent(reportingEvent);

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
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null) {
                mMediaEvents.bufferStart();

                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setEventType(Reporting.EventType.VIDEO_AD_BUFFER_START);
                viewabilityManager.getReportingController().reportEvent(reportingEvent);

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
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null) {
                mMediaEvents.bufferFinish();

                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setEventType(Reporting.EventType.VIDEO_AD_BUFFER_FINISH);
                viewabilityManager.getReportingController().reportEvent(reportingEvent);

            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void fireVolumeChange(boolean mute) {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null && !completeFired) {
                mMediaEvents.volumeChange(mute ? 0 : 1);

                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setEventType(Reporting.EventType.VIDEO_AD_VOLUME_CHANGE);
                viewabilityManager.getReportingController().reportEvent(reportingEvent);

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
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null) {
                mMediaEvents.skipped();

                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setEventType(Reporting.EventType.VIDEO_AD_SKIPPED);
                viewabilityManager.getReportingController().reportEvent(reportingEvent);

            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void fireClick() {
        try {
            if (!viewabilityManager.isViewabilityMeasurementEnabled())
                return;

            if (mMediaEvents != null) {
                mMediaEvents.adUserInteraction(InteractionType.CLICK);

                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setEventType(Reporting.EventType.VIDEO_AD_CLICKED);
                viewabilityManager.getReportingController().reportEvent(reportingEvent);

            }
        } catch (Exception exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }
}
