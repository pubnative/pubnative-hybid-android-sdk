// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.utils.svgparser.SVG;
import net.pubnative.lite.sdk.utils.svgparser.SVGParseException;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.RejectedExecutionException;

public class PNBitmapDownloader {

    private static final String TAG = PNBitmapDownloader.class.getSimpleName();
    private static final int MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;
    private static final int BUFFER_SIZE = 8 * 1024;
    private static final String SVG_CONTENT_TYPE = "image/svg+xml";

    private DownloadListener mDownloadListener;
    private String mURL;
    private Handler mHandler;
    private int mHeight;
    private int mWidth;

    private final Runnable downloadTask = new Runnable() {
        @Override
        public void run() {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(mURL).openConnection();

                int contentLength = connection.getContentLength();
                ByteBuffer imageData = null;
                if (contentLength <= MAX_IMAGE_SIZE_BYTES) {
                    try (InputStream inputStream = connection.getInputStream()) {
                        imageData = readImageData(inputStream, contentLength);
                    }
                }
                if (imageData == null) {
                    invokeFail(new HyBidError(HyBidErrorCode.RESOURCE_EXHAUSTION, "Image exceeds the maximum allowed size"));
                    return;
                }

                Bitmap bitmap;
                if (isSvg(connection.getContentType())) {
                    bitmap = renderSvg(imageData);
                } else {
                    bitmap = decodeImageData(imageData);
                }
                invokeLoad(bitmap);
            } catch (RuntimeException e) {
                invokeFail(new HyBidError(HyBidErrorCode.INTERNAL_ERROR, e));
            } catch (OutOfMemoryError error) {
                invokeFail(new HyBidError(HyBidErrorCode.RESOURCE_EXHAUSTION, error));
            } catch (Exception e) {
                invokeFail(new HyBidError(HyBidErrorCode.INTERNAL_ERROR, e));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    };

    private final Runnable loadFromFileSystemTask = new Runnable() {
        @Override
        public void run() {
            try {
                Uri uri = Uri.parse(mURL);
                Bitmap bitmap = decodeImageFile(uri.getEncodedPath());
                invokeLoad(bitmap);
            } catch (RuntimeException e) {
                invokeFail(new HyBidError(HyBidErrorCode.INTERNAL_ERROR, e));
            } catch (OutOfMemoryError error) {
                invokeFail(new HyBidError(HyBidErrorCode.RESOURCE_EXHAUSTION, error));
            } catch (Exception e) {
                invokeFail(new HyBidError(HyBidErrorCode.INTERNAL_ERROR, e));
            }
        }
    };

    /**
     * Interface for callbacks related to image downloader
     */
    public interface DownloadListener {
        /**
         * Called whenever image is loaded either from fetchAssets or from networkName
         *
         * @param bitmap Image
         */
        void onDownloadFinish(String url, Bitmap bitmap);

        /**
         * Called whenever image loading failed
         *
         * @param url Url that failed
         */
        void onDownloadFailed(String url, Exception exception);
    }

    public synchronized void download(String url, DownloadListener listener) {
        download(url, 0, 0, listener);
    }

    public synchronized void download(String url, int width, int height, DownloadListener listener) {

        mHandler = new Handler(Looper.getMainLooper());

        if (listener == null) {
            Log.w(TAG, "download won't start since there is no assigned listener to It");
        } else {
            mDownloadListener = listener;
            mURL = url;
            mWidth = width;
            mHeight = height;
            if (TextUtils.isEmpty(url)) {
                invokeFail(new HyBidError(HyBidErrorCode.INVALID_URL, "Image URL is empty"));
            } else if (URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)) {
                downloadImage();
            } else if (URLUtil.isFileUrl(url)) {
                loadCachedImage();
            } else {
                invokeFail(new HyBidError(HyBidErrorCode.INVALID_URL, "Wrong file URL!"));
            }
        }
    }


    //==============================================================================================
    // Private methods
    //==============================================================================================
    private synchronized void downloadImage() {
        try {
            BitmapDownloaderExecutor.getExecutor().submit(downloadTask);
        } catch (RejectedExecutionException e) {
            Logger.e(TAG, "Task submission rejected: " + e.getMessage());
            invokeFail(new HyBidError(HyBidErrorCode.RESOURCE_EXHAUSTION, e));
        } catch (OutOfMemoryError error) {
            Logger.e(TAG, "Error submitting download task: " + error.getMessage());
            invokeFail(new HyBidError(HyBidErrorCode.RESOURCE_EXHAUSTION, error));
        }
    }

    private void loadCachedImage() {
        try {
            BitmapDownloaderExecutor.getExecutor().submit(loadFromFileSystemTask);
        } catch (RejectedExecutionException e) {
            Logger.e(TAG, "Task submission rejected: " + e.getMessage());
            invokeFail(new HyBidError(HyBidErrorCode.RESOURCE_EXHAUSTION, e));
        } catch (OutOfMemoryError error) {
            Logger.e(TAG, "Error submitting file load task: " + error.getMessage());
            invokeFail(new HyBidError(HyBidErrorCode.RESOURCE_EXHAUSTION, error));
        }
    }

    private static ByteBuffer readImageData(InputStream inputStream, int contentLength) throws IOException {
        if (contentLength > 0) {
            byte[] imageData = new byte[contentLength];
            int offset = 0;
            while (offset < contentLength) {
                int bytesRead = inputStream.read(imageData, offset, contentLength - offset);
                if (bytesRead == -1) {
                    throw new EOFException("Image body is shorter than its Content-Length");
                }
                offset += bytesRead;
            }
            return ByteBuffer.wrap(imageData);
        }

        byte[] imageData = new byte[BUFFER_SIZE];
        int length = 0;
        int bytesRead;
        while ((bytesRead = inputStream.read(imageData, length, imageData.length - length)) != -1) {
            length += bytesRead;
            if (length > MAX_IMAGE_SIZE_BYTES) {
                return null;
            }
            if (length == imageData.length) {
                imageData = Arrays.copyOf(imageData, Math.min(imageData.length * 2, MAX_IMAGE_SIZE_BYTES + 1));
            }
        }
        return ByteBuffer.wrap(imageData, 0, length);
    }

    private static boolean isSvg(String contentType) {
        return SVG_CONTENT_TYPE.equals(contentType);
    }

    private static Bitmap renderSvg(ByteBuffer imageData) throws SVGParseException {
        SVG svg = SVG.getFromInputStream(new ByteArrayInputStream(imageData.array(), 0, imageData.limit()));
        Picture picture = svg.renderToPicture();
        Bitmap bitmap = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawPicture(picture);
        return bitmap;
    }

    private Bitmap decodeImageData(ByteBuffer imageData) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(imageData.array(), 0, imageData.limit(), options);
        applySampleSize(options);
        return BitmapFactory.decodeByteArray(imageData.array(), 0, imageData.limit(), options);
    }

    private Bitmap decodeImageFile(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        applySampleSize(options);
        return BitmapFactory.decodeFile(path, options);
    }

    private void applySampleSize(BitmapFactory.Options options) {
        if (mWidth > 0 && mHeight > 0) {
            options.inSampleSize = calculateInSampleSize(options, mWidth, mHeight);
        }
        options.inJustDecodeBounds = false;
    }

    /**
     * This method calculates the inSampleSize which create a smaller image than original.
     * It's needed, because when we try to use decodeStream for the big image BitmapFactory
     * allocate memory not only for himself but also and for downloaded image and some processed data.
     *
     * @param options options for {@link BitmapFactory.Options BitmapFactory.Options}
     * @return int {@link BitmapFactory.Options#inSampleSize inSampleSize} which decoder use to subsample the original image, returning a smaller image to save memory
     */
    protected int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int result = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / result) >= reqHeight
                    && (halfWidth / result) >= reqWidth) {
                result *= 2;
            }
        }

        return result;
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeLoad(final Bitmap bitmap) {
        mHandler.post(() -> {
            final DownloadListener listener = mDownloadListener;
            mDownloadListener = null;
            if (listener != null) {
                listener.onDownloadFinish(mURL, bitmap);
            }
        });
    }

    protected void invokeFail(final Exception exception) {
        mHandler.post(() -> {
            final DownloadListener listener = mDownloadListener;
            mDownloadListener = null;
            if (listener != null) {
                listener.onDownloadFailed(mURL, exception);
            }
        });
    }
}
