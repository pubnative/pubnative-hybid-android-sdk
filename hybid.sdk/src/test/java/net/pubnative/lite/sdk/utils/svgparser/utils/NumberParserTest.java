// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the NumberParser class.
 * Verifies all parsing logic for integers, floats, and scientific notation,
 * including edge cases, boundary conditions, and error handling.
 */
public class NumberParserTest {

    // A small delta for float comparisons to account for precision issues.
    private static final float FLOAT_DELTA = 1e-6f;

    @Test
    public void parseNumber_coversAllOutcomes() {
        NumberParser parser = new NumberParser();
        float result;

        // --- Basic Integers and Floats ---
        result = parser.parseNumber("123", 0, 3);
        assertEquals(123f, result, FLOAT_DELTA);
        assertEquals(3, parser.getEndPos());

        result = parser.parseNumber("45.67", 0, 5);
        assertEquals(45.67f, result, FLOAT_DELTA);
        assertEquals(5, parser.getEndPos());

        result = parser.parseNumber(".5", 0, 2);
        assertEquals(0.5f, result, FLOAT_DELTA);
        assertEquals(2, parser.getEndPos());

        // --- Sign Handling ---
        result = parser.parseNumber("-99.9", 0, 5);
        assertEquals(-99.9f, result, FLOAT_DELTA);
        assertEquals(5, parser.getEndPos());

        result = parser.parseNumber("+42", 0, 3);
        assertEquals(42f, result, FLOAT_DELTA);
        assertEquals(3, parser.getEndPos());

        // --- Exponent Handling ---
        result = parser.parseNumber("1.23e5", 0, 6);
        assertEquals(123000f, result, FLOAT_DELTA);
        assertEquals(6, parser.getEndPos());

        result = parser.parseNumber("5E-3", 0, 4);
        assertEquals(0.005f, result, FLOAT_DELTA);
        assertEquals(4, parser.getEndPos());

        result = parser.parseNumber("6.7e+2", 0, 6);
        assertEquals(670f, result, FLOAT_DELTA);
        assertEquals(6, parser.getEndPos());

        // --- Zero Handling ---
        result = parser.parseNumber("0", 0, 1);
        assertEquals(0f, result, FLOAT_DELTA);
        assertEquals(1, parser.getEndPos());

        result = parser.parseNumber("000.123", 0, 7);
        assertEquals(0.123f, result, FLOAT_DELTA);
        assertEquals(7, parser.getEndPos());

        result = parser.parseNumber("12300", 0, 5);
        assertEquals(12300f, result, FLOAT_DELTA);
        assertEquals(5, parser.getEndPos());

        // --- Boundary and Overflow Cases ---
        result = parser.parseNumber("3.4e38", 0, 6);
        assertEquals(3.4e38f, result, FLOAT_DELTA);

        // Very large significand should fail
        result = parser.parseNumber("9999999999999999999", 0, 19);
        assertTrue("Significand overflow should result in NaN", Float.isNaN(result));

        // Exponent out of float range should fail
        result = parser.parseNumber("1e39", 0, 4);
        assertTrue("Large exponent should result in NaN", Float.isNaN(result));

        // Using 1e-46, which is truly out of bounds for the parser's check
        result = parser.parseNumber("1e-46", 0, 5);
        assertTrue("Small exponent should result in NaN", Float.isNaN(result));

        // Tiny number that requires multi-step exponent application
        result = parser.parseNumber("1.2e-40", 0, 7);
        assertEquals(1.2e-40f, result, 1e-45f); // Use a smaller delta for tiny numbers

        // --- Failure Cases (should return NaN) ---
        assertTrue("Empty string should result in NaN", Float.isNaN(parser.parseNumber("", 0, 0)));
        assertTrue("Non-numeric input should result in NaN", Float.isNaN(parser.parseNumber("abc", 0, 3)));
        assertTrue("A single sign should result in NaN", Float.isNaN(parser.parseNumber("+", 0, 1)));
        assertTrue("A single decimal point should result in NaN", Float.isNaN(parser.parseNumber(".", 0, 1)));
        assertTrue("Trailing decimal point should result in NaN", Float.isNaN(parser.parseNumber("123.", 0, 4)));
        assertTrue("Incomplete exponent 'e' should result in NaN", Float.isNaN(parser.parseNumber("123e", 0, 4)));
        assertTrue("Incomplete exponent 'e-' should result in NaN", Float.isNaN(parser.parseNumber("123e-", 0, 5)));

        // --- Substring and Partial Parsing ---
        // Should parse '1.2' and stop at the second '.'
        result = parser.parseNumber("1.2.3", 0, 5);
        assertEquals(1.2f, result, FLOAT_DELTA);
        assertEquals(3, parser.getEndPos());

        // Should parse '5.4' and stop at 'em'
        result = parser.parseNumber("5.4em", 0, 5);
        assertEquals(5.4f, result, FLOAT_DELTA);
        assertEquals(3, parser.getEndPos());

        // Should respect startpos and len
        result = parser.parseNumber("ignored -12.3e2 ignored", 8, 16);
        assertEquals(-1230f, result, FLOAT_DELTA);
        assertEquals(15, parser.getEndPos());
    }
}