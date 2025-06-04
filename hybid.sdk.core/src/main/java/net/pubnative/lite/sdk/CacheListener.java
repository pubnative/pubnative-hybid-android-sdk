// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk;

public interface CacheListener {
    void onCacheSuccess();
    void onCacheFailed(Throwable error);
}
