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
public class CodePointTranslatorTest {

    //----------------------------------------------------------------------------------------------
    // Core Translation Logic Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void translate_shouldHandleVariousTranslationScenarios() throws IOException {
        // Group multiple translation scenarios in one test

        // 1. Translation that consumes (returns true)
        TestCodePointTranslator consumingTranslator = new TestCodePointTranslator('A', "[A]", true);
        StringWriter writer = new StringWriter();

        int consumed = consumingTranslator.translate("ABCD", 0, writer);
        assertEquals(1, consumed);
        assertEquals("[A]", writer.toString());

        // 2. Translation that doesn't consume (returns false)
        TestCodePointTranslator nonConsumingTranslator = new TestCodePointTranslator('X', "[X]", false);
        writer = new StringWriter();

        consumed = nonConsumingTranslator.translate("ABCD", 0, writer);
        assertEquals(0, consumed);
        assertEquals("", writer.toString()); // Nothing written when not consumed

        // 3. Character not handled by translator
        writer = new StringWriter();
        consumed = consumingTranslator.translate("BXYZ", 0, writer); // 'B' not handled
        assertEquals(0, consumed);
        assertEquals("", writer.toString());
    }

    @Test
    public void translate_shouldHandleUnicodeCodePoints() throws IOException {
        // Test Unicode character handling and surrogate pairs
        TestCodePointTranslator unicodeTranslator = new TestCodePointTranslator(0x1F600, "[EMOJI]", true); // Grinning face emoji

        // Test with emoji (surrogate pair)
        String emojiText = "😀ABC"; // Contains U+1F600
        StringWriter writer = new StringWriter();

        int consumed = unicodeTranslator.translate(emojiText, 0, writer);
        assertEquals(1, consumed); // Should consume 1 code point (even though it's 2 chars)
        assertEquals("[EMOJI]", writer.toString());

        // Test regular ASCII after emoji
        writer = new StringWriter();
        consumed = unicodeTranslator.translate(emojiText, 2, writer); // Index 2 = 'A'
        assertEquals(0, consumed);
        assertEquals("", writer.toString());
    }

    @Test
    public void translate_shouldHandleExceptionsFromAbstractMethod() throws IOException {
        // Test exception propagation from abstract translate method
        CodePointTranslator faultyTranslator = new CodePointTranslator() {
            @Override
            public boolean translate(int codePoint, Writer writer) throws IOException {
                throw new IOException("Code point translation error");
            }
        };

        StringWriter writer = new StringWriter();

        try {
            faultyTranslator.translate("test", 0, writer);
            fail("Expected IOException to be thrown");
        } catch (IOException e) {
            assertEquals("Code point translation error", e.getMessage());
        }
    }

    //----------------------------------------------------------------------------------------------
    // Edge Cases and Boundary Conditions
    //----------------------------------------------------------------------------------------------

    @Test
    public void translate_shouldHandleVariousEdgeCases() throws IOException {
        // Group multiple edge case scenarios
        TestCodePointTranslator translator = new TestCodePointTranslator('T', "[T]", true);

        // 1. Empty string
        StringWriter writer = new StringWriter();
        try {
            translator.translate("", 0, writer);
            fail("Expected StringIndexOutOfBoundsException for empty string");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected - Character.codePointAt throws this for empty string
        }

        // 2. Index at end of string
        writer = new StringWriter();
        try {
            translator.translate("ABC", 3, writer);
            fail("Expected StringIndexOutOfBoundsException for index beyond string");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected - Character.codePointAt throws this for invalid index
        }

        // 3. Negative index
        writer = new StringWriter();
        try {
            translator.translate("ABC", -1, writer);
            fail("Expected StringIndexOutOfBoundsException for negative index");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected - Character.codePointAt throws this for negative index
        }

        // 4. Null writer - should not crash
        int result = translator.translate("T", 0, null);
        assertEquals(1, result); // Should still return consumption count
    }

    //----------------------------------------------------------------------------------------------
    // Integration with Parent Class
    //----------------------------------------------------------------------------------------------

    @Test
    public void integration_shouldWorkWithParentClassMethods() throws IOException {
        // Test integration with CharSequenceTranslator methods
        TestCodePointTranslator translator = new TestCodePointTranslator('A', "*", true);

        // Test string translate method from parent
        assertEquals("*BC", translator.translate("ABC"));

        // Test with writer method from parent
        StringWriter writer = new StringWriter();
        translator.translate("AAABBB", writer);
        assertEquals("***BBB", writer.toString());

        // Test with() method from parent
        TestCodePointTranslator additionalTranslator = new TestCodePointTranslator('B', "#", true);
        CharSequenceTranslator composite = translator.with(additionalTranslator);

        assertEquals("*#*#*#", composite.translate("ABABAB"));
    }

    @Test
    public void integration_shouldWorkInAggregateTranslator() throws IOException {
        // Test as part of AggregateTranslator
        TestCodePointTranslator vowelTranslator = new TestCodePointTranslator('A', "[VOWEL]", true);
        TestCodePointTranslator consonantTranslator = new TestCodePointTranslator('B', "[CONSONANT]", true);

        AggregateTranslator aggregate = new AggregateTranslator(vowelTranslator, consonantTranslator);

        assertEquals("[VOWEL][CONSONANT]CD", aggregate.translate("ABCD"));

        // Test precedence - first translator wins
        TestCodePointTranslator competing1 = new TestCodePointTranslator('X', "[FIRST]", true);
        TestCodePointTranslator competing2 = new TestCodePointTranslator('X', "[SECOND]", true);

        AggregateTranslator precedenceTest = new AggregateTranslator(competing1, competing2);
        assertEquals("[FIRST]", precedenceTest.translate("X"));
    }

    //----------------------------------------------------------------------------------------------
    // Performance and Complex Scenarios
    //----------------------------------------------------------------------------------------------

    @Test
    public void complex_shouldHandleVariousRealWorldScenarios() throws IOException {
        // Test realistic translation scenarios

        // 1. HTML entity translator - create one that handles both < and >
        CodePointTranslator htmlTranslator = new CodePointTranslator() {
            @Override
            public boolean translate(int codePoint, Writer writer) throws IOException {
                if (codePoint == '<') {
                    if (writer != null) {
                        writer.write("&lt;");
                    }
                    return true;
                } else if (codePoint == '>') {
                    if (writer != null) {
                        writer.write("&gt;");
                    }
                    return true;
                }
                return false;
            }
        };
        assertEquals("&lt;div&gt;", htmlTranslator.translate("<div>"));

        // 2. Case conversion translator
        CodePointTranslator upperCaseTranslator = new CodePointTranslator() {
            @Override
            public boolean translate(int codePoint, Writer writer) throws IOException {
                if (Character.isLowerCase(codePoint)) {
                    if (writer != null) {
                        writer.write(Character.toUpperCase(codePoint));
                    }
                    return true;
                }
                return false;
            }
        };

        assertEquals("HELLO WORLD", upperCaseTranslator.translate("hello world"));

        // 3. Numeric filter translator
        CodePointTranslator digitTranslator = new CodePointTranslator() {
            @Override
            public boolean translate(int codePoint, Writer writer) throws IOException {
                if (Character.isDigit(codePoint)) {
                    if (writer != null) {
                        writer.write("[" + (char)codePoint + "]");
                    }
                    return true;
                }
                return false;
            }
        };

        assertEquals("ABC[1][2][3]XYZ", digitTranslator.translate("ABC123XYZ"));
    }

    @Test
    public void complex_shouldHandleMixedUnicodeContent() throws IOException {
        // Test with mixed Unicode content including emojis and special chars
        CodePointTranslator emojiTranslator = new CodePointTranslator() {
            @Override
            public boolean translate(int codePoint, Writer writer) throws IOException {
                // Translate emojis to [EMOJI]
                if (codePoint >= 0x1F600 && codePoint <= 0x1F64F) { // Emoticons block
                    if (writer != null) {
                        writer.write("[EMOJI]");
                    }
                    return true;
                }
                return false;
            }
        };

        String mixedText = "Hello 😀 World 😃 Test";
        String result = emojiTranslator.translate(mixedText);
        assertEquals("Hello [EMOJI] World [EMOJI] Test", result);

        // Test with null writer
        StringWriter writer = new StringWriter();
        emojiTranslator.translate("😀A😃B", writer);
        assertEquals("[EMOJI]A[EMOJI]B", writer.toString());
    }

    //----------------------------------------------------------------------------------------------
    // Helper Test Implementation
    //----------------------------------------------------------------------------------------------

    /**
     * Test implementation of CodePointTranslator for testing purposes
     */
    private static class TestCodePointTranslator extends CodePointTranslator {
        private final int targetCodePoint;
        private final String replacement;
        private final boolean shouldConsume;

        public TestCodePointTranslator(char targetChar, String replacement, boolean shouldConsume) {
            this((int) targetChar, replacement, shouldConsume); // Cast char to int
        }

        public TestCodePointTranslator(int targetCodePoint, String replacement, boolean shouldConsume) {
            this.targetCodePoint = targetCodePoint;
            this.replacement = replacement;
            this.shouldConsume = shouldConsume;
        }

        @Override
        public boolean translate(int codePoint, Writer writer) throws IOException {
            if (codePoint == targetCodePoint) {
                if (writer != null && shouldConsume) {
                    writer.write(replacement);
                }
                return shouldConsume;
            }
            return false;
        }
    }
}