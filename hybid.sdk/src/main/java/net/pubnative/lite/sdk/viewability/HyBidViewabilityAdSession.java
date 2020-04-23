package net.pubnative.lite.sdk.viewability;

import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;

import com.iab.omid.library.pubnativenet.ScriptInjector;
import com.iab.omid.library.pubnativenet.adsession.AdEvents;
import com.iab.omid.library.pubnativenet.adsession.AdSession;
import com.iab.omid.library.pubnativenet.adsession.AdSessionConfiguration;
import com.iab.omid.library.pubnativenet.adsession.AdSessionContext;
import com.iab.omid.library.pubnativenet.adsession.CreativeType;
import com.iab.omid.library.pubnativenet.adsession.FriendlyObstructionPurpose;
import com.iab.omid.library.pubnativenet.adsession.ImpressionType;
import com.iab.omid.library.pubnativenet.adsession.Owner;
import com.iab.omid.library.pubnativenet.adsession.VerificationScriptResource;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class HyBidViewabilityAdSession {
    private static final String TAG = HyBidViewabilityAdSession.class.getSimpleName();

    private AdSession mAdSession;
    private List<VerificationScriptResource> mVerificationScriptResources = new ArrayList<>();

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

    public void initAdSession(WebView webView, boolean isVideoAd) {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        try {
            String customReferenceData = "";
            String contentUrl = "";
            AdSessionContext adSessionContext = AdSessionContext.createHtmlAdSessionContext(
                    HyBid.getViewabilityManager().getPartner(), webView,
                    contentUrl, customReferenceData);

            Owner owner = isVideoAd ? Owner.JAVASCRIPT : Owner.NATIVE;

            AdSessionConfiguration adSessionConfiguration =
                    AdSessionConfiguration.createAdSessionConfiguration(
                            isVideoAd ? CreativeType.DEFINED_BY_JAVASCRIPT : CreativeType.HTML_DISPLAY,
                            isVideoAd ? ImpressionType.DEFINED_BY_JAVASCRIPT : ImpressionType.BEGIN_TO_RENDER,
                            owner,
                            isVideoAd ? owner : Owner.NONE, false);


            mAdSession = AdSession.createAdSession(adSessionConfiguration, adSessionContext);
            mAdSession.registerAdView(webView);
            mAdSession.start();
        } catch (IllegalArgumentException e) {
            Logger.e("", e.getMessage());
        } catch (NullPointerException exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }

    public void initNativeAdSession(View view) {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        try {
            AdSessionContext adSessionContext = AdSessionContext.createNativeAdSessionContext(HyBid.getViewabilityManager().getPartner(),
                    HyBid.getViewabilityManager().getServiceJs(), mVerificationScriptResources, "", "");


            AdSessionConfiguration adSessionConfiguration =
                    AdSessionConfiguration.createAdSessionConfiguration(
                            CreativeType.NATIVE_DISPLAY,
                            ImpressionType.BEGIN_TO_RENDER,
                            Owner.NATIVE, Owner.NONE, false);


            mAdSession = AdSession.createAdSession(adSessionConfiguration, adSessionContext);
            mAdSession.registerAdView(view);
            mAdSession.start();
        } catch (IllegalArgumentException e) {
            Logger.e("", e.getMessage());
        } catch (NullPointerException exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
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

        if (mAdSession != null) {
            try {
                AdEvents adEvents = AdEvents.createAdEvents(mAdSession);
                adEvents.loaded();
            } catch (IllegalArgumentException | IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void fireImpression() {
        if (!HyBid.getViewabilityManager().isViewabilityMeasurementEnabled())
            return;

        if (mAdSession != null) {
            try {
                AdEvents adEvents = AdEvents.createAdEvents(mAdSession);
                adEvents.impressionOccurred();
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

    public void addFriendlyObstruction(View friendlyObstructionView, String reason) {
        if (friendlyObstructionView != null && mAdSession != null) {
            mAdSession.addFriendlyObstruction(friendlyObstructionView, FriendlyObstructionPurpose.OTHER, reason);
        }
    }
}
