package net.pubnative.lite.sdk.vpaid.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import net.pubnative.lite.sdk.HyBid;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ImageUtilsHelpersTest {

    // --- Tests for calculateInSampleSize ---

    @Test
    public void calculateInSampleSize_whenImageIsSmaller_returnsOne() {
        int sampleSize = ImageUtils.calculateInSampleSize(100, 100, 200, 200);
        assertEquals(1, sampleSize);
    }

    @Test
    public void calculateInSampleSize_whenImageIsLarger_returnsPowerOfTwo() {
        int sampleSize = ImageUtils.calculateInSampleSize(800, 600, 200, 150);
        assertEquals(2, sampleSize);
    }

    @Test
    public void calculateInSampleSize_whenImageIsMuchLarger_returnsCorrectPowerOfTwo() {
        int sampleSize = ImageUtils.calculateInSampleSize(4000, 3000, 200, 150);
        assertEquals(16, sampleSize);
    }

    // --- Tests for decodeSampledBitmap ---

    @Test
    public void decodeSampledBitmap_whenDecodeSucceeds_returnsBitmap() {
        Bitmap mockBitmap = mock(Bitmap.class);
        // Use try-with-resources to mock the static BitmapFactory
        try (MockedStatic<BitmapFactory> mockedFactory = Mockito.mockStatic(BitmapFactory.class)) {
            mockedFactory.when(() -> BitmapFactory.decodeFile(anyString(), any(BitmapFactory.Options.class)))
                    .thenReturn(mockBitmap);

            Bitmap result = ImageUtils.decodeSampledBitmap("/fake/path", 100, 100);
            assertNotNull(result);
        }
    }

    @Test
    public void decodeSampledBitmap_whenDecodeFails_reportsExceptionAndFallsBack() {
        RuntimeException exception = new RuntimeException("Bitmap decode failed");

        try (MockedStatic<BitmapFactory> mockedFactory = Mockito.mockStatic(BitmapFactory.class);
             MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class);
             MockedConstruction<AndroidBitmapDecoder> mockedDecoder = mockConstruction(AndroidBitmapDecoder.class)) {

            mockedFactory.when(() -> BitmapFactory.decodeFile(anyString(), any(BitmapFactory.Options.class)))
                    .thenThrow(exception);

            ImageUtils.decodeSampledBitmap("/fake/path", 100, 100);

            ArgumentCaptor<Throwable> exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);

            // 1. Verify the method was called and capture the argument it was called with.
            mockedHyBid.verify(() -> HyBid.reportException(exceptionCaptor.capture()));

            // 2. Perform standard assertions on the captured argument.
            Throwable capturedException = exceptionCaptor.getValue();
            assertNotNull(capturedException);
            assertTrue(capturedException instanceof RuntimeException);
            assertEquals("Bitmap decode failed", capturedException.getMessage());
        }
    }
}
