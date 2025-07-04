// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid;

import net.pubnative.lite.sdk.utils.Logger;

import java.util.HashMap;
import java.util.Map;

public class VideoAdCache {
    private static final String TAG = VideoAdCache.class.getSimpleName();
    private final Map<String, VideoAdCacheItem> mAdMap;
    private final String mLatestZoneId = "latestZoneId";

    public VideoAdCache() {
        mAdMap = new HashMap<>();
    }

    public VideoAdCacheItem remove(String zoneId) {
        return mAdMap.remove(zoneId);
    }

    public VideoAdCacheItem inspect(String zoneId) {
        return mAdMap.get(zoneId);
    }

    public VideoAdCacheItem inspectLatest() {
        return mAdMap.get(mLatestZoneId);
    }

    public void put(String zoneId, VideoAdCacheItem adCacheItem) {
        Logger.d(TAG, "VideoAdCache putting video for zone id: " + zoneId);
        mAdMap.put(zoneId, adCacheItem);
        mAdMap.put(mLatestZoneId, adCacheItem);
    }
}
