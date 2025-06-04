// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.adapters.dfp;

import net.pubnative.lite.sdk.models.AdSize;

public class HyBidDFPMRectCustomEvent extends HyBidDFPBannerCustomEvent {
    /*
     *  This class is kept for backwards compatibility.
     */

    @Override
    public AdSize getAdSize(com.google.android.gms.ads.AdSize adSize) {
        return AdSize.SIZE_300x250;
    }
}
