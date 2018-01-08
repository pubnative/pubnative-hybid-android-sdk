package net.pubnative.tarantula.sdk;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.utils.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class AdCache {
    @NonNull
    private static final String TAG = AdCache.class.getSimpleName();
    @NonNull
    private final Map<String, Ad> mAdMap;

    public AdCache() {
        mAdMap = new HashMap<>();
    }

    @Nullable
    public Ad remove(@NonNull String adUnitKey) {
        return mAdMap.remove(adUnitKey);
    }

    public void put(@NonNull String adUnitKey, @NonNull Ad ad) {
        Logger.d(TAG, "AdCache putting ad for adUnitKey: " + adUnitKey);
        mAdMap.put(adUnitKey, ad);
    }
}
