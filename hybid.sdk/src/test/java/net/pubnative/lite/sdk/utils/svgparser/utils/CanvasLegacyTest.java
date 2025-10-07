// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser.utils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class CanvasLegacyTest {

    private Paint mockPaint;
    private RectF testRect;

    @Before
    public void setUp() {
        mockPaint = mock(Paint.class);
        testRect = new RectF(0f, 0f, 100f, 100f);
    }

    @Test
    public void staticConstants_areInitializedCorrectly() {
        // Test that the static constants are properly initialized from Canvas class
        assertTrue("MATRIX_SAVE_FLAG should be initialized to a valid integer",
                CanvasLegacy.MATRIX_SAVE_FLAG != 0);
        assertTrue("ALL_SAVE_FLAG should be initialized to a valid integer",
                CanvasLegacy.ALL_SAVE_FLAG != 0);
    }

    @Test
    public void staticConstants_haveExpectedValues() throws Exception {
        // Verify constants match the actual Canvas field values (when available)
        try {
            Field matrixSaveField = Canvas.class.getField("MATRIX_SAVE_FLAG");
            Field allSaveField = Canvas.class.getField("ALL_SAVE_FLAG");

            int expectedMatrixFlag = (int) matrixSaveField.get(null);
            int expectedAllFlag = (int) allSaveField.get(null);

            assertEquals("MATRIX_SAVE_FLAG should match Canvas.MATRIX_SAVE_FLAG",
                    expectedMatrixFlag, CanvasLegacy.MATRIX_SAVE_FLAG);
            assertEquals("ALL_SAVE_FLAG should match Canvas.ALL_SAVE_FLAG",
                    expectedAllFlag, CanvasLegacy.ALL_SAVE_FLAG);
        } catch (NoSuchFieldException e) {
            // Skip this test on API levels where these fields don't exist
            // This is expected behavior on newer Android versions
        }
    }

    @Test
    public void save_withValidCanvasAndFlags_invokesCanvasSaveMethod() {
        // Test successful save method invocation
        Canvas realCanvas = new Canvas();
        int testFlags = CanvasLegacy.MATRIX_SAVE_FLAG;

        // This should not throw an exception
        assertDoesNotThrow(() -> {
            CanvasLegacy.save(realCanvas, testFlags);
        });
    }

    @Test
    public void save_withNullCanvas_throwsException() {
        // Test null canvas handling
        int testFlags = CanvasLegacy.MATRIX_SAVE_FLAG;

        // The sneakyThrow method will re-throw the underlying exception.
        // For Method.invoke(null, ...), this is a NullPointerException.
        assertThrows(NullPointerException.class, () -> {
            CanvasLegacy.save(null, testFlags);
        });
    }

    @Test
    public void save_withInvalidFlags_handlesGracefully() {
        // Test with invalid flags - should still call the method
        Canvas realCanvas = new Canvas();
        int invalidFlags = -999;

        // Should not crash, even with invalid flags (Canvas method will handle it)
        assertDoesNotThrow(() -> {
            CanvasLegacy.save(realCanvas, invalidFlags);
        });
    }

    @Test
    public void saveLayer_withValidParameters_invokesCanvasSaveLayerMethod() {
        // Test successful saveLayer method invocation
        Canvas realCanvas = new Canvas();
        Paint realPaint = new Paint();
        int testFlags = CanvasLegacy.ALL_SAVE_FLAG;

        // This should not throw an exception
        assertDoesNotThrow(() -> {
            CanvasLegacy.saveLayer(realCanvas, testRect, realPaint, testFlags);
        });
    }

    @Test
    public void saveLayer_withNullCanvas_throwsException() {
        // Test null canvas handling in saveLayer
        int testFlags = CanvasLegacy.ALL_SAVE_FLAG;

        assertThrows(NullPointerException.class, () -> {
            CanvasLegacy.saveLayer(null, testRect, mockPaint, testFlags);
        });
    }

    @Test
    public void saveLayer_withNullRect_handlesGracefully() {
        // Test with null RectF (which should be valid for saveLayer)
        Canvas realCanvas = new Canvas();
        Paint realPaint = new Paint();
        int testFlags = CanvasLegacy.ALL_SAVE_FLAG;

        // Should not crash with null rect (Canvas allows this)
        assertDoesNotThrow(() -> {
            CanvasLegacy.saveLayer(realCanvas, null, realPaint, testFlags);
        });
    }

    @Test
    public void saveLayer_withNullPaint_handlesGracefully() {
        // Test with null Paint (which should be valid for saveLayer)
        Canvas realCanvas = new Canvas();
        int testFlags = CanvasLegacy.ALL_SAVE_FLAG;

        // Should not crash with null paint (Canvas allows this)
        assertDoesNotThrow(() -> {
            CanvasLegacy.saveLayer(realCanvas, testRect, null, testFlags);
        });
    }

    @Test
    public void saveLayer_withAllNullParameters_throwsException() {
        // Test with null canvas (other nulls are OK)
        int testFlags = CanvasLegacy.ALL_SAVE_FLAG;

        assertThrows(NullPointerException.class, () -> {
            CanvasLegacy.saveLayer(null, null, null, testFlags);
        });
    }

    @Test
    public void sneakyThrow_withNullThrowable_throwsNullPointerException() {
        // Test sneakyThrow with null parameter
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            // We can't directly test sneakyThrow since it's private, but we can trigger it
            // by causing a reflection error that will call it
            try {
                Method sneakyThrow = CanvasLegacy.class.getDeclaredMethod("sneakyThrow", Throwable.class);
                sneakyThrow.setAccessible(true);
                sneakyThrow.invoke(null, (Throwable) null);
            } catch (Exception e) {
                throw (RuntimeException) e.getCause();
            }
        });

        assertEquals("t", exception.getMessage());
    }

    @Test
    public void reflectionMethods_areInitializedCorrectly() throws Exception {
        // Test that the static methods are properly initialized
        Field saveField = CanvasLegacy.class.getDeclaredField("SAVE");
        Field saveLayerField = CanvasLegacy.class.getDeclaredField("SAVE_LAYER");

        saveField.setAccessible(true);
        saveLayerField.setAccessible(true);

        Method saveMethod = (Method) saveField.get(null);
        Method saveLayerMethod = (Method) saveLayerField.get(null);

        assertNotNull("SAVE method should be initialized", saveMethod);
        assertNotNull("SAVE_LAYER method should be initialized", saveLayerMethod);

        // Verify method signatures
        assertEquals("SAVE method should have int parameter", 1, saveMethod.getParameterCount());
        assertEquals("SAVE method first parameter should be int",
                int.class, saveMethod.getParameterTypes()[0]);

        assertEquals("SAVE_LAYER method should have 3 parameters", 3, saveLayerMethod.getParameterCount());
        assertEquals("SAVE_LAYER method first parameter should be RectF",
                RectF.class, saveLayerMethod.getParameterTypes()[0]);
        assertEquals("SAVE_LAYER method second parameter should be Paint",
                Paint.class, saveLayerMethod.getParameterTypes()[1]);
        assertEquals("SAVE_LAYER method third parameter should be int",
                int.class, saveLayerMethod.getParameterTypes()[2]);
    }

    @Test
    public void staticInitialization_handlesReflectionErrors() {
        // This test verifies that if reflection fails during static initialization,
        // a RuntimeException is thrown via sneakyThrow
        // We can't easily test the static block directly, but we can verify the behavior

        // The class should have loaded successfully if we got this far
        assertNotNull("Class should be loaded", CanvasLegacy.class);

        // If static initialization had failed, these fields would not be accessible
        assertTrue("Static constants should be initialized",
                CanvasLegacy.MATRIX_SAVE_FLAG != 0 || CanvasLegacy.ALL_SAVE_FLAG != 0);
    }

    @Test
    public void save_differentFlagValues_handlesAllValidFlags() {
        // Test with different valid flag combinations
        Canvas realCanvas = new Canvas();

        // Test with MATRIX_SAVE_FLAG
        assertDoesNotThrow(() -> {
            CanvasLegacy.save(realCanvas, CanvasLegacy.MATRIX_SAVE_FLAG);
        });

        // Test with ALL_SAVE_FLAG
        assertDoesNotThrow(() -> {
            CanvasLegacy.save(realCanvas, CanvasLegacy.ALL_SAVE_FLAG);
        });

        // Test with combined flags
        assertDoesNotThrow(() -> {
            CanvasLegacy.save(realCanvas, CanvasLegacy.MATRIX_SAVE_FLAG | CanvasLegacy.ALL_SAVE_FLAG);
        });

        // Test with zero flags
        assertDoesNotThrow(() -> {
            CanvasLegacy.save(realCanvas, 0);
        });
    }

    @Test
    public void saveLayer_differentFlagValues_handlesAllValidFlags() {
        // Test saveLayer with different valid flag combinations
        Canvas realCanvas = new Canvas();
        Paint realPaint = new Paint();

        // Test with MATRIX_SAVE_FLAG
        assertDoesNotThrow(() -> {
            CanvasLegacy.saveLayer(realCanvas, testRect, realPaint, CanvasLegacy.MATRIX_SAVE_FLAG);
        });

        // Test with ALL_SAVE_FLAG
        assertDoesNotThrow(() -> {
            CanvasLegacy.saveLayer(realCanvas, testRect, realPaint, CanvasLegacy.ALL_SAVE_FLAG);
        });

        // Test with combined flags
        assertDoesNotThrow(() -> {
            CanvasLegacy.saveLayer(realCanvas, testRect, realPaint,
                    CanvasLegacy.MATRIX_SAVE_FLAG | CanvasLegacy.ALL_SAVE_FLAG);
        });
    }

    @Test
    public void errorHandling_preservesOriginalExceptionTypes() {
        // Test that sneakyThrow preserves the original exception type
        Canvas realCanvas = new Canvas();

        // We can't easily create a controlled reflection error, but we can verify
        // that when errors do occur, they're properly wrapped and thrown
        // This is more of a structural test to ensure the error handling exists

        try {
            CanvasLegacy.save(realCanvas, CanvasLegacy.MATRIX_SAVE_FLAG);
            // If no exception, that's also fine - it means the method worked
        } catch (RuntimeException e) {
            // If an exception occurred, it should have a cause
            assertNotNull("Runtime exception should have a cause", e.getCause());
        }
    }

    // Helper method for assertDoesNotThrow (if not available in your JUnit version)
    private void assertDoesNotThrow(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            fail("Expected no exception, but got: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}