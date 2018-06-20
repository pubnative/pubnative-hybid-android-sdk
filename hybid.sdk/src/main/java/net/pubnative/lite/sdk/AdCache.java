package net.pubnative.lite.sdk;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class AdCache {
    private static final String TAG = AdCache.class.getSimpleName();
    private final Map<String, Ad> mAdMap;

    public AdCache() {
        mAdMap = new HashMap<>();
    }

    public Ad remove(String zoneId) {
        return mAdMap.remove(zoneId);
    }

    public void put(String zoneId, Ad ad) {
        Logger.d(TAG, "AdCache putting ad for zone id: " + zoneId);
        mAdMap.put(zoneId, ad);
    }
}
