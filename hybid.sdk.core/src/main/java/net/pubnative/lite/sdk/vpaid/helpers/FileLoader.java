package net.pubnative.lite.sdk.vpaid.helpers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.PlayerInfo;
import net.pubnative.lite.sdk.vpaid.enums.ConnectionType;
import net.pubnative.lite.sdk.vpaid.enums.VastError;
import net.pubnative.lite.sdk.vpaid.utils.FileUtils;
import net.pubnative.lite.sdk.vpaid.utils.Utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

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

        FileHeaders(String eTag, int fileLength) {
            this.eTag = eTag;
            this.fileLength = fileLength;
        }
    }

    private static final String LOG_TAG = FileLoader.class.getSimpleName();
    private static final int CONNECT_TIMEOUT = 10_000;
    private static final int READ_TIMEOUT = 10_000;

    private static boolean useMobileNetworkForCaching;

    private final Context mContext;
    private final File mLoadingFile;
    private final Callback mCallback;
    private final String mRemoteFileUrl;

    private volatile HttpURLConnection mConnection;
    private volatile boolean mIsFileFullyDownloaded;
    private volatile boolean mStop;

    // progress flags
    private boolean firstQuartile;
    private boolean midpoint;
    private boolean thirdQuartile;

    public FileLoader(String fileUrl, Context context, Callback callback) {
        mCallback = callback;
        mContext = context;
        mRemoteFileUrl = fileUrl;
        String shortFileName = FileUtils.obtainHashName(mRemoteFileUrl);
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
        ExecutorHelper.getExecutor().submit(() -> load());
    }

    private void load() {
        try {
            if (mStop) {
                return;
            }
            FileHeaders headers = obtainHeaders(mRemoteFileUrl);
            if (headers == null) {
                if (mCallback != null) {
                    mCallback.onError(new PlayerInfo("Error during loading file"));
                }
                return;
            }

            Logger.d(LOG_TAG, "File length: " + headers.fileLength);
            int attemptsCount = 0;
            int downloadedBytes = 0;
            long time = System.currentTimeMillis();
            while (!mStop && downloadedBytes < headers.fileLength) {
                downloadedBytes = appendFile(mLoadingFile, mRemoteFileUrl, downloadedBytes, headers);
                attemptsCount++;
            }
            time = System.currentTimeMillis() - time;
            Logger.d(LOG_TAG, "Load time: " + time / 1000.0);
            Logger.d(LOG_TAG, "AttemptsCount: " + attemptsCount);

            if (downloadedBytes == headers.fileLength) {
                handleFileFullDownloaded();
            } else {
                if (mCallback != null) {
                    mCallback.onError(new PlayerInfo("Error during file loading, attemptsCount: " + attemptsCount));
                }
            }
        } catch (Exception e) {
            Logger.e(LOG_TAG, "Unexpected FileLoader error: " + e.getMessage());
        }
    }

    /**
     * @return total progress
     */
    private int appendFile(File file, String url, int downloadedBytes, FileHeaders headers) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            mConnection = obtainGetConnection(url, downloadedBytes, headers);
            inputStream = mConnection.getInputStream();
            outputStream = new FileOutputStream(file, true);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
                downloadedBytes += length;
                double progress = downloadedBytes / (double) headers.fileLength;
                handelProgress(progress);
            }
        } catch (Exception e) {
            Logger.e(LOG_TAG, "appendFile interrupted: " + e.getMessage());
        } finally {
            closeStream(inputStream);
            closeStream(outputStream);
        }
        return downloadedBytes;
    }

    private FileHeaders obtainHeaders(String remoteFileUrl) {
        try {
            URL url = new URL(remoteFileUrl);
            mConnection = (HttpURLConnection) url.openConnection();
            mConnection.setRequestMethod("HEAD");
            if (mConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String eTag = mConnection.getHeaderField("ETag");
                int fileLength = mConnection.getContentLength();
                return new FileHeaders(eTag, fileLength);
            } else if (mConnection.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN ||
                    mConnection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL ||
                    mConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
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
        connection.setRequestProperty("Range", "bytes=" + downloadedBytes + "-" + headers.fileLength);
        connection.setRequestProperty("If-Range", headers.eTag);
        return connection;
    }

    private void handelProgress(double progress) {
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
