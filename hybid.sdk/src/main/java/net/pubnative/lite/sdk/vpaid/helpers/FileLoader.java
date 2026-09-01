// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.PlayerInfo;
import net.pubnative.lite.sdk.vpaid.enums.ConnectionType;
import net.pubnative.lite.sdk.vpaid.enums.VastError;
import net.pubnative.lite.sdk.vpaid.utils.FileUtils;
import net.pubnative.lite.sdk.vpaid.utils.Utils;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

public class FileLoader {

    public interface Callback {
        void onFileLoaded(String filePath);

        void onError(PlayerInfo info);

        /**
         * @param progress 0..1
         */
        void onProgress(double progress);
    }

    private static class FileHeaders {
        final String eTag;
        final int fileLength;
        final Bitmap bitmap;

        FileHeaders(String eTag, int fileLength) {
            this.eTag = eTag;
            this.fileLength = fileLength;
            this.bitmap = null;
        }

        FileHeaders(String eTag, int fileLength, Bitmap bitmap) {
            this.eTag = eTag;
            this.fileLength = fileLength;
            this.bitmap = bitmap;
        }
    }

    private static class InitialFetchResult {
        final FileHeaders headers;
        final int downloadedBytes;

        InitialFetchResult(FileHeaders headers, int downloadedBytes) {
            this.headers = headers;
            this.downloadedBytes = downloadedBytes;
        }
    }

    private static class AttemptState {
        final FileHeaders headers;
        final int downloadedBytes;
        final int attemptsCount;

        AttemptState(FileHeaders headers, int downloadedBytes, int attemptsCount) {
            this.headers = headers;
            this.downloadedBytes = downloadedBytes;
            this.attemptsCount = attemptsCount;
        }
    }

    private static class NonRetryableIOException extends RuntimeException {
        NonRetryableIOException(Throwable cause) {
            super(cause);
        }
    }

    private static final String LOG_TAG = FileLoader.class.getSimpleName();
    private static final int CONNECT_TIMEOUT = 10_000;
    private static final int READ_TIMEOUT = 10_000;
    private static final int MAX_ATTEMPTS = 3;
    private static final long BASE_BACKOFF_MS = 250;
    private static final long MAX_BACKOFF_MS = 2_000;

    private static boolean useMobileNetworkForCaching;

    private final Context mContext;
    private final File mLoadingFile;
    private final Callback mCallback;
    private final String mRemoteFileUrl;

    private volatile HttpURLConnection mConnection;
    private volatile boolean mIsFileFullyDownloaded;
    private volatile boolean mStop;
    private volatile long mLoadStartMs;
    private final AtomicReference<Runnable> mPendingRetry = new AtomicReference<>();

    // progress flags
    private boolean firstQuartile;
    private boolean midpoint;
    private boolean thirdQuartile;
    private boolean mIsEndCard;

    public FileLoader(String fileUrl, Context context, Callback callback, Boolean isEndCard) {
        mCallback = callback;
        mContext = context;
        mRemoteFileUrl = fileUrl;
        mIsEndCard = isEndCard;
        String shortFileName = TextUtils.isEmpty(mRemoteFileUrl) ? "default_file" : FileUtils.obtainHashName(mRemoteFileUrl);
        mLoadingFile = new File(FileUtils.getParentDir(mContext), shortFileName);
    }

    public void start() {
        Logger.d(LOG_TAG, "start");
        handleEmulator();
        Logger.d(LOG_TAG, "Use mobile network for caching: " + useMobileNetworkForCaching);

        if (TextUtils.isEmpty(mRemoteFileUrl)) {
            mCallback.onError(new PlayerInfo("FileUrl is empty"));
            return;
        }

        if (mLoadingFile.exists()) {
            Logger.d(LOG_TAG, "File already exists");
            handleFileFullDownloaded();
        } else {
            maybeLoadFile();
        }
    }

    private void maybeLoadFile() {
        int connectionType = RequestParametersProvider.getConnectionType(mContext);
        if (connectionType != ConnectionType.WIFI && !useMobileNetworkForCaching) {
            if (mCallback != null) {
                mCallback.onError(new PlayerInfo("Mobile network. File will not be cached"));
            }
            return;
        }
        ExecutorHelper.getExecutor().submit(this::load);
    }

    /**
     * Runs the first attempt synchronously; onLoadingAttemptFinished() takes it from there.
     */
    private void load() {
        try {
            if (mStop) {
                return;
            }

            File parentDir = FileUtils.getParentDir(mContext);
            if (parentDir == null || !parentDir.canWrite()) {
                Logger.e(LOG_TAG, "Cache directory unavailable or not writable, aborting before any network request");
                if (mCallback != null) {
                    mCallback.onError(new PlayerInfo("Cache directory unavailable or not writable"));
                }
                return;
            }

            mLoadStartMs = System.currentTimeMillis();

            InitialFetchResult initial;
            try {
                initial = fetchHeadersAndBody(mRemoteFileUrl, mLoadingFile);
            } catch (NonRetryableIOException e) {
                Logger.e(LOG_TAG, "Cache write failed, aborting without retry: " + e.getCause());
                if (mCallback != null) {
                    mCallback.onError(new PlayerInfo("Cache write failed, aborting without retry"));
                }
                return;
            }
            if (initial == null) {
                if (mCallback != null) {
                    mCallback.onError(new PlayerInfo("Error during loading file"));
                }
                return;
            }

            Logger.d(LOG_TAG, "File length: " + initial.headers.fileLength);
            onLoadingAttemptFinished(new AttemptState(initial.headers, initial.downloadedBytes, 1));
        } catch (Throwable t) {
            Logger.e(LOG_TAG, "Unexpected FileLoader error: " + t);
            reportUnexpectedFailure();
        }
    }

    private void onLoadingAttemptFinished(AttemptState state) {
        if (mStop) {
            return;
        }
        // Finish (don't retry) once there is no more progress to make:
        //  - downloadedBytes == fileLength: fully downloaded.
        //  - fileLength < 0: no Content-Length, so there is no byte target to reach.
        //  - bitmap != null: end card already decoded. Redundant with fileLength < 0 today, kept explicit.
        //  - attemptsCount >= MAX_ATTEMPTS: retry budget exhausted.
        if (state.downloadedBytes == state.headers.fileLength
                || state.headers.fileLength < 0
                || state.headers.bitmap != null
                || state.attemptsCount >= MAX_ATTEMPTS) {
            finishLoading(state, false);
            return;
        }
        Runnable retry = () -> ExecutorHelper.getExecutor().submit(() -> runRetry(state));
        mPendingRetry.set(retry);
        RetryScheduler.postDelayed(retry, computeBackoffMs(state.attemptsCount));
    }

    private static long computeBackoffMs(int attemptsCount) {
        return Math.min(BASE_BACKOFF_MS * (1L << (attemptsCount - 1)), MAX_BACKOFF_MS);
    }

    private void runRetry(AttemptState state) {
        try {
            if (mStop) {
                return;
            }
            int newDownloadedBytes;
            try {
                newDownloadedBytes = appendFile(mLoadingFile, mRemoteFileUrl, state.downloadedBytes, state.headers);
            } catch (NonRetryableIOException e) {
                finishLoading(state, true);
                return;
            }
            onLoadingAttemptFinished(new AttemptState(state.headers, newDownloadedBytes, state.attemptsCount + 1));
        } catch (Throwable t) {
            Logger.e(LOG_TAG, "Unexpected FileLoader error during retry: " + t);
            reportUnexpectedFailure();
        }
    }

    private void finishLoading(AttemptState state, boolean nonRetryable) {
        long elapsedMs = System.currentTimeMillis() - mLoadStartMs;
        Logger.d(LOG_TAG, "Load time: " + elapsedMs / 1000.0);
        Logger.d(LOG_TAG, "AttemptsCount: " + state.attemptsCount);
        if (state.downloadedBytes == state.headers.fileLength) {
            handleFileFullDownloaded();
        } else if (state.headers.bitmap != null) {
            if (saveBitmapIntoFile(state.headers.bitmap)) {
                handleFileFullDownloaded();
            } else {
                // don't report success if the decoded end card couldn't be persisted
                deleteLoadingFile();
                if (mCallback != null) {
                    mCallback.onError(new PlayerInfo("Cache write failed, aborting without retry"));
                }
            }
        } else {
            // don't leave a partial file that a later load would treat as a cache hit
            deleteLoadingFile();
            if (mCallback != null) {
                String reason;
                if (nonRetryable) {
                    reason = "Cache write failed, aborting without retry";
                } else if (mIsEndCard && state.headers.fileLength < 0) {
                    reason = "End card could not be decoded";
                } else {
                    reason = "Error during file loading, attemptsCount: " + state.attemptsCount;
                }
                mCallback.onError(new PlayerInfo(reason));
            }
        }
    }

    private boolean saveBitmapIntoFile(Bitmap bitmap) {
        return new AndroidBmpUtil().save(bitmap, mLoadingFile.getAbsolutePath());
    }

    /** Removes a partial/failed cache file so a later load doesn't treat it as a cache hit. */
    private void deleteLoadingFile() {
        if (mLoadingFile != null && mLoadingFile.exists() && !mLoadingFile.delete()) {
            Logger.w(LOG_TAG, "Failed to delete cache file: " + mLoadingFile.getAbsolutePath());
        }
    }

    /** Last-resort callback for an unexpected Throwable, so a failed load can never end silently. */
    private void reportUnexpectedFailure() {
        if (mStop || mIsFileFullyDownloaded) {
            return;
        }
        deleteLoadingFile();
        if (mCallback == null) {
            return;
        }
        try {
            mCallback.onError(new PlayerInfo("Unexpected error during file loading"));
        } catch (Throwable t) {
            Logger.e(LOG_TAG, "Callback threw while reporting an unexpected failure: " + t);
        }
    }

    /**
     * A retry always restarts from byte 0 -- streamBody() truncates the file to match.
     *
     * @return total progress
     */
    private int appendFile(File file, String url, int downloadedBytes, FileHeaders headers) {
        try {
            mConnection = obtainGetConnection(url, downloadedBytes, headers);
        } catch (Exception e) {
            Logger.e(LOG_TAG, "appendFile interrupted: " + e.getMessage());
            return downloadedBytes;
        }
        return streamBody(mConnection, file, headers, 0);
    }

    private int streamBody(HttpURLConnection connection, File file, FileHeaders headers, int downloadedBytes) {
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file, downloadedBytes > 0);
        } catch (IOException e) {
            Logger.e(LOG_TAG, "Cannot write cache file, aborting: " + e.getMessage());
            throw new NonRetryableIOException(e);
        }
        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
                downloadedBytes += length;
                double progress = downloadedBytes / (double) headers.fileLength;
                handleProgress(progress);
            }
        } catch (Exception e) {
            Logger.e(LOG_TAG, "appendFile interrupted: " + e.getMessage());
        } finally {
            closeStream(inputStream);
            closeStream(outputStream);
        }
        return downloadedBytes;
    }

    /**
     * Opens a single GET connection and streams the body directly from the response used to read
     * headers, instead of the old probe-then-download two-request flow. setRequestMethod("GET")
     * is called before any header/response accessor since calling it after is a silent no-op on
     * Android's OkHttp HttpURLConnection once the request is already dispatched.
     */
    private InitialFetchResult fetchHeadersAndBody(String remoteFileUrl, File file) {
        try {
            URL url = new URL(remoteFileUrl);
            mConnection = (HttpURLConnection) url.openConnection();
            mConnection.setReadTimeout(READ_TIMEOUT);
            mConnection.setConnectTimeout(CONNECT_TIMEOUT);
            mConnection.setRequestMethod("GET");
            int status = mConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                String eTag = mConnection.getHeaderField("ETag");
                int fileLength = mConnection.getContentLength();
                if (fileLength == -1) {
                    Bitmap bitmap = null;
                    if (mIsEndCard) {
                        // Decode the end card from this response rather than a second request.
                        // Throwable: decodeStream can throw OOM, and an Error escaping means no callback.
                        try (InputStream endCardStream = new BufferedInputStream(mConnection.getInputStream())) {
                            bitmap = BitmapFactory.decodeStream(endCardStream);
                        } catch (Throwable t) {
                            Logger.e(LOG_TAG, "End card decode failed: " + t);
                        }
                    }
                    return new InitialFetchResult(new FileHeaders(eTag, fileLength, bitmap), 0);
                }
                FileHeaders headers = new FileHeaders(eTag, fileLength);
                int downloadedBytes = streamBody(mConnection, file, headers, 0);
                return new InitialFetchResult(headers, downloadedBytes);
            } else if (status == HttpURLConnection.HTTP_FORBIDDEN ||
                    status == HttpURLConnection.HTTP_PARTIAL ||
                    status == HttpURLConnection.HTTP_NOT_FOUND) {
                Logger.e(LOG_TAG, "File not found by URL: " + mRemoteFileUrl);
                ErrorLog.postError(mContext, VastError.TRAFFICKING);
                return null;
            } else {
                return null;
            }
        } catch (SocketTimeoutException e) {
            Logger.e(LOG_TAG, "Timeout by URL: " + mRemoteFileUrl);
            ErrorLog.postError(mContext, VastError.TIMEOUT);
            return null;
        } catch (IOException e) {
            Logger.e(LOG_TAG, "File not found by URL: " + mRemoteFileUrl);
            ErrorLog.postError(mContext, VastError.FILE_NOT_FOUND);
            return null;
        } finally {
            if (mConnection != null) {
                mConnection.disconnect();
            }
        }
    }

    private HttpURLConnection obtainGetConnection(String remoteFileUrl, int downloadedBytes, FileHeaders headers) throws IOException {
        URL url = new URL(remoteFileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setRequestMethod("GET");
        return connection;
    }

    private void handleProgress(double progress) {
        if (mCallback != null) {
            if (!firstQuartile) {
                if (progress > 1.0 / 4) {
                    firstQuartile = true;
                    mCallback.onProgress(1.0 / 4);
                }
            } else if (!midpoint) {
                if (progress > 1.0 / 2) {
                    midpoint = true;
                    mCallback.onProgress(1.0 / 2);
                }
            } else if (!thirdQuartile) {
                if (progress > 3.0 / 4) {
                    thirdQuartile = true;
                    mCallback.onProgress(3.0 / 4);
                }
            }
        }
    }

    private void handleFileFullDownloaded() {
        mIsFileFullyDownloaded = true;
        new Handler(Looper.getMainLooper()).post(() -> {
            if (mCallback != null) {
                mCallback.onFileLoaded(mLoadingFile.getAbsolutePath());
            }
        });
    }

    public void stop() {
        Logger.e(LOG_TAG, "stop()");
        mStop = true;
        Runnable pendingRetry = mPendingRetry.get();
        if (pendingRetry != null) {
            RetryScheduler.cancel(pendingRetry);
        }
        if (mConnection != null) {
            ExecutorHelper.getExecutor().submit(() -> {
                Logger.e(LOG_TAG, "disconnect()");
                mConnection.disconnect();
            });
        }

        //delete file if it not fully downloaded
        if (!mIsFileFullyDownloaded && mLoadingFile != null && mLoadingFile.exists()) {
            Logger.e(LOG_TAG, "remove bad file");
            mLoadingFile.delete();
        }
    }

    private void handleEmulator() {
        if (Utils.isEmulator()) {
            Logger.e(LOG_TAG, "running on emulator");
            useMobileNetworkForCaching = true;
        }
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                Logger.e(LOG_TAG, "Can't close stream");
            }
        }
    }

    public static void setUseMobileNetworkForCaching(boolean useMobile) {
        FileLoader.useMobileNetworkForCaching = useMobile;
    }
}
