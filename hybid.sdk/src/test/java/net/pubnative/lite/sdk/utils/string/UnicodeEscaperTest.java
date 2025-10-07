// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.string;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class UnicodeEscaperTest {

    //----------------------------------------------------------------------------------------------
    // Static Factory Method Tests - Grouped by Method
    //----------------------------------------------------------------------------------------------

    @Test
    public void above_shouldCreateEscaperForCodePointsAboveThreshold() throws IOException {
        // Test above() factory method with various thresholds

        // Above ASCII range (127)
        UnicodeEscaper escaper = UnicodeEscaper.above(127);
        assertNotNull(escaper);

        // Test ASCII characters are not escaped
        StringWriter writer = new StringWriter();
        boolean consumed = escaper.translate(65, writer); // 'A'
        assertFalse(consumed);
        assertEquals("", writer.toString());

        // Test characters above threshold are escaped
        writer = new StringWriter();
        consumed = escaper.translate(233, writer); // 'é'
        assertTrue(consumed);
        assertEquals("\\u00E9", writer.toString());

        // Test boundary condition
        writer = new StringWriter();
        consumed = escaper.translate(127, writer); // exactly at threshold
        assertFalse(consumed); // above() is exclusive

        consumed = escaper.translate(128, writer); // one above threshold
        assertTrue(consumed);
    }

    @Test
    public void below_shouldCreateEscaperForCodePointsBelowThreshold() throws IOException {
        // Test below() factory method with various thresholds

        // Below extended ASCII (256)
        UnicodeEscaper escaper = UnicodeEscaper.below(256);
        assertNotNull(escaper);

        // Test characters below threshold are escaped
        StringWriter writer = new StringWriter();
        boolean consumed = escaper.translate(65, writer); // 'A'
        assertTrue(consumed);
        assertEquals("\\u0041", writer.toString());

        // Test characters above threshold are not escaped
        writer = new StringWriter();
        consumed = escaper.translate(300, writer);
        assertFalse(consumed);
        assertEquals("", writer.toString());

        // Test boundary conditions
        writer = new StringWriter();
        consumed = escaper.translate(256, writer); // exactly at threshold
        assertFalse(consumed); // below() is exclusive

        consumed = escaper.translate(255, writer); // one below threshold
        assertTrue(consumed);
    }

    @Test
    public void between_shouldCreateEscaperForCodePointsInRange() throws IOException {
        // Test between() factory method with various ranges

        // Between printable ASCII (32-126)
        UnicodeEscaper escaper = UnicodeEscaper.between(32, 126);
        assertNotNull(escaper);

        // Test characters in range are escaped
        StringWriter writer = new StringWriter();
        boolean consumed = escaper.translate(65, writer); // 'A'
        assertTrue(consumed);
        assertEquals("\\u0041", writer.toString());

        // Test characters outside range are not escaped
        writer = new StringWriter();
        consumed = escaper.translate(31, writer); // below range
        assertFalse(consumed);

        consumed = escaper.translate(127, writer); // above range
        assertFalse(consumed);

        // Test boundary conditions (inclusive)
        writer = new StringWriter();
        consumed = escaper.translate(32, writer); // lower boundary
        assertTrue(consumed);

        writer = new StringWriter();
        consumed = escaper.translate(126, writer); // upper boundary
        assertTrue(consumed);
    }

    @Test
    public void outsideOf_shouldCreateEscaperForCodePointsOutsideRange() throws IOException {
        // Test outsideOf() factory method with various ranges

        // Outside basic Latin (0-127)
        UnicodeEscaper escaper = UnicodeEscaper.outsideOf(0, 127);
        assertNotNull(escaper);

        // Test characters outside range are escaped
        StringWriter writer = new StringWriter();
        boolean consumed = escaper.translate(128, writer); // above range
        assertTrue(consumed);
        assertEquals("\\u0080", writer.toString());

        // Test characters inside range are not escaped
        writer = new StringWriter();
        consumed = escaper.translate(65, writer); // 'A' - inside range
        assertFalse(consumed);
        assertEquals("", writer.toString());

        // Test boundary conditions (exclusive for outsideOf)
        writer = new StringWriter();
        consumed = escaper.translate(0, writer); // lower boundary
        assertFalse(consumed); // inside range

        consumed = escaper.translate(127, writer); // upper boundary
        assertFalse(consumed); // inside range
    }

    //----------------------------------------------------------------------------------------------
    // Constructor Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void constructor_shouldCreateEscaperWithSpecifiedParameters() throws IOException {
        // Test constructors with various parameter combinations

        // Default constructor - escapes everything
        UnicodeEscaper defaultEscaper = new UnicodeEscaper();
        StringWriter writer = new StringWriter();
        boolean consumed = defaultEscaper.translate(65, writer);
        assertTrue(consumed);
        assertEquals("\\u0041", writer.toString());

        // Between mode constructor
        UnicodeEscaper betweenEscaper = new UnicodeEscaper(50, 100, true);
        writer = new StringWriter();
        consumed = betweenEscaper.translate(75, writer); // in range
        assertTrue(consumed);

        writer = new StringWriter();
        consumed = betweenEscaper.translate(25, writer); // outside range
        assertFalse(consumed);

        // Outside mode constructor
        UnicodeEscaper outsideEscaper = new UnicodeEscaper(50, 100, false);
        writer = new StringWriter();
        consumed = outsideEscaper.translate(25, writer); // outside range
        assertTrue(consumed);

        writer = new StringWriter();
        consumed = outsideEscaper.translate(75, writer); // in range
        assertFalse(consumed);
    }

    //----------------------------------------------------------------------------------------------
    // Core Translation Logic Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void translate_shouldHandleBMPCharactersCorrectly() throws IOException {
        // Test Basic Multilingual Plane characters (0x0000 to 0xFFFF)
        UnicodeEscaper escaper = UnicodeEscaper.above(0); // Escape everything

        // Test various BMP ranges
        StringWriter writer = new StringWriter();

        // ASCII
        escaper.translate(65, writer); // 'A'
        assertEquals("\\u0041", writer.toString());

        // Latin-1 Supplement
        writer = new StringWriter();
        escaper.translate(0x00E9, writer); // 'é'
        assertEquals("\\u00E9", writer.toString());

        // Greek
        writer = new StringWriter();
        escaper.translate(0x03B1, writer); // 'α'
        assertEquals("\\u03B1", writer.toString());

        // Mathematical symbols
        writer = new StringWriter();
        escaper.translate(0x221E, writer); // '∞'
        assertEquals("\\u221E", writer.toString());

        // CJK
        writer = new StringWriter();
        escaper.translate(0x4E2D, writer); // '中'
        assertEquals("\\u4E2D", writer.toString());
    }

    @Test
    public void translate_shouldHandleSupplementaryCharactersCorrectly() throws IOException {
        // Test supplementary characters (> 0xFFFF) - uses toUtf16Escape
        UnicodeEscaper escaper = UnicodeEscaper.above(0);

        // Test emoji (supplementary plane)
        StringWriter writer = new StringWriter();
        boolean consumed = escaper.translate(0x1F600, writer); // 😀
        assertTrue(consumed);
        String result = writer.toString();
        assertEquals("\\u1F600", result); // Parent class toUtf16Escape format

        // Test other supplementary characters
        writer = new StringWriter();
        consumed = escaper.translate(0x10000, writer); // First supplementary
        assertTrue(consumed);
        result = writer.toString();
        assertEquals("\\u10000", result);
    }

    @Test
    public void translate_shouldRespectBetweenModeLogic() throws IOException {
        // Test the between mode logic thoroughly
        UnicodeEscaper betweenEscaper = new UnicodeEscaper(65, 90, true); // A-Z

        // Characters in range should be escaped
        StringWriter writer = new StringWriter();
        boolean consumed = betweenEscaper.translate(65, writer); // 'A'
        assertTrue(consumed);
        assertEquals("\\u0041", writer.toString());

        consumed = betweenEscaper.translate(90, writer); // 'Z'
        assertTrue(consumed);

        consumed = betweenEscaper.translate(75, writer); // 'K'
        assertTrue(consumed);

        // Characters outside range should not be escaped
        writer = new StringWriter();
        consumed = betweenEscaper.translate(64, writer); // '@' (before A)
        assertFalse(consumed);
        assertEquals("", writer.toString());

        consumed = betweenEscaper.translate(91, writer); // '[' (after Z)
        assertFalse(consumed);

        consumed = betweenEscaper.translate(97, writer); // 'a' (lowercase)
        assertFalse(consumed);
    }

    @Test
    public void translate_shouldRespectOutsideModeLogic() throws IOException {
        // Test the outside mode logic thoroughly
        UnicodeEscaper outsideEscaper = new UnicodeEscaper(65, 90, false); // outside A-Z

        // Characters outside range should be escaped
        StringWriter writer = new StringWriter();
        boolean consumed = outsideEscaper.translate(64, writer); // '@'
        assertTrue(consumed);
        assertEquals("\\u0040", writer.toString());

        consumed = outsideEscaper.translate(91, writer); // '['
        assertTrue(consumed);

        consumed = outsideEscaper.translate(97, writer); // 'a'
        assertTrue(consumed);

        // Characters in range should not be escaped
        writer = new StringWriter();
        consumed = outsideEscaper.translate(65, writer); // 'A'
        assertFalse(consumed);
        assertEquals("", writer.toString());

        consumed = outsideEscaper.translate(90, writer); // 'Z'
        assertFalse(consumed);

        consumed = outsideEscaper.translate(75, writer); // 'K'
        assertFalse(consumed);
    }

    //----------------------------------------------------------------------------------------------
    // toUtf16Escape Method Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void toUtf16Escape_shouldFormatSupplementaryCharacters() throws IOException {
        // Test the protected toUtf16Escape method through translate
        UnicodeEscaper escaper = UnicodeEscaper.above(0);

        // Supplementary characters use toUtf16Escape
        StringWriter writer = new StringWriter();
        escaper.translate(0x1F600, writer); // 😀
        assertEquals("\\u1F600", writer.toString());

        writer = new StringWriter();
        escaper.translate(0x10000, writer);
        assertEquals("\\u10000", writer.toString());

        writer = new StringWriter();
        escaper.translate(0x10FFFF, writer); // Maximum Unicode
        assertEquals("\\u10FFFF", writer.toString());
    }

    //----------------------------------------------------------------------------------------------
    // Integration and Edge Cases
    //----------------------------------------------------------------------------------------------

    @Test
    public void integration_shouldWorkWithCharSequenceTranslatorMethods() throws IOException {
        // Test integration with parent CharSequenceTranslator methods
        UnicodeEscaper escaper = UnicodeEscaper.above(127);

        // Test string translate method
        String result = escaper.translate("Café");
        assertEquals("Caf\\u00E9", result);

        // Test with writer method
        StringWriter writer = new StringWriter();
        escaper.translate("Hello 世界", writer);
        assertEquals("Hello \\u4E16\\u754C", writer.toString());

        // Test null input
        assertNull(escaper.translate(null));

        // Test empty string
        assertEquals("", escaper.translate(""));
    }

    @Test
    public void integration_shouldWorkInAggregateTranslator() throws IOException {
        // Test as component in AggregateTranslator
        UnicodeEscaper unicodeEscaper = UnicodeEscaper.above(127);

        // Create aggregate with other translators (using basic example)
        AggregateTranslator aggregate = new AggregateTranslator(unicodeEscaper);

        String result = aggregate.translate("ASCII ñoño");
        assertEquals("ASCII \\u00F1o\\u00F1o", result);
    }

    @Test
    public void boundary_shouldHandleEdgeCaseRanges() throws IOException {
        // Test boundary conditions and edge cases

        // Same start and end values
        UnicodeEscaper sameValueEscaper = new UnicodeEscaper(65, 65, true);
        StringWriter writer = new StringWriter();
        boolean consumed = sameValueEscaper.translate(65, writer);
        assertTrue(consumed); // Should escape the exact value

        // Inverted range (end < start)
        UnicodeEscaper invertedEscaper = new UnicodeEscaper(100, 50, true);
        writer = new StringWriter();
        consumed = invertedEscaper.translate(75, writer);
        assertFalse(consumed); // No valid range

        // Zero range
        UnicodeEscaper zeroEscaper = new UnicodeEscaper(0, 0, true);
        writer = new StringWriter();
        consumed = zeroEscaper.translate(0, writer);
        assertTrue(consumed);

        // Maximum values
        UnicodeEscaper maxEscaper = new UnicodeEscaper(0, Integer.MAX_VALUE, true);
        assertNotNull(maxEscaper);
    }

    @Test
    public void boundary_shouldHandleUnicodeExtremes() throws IOException {
        // Test Unicode boundary values
        UnicodeEscaper escaper = new UnicodeEscaper(0, Integer.MAX_VALUE, true); // Escape everything (inclusive)

        // Minimum Unicode
        StringWriter writer = new StringWriter();
        escaper.translate(0, writer);
        assertEquals("\\u0000", writer.toString());

        // BMP maximum
        writer = new StringWriter();
        escaper.translate(0xFFFF, writer);
        assertEquals("\\uFFFF", writer.toString());

        // First supplementary
        writer = new StringWriter();
        escaper.translate(0x10000, writer);
        assertEquals("\\u10000", writer.toString());

        // Maximum valid Unicode
        writer = new StringWriter();
        escaper.translate(0x10FFFF, writer);
        assertEquals("\\u10FFFF", writer.toString());
    }

    @Test
    public void realWorld_shouldHandleCommonUnicodeContent() {
        // Test with realistic Unicode content
        UnicodeEscaper nonAsciiEscaper = UnicodeEscaper.above(127);

        // Mixed language text
        String multilingual = "Hello 你好 Bonjour ñoño";
        String result = nonAsciiEscaper.translate(multilingual);
        assertTrue(result.contains("Hello"));
        assertTrue(result.contains("Bonjour"));
        assertTrue(result.contains("\\u"));

        // Mathematical expressions
        UnicodeEscaper mathEscaper = UnicodeEscaper.between(0x2200, 0x22FF);
        String mathText = "∀x∈ℝ: x²≥0";
        String mathResult = mathEscaper.translate(mathText);
        assertTrue(mathResult.contains("\\u"));

        // Emoji and symbols
        UnicodeEscaper emojiEscaper = UnicodeEscaper.above(0xFFFF);
        String emojiText = "Hello 😀 World 🌍";
        String emojiResult = emojiEscaper.translate(emojiText);
        assertTrue(emojiResult.contains("Hello"));
        assertTrue(emojiResult.contains("World"));
        assertTrue(emojiResult.contains("\\u"));
    }
}