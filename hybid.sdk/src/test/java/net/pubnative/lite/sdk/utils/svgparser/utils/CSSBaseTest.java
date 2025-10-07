// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser.utils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CSSBaseTest {

    private CSSParser.Ruleset mockRuleset;

    @Before
    public void setUp() {
        mockRuleset = mock(CSSParser.Ruleset.class);
    }

    @Test
    public void constructor_withValidCSS_createsParserAndParsesCSS() {
        String validCSS = "body { color: red; }";

        try (MockedConstruction<CSSParser> mockedParser = mockConstruction(CSSParser.class,
                (mock, context) -> {
                    when(mock.parse(validCSS)).thenReturn(mockRuleset);
                })) {

            CSSBase cssBase = new CSSBase(validCSS);

            // Verify parser was created with correct parameters
            assertEquals(1, mockedParser.constructed().size());
            CSSParser parser = mockedParser.constructed().get(0);

            // Verify parse was called with the CSS string
            verify(parser).parse(validCSS);

            // Verify ruleset was assigned
            assertEquals(mockRuleset, cssBase.cssRuleset);
        }
    }

    @Test
    public void constructor_withEmptyCSS_parsesEmptyString() {
        String emptyCSS = "";

        try (MockedConstruction<CSSParser> mockedParser = mockConstruction(CSSParser.class,
                (mock, context) -> {
                    when(mock.parse(emptyCSS)).thenReturn(mockRuleset);
                })) {

            CSSBase cssBase = new CSSBase(emptyCSS);

            verify(mockedParser.constructed().get(0)).parse(emptyCSS);
            assertEquals(mockRuleset, cssBase.cssRuleset);
        }
    }

    @Test
    public void constructor_withWhitespaceOnlyCSS_parsesWhitespaceString() {
        String whitespaceCSS = "   \n\t  ";

        try (MockedConstruction<CSSParser> mockedParser = mockConstruction(CSSParser.class,
                (mock, context) -> {
                    when(mock.parse(whitespaceCSS)).thenReturn(mockRuleset);
                })) {

            CSSBase cssBase = new CSSBase(whitespaceCSS);

            verify(mockedParser.constructed().get(0)).parse(whitespaceCSS);
            assertEquals(mockRuleset, cssBase.cssRuleset);
        }
    }

    @Test
    public void constructor_withComplexCSS_parsesSuccessfully() {
        String complexCSS = "body { color: red; } .class1 { font-size: 12px; } #id1 { margin: 10px; }";

        try (MockedConstruction<CSSParser> mockedParser = mockConstruction(CSSParser.class,
                (mock, context) -> {
                    when(mock.parse(complexCSS)).thenReturn(mockRuleset);
                })) {

            CSSBase cssBase = new CSSBase(complexCSS);

            verify(mockedParser.constructed().get(0)).parse(complexCSS);
            assertEquals(mockRuleset, cssBase.cssRuleset);
        }
    }

    @Test
    public void constructor_withNullCSS_throwsException() {
        // Test null CSS input - should throw NullPointerException
        assertThrows(NullPointerException.class, () -> {
            new CSSBase(null);
        });
    }

    @Test
    public void constructor_withInvalidCSS_parsesAndHandlesGracefully() {
        String invalidCSS = "invalid css syntax {{{ ;;; }}}";

        try (MockedConstruction<CSSParser> mockedParser = mockConstruction(CSSParser.class,
                (mock, context) -> {
                    when(mock.parse(invalidCSS)).thenReturn(mockRuleset);
                })) {

            // Constructor should not throw exception even with invalid CSS
            CSSBase cssBase = new CSSBase(invalidCSS);

            verify(mockedParser.constructed().get(0)).parse(invalidCSS);
            assertEquals(mockRuleset, cssBase.cssRuleset);
        }
    }

    @Test
    public void constructor_createsParserWithCorrectParameters() {
        String testCSS = "test css";

        try (MockedConstruction<CSSParser> mockedParser = mockConstruction(CSSParser.class,
                (mock, context) -> {
                    // Verify constructor parameters
                    assertEquals(CSSParser.Source.RenderOptions, context.arguments().get(0));
                    assertNull(context.arguments().get(1)); // SVGExternalFileResolver should be null

                    when(mock.parse(testCSS)).thenReturn(mockRuleset);
                })) {

            new CSSBase(testCSS);

            assertEquals(1, mockedParser.constructed().size());
        }
    }

    @Test
    public void constructor_withCSSContainingComments_parsesCorrectly() {
        String cssWithComments = "/* Comment */ body { color: red; /* Another comment */ }";

        try (MockedConstruction<CSSParser> mockedParser = mockConstruction(CSSParser.class,
                (mock, context) -> {
                    when(mock.parse(cssWithComments)).thenReturn(mockRuleset);
                })) {

            CSSBase cssBase = new CSSBase(cssWithComments);

            verify(mockedParser.constructed().get(0)).parse(cssWithComments);
            assertEquals(mockRuleset, cssBase.cssRuleset);
        }
    }

    @Test
    public void constructor_withCSSContainingMediaQueries_parsesCorrectly() {
        String cssWithMedia = "@media screen { body { color: blue; } }";

        try (MockedConstruction<CSSParser> mockedParser = mockConstruction(CSSParser.class,
                (mock, context) -> {
                    when(mock.parse(cssWithMedia)).thenReturn(mockRuleset);
                })) {

            CSSBase cssBase = new CSSBase(cssWithMedia);

            verify(mockedParser.constructed().get(0)).parse(cssWithMedia);
            assertEquals(mockRuleset, cssBase.cssRuleset);
        }
    }

    @Test
    public void constructor_withCSSContainingAtRules_parsesCorrectly() {
        String cssWithAtRules = "@import 'styles.css'; body { color: green; }";

        try (MockedConstruction<CSSParser> mockedParser = mockConstruction(CSSParser.class,
                (mock, context) -> {
                    when(mock.parse(cssWithAtRules)).thenReturn(mockRuleset);
                })) {

            CSSBase cssBase = new CSSBase(cssWithAtRules);

            verify(mockedParser.constructed().get(0)).parse(cssWithAtRules);
            assertEquals(mockRuleset, cssBase.cssRuleset);
        }
    }

    @Test
    public void cssRuleset_isProtectedAndAccessible() throws Exception {
        String testCSS = "test";

        try (MockedConstruction<CSSParser> mockedParser = mockConstruction(CSSParser.class,
                (mock, context) -> {
                    when(mock.parse(testCSS)).thenReturn(mockRuleset);
                })) {

            CSSBase cssBase = new CSSBase(testCSS);

            // Verify that cssRuleset field exists and is accessible (protected)
            java.lang.reflect.Field rulesetField = CSSBase.class.getDeclaredField("cssRuleset");
            assertNotNull("cssRuleset field should exist", rulesetField);

            // Field should be protected (accessible to subclasses)
            int modifiers = rulesetField.getModifiers();
            assertTrue("cssRuleset should be protected",
                    java.lang.reflect.Modifier.isProtected(modifiers));

            // Verify the field value
            rulesetField.setAccessible(true);
            assertEquals(mockRuleset, rulesetField.get(cssBase));
        }
    }

    @Test
    public void constructor_multipleInstances_createsSeparateParsers() {
        String css1 = "body { color: red; }";
        String css2 = "div { color: blue; }";

        CSSParser.Ruleset mockRuleset1 = mock(CSSParser.Ruleset.class);
        CSSParser.Ruleset mockRuleset2 = mock(CSSParser.Ruleset.class);

        try (MockedConstruction<CSSParser> mockedParser = mockConstruction(CSSParser.class,
                (mock, context) -> {
                    // Return different rulesets based on which CSS is parsed
                    when(mock.parse(css1)).thenReturn(mockRuleset1);
                    when(mock.parse(css2)).thenReturn(mockRuleset2);
                })) {

            CSSBase cssBase1 = new CSSBase(css1);
            CSSBase cssBase2 = new CSSBase(css2);

            // Verify two separate parsers were created
            assertEquals(2, mockedParser.constructed().size());

            // Verify each parser was called with the correct CSS
            verify(mockedParser.constructed().get(0)).parse(css1);
            verify(mockedParser.constructed().get(1)).parse(css2);

            // Verify different rulesets were assigned
            assertEquals(mockRuleset1, cssBase1.cssRuleset);
            assertEquals(mockRuleset2, cssBase2.cssRuleset);
        }
    }

    @Test
    public void constructor_withLongCSS_handlesLargeInput() {
        // Test with a large CSS string
        StringBuilder largeCSS = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeCSS.append(".class").append(i).append(" { color: red; } ");
        }
        String largeCSSString = largeCSS.toString();

        try (MockedConstruction<CSSParser> mockedParser = mockConstruction(CSSParser.class,
                (mock, context) -> {
                    when(mock.parse(largeCSSString)).thenReturn(mockRuleset);
                })) {

            CSSBase cssBase = new CSSBase(largeCSSString);

            verify(mockedParser.constructed().get(0)).parse(largeCSSString);
            assertEquals(mockRuleset, cssBase.cssRuleset);
        }
    }

    @Test
    public void constructor_withSpecialCharacters_handlesUnicodeAndSymbols() {
        String cssWithSpecialChars = ".测试 { color: red; } .symbol∑ { font-size: 12px; }";

        try (MockedConstruction<CSSParser> mockedParser = mockConstruction(CSSParser.class,
                (mock, context) -> {
                    when(mock.parse(cssWithSpecialChars)).thenReturn(mockRuleset);
                })) {

            CSSBase cssBase = new CSSBase(cssWithSpecialChars);

            verify(mockedParser.constructed().get(0)).parse(cssWithSpecialChars);
            assertEquals(mockRuleset, cssBase.cssRuleset);
        }
    }
}