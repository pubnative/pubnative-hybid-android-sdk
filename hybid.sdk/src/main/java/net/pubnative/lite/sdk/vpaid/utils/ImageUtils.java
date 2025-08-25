// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.VisibleForTesting;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.utils.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageUtils {
    private static final String TAG = ImageUtils.class.getSimpleName();

    public static void setScaledImage(ImageView imageView, final String filePath) {
        setScaledImage(imageView, filePath, new AndroidBitmapDecoder());
    }

    public static void setScaledImage(ImageView imageView, final String filePath, final BitmapDecoder decoder) {
        final ImageView view = imageView;
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        if (!viewTreeObserver.isAlive()) {
            return;
        }
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                int imageViewHeight = view.getMeasuredHeight();
                int imageViewWidth = view.getMeasuredWidth();

                Bitmap decoded = decoder.decodeFile(new File(filePath), imageViewWidth, imageViewHeight);

                if (decoded != null) {
                    view.setImageBitmap(decoded);
                }
                return true;
            }
        });
    }

    @VisibleForTesting
    static Bitmap decodeSampledBitmap(String filePath, int reqWidth, int reqHeight) {
        Bitmap bitmap;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(filePath, options);
            return bitmap;
        } catch (OutOfMemoryError | RuntimeException ex) {
            HyBid.reportException(ex);
            bitmap = new AndroidBitmapDecoder().decodeFile(new File(filePath), reqWidth, reqHeight);
            return bitmap;
        }
    }

    @VisibleForTesting
    static Bitmap decodeSampledBitmap(Bitmap image, int reqWidth, int reqHeight) {
        try {
            int inSampleSize = calculateInSampleSize(image.getWidth(), image.getHeight(), reqWidth, reqHeight);
            int dstWidth = (int) (image.getWidth() / inSampleSize);
            int dstHeight = (int) (image.getHeight() / inSampleSize);
            return Bitmap.createScaledBitmap(image, dstWidth, dstHeight, false);
        } catch (RuntimeException exception) {
            HyBid.reportException(exception);
            Logger.e(TAG, exception.getMessage());
            return null;
        }
    }

    @VisibleForTesting
    static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        return calculateInSampleSize(width, height, reqWidth, reqHeight);
    }

    @VisibleForTesting
    static int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
