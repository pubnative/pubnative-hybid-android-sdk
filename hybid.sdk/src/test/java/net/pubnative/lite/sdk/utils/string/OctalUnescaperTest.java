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
public class OctalUnescaperTest {

    //----------------------------------------------------------------------------------------------
    // Basic Octal Unescaping Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void translate_shouldHandleBasicOctalSequences() throws IOException {
        // Test basic octal escape sequences
        OctalUnescaper unescaper = new OctalUnescaper();

        // Single digit octal (0-7)
        StringWriter writer = new StringWriter();
        assertEquals(2, unescaper.translate("\\7", 0, writer)); // \7 -> 7
        assertEquals(String.valueOf((char)7), writer.toString());

        // Two digit octal
        writer = new StringWriter();
        assertEquals(3, unescaper.translate("\\77", 0, writer)); // \77 -> 63
        assertEquals(String.valueOf((char)63), writer.toString());

        // Three digit octal
        writer = new StringWriter();
        assertEquals(4, unescaper.translate("\\377", 0, writer)); // \377 -> 255
        assertEquals(String.valueOf((char)255), writer.toString());

        // Common ASCII characters
        writer = new StringWriter();
        assertEquals(4, unescaper.translate("\\101", 0, writer)); // \101 -> 'A' (65) - consumes 4 chars
        assertEquals("A", writer.toString());
    }

    @Test
    public void translate_shouldHandleOctalDigitLimitations() throws IOException {
        // Test octal digit restrictions (0-3 for first digit of 3-digit sequences)
        OctalUnescaper unescaper = new OctalUnescaper();

        // Valid 3-digit sequences (first digit 0-3)
        StringWriter writer = new StringWriter();
        assertEquals(4, unescaper.translate("\\012", 0, writer)); // \012 -> 10
        assertEquals(String.valueOf((char)10), writer.toString());

        writer = new StringWriter();
        assertEquals(4, unescaper.translate("\\377", 0, writer)); // \377 -> 255 (max valid)
        assertEquals(String.valueOf((char)255), writer.toString());

        // Invalid 3-digit sequences (first digit 4-7) - should only parse 2 digits
        writer = new StringWriter();
        assertEquals(3, unescaper.translate("\\477", 0, writer)); // \47 -> 39, last 7 ignored
        assertEquals(String.valueOf((char)39), writer.toString());

        writer = new StringWriter();
        assertEquals(3, unescaper.translate("\\567", 0, writer)); // \56 -> 46, last 7 ignored
        assertEquals(String.valueOf((char)46), writer.toString());
    }

    @Test
    public void translate_shouldHandleInvalidSequences() throws IOException {
        // Test sequences that don't qualify for octal unescaping
        OctalUnescaper unescaper = new OctalUnescaper();

        // No backslash
        StringWriter writer = new StringWriter();
        assertEquals(0, unescaper.translate("123", 0, writer));
        assertEquals("", writer.toString());

        // Backslash but no octal digit
        writer = new StringWriter();
        assertEquals(0, unescaper.translate("\\a", 0, writer));
        assertEquals("", writer.toString());

        // Backslash with invalid octal digit (8, 9)
        writer = new StringWriter();
        assertEquals(0, unescaper.translate("\\8", 0, writer));
        assertEquals("", writer.toString());

        writer = new StringWriter();
        assertEquals(0, unescaper.translate("\\9", 0, writer));
        assertEquals("", writer.toString());

        // Just backslash at end
        writer = new StringWriter();
        assertEquals(0, unescaper.translate("\\", 0, writer));
        assertEquals("", writer.toString());
    }

    //----------------------------------------------------------------------------------------------
    // Boundary and Edge Case Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void translate_shouldHandleBoundaryConditions() throws IOException {
        // Test boundary conditions and edge cases
        OctalUnescaper unescaper = new OctalUnescaper();

        // Minimum octal value
        StringWriter writer = new StringWriter();
        assertEquals(2, unescaper.translate("\\0", 0, writer)); // \0 -> null char
        assertEquals(String.valueOf((char)0), writer.toString());

        // Single digit maximum
        writer = new StringWriter();
        assertEquals(2, unescaper.translate("\\7", 0, writer)); // \7 -> 7
        assertEquals(String.valueOf((char)7), writer.toString());

        // Two digit maximum
        writer = new StringWriter();
        assertEquals(3, unescaper.translate("\\77", 0, writer)); // \77 -> 63
        assertEquals(String.valueOf((char)63), writer.toString());

        // At different positions in string
        writer = new StringWriter();
        assertEquals(3, unescaper.translate("abc\\12def", 3, writer)); // Position 3
        assertEquals(String.valueOf((char)10), writer.toString());

        // At end of string
        writer = new StringWriter();
        assertEquals(2, unescaper.translate("test\\7", 4, writer));
        assertEquals(String.valueOf((char)7), writer.toString());
    }

    @Test
    public void translate_shouldHandlePartialSequences() throws IOException {
        // Test partial sequences at string boundaries
        OctalUnescaper unescaper = new OctalUnescaper();

        // Two characters left, valid sequence
        StringWriter writer = new StringWriter();
        assertEquals(2, unescaper.translate("\\7", 0, writer));
        assertEquals(String.valueOf((char)7), writer.toString());

        // Three characters left, two-digit sequence
        writer = new StringWriter();
        assertEquals(3, unescaper.translate("\\12x", 0, writer)); // \12 -> 10, x ignored
        assertEquals(String.valueOf((char)10), writer.toString());

        // Four characters, three-digit sequence possible
        writer = new StringWriter();
        assertEquals(4, unescaper.translate("\\123x", 0, writer)); // \123 -> 83
        assertEquals(String.valueOf((char)83), writer.toString());

        // Partial at end of string
        writer = new StringWriter();
        assertEquals(3, unescaper.translate("test\\12", 4, writer));
        assertEquals(String.valueOf((char)10), writer.toString());
    }

    //----------------------------------------------------------------------------------------------
    // Integration and Complex Scenarios
    //----------------------------------------------------------------------------------------------

    @Test
    public void integration_shouldWorkWithCharSequenceTranslatorMethods() {
        // Test integration with parent CharSequenceTranslator methods
        OctalUnescaper unescaper = new OctalUnescaper();

        // Test string translate method
        String result = unescaper.translate("Hello\\40World\\41"); // \40 = space, \41 = !
        assertEquals("Hello World!", result);

        // Test with mixed content
        result = unescaper.translate("\\101\\102\\103"); // ABC
        assertEquals("ABC", result);

        // Test with no octal sequences
        result = unescaper.translate("Hello World");
        assertEquals("Hello World", result);

        // Test null input
        assertNull(unescaper.translate(null));

        // Test empty input
        assertEquals("", unescaper.translate(""));
    }

    @Test
    public void integration_shouldWorkInAggregateTranslator() {
        // Test as component in AggregateTranslator
        OctalUnescaper octalUnescaper = new OctalUnescaper();

        // Create aggregate with other unescapers
        AggregateTranslator aggregate = new AggregateTranslator(octalUnescaper);

        String result = aggregate.translate("\\101\\102\\103\\40test");
        assertEquals("ABC test", result);
    }

    @Test
    public void complex_shouldHandleRealisticScenarios() {
        // Test realistic octal unescaping scenarios
        OctalUnescaper unescaper = new OctalUnescaper();

        // Common escape sequences
        String input = "Line1\\12Line2\\12Line3"; // \12 = newline (LF)
        String result = unescaper.translate(input);
        assertEquals("Line1\nLine2\nLine3", result);

        // Mixed with regular text
        input = "Hello\\40World\\41\\40How\\40are\\40you\\77"; // spaces and question mark
        result = unescaper.translate(input);
        assertEquals("Hello World! How are you?", result);

        // Tab and other control characters
        input = "Name\\11Value\\11Description"; // \11 = tab
        result = unescaper.translate(input);
        assertEquals("Name\tValue\tDescription", result);

        // Unicode-ish values (within single byte range)
        input = "\\141\\142\\143"; // abc in octal
        result = unescaper.translate(input);
        assertEquals("abc", result);
    }

    @Test
    public void complex_shouldHandleEdgeCasesAndMixedSequences() {
        // Test complex edge cases
        OctalUnescaper unescaper = new OctalUnescaper();

        // Consecutive octal sequences
        String result = unescaper.translate("\\101\\102\\103\\104"); // ABCD
        assertEquals("ABCD", result);

        // Mixed valid and invalid sequences
        result = unescaper.translate("\\101\\800\\102"); // A + \8 (invalid) + 00 + B
        assertEquals("A\\800B", result); // \8 not processed, treated as literal

        // Octal followed by digits
        result = unescaper.translate("\\1234"); // \123 -> { (83), 4 remains
        assertEquals(String.valueOf((char)83) + "4", result);

        // Maximum values
        result = unescaper.translate("\\377\\376\\375"); // 255, 254, 253
        assertEquals(String.valueOf((char)255) + String.valueOf((char)254) + String.valueOf((char)253), result);
    }

    //----------------------------------------------------------------------------------------------
    // Return Value and Code Point Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void translate_shouldReturnCorrectCharacterCount() throws IOException {
        // Test that return value reflects consumed characters from input
        OctalUnescaper unescaper = new OctalUnescaper();

        // Single digit: \ + 1 digit = 2 chars consumed
        StringWriter writer = new StringWriter();
        assertEquals(2, unescaper.translate("\\7abc", 0, writer));

        // Two digits: \ + 2 digits = 3 chars consumed
        writer = new StringWriter();
        assertEquals(3, unescaper.translate("\\77abc", 0, writer));

        // Three digits: \ + 3 digits = 4 chars consumed
        writer = new StringWriter();
        assertEquals(4, unescaper.translate("\\377abc", 0, writer));

        // Invalid sequence: 0 chars consumed
        writer = new StringWriter();
        assertEquals(0, unescaper.translate("\\8abc", 0, writer));

        // No backslash: 0 chars consumed
        writer = new StringWriter();
        assertEquals(0, unescaper.translate("abc", 0, writer));
    }

    @Test
    public void translate_shouldHandleSpecialCharacters() throws IOException {
        // Test special control characters
        OctalUnescaper unescaper = new OctalUnescaper();

        // Bell character
        StringWriter writer = new StringWriter();
        assertEquals(2, unescaper.translate("\\7", 0, writer)); // \7 -> BEL
        assertEquals(String.valueOf((char)7), writer.toString());

        // Backspace
        writer = new StringWriter();
        assertEquals(3, unescaper.translate("\\10", 0, writer)); // \10 -> BS
        assertEquals(String.valueOf((char)8), writer.toString());

        // Form feed
        writer = new StringWriter();
        assertEquals(3, unescaper.translate("\\14", 0, writer)); // \14 -> FF
        assertEquals(String.valueOf((char)12), writer.toString());

        // Carriage return
        writer = new StringWriter();
        assertEquals(3, unescaper.translate("\\15", 0, writer)); // \15 -> CR
        assertEquals(String.valueOf((char)13), writer.toString());
    }
}