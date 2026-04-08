// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class WatermarkHelperTest {

    private Context context;
    private WatermarkHelper watermarkHelper;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        watermarkHelper = new WatermarkHelper();
    }

    // Tests for createWatermarkView(Context, String)

    @Test
    public void createWatermarkView_withValidData_returnsImageView() {
        String validWatermarkData = "valid_base64_data";
        Drawable mockDrawable = mock(Drawable.class);

        try (MockedStatic<WatermarkDecoder> mockedDecoder = mockStatic(WatermarkDecoder.class)) {
            mockedDecoder.when(() -> WatermarkDecoder.decodeWatermark(eq(context), eq(validWatermarkData)))
                    .thenReturn(mockDrawable);

            View result = WatermarkHelper.createWatermarkView(context, validWatermarkData);

            assertNotNull("Watermark view should not be null", result);
            assertTrue("Result should be an ImageView", result instanceof ImageView);

            ImageView imageView = (ImageView) result;
            assertFalse("ImageView should not be clickable", imageView.isClickable());
            assertFalse("ImageView should not be focusable", imageView.isFocusable());
            assertEquals("Background should be set", mockDrawable, imageView.getBackground());

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
            assertNotNull("Layout params should be set", layoutParams);
            assertEquals("Width should be MATCH_PARENT", ViewGroup.LayoutParams.MATCH_PARENT, layoutParams.width);
            assertEquals("Height should be MATCH_PARENT", ViewGroup.LayoutParams.MATCH_PARENT, layoutParams.height);
        }
    }

    @Test
    public void createWatermarkView_withNullData_returnsNull() {
        View result = WatermarkHelper.createWatermarkView(context, (String) null);
        assertNull("Should return null for null data", result);
    }

    @Test
    public void createWatermarkView_withEmptyData_returnsNull() {
        View result = WatermarkHelper.createWatermarkView(context, "");
        assertNull("Should return null for empty data", result);
    }

    @Test
    public void createWatermarkView_whenDecodingFails_returnsNull() {
        String invalidData = "invalid_data";

        try (MockedStatic<WatermarkDecoder> mockedDecoder = mockStatic(WatermarkDecoder.class)) {
            mockedDecoder.when(() -> WatermarkDecoder.decodeWatermark(eq(context), eq(invalidData)))
                    .thenReturn(null);

            View result = WatermarkHelper.createWatermarkView(context, invalidData);

            assertNull("Should return null when decoding fails", result);
        }
    }

    // Tests for createWatermarkView(Context, Drawable)

    @Test
    public void createWatermarkViewFromDrawable_withValidDrawable_returnsImageView() {
        Drawable mockDrawable = mock(Drawable.class);

        ImageView result = WatermarkHelper.createWatermarkView(context, mockDrawable);

        assertNotNull("Watermark view should not be null", result);
        assertFalse("ImageView should not be clickable", result.isClickable());
        assertFalse("ImageView should not be focusable", result.isFocusable());
        assertEquals("Background should be set", mockDrawable, result.getBackground());

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) result.getLayoutParams();
        assertNotNull("Layout params should be set", layoutParams);
        assertEquals("Width should be MATCH_PARENT", ViewGroup.LayoutParams.MATCH_PARENT, layoutParams.width);
        assertEquals("Height should be MATCH_PARENT", ViewGroup.LayoutParams.MATCH_PARENT, layoutParams.height);
        assertEquals("Gravity should be TOP|START", Gravity.TOP | Gravity.START, layoutParams.gravity);
    }

    @Test
    public void createWatermarkViewFromDrawable_withNullDrawable_returnsNull() {
        ImageView result = WatermarkHelper.createWatermarkView(context, (Drawable) null);
        assertNull("Should return null for null drawable", result);
    }

    // Tests for decodeWatermark

    @Test
    public void decodeWatermark_withValidData_returnsDrawable() {
        String validData = "valid_base64";
        Drawable mockDrawable = mock(Drawable.class);

        try (MockedStatic<WatermarkDecoder> mockedDecoder = mockStatic(WatermarkDecoder.class)) {
            mockedDecoder.when(() -> WatermarkDecoder.decodeWatermark(eq(context), eq(validData)))
                    .thenReturn(mockDrawable);

            Drawable result = WatermarkHelper.decodeWatermark(context, validData);

            assertNotNull("Decoded drawable should not be null", result);
            assertEquals("Should return the decoded drawable", mockDrawable, result);
        }
    }

    @Test
    public void decodeWatermark_withInvalidData_returnsNull() {
        String invalidData = "invalid";

        try (MockedStatic<WatermarkDecoder> mockedDecoder = mockStatic(WatermarkDecoder.class)) {
            mockedDecoder.when(() -> WatermarkDecoder.decodeWatermark(eq(context), eq(invalidData)))
                    .thenReturn(null);

            Drawable result = WatermarkHelper.decodeWatermark(context, invalidData);

            assertNull("Should return null for invalid data", result);
        }
    }

    // Tests for removeWatermarkView

    @Test
    public void removeWatermarkView_withValidView_removesFromParentAndReturnsTrue() {
        ImageView watermarkView = new ImageView(context);
        FrameLayout parent = new FrameLayout(context);
        parent.addView(watermarkView);
        Drawable mockDrawable = mock(Drawable.class);
        watermarkView.setImageDrawable(mockDrawable);

        assertTrue("Parent should contain watermark view", parent.getChildCount() > 0);

        boolean result = WatermarkHelper.removeWatermarkView(watermarkView);

        assertTrue("Should return true when watermark is removed", result);
        assertEquals("Parent should have no children", 0, parent.getChildCount());
        assertNull("ImageDrawable should be null", watermarkView.getDrawable());
    }

    @Test
    public void removeWatermarkView_withViewWithoutParent_clearsDrawableAndReturnsTrue() {
        ImageView watermarkView = new ImageView(context);
        Drawable mockDrawable = mock(Drawable.class);
        watermarkView.setImageDrawable(mockDrawable);

        boolean result = WatermarkHelper.removeWatermarkView(watermarkView);

        assertTrue("Should return true", result);
        assertNull("ImageDrawable should be null", watermarkView.getDrawable());
    }

    @Test
    public void removeWatermarkView_withNullView_returnsFalse() {
        boolean result = WatermarkHelper.removeWatermarkView(null);
        assertFalse("Should return false for null view", result);
    }

    // Tests for isWatermarkRegistered

    @Test
    public void isWatermarkRegistered_initialState_returnsFalse() {
        assertFalse("Initial state should be not registered", watermarkHelper.isWatermarkRegistered());
    }

    @Test
    public void isWatermarkRegistered_afterSetRegistered_returnsTrue() {
        watermarkHelper.setWatermarkRegistered();
        assertTrue("Should return true after setting registered", watermarkHelper.isWatermarkRegistered());
    }

    // Tests for setWatermarkRegistered

    @Test
    public void setWatermarkRegistered_changesStateToRegistered() {
        assertFalse("Should start as not registered", watermarkHelper.isWatermarkRegistered());

        watermarkHelper.setWatermarkRegistered();

        assertTrue("Should be registered after setting", watermarkHelper.isWatermarkRegistered());
    }

    // Tests for reset

    @Test
    public void reset_afterSetRegistered_resetsStateToNotRegistered() {
        watermarkHelper.setWatermarkRegistered();
        assertTrue("Should be registered", watermarkHelper.isWatermarkRegistered());

        watermarkHelper.reset();

        assertFalse("Should be not registered after reset", watermarkHelper.isWatermarkRegistered());
    }

    @Test
    public void reset_whenNotRegistered_remainsNotRegistered() {
        assertFalse("Should start as not registered", watermarkHelper.isWatermarkRegistered());

        watermarkHelper.reset();

        assertFalse("Should still be not registered", watermarkHelper.isWatermarkRegistered());
    }

    // Integration tests

    @Test
    public void integrationTest_multipleInstancesHaveIndependentState() {
        WatermarkHelper helper1 = new WatermarkHelper();
        WatermarkHelper helper2 = new WatermarkHelper();

        helper1.setWatermarkRegistered();

        assertTrue("Helper1 should be registered", helper1.isWatermarkRegistered());
        assertFalse("Helper2 should not be registered", helper2.isWatermarkRegistered());
    }

    @Test
    public void integrationTest_fullWorkflow() {
        // Create watermark
        String watermarkData = "test_data";
        Drawable mockDrawable = mock(Drawable.class);

        try (MockedStatic<WatermarkDecoder> mockedDecoder = mockStatic(WatermarkDecoder.class)) {
            mockedDecoder.when(() -> WatermarkDecoder.decodeWatermark(eq(context), eq(watermarkData)))
                    .thenReturn(mockDrawable);

            // Create view
            View watermarkView = WatermarkHelper.createWatermarkView(context, watermarkData);
            assertNotNull("View should be created", watermarkView);

            // Register watermark
            assertFalse("Should start unregistered", watermarkHelper.isWatermarkRegistered());
            watermarkHelper.setWatermarkRegistered();
            assertTrue("Should be registered", watermarkHelper.isWatermarkRegistered());

            // Remove watermark
            boolean removed = WatermarkHelper.removeWatermarkView((ImageView) watermarkView);
            assertTrue("Should be removed", removed);

            // Reset state
            watermarkHelper.reset();
            assertFalse("Should be unregistered after reset", watermarkHelper.isWatermarkRegistered());
        }
    }

    @Test
    public void integrationTest_bannerAdWorkflow() {
        Drawable mockDrawable = mock(Drawable.class);

        // Decode watermark
        String watermarkString = "banner_watermark";
        try (MockedStatic<WatermarkDecoder> mockedDecoder = mockStatic(WatermarkDecoder.class)) {
            mockedDecoder.when(() -> WatermarkDecoder.decodeWatermark(eq(context), eq(watermarkString)))
                    .thenReturn(mockDrawable);

            Drawable drawable = WatermarkHelper.decodeWatermark(context, watermarkString);
            assertNotNull("Drawable should be decoded", drawable);

            // Create view with gravity
            ImageView watermarkView = WatermarkHelper.createWatermarkView(context, drawable);
            assertNotNull("View should be created", watermarkView);

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) watermarkView.getLayoutParams();
            assertEquals("Should have TOP|START gravity", Gravity.TOP | Gravity.START, params.gravity);

            // Add to parent
            FrameLayout parent = new FrameLayout(context);
            parent.addView(watermarkView);

            // Register
            watermarkHelper.setWatermarkRegistered();
            assertTrue("Should be registered", watermarkHelper.isWatermarkRegistered());

            // Remove
            boolean removed = WatermarkHelper.removeWatermarkView(watermarkView);
            assertTrue("Should be removed", removed);
            assertEquals("Parent should be empty", 0, parent.getChildCount());

            // Reset
            watermarkHelper.reset();
            assertFalse("Should be reset", watermarkHelper.isWatermarkRegistered());
        }
    }
}

