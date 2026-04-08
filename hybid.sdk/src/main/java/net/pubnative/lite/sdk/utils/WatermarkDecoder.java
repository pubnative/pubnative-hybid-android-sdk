// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Base64;

import java.util.Arrays;

public class WatermarkDecoder {

    private static final byte[] PNG_SIGNATURE = new byte[] {
            (byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47,
            (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A
    };
    private WatermarkDecoder() {
    }

    /**
     * Decodes a base64 watermark string into a Drawable.
     *
     * @param context The context to create the drawable
     * @param watermarkString The base64 encoded watermark string
     * @return A BitmapDrawable if decoding is successful, null otherwise
     */
    public static Drawable decodeWatermark(Context context, String watermarkString) {
        if (TextUtils.isEmpty(watermarkString)) {
            return null;
        }

        try {
            // Decode base64 string to bitmap
            byte[] decodedBytes = Base64.decode(watermarkString, Base64.DEFAULT);
            // Check for PNG header signature
            if (decodedBytes.length < PNG_SIGNATURE.length || !Arrays.equals(Arrays.copyOf(decodedBytes, PNG_SIGNATURE.length), PNG_SIGNATURE)) {
                return null;
            }
            Bitmap watermarkBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            if (watermarkBitmap != null) {
                // Convert bitmap to drawable
                BitmapDrawable repeatDrawable = new BitmapDrawable(context.getResources(), watermarkBitmap);
                repeatDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                return repeatDrawable;
            }
        } catch (Exception e) {
            Logger.d("WatermarkDecoder", "Exception in watermark decoding: " + e.getMessage());
        }
        return null;
    }
}
