package net.pubnative.lite.sdk.viewability;

import android.text.TextUtils;
import android.view.View;

import com.iab.omid.library.pubnativenet.ScriptInjector;
import com.iab.omid.library.pubnativenet.adsession.AdEvents;
import com.iab.omid.library.pubnativenet.adsession.AdSession;
import com.iab.omid.library.pubnativenet.adsession.FriendlyObstructionPurpose;
import com.iab.omid.library.pubnativenet.adsession.VerificationScriptResource;

import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingEvent;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public abstract class HyBidViewabilityAdSession {
    protected AdSession mAdSession;
    protected AdEvents mAdEvents;
    protected List<VerificationScriptResource> mVerificationScriptResources = new ArrayList<>();

    ViewabilityManager viewabilityManager;

    public HyBidViewabilityAdSession(ViewabilityManager viewabilityManager) {
        this.viewabilityManager = viewabilityManager;
    }

    public String prependOMJS(String html) {
        if (!viewabilityManager.isViewabilityMeasurementEnabled())
            return html;

        try {
            String htmlString = html;
            if (!TextUtils.isEmpty(viewabilityManager.getServiceJs())) {
                htmlString = ScriptInjector.injectScriptContentIntoHtml(viewabilityManager.getServiceJs(), html);
            }
            return htmlString;
        } catch (Exception e) {
            e.printStackTrace();
            return html;
        }
    }

    public void addVerificationScriptResource(VerificationScriptResource verificationScriptResource) {
        mVerificationScriptResources.add(verificationScriptResource);

    }

    public boolean isVerificationResourcesPresent() {
        if (mVerificationScriptResources != null && !mVerificationScriptResources.isEmpty()) {
            return true;
        }
        return false;
    }

    public void fireLoaded() {
        if (!viewabilityManager.isViewabilityMeasurementEnabled())
            return;

        if (mAdEvents != null) {
            try {
                mAdEvents.loaded();

                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setEventType(Reporting.EventType.VIDEO_AD_SESSION_LOADED);
                viewabilityManager.getReportingController().reportEvent(reportingEvent);

            } catch (IllegalArgumentException | IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void fireImpression() {
        if (!viewabilityManager.isViewabilityMeasurementEnabled())
            return;

        if (mAdEvents != null) {
            try {
                mAdEvents.impressionOccurred();

                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setEventType(Reporting.EventType.IMPRESSION);
                viewabilityManager.getReportingController().reportEvent(reportingEvent);

            } catch (IllegalArgumentException | IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopAdSession() {
        if (!viewabilityManager.isViewabilityMeasurementEnabled())
            return;

        if (mAdSession != null) {
            mAdSession.finish();
            mAdSession = null;

            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.VIDEO_AD_SESSION_STOPPED);
            viewabilityManager.getReportingController().reportEvent(reportingEvent);
        }
    }

    public void removeFriendlyObstruction(View view) {
        if (!viewabilityManager.isViewabilityMeasurementEnabled())
            return;
        if (mAdSession != null) {
            mAdSession.removeFriendlyObstruction(view);
        }
    }

    public void removeAllFriendlyObstructions() {
        if (!viewabilityManager.isViewabilityMeasurementEnabled())
            return;
        if (mAdSession != null) {
            mAdSession.removeAllFriendlyObstructions();
        }
    }

    public void addFriendlyObstruction(View friendlyObstructionView, FriendlyObstructionPurpose purpose, String reason) {
        if (friendlyObstructionView != null && mAdSession != null) {
            mAdSession.addFriendlyObstruction(friendlyObstructionView, purpose, reason);
        }
    }

    protected void createAdEvents() {
        if (mAdSession != null) {
            mAdEvents = AdEvents.createAdEvents(mAdSession);
        }
    }
}
