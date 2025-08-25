// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.pubnative.lite.sdk.HyBid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import androidx.annotation.VisibleForTesting;


public class AndroidBitmapDecoder implements BitmapDecoder {

    @Override
    public Bitmap decodeFile(File file, int reqWidth, int reqHeight) {
        try {
            return decode(new FileInputStream(file), new FileInputStream(file), reqWidth, reqHeight);
        } catch (FileNotFoundException ignored) {
            HyBid.reportException(ignored);
        }
        return null;
    }

    @VisibleForTesting
    Bitmap decode(InputStream boundsStream, InputStream fullStream, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(boundsStream, null, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(fullStream, null, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (reqHeight <= 0 || reqWidth <= 0) {
            return inSampleSize;
        }

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}