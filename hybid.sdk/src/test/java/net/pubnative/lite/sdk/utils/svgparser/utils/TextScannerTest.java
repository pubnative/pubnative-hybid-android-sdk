// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * A comprehensive suite of unit tests for the TextScanner class, covering all methods
 * and edge cases.
 */
public class TextScannerTest {

    // A slightly larger delta for float comparisons to account for precision issues.
    private static final float FLOAT_DELTA = 1e-5f;

    //---------------------------------------------------------------------
    // Constructor and State Tests
    //---------------------------------------------------------------------

    @Test
    public void constructor_trimsInputAndSetsLength() {
        TextScanner scanner = new TextScanner("  hello  ");
        assertEquals("hello", scanner.input);
        assertEquals(5, scanner.inputLength);
    }

    @Test
    public void empty_and_restOfText_reflectsCurrentPosition() {
        TextScanner scanner = new TextScanner("abc");
        assertFalse(scanner.empty());
        assertEquals("abc", scanner.restOfText());

        scanner.position = 1;
        assertFalse(scanner.empty());
        assertEquals("bc", scanner.restOfText());

        scanner.position = 3;
        assertTrue(scanner.empty());
        assertNull(scanner.restOfText());
    }

    //---------------------------------------------------------------------
    // Whitespace and Consumer Tests
    //---------------------------------------------------------------------

    @Test
    public void whitespaceAndConsuming_workCorrectly() {
        TextScanner scanner = new TextScanner("word  ,  word1 word2");
        scanner.position = 4; // Position after "word"

        // skipWhitespace
        scanner.skipWhitespace();
        assertEquals(6, scanner.position);

        // skipCommaWhitespace
        assertTrue(scanner.skipCommaWhitespace());
        assertEquals(9, scanner.position);

        // consume(String)
        assertTrue(scanner.consume("word1"));
        assertEquals(14, scanner.position);
        assertFalse(scanner.consume("word2")); // There is a space first

        // consume(char)
        scanner.skipWhitespace();
        assertTrue(scanner.consume('w'));
        assertEquals(16, scanner.position);
    }

    //---------------------------------------------------------------------
    // Character-level Method Tests
    //---------------------------------------------------------------------

    @Test
    public void characterMethods_behaveAsExpected() {
        TextScanner scanner = new TextScanner("ABC");

        // hasLetter at start
        assertTrue(scanner.hasLetter());

        // nextChar reads 'A' and advances position to 1
        assertEquals((Integer)(int)'A', scanner.nextChar());
        assertEquals(1, scanner.position);

        // advanceChar now correctly skips 'B' and returns 'C'
        assertEquals('C', scanner.advanceChar());
        assertEquals(2, scanner.position);

        // advanceChar from the last character should now return -1
        assertEquals(-1, scanner.advanceChar());
        assertEquals(3, scanner.position);

        // Now scanner is empty
        assertTrue(scanner.empty());
        assertNull(scanner.nextChar());
        assertEquals(-1, scanner.advanceChar()); // Calling again should still be -1
    }

    //---------------------------------------------------------------------
    // Number and Flag Parsing Tests
    //---------------------------------------------------------------------

    @Test
    public void numberAndFlagParsing_coversAllMethods() {
        TextScanner scanner = new TextScanner("12.3, 45.6 789 1 0");

        // nextFloat
        assertEquals(12.3f, scanner.nextFloat(), FLOAT_DELTA);
        assertEquals(4, scanner.position);

        // possibleNextFloat
        assertEquals(45.6f, scanner.possibleNextFloat(), FLOAT_DELTA);
        assertEquals(10, scanner.position);

        // checkedNextFloat
        TextScanner scannerForChecked = new TextScanner(" 99.9");
        assertEquals(99.9f, scannerForChecked.checkedNextFloat(1f), FLOAT_DELTA);
        assertTrue(Float.isNaN(new TextScanner(" 99.9").checkedNextFloat(Float.NaN)));

        // nextInteger
        scanner.skipWhitespace(); // Correctly skip the space before the integer
        assertEquals((Integer) 789, scanner.nextInteger(false));
        assertEquals(14, scanner.position);

        // nextFlag
        scanner.skipWhitespace();
        assertEquals(Boolean.TRUE, scanner.nextFlag());
        assertEquals(16, scanner.position);
        scanner.skipWhitespace();
        assertEquals(Boolean.FALSE, scanner.nextFlag());
        assertEquals(18, scanner.position);

        // checkedNextFlag
        TextScanner scannerForFlag = new TextScanner(", 1");
        assertTrue(scannerForFlag.checkedNextFlag("not null"));
        assertNull(new TextScanner(", 1").checkedNextFlag(null));
    }

    //---------------------------------------------------------------------
    // Token, Word, and String Parsing
    //---------------------------------------------------------------------

    @Test
    public void tokenAndWordParsing_coversAllMethods() {
        // nextToken
        TextScanner scanner = new TextScanner("token1 token2");
        assertEquals("token1", scanner.nextToken());

        // nextToken with terminator
        scanner = new TextScanner("key=value");
        assertEquals("key", scanner.nextToken('='));

        // nextWord
        scanner = new TextScanner("word 123");
        assertEquals("word", scanner.nextWord());
        assertEquals(4, scanner.position);
        assertNull(scanner.nextWord()); // Next is whitespace

        // nextFunction
        scanner = new TextScanner("myFunction(arg)");
        assertEquals("myFunction", scanner.nextFunction());
        assertEquals(11, scanner.position); // Position is after '('
        assertNull(new TextScanner("not a function").nextFunction());

        // nextQuotedString
        scanner = new TextScanner("'hello' \"world\"");
        assertEquals("hello", scanner.nextQuotedString());
        scanner.skipWhitespace();
        assertEquals("world", scanner.nextQuotedString());
        assertNull(new TextScanner("'unclosed").nextQuotedString());
    }

    //---------------------------------------------------------------------
    // Utility Methods
    //---------------------------------------------------------------------

    @Test
    public void nextUnit_parsesCorrectly() {
        // nextUnit is used by nextLength, but we can test it directly
        TextScanner scanner = new TextScanner("10px 5em 2.5cm 50% 12pt");
        scanner.position = 2;
        assertEquals(SVGBase.Unit.px, scanner.nextUnit());
        scanner.skipWhitespace();
        scanner.position +=1;
        assertEquals(SVGBase.Unit.em, scanner.nextUnit());
        scanner.skipWhitespace();
        scanner.position +=3;
        assertEquals(SVGBase.Unit.cm, scanner.nextUnit());
        scanner.skipWhitespace();
        scanner.position +=2;
        assertEquals(SVGBase.Unit.percent, scanner.nextUnit());
        assertNull(new TextScanner("12xx").nextUnit());
    }

    @Test
    public void ahead_peeksAtNextToken() {
        TextScanner scanner = new TextScanner("first second third");
        assertEquals("first", scanner.ahead());
        // Position should not have changed
        assertEquals(0, scanner.position);
    }
}