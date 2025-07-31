// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.interstitial;

import android.app.Activity;

public class PNInterstitialAd extends HyBidInterstitialAd {
    /*
     *  This class is kept for backwards compatibility.
     */

    public interface Listener extends HyBidInterstitialAd.Listener {
    }

    public PNInterstitialAd(Activity activity, String zoneId, Listener listener) {
        super(activity, zoneId, listener);
    }
}
