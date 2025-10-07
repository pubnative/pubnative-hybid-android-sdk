// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser.utils;

import android.os.Build;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for the CSSTextScanner class.
 * This class verifies all parsing methods, including edge cases and error handling.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P})
public class CSSTextScannerTest {

    //---------------------------------------------------------------------
    // Constructor tests
    //---------------------------------------------------------------------

    @Test
    public void constructor_stripsBlockComments() {
        // The constructor should remove all CSS block comments (/* ... */).
        String input = "/* comment */ p { /* another */ color: black; }";
        CSSTextScanner scanner = new CSSTextScanner(input);
        assertEquals("p {  color: black; }", scanner.input);
    }

    @Test
    public void constructor_trimsWhitespace() {
        // The constructor should also trim leading/trailing whitespace after stripping comments.
        String input = "  \n /* comment */   h1 { font-size: 12pt; }   ";
        CSSTextScanner scanner = new CSSTextScanner(input);
        assertEquals("h1 { font-size: 12pt; }", scanner.input);
    }

    //---------------------------------------------------------------------
    // Token and Identifier parsing tests
    //---------------------------------------------------------------------

    @Test
    public void nextIdentifier_parsesValidIdentifiers() {
        // Test various forms of valid CSS identifiers.
        CSSTextScanner scanner = new CSSTextScanner("simple-id _id_with_underscores -moz-specific id123");

        assertEquals("simple-id", scanner.nextIdentifier());
        scanner.skipWhitespace();
        assertEquals("_id_with_underscores", scanner.nextIdentifier());
        scanner.skipWhitespace();
        assertEquals("-moz-specific", scanner.nextIdentifier());
        scanner.skipWhitespace();
        assertEquals("id123", scanner.nextIdentifier());
    }

    @Test
    public void nextIdentifier_withInvalidStart_returnsNull() {
        // If the next character cannot start an identifier (e.g., '#', '.'), it should return null.
        CSSTextScanner scanner = new CSSTextScanner("#not-an-id");
        assertNull(scanner.nextIdentifier());
    }

    @Test
    public void nextURL_parsesVariousFormats() {
        // Test url() syntax with and without quotes, and with internal whitespace.
        CSSTextScanner scanner = new CSSTextScanner("url(\"path/to/file.css\") url( 'another.css' ) url(no-quotes.svg)");

        assertEquals("path/to/file.css", scanner.nextURL());
        scanner.skipWhitespace();
        assertEquals("another.css", scanner.nextURL());
        scanner.skipWhitespace();
        assertEquals("no-quotes.svg", scanner.nextURL());
    }

    @Test
    public void nextURL_withInvalidContent_returnsNull() {
        // An unclosed url() function should return null.
        CSSTextScanner scanner = new CSSTextScanner("url( \"path/to/file.css\" bogus");
        int initialPosition = scanner.position;
        assertNull(scanner.nextURL());
        assertEquals(initialPosition, scanner.position);
    }

    @Test
    public void nextCSSString_parsesQuotedStrings() {
        // Test parsing of single and double quoted strings, including escaped quotes.
        CSSTextScanner scanner = new CSSTextScanner("'hello world' \"it's a \\\"quote\\\"\"");

        assertEquals("hello world", scanner.nextCSSString());
        scanner.skipWhitespace();
        assertEquals("it's a \"quote\"", scanner.nextCSSString());
    }

    @Test
    public void nextPropertyValue_parsesUntilTerminator() {
        // It should read a property value until it hits a terminator like ';', '}', or '!'.
        CSSTextScanner scanner = new CSSTextScanner("12px solid blue !important; other: val");

        assertEquals("12px solid blue", scanner.nextPropertyValue());
        // The scanner should be positioned right after the value, before the '!'
        assertTrue(scanner.consume('!'));
    }

    //---------------------------------------------------------------------
    // Selector parsing tests
    //---------------------------------------------------------------------

    @Test
    public void nextSelectorGroup_parsesSimpleAndMultipleSelectors() throws CSSParseException {
        // Test parsing a single selector and a comma-separated list of selectors.
        CSSTextScanner scanner1 = new CSSTextScanner("h1");
        List<CSSParser.Selector> group1 = scanner1.nextSelectorGroup();
        assertEquals(1, group1.size());
        assertEquals("h1 [1]", group1.get(0).toString());

        CSSTextScanner scanner2 = new CSSTextScanner("h1, h2.classy, #main");
        List<CSSParser.Selector> group2 = scanner2.nextSelectorGroup();
        assertEquals(3, group2.size());
        assertEquals("h1 [1]", group2.get(0).toString());
        assertEquals("h2[class=classy] [1001]", group2.get(1).toString());
        assertEquals("*[id=main] [1000000]", group2.get(2).toString());
    }

    @Test
    public void nextSimpleSelector_parsesChainedSelectors() throws CSSParseException {
        // Test a complex selector with tag, class, id, attribute, and pseudo-class chained together.
        CSSTextScanner scanner = new CSSTextScanner("path.stroke-class#my-path[fill=\"none\"]:first-child");
        CSSParser.Selector selector = new CSSParser.Selector();
        assertTrue(scanner.nextSimpleSelector(selector));
        assertEquals(1, selector.size());
        // Note: The toString() for pseudo-classes can be complex, so we check parts of it.
        String resultString = selector.get(0).toString();
        assertTrue(resultString.startsWith("path[class=stroke-class][id=my-path][fill=none]:"));
        assertTrue(resultString.contains("nth-child(0n+1)"));
    }

    @Test
    public void nextSimpleSelector_parsesCombinators() throws CSSParseException {
        // Test child (>) and adjacent sibling (+) combinators.
        CSSTextScanner scanner = new CSSTextScanner("div > p + span");
        List<CSSParser.Selector> selectorGroup = scanner.nextSelectorGroup();
        assertEquals(1, selectorGroup.size());

        CSSParser.Selector selector = selectorGroup.get(0);
        assertEquals(3, selector.size());
        assertEquals("div", selector.get(0).toString());
        assertEquals("> p", selector.get(1).toString());
        assertEquals("+ span", selector.get(2).toString());
    }

    //---------------------------------------------------------------------
    // Exception handling tests
    //---------------------------------------------------------------------

    @Test(expected = CSSParseException.class)
    public void nextSimpleSelector_withInvalidClass_throwsException() throws CSSParseException {
        // A selector with an invalid class (e.g., ".#id") should throw a CSSParseException.
        CSSTextScanner scanner = new CSSTextScanner(".#id");
        scanner.nextSimpleSelector(new CSSParser.Selector());
    }

    @Test(expected = CSSParseException.class)
    public void nextSimpleSelector_withUnclosedAttribute_throwsException() throws CSSParseException {
        // An unclosed attribute selector should throw an exception.
        CSSTextScanner scanner = new CSSTextScanner("[href=\"google.com");
        scanner.nextSimpleSelector(new CSSParser.Selector());
    }

    @Test(expected = CSSParseException.class)
    public void nextSimpleSelector_withInvalidPseudoClassParam_throwsException() throws CSSParseException {
        // A pseudo-class with a missing or invalid parameter should throw an exception.
        CSSTextScanner scanner = new CSSTextScanner(":nth-child(2n+B)");
        scanner.nextSimpleSelector(new CSSParser.Selector());
    }

    @Test(expected = CSSParseException.class)
    public void parsePseudoClass_withUnsupportedPseudo_throwsException() throws CSSParseException {
        // An unsupported pseudo class should throw an exception.
        CSSTextScanner scanner = new CSSTextScanner(":unsupported-pseudo");
        scanner.nextSimpleSelector(new CSSParser.Selector());
    }
}