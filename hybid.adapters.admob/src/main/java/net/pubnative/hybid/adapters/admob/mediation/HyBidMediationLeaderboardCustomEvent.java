// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.hybid.adapters.admob.mediation;

import net.pubnative.lite.sdk.models.AdSize;

public class HyBidMediationLeaderboardCustomEvent extends HyBidMediationBannerCustomEvent {
    @Override
    protected AdSize getAdSize(com.google.android.gms.ads.AdSize adSize) {
        return AdSize.SIZE_728x90;
    }
}
