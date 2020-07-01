package net.pubnative.lite.sdk.viewability;

import android.text.TextUtils;
import android.view.View;

import com.iab.omid.library.pubnativenet.ScriptInjector;
import com.iab.omid.library.pubnativenet.adsession.AdEvents;
import com.iab.omid.library.pubnativenet.adsession.AdSession;
import com.iab.omid.library.pubnativenet.adsession.FriendlyObstructionPurpose;
import com.iab.omid.library.pubnativenet.adsession.VerificationScriptResource;

import net.pubnative.lite.sdk.HyBid;

import java.util.ArrayList;
import java.util.List;

public abstract class HyBidViewabilityAdSession {
    protected AdSession mAdSession;
    protected AdEvents mAdEvents;
    protected List<VerificationScriptResource> mVerificationScriptResources = new ArrayList<>();

    public String prependOMJS(String html) {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return html;

        try {
            String htmlString = html;
            if (!TextUtils.isEmpty(HyBid.getViewabilityManager().getServiceJs())) {
                htmlString = ScriptInjector.injectScriptContentIntoHtml(HyBid.getViewabilityManager().getServiceJs(), html);
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
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        if (mAdEvents != null) {
            try {
                mAdEvents.loaded();
            } catch (IllegalArgumentException | IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void fireImpression() {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        if (mAdEvents != null) {
            try {
                mAdEvents.impressionOccurred();
            } catch (IllegalArgumentException | IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopAdSession() {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        if (mAdSession != null) {
            mAdSession.finish();
            mAdSession = null;
        }
    }

    public void removeFriendlyObstruction(View view) {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;
        if (mAdSession != null) {
            mAdSession.removeFriendlyObstruction(view);
        }
    }

    public void removeAllFriendlyObstructions() {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
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
