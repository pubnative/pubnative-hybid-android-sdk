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
import java.io.Writer;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class CharSequenceTranslatorTest {

    //----------------------------------------------------------------------------------------------
    // Static hex() Method Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void hex_shouldConvertVariousCodePointsToUppercaseHex() {
        // Test multiple hex conversions in one test
        assertEquals("0", CharSequenceTranslator.hex(0));
        assertEquals("A", CharSequenceTranslator.hex(10));
        assertEquals("FF", CharSequenceTranslator.hex(255));
        assertEquals("100", CharSequenceTranslator.hex(256));
        assertEquals("1234", CharSequenceTranslator.hex(0x1234));
        assertEquals("ABCD", CharSequenceTranslator.hex(0xABCD));
        assertEquals("FFFF", CharSequenceTranslator.hex(65535));
    }

    //----------------------------------------------------------------------------------------------
    // String translate() Method Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void stringTranslate_shouldHandleVariousInputs() {
        // Group multiple string translation scenarios
        TestTranslator translator = new TestTranslator('A', "[A]", 1);

        // Normal translation
        assertEquals("Hello [A]nd", translator.translate("Hello And"));

        // Null input
        assertNull(translator.translate(null));

        // Empty input
        assertEquals("", translator.translate(""));

        // No matches
        assertEquals("Hello", translator.translate("Hello"));

        // Multiple matches
        assertEquals("[A]ll [A]bout", translator.translate("All About"));
    }

    @Test
    public void stringTranslate_shouldHandleIOExceptionFromTranslator() {
        // Test that IOException gets wrapped in RuntimeException
        CharSequenceTranslator faultyTranslator = new CharSequenceTranslator() {
            @Override
            public int translate(CharSequence input, int index, Writer writer) throws IOException {
                throw new IOException("Test exception");
            }
        };

        try {
            faultyTranslator.translate("test");
            fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            assertTrue(e.getCause() instanceof IOException);
            assertEquals("Test exception", e.getCause().getMessage());
        }
    }

    //----------------------------------------------------------------------------------------------
    // Writer translate() Method Tests - Core Algorithm
    //----------------------------------------------------------------------------------------------

    @Test
    public void writerTranslate_shouldHandleNullInputsGracefully() throws IOException {
        // Test null handling scenarios
        TestTranslator translator = new TestTranslator('A', "[A]", 1);
        StringWriter writer = new StringWriter();

        // Null writer - should return without error
        translator.translate("test", null);

        // Null input - should return without error
        translator.translate(null, writer);
        assertEquals("", writer.toString());

        // Both null - should return without error
        translator.translate(null, null);
    }

    @Test
    public void writerTranslate_shouldImplementCoreTranslationAlgorithm() throws IOException {
        // Test the main translation loop algorithm
        TestTranslator translator = new TestTranslator('o', "*", 1);
        StringWriter writer = new StringWriter();

        translator.translate("Hello World", writer);

        // Should replace 'o' with '*' and leave other chars unchanged
        assertEquals("Hell* W*rld", writer.toString());
    }

    @Test
    public void writerTranslate_shouldHandleZeroConsumptionAndSurrogatePairs() throws IOException {
        // Test the surrogate pair handling when translator returns 0
        CharSequenceTranslator noOpTranslator = new CharSequenceTranslator() {
            @Override
            public int translate(CharSequence input, int index, Writer writer) throws IOException {
                return 0; // Never consumes anything
            }
        };

        StringWriter writer = new StringWriter();

        // Test regular characters
        noOpTranslator.translate("ABC", writer);
        assertEquals("ABC", writer.toString());

        // Test with surrogate pairs (emoji)
        writer = new StringWriter();
        String emojiText = "A😀B"; // Contains surrogate pair
        noOpTranslator.translate(emojiText, writer);
        assertEquals("A😀B", writer.toString());
    }

    @Test
    public void writerTranslate_shouldHandleMultiCharacterConsumption() throws IOException {
        // Test translator that consumes multiple characters
        CharSequenceTranslator multiCharTranslator = new CharSequenceTranslator() {
            @Override
            public int translate(CharSequence input, int index, Writer writer) throws IOException {
                if (index < input.length() - 1 &&
                        input.charAt(index) == 'a' && input.charAt(index + 1) == 'b') {
                    writer.write("[AB]");
                    return 2; // Consume 2 characters
                }
                return 0;
            }
        };

        StringWriter writer = new StringWriter();
        multiCharTranslator.translate("xabyzab", writer);
        assertEquals("x[AB]yz[AB]", writer.toString());
    }

    //----------------------------------------------------------------------------------------------
    // with() Method Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void with_shouldCreateAggregateTranslatorWithCorrectOrder() throws IOException {
        // Test the with() method creates proper AggregateTranslator
        TestTranslator baseTranslator = new TestTranslator('A', "[A]", 1);
        TestTranslator additionalTranslator1 = new TestTranslator('B', "[B]", 1);
        TestTranslator additionalTranslator2 = new TestTranslator('C', "[C]", 1);

        CharSequenceTranslator composite = baseTranslator.with(additionalTranslator1, additionalTranslator2);

        // Should create AggregateTranslator
        assertTrue(composite instanceof AggregateTranslator);

        // Test that all translators work (each handles different characters)
        assertEquals("[A][B][C]", composite.translate("ABC"));

        // Test precedence with competing translators (same target character)
        TestTranslator firstPriority = new TestTranslator('X', "[FIRST]", 1);
        TestTranslator secondPriority = new TestTranslator('X', "[SECOND]", 1);

        CharSequenceTranslator precedenceTest = firstPriority.with(secondPriority);
        assertEquals("[FIRST]", precedenceTest.translate("X")); // First should win
    }

    @Test
    public void with_shouldHandleEmptyAndNullTranslatorArrays() throws IOException {
        // Test edge cases for with() method
        TestTranslator baseTranslator = new TestTranslator('A', "[A]", 1);

        // Empty array
        CharSequenceTranslator withEmpty = baseTranslator.with();
        assertTrue(withEmpty instanceof AggregateTranslator);
        assertEquals("[A]BC", withEmpty.translate("ABC"));

        // Array with nulls
        CharSequenceTranslator withNulls = baseTranslator.with(null, null);
        assertTrue(withNulls instanceof AggregateTranslator);
        assertEquals("[A]BC", withNulls.translate("ABC"));
    }

    //----------------------------------------------------------------------------------------------
    // Integration and Complex Scenarios
    //----------------------------------------------------------------------------------------------

    @Test
    public void integration_shouldHandleComplexTranslationScenarios() throws IOException {
        // Test complex real-world-like scenarios

        // Create a translator that handles HTML entities
        CharSequenceTranslator htmlTranslator = new CharSequenceTranslator() {
            @Override
            public int translate(CharSequence input, int index, Writer writer) throws IOException {
                if (index < input.length()) {
                    char c = input.charAt(index);
                    switch (c) {
                        case '<':
                            writer.write("&lt;");
                            return 1;
                        case '>':
                            writer.write("&gt;");
                            return 1;
                        case '&':
                            writer.write("&amp;");
                            return 1;
                    }
                }
                return 0;
            }
        };

        // Test complex HTML content
        String result = htmlTranslator.translate("<div>Hello & goodbye</div>");
        assertEquals("&lt;div&gt;Hello &amp; goodbye&lt;/div&gt;", result);

        // Test with writer
        StringWriter writer = new StringWriter();
        htmlTranslator.translate("<script>alert('test');</script>", writer);
        assertEquals("&lt;script&gt;alert('test');&lt;/script&gt;", writer.toString());
    }

    @Test
    public void integration_shouldWorkWithChainedWithCalls() {
        // Test chaining multiple with() calls
        TestTranslator baseTranslator = new TestTranslator('A', "[A]", 1);
        TestTranslator translator1 = new TestTranslator('B', "[B]", 1);
        TestTranslator translator2 = new TestTranslator('C', "[C]", 1);

        CharSequenceTranslator chained = baseTranslator
                .with(translator1)
                .with(translator2);

        assertTrue(chained instanceof AggregateTranslator);

        // All translators should work
        String result = chained.translate("ABC123");
        assertTrue(result.contains("[A]"));
        assertTrue(result.contains("[B]"));
        assertTrue(result.contains("[C]"));
        assertEquals("123", result.replaceAll("\\[[ABC]]", ""));
    }

    //----------------------------------------------------------------------------------------------
    // Edge Cases and Error Handling
    //----------------------------------------------------------------------------------------------

    @Test
    public void edgeCases_shouldHandleVariousBoundaryConditions() throws IOException {
        // Group multiple edge cases
        TestTranslator translator = new TestTranslator('X', "[X]", 1);

        // Very long string
        StringBuilder longInput = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longInput.append(i % 10 == 0 ? 'X' : 'a');
        }
        String result = translator.translate(longInput.toString());
        assertTrue(result.contains("[X]"));
        assertTrue(result.length() > longInput.length()); // Should be longer due to replacements

        // String with only target characters
        assertEquals("[X][X][X]", translator.translate("XXX"));

        // Single character
        assertEquals("[X]", translator.translate("X"));
        assertEquals("Y", translator.translate("Y"));
    }

    //----------------------------------------------------------------------------------------------
    // Static Field Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void hexDigits_shouldContainCorrectDigits() {
        // Test the static HEX_DIGITS array
        char[] expected = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        assertArrayEquals(CharSequenceTranslator.HEX_DIGITS, expected);
        assertEquals(16, CharSequenceTranslator.HEX_DIGITS.length);
    }

    //----------------------------------------------------------------------------------------------
    // Helper Test Translator Class
    //----------------------------------------------------------------------------------------------

    /**
     * Test translator that replaces a specific character with a replacement string
     */
    private static class TestTranslator extends CharSequenceTranslator {
        private final char targetChar;
        private final String replacement;
        private final int consumeCount;

        public TestTranslator(char targetChar, String replacement, int consumeCount) {
            this.targetChar = targetChar;
            this.replacement = replacement;
            this.consumeCount = consumeCount;
        }

        @Override
        public int translate(CharSequence input, int index, Writer writer) throws IOException {
            if (index < input.length() && input.charAt(index) == targetChar) {
                if (writer != null) {
                    writer.write(replacement);
                }
                return consumeCount;
            }
            return 0;
        }
    }
}