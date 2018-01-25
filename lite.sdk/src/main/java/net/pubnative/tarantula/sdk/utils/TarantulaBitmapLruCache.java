package net.pubnative.tarantula.sdk.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.LruCache;

public class TarantulaBitmapLruCache {

    public static final String TAG = TarantulaBitmapLruCache.class.getSimpleName();

    private static final int BYTES_IN_KILOBYTES = 1024;
    private static final int MAX_MEMORY_SIZE = 30 * 1024 * 1024; // 30MB

    private static LruCache<String, Bitmap> mMemoryCache;

    static {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / BYTES_IN_KILOBYTES);
        final int cacheSize = maxMemory <= MAX_MEMORY_SIZE ? maxMemory : MAX_MEMORY_SIZE;

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
        if (getBitmapFromMemCache(key) == null) {
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