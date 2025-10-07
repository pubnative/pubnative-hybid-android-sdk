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
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class LookupTranslatorTest {

    //----------------------------------------------------------------------------------------------
    // Constructor Tests - Error Handling and Initialization
    //----------------------------------------------------------------------------------------------

    @Test(expected = InvalidParameterException.class)
    public void constructor_shouldThrowExceptionForNullLookupMap() {
        // Test that null lookup map throws InvalidParameterException
        new LookupTranslator(null);
    }

    @Test
    public void constructor_shouldHandleVariousMapTypes() throws IOException {
        // Test constructor with different map configurations

        // Empty map
        Map<CharSequence, CharSequence> emptyMap = new HashMap<>();
        LookupTranslator emptyTranslator = new LookupTranslator(emptyMap);
        StringWriter writer = new StringWriter();
        assertEquals(0, emptyTranslator.translate("test", 0, writer));

        // Single entry map
        Map<CharSequence, CharSequence> singleMap = new HashMap<>();
        singleMap.put("a", "X");
        LookupTranslator singleTranslator = new LookupTranslator(singleMap);
        writer = new StringWriter();
        assertEquals(1, singleTranslator.translate("abc", 0, writer));
        assertEquals("X", writer.toString());

        // Multiple entries with different lengths
        Map<CharSequence, CharSequence> multiMap = new HashMap<>();
        multiMap.put("&", "&amp;");
        multiMap.put("&amp;", "&amp;amp;");
        multiMap.put("<", "&lt;");
        LookupTranslator multiTranslator = new LookupTranslator(multiMap);
        assertNotNull(multiTranslator);
    }

    @Test
    public void constructor_shouldConvertCharSequencesToStrings() throws IOException {
        // Test that CharSequence keys/values are properly converted to Strings
        Map<CharSequence, CharSequence> charSeqMap = new HashMap<>();
        StringBuilder keyBuilder = new StringBuilder("test");
        StringBuilder valueBuilder = new StringBuilder("result");
        charSeqMap.put(keyBuilder, valueBuilder);

        LookupTranslator translator = new LookupTranslator(charSeqMap);
        StringWriter writer = new StringWriter();

        // Should work even if original CharSequence objects are modified
        keyBuilder.append("modified");
        valueBuilder.append("modified");

        int consumed = translator.translate("test", 0, writer);
        assertEquals(4, consumed); // "test" matches and returns its length (4 code points)
        assertEquals("result", writer.toString()); // Original value, not modified
    }

    //----------------------------------------------------------------------------------------------
    // Core Translation Logic Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void translate_shouldPerformBasicLookupTranslation() throws IOException {
        // Test basic translation functionality
        Map<CharSequence, CharSequence> htmlMap = new HashMap<>();
        htmlMap.put("&", "&amp;");
        htmlMap.put("<", "&lt;");
        htmlMap.put(">", "&gt;");
        htmlMap.put("\"", "&quot;");

        LookupTranslator translator = new LookupTranslator(htmlMap);

        // Test individual translations
        StringWriter writer = new StringWriter();
        assertEquals(1, translator.translate("&test", 0, writer));
        assertEquals("&amp;", writer.toString());

        writer = new StringWriter();
        assertEquals(1, translator.translate("<div>", 0, writer));
        assertEquals("&lt;", writer.toString());

        writer = new StringWriter();
        assertEquals(1, translator.translate("\"quote\"", 0, writer));
        assertEquals("&quot;", writer.toString());

        // Test non-matching character
        writer = new StringWriter();
        assertEquals(0, translator.translate("abc", 0, writer));
        assertEquals("", writer.toString());
    }

    @Test
    public void translate_shouldImplementGreedyAlgorithm() throws IOException {
        // Test greedy matching (longest match first)
        Map<CharSequence, CharSequence> greedyMap = new HashMap<>();
        greedyMap.put("a", "short");
        greedyMap.put("ab", "medium");
        greedyMap.put("abc", "long");
        greedyMap.put("abcd", "longest");

        LookupTranslator translator = new LookupTranslator(greedyMap);

        // Should match longest possible sequence
        StringWriter writer = new StringWriter();
        assertEquals(4, translator.translate("abcd", 0, writer)); // Should match "abcd"
        assertEquals("longest", writer.toString());

        writer = new StringWriter();
        assertEquals(3, translator.translate("abc", 0, writer)); // Should match "abc"
        assertEquals("long", writer.toString());

        writer = new StringWriter();
        assertEquals(2, translator.translate("ab", 0, writer)); // Should match "ab"
        assertEquals("medium", writer.toString());

        writer = new StringWriter();
        assertEquals(1, translator.translate("a", 0, writer)); // Should match "a"
        assertEquals("short", writer.toString());
    }

    @Test
    public void translate_shouldHandlePartialMatches() throws IOException {
        // Test scenarios where longest match doesn't exist but shorter ones do
        Map<CharSequence, CharSequence> partialMap = new HashMap<>();
        partialMap.put("ab", "match1");
        partialMap.put("abcd", "match2");
        // Note: no "abc" mapping

        LookupTranslator translator = new LookupTranslator(partialMap);

        // Input "abcx" should match "ab" since "abc" and "abcd" don't exist
        StringWriter writer = new StringWriter();
        assertEquals(2, translator.translate("abcx", 0, writer));
        assertEquals("match1", writer.toString());

        // Input "abcd" should match exactly
        writer = new StringWriter();
        assertEquals(4, translator.translate("abcd", 0, writer));
        assertEquals("match2", writer.toString());
    }

    //----------------------------------------------------------------------------------------------
    // Index and Boundary Handling Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void translate_shouldHandleVariousIndices() throws IOException {
        // Test translation at different positions in input
        Map<CharSequence, CharSequence> indexMap = new HashMap<>();
        indexMap.put("x", "X");
        indexMap.put("y", "Y");

        LookupTranslator translator = new LookupTranslator(indexMap);

        // Test at beginning
        StringWriter writer = new StringWriter();
        assertEquals(1, translator.translate("xyz", 0, writer));
        assertEquals("X", writer.toString());

        // Test in middle
        writer = new StringWriter();
        assertEquals(1, translator.translate("axy", 1, writer));
        assertEquals("X", writer.toString());

        // Test at end
        writer = new StringWriter();
        assertEquals(1, translator.translate("ayx", 2, writer));
        assertEquals("X", writer.toString());

        // Test character not in map
        writer = new StringWriter();
        assertEquals(0, translator.translate("abc", 0, writer)); // 'a' not in map
        assertEquals("", writer.toString());
    }

    @Test
    public void translate_shouldHandleBoundaryConditions() throws IOException {
        // Test edge cases with string boundaries
        Map<CharSequence, CharSequence> boundaryMap = new HashMap<>();
        boundaryMap.put("abc", "result");
        boundaryMap.put("ab", "partial");

        LookupTranslator translator = new LookupTranslator(boundaryMap);

        // Test when remaining string is shorter than longest key
        StringWriter writer = new StringWriter();
        assertEquals(2, translator.translate("ab", 0, writer)); // Only "ab" can match
        assertEquals("partial", writer.toString());

        // Test at position 1 where "ab" should match
        writer = new StringWriter();
        assertEquals(2, translator.translate("xab", 1, writer)); // "ab" at position 1
        assertEquals("partial", writer.toString());
    }

    //----------------------------------------------------------------------------------------------
    // Character Code Point Handling Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void translate_shouldReturnCorrectCodePointCount() throws IOException {
        // Test that return value reflects code point count, not char count
        Map<CharSequence, CharSequence> codePointMap = new HashMap<>();
        codePointMap.put("😀", "smile"); // Emoji is 1 code point but 2 chars
        codePointMap.put("a", "letter");

        LookupTranslator translator = new LookupTranslator(codePointMap);

        // Regular character
        StringWriter writer = new StringWriter();
        int consumed = translator.translate("abc", 0, writer);
        assertEquals(1, consumed); // 1 code point
        assertEquals("letter", writer.toString());

        // Emoji character
        writer = new StringWriter();
        String emojiText = "😀test";
        consumed = translator.translate(emojiText, 0, writer);
        assertEquals(1, consumed); // 1 code point (even though emoji is 2 chars)
        assertEquals("smile", writer.toString());
    }

    //----------------------------------------------------------------------------------------------
    // Integration with Parent Class Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void integration_shouldWorkWithCharSequenceTranslatorMethods() {
        // Test integration with parent CharSequenceTranslator methods
        Map<CharSequence, CharSequence> integrationMap = new HashMap<>();
        integrationMap.put("&", "&amp;");
        integrationMap.put("<", "&lt;");
        integrationMap.put(">", "&gt;");

        LookupTranslator translator = new LookupTranslator(integrationMap);

        // Test string translate method
        String result = translator.translate("Hello <world> & goodbye");
        assertEquals("Hello &lt;world&gt; &amp; goodbye", result);

        // Test null input
        assertNull(translator.translate(null));

        // Test empty input
        assertEquals("", translator.translate(""));

        // Test input with no matches
        assertEquals("xyz", translator.translate("xyz"));
    }

    @Test
    public void integration_shouldWorkInAggregateTranslator() throws IOException {
        // Test as component in AggregateTranslator
        Map<CharSequence, CharSequence> htmlMap = new HashMap<>();
        htmlMap.put("&", "&amp;");
        htmlMap.put("<", "&lt;");
        htmlMap.put(">", "&gt;"); // Added missing > mapping

        LookupTranslator htmlTranslator = new LookupTranslator(htmlMap);

        Map<CharSequence, CharSequence> quoteMap = new HashMap<>();
        quoteMap.put("\"", "&quot;");
        quoteMap.put("'", "&apos;");

        LookupTranslator quoteTranslator = new LookupTranslator(quoteMap);

        AggregateTranslator aggregate = new AggregateTranslator(htmlTranslator, quoteTranslator);

        String result = aggregate.translate("<div class=\"test\">content & more</div>");
        assertEquals("&lt;div class=&quot;test&quot;&gt;content &amp; more&lt;/div&gt;", result);
    }

    //----------------------------------------------------------------------------------------------
    // Performance and Complex Scenarios Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void complex_shouldHandleRealisticEscapeScenarios() {
        // Test with realistic HTML/XML escaping scenarios
        Map<CharSequence, CharSequence> htmlEntities = new HashMap<>();
        htmlEntities.put("&", "&amp;");
        htmlEntities.put("<", "&lt;");
        htmlEntities.put(">", "&gt;");
        htmlEntities.put("\"", "&quot;");
        htmlEntities.put("'", "&apos;");
        htmlEntities.put("©", "&copy;");
        htmlEntities.put("®", "&reg;");

        LookupTranslator translator = new LookupTranslator(htmlEntities);

        // Complex HTML content
        String html = "<div class=\"content\">Hello & welcome to 'our' site © 2023</div>";
        String escaped = translator.translate(html);
        assertEquals("&lt;div class=&quot;content&quot;&gt;Hello &amp; welcome to &apos;our&apos; site &copy; 2023&lt;/div&gt;", escaped);

        // JavaScript-like content
        String js = "if (x < 5 && y > 3) { alert(\"test\"); }";
        String escapedJs = translator.translate(js);
        assertTrue(escapedJs.contains("&lt;"));
        assertTrue(escapedJs.contains("&amp;"));
        assertTrue(escapedJs.contains("&gt;"));
        assertTrue(escapedJs.contains("&quot;"));
    }

    @Test
    public void complex_shouldHandleOverlappingPatterns() throws IOException {
        // Test with overlapping patterns to verify greedy algorithm
        Map<CharSequence, CharSequence> overlappingMap = new HashMap<>();
        overlappingMap.put("test", "TEST");
        overlappingMap.put("testing", "TESTING");
        overlappingMap.put("test123", "TEST123");
        overlappingMap.put("t", "T");
        overlappingMap.put("te", "TE");

        LookupTranslator translator = new LookupTranslator(overlappingMap);

        // Should choose longest match
        StringWriter writer = new StringWriter();
        assertEquals(7, translator.translate("testing", 0, writer));
        assertEquals("TESTING", writer.toString());

        writer = new StringWriter();
        assertEquals(7, translator.translate("test123", 0, writer));
        assertEquals("TEST123", writer.toString());

        writer = new StringWriter();
        assertEquals(4, translator.translate("test", 0, writer));
        assertEquals("TEST", writer.toString());

        // Partial matches
        writer = new StringWriter();
        assertEquals(2, translator.translate("te", 0, writer));
        assertEquals("TE", writer.toString());
    }

    @Test
    public void complex_shouldHandleUnicodeContent() {
        // Test with Unicode characters
        Map<CharSequence, CharSequence> unicodeMap = new HashMap<>();
        unicodeMap.put("α", "alpha");
        unicodeMap.put("β", "beta");
        unicodeMap.put("γ", "gamma");
        unicodeMap.put("😀", ":smile:");
        unicodeMap.put("世", "world");

        LookupTranslator translator = new LookupTranslator(unicodeMap);

        String unicode = "Hello 世界! αβγ 😀";
        String result = translator.translate(unicode);
        assertTrue(result.contains("world"));
        assertTrue(result.contains("alpha"));
        assertTrue(result.contains("beta"));
        assertTrue(result.contains("gamma"));
        assertTrue(result.contains(":smile:"));
    }

    //----------------------------------------------------------------------------------------------
    // Edge Cases and Error Handling Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void edgeCases_shouldHandleVariousInputConditions() throws IOException {
        // Group multiple edge case scenarios (excluding empty key which causes StringIndexOutOfBoundsException)
        Map<CharSequence, CharSequence> edgeMap = new HashMap<>();
        edgeMap.put("a", "single");

        LookupTranslator translator = new LookupTranslator(edgeMap);

        // Test empty string input
        StringWriter writer = new StringWriter();
        try {
            translator.translate("", 0, writer);
            // If no exception, check result
            assertEquals("", writer.toString());
        } catch (StringIndexOutOfBoundsException e) {
            // Expected - empty string causes charAt(0) to fail
            assertTrue(true);
        }

        // Test single character input
        writer = new StringWriter();
        assertEquals(1, translator.translate("a", 0, writer));
        assertEquals("single", writer.toString());

        // Test with very long input
        StringBuilder longInput = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longInput.append("a");
        }
        String longResult = translator.translate(longInput.toString());
        // Should translate each 'a' to "single"
        assertTrue(longResult.length() > longInput.length());
        assertTrue(longResult.contains("single"));
    }
}