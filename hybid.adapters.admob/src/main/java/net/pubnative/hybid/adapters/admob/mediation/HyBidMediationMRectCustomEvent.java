package net.pubnative.hybid.adapters.admob.mediation;

import net.pubnative.lite.sdk.models.AdSize;

public class HyBidMediationMRectCustomEvent extends HyBidMediationBannerCustomEvent {
    @Override
    protected AdSize getAdSize(com.google.android.gms.ads.AdSize adSize) {
        return AdSize.SIZE_300x250;
    }
}
