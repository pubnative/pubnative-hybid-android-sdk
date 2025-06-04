// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import net.pubnative.lite.sdk.utils.svgparser.SVG;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.RejectedExecutionException;

public class PNBitmapDownloader {

    private static final String TAG = PNBitmapDownloader.class.getSimpleName();

    private DownloadListener mDownloadListener;
    private String mURL;
    private Handler mHandler;
    private int mHeight;
    private int mWidth;

    private final Runnable downloadTask = new Runnable() {
        HttpURLConnection connection = null;

        @Override
        public void run() {
            try {
                URL url = new URL(mURL);
                connection = (HttpURLConnection) url.openConnection();

                // Get InputStream for get a real size of bitmap
                InputStream checkSizeInputStream = url.openConnection().getInputStream();
                BitmapFactory.decodeStream(checkSizeInputStream, new Rect(), getBitmapOptionsDecodingBounds(true));
                checkSizeInputStream.close();

                // Get new InputStream second time,
                // because it can't be reused from previous time
                InputStream inputStream = url.openConnection().getInputStream();
                Bitmap bitmap;
                if(url.openConnection().getContentType().equals("image/svg+xml")){
                    SVG svg = SVG.getFromInputStream(inputStream);
                    Picture picture = svg.renderToPicture();
                    bitmap = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawPicture(picture);
                } else {
                    bitmap = BitmapFactory.decodeStream(inputStream, null, getBitmapOptionsDecodingBounds(false));
                }
                inputStream.close();
                PNBitmapLruCache.addBitmapToMemoryCache(mURL, bitmap);
                invokeLoad(bitmap);
            } catch (RuntimeException e) {
                invokeFail(e);
            } catch (Error error) {
                invokeFail(new Exception(error.toString()));
            } catch (Exception e) {
                invokeFail(e);
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
                Bitmap bitmap = BitmapFactory.decodeFile(uri.getEncodedPath(), getBitmapOptionsDecodingBounds(false));
                PNBitmapLruCache.addBitmapToMemoryCache(mURL, bitmap);
                invokeLoad(bitmap);
            } catch (RuntimeException e) {
                invokeFail(e);
            } catch (Error error) {
                invokeFail(new Exception(error.toString()));
            } catch (Exception e) {
                invokeFail(e);
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
                invokeFail(new Exception("Image URL is empty"));
            } else if (URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)) {
                downloadImage();
            } else if (URLUtil.isFileUrl(url)) {
                loadCachedImage();
            } else {
                invokeFail(new Exception("Wrong file URL!"));
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
            invokeFail(e);
        }
    }

    private void loadCachedImage() {
        BitmapDownloaderExecutor.getExecutor().submit(loadFromFileSystemTask);
    }

    private BitmapFactory.Options getBitmapOptionsDecodingBounds(boolean decodeBounds) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        if (mWidth > 0 && mHeight > 0 && !decodeBounds) {
            options.inSampleSize = calculateInSampleSize(options, mWidth, mHeight);
        }
        options.inJustDecodeBounds = decodeBounds;
        return options;
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
