package net.pubnative.lite.sdk.vpaid.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import net.pubnative.lite.sdk.utils.Logger;

import java.io.ByteArrayOutputStream;

public class ImageUtils {
    private static final String TAG = ImageUtils.class.getSimpleName();

    public static void setScaledImage(ImageView imageView, final String filePath) {
        final ImageView view = imageView;
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                int imageViewHeight = view.getMeasuredHeight();
                int imageViewWidth = view.getMeasuredWidth();
                Bitmap decoded = decodeSampledBitmap(filePath, imageViewWidth, imageViewHeight);
                if (decoded != null) {
                    view.setImageBitmap(decoded);
                }
                return true;
            }
        });
    }

    public static void setScaledImage(ImageView imageView, final Bitmap image) {
        final ImageView view = imageView;
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                int imageViewHeight = view.getMeasuredHeight();
                int imageViewWidth = view.getMeasuredWidth();
                Bitmap decoded = decodeSampledBitmap(image, imageViewWidth, imageViewHeight);
                if (decoded != null) {
                    view.setImageBitmap(decoded);
                }
                return true;
            }
        });
    }

    private static Bitmap decodeSampledBitmap(String filePath, int reqWidth, int reqHeight) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(filePath, options);
        } catch (RuntimeException exception) {
            Logger.e(TAG, exception.getMessage());
            return null;
        }
    }

    private static Bitmap decodeSampledBitmap(Bitmap image, int reqWidth, int reqHeight) {
        try {
            int inSampleSize = calculateInSampleSize(image.getWidth(), image.getHeight(), reqWidth, reqHeight);
            int dstWidth = (int) (image.getWidth() / inSampleSize);
            int dstHeight = (int) (image.getHeight() / inSampleSize);
            return Bitmap.createScaledBitmap(image, dstWidth, dstHeight, false);
        } catch (RuntimeException exception) {
            Logger.e(TAG, exception.getMessage());
            return null;
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        return calculateInSampleSize(width, height, reqWidth, reqHeight);
    }

    private static int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {

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
