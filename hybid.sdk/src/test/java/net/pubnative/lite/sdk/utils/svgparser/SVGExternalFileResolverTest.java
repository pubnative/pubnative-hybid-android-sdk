// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the SVGExternalFileResolver class.
 * This test verifies that the default implementation returns null or false for all methods.
 */
public class SVGExternalFileResolverTest {

    private SVGExternalFileResolver resolver;

    @Before
    public void setUp() {
        resolver = new SVGExternalFileResolver();
    }

    @Test
    public void resolveFont_shouldReturnNull() {
        // The default implementation should always return null for font resolution.
        Typeface result = resolver.resolveFont("Arial", 400f, "normal", 100f);
        assertNull("resolveFont should return null", result);
    }

    @Test
    public void resolveImage_shouldReturnNull() {
        // The default implementation should always return null for image resolution.
        Bitmap result = resolver.resolveImage("image.png");
        assertNull("resolveImage should return null", result);
    }

    @Test
    public void resolveCSSStyleSheet_shouldReturnNull() {
        // The default implementation should always return null for stylesheet resolution.
        String result = resolver.resolveCSSStyleSheet("style.css");
        assertNull("resolveCSSStyleSheet should return null", result);
    }

    @Test
    public void isFormatSupported_shouldReturnFalse() {
        // The default implementation should always return false for format support checks.
        boolean result = resolver.isFormatSupported("image/jpeg");
        assertFalse("isFormatSupported should return false", result);
    }
}