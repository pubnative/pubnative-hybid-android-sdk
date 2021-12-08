package net.pubnative.lite.sdk.viewability;

import android.webkit.WebView;

import com.iab.omid.library.pubnativenet.adsession.AdSession;
import com.iab.omid.library.pubnativenet.adsession.AdSessionConfiguration;
import com.iab.omid.library.pubnativenet.adsession.AdSessionContext;
import com.iab.omid.library.pubnativenet.adsession.CreativeType;
import com.iab.omid.library.pubnativenet.adsession.ImpressionType;
import com.iab.omid.library.pubnativenet.adsession.Owner;

import net.pubnative.lite.sdk.utils.Logger;

public class HyBidViewabilityWebAdSession extends HyBidViewabilityAdSession {
    private static final String TAG = HyBidViewabilityWebAdSession.class.getSimpleName();

    public HyBidViewabilityWebAdSession(ViewabilityManager viewabilityManager) {
        super(viewabilityManager);
    }

    public void initAdSession(WebView webView, boolean isVideoAd) {
        if (!viewabilityManager.isViewabilityMeasurementEnabled())
            return;

        try {
            String customReferenceData = "";
            String contentUrl = "";
            AdSessionContext adSessionContext = AdSessionContext.createHtmlAdSessionContext(
                    viewabilityManager.getPartner(), webView,
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
            createAdEvents();
            mAdSession.start();

        } catch (IllegalArgumentException e) {
            Logger.e("", e.getMessage());
        } catch (NullPointerException exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }
}
