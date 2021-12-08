package net.pubnative.lite.sdk.auction;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;

public interface AdSource {
    interface Listener {
        void onAdFetched(Ad ad);
        void onError(AuctionError error);
    }

    void fetchAd(Listener listener);

    AdSize getAdSize();
    String getName();
    double getECPM();
}
