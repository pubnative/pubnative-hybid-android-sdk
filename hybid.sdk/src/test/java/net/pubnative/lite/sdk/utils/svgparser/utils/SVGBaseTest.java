// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser.utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import net.pubnative.lite.sdk.utils.svgparser.PreserveAspectRatio;
import net.pubnative.lite.sdk.utils.svgparser.SVGExternalFileResolver;
import net.pubnative.lite.sdk.utils.svgparser.SVGParseException;

/**
 * Consolidated unit tests for the SVGBase class and its important inner classes.
 * This class uses nested classes to organize tests for each component.
 */
@RunWith(Enclosed.class)
public class SVGBaseTest {

    /**
     * Tests for the main logic of the SVGBase class itself.
     */
    @RunWith(RobolectricTestRunner.class)
    @Config(sdk = 28)
    public static class SVGBaseMainLogicTest {

        private SVGBase document;
        private SVGBase.Svg rootElement;

        @Mock
        private SVGExternalFileResolver mockFileResolver;

        @Before
        public void setUp() {
            MockitoAnnotations.openMocks(this);
            document = new SVGBase(true, mockFileResolver);
            rootElement = new SVGBase.Svg();
            rootElement.document = document;
            document.setRootElement(rootElement);
        }

        @Test
        public void getDocumentPreserveAspectRatio_returnsCorrectly() {
            // Test success path using a public static instance, since the constructor is not public.
            PreserveAspectRatio pa = PreserveAspectRatio.LETTERBOX;
            rootElement.preserveAspectRatio = pa;
            assertSame(pa, document.getDocumentPreserveAspectRatio());

            // Test when null
            rootElement.preserveAspectRatio = null;
            assertNull(document.getDocumentPreserveAspectRatio());
        }

        @Test(expected = IllegalArgumentException.class)
        public void getDocumentPreserveAspectRatio_withNullRoot_throwsException() {
            // Test failure path
            document.setRootElement(null);
            document.getDocumentPreserveAspectRatio();
        }

        @Test
        public void cssManagement_addAndRetrieveRules() {
            assertFalse(document.hasCSSRules());

            CSSParser.Ruleset ruleset = new CSSParser.Ruleset();
            ruleset.add(new CSSParser.Rule(new CSSParser.Selector(), new Style(), CSSParser.Source.Document));

            document.addCSSRules(ruleset);

            assertTrue(document.hasCSSRules());
            assertEquals(1, document.getCSSRules().size());
        }

        @Test
        public void cssManagement_clearRenderCSSRules() {
            CSSParser.Ruleset ruleset = new CSSParser.Ruleset();
            ruleset.add(new CSSParser.Rule(new CSSParser.Selector(), new Style(), CSSParser.Source.Document));
            ruleset.add(new CSSParser.Rule(new CSSParser.Selector(), new Style(), CSSParser.Source.RenderOptions));
            document.addCSSRules(ruleset);

            assertEquals(2, document.getCSSRules().size());

            document.clearRenderCSSRules();

            assertEquals(1, document.getCSSRules().size());
            assertEquals(CSSParser.Source.Document, document.getCSSRules().get(0).source);
        }

        @Test
        public void getElementById_findsCorrectElements() throws SVGParseException {
            // Build a simple tree: <svg id="root"><g id="group1"><rect id="rect1"/></g></svg>
            rootElement.id = "root";

            SVGBase.Group group = new SVGBase.Group();
            group.id = "group1";
            group.parent = rootElement;
            group.document = document;

            SVGBase.Rect rect = new SVGBase.Rect();
            rect.id = "rect1";
            rect.parent = group;
            rect.document = document;

            group.addChild(rect);
            rootElement.addChild(group);

            assertSame(rootElement, document.getElementById("root"));
            assertSame(group, document.getElementById("group1"));
            assertSame(rect, document.getElementById("rect1"));
            assertNull(document.getElementById("nonexistent"));

            assertSame(rect, document.getElementById("rect1"));
        }

        @Test
        public void resolveIRI_handlesAllCases() {
            rootElement.id = "myId";

            assertSame(rootElement, document.resolveIRI("#myId"));
            assertSame(rootElement, document.resolveIRI("\"#myId\""));
            assertSame(rootElement, document.resolveIRI("'#myId'"));
            assertNull(document.resolveIRI("http://example.com#myId"));
            assertNull(document.resolveIRI("not-a-hash-ref"));
            assertNull(document.resolveIRI(null));
        }

        @Test
        public void getElementsByTagName_findsAllMatchingElements() throws SVGParseException {
            SVGBase.Rect rect1 = new SVGBase.Rect();
            SVGBase.Group group = new SVGBase.Group();
            SVGBase.Rect rect2 = new SVGBase.Rect();

            group.addChild(rect2);
            rootElement.addChild(rect1);
            rootElement.addChild(group);

            List<SVGBase.SvgObject> results = document.getElementsByTagName("rect");

            assertEquals(2, results.size());
            assertTrue(results.contains(rect1));
            assertTrue(results.contains(rect2));
        }
    }

    /**
     * Tests for the SVGBase.Box inner class.
     * FIXED: Added Robolectric runner so Android RectF works properly.
     */
    @RunWith(RobolectricTestRunner.class)
    @Config(sdk = 28)
    public static class BoxTest {

        @Test
        public void constructors_and_accessors_workCorrectly() {
            SVGBase.Box box1 = new SVGBase.Box(10, 20, 100, 200);
            assertEquals(10f, box1.minX, 0f);
            assertEquals(110f, box1.maxX(), 0f);
            assertEquals(220f, box1.maxY(), 0f);

            SVGBase.Box box2 = new SVGBase.Box(box1);
            assertEquals(10f, box2.minX, 0f);
            assertEquals(110f, box2.maxX(), 0f);
        }

        @Test
        public void fromLimits_createsCorrectBox() {
            SVGBase.Box box = SVGBase.Box.fromLimits(10, 20, 110, 220);
            assertEquals(100f, box.width, 0f);
            assertEquals(200f, box.height, 0f);
        }

        @Test
        public void union_calculatesBoundingBox() {
            SVGBase.Box box1 = new SVGBase.Box(0, 0, 10, 10);
            SVGBase.Box box2 = new SVGBase.Box(5, 5, 10, 10); // Extends to (15, 15)

            box1.union(box2);

            assertEquals(0f, box1.minX, 0f);
            assertEquals(15f, box1.width, 0f);
            assertEquals(15f, box1.height, 0f);
        }

        @Test
        public void toRectF_createsCorrectRectF() {
            SVGBase.Box box = new SVGBase.Box(10, 20, 30, 40);
            android.graphics.RectF rect = box.toRectF();

            // Box constructor: (minX, minY, width, height)
            // Expected RectF: (left, top, right, bottom) = (minX, minY, minX+width, minY+height)
            assertEquals(10f, rect.left, 0f);   // minX
            assertEquals(20f, rect.top, 0f);    // minY
            assertEquals(40f, rect.right, 0f);  // minX + width = 10 + 30 = 40
            assertEquals(60f, rect.bottom, 0f); // minY + height = 20 + 40 = 60
        }

        @Test
        public void box_calculatesCorrectBounds() {
            SVGBase.Box box = new SVGBase.Box(10, 20, 30, 40);

            // Test direct field access and calculated methods
            assertEquals(10f, box.minX, 0f);
            assertEquals(20f, box.minY, 0f);
            assertEquals(30f, box.width, 0f);
            assertEquals(40f, box.height, 0f);
            assertEquals(40f, box.maxX(), 0f);  // minX + width
            assertEquals(60f, box.maxY(), 0f);  // minY + height
        }

        @Test
        public void toString_formatsCorrectly() {
            SVGBase.Box box = new SVGBase.Box(1, 2, 3, 4);
            String result = box.toString();
            assertEquals("[1.0 2.0 3.0 4.0]", result);
        }
    }

    /**
     * Tests for the SVGBase.Length inner class.
     */
    @RunWith(RobolectricTestRunner.class)
    @Config(sdk = 28)
    public static class LengthTest {

        @Mock
        private SVGAndroidRenderer mockRenderer;
        private final SVGBase.Box viewport = new SVGBase.Box(0, 0, 200, 400);

        @Before
        public void setUp() {
            MockitoAnnotations.openMocks(this);
            when(mockRenderer.getDPI()).thenReturn(96f);
            when(mockRenderer.getCurrentFontSize()).thenReturn(16f);
            when(mockRenderer.getCurrentFontXHeight()).thenReturn(8f);
            when(mockRenderer.getEffectiveViewPortInUserUnits()).thenReturn(viewport);
        }

        @Test
        public void booleanChecks_returnCorrectly() {
            assertTrue(new SVGBase.Length(0f).isZero());
            assertFalse(new SVGBase.Length(1f).isZero());
            assertTrue(new SVGBase.Length(-10f).isNegative());
            assertFalse(new SVGBase.Length(10f).isNegative());
        }

        @Test
        public void floatValue_withPhysicalUnits() {
            SVGBase.Length oneInch = new SVGBase.Length(1f, SVGBase.Unit.in);
            assertEquals(96f, oneInch.floatValue(96f), 0f);

            SVGBase.Length oneCm = new SVGBase.Length(2.54f, SVGBase.Unit.cm);
            assertEquals(96f, oneCm.floatValue(mockRenderer), 0.01f);
        }

        @Test
        public void floatValue_withRelativeUnits() {
            SVGBase.Length twoEm = new SVGBase.Length(2f, SVGBase.Unit.em);
            assertEquals(32f, twoEm.floatValueX(mockRenderer), 0f);

            SVGBase.Length tenPercentWidth = new SVGBase.Length(10f, SVGBase.Unit.percent);
            assertEquals(20f, tenPercentWidth.floatValueX(mockRenderer), 0f); // 10% of 200

            SVGBase.Length tenPercentHeight = new SVGBase.Length(10f, SVGBase.Unit.percent);
            assertEquals(40f, tenPercentHeight.floatValueY(mockRenderer), 0f); // 10% of 400
        }

        @Test
        public void floatValue_withMax_calculatesPercentageCorrectly() {
            SVGBase.Length fiftyPercent = new SVGBase.Length(50f, SVGBase.Unit.percent);
            assertEquals(100f, fiftyPercent.floatValue(mockRenderer, 200f), 0f);
        }

        @Test
        public void toString_formatsCorrectly() {
            SVGBase.Length length = new SVGBase.Length(12.5f, SVGBase.Unit.px);
            assertEquals("12.5px", length.toString());
        }

        @Test
        public void constants_haveCorrectValues() {
            assertEquals(0f, SVGBase.Length.ZERO.value, 0f);
            assertEquals(SVGBase.Unit.px, SVGBase.Length.ZERO.unit);

            assertEquals(100f, SVGBase.Length.PERCENT_100.value, 0f);
            assertEquals(SVGBase.Unit.percent, SVGBase.Length.PERCENT_100.unit);
        }
    }

    /**
     * Tests for the SVGBase.PathDefinition inner class.
     */
    @RunWith(RobolectricTestRunner.class)
    @Config(sdk = 28)
    public static class PathDefinitionTest {

        private SVGBase.PathDefinition pathDef;

        private static class TestPathInterface implements SVGBase.PathInterface {
            int moveToCount = 0;
            int lineToCount = 0;
            int cubicToCount = 0;
            int quadToCount = 0;
            int arcToCount = 0;
            int closeCount = 0;

            float lastMoveToX, lastMoveToY;
            float lastLineToX, lastLineToY;
            float lastCubicX1, lastCubicY1, lastCubicX2, lastCubicY2, lastCubicX3, lastCubicY3;
            float lastQuadX1, lastQuadY1, lastQuadX2, lastQuadY2;
            float lastArcRx, lastArcRy, lastArcRotation;
            boolean lastArcLargeFlag, lastArcSweepFlag;
            float lastArcX, lastArcY;

            @Override
            public void moveTo(float x, float y) {
                moveToCount++;
                lastMoveToX = x;
                lastMoveToY = y;
            }

            @Override
            public void lineTo(float x, float y) {
                lineToCount++;
                lastLineToX = x;
                lastLineToY = y;
            }

            @Override
            public void cubicTo(float x1, float y1, float x2, float y2, float x3, float y3) {
                cubicToCount++;
                lastCubicX1 = x1;
                lastCubicY1 = y1;
                lastCubicX2 = x2;
                lastCubicY2 = y2;
                lastCubicX3 = x3;
                lastCubicY3 = y3;
            }

            @Override
            public void quadTo(float x1, float y1, float x2, float y2) {
                quadToCount++;
                lastQuadX1 = x1;
                lastQuadY1 = y1;
                lastQuadX2 = x2;
                lastQuadY2 = y2;
            }

            @Override
            public void arcTo(float rx, float ry, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag, float x, float y) {
                arcToCount++;
                lastArcRx = rx;
                lastArcRy = ry;
                lastArcRotation = xAxisRotation;
                lastArcLargeFlag = largeArcFlag;
                lastArcSweepFlag = sweepFlag;
                lastArcX = x;
                lastArcY = y;
            }

            @Override
            public void close() {
                closeCount++;
            }
        }

        @Before
        public void setUp() {
            pathDef = new SVGBase.PathDefinition();
        }

        @Test
        public void pathCommands_areEnumeratedCorrectly() {
            assertTrue(pathDef.isEmpty());
            pathDef.moveTo(10, 10);
            pathDef.lineTo(50, 50);
            pathDef.cubicTo(60, 60, 70, 70, 80, 80);
            pathDef.quadTo(90, 90, 100, 100);
            pathDef.close();
            assertFalse(pathDef.isEmpty());

            TestPathInterface testPather = new TestPathInterface();
            pathDef.enumeratePath(testPather);

            assertEquals(1, testPather.moveToCount);
            assertEquals(10f, testPather.lastMoveToX, 0f);
            assertEquals(10f, testPather.lastMoveToY, 0f);

            assertEquals(1, testPather.lineToCount);
            assertEquals(50f, testPather.lastLineToX, 0f);
            assertEquals(50f, testPather.lastLineToY, 0f);

            assertEquals(1, testPather.cubicToCount);
            assertEquals(60f, testPather.lastCubicX1, 0f);
            assertEquals(60f, testPather.lastCubicY1, 0f);
            assertEquals(70f, testPather.lastCubicX2, 0f);
            assertEquals(70f, testPather.lastCubicY2, 0f);
            assertEquals(80f, testPather.lastCubicX3, 0f);
            assertEquals(80f, testPather.lastCubicY3, 0f);

            assertEquals(1, testPather.quadToCount);
            assertEquals(90f, testPather.lastQuadX1, 0f);
            assertEquals(90f, testPather.lastQuadY1, 0f);
            assertEquals(100f, testPather.lastQuadX2, 0f);
            assertEquals(100f, testPather.lastQuadY2, 0f);

            assertEquals(1, testPather.closeCount);
        }

        @Test
        public void arcTo_handlesFlags() {
            pathDef.arcTo(10, 20, 45, true, false, 100, 200);
            assertFalse(pathDef.isEmpty());

            TestPathInterface testPather = new TestPathInterface();
            pathDef.enumeratePath(testPather);

            assertEquals(1, testPather.arcToCount);
            assertEquals(10f, testPather.lastArcRx, 0f);
            assertEquals(20f, testPather.lastArcRy, 0f);
            assertEquals(45f, testPather.lastArcRotation, 0f);
            assertTrue(testPather.lastArcLargeFlag);
            assertFalse(testPather.lastArcSweepFlag);
            assertEquals(100f, testPather.lastArcX, 0f);
            assertEquals(200f, testPather.lastArcY, 0f);
        }

        @Test
        public void pathDefinition_expandsArrays() {
            // Test that internal arrays expand when needed
            for (int i = 0; i < 20; i++) {
                pathDef.moveTo(i, i);
                pathDef.lineTo(i + 1, i + 1);
            }
            assertFalse(pathDef.isEmpty());
        }
    }

    /**
     * Tests for SVGBase.Colour inner class.
     */
    public static class ColourTest {

        @Test
        public void constants_haveCorrectValues() {
            assertEquals(0xff000000, SVGBase.Colour.BLACK.colour);
            assertEquals(0, SVGBase.Colour.TRANSPARENT.colour);
        }

        @Test
        public void toString_formatsCorrectly() {
            SVGBase.Colour red = new SVGBase.Colour(0xffff0000);
            assertEquals("#ffff0000", red.toString());
        }
    }

    /**
     * Tests for SVGBase.CurrentColor inner class.
     */
    public static class CurrentColorTest {

        @Test
        public void getInstance_returnsSameInstance() {
            SVGBase.CurrentColor instance1 = SVGBase.CurrentColor.getInstance();
            SVGBase.CurrentColor instance2 = SVGBase.CurrentColor.getInstance();
            assertSame(instance1, instance2);
        }
    }

    /**
     * Tests for SVGBase.PaintReference inner class.
     */
    public static class PaintReferenceTest {

        @Test
        public void constructor_setsFields() {
            SVGBase.Colour fallback = SVGBase.Colour.BLACK;
            SVGBase.PaintReference paintRef = new SVGBase.PaintReference("#myGradient", fallback);

            assertEquals("#myGradient", paintRef.href);
            assertSame(fallback, paintRef.fallback);
        }

        @Test
        public void toString_includesBothValues() {
            SVGBase.PaintReference paintRef = new SVGBase.PaintReference("#grad", SVGBase.Colour.BLACK);
            String result = paintRef.toString();

            assertTrue(result.contains("#grad"));
            assertTrue(result.contains("#ff000000")); // BLACK's toString
        }
    }

    /**
     * Tests for SVGBase.CSSClipRect inner class.
     */
    public static class CSSClipRectTest {

        @Test
        public void constructor_setsFields() {
            SVGBase.Length top = new SVGBase.Length(10);
            SVGBase.Length right = new SVGBase.Length(20);
            SVGBase.Length bottom = new SVGBase.Length(30);
            SVGBase.Length left = new SVGBase.Length(40);

            SVGBase.CSSClipRect clipRect = new SVGBase.CSSClipRect(top, right, bottom, left);

            assertSame(top, clipRect.top);
            assertSame(right, clipRect.right);
            assertSame(bottom, clipRect.bottom);
            assertSame(left, clipRect.left);
        }
    }
}