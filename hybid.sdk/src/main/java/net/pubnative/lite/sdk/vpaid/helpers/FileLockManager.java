// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.helpers;

import net.pubnative.lite.sdk.utils.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple file reference counter to prevent deletion of files that are still in use by ads.
 *
 * Usage:
 * - acquire(filePath) when an ad starts using a file
 * - release(filePath) when an ad is done with the file
 * - isLocked(filePath) before deleting a file
 */
public class FileLockManager {
    private static final String TAG = FileLockManager.class.getSimpleName();
    private static FileLockManager sInstance;

    // Map: filePath -> reference count
    private final Map<String, Integer> mFileReferences;

    private FileLockManager() {
        mFileReferences = new ConcurrentHashMap<>();
    }

    public static synchronized FileLockManager getInstance() {
        if (sInstance == null) {
            sInstance = new FileLockManager();
        }
        return sInstance;
    }

    /**
     * Acquire a reference to a file (increment reference count).
     * Call this when an ad starts using a file.
     *
     * @param filePath The path to the file
     */
    public synchronized void acquire(String filePath) {
        if (filePath == null) {
            return;
        }

        Integer count = mFileReferences.get(filePath);
        int newCount = (count == null ? 0 : count) + 1;
        mFileReferences.put(filePath, newCount);
        Logger.d(TAG, "File locked: " + filePath + " (refCount=" + newCount + ")");
    }

    /**
     * Release a reference to a file (decrement reference count).
     * Call this when an ad is done using a file.
     *
     * @param filePath The path to the file
     */
    public synchronized void release(String filePath) {
        if (filePath == null) {
            return;
        }

        Integer count = mFileReferences.get(filePath);
        if (count == null || count <= 0) {
            Logger.w(TAG, "Attempted to release unlocked file: " + filePath);
            return;
        }

        int newCount = count - 1;
        if (newCount == 0) {
            mFileReferences.remove(filePath);
            Logger.d(TAG, "File unlocked: " + filePath);
        } else {
            mFileReferences.put(filePath, newCount);
            Logger.d(TAG, "File reference released: " + filePath + " (refCount=" + newCount + ")");
        }
    }

    /**
     * Check if a file is currently locked (in use by any ad).
     *
     * @param filePath The path to the file
     * @return true if the file is locked (has active references)
     */
    public synchronized boolean isLocked(String filePath) {
        if (filePath == null) {
            return false;
        }

        Integer count = mFileReferences.get(filePath);
        return count != null && count > 0;
    }

    /**
     * Force release a file lock. Used for cleanup of expired files.
     * Should only be called when file is expired and needs to be deleted.
     *
     * @param filePath The path to the file
     */
    public synchronized void forceRelease(String filePath) {
        if (filePath == null) {
            return;
        }

        Integer count = mFileReferences.get(filePath);
        if (count != null && count > 0) {
            mFileReferences.remove(filePath);
            Logger.w(TAG, "Force released lock on expired file: " + filePath + " (was refCount=" + count + ")");
        }
    }
}

