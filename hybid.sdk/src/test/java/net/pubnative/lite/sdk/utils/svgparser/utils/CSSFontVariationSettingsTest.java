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
 * Unit tests for the CSSFontVariationSettings class.
 * This class verifies the constructor, instance methods, and the static parsing logic.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P})
public class CSSFontVariationSettingsTest {

    //---------------------------------------------------------------------
    // Constructor and Instance Method Tests
    //---------------------------------------------------------------------

    @Test
    public void constructors_and_addSetting_workCorrectly() {
        // Test the default and copy constructors.
        CSSFontVariationSettings settings1 = new CSSFontVariationSettings();
        assertNotNull(settings1);
        assertEquals("", settings1.toString());

        // Test addSetting
        settings1.addSetting("wght", 400f);
        assertTrue(settings1.toString().contains("'wght' 400"));

        // Test copy constructor
        CSSFontVariationSettings settings2 = new CSSFontVariationSettings(settings1);
        assertEquals(settings1.toString(), settings2.toString());
    }

    @Test
    public void applySettings_mergesAndOverwritesCorrectly() {
        // The applySettings method should add new settings and overwrite existing ones.
        CSSFontVariationSettings baseSettings = new CSSFontVariationSettings();
        baseSettings.addSetting("wght", 400f);
        baseSettings.addSetting("wdth", 100f);

        CSSFontVariationSettings newSettings = new CSSFontVariationSettings();
        newSettings.addSetting("wght", 700f); // Overwrite existing
        newSettings.addSetting("ital", 1f);   // Add new

        baseSettings.applySettings(newSettings);
        baseSettings.applySettings(null); // Should be a no-op and not crash

        String result = baseSettings.toString();
        assertTrue("Should contain new setting 'ital'", result.contains("'ital' 1"));
        assertTrue("Should contain overwritten setting 'wght'", result.contains("'wght' 700"));
        assertTrue("Should still contain original setting 'wdth'", result.contains("'wdth' 100"));
        assertFalse("Should not contain old setting 'wght'", result.contains("'wght' 400"));
    }

    @Test
    public void toString_formatsOutputCorrectly() {
        // The toString method should format floats correctly and handle multiple entries.
        CSSFontVariationSettings settings = new CSSFontVariationSettings();

        // Test empty
        assertEquals("", settings.toString());

        // Test single entry with integer-like float
        settings.addSetting("wght", 500f);
        assertEquals("'wght' 500", settings.toString());

        // Test multiple entries and float formatting/rounding
        settings.addSetting("slnt", -12.345f);
        String result = settings.toString();
        assertTrue(result.contains("'wght' 500"));
        assertTrue("Should format and round the float value", result.contains("'slnt' -12.35"));
        assertTrue(result.contains(","));
    }


    //---------------------------------------------------------------------
    // Static Parser Test
    //---------------------------------------------------------------------

    @Test
    public void parseFontVariationSettings_coversAllOutcomes() {
        // This single test covers all success and failure paths of the parsing logic.

        // --- Success Paths ---
        CSSFontVariationSettings resultSingle = CSSFontVariationSettings.parseFontVariationSettings("'wght' 700");
        assertNotNull("Should parse a single valid entry", resultSingle);
        assertEquals("'wght' 700", resultSingle.toString());

        CSSFontVariationSettings resultMultiple = CSSFontVariationSettings.parseFontVariationSettings("'wdth' 75.5, 'slnt' -10");
        assertNotNull("Should parse multiple valid entries", resultMultiple);
        String resultString = resultMultiple.toString();
        assertTrue(resultString.contains("'wdth' 75.5"));
        assertTrue(resultString.contains("'slnt' -10"));

        // --- Keyword Path ---
        // The spec says "normal" resets the settings. The current implementation returns null. We test for that.
        assertNull("Should return null for the 'normal' keyword", CSSFontVariationSettings.parseFontVariationSettings("normal"));

        // --- Failure Paths ---
        assertNull("Should fail on unquoted name", CSSFontVariationSettings.parseFontVariationSettings("wght 400"));
        assertNull("Should fail on name length != 4", CSSFontVariationSettings.parseFontVariationSettings("'width' 100"));
        assertNull("Should fail when value is missing", CSSFontVariationSettings.parseFontVariationSettings("'wght'"));
        assertNull("Should fail when value is not a number", CSSFontVariationSettings.parseFontVariationSettings("'wght' heavy"));
    }
}