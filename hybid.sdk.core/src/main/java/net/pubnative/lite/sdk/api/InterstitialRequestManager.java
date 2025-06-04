// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.api;

import net.pubnative.lite.sdk.models.AdSize;

/**
 * Created by erosgarciaponte on 11.01.18.
 */

public class InterstitialRequestManager extends RequestManager {
    @Override
    public AdSize getAdSize() {
        return AdSize.SIZE_INTERSTITIAL;
    }
}
