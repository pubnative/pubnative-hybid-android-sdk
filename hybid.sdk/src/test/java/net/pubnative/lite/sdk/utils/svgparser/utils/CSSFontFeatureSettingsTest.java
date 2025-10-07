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
 * Unit tests for the CSSFontFeatureSettings class.
 * This class verifies the complex parsing logic for font-feature-settings and
 * the various font-variant CSS properties.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P})
public class CSSFontFeatureSettingsTest {

    //---------------------------------------------------------------------
    // Instance Method Tests
    //---------------------------------------------------------------------

    @Test
    public void constructors_and_hasSettings_workCorrectly() {
        // Test the default and copy constructors.
        CSSFontFeatureSettings settings1 = new CSSFontFeatureSettings();
        assertNotNull(settings1);
        assertFalse(settings1.hasSettings());

        settings1.applyKerning(Style.FontKerning.normal);
        assertTrue(settings1.hasSettings());

        CSSFontFeatureSettings settings2 = new CSSFontFeatureSettings(settings1);
        assertTrue(settings2.hasSettings());
        assertEquals(settings1.toString(), settings2.toString());
    }

    @Test
    public void applySettings_mergesSettings() {
        // It should merge the settings from another instance into the current one.
        CSSFontFeatureSettings base = CSSFontFeatureSettings.parseFontFeatureSettings("'liga' 1");
        CSSFontFeatureSettings toApply = CSSFontFeatureSettings.parseFontFeatureSettings("'clig' 0, 'liga' 0");

        base.applySettings(toApply);
        base.applySettings(null); // Should not crash

        String resultString = base.toString();
        assertTrue("Should contain merged setting 'clig' 0", resultString.contains("'clig' 0"));
        assertTrue("Should contain overwritten setting 'liga' 0", resultString.contains("'liga' 0"));
        assertFalse("Should no longer contain old setting 'liga' 1", resultString.contains("'liga' 1"));
    }

    @Test
    public void applyKerning_setsKernFeature() {
        // Test that applyKerning correctly sets the 'kern' feature to ON or OFF.
        CSSFontFeatureSettings settings = new CSSFontFeatureSettings();
        settings.applyKerning(Style.FontKerning.none);
        assertTrue(settings.toString().contains("'kern' 0"));

        settings.applyKerning(Style.FontKerning.auto);
        assertTrue(settings.toString().contains("'kern' 1"));

        settings.applyKerning(Style.FontKerning.normal);
        assertTrue(settings.toString().contains("'kern' 1"));
    }

    @Test
    public void toString_formatsCorrectly() {
        // The toString() method should format the settings map into a CSS-compatible string.
        CSSFontFeatureSettings settings = new CSSFontFeatureSettings();
        assertTrue(settings.toString().isEmpty());

        // Use the parser to create an object with known state
        settings = CSSFontFeatureSettings.parseFontFeatureSettings("'liga' 1, 'dlig' 0");
        assertNotNull(settings);

        // HashMap order is not guaranteed, so check for containment
        String resultString = settings.toString();
        assertTrue(resultString.contains("'liga' 1"));
        assertTrue(resultString.contains("'dlig' 0"));
        assertTrue(resultString.contains(","));
    }

    //---------------------------------------------------------------------
    // Static Parser Tests
    //---------------------------------------------------------------------

    @Test
    public void parseFontFeatureSettings_parsesCorrectly() {
        // Test the main parser for the 'font-feature-settings' property.
        CSSFontFeatureSettings result = CSSFontFeatureSettings.parseFontFeatureSettings("'liga' on, 'clig' 0, 'dlig'");
        assertNotNull(result);

        String resultString = result.toString();
        assertTrue(resultString.contains("'liga' 1"));
        assertTrue(resultString.contains("'clig' 0"));
        assertTrue(resultString.contains("'dlig' 1")); // Default value is 1
    }

    @Test
    public void parseFontFeatureSettings_withInvalidInput_returnsNull() {
        // Invalid feature names (not 4 chars) should result in a null return.
        assertNull(CSSFontFeatureSettings.parseFontFeatureSettings("'longname' on"));
        assertNull(CSSFontFeatureSettings.parseFontFeatureSettings("'liga' 100"));
    }

    @Test
    public void nextFeatureEntry_success_defaultValue() {
        CSSFontFeatureSettings result = CSSFontFeatureSettings.parseFontFeatureSettings("'liga'");
        assertNotNull(result);
        assertTrue(result.toString().contains("'liga' 1"));
    }

    @Test
    public void nextFeatureEntry_success_onValue() {
        CSSFontFeatureSettings result = CSSFontFeatureSettings.parseFontFeatureSettings("'kern' on");
        assertNotNull(result);
        assertTrue(result.toString().contains("'kern' 1"));
    }

    @Test
    public void nextFeatureEntry_success_offValue() {
        CSSFontFeatureSettings result = CSSFontFeatureSettings.parseFontFeatureSettings("'clig' off");
        assertNotNull(result);
        assertTrue(result.toString().contains("'clig' 0"));
    }

    @Test
    public void nextFeatureEntry_success_integerValue() {
        CSSFontFeatureSettings result = CSSFontFeatureSettings.parseFontFeatureSettings("'smcp' 12");
        assertNotNull(result);
        assertTrue(result.toString().contains("'smcp' 12"));
    }

    @Test
    public void nextFeatureEntry_success_withWhitespace() {
        CSSFontFeatureSettings result = CSSFontFeatureSettings.parseFontFeatureSettings("  'dlig'   5  ");
        assertNotNull(result);
        assertTrue(result.toString().contains("'dlig' 5"));
    }

    @Test
    public void nextFeatureEntry_failure_unquotedName() {
        // nextQuotedString() will return null, causing nextFeatureEntry to return null.
        CSSFontFeatureSettings result = CSSFontFeatureSettings.parseFontFeatureSettings("liga 1");
        assertNull(result);
    }

    @Test
    public void nextFeatureEntry_failure_nameLengthNot4() {
        // The condition 'name.length() != 4' will trigger.
        assertNull(CSSFontFeatureSettings.parseFontFeatureSettings("'lig' 1"));
        assertNull(CSSFontFeatureSettings.parseFontFeatureSettings("'ligature' 1"));
    }

    @Test
    public void nextFeatureEntry_failure_valueTooHigh() {
        // The condition 'num > 99' will trigger.
        CSSFontFeatureSettings result = CSSFontFeatureSettings.parseFontFeatureSettings("'kern' 100");
        assertNull(result);
    }

    @Test
    public void parseFontKerning_parsesKeywords() {
        // Test the simple keyword parser for 'font-kerning'.
        assertEquals(Style.FontKerning.auto, CSSFontFeatureSettings.parseFontKerning("auto"));
        assertEquals(Style.FontKerning.normal, CSSFontFeatureSettings.parseFontKerning("normal"));
        assertEquals(Style.FontKerning.none, CSSFontFeatureSettings.parseFontKerning("none"));
        assertNull(CSSFontFeatureSettings.parseFontKerning("invalid"));
    }

    @Test
    public void parseVariantLigatures_parsesKeywordsAndCombinations() {
        // Test the complex parser for 'font-variant-ligatures'.
        assertSame(CSSFontFeatureSettings.LIGATURES_NORMAL, CSSFontFeatureSettings.parseVariantLigatures("normal"));
        assertNotNull(CSSFontFeatureSettings.parseVariantLigatures("none"));
        assertNotNull(CSSFontFeatureSettings.parseVariantLigatures("common-ligatures no-discretionary-ligatures"));

        // Invalid combinations should return null
        assertNull(CSSFontFeatureSettings.parseVariantLigatures("common-ligatures no-common-ligatures"));
        assertNull(CSSFontFeatureSettings.parseVariantLigatures("common-ligatures extra-token"));
    }

    @Test
    public void parseVariantPosition_parsesKeywords() {
        // Test the parser for 'font-variant-position'.
        assertSame(CSSFontFeatureSettings.POSITION_ALL_OFF, CSSFontFeatureSettings.parseVariantPosition("normal"));
        assertNotNull(CSSFontFeatureSettings.parseVariantPosition("sub"));
        assertNotNull(CSSFontFeatureSettings.parseVariantPosition("super"));
        assertNull(CSSFontFeatureSettings.parseVariantPosition("invalid"));
    }

    @Test
    public void parseVariantCaps_parsesKeywords() {
        // Test the parser for 'font-variant-caps'.
        assertSame(CSSFontFeatureSettings.CAPS_ALL_OFF, CSSFontFeatureSettings.parseVariantCaps("normal"));
        assertNotNull(CSSFontFeatureSettings.parseVariantCaps("small-caps"));
        assertNotNull(CSSFontFeatureSettings.parseVariantCaps("all-small-caps"));
        assertNotNull(CSSFontFeatureSettings.parseVariantCaps("unicase"));
        assertNull(CSSFontFeatureSettings.parseVariantCaps("invalid"));
    }

    @Test
    public void parseVariantNumeric_parsesKeywordsAndCombinations() {
        // Test the complex parser for 'font-variant-numeric'.
        assertSame(CSSFontFeatureSettings.NUMERIC_ALL_OFF, CSSFontFeatureSettings.parseVariantNumeric("normal"));
        assertNotNull(CSSFontFeatureSettings.parseVariantNumeric("lining-nums proportional-nums ordinal"));

        // Invalid combinations should return null
        assertNull(CSSFontFeatureSettings.parseVariantNumeric("lining-nums oldstyle-nums"));
    }

    @Test
    public void parseEastAsian_parsesKeywordsAndCombinations() {
        // Test the complex parser for 'font-variant-east-asian'.
        assertSame(CSSFontFeatureSettings.EAST_ASIAN_ALL_OFF, CSSFontFeatureSettings.parseEastAsian("normal"));
        assertNotNull(CSSFontFeatureSettings.parseEastAsian("jis78 full-width ruby"));

        // Invalid combinations should return null
        assertNull(CSSFontFeatureSettings.parseEastAsian("jis78 jis90"));
    }

    @Test
    public void parseFontVariant_orchestratesCorrectly() {
        // This is the main orchestrator method for the 'font-variant' shorthand property.
        Style style = new Style();

        // Test "normal" reset
        CSSFontFeatureSettings.parseFontVariant(style, "normal");
        assertSame(CSSFontFeatureSettings.LIGATURES_NORMAL, style.fontVariantLigatures);
        assertSame(CSSFontFeatureSettings.CAPS_ALL_OFF, style.fontVariantCaps);

        // Test a complex combination
        CSSFontFeatureSettings.parseFontVariant(style, "no-common-ligatures super small-caps slashed-zero jis04");

        // Verify that each sub-property was parsed and set on the Style object
        assertNotSame(CSSFontFeatureSettings.LIGATURES_NORMAL, style.fontVariantLigatures);
        assertNotSame(CSSFontFeatureSettings.POSITION_ALL_OFF, style.fontVariantPosition);
        assertNotSame(CSSFontFeatureSettings.CAPS_ALL_OFF, style.fontVariantCaps);
        assertNotSame(CSSFontFeatureSettings.NUMERIC_ALL_OFF, style.fontVariantNumeric);
        assertNotSame(CSSFontFeatureSettings.EAST_ASIAN_ALL_OFF, style.fontVariantEastAsian);

        // Verify a specific setting from the parse using the public toString() method
        assertTrue(style.fontVariantLigatures.toString().contains("'liga' 0")); // from no-common-ligatures
        assertTrue(style.fontVariantPosition.toString().contains("'sups' 1")); // from super
        assertTrue(style.fontVariantCaps.toString().contains("'smcp' 1"));     // from small-caps
        assertTrue(style.fontVariantNumeric.toString().contains("'zero' 1"));  // from slashed-zero
        assertTrue(style.fontVariantEastAsian.toString().contains("'jp04' 1"));  // from jis04
    }
}