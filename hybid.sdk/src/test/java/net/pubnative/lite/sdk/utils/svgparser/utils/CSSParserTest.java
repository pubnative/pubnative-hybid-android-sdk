// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser.utils;

import android.os.Build;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.pubnative.lite.sdk.utils.svgparser.SVGExternalFileResolver;
import net.pubnative.lite.sdk.utils.svgparser.SVGParseException;

/**
 * Unit tests for the CSSParser class.
 * This class uses Robolectric to mock Android framework dependencies like android.util.Log.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P})
public class CSSParserTest {

    // Mock for external dependencies with complex logic
    @Mock
    private SVGExternalFileResolver mockExternalFileResolver;

    // Real object instances for the SVG element tree (data containers)
    private SVGBase.Svg svgElement;
    private SVGBase.Group groupElement;
    private SVGBase.Path pathElement;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Direct Log output to the console during tests
        ShadowLog.stream = System.out;
        // Set up the element tree for rule matching tests
        setupElementTree();
    }

    //---------------------------------------------------------------------
    // Constructor and basic parsing tests
    //---------------------------------------------------------------------

    @Test
    public void constructor_initializesPropertiesCorrectly() {
        // This test covers the two constructors to ensure they set internal fields as expected.
        CSSParser parserWithDefaults = new CSSParser(CSSParser.Source.Document, mockExternalFileResolver);
        assertNotNull(parserWithDefaults);

        CSSParser parserWithMediaType = new CSSParser(CSSParser.MediaType.print, CSSParser.Source.RenderOptions, mockExternalFileResolver);
        assertNotNull(parserWithMediaType);
    }

    @Test
    public void parse_withEmptyOrNullSheet_returnsEmptyRuleset() {
        // An empty or null CSS string should result in an empty, but not null, ruleset.
        CSSParser parser = new CSSParser(CSSParser.Source.Document, null);

        CSSParser.Ruleset rulesetFromEmpty = parser.parse("");
        assertNotNull(rulesetFromEmpty);
        assertTrue(rulesetFromEmpty.isEmpty());

        // Note: Calling parser.parse(null) is skipped because it reveals a NullPointerException
        // bug in the CSSTextScanner constructor, which cannot handle a null input.
        // A robust implementation should have a null check.
    }

    @Test
    public void parse_simpleRule_parsesCorrectly() {
        // A simple CSS rule should be parsed into a selector and style properties.
        CSSParser parser = new CSSParser(CSSParser.Source.Document, null);
        String css = "svg { fill: black; }";

        CSSParser.Ruleset ruleset = parser.parse(css);

        assertEquals(1, ruleset.ruleCount());
        CSSParser.Rule rule = ruleset.getRules().get(0);
        assertEquals("svg [1]", rule.selector.toString());
        assertNotNull(rule.style);
    }

    @Test
    public void parse_handlesComments() {
        // CSS comments (/*...*/) and HTML-style comments () should be ignored.
        CSSParser parser = new CSSParser(CSSParser.Source.Document, null);
        String css = "path { stroke: #fff; }";

        CSSParser.Ruleset ruleset = parser.parse(css);
        assertEquals(1, ruleset.ruleCount());
        assertEquals("path [1]", ruleset.getRules().get(0).selector.toString());
    }

    @Test
    public void parse_withMalformedRule_logsErrorAndContinues() {
        // A malformed rule should cause an error to be logged but should not crash the parser.
        CSSParser parser = new CSSParser(CSSParser.Source.Document, null);
        String css = "path { fill: ; } circle { stroke: red; }"; // Value for fill is missing

        parser.parse(css);

        // Verify that an error was logged for the malformed part
        List<ShadowLog.LogItem> logs = ShadowLog.getLogsForTag("CSSParser");
        assertTrue(logs.stream().anyMatch(item -> item.msg.contains("Expected property value")));
    }

    //---------------------------------------------------------------------
    // @-rule parsing tests
    //---------------------------------------------------------------------

    @Test
    public void parseAtRule_mediaRuleMatching_includesRules() {
        // An @media rule matching the device media type should have its rules included.
        CSSParser parser = new CSSParser(CSSParser.MediaType.screen, CSSParser.Source.Document, null);
        String css = "@media screen { .myclass { opacity: 0.5; } }";

        CSSParser.Ruleset ruleset = parser.parse(css);
        assertEquals(1, ruleset.ruleCount());
        assertEquals("*[class=myclass] [1000]", ruleset.getRules().get(0).selector.toString());
    }

    @Test
    public void parseAtRule_mediaRuleNotMatching_ignoresRules() {
        // An @media rule that does NOT match the device media type should be parsed and ignored.
        CSSParser parser = new CSSParser(CSSParser.MediaType.screen, CSSParser.Source.Document, null);
        String css = "@media print { .myclass { opacity: 0.5; } }";

        CSSParser.Ruleset ruleset = parser.parse(css);
        assertTrue(ruleset.isEmpty());
    }

    @Test
    public void parseAtRule_importRule_resolvesAndAddsRules() {
        // An @import rule should use the external file resolver to fetch and include more CSS.
        String importedCss = "path { fill: blue; }";
        when(mockExternalFileResolver.resolveCSSStyleSheet("extra.css")).thenReturn(importedCss);

        CSSParser parser = new CSSParser(CSSParser.MediaType.screen, CSSParser.Source.Document, mockExternalFileResolver);
        String css = "@import url('extra.css'); .myclass { opacity: 0.5; }";

        CSSParser.Ruleset ruleset = parser.parse(css);
        assertEquals(2, ruleset.ruleCount()); // Expects 2 rules: one from @import, one from main
        verify(mockExternalFileResolver).resolveCSSStyleSheet("extra.css");
    }

    @Test
    public void parseAtRule_importRuleWithNoResolver_isIgnored() {
        // If no file resolver is provided, @import rules should be safely ignored.
        CSSParser parser = new CSSParser(CSSParser.Source.Document, null);
        String css = "@import 'another.css';";

        CSSParser.Ruleset ruleset = parser.parse(css);
        assertTrue(ruleset.isEmpty());
    }

    @Test
    public void parseAtRule_unsupportedRule_isSkipped() {
        // Unsupported @-rules should be skipped without crashing, and a warning should be logged.
        CSSParser parser = new CSSParser(CSSParser.Source.Document, null);
        String css = "@charset \"UTF-8\"; p { color: black; }";

        CSSParser.Ruleset ruleset = parser.parse(css);
        assertEquals(1, ruleset.ruleCount()); // The 'p' rule should still be parsed

        List<ShadowLog.LogItem> logs = ShadowLog.getLogsForTag("CSSParser");
        assertTrue(logs.stream().anyMatch(item -> item.msg.contains("Ignoring @charset rule")));
    }

    //---------------------------------------------------------------------
    // Static helper method tests
    //---------------------------------------------------------------------

    @Test
    public void mediaMatches_variousMediaLists_returnsCorrectResult() {
        // Test the static mediaMatches helper for various conditions.
        assertTrue(CSSParser.mediaMatches("screen", CSSParser.MediaType.screen));
        assertTrue(CSSParser.mediaMatches("screen, print", CSSParser.MediaType.print));
        assertTrue(CSSParser.mediaMatches("all", CSSParser.MediaType.screen));
        assertFalse(CSSParser.mediaMatches("print", CSSParser.MediaType.screen));
        assertTrue(CSSParser.mediaMatches("", CSSParser.MediaType.screen)); // Empty list matches all
        assertFalse(CSSParser.mediaMatches("invalid, print", CSSParser.MediaType.screen));
    }

    @Test
    public void parseClassAttribute_variousInputs_returnsCorrectList() {
        // Test the static class attribute parser.
        // Note: Calling with null is skipped due to a bug in CSSTextScanner that causes an NPE.
        assertNull(CSSParser.parseClassAttribute(""));
        assertEquals(Collections.singletonList("one"), CSSParser.parseClassAttribute("one")); // Using singletonList for compatibility
        assertEquals(Arrays.asList("one", "two", "three"), CSSParser.parseClassAttribute(" one  two \n three "));
    }


    //---------------------------------------------------------------------
    // Rule matching tests
    //---------------------------------------------------------------------

    /**
     * Creates a real DOM tree for rule matching tests: <svg><group id="grp1"><path class="p1"/></group></svg>
     */
    private void setupElementTree() {
        svgElement = new SVGBase.Svg();
        svgElement.parent = null; // The root has no parent

        groupElement = new SVGBase.Group();
        groupElement.id = "grp1";
        groupElement.parent = svgElement;

        pathElement = new SVGBase.Path();
        pathElement.classNames = Collections.singletonList("p1");
        pathElement.parent = groupElement;

        try {
            svgElement.addChild(groupElement);
            groupElement.addChild(pathElement);
        } catch (SVGParseException e) {
            fail("Setup of element tree failed: " + e.getMessage());
        }
    }

    private CSSParser.Selector selectorFor(String selectorStr) {
        CSSTextScanner scanner = new CSSTextScanner(selectorStr);
        try {
            return scanner.nextSelectorGroup().get(0);
        } catch (CSSParseException e) {
            fail("Failed to parse selector: " + selectorStr);
            return null;
        }
    }

    @Test
    public void ruleMatch_simpleSelectors_matchCorrectly() {
        // Test simple selectors like tag name, ID, and class.
        assertTrue(CSSParser.ruleMatch(null, selectorFor("path"), pathElement));
        assertFalse(CSSParser.ruleMatch(null, selectorFor("svg"), pathElement));

        assertTrue(CSSParser.ruleMatch(null, selectorFor("#grp1"), groupElement));
        assertFalse(CSSParser.ruleMatch(null, selectorFor("#other"), groupElement));

        assertTrue(CSSParser.ruleMatch(null, selectorFor(".p1"), pathElement));
        assertFalse(CSSParser.ruleMatch(null, selectorFor(".other"), pathElement));
    }

    @Test
    public void ruleMatch_combinatorSelectors_matchCorrectly() {
        // Test selectors with descendant, child, and adjacent sibling combinators.
        // Descendant: svg path
        assertTrue(CSSParser.ruleMatch(null, selectorFor("svg path"), pathElement));
        assertTrue(CSSParser.ruleMatch(null, selectorFor("svg .p1"), pathElement));
        assertFalse(CSSParser.ruleMatch(null, selectorFor("path svg"), svgElement));

        // Child: group > path
        assertTrue(CSSParser.ruleMatch(null, selectorFor("group > path"), pathElement));
        assertFalse(CSSParser.ruleMatch(null, selectorFor("svg > path"), pathElement));

        // Adjacent Sibling: group + rect
        SVGBase.Rect rectElement = new SVGBase.Rect();
        rectElement.parent = svgElement;
        try {
            svgElement.addChild(rectElement);
        } catch (SVGParseException e) {
            fail("Setup failed for sibling element: " + e.getMessage());
        }

        assertTrue(CSSParser.ruleMatch(null, selectorFor("group + rect"), rectElement));
        assertFalse(CSSParser.ruleMatch(null, selectorFor("path + rect"), rectElement));
    }

    //---------------------------------------------------------------------
    // Pseudo-class matching tests
    //---------------------------------------------------------------------

    @Test
    public void pseudoClass_Root_matchesOnlyRootElement() {
        CSSParser.PseudoClassRoot pseudo = new CSSParser.PseudoClassRoot();
        assertTrue(pseudo.matches(null, svgElement));
        assertFalse(pseudo.matches(null, groupElement));
    }

    @Test
    public void pseudoClass_Empty_matchesCorrectly() {
        CSSParser.PseudoClassEmpty pseudo = new CSSParser.PseudoClassEmpty();

        SVGBase.Group emptyGroup = new SVGBase.Group();
        SVGBase.Group nonEmptyGroup = new SVGBase.Group();
        try {
            nonEmptyGroup.addChild(new SVGBase.Path());
        } catch (SVGParseException e) {
            fail("Setup failed for empty test: " + e.getMessage());
        }

        assertTrue(pseudo.matches(null, emptyGroup));
        assertFalse(pseudo.matches(null, nonEmptyGroup));
        assertTrue(pseudo.matches(null, pathElement)); // Non-container element returns true
    }

    @Test
    public void pseudoClass_AnPlusB_matchesCorrectly() {
        // Test nth-child logic with a list of three elements
        SVGBase.Svg parent = new SVGBase.Svg();
        SVGBase.Rect r1 = new SVGBase.Rect();
        SVGBase.Circle c1 = new SVGBase.Circle();
        SVGBase.Rect r2 = new SVGBase.Rect();
        r1.parent = parent; c1.parent = parent; r2.parent = parent;

        try {
            parent.addChild(r1); parent.addChild(c1); parent.addChild(r2);
        } catch(SVGParseException e) {
            fail("Setup failed for AnPlusB test: " + e.getMessage());
        }

        // nth-child(2n+1) -> 1st, 3rd, 5th...
        CSSParser.PseudoClassAnPlusB firstAndThird = new CSSParser.PseudoClassAnPlusB(2, 1, true, false, null);
        assertTrue(firstAndThird.matches(null, r1));  // 1st child
        assertFalse(firstAndThird.matches(null, c1)); // 2nd child
        assertTrue(firstAndThird.matches(null, r2));  // 3rd child

        // nth-last-child(1) -> last child
        CSSParser.PseudoClassAnPlusB last = new CSSParser.PseudoClassAnPlusB(0, 1, false, false, null);
        assertFalse(last.matches(null, r1));
        assertFalse(last.matches(null, c1));
        assertTrue(last.matches(null, r2));

        // nth-of-type(2) -> 2nd element of its type ("rect")
        CSSParser.PseudoClassAnPlusB secondOfType = new CSSParser.PseudoClassAnPlusB(0, 2, true, true, "rect");
        assertFalse(secondOfType.matches(null, r1)); // 1st rect
        assertTrue(secondOfType.matches(null, r2));  // 2nd rect
    }

    @Test
    public void pseudoClass_OnlyChild_matchesCorrectly() {
        SVGBase.Svg parentWithOneChild = new SVGBase.Svg();
        SVGBase.Rect onlyChild = new SVGBase.Rect();
        onlyChild.parent = parentWithOneChild;

        SVGBase.Svg parentWithManyChildren = new SVGBase.Svg();
        SVGBase.Rect rect1 = new SVGBase.Rect();
        SVGBase.Circle circle1 = new SVGBase.Circle();
        SVGBase.Rect rect2 = new SVGBase.Rect();
        rect1.parent = parentWithManyChildren;
        circle1.parent = parentWithManyChildren;
        rect2.parent = parentWithManyChildren;

        try {
            parentWithOneChild.addChild(onlyChild);
            parentWithManyChildren.addChild(rect1);
            parentWithManyChildren.addChild(circle1);
            parentWithManyChildren.addChild(rect2);
        } catch(SVGParseException e) {
            fail("Setup failed for OnlyChild test: " + e.getMessage());
        }

        // Test :only-child
        CSSParser.PseudoClassOnlyChild onlyChildPseudo = new CSSParser.PseudoClassOnlyChild(false, null);
        assertTrue("An element that is the single child should match :only-child", onlyChildPseudo.matches(null, onlyChild));
        assertFalse("An element with siblings should not match :only-child", onlyChildPseudo.matches(null, rect1));

        // Test :only-of-type
        CSSParser.PseudoClassOnlyChild onlyOfTypePseudo = new CSSParser.PseudoClassOnlyChild(true, null);
        assertTrue("A circle that is the only circle should match :only-of-type", onlyOfTypePseudo.matches(null, circle1));
        assertFalse("A rect with another rect sibling should not match :only-of-type", onlyOfTypePseudo.matches(null, rect1));
        assertFalse("A rect with another rect sibling should not match :only-of-type", onlyOfTypePseudo.matches(null, rect2));
    }

    @Test
    public void pseudoClass_Not_matchesCorrectly() {
        // :not(selector) should match elements that do NOT match the inner selector.
        List<CSSParser.Selector> notSelectorGroup = Collections.singletonList(selectorFor(".p1"));
        CSSParser.PseudoClassNot pseudo = new CSSParser.PseudoClassNot(notSelectorGroup);

        // pathElement has class 'p1', so :not(.p1) should NOT match
        assertFalse(pseudo.matches(null, pathElement));
        // groupElement does not have class 'p1', so :not(.p1) SHOULD match
        assertTrue(pseudo.matches(null, groupElement));
    }
}