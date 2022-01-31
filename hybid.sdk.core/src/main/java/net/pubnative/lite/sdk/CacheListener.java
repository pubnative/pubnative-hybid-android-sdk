package net.pubnative.lite.sdk;

public interface CacheListener {
    void onCacheSuccess();
    void onCacheFailed(Throwable error);
}
