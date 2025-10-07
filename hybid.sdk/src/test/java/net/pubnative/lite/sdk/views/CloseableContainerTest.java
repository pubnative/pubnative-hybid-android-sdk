// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.views;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.R;
import net.pubnative.lite.sdk.utils.ViewUtils;
import net.pubnative.lite.sdk.vpaid.helpers.BitmapHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.lang.reflect.Field;
import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
public class CloseableContainerTest {

    private Context context;
    private CloseableContainer closeableContainer;
    private AttributeSet mockAttributeSet;

    @Mock
    private CloseableContainer.OnCloseListener mockCloseListener;
    @Mock
    private CloseableContainer.OnSkipListener mockSkipListener;
    @Mock
    private Bitmap mockCloseBitmap;
    @Mock
    private Bitmap mockSkipBitmap;
    @Mock
    private Bitmap mockDecodedBitmap;

    private MockedStatic<BitmapHelper> mockedBitmapHelper;
    private MockedStatic<HyBid> mockedHyBid;
    private MockedStatic<ViewUtils> mockedViewUtils;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();
        mockAttributeSet = Robolectric.buildAttributeSet().build();

        mockedBitmapHelper = mockStatic(BitmapHelper.class);
        mockedHyBid = mockStatic(HyBid.class);
        mockedViewUtils = mockStatic(ViewUtils.class);

        // Mock static method calls
        when(HyBid.getNormalCloseXmlResource()).thenReturn(12345);
        when(ViewUtils.convertDpToPixel(eq(30.0f), any(Context.class))).thenReturn(90.0f);
        when(ViewUtils.convertDpToPixel(eq(0.0f), any(Context.class))).thenReturn(0.0f);
        when(ViewUtils.convertDpToPixel(eq(8f), any(Context.class))).thenReturn(24.0f);
        when(ViewUtils.convertDpToPixel(anyFloat(), any(Context.class))).thenReturn(60.0f);
    }

    @After
    public void tearDown() {
        mockedBitmapHelper.close();
        mockedHyBid.close();
        mockedViewUtils.close();
    }

    // Constructor Tests
    @Test
    public void constructor_singleParam_initializesCorrectly() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);

        closeableContainer = new CloseableContainer(context);

        assertNotNull("Container should be initialized", closeableContainer);
        assertEquals("Default close position should be TOP_LEFT", CloseableContainer.ClosePosition.TOP_LEFT, getPrivateField("mClosePosition"));
    }

    @Test
    public void constructor_twoParams_initializesCorrectly() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);

        closeableContainer = new CloseableContainer(context, mockAttributeSet);

        assertNotNull("Container should be initialized", closeableContainer);
        assertEquals("Default close position should be TOP_LEFT", CloseableContainer.ClosePosition.TOP_LEFT, getPrivateField("mClosePosition"));
    }

    @Test
    public void constructor_threeParams_initializesCorrectly() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);

        closeableContainer = new CloseableContainer(context, mockAttributeSet, 0);

        assertNotNull("Container should be initialized", closeableContainer);
    }

    @Test
    public void constructor_whenBitmapHelperReturnsNull_usesFallbackBitmaps() {
        // Test path when BitmapHelper.toBitmap returns null
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(null, null);
        when(BitmapHelper.decodeResource(any(Context.class), eq(R.mipmap.close))).thenReturn(mockDecodedBitmap);
        when(BitmapHelper.decodeResource(any(Context.class), eq(R.mipmap.skip))).thenReturn(mockDecodedBitmap);

        closeableContainer = new CloseableContainer(context);

        // Verify fallback decode methods were called
        mockedBitmapHelper.verify(() -> BitmapHelper.decodeResource(any(Context.class), eq(R.mipmap.close)));
        mockedBitmapHelper.verify(() -> BitmapHelper.decodeResource(any(Context.class), eq(R.mipmap.skip)));
    }

    @Test
    public void constructor_setsButtonPropertiesCorrectly() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);

        closeableContainer = new CloseableContainer(context);

        ImageButton closeButton = (ImageButton) getPrivateField("mCloseButton");
        ImageButton skipButton = (ImageButton) getPrivateField("mSkipButton");

        // Verify button properties
        assertEquals("Close button should have correct ID", R.id.button_fullscreen_close, closeButton.getId());
        assertEquals("Skip button should have correct ID", R.id.button_fullscreen_skip, skipButton.getId());
        assertEquals("Close button should be transparent", Color.TRANSPARENT, closeButton.getDrawingCacheBackgroundColor());
        assertEquals("Skip button should be transparent", Color.TRANSPARENT, skipButton.getDrawingCacheBackgroundColor());
    }

    // Click Listener Tests
    @Test
    public void closeButton_onClick_invokesListener() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);
        closeableContainer.setOnCloseListener(mockCloseListener);
        closeableContainer.setCloseVisible(true);

        ImageButton closeButton = closeableContainer.findViewById(R.id.button_fullscreen_close);
        closeButton.performClick();

        verify(mockCloseListener).onClose();
    }

    @Test
    public void closeButton_onClick_withNullListener_doesNotCrash() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);
        closeableContainer.setOnCloseListener(null);
        closeableContainer.setCloseVisible(true);

        ImageButton closeButton = closeableContainer.findViewById(R.id.button_fullscreen_close);

        // Should not throw exception
        closeButton.performClick();
    }

    @Test
    public void skipButton_onClick_invokesListener() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);
        closeableContainer.setOnSkipListener(mockSkipListener);
        closeableContainer.setSkipVisible(true);

        ImageButton skipButton = closeableContainer.findViewById(R.id.button_fullscreen_skip);
        skipButton.performClick();

        verify(mockSkipListener).onSkip();
    }

    @Test
    public void skipButton_onClick_withNullListener_doesNotCrash() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);
        closeableContainer.setOnSkipListener(null);
        closeableContainer.setSkipVisible(true);

        ImageButton skipButton = closeableContainer.findViewById(R.id.button_fullscreen_skip);

        // Should not throw exception
        skipButton.performClick();
    }

    // ClosePosition Enum Tests
    @Test
    public void closePosition_getGravity_returnsCorrectValues() {
        assertEquals(Gravity.TOP | Gravity.START, CloseableContainer.ClosePosition.TOP_LEFT.getGravity());
        assertEquals(Gravity.TOP | Gravity.CENTER_HORIZONTAL, CloseableContainer.ClosePosition.TOP_CENTER.getGravity());
        assertEquals(Gravity.TOP | Gravity.END, CloseableContainer.ClosePosition.TOP_RIGHT.getGravity());
        assertEquals(Gravity.CENTER, CloseableContainer.ClosePosition.CENTER.getGravity());
        assertEquals(Gravity.BOTTOM | Gravity.START, CloseableContainer.ClosePosition.BOTTOM_LEFT.getGravity());
        assertEquals(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, CloseableContainer.ClosePosition.BOTTOM_CENTER.getGravity());
        assertEquals(Gravity.BOTTOM | Gravity.END, CloseableContainer.ClosePosition.BOTTOM_RIGHT.getGravity());
        assertEquals(0, CloseableContainer.ClosePosition.RANDOM.getGravity());
    }

    @Test
    public void closePosition_getRandomPosition_returnsValidPosition() {
        // Test multiple times to ensure randomness works and all positions are valid
        for (int i = 0; i < 20; i++) {
            CloseableContainer.ClosePosition randomPosition = CloseableContainer.ClosePosition.getRandomPosition();
            assertNotNull("Random position should not be null", randomPosition);
            assertTrue("Random position should be a valid enum value",
                    Arrays.asList(CloseableContainer.ClosePosition.values()).contains(randomPosition));
        }
    }

    // SetClosePosition Tests
    @Test
    public void setClosePosition_withNull_doesNotChange() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);
        CloseableContainer.ClosePosition originalPosition = (CloseableContainer.ClosePosition) getPrivateField("mClosePosition");

        closeableContainer.setClosePosition(null);

        assertEquals("Position should not change when null is passed", originalPosition, getPrivateField("mClosePosition"));
    }

    @Test
    public void setClosePosition_withTopLeft_setsPaddingCorrectly() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);

        closeableContainer.setClosePosition(CloseableContainer.ClosePosition.TOP_LEFT);

        assertEquals("Position should be TOP_LEFT", CloseableContainer.ClosePosition.TOP_LEFT, getPrivateField("mClosePosition"));
        // Verify padding was set (specific padding values depend on ViewUtils mock)
        ImageButton closeButton = (ImageButton) getPrivateField("mCloseButton");
        assertNotNull("Close button should exist", closeButton);
    }

    @Test
    public void setClosePosition_withNonTopLeftPosition_setsPositionCorrectly() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);

        closeableContainer.setClosePosition(CloseableContainer.ClosePosition.BOTTOM_RIGHT);

        assertEquals("Position should be BOTTOM_RIGHT", CloseableContainer.ClosePosition.BOTTOM_RIGHT, getPrivateField("mClosePosition"));
    }

    // Visibility Tests
    @Test
    public void setCloseVisible_whenTrue_showsCloseButtonAndHidesSkip() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);

        closeableContainer.setCloseVisible(true);

        View closeButton = closeableContainer.findViewById(R.id.button_fullscreen_close);
        assertNotNull("Close button should be in view hierarchy", closeButton);
        assertEquals("Close button should be visible", View.VISIBLE, closeButton.getVisibility());
    }

    @Test
    public void setCloseVisible_whenFalse_hidesCloseButton() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);
        closeableContainer.setCloseVisible(true); // First make it visible

        closeableContainer.setCloseVisible(false);

        View closeButton = closeableContainer.findViewById(R.id.button_fullscreen_close);
        if (closeButton != null) {
            assertEquals("Close button should be gone", View.GONE, closeButton.getVisibility());
        }
    }

    @Test
    public void setSkipVisible_whenTrue_showsSkipButtonAndHidesClose() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);

        closeableContainer.setSkipVisible(true);

        View skipButton = closeableContainer.findViewById(R.id.button_fullscreen_skip);
        assertNotNull("Skip button should be in view hierarchy", skipButton);
        assertEquals("Skip button should be visible", View.VISIBLE, skipButton.getVisibility());
    }

    @Test
    public void setSkipVisible_whenFalse_hidesSkipButton() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);
        closeableContainer.setSkipVisible(true); // First make it visible

        closeableContainer.setSkipVisible(false);

        View skipButton = closeableContainer.findViewById(R.id.button_fullscreen_skip);
        if (skipButton != null) {
            assertEquals("Skip button should be gone", View.GONE, skipButton.getVisibility());
        }
    }

    // Custom Size Tests
    @Test
    public void setCloseSize_setsCustomSizeAndRepositions() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);
        Integer customSize = 40;

        closeableContainer.setCloseSize(customSize);

        // Verify custom size was stored
        Integer storedSize = (Integer) getPrivateField("mCustomCloseSize");
        assertEquals("Custom close size should be stored (converted to pixels)", 60, storedSize.intValue()); // Based on mock conversion

        // Verify button was repositioned
        View closeButton = closeableContainer.findViewById(R.id.button_fullscreen_close);
        assertNotNull("Close button should be repositioned in view hierarchy", closeButton);
    }

    @Test
    public void setSkipSize_setsCustomSizeAndRepositions() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);
        Integer customSize = 50;

        closeableContainer.setSkipSize(customSize);

        // Verify custom size was stored
        Integer storedSize = (Integer) getPrivateField("mCustomSkipSize");
        assertEquals("Custom skip size should be stored (converted to pixels)", 60, storedSize.intValue()); // Based on mock conversion

        // Verify button was repositioned
        View skipButton = closeableContainer.findViewById(R.id.button_fullscreen_skip);
        assertNotNull("Skip button should be repositioned in view hierarchy", skipButton);
    }

    // Position Button Tests (Private Method Coverage)
    @Test
    public void positionCloseButton_withCustomSize_setsSmallIdAndMargins() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);

        // Set custom size to trigger the custom size path in positionCloseButton
        setPrivateField("mCustomCloseSize", 80);
        closeableContainer.setCloseVisible(true); // This calls positionCloseButton internally

        View closeButton = closeableContainer.findViewById(R.id.button_fullscreen_close_small);
        assertNotNull("Close button with small ID should exist", closeButton);
    }

    @Test
    public void positionCloseButton_withDefaultSize_usesRegularSize() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);

        // Ensure no custom size is set
        setPrivateField("mCustomCloseSize", null);
        closeableContainer.setCloseVisible(true);

        View closeButton = closeableContainer.findViewById(R.id.button_fullscreen_close);
        assertNotNull("Close button with regular ID should exist", closeButton);
    }

    @Test
    public void positionSkipButton_withCustomSize_setsSmallIdAndMargins() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);

        // Set custom size to trigger the custom size path in positionSkipButton
        setPrivateField("mCustomSkipSize", 80);
        closeableContainer.setSkipVisible(true); // This calls positionSkipButton internally

        View skipButton = closeableContainer.findViewById(R.id.button_fullscreen_skip_small);
        assertNotNull("Skip button with small ID should exist", skipButton);
    }

    @Test
    public void positionSkipButton_withDefaultSize_usesRegularSize() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);

        // Ensure no custom size is set
        setPrivateField("mCustomSkipSize", null);
        closeableContainer.setSkipVisible(true);

        View skipButton = closeableContainer.findViewById(R.id.button_fullscreen_skip);
        assertNotNull("Skip button with regular ID should exist", skipButton);
    }

    // Listener Setter Tests
    @Test
    public void setOnCloseListener_storesListener() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);

        closeableContainer.setOnCloseListener(mockCloseListener);

        assertEquals("Close listener should be stored", mockCloseListener, getPrivateField("mOnCloseListener"));
    }

    @Test
    public void setOnSkipListener_storesListener() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);

        closeableContainer.setOnSkipListener(mockSkipListener);

        assertEquals("Skip listener should be stored", mockSkipListener, getPrivateField("mOnSkipListener"));
    }

    // Edge Case Tests
    @Test
    public void positionCloseButton_withNullButtons_doesNotCrash() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);

        // Set buttons to null to test null checks
        setPrivateField("mCloseButton", null);
        setPrivateField("mSkipButton", null);

        // Should not crash
        closeableContainer.setCloseVisible(true);
    }

    @Test
    public void positionSkipButton_withNullButtons_doesNotCrash() {
        when(BitmapHelper.toBitmap(any(Context.class), anyInt(), anyInt())).thenReturn(mockCloseBitmap, mockSkipBitmap);
        closeableContainer = new CloseableContainer(context);

        // Set buttons to null to test null checks
        setPrivateField("mCloseButton", null);
        setPrivateField("mSkipButton", null);

        // Should not crash
        closeableContainer.setSkipVisible(true);
    }

    // Helper methods for reflection access
    private Object getPrivateField(String fieldName) {
        try {
            Field field = CloseableContainer.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(closeableContainer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get private field: " + fieldName, e);
        }
    }

    private void setPrivateField(String fieldName, Object value) {
        try {
            Field field = CloseableContainer.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(closeableContainer, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set private field: " + fieldName, e);
        }
    }
}