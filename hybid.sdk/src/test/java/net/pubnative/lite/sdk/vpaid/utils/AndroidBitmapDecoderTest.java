package net.pubnative.lite.sdk.vpaid.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

import net.pubnative.lite.sdk.HyBid;

@RunWith(RobolectricTestRunner.class)
public class AndroidBitmapDecoderTest {

    private AndroidBitmapDecoder decoder;
    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = openMocks(this);
        decoder = new AndroidBitmapDecoder();
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    // --- Tests for decodeFile() ---

    @Test
    public void decodeFile_whenFileDoesNotExist_returnsNullAndReportsException() {
        try (MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class)) {
            File nonExistentFile = new File("/path/to/non_existent_file.jpg");

            Bitmap result = decoder.decodeFile(nonExistentFile, 100, 100);

            assertNull(result);
            mockedHyBid.verify(() -> HyBid.reportException(any(FileNotFoundException.class)));
        }
    }

    // --- Tests for decode() ---

    @Test
    public void decode_withValidInputs_calculatesSampleSizeAndDecodesBitmap() {
        String testData = "test_image_data";
        InputStream boundsStream = new ByteArrayInputStream(testData.getBytes());
        InputStream fullStream = new ByteArrayInputStream(testData.getBytes());

        try (MockedStatic<BitmapFactory> mockedFactory = mockStatic(BitmapFactory.class)) {
            mockedFactory.when(() -> BitmapFactory.decodeStream(eq(boundsStream), isNull(), any(BitmapFactory.Options.class)))
                    .thenAnswer(invocation -> {
                        BitmapFactory.Options options = invocation.getArgument(2);
                        options.outWidth = 2048; // Simulate a large image
                        options.outHeight = 1536;
                        return null;
                    });

            Bitmap mockBitmap = mock(Bitmap.class);
            mockedFactory.when(() -> BitmapFactory.decodeStream(eq(fullStream), isNull(), any(BitmapFactory.Options.class)))
                    .thenReturn(mockBitmap);

            Bitmap result = decoder.decode(boundsStream, fullStream, 400, 300);

            assertEquals(mockBitmap, result);

            // Verify the final sample size was calculated correctly for the final decode call
            ArgumentCaptor<BitmapFactory.Options> optionsCaptor = ArgumentCaptor.forClass(BitmapFactory.Options.class);
            mockedFactory.verify(() -> BitmapFactory.decodeStream(eq(fullStream), isNull(), optionsCaptor.capture()));
            assertEquals(4, optionsCaptor.getValue().inSampleSize);
        }
    }

    // --- Tests for calculateInSampleSize() via Reflection ---

    @Test
    public void calculateInSampleSize_whenImageIsSmaller_returnsOne() throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = 100;
        options.outHeight = 100;

        int sampleSize = invokeCalculateInSampleSize(options, 200, 200);
        assertEquals(1, sampleSize);
    }

    @Test
    public void calculateInSampleSize_whenImageIsLarger_returnsCorrectPowerOfTwo() throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = 4000;
        options.outHeight = 3000;

        int sampleSize = invokeCalculateInSampleSize(options, 200, 150);
        assertEquals(16, sampleSize); // Should downsample by 8x to meet the target
    }

    @Test
    public void calculateInSampleSize_whenTargetIsZeroOrNegative_returnsOne() throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = 1000;
        options.outHeight = 1000;

        // Test the guard clause for invalid dimensions
        int sampleSizeZero = invokeCalculateInSampleSize(options, 0, 100);
        assertEquals(1, sampleSizeZero);

        int sampleSizeNegative = invokeCalculateInSampleSize(options, 100, -50);
        assertEquals(1, sampleSizeNegative);
    }

    /**
     * Helper to test the private static method `calculateInSampleSize` using reflection.
     */
    private int invokeCalculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) throws Exception {
        Method method = AndroidBitmapDecoder.class.getDeclaredMethod("calculateInSampleSize", BitmapFactory.Options.class, int.class, int.class);
        method.setAccessible(true);

        Object result = method.invoke(decoder, options, reqWidth, reqHeight);
        assertNotNull("The reflection call should not return null", result);
        return (int) result;
    }
}