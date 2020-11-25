package net.pubnative.lite.sdk.auction;

import net.pubnative.lite.sdk.models.Ad;

public interface AdSource {
    public interface Listener {
        void onAdFetched(Ad ad);
        void onError(Throwable error);
    }

    void fetchAd(Listener listener);
}
