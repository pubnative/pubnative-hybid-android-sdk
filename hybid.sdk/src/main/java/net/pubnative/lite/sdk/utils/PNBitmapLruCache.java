// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.LruCache;

/**
 * @deprecated No longer used by the SDK and will be removed in a future major release.
 */
@Deprecated
public class PNBitmapLruCache {

    private static final int BYTES_IN_KILOBYTES = 1024;
    private static final int MAX_CACHE_SIZE_KB = 30 * 1024;
    private static final int HEAP_FRACTION = 8;

    private static final LruCache<String, Bitmap> mMemoryCache;

    static {
        final int maxMemoryKb = (int) (Runtime.getRuntime().maxMemory() / BYTES_IN_KILOBYTES);
        final int cacheSize = Math.min(maxMemoryKb / HEAP_FRACTION, MAX_CACHE_SIZE_KB);

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / BYTES_IN_KILOBYTES;
            }
        };
    }

    public static void addBitmapToMemoryCache(String url, Bitmap bitmap) {
        if (TextUtils.isEmpty(url) || bitmap == null) {
            return;
        }

        String key = String.valueOf(url.hashCode());
        if (mMemoryCache.get(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        } else {
            String key = String.valueOf(url.hashCode());
            return mMemoryCache.get(key);
        }
    }

}