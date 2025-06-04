// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import net.pubnative.lite.sdk.HyBid;

/**
 * Created by erosgarciaponte on 24.01.18.
 */

public class PNInitializationHelper {
    public boolean isInitialized() {
        return HyBid.isInitialized();
    }
}
