// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser.utils;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import net.pubnative.lite.sdk.utils.svgparser.SVGExternalFileResolver;
import net.pubnative.lite.sdk.utils.svgparser.SVGParseException;

/**
 * Unit tests for the SVGAndroidRenderer class.
 * This class uses Robolectric to simulate the Android environment and Mockito to verify
 * interactions with the Canvas object.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P})
public class SVGAndroidRendererTest {

    @Mock
    private Canvas mockCanvas;
    @Mock
    private SVGExternalFileResolver mockFileResolver;

    private SVGAndroidRenderer renderer;
    private SVGBase document;
    private SVGBase.Svg rootElement;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        renderer = new SVGAndroidRenderer(mockCanvas, 96f, mockFileResolver);

        // Basic SVG document structure for tests
        document = new SVGBase(true, mockFileResolver);
        rootElement = new SVGBase.Svg();
        rootElement.document = document; // Set document reference
        rootElement.width = new SVGBase.Length(100f);
        rootElement.height = new SVGBase.Length(100f);
        document.setRootElement(rootElement);

        // Mock the canvas matrix to avoid NPEs
        when(mockCanvas.getMatrix()).thenReturn(new Matrix());
    }

    private RenderOptionsBase simpleRenderOptions() {
        RenderOptionsBase options = new RenderOptionsBase();
        options.viewPort(0, 0, 100, 100);
        return options;
    }

    //---------------------------------------------------------------------
    // Core Rendering Workflow Tests
    //---------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void renderDocument_withNullOptions_throwsNPE() {
        // The method has a check for null RenderOptions.
        renderer.renderDocument(document, null);
    }

    @Test
    public void renderDocument_withEmptyDocument_doesNotCrash() {
        // If the root element is null, it should warn and exit gracefully.
        document.setRootElement(null);
        renderer.renderDocument(document, simpleRenderOptions());
        // Verify no drawing commands were issued
        verify(mockCanvas, never()).drawPath(any(), any());
    }

    @Test
    public void render_dispatchesToCorrectShapeRenderer() {
        // Verifies that the main render(SvgObject) method dispatches to the correct private render method.
        // We test this by checking that a Path object gets a drawPath call.
        SVGBase.Path path = new SVGBase.Path();
        path.document = document;
        path.parent = rootElement;
        path.d = new SVGBase.PathDefinition();
        path.d.moveTo(10, 10);
        path.d.lineTo(20, 20);

        try {
            rootElement.addChild(path);
        } catch (SVGParseException e) { fail(); }

        renderer.renderDocument(document, simpleRenderOptions());

        // Verify that the canvas was asked to draw a path
        verify(mockCanvas).drawPath(any(Path.class), any(Paint.class));
    }

    //---------------------------------------------------------------------
    // Style and Transform Tests
    //---------------------------------------------------------------------

    @Test
    public void render_appliesFillAndStrokeStyle() {
        ArgumentCaptor<Paint> paintCaptor = ArgumentCaptor.forClass(Paint.class);

        SVGBase.Rect rect = new SVGBase.Rect();
        rect.document = document;
        rect.parent = rootElement;
        rect.width = new SVGBase.Length(10f);
        rect.height = new SVGBase.Length(10f);
        rect.style = new Style();
        Style.processStyleProperty(rect.style, "fill", "#ff0000", false); // Red fill
        Style.processStyleProperty(rect.style, "stroke", "#00ff00", false); // Green stroke
        Style.processStyleProperty(rect.style, "stroke-width", "2", false);

        try {
            rootElement.addChild(rect);
        } catch (SVGParseException e) { fail(); }

        renderer.renderDocument(document, simpleRenderOptions());

        // Verify drawPath is called twice (once for fill, once for stroke) and capture the paints used
        verify(mockCanvas, times(2)).drawPath(any(Path.class), paintCaptor.capture());

        Paint fillPaint = paintCaptor.getAllValues().get(0);
        Paint strokePaint = paintCaptor.getAllValues().get(1);

        assertEquals("Fill paint should be red", 0xffff0000, fillPaint.getColor());
        assertEquals("Stroke paint should be green", 0xff00ff00, strokePaint.getColor());
        assertEquals("Stroke width should be 2", 2f, strokePaint.getStrokeWidth(), 0f);
    }

    @Test
    public void render_appliesGroupTransform() {
        Matrix expectedMatrix = new Matrix();
        expectedMatrix.setTranslate(50, 50);

        SVGBase.Group group = new SVGBase.Group();
        group.document = document;
        group.parent = rootElement;
        group.transform = expectedMatrix;

        SVGBase.Rect rect = new SVGBase.Rect();
        rect.document = document;
        rect.parent = group;
        rect.width = new SVGBase.Length(10f);
        rect.height = new SVGBase.Length(10f);

        try {
            rootElement.addChild(group);
            group.addChild(rect);
        } catch (SVGParseException e) { fail(); }

        renderer.renderDocument(document, simpleRenderOptions());

        // Verify that the transform was applied to the canvas
        verify(mockCanvas).concat(eq(expectedMatrix));
    }

    @Test
    public void render_handlesStyleInheritance() {
        ArgumentCaptor<Paint> paintCaptor = ArgumentCaptor.forClass(Paint.class);

        // Group has red fill
        SVGBase.Group group = new SVGBase.Group();
        group.document = document;
        group.parent = rootElement;
        group.style = new Style();
        Style.processStyleProperty(group.style, "fill", "red", false);

        // Child rect should inherit the red fill
        SVGBase.Rect rect = new SVGBase.Rect();
        rect.document = document;
        rect.parent = group;
        rect.width = new SVGBase.Length(10f);
        rect.height = new SVGBase.Length(10f);

        try {
            rootElement.addChild(group);
            group.addChild(rect);
        } catch (SVGParseException e) { fail(); }

        renderer.renderDocument(document, simpleRenderOptions());

        // Capture the paint used to draw the rect
        verify(mockCanvas).drawPath(any(Path.class), paintCaptor.capture());

        Paint rectPaint = paintCaptor.getValue();
        // 0xffff0000 is ARGB for opaque red
        assertEquals("Rect should inherit the red fill from the group", 0xffff0000, rectPaint.getColor());
    }

    //---------------------------------------------------------------------
    // Structural Element Tests
    //---------------------------------------------------------------------

    @Test
    public void render_handlesUseElement() {
        // A <use> element should render a copy of a referenced element from <defs>.
        SVGBase.Defs defs = new SVGBase.Defs();
        defs.document = document;
        defs.parent = rootElement;

        SVGBase.Rect templateRect = new SVGBase.Rect();
        templateRect.document = document;
        templateRect.parent = defs;
        templateRect.id = "template";
        templateRect.width = new SVGBase.Length(20f);
        templateRect.height = new SVGBase.Length(20f);

        SVGBase.Use useElement = new SVGBase.Use();
        useElement.document = document;
        useElement.parent = rootElement;
        useElement.href = "#template";

        try {
            rootElement.addChild(defs);
            defs.addChild(templateRect);
            rootElement.addChild(useElement);
        } catch (SVGParseException e) { fail(); }

        renderer.renderDocument(document, simpleRenderOptions());

        // Verify that a path was drawn, which means the <use> element was resolved and rendered.
        verify(mockCanvas).drawPath(any(Path.class), any(Paint.class));
    }

    @Test
    public void render_handlesSwitchElement() {
        // A <switch> should render the first child that passes conditional checks.
        SVGBase.Switch switchElement = new SVGBase.Switch();
        switchElement.document = document;
        switchElement.parent = rootElement;

        // This circle requires an unsupported feature ("Animation")
        SVGBase.Circle circle = new SVGBase.Circle();
        circle.document = document;
        circle.parent = switchElement;
        circle.r = new SVGBase.Length(5f);
        circle.setRequiredFeatures(java.util.Collections.singleton("Animation"));

        // This rect requires a supported feature ("Structure")
        SVGBase.Rect rect = new SVGBase.Rect();
        rect.document = document;
        rect.parent = switchElement;
        rect.width = new SVGBase.Length(10f);
        rect.height = new SVGBase.Length(10f);
        rect.setRequiredFeatures(java.util.Collections.singleton("Structure"));

        try {
            rootElement.addChild(switchElement);
            switchElement.addChild(circle); // Should be skipped
            switchElement.addChild(rect);   // Should be rendered
        } catch (SVGParseException e) { fail(); }

        renderer.renderDocument(document, simpleRenderOptions());

        // Verify that a path was drawn (for the rect). If the circle was drawn, or both, or neither,
        // this test would fail. We expect exactly one drawPath call for the fill of the rect.
        verify(mockCanvas, times(1)).drawPath(any(Path.class), any(Paint.class));
    }
}