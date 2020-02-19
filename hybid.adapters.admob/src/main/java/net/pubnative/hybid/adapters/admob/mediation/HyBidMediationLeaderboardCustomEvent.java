package net.pubnative.hybid.adapters.admob.mediation;

import net.pubnative.lite.sdk.models.AdSize;

public class HyBidMediationLeaderboardCustomEvent extends HyBidMediationBannerCustomEvent {
    @Override
    protected AdSize getAdSize() {
        return AdSize.SIZE_728x90;
    }
}
