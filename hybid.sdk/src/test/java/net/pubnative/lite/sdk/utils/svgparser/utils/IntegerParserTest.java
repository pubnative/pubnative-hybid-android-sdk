// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the IntegerParser class.
 * This class verifies all parsing logic for decimal and hexadecimal integers,
 * including sign handling, boundary conditions, and invalid inputs.
 */
public class IntegerParserTest {

    //---------------------------------------------------------------------
    // parseInt Tests
    //---------------------------------------------------------------------

    @Test
    public void parseInt_coversAllOutcomes() {
        // This single test covers all success, failure, and edge cases for the parseInt method.
        IntegerParser result;

        // --- Basic Success Cases ---
        result = IntegerParser.parseInt("123", 0, 3, true);
        assertNotNull("Should parse a simple positive integer", result);
        assertEquals(123, result.value());
        assertEquals(3, result.getEndPos());

        result = IntegerParser.parseInt("99abc", 0, 5, true);
        assertNotNull("Should parse integer followed by letters", result);
        assertEquals(99, result.value());
        assertEquals(2, result.getEndPos());

        // --- Sign Handling ---
        result = IntegerParser.parseInt("-45", 0, 3, true);
        assertNotNull("Should parse a negative integer with sign enabled", result);
        assertEquals(-45, result.value());
        assertEquals(3, result.getEndPos());

        result = IntegerParser.parseInt("+67", 0, 3, true);
        assertNotNull("Should parse a positive integer with explicit sign", result);
        assertEquals(67, result.value());
        assertEquals(3, result.getEndPos());

        assertNull("Should NOT parse a sign when includeSign is false", IntegerParser.parseInt("+67", 0, 3, false));

        // --- Boundary Value Cases ---
        result = IntegerParser.parseInt(String.valueOf(Integer.MAX_VALUE), 0, 10, true);
        assertNotNull("Should parse Integer.MAX_VALUE", result);
        assertEquals(Integer.MAX_VALUE, result.value());

        assertNull("Should return null for value > Integer.MAX_VALUE", IntegerParser.parseInt("2147483648", 0, 10, true));

        result = IntegerParser.parseInt(String.valueOf(Integer.MIN_VALUE), 0, 11, true);
        assertNotNull("Should parse Integer.MIN_VALUE", result);
        assertEquals(Integer.MIN_VALUE, result.value());

        assertNull("Should return null for value < Integer.MIN_VALUE", IntegerParser.parseInt("-2147483649", 0, 11, true));

        // --- Failure Cases ---
        assertNull("Should return null for empty input", IntegerParser.parseInt("", 0, 0, true));
        assertNull("Should return null for non-numeric input", IntegerParser.parseInt("abc", 0, 3, true));
        assertNull("Should return null for input that is only a sign", IntegerParser.parseInt("-", 0, 1, true));
        assertNull("Should return null for input that is only a sign", IntegerParser.parseInt("+", 0, 1, true));

        // --- Substring Parsing ---
        result = IntegerParser.parseInt("ignored-123-ignored", 7, 11, true);
        assertNotNull("Should respect startpos and len", result);
        assertEquals(-123, result.value());
        assertEquals(11, result.getEndPos());
    }


    //---------------------------------------------------------------------
    // parseHex Tests
    //---------------------------------------------------------------------

    @Test
    public void parseHex_coversAllOutcomes() {
        // This single test covers all success, failure, and edge cases for the parseHex method.
        IntegerParser result;

        // --- Basic Success Cases ---
        result = IntegerParser.parseHex("FF", 0, 2);
        assertNotNull("Should parse uppercase hex", result);
        assertEquals(255, result.value());
        assertEquals(2, result.getEndPos());

        result = IntegerParser.parseHex("abcdef", 0, 6);
        assertNotNull("Should parse lowercase hex", result);
        assertEquals(0xabcdef, result.value());
        assertEquals(6, result.getEndPos());

        result = IntegerParser.parseHex("1a2B3c", 0, 6);
        assertNotNull("Should parse mixed case hex", result);
        assertEquals(0x1a2B3c, result.value());
        assertEquals(6, result.getEndPos());

        result = IntegerParser.parseHex("a9-", 0, 3);
        assertNotNull("Should parse hex followed by other chars", result);
        assertEquals(0xa9, result.value());
        assertEquals(2, result.getEndPos());

        // --- Boundary Value Cases ---
        // In the SUT, 0xffffffffL is treated as an unsigned int, which wraps to -1 as a signed int.
        result = IntegerParser.parseHex("FFFFFFFF", 0, 8);
        assertNotNull("Should parse max unsigned int value", result);
        assertEquals(-1, result.value());

        assertNull("Should return null for value > 0xffffffff", IntegerParser.parseHex("100000000", 0, 9));

        // --- Failure Cases ---
        assertNull("Should return null for empty input", IntegerParser.parseHex("", 0, 0));
        assertNull("Should return null for non-hex input", IntegerParser.parseHex("xyz", 0, 3));

        // --- Substring Parsing ---
        result = IntegerParser.parseHex("CAFE", 0, 4);
        assertNotNull("Should parse a simple hex string", result);
        assertEquals("The hex value CAFE should be 51966", 0xCAFE, result.value());
        assertEquals(4, result.getEndPos());
    }
}