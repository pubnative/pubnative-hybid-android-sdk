// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser.utils;

import android.os.Build;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Unit tests for the Style class.
 * This class verifies the internal logic of Style, such as cloning, resetting properties,
 * and default value initialization.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P})
public class StyleTest {

    //---------------------------------------------------------------------
    // Enum logic tests
    //---------------------------------------------------------------------

    @Test
    public void cssBlendMode_fromString_handlesValidAndInvalidValues() {
        // Test the custom fromString() method in the CSSBlendMode enum.
        assertEquals(Style.CSSBlendMode.multiply, Style.CSSBlendMode.fromString("multiply"));
        assertEquals(Style.CSSBlendMode.color_dodge, Style.CSSBlendMode.fromString("color-dodge"));
        assertEquals(Style.CSSBlendMode.UNSUPPORTED, Style.CSSBlendMode.fromString("unsupported-mode"));
        assertEquals(Style.CSSBlendMode.UNSUPPORTED, Style.CSSBlendMode.fromString(null));
    }

    //---------------------------------------------------------------------
    // Method logic tests
    //---------------------------------------------------------------------

    @Test
    public void getDefaultStyle_setsCorrectDefaults() {
        // Verifies that the static factory method populates a Style object with the correct defaults.
        Style defaultStyle = Style.getDefaultStyle();

        assertNotNull(defaultStyle);
        assertEquals(SVGBase.Colour.BLACK, defaultStyle.fill);
        assertNull("Default stroke should be null", defaultStyle.stroke);
        assertEquals(1.0f, defaultStyle.opacity, 0.0f);
        assertEquals(Style.FONT_WEIGHT_NORMAL, defaultStyle.fontWeight, 0.0f);
        assertEquals(Style.FillRule.NonZero, defaultStyle.fillRule);
        assertTrue("Default display should be true", defaultStyle.display);
    }

    @Test
    public void resetNonInheritingProperties_handlesRootAndNonRoot() {
        // This method has different behavior based on the isRootSVG flag, so we test both paths.
        Style style = new Style();
        // Modify some non-inheriting properties from their defaults
        style.opacity = 0.5f;
        style.mask = "url(#mymask)";
        style.overflow = false; // Set to a non-default to ensure it gets reset

        // Test the case for a non-root element
        style.resetNonInheritingProperties(false);
        assertEquals(1.0f, style.opacity, 0.0f);
        assertNull(style.mask);
        assertFalse("Overflow should be false for non-root elements", style.overflow);

        // Modify it again and test the case for a root element
        style.opacity = 0.5f;
        style.resetNonInheritingProperties(true);
        assertEquals(1.0f, style.opacity, 0.0f);
        assertTrue("Overflow should be true for the root SVG element", style.overflow);
    }

    @Test
    public void clone_createsDeepCopy() throws CloneNotSupportedException {
        // Verifies that clone() creates a deep copy, especially for mutable array fields.
        Style original = new Style();
        original.opacity = 0.8f;
        original.fillRule = Style.FillRule.EvenOdd;
        original.strokeDashArray = new SVGBase.Length[]{ new SVGBase.Length(5f), new SVGBase.Length(2f) };

        Style cloned = (Style) original.clone();

        // 1. Check that it's a new object instance
        assertNotSame("Cloned object should be a new instance", original, cloned);

        // 2. Check that values have been copied
        assertEquals(original.opacity, cloned.opacity);
        assertEquals(original.fillRule, cloned.fillRule);
        assertNotNull(cloned.strokeDashArray);
        assertEquals(2, cloned.strokeDashArray.length);

        // 3. Check that mutable fields (like arrays) are new instances (deep copy)
        assertNotSame("strokeDashArray should be a new array instance", original.strokeDashArray, cloned.strokeDashArray);
        assertArrayEquals(original.strokeDashArray, cloned.strokeDashArray);

        // 4. Modify the original array to ensure the clone is not affected
        original.strokeDashArray[0] = new SVGBase.Length(10f);
        assertNotEquals("Modifying original's array should not affect the clone's array",
                original.strokeDashArray[0].floatValue(),
                cloned.strokeDashArray[0].floatValue(),
                0.0f);
    }
}