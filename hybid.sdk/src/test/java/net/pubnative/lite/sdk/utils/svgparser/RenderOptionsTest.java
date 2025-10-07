// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import net.pubnative.lite.sdk.utils.svgparser.utils.RenderOptionsBase;

/**
 * Unit tests for the RenderOptions and RenderOptionsBase classes.
 * It verifies the fluent setters and state-checking methods using only the public API.
 */
public class RenderOptionsTest {

    private RenderOptions renderOptions;

    @Before
    public void setUp() {
        renderOptions = new RenderOptions();
    }

    @Test
    public void create_and_default_constructor_workCorrectly() {
        assertNotNull("RenderOptions.create() should return a new instance", RenderOptions.create());
        assertNotNull("Default constructor should create a new instance", new RenderOptions());
    }

    @Test
    public void css_setsValueAndHasCssReturnsTrue() {
        assertFalse(renderOptions.hasCss());

        // Test with String
        RenderOptions result = renderOptions.css("h1 { fill: red; }");
        assertSame("Should return the same instance for fluency", renderOptions, result);
        assertTrue(renderOptions.hasCss());

        // Note: The test for css(CSS) has been removed because its dependencies
        // (CSSParser.Ruleset, CSSParser.Selector) are not public.
    }

    @Test
    public void hasCss_handlesEmptyAndWhitespaceStrings() {
        // Start with a known "true" state
        renderOptions.css("h1 { fill: red; }");
        assertTrue("hasCss should be true for a valid string", renderOptions.hasCss());

        // Test invalid states
        renderOptions.css("");
        assertFalse("hasCss should be false for empty string", renderOptions.hasCss());

        renderOptions.css("   ");
        assertFalse("hasCss should be false for whitespace-only string", renderOptions.hasCss());

        renderOptions.css((String) null);
        assertFalse("hasCss should be false for null string", renderOptions.hasCss());
    }

    @Test
    public void preserveAspectRatio_setsValueAndHasReturnsTrue() {
        assertFalse(renderOptions.hasPreserveAspectRatio());

        RenderOptions result = renderOptions.preserveAspectRatio(PreserveAspectRatio.LETTERBOX);
        assertSame(renderOptions, result);
        assertTrue(renderOptions.hasPreserveAspectRatio());
    }

    @Test
    public void view_setsValueAndHasReturnsTrue() {
        assertFalse(renderOptions.hasView());

        RenderOptions result = renderOptions.view("my-view-id");
        assertSame(renderOptions, result);
        assertTrue(renderOptions.hasView());
    }

    @Test
    public void viewBox_setsValueAndHasReturnsTrue() {
        assertFalse(renderOptions.hasViewBox());

        RenderOptions result = renderOptions.viewBox(10f, 20f, 100f, 200f);
        assertSame(renderOptions, result);
        assertTrue(renderOptions.hasViewBox());
        // Note: Assertions that access the private 'viewBox' field have been removed.
    }

    @Test
    public void viewPort_setsValueAndHasReturnsTrue() {
        assertFalse(renderOptions.hasViewPort());

        RenderOptions result = renderOptions.viewPort(0f, 0f, 800f, 600f);
        assertSame(renderOptions, result);
        assertTrue(renderOptions.hasViewPort());
        // Note: Assertions that access the private 'viewPort' field have been removed.
    }

    @Test
    public void target_setsValueAndHasReturnsTrue() {
        assertFalse(renderOptions.hasTarget());

        RenderOptions result = renderOptions.target("element-id-for-target");
        assertSame(renderOptions, result);
        assertTrue(renderOptions.hasTarget());
    }

    @Test
    public void copyConstructor_copiesAllProperties() {
        // This test directly verifies the RenderOptionsBase copy constructor
        RenderOptionsBase original = new RenderOptionsBase();
        original.css("h1 { fill:red; }");
        original.viewBox(10, 20, 80, 90);
        original.view("myView");
        original.target("myTarget");
        original.preserveAspectRatio(PreserveAspectRatio.LETTERBOX);
        original.viewPort(0, 0, 100, 100);

        RenderOptionsBase copy = new RenderOptionsBase(original);

        assertEquals(original.hasCss(), copy.hasCss());
        assertEquals(original.hasViewBox(), copy.hasViewBox());
        assertEquals(original.hasView(), copy.hasView());
        assertEquals(original.hasTarget(), copy.hasTarget());
        assertEquals(original.hasPreserveAspectRatio(), copy.hasPreserveAspectRatio());
        assertEquals(original.hasViewPort(), copy.hasViewPort());
    }

    @Test
    public void fluentSetters_canBeChained() {
        // This test verifies that the fluent interface works correctly when chained.
        assertFalse(renderOptions.hasView());
        assertFalse(renderOptions.hasViewBox());
        assertFalse(renderOptions.hasTarget());

        RenderOptions result = renderOptions.view("myView")
                .viewBox(0, 0, 100, 100)
                .target("myTarget");

        // Check that the return value is the same instance
        assertSame(renderOptions, result);

        // Check that all properties were set
        assertTrue(renderOptions.hasView());
        assertTrue(renderOptions.hasViewBox());
        assertTrue(renderOptions.hasTarget());
    }

    @Test
    public void individualSetters_and_hasMethods_workCorrectly() {
        // This test verifies each setter and its corresponding has...() method individually.
        assertFalse(renderOptions.hasPreserveAspectRatio());
        renderOptions.preserveAspectRatio(PreserveAspectRatio.LETTERBOX);
        assertTrue(renderOptions.hasPreserveAspectRatio());

        assertFalse(renderOptions.hasView());
        renderOptions.view("my-view-id");
        assertTrue(renderOptions.hasView());

        assertFalse(renderOptions.hasViewBox());
        renderOptions.viewBox(10f, 20f, 100f, 200f);
        assertTrue(renderOptions.hasViewBox());

        assertFalse(renderOptions.hasViewPort());
        renderOptions.viewPort(0f, 0f, 800f, 600f);
        assertTrue(renderOptions.hasViewPort());

        assertFalse(renderOptions.hasTarget());
        renderOptions.target("element-id-for-target");
        assertTrue(renderOptions.hasTarget());
    }
}