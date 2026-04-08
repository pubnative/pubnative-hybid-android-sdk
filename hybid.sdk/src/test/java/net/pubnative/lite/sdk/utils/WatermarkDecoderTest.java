package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;


@RunWith(RobolectricTestRunner.class)
public class WatermarkDecoderTest {
    private Context mContext;

    @Before
    public void setup() {
        mContext = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void decodeWatermark_withNullString_returnsNull() {
        Drawable result = WatermarkDecoder.decodeWatermark(mContext, null);
        assertNull(result);
    }

    @Test
    public void decodeWatermark_withEmptyString_returnsNull() {
        Drawable result = WatermarkDecoder.decodeWatermark(mContext, "");
        assertNull(result);
    }

    @Test
    public void decodeWatermark_withInvalidBase64_returnsNull() {
        Drawable result = WatermarkDecoder.decodeWatermark(mContext, "this is not base64");
        assertNull(result);
    }

    @Test
    public void decodeWatermark_withValidBase64ButNotImage_returnsNull() {
        String nonImageBase64 = "VmVydmU="; // "Verve" in base64
        Drawable result = WatermarkDecoder.decodeWatermark(mContext, nonImageBase64);
        assertNull(result);
    }

    @Test
    public void decodeWatermark_withValidImage_returnsDrawable() {
        //Verve logo in base64
        String validImageBase64 = "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";
        Drawable result = WatermarkDecoder.decodeWatermark(mContext, validImageBase64);
        assertNotNull(result);
    }

    @Test
    public void decodeWatermark_withBase64CausingBitmapException_catchesGeneralException() {
        try (MockedStatic<Logger> logMock = mockStatic(Logger.class)) {
            // Use valid PNG base64 but null context to trigger NullPointerException in BitmapDrawable constructor
            String validPngBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
            Drawable result = WatermarkDecoder.decodeWatermark(null, validPngBase64);

            assertNull("Should return null when general Exception is caught during bitmap processing", result);
            logMock.verify(() -> Logger.d(eq("WatermarkDecoder"), contains("Exception in watermark decoding:")));
        }
    }

    @Test
    public void decodeWatermark_withInvalidPngSignature_returnsNull() {
        String invalidPngBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAUAABCCAYAAACNbyblAAAAFElEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        Drawable result = WatermarkDecoder.decodeWatermark(mContext, invalidPngBase64);
        assertNull(result);
    }

}
