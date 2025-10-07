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
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class AggregateTranslatorTest {

    //----------------------------------------------------------------------------------------------
    // Constructor Tests - Grouped Edge Cases
    //----------------------------------------------------------------------------------------------

    @Test
    public void constructor_shouldHandleVariousInputCombinations() throws IOException {
        // Test multiple constructor scenarios in one test

        // 1. Empty constructor (no translators)
        AggregateTranslator emptyTranslator = new AggregateTranslator();
        assertEquals(0, emptyTranslator.translate("test", 0, new StringWriter()));

        // 2. Null array
        AggregateTranslator nullArrayTranslator = new AggregateTranslator((CharSequenceTranslator[]) null);
        assertEquals(0, nullArrayTranslator.translate("test", 0, new StringWriter()));

        // 3. Array with null elements - should filter them out
        CharSequenceTranslator mockTranslator = createMockTranslator(2);
        AggregateTranslator mixedTranslator = new AggregateTranslator(null, mockTranslator, null);
        assertEquals(2, mixedTranslator.translate("test", 0, new StringWriter()));

        // 4. All null elements
        AggregateTranslator allNullTranslator = new AggregateTranslator(null, null, null);
        assertEquals(0, allNullTranslator.translate("test", 0, new StringWriter()));
    }

    //----------------------------------------------------------------------------------------------
    // Translate Method Tests - Grouped Scenarios
    //----------------------------------------------------------------------------------------------

    @Test
    public void translate_shouldImplementFirstWinsStrategy() throws IOException {
        // Test the "first translator to consume wins" behavior
        StringWriter writer = new StringWriter();
        String input = "testInput";
        int index = 0;

        // Create mock translators with different consumption behaviors
        CharSequenceTranslator noConsume1 = createMockTranslator(0); // consumes nothing
        CharSequenceTranslator noConsume2 = createMockTranslator(0); // consumes nothing
        CharSequenceTranslator winner = createMockTranslator(3);      // consumes 3 chars
        CharSequenceTranslator notCalled = createMockTranslator(5);   // should not be called

        AggregateTranslator translator = new AggregateTranslator(noConsume1, noConsume2, winner, notCalled);

        int result = translator.translate(input, index, writer);

        // Verify first wins strategy
        assertEquals(3, result);
        verify(noConsume1, times(1)).translate(input, index, writer);
        verify(noConsume2, times(1)).translate(input, index, writer);
        verify(winner, times(1)).translate(input, index, writer);
        verify(notCalled, never()).translate(any(), anyInt(), any()); // Should not be called
    }

    @Test
    public void translate_shouldHandleAllTranslatorsReturningZero() throws IOException {
        // Test when all translators return 0 (no consumption)
        StringWriter writer = new StringWriter();
        String input = "testInput";

        CharSequenceTranslator translator1 = createMockTranslator(0);
        CharSequenceTranslator translator2 = createMockTranslator(0);
        CharSequenceTranslator translator3 = createMockTranslator(0);

        AggregateTranslator aggregateTranslator = new AggregateTranslator(translator1, translator2, translator3);

        int result = aggregateTranslator.translate(input, 0, writer);

        assertEquals(0, result);
        // Verify all translators were called
        verify(translator1, times(1)).translate(input, 0, writer);
        verify(translator2, times(1)).translate(input, 0, writer);
        verify(translator3, times(1)).translate(input, 0, writer);
    }

    @Test
    public void translate_shouldHandleExceptionsFromTranslators() throws IOException {
        // Test exception handling from individual translators
        StringWriter writer = new StringWriter();
        String input = "testInput";

        CharSequenceTranslator faultyTranslator = new CharSequenceTranslator() {
            @Override
            public int translate(CharSequence input, int index, Writer writer) throws IOException {
                throw new IOException("Translator error");
            }
        };

        AggregateTranslator aggregateTranslator = new AggregateTranslator(faultyTranslator);

        try {
            aggregateTranslator.translate(input, 0, writer);
            fail("Expected IOException to be thrown");
        } catch (IOException e) {
            assertEquals("Translator error", e.getMessage());
        }
    }

    //----------------------------------------------------------------------------------------------
    // Integration Tests - Real Usage Scenarios
    //----------------------------------------------------------------------------------------------

    @Test
    public void integration_shouldWorkWithMultipleTranslatorTypes() throws IOException {
        // Test with different types of translators working together
        StringWriter writer = new StringWriter();
        String input = "Hello & World";

        // Create translators for different scenarios
        CharSequenceTranslator spaceTranslator = createConditionalTranslator(' ', "_SPACE_", 1);
        CharSequenceTranslator ampersandTranslator = createConditionalTranslator('&', "_AND_", 1);
        CharSequenceTranslator defaultTranslator = createMockTranslator(0); // Doesn't consume anything

        AggregateTranslator aggregateTranslator = new AggregateTranslator(
                spaceTranslator, ampersandTranslator, defaultTranslator
        );

        // Test space character translation
        int spaceResult = aggregateTranslator.translate(input, 5, writer); // index 5 is space
        assertEquals(1, spaceResult);

        // Test ampersand character translation
        writer = new StringWriter();
        int ampResult = aggregateTranslator.translate(input, 6, writer); // index 6 is &
        assertEquals(1, ampResult);

        // Test character that no translator handles
        writer = new StringWriter();
        int noMatchResult = aggregateTranslator.translate(input, 0, writer); // index 0 is 'H'
        assertEquals(0, noMatchResult);
    }

    @Test
    public void integration_shouldMaintainTranslatorOrder() throws IOException {
        // Test that translator order matters for precedence
        StringWriter writer = new StringWriter();
        String input = "test";

        CharSequenceTranslator highPriority = createMockTranslator(2); // Returns 2
        CharSequenceTranslator lowPriority = createMockTranslator(3);  // Returns 3, but won't be reached

        // Test order 1: high priority first
        AggregateTranslator translator1 = new AggregateTranslator(highPriority, lowPriority);
        assertEquals(2, translator1.translate(input, 0, writer));

        // Reset mocks
        reset(highPriority, lowPriority);
        when(highPriority.translate(any(), anyInt(), any())).thenReturn(2);
        when(lowPriority.translate(any(), anyInt(), any())).thenReturn(3);

        // Test order 2: low priority first
        AggregateTranslator translator2 = new AggregateTranslator(lowPriority, highPriority);
        assertEquals(3, translator2.translate(input, 0, writer)); // Should return 3 now

        verify(highPriority, never()).translate(any(), anyInt(), any()); // Should not be called
    }

    @Test
    public void integration_shouldWorkWithCharSequenceTranslatorWith() throws IOException {
        // Test integration with the parent class 'with' method
        CharSequenceTranslator baseTranslator = createConditionalTranslator('A', "[A]", 1);
        CharSequenceTranslator additionalTranslator = createConditionalTranslator('B', "[B]", 1);

        // Create composite using the 'with' method
        CharSequenceTranslator composite = baseTranslator.with(additionalTranslator);

        // Test that the composite is an AggregateTranslator
        assertTrue(composite instanceof AggregateTranslator);

        // Test translation behavior
        StringWriter writer = new StringWriter();
        assertEquals(1, composite.translate("ABC", 0, writer)); // Should translate 'A'

        writer = new StringWriter();
        assertEquals(1, composite.translate("ABC", 1, writer)); // Should translate 'B'

        writer = new StringWriter();
        assertEquals(0, composite.translate("ABC", 2, writer)); // 'C' not handled
    }

    @Test
    public void integration_shouldWorkWithStringTranslateMethod() {
        // Test integration with the parent class string translate method
        CharSequenceTranslator spaceReplacer = createConditionalTranslator(' ', "_", 1);
        AggregateTranslator translator = new AggregateTranslator(spaceReplacer);

        // Test the convenience string method from parent class
        String result = translator.translate("Hello World");
        assertEquals("Hello_World", result);

        // Test with null input
        assertNull(translator.translate(null));

        // Test with empty input
        assertEquals("", translator.translate(""));
    }

    @Test
    public void edgeCases_shouldHandleVariousInputConditions() throws IOException {
        // Group multiple edge case scenarios
        CharSequenceTranslator mockTranslator = createMockTranslator(1);
        AggregateTranslator translator = new AggregateTranslator(mockTranslator);
        StringWriter writer = new StringWriter();

        // Test empty input
        assertEquals(1, translator.translate("", 0, writer));

        // Test null writer (should propagate to individual translator)
        try {
            translator.translate("test", 0, null);
            // If no exception, the mock handled it
        } catch (Exception e) {
            // Expected if mock doesn't handle null writer
            assertTrue(e instanceof NullPointerException || e instanceof IOException);
        }

        // Test negative index
        assertEquals(1, translator.translate("test", -1, writer));

        // Test index beyond string length
        assertEquals(1, translator.translate("test", 10, writer));
    }

    //----------------------------------------------------------------------------------------------
    // Helper Methods
    //----------------------------------------------------------------------------------------------

    /**
     * Creates a mock translator that returns the specified consumption count
     */
    private CharSequenceTranslator createMockTranslator(int consumeCount) {
        CharSequenceTranslator mock = mock(CharSequenceTranslator.class);
        try {
            when(mock.translate(any(CharSequence.class), anyInt(), any(Writer.class))).thenReturn(consumeCount);
        } catch (IOException e) {
            throw new RuntimeException("Failed to setup mock", e);
        }
        return mock;
    }

    /**
     * Creates a translator that handles a specific character
     */
    private CharSequenceTranslator createConditionalTranslator(char targetChar, String replacement, int consumeCount) {
        return new CharSequenceTranslator() {
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
        };
    }
}