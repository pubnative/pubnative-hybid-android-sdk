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
public class JavaUnicodeEscaperTest {

    //----------------------------------------------------------------------------------------------
    // Static Factory Method Tests - Grouped by Method
    //----------------------------------------------------------------------------------------------

    @Test
    public void above_shouldCreateEscaperForCodePointsAboveThreshold() throws IOException {
        // Test above() factory method with various thresholds

        // Above ASCII range (127)
        JavaUnicodeEscaper escaper = JavaUnicodeEscaper.above(127);
        assertNotNull(escaper);

        // Test that ASCII characters are not escaped
        StringWriter writer = new StringWriter();
        int consumed = escaper.translate("ABC", 0, writer);
        assertEquals(0, consumed); // Should not consume ASCII
        assertEquals("", writer.toString());

        // Test that characters above threshold are escaped
        writer = new StringWriter();
        String unicodeText = "A\u00E9B"; // A + é + B
        consumed = escaper.translate(unicodeText, 1, writer); // Index 1 = é (233)
        assertEquals(1, consumed);
        assertTrue(writer.toString().contains("\\u"));

        // Above higher threshold
        JavaUnicodeEscaper highEscaper = JavaUnicodeEscaper.above(1000);
        assertNotNull(highEscaper);
    }

    @Test
    public void below_shouldCreateEscaperForCodePointsBelowThreshold() throws IOException {
        // Test below() factory method with various thresholds

        // Below common Unicode range
        JavaUnicodeEscaper escaper = JavaUnicodeEscaper.below(200);
        assertNotNull(escaper);

        // Test that characters below threshold are escaped
        StringWriter writer = new StringWriter();
        int consumed = escaper.translate("A", 0, writer); // A = 65, below 200
        assertEquals(1, consumed);
        assertTrue(writer.toString().contains("\\u"));

        // Test that characters above threshold are not escaped
        writer = new StringWriter();
        String unicodeText = "\u00E9"; // é = 233, above 200
        consumed = escaper.translate(unicodeText, 0, writer);
        assertEquals(0, consumed);
        assertEquals("", writer.toString());

        // Below very low threshold
        JavaUnicodeEscaper lowEscaper = JavaUnicodeEscaper.below(32);
        assertNotNull(lowEscaper);
    }

    @Test
    public void between_shouldCreateEscaperForCodePointsInRange() throws IOException {
        // Test between() factory method with various ranges

        // Between ASCII printable range (32-126)
        JavaUnicodeEscaper escaper = JavaUnicodeEscaper.between(32, 126);
        assertNotNull(escaper);

        // Test that characters in range are escaped
        StringWriter writer = new StringWriter();
        int consumed = escaper.translate("A", 0, writer); // A = 65, in range
        assertEquals(1, consumed);
        assertTrue(writer.toString().contains("\\u"));

        // Test that characters outside range are not escaped
        writer = new StringWriter();
        String unicodeText = "\u00E9"; // é = 233, outside range
        consumed = escaper.translate(unicodeText, 0, writer);
        assertEquals(0, consumed);
        assertEquals("", writer.toString());

        // Test range boundaries
        JavaUnicodeEscaper rangeEscaper = JavaUnicodeEscaper.between(100, 200);
        assertNotNull(rangeEscaper);
    }

    @Test
    public void outsideOf_shouldCreateEscaperForCodePointsOutsideRange() throws IOException {
        // Test outsideOf() factory method with various ranges

        // Outside extended ASCII range (128-255)
        JavaUnicodeEscaper escaper = JavaUnicodeEscaper.outsideOf(128, 255);
        assertNotNull(escaper);

        // Test that characters outside range are escaped
        StringWriter writer = new StringWriter();
        int consumed = escaper.translate("A", 0, writer); // A = 65, outside range
        assertEquals(1, consumed);
        assertTrue(writer.toString().contains("\\u"));

        // Test that characters inside range are not escaped
        writer = new StringWriter();
        String unicodeText = "\u00E9"; // é = 233, inside range
        consumed = escaper.translate(unicodeText, 0, writer);
        assertEquals(0, consumed);
        assertEquals("", writer.toString());

        // Test different range
        JavaUnicodeEscaper outsideEscaper = JavaUnicodeEscaper.outsideOf(50, 100);
        assertNotNull(outsideEscaper);
    }

    //----------------------------------------------------------------------------------------------
    // Constructor Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void constructor_shouldCreateEscaperWithSpecifiedParameters() throws IOException {
        // Test direct constructor with various parameter combinations

        // Between mode
        JavaUnicodeEscaper betweenEscaper = new JavaUnicodeEscaper(50, 100, true);
        assertNotNull(betweenEscaper);

        // Outside mode
        JavaUnicodeEscaper outsideEscaper = new JavaUnicodeEscaper(50, 100, false);
        assertNotNull(outsideEscaper);

        // Edge case ranges
        JavaUnicodeEscaper edgeEscaper = new JavaUnicodeEscaper(0, Integer.MAX_VALUE, true);
        assertNotNull(edgeEscaper);

        JavaUnicodeEscaper reverseEscaper = new JavaUnicodeEscaper(100, 50, false);
        assertNotNull(reverseEscaper);
    }

    //----------------------------------------------------------------------------------------------
    // toUtf16Escape Method Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void toUtf16Escape_shouldFormatBasicMultilingualPlaneCharacters() throws IOException {
        // Note: toUtf16Escape is only called for supplementary characters (> 0xFFFF)
        // BMP characters are handled directly by parent class translate method
        JavaUnicodeEscaper escaper = JavaUnicodeEscaper.above(0); // Escape everything

        // Test BMP characters - should be handled by parent, not toUtf16Escape
        StringWriter writer = new StringWriter();
        escaper.translate("A", 0, writer); // U+0041
        String result = writer.toString();
        assertEquals("\\u0041", result);

        writer = new StringWriter();
        escaper.translate("é", 0, writer); // U+00E9
        result = writer.toString();
        assertEquals("\\u00E9", result); // Parent class format for BMP
    }

    @Test
    public void toUtf16Escape_shouldFormatSupplementaryCharacters() throws IOException {
        // Test toUtf16Escape with characters that require surrogate pairs
        JavaUnicodeEscaper escaper = JavaUnicodeEscaper.above(0); // Escape everything

        // Test emoji (supplementary plane character)
        String emojiText = "😀"; // U+1F600 - requires surrogate pair
        StringWriter writer = new StringWriter();

        int consumed = escaper.translate(emojiText, 0, writer);
        assertEquals(1, consumed); // Should consume 1 code point

        String result = writer.toString();
        assertTrue(result.contains("\\u"));
        // Should contain two \\uXXXX sequences for surrogate pair
        assertTrue(result.length() >= 12); // At least \\uXXXX\\uXXXX
    }

    @Test
    public void toUtf16Escape_shouldHandleVariousUnicodeRanges() throws IOException {
        // Test escaping across different Unicode ranges
        JavaUnicodeEscaper escaper = JavaUnicodeEscaper.above(0);

        // Test Latin-1 Supplement
        StringWriter writer = new StringWriter();
        escaper.translate("©", 0, writer); // U+00A9
        String result = writer.toString();
        assertTrue(result.contains("\\u"));

        // Test Greek letters
        writer = new StringWriter();
        escaper.translate("α", 0, writer); // U+03B1
        result = writer.toString();
        assertTrue(result.contains("\\u"));

        // Test mathematical symbols
        writer = new StringWriter();
        escaper.translate("∞", 0, writer); // U+221E
        result = writer.toString();
        assertTrue(result.contains("\\u"));
    }

    //----------------------------------------------------------------------------------------------
    // Integration and Boundary Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void integration_shouldWorkWithDifferentEscapingStrategies() throws IOException {
        // Test different escaping strategies working correctly

        // Escape only control characters (below 32)
        JavaUnicodeEscaper controlEscaper = JavaUnicodeEscaper.below(32);
        String controlText = "Hello\nWorld\t!";
        String controlResult = controlEscaper.translate(controlText);
        assertTrue(controlResult.contains("\\u")); // Should escape \n and \t
        assertTrue(controlResult.contains("Hello"));
        assertTrue(controlResult.contains("World"));
        assertTrue(controlResult.contains("!"));

        // Escape only non-ASCII (above 127)
        JavaUnicodeEscaper nonAsciiEscaper = JavaUnicodeEscaper.above(127);
        String mixedText = "Café ñoño";
        String nonAsciiResult = nonAsciiEscaper.translate(mixedText);
        assertTrue(nonAsciiResult.contains("Caf"));
        assertTrue(nonAsciiResult.contains("\\u")); // Should escape é and ñ

        // Escape everything except basic ASCII letters (outside 65-90, 97-122)
        JavaUnicodeEscaper lettersOnlyEscaper = JavaUnicodeEscaper.outsideOf(65, 122);
        String lettersText = "ABC123xyz!";
        String lettersResult = lettersOnlyEscaper.translate(lettersText);
        assertTrue(lettersResult.contains("\\u")); // Should escape numbers and punctuation
    }

    @Test
    public void integration_shouldHandleComplexTextWithMixedContent() throws IOException {
        // Test with realistic mixed content
        JavaUnicodeEscaper escaper = JavaUnicodeEscaper.above(127);

        String complexText = "Hello 世界! Welcome to café München 🎉";
        String result = escaper.translate(complexText);

        // ASCII parts should remain unchanged
        assertTrue(result.contains("Hello"));
        assertTrue(result.contains("Welcome to caf"));

        // Non-ASCII parts should be escaped
        assertTrue(result.contains("\\u")); // Chinese, accented chars, emoji should be escaped
        assertTrue(result.length() > complexText.length()); // Should be longer due to escaping
    }

    @Test
    public void boundary_shouldHandleEdgeCaseRanges() throws IOException {
        // Test boundary conditions and edge cases

        // Same low and high values
        JavaUnicodeEscaper sameValueEscaper = JavaUnicodeEscaper.between(65, 65);
        StringWriter writer = new StringWriter();
        int consumed = sameValueEscaper.translate("A", 0, writer); // A = 65
        assertEquals(1, consumed); // Should escape the exact boundary

        // Zero range
        JavaUnicodeEscaper zeroEscaper = JavaUnicodeEscaper.above(0);
        assertNotNull(zeroEscaper);

        // Maximum range
        JavaUnicodeEscaper maxEscaper = JavaUnicodeEscaper.below(Integer.MAX_VALUE);
        assertNotNull(maxEscaper);

        // Inverted range (high < low)
        JavaUnicodeEscaper invertedEscaper = JavaUnicodeEscaper.between(100, 50);
        assertNotNull(invertedEscaper); // Should still create escaper
    }

    @Test
    public void boundary_shouldHandleEmptyAndNullInputs() throws IOException {
        // Test edge cases for input handling
        JavaUnicodeEscaper escaper = JavaUnicodeEscaper.above(127);

        // Empty string
        String emptyResult = escaper.translate("");
        assertNotNull(emptyResult);
        assertEquals("", emptyResult);

        // Single character
        String singleResult = escaper.translate("A");
        assertEquals("A", singleResult); // ASCII should not be escaped

        // Null input should be handled by parent class
        String nullResult = escaper.translate(null);
        assertNull(nullResult);
    }
}