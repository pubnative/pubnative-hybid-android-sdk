// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.verify;

import net.pubnative.lite.sdk.utils.svgparser.utils.SVGBase;

/**
 * Unit tests for the SVG facade class.
 * NOTE: This test requires the SVG(SVGBase) constructor to be package-private.
 */
@RunWith(MockitoJUnitRunner.class)
public class SVGTest {

    @Mock
    private SVGBase mockSVGBase;

    @Test
    public void renderToPicture_noArgs_callsBaseMethod() {
        // Create an instance of SVG by injecting the mock base class
        SVG svg = new SVG(mockSVGBase);

        // Call the method to be tested
        svg.renderToPicture();

        // Verify that the call was correctly passed to the SVGBase object
        verify(mockSVGBase).renderToPicture(null);
    }

    @Test
    public void renderToPicture_withDimensions_callsBaseMethod() {
        SVG svg = new SVG(mockSVGBase);

        svg.renderToPicture(800, 600);

        // Verify that the call was passed through to the correct overload
        verify(mockSVGBase).renderToPicture(800, 600, null);
    }

    @Test
    public void renderToPicture_withDimensionsAndOptions_callsBaseMethod() {
        SVG svg = new SVG(mockSVGBase);
        RenderOptions options = new RenderOptions();

        svg.renderToPicture(1024, 768, options);

        // Verify that all arguments were passed through correctly
        verify(mockSVGBase).renderToPicture(1024, 768, options);
    }
}