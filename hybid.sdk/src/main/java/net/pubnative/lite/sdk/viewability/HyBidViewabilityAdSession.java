package net.pubnative.lite.sdk.viewability;

import android.view.View;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.viewability.baseom.BaseFriendlyObstructionPurpose;
import net.pubnative.lite.sdk.viewability.baseom.BaseVerificationScriptResource;
import net.pubnative.lite.sdk.viewability.baseom.BaseViewabilityManager;

import java.util.ArrayList;
import java.util.List;

public abstract class HyBidViewabilityAdSession {
    private static final String TAG = HyBidViewabilityAdSession.class.getSimpleName();
    protected Object mAdSession;
    protected Object mAdEvents;
    protected final List<BaseVerificationScriptResource> mVerificationScriptResources = new ArrayList<>();

    final BaseViewabilityManager viewabilityManager;

    public HyBidViewabilityAdSession(BaseViewabilityManager viewabilityManager) {
        this.viewabilityManager = viewabilityManager;
    }

    public void fireLoaded() {
        if (!viewabilityManager.isViewabilityMeasurementEnabled())
            return;

        if (mAdEvents != null) {
            try {
                viewabilityManager.fireLoaded(mAdEvents);
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
                viewabilityManager.fireImpression(mAdEvents);
            } catch (IllegalArgumentException | IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopAdSession() {
        if (!viewabilityManager.isViewabilityMeasurementEnabled())
            return;

        if (mAdSession != null) {
            try {
                viewabilityManager.stopAdSession(mAdSession);
            } catch (RuntimeException e) {
                Logger.e(TAG, e.getMessage());
            }
            mAdSession = null;
        }
    }

    public void addFriendlyObstruction(View friendlyObstructionView, BaseFriendlyObstructionPurpose purpose, String reason) {
        if (friendlyObstructionView != null && mAdSession != null) {
            viewabilityManager.addFriendlyObstruction(mAdSession, friendlyObstructionView, purpose, reason);
        }
    }
}
