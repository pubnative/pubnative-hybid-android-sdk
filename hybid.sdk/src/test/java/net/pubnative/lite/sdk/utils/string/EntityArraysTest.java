// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.string;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class EntityArraysTest {

    //----------------------------------------------------------------------------------------------
    // Constructor and Class Structure Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void constructor_shouldBePrivateAndThrowException() throws Exception {
        // Test that constructor is private and cannot be instantiated
        Constructor<EntityArrays> constructor = EntityArrays.class.getDeclaredConstructor();
        assertTrue("Constructor should be private", Modifier.isPrivate(constructor.getModifiers()));

        constructor.setAccessible(true);
        // Should be able to create instance via reflection (utility class pattern)
        EntityArrays instance = constructor.newInstance();
        assertNotNull(instance);
    }

    //----------------------------------------------------------------------------------------------
    // Static Map Integrity Tests - Grouped by Category
    //----------------------------------------------------------------------------------------------

    @Test
    public void staticMaps_shouldBeUnmodifiableAndNonNull() {
        // Test all static maps are properly initialized and immutable

        // All maps should be non-null
        assertNotNull("ISO8859_1_ESCAPE should not be null", EntityArrays.ISO8859_1_ESCAPE);
        assertNotNull("ISO8859_1_UNESCAPE should not be null", EntityArrays.ISO8859_1_UNESCAPE);
        assertNotNull("HTML40_EXTENDED_ESCAPE should not be null", EntityArrays.HTML40_EXTENDED_ESCAPE);
        assertNotNull("HTML40_EXTENDED_UNESCAPE should not be null", EntityArrays.HTML40_EXTENDED_UNESCAPE);
        assertNotNull("BASIC_ESCAPE should not be null", EntityArrays.BASIC_ESCAPE);
        assertNotNull("BASIC_UNESCAPE should not be null", EntityArrays.BASIC_UNESCAPE);
        assertNotNull("APOS_ESCAPE should not be null", EntityArrays.APOS_ESCAPE);
        assertNotNull("APOS_UNESCAPE should not be null", EntityArrays.APOS_UNESCAPE);
        assertNotNull("JAVA_CTRL_CHARS_ESCAPE should not be null", EntityArrays.JAVA_CTRL_CHARS_ESCAPE);
        assertNotNull("JAVA_CTRL_CHARS_UNESCAPE should not be null", EntityArrays.JAVA_CTRL_CHARS_UNESCAPE);

        // All maps should be unmodifiable
        try {
            EntityArrays.BASIC_ESCAPE.put("test", "test");
            fail("BASIC_ESCAPE should be unmodifiable");
        } catch (UnsupportedOperationException e) {
            // Expected
        }

        try {
            EntityArrays.ISO8859_1_ESCAPE.clear();
            fail("ISO8859_1_ESCAPE should be unmodifiable");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }

    @Test
    public void basicEscapeMaps_shouldContainCorrectMappings() {
        // Test BASIC_ESCAPE contains expected XML/HTML entities
        Map<CharSequence, CharSequence> basic = EntityArrays.BASIC_ESCAPE;

        assertEquals("&quot;", basic.get("\""));
        assertEquals("&amp;", basic.get("&"));
        assertEquals("&lt;", basic.get("<"));
        assertEquals("&gt;", basic.get(">"));
        assertEquals(4, basic.size());

        // Test corresponding unescape map
        Map<CharSequence, CharSequence> basicUnescape = EntityArrays.BASIC_UNESCAPE;
        assertEquals("\"", basicUnescape.get("&quot;"));
        assertEquals("&", basicUnescape.get("&amp;"));
        assertEquals("<", basicUnescape.get("&lt;"));
        assertEquals(">", basicUnescape.get("&gt;"));
        assertEquals(4, basicUnescape.size());
    }

    @Test
    public void aposEscapeMaps_shouldContainApostropheMapping() {
        // Test APOS_ESCAPE contains apostrophe mapping
        Map<CharSequence, CharSequence> apos = EntityArrays.APOS_ESCAPE;

        assertEquals("&apos;", apos.get("'"));
        assertEquals(1, apos.size());

        // Test corresponding unescape map
        Map<CharSequence, CharSequence> aposUnescape = EntityArrays.APOS_UNESCAPE;
        assertEquals("'", aposUnescape.get("&apos;"));
        assertEquals(1, aposUnescape.size());
    }

    @Test
    public void javaCtrlCharsEscapeMaps_shouldContainControlCharacters() {
        // Test JAVA_CTRL_CHARS_ESCAPE contains Java control characters
        Map<CharSequence, CharSequence> ctrl = EntityArrays.JAVA_CTRL_CHARS_ESCAPE;

        assertEquals("\\b", ctrl.get("\b"));
        assertEquals("\\n", ctrl.get("\n"));
        assertEquals("\\t", ctrl.get("\t"));
        assertEquals("\\f", ctrl.get("\f"));
        assertEquals("\\r", ctrl.get("\r"));
        assertEquals(5, ctrl.size());

        // Test corresponding unescape map
        Map<CharSequence, CharSequence> ctrlUnescape = EntityArrays.JAVA_CTRL_CHARS_UNESCAPE;
        assertEquals("\b", ctrlUnescape.get("\\b"));
        assertEquals("\n", ctrlUnescape.get("\\n"));
        assertEquals("\t", ctrlUnescape.get("\\t"));
        assertEquals("\f", ctrlUnescape.get("\\f"));
        assertEquals("\r", ctrlUnescape.get("\\r"));
        assertEquals(5, ctrlUnescape.size());
    }

    @Test
    public void iso8859Maps_shouldContainExpectedEntities() {
        // Test sample of ISO8859-1 entities to verify correct initialization
        Map<CharSequence, CharSequence> iso = EntityArrays.ISO8859_1_ESCAPE;

        // Test a few key entities
        assertEquals("&nbsp;", iso.get("\u00A0")); // non-breaking space
        assertEquals("&copy;", iso.get("\u00A9")); // copyright
        assertEquals("&reg;", iso.get("\u00AE"));  // registered trademark
        assertEquals("&deg;", iso.get("\u00B0"));  // degree sign
        assertEquals("&Agrave;", iso.get("\u00C0")); // À
        assertEquals("&aacute;", iso.get("\u00E1")); // á

        // Verify map is substantial (should contain many ISO-8859-1 characters)
        assertTrue("ISO8859_1_ESCAPE should contain many entities", iso.size() > 90);

        // Test some corresponding unescape entries
        Map<CharSequence, CharSequence> isoUnescape = EntityArrays.ISO8859_1_UNESCAPE;
        assertEquals("\u00A0", isoUnescape.get("&nbsp;"));
        assertEquals("\u00A9", isoUnescape.get("&copy;"));
        assertEquals("\u00C0", isoUnescape.get("&Agrave;"));

        // Maps should be same size (one-to-one mapping)
        assertEquals(iso.size(), isoUnescape.size());
    }

    @Test
    public void html40ExtendedMaps_shouldContainExtendedEntities() {
        // Test sample of HTML 4.0 extended entities
        Map<CharSequence, CharSequence> html40 = EntityArrays.HTML40_EXTENDED_ESCAPE;

        // Test Greek letters
        assertEquals("&Alpha;", html40.get("\u0391"));
        assertEquals("&beta;", html40.get("\u03B2"));
        assertEquals("&gamma;", html40.get("\u03B3"));

        // Test mathematical symbols
        assertEquals("&sum;", html40.get("\u2211"));
        assertEquals("&infin;", html40.get("\u221E"));
        assertEquals("&ne;", html40.get("\u2260"));

        // Test arrows and misc symbols
        assertEquals("&larr;", html40.get("\u2190"));
        assertEquals("&spades;", html40.get("\u2660"));
        assertEquals("&euro;", html40.get("\u20AC"));

        // Verify map is substantial
        assertTrue("HTML40_EXTENDED_ESCAPE should contain many entities", html40.size() > 100);

        // Test corresponding unescape entries
        Map<CharSequence, CharSequence> html40Unescape = EntityArrays.HTML40_EXTENDED_UNESCAPE;
        assertEquals("\u0391", html40Unescape.get("&Alpha;"));
        assertEquals("\u03B2", html40Unescape.get("&beta;"));
        assertEquals("\u20AC", html40Unescape.get("&euro;"));

        // Maps should be same size
        assertEquals(html40.size(), html40Unescape.size());
    }

    //----------------------------------------------------------------------------------------------
    // invert() Method Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void invert_shouldCreateCorrectReverseMapping() {
        // Test invert method with various map types

        // Simple test map
        Map<CharSequence, CharSequence> testMap = new HashMap<>();
        testMap.put("key1", "value1");
        testMap.put("key2", "value2");
        testMap.put("key3", "value3");

        Map<CharSequence, CharSequence> inverted = EntityArrays.invert(testMap);

        assertEquals("key1", inverted.get("value1"));
        assertEquals("key2", inverted.get("value2"));
        assertEquals("key3", inverted.get("value3"));
        assertEquals(3, inverted.size());

        // Verify original map unchanged
        assertEquals("value1", testMap.get("key1"));
        assertEquals(3, testMap.size());
    }

    @Test
    public void invert_shouldHandleEmptyMap() {
        // Test invert with empty map
        Map<CharSequence, CharSequence> emptyMap = new HashMap<>();
        Map<CharSequence, CharSequence> inverted = EntityArrays.invert(emptyMap);

        assertNotNull(inverted);
        assertEquals(0, inverted.size());
        assertTrue(inverted.isEmpty());
    }

    @Test
    public void invert_shouldHandleSpecialCharacters() {
        // Test invert with special characters and Unicode
        Map<CharSequence, CharSequence> specialMap = new HashMap<>();
        specialMap.put("<", "&lt;");
        specialMap.put("&", "&amp;");
        specialMap.put("\u00A9", "&copy;");
        specialMap.put("\u03B1", "&alpha;");

        Map<CharSequence, CharSequence> inverted = EntityArrays.invert(specialMap);

        assertEquals("<", inverted.get("&lt;"));
        assertEquals("&", inverted.get("&amp;"));
        assertEquals("\u00A9", inverted.get("&copy;"));
        assertEquals("\u03B1", inverted.get("&alpha;"));
        assertEquals(4, inverted.size());
    }

    @Test
    public void invert_shouldCreateNewMapInstance() {
        // Test that invert creates a new map, not a view
        Map<CharSequence, CharSequence> original = new HashMap<>();
        original.put("a", "b");

        Map<CharSequence, CharSequence> inverted = EntityArrays.invert(original);

        // Modify original
        original.put("c", "d");

        // Inverted should not change
        assertEquals(1, inverted.size());
        assertFalse(inverted.containsKey("c"));
        assertFalse(inverted.containsValue("d"));
    }

    //----------------------------------------------------------------------------------------------
    // Integration and Consistency Tests
    //----------------------------------------------------------------------------------------------

    @Test
    public void escapeUnescapePairs_shouldBeConsistentInversions() {
        // Test that escape/unescape pairs are properly inverted versions of each other

        // BASIC maps
        verifyMapPairConsistency(EntityArrays.BASIC_ESCAPE, EntityArrays.BASIC_UNESCAPE);

        // APOS maps
        verifyMapPairConsistency(EntityArrays.APOS_ESCAPE, EntityArrays.APOS_UNESCAPE);

        // JAVA_CTRL_CHARS maps
        verifyMapPairConsistency(EntityArrays.JAVA_CTRL_CHARS_ESCAPE, EntityArrays.JAVA_CTRL_CHARS_UNESCAPE);

        // ISO8859_1 maps
        verifyMapPairConsistency(EntityArrays.ISO8859_1_ESCAPE, EntityArrays.ISO8859_1_UNESCAPE);

        // HTML40_EXTENDED maps
        verifyMapPairConsistency(EntityArrays.HTML40_EXTENDED_ESCAPE, EntityArrays.HTML40_EXTENDED_UNESCAPE);
    }

    @Test
    public void staticInitialization_shouldCreateDistinctMapContents() {
        // Verify that different entity maps don't overlap incorrectly

        // BASIC and APOS should not share keys
        for (CharSequence key : EntityArrays.BASIC_ESCAPE.keySet()) {
            assertFalse("BASIC and APOS should not share keys",
                    EntityArrays.APOS_ESCAPE.containsKey(key));
        }

        // BASIC should not contain Java control characters
        assertFalse(EntityArrays.BASIC_ESCAPE.containsKey("\n"));
        assertFalse(EntityArrays.BASIC_ESCAPE.containsKey("\t"));

        // ISO8859_1 and HTML40_EXTENDED should not overlap (they're complementary)
        // Note: They shouldn't have overlapping keys as they cover different character ranges
        boolean hasOverlap = false;
        for (CharSequence key : EntityArrays.ISO8859_1_ESCAPE.keySet()) {
            if (EntityArrays.HTML40_EXTENDED_ESCAPE.containsKey(key)) {
                hasOverlap = true;
                break;
            }
        }
        assertFalse("ISO8859_1 and HTML40_EXTENDED should not have overlapping keys", hasOverlap);
    }

    @Test
    public void realWorldUsage_shouldSupportCommonEscapeScenarios() {
        // Test that the maps support common real-world usage scenarios

        // HTML escaping scenario
        assertTrue("Should escape quote", EntityArrays.BASIC_ESCAPE.containsKey("\""));
        assertTrue("Should escape ampersand", EntityArrays.BASIC_ESCAPE.containsKey("&"));
        assertTrue("Should escape less-than", EntityArrays.BASIC_ESCAPE.containsKey("<"));
        assertTrue("Should escape greater-than", EntityArrays.BASIC_ESCAPE.containsKey(">"));

        // XML apostrophe scenario
        assertTrue("Should escape apostrophe for XML", EntityArrays.APOS_ESCAPE.containsKey("'"));

        // Java string escaping scenario
        assertTrue("Should escape newline", EntityArrays.JAVA_CTRL_CHARS_ESCAPE.containsKey("\n"));
        assertTrue("Should escape tab", EntityArrays.JAVA_CTRL_CHARS_ESCAPE.containsKey("\t"));

        // Common HTML entities scenario
        assertTrue("Should have copyright symbol", EntityArrays.ISO8859_1_ESCAPE.containsKey("\u00A9"));
        assertTrue("Should have non-breaking space", EntityArrays.ISO8859_1_ESCAPE.containsKey("\u00A0"));

        // Mathematical/Greek symbols scenario
        assertTrue("Should have infinity symbol", EntityArrays.HTML40_EXTENDED_ESCAPE.containsKey("\u221E"));
        assertTrue("Should have alpha symbol", EntityArrays.HTML40_EXTENDED_ESCAPE.containsKey("\u03B1"));
    }

    //----------------------------------------------------------------------------------------------
    // Helper Methods
    //----------------------------------------------------------------------------------------------

    /**
     * Verifies that an escape/unescape map pair are proper inverses of each other
     */
    private void verifyMapPairConsistency(Map<CharSequence, CharSequence> escapeMap,
                                          Map<CharSequence, CharSequence> unescapeMap) {
        // Maps should be same size
        assertEquals("Escape and unescape maps should be same size",
                escapeMap.size(), unescapeMap.size());

        // Every escape mapping should have corresponding unescape mapping
        for (Map.Entry<CharSequence, CharSequence> entry : escapeMap.entrySet()) {
            CharSequence original = entry.getKey();
            CharSequence escaped = entry.getValue();

            assertTrue("Unescape map should contain escaped value",
                    unescapeMap.containsKey(escaped));
            assertEquals("Unescape should reverse escape",
                    original, unescapeMap.get(escaped));
        }

        // Every unescape mapping should have corresponding escape mapping
        for (Map.Entry<CharSequence, CharSequence> entry : unescapeMap.entrySet()) {
            CharSequence escaped = entry.getKey();
            CharSequence original = entry.getValue();

            assertTrue("Escape map should contain original value",
                    escapeMap.containsKey(original));
            assertEquals("Escape should reverse unescape",
                    escaped, escapeMap.get(original));
        }
    }
}