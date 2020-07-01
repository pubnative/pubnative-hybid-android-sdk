package net.pubnative.lite.sdk.viewability;

import android.view.View;

import com.iab.omid.library.pubnativenet.adsession.AdEvents;
import com.iab.omid.library.pubnativenet.adsession.AdSession;
import com.iab.omid.library.pubnativenet.adsession.AdSessionConfiguration;
import com.iab.omid.library.pubnativenet.adsession.AdSessionContext;
import com.iab.omid.library.pubnativenet.adsession.CreativeType;
import com.iab.omid.library.pubnativenet.adsession.ImpressionType;
import com.iab.omid.library.pubnativenet.adsession.Owner;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.utils.Logger;

public class HyBidViewabilityNativeAdSession extends HyBidViewabilityAdSession {
    private static final String TAG = HyBidViewabilityNativeAdSession.class.getSimpleName();

    public void initAdSession(View view) {
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
            createAdEvents();
            mAdSession.start();
        } catch (IllegalArgumentException e) {
            Logger.e("", e.getMessage());
        } catch (NullPointerException exception) {
            Logger.e(TAG, "OM SDK Ad Session - Exception", exception);
        }
    }
}
