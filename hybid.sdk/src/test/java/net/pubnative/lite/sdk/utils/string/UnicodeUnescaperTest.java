// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.string;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import net.pubnative.lite.sdk.HyBid;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.io.StringWriter;

@RunWith(MockitoJUnitRunner.class)
public class UnicodeUnescaperTest {

    private UnicodeUnescaper unescaper;
    private StringWriter writer;

    @Before
    public void setUp() {
        unescaper = new UnicodeUnescaper();
        writer = new StringWriter();
    }

    @Test
    public void translate_withBasicUnicodeEscape_returnsCorrectCharacterAndLength() throws IOException {
        // Test basic backslash-u escape sequence
        String input = "\\u0041"; // Should translate to 'A'

        int result = unescaper.translate(input, 0, writer);

        assertEquals(6, result); // backslash-u + 4 hex digits = 6 characters consumed
        assertEquals("A", writer.toString());
    }

    @Test
    public void translate_withMultipleUChars_consumesAllUsAndReturnsCorrectLength() throws IOException {
        // Test multiple u chars: backslash-uuu0042
        String input = "\\uuu0042"; // Should translate to 'B'

        int result = unescaper.translate(input, 0, writer);

        assertEquals(8, result); // backslash-uuu + 4 hex digits = 8 characters consumed
        assertEquals("B", writer.toString());
    }

    @Test
    public void translate_withPlusSign_consumesPlusAndReturnsCorrectLength() throws IOException {
        // Test with plus sign: backslash-u+0043
        String input = "\\u+0043"; // Should translate to 'C'

        int result = unescaper.translate(input, 0, writer);

        assertEquals(7, result); // backslash-u+ + 4 hex digits = 7 characters consumed
        assertEquals("C", writer.toString());
    }

    @Test
    public void translate_withMultipleUCharsAndPlus_handlesComplexFormat() throws IOException {
        // Test complex format: backslash-uuu+0044
        String input = "\\uuu+0044"; // Should translate to 'D'

        int result = unescaper.translate(input, 0, writer);

        assertEquals(9, result); // backslash-uuu+ + 4 hex digits = 9 characters consumed
        assertEquals("D", writer.toString());
    }

    @Test
    public void translate_withHexadecimalValues_handlesAllValidHexDigits() throws IOException {
        // Test various hex values including A-F
        String input1 = "\\u00FF"; // 255 in hex
        String input2 = "\\u00AB"; // Mixed hex digits

        int result1 = unescaper.translate(input1, 0, writer);
        assertEquals(6, result1);
        assertEquals("\u00FF", writer.toString());

        writer = new StringWriter(); // Reset writer
        int result2 = unescaper.translate(input2, 0, writer);
        assertEquals(6, result2);
        assertEquals("\u00AB", writer.toString());
    }

    @Test
    public void translate_withLowercaseHex_parsesCorrectly() throws IOException {
        // Test lowercase hex digits
        String input = "\\u00af"; // Lowercase hex

        int result = unescaper.translate(input, 0, writer);

        assertEquals(6, result);
        assertEquals("\u00af", writer.toString());
    }

    @Test
    public void translate_withNonBackslashStart_returnsZero() throws IOException {
        // Test input that doesn't start with backslash
        String input = "u0041";

        int result = unescaper.translate(input, 0, writer);

        assertEquals(0, result);
        assertEquals("", writer.toString());
    }

    @Test
    public void translate_withBackslashButNotU_returnsZero() throws IOException {
        // Test backslash followed by non-u character
        String input = "\\n0041";

        int result = unescaper.translate(input, 0, writer);

        assertEquals(0, result);
        assertEquals("", writer.toString());
    }

    @Test
    public void translate_atEndOfString_returnsZero() throws IOException {
        // Test when index is at end of string
        String input = "\\u0041";

        int result = unescaper.translate(input, 5, writer); // Index beyond last valid position

        assertEquals(0, result);
        assertEquals("", writer.toString());
    }

    @Test
    public void translate_withBackslashAtEnd_returnsZero() throws IOException {
        // Test when backslash is at the very end
        String input = "test\\";

        int result = unescaper.translate(input, 4, writer);

        assertEquals(0, result);
        assertEquals("", writer.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void translate_withInsufficientHexDigits_throwsIllegalArgumentException() throws IOException {
        // Test with less than 4 hex digits
        String input = "\\u00A";

        unescaper.translate(input, 0, writer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void translate_withInsufficientHexDigitsAfterPlus_throwsException() throws IOException {
        // Test with plus but insufficient hex digits
        String input = "\\u+00A";

        unescaper.translate(input, 0, writer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void translate_withInsufficientHexDigitsAfterMultipleUs_throwsException() throws IOException {
        // Test with multiple us but insufficient hex digits
        String input = "\\uuu00A";

        unescaper.translate(input, 0, writer);
    }

    @Test
    public void translate_withInvalidHexCharacters_throwsIllegalArgumentException() throws IOException {
        // Test with invalid hex characters - should trigger NumberFormatException -> IllegalArgumentException
        String input = "\\u00XY";

        try (MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class)) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                unescaper.translate(input, 0, writer);
            });

            assertTrue(exception.getMessage().contains("Unable to parse unicode value"));
            assertTrue(exception.getMessage().contains("00XY"));
            assertNotNull(exception.getCause());
            assertTrue(exception.getCause() instanceof NumberFormatException);

            // Verify HyBid.reportException was called with the NumberFormatException
            mockedHyBid.verify(() -> HyBid.reportException(any(NumberFormatException.class)));
        }
    }

    @Test
    public void translate_withNonHexCharactersAfterValidStart_reportsExceptionAndThrows() throws IOException {
        // Test invalid hex with different invalid characters
        String input = "\\u0GHI";

        try (MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class)) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                unescaper.translate(input, 0, writer);
            });

            assertTrue(exception.getMessage().contains("Unable to parse unicode value"));
            assertTrue(exception.getMessage().contains("0GHI"));

            // Verify exception reporting
            mockedHyBid.verify(() -> HyBid.reportException(any(NumberFormatException.class)));
        }
    }

    @Test
    public void translate_boundaryConditions_handlesEdgeCases() throws IOException {
        // Test boundary hex values
        String input1 = "\\u0000"; // Minimum value
        String input2 = "\\uFFFF"; // Maximum 4-digit hex value

        // Test minimum value
        int result1 = unescaper.translate(input1, 0, writer);
        assertEquals(6, result1);
        assertEquals("\u0000", writer.toString());

        // Test maximum value
        writer = new StringWriter();
        int result2 = unescaper.translate(input2, 0, writer);
        assertEquals(6, result2);
        assertEquals("\uFFFF", writer.toString());
    }

    @Test
    public void translate_withIndexInMiddleOfString_translatesFromCorrectPosition() throws IOException {
        // Test translation starting from a specific index
        String input = "abc\\u0045def"; // Contains unicode escape that should translate to 'E'

        int result = unescaper.translate(input, 3, writer);

        assertEquals(6, result);
        assertEquals("E", writer.toString());
    }

    @Test
    public void translate_errorMessages_containHelpfulInformation() throws IOException {
        // Test that error messages contain the problematic input
        String input = "\\u00"; // Too short

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            unescaper.translate(input, 0, writer);
        });

        assertTrue(exception.getMessage().contains("Less than 4 hex digits"));
        assertTrue(exception.getMessage().contains("\\u00"));
        assertTrue(exception.getMessage().contains("due to end of CharSequence"));
    }

    @Test
    public void translate_integrationWithParentClass_worksWithStringTranslateMethod() {
        // Test integration with parent class translate(CharSequence) method
        String input = "Hello \\u0057orld"; // Unicode escape should become 'W'

        String result = unescaper.translate(input);

        assertEquals("Hello World", result);
    }

    @Test
    public void translate_withComplexString_handlesMultipleEscapes() {
        // Test multiple unicode escapes in one string
        String input = "\\u0048\\u0065\\u006C\\u006C\\u006F"; // "Hello" in unicode

        String result = unescaper.translate(input);

        assertEquals("Hello", result);
    }

    @Test
    public void translate_withMixedContent_preservesNonEscapeSequences() {
        // Test mixed content with regular chars and unicode escapes
        String input = "Start \\u0041\\u0042 End"; // Should become "Start AB End"

        String result = unescaper.translate(input);

        assertEquals("Start AB End", result);
    }
}