// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the PreserveAspectRatio class.
 * This class verifies the parsing logic, public constants, and object behavior.
 */
public class PreserveAspectRatioTest {

    //---------------------------------------------------------------------
    // Tests for Public Static Constants
    //---------------------------------------------------------------------

    @Test
    public void publicConstants_haveCorrectValues() {
        // This test now covers every public static constant
        assertNull(PreserveAspectRatio.UNSCALED.getAlignment());
        assertNull(PreserveAspectRatio.UNSCALED.getScale());

        assertEquals(PreserveAspectRatio.Alignment.none, PreserveAspectRatio.STRETCH.getAlignment());
        assertNull(PreserveAspectRatio.STRETCH.getScale());

        assertEquals(PreserveAspectRatio.Alignment.xMidYMid, PreserveAspectRatio.LETTERBOX.getAlignment());
        assertEquals(PreserveAspectRatio.Scale.meet, PreserveAspectRatio.LETTERBOX.getScale());

        assertEquals(PreserveAspectRatio.Alignment.xMinYMin, PreserveAspectRatio.START.getAlignment());
        assertEquals(PreserveAspectRatio.Scale.meet, PreserveAspectRatio.START.getScale());

        assertEquals(PreserveAspectRatio.Alignment.xMaxYMax, PreserveAspectRatio.END.getAlignment());
        assertEquals(PreserveAspectRatio.Scale.meet, PreserveAspectRatio.END.getScale());

        assertEquals(PreserveAspectRatio.Alignment.xMidYMin, PreserveAspectRatio.TOP.getAlignment());
        assertEquals(PreserveAspectRatio.Scale.meet, PreserveAspectRatio.TOP.getScale());

        assertEquals(PreserveAspectRatio.Alignment.xMidYMax, PreserveAspectRatio.BOTTOM.getAlignment());
        assertEquals(PreserveAspectRatio.Scale.meet, PreserveAspectRatio.BOTTOM.getScale());

        assertEquals(PreserveAspectRatio.Alignment.xMidYMid, PreserveAspectRatio.FULLSCREEN.getAlignment());
        assertEquals(PreserveAspectRatio.Scale.slice, PreserveAspectRatio.FULLSCREEN.getScale());

        assertEquals(PreserveAspectRatio.Alignment.xMinYMin, PreserveAspectRatio.FULLSCREEN_START.getAlignment());
        assertEquals(PreserveAspectRatio.Scale.slice, PreserveAspectRatio.FULLSCREEN_START.getScale());
    }

    //---------------------------------------------------------------------
    // Tests for the static 'of()' parser method
    //---------------------------------------------------------------------

    @Test
    public void of_parsesCorrectly() {
        // Test alignment only
        PreserveAspectRatio result1 = PreserveAspectRatio.of("xMaxYMin");
        assertEquals(PreserveAspectRatio.Alignment.xMaxYMin, result1.getAlignment());
        assertNull("Scale should be null when not specified", result1.getScale());

        // Test alignment and "meet"
        PreserveAspectRatio result2 = PreserveAspectRatio.of("xMinYMid meet");
        assertEquals(PreserveAspectRatio.Alignment.xMinYMid, result2.getAlignment());
        assertEquals(PreserveAspectRatio.Scale.meet, result2.getScale());

        // Test alignment and "slice"
        PreserveAspectRatio result3 = PreserveAspectRatio.of("xMidYMax slice");
        assertEquals(PreserveAspectRatio.Alignment.xMidYMax, result3.getAlignment());
        assertEquals(PreserveAspectRatio.Scale.slice, result3.getScale());

        // Test with "defer" keyword
        PreserveAspectRatio result4 = PreserveAspectRatio.of("defer xMidYMid meet");
        assertEquals(PreserveAspectRatio.Alignment.xMidYMid, result4.getAlignment());
        assertEquals(PreserveAspectRatio.Scale.meet, result4.getScale());

        // Test with extra whitespace
        PreserveAspectRatio result5 = PreserveAspectRatio.of("  xMinYMin  slice  ");
        assertEquals(PreserveAspectRatio.Alignment.xMinYMin, result5.getAlignment());
        assertEquals(PreserveAspectRatio.Scale.slice, result5.getScale());
    }

    @Test(expected = IllegalArgumentException.class)
    public void of_withInvalidAlignment_throwsException() {
        PreserveAspectRatio.of("invalidAlignment meet");
    }

    @Test(expected = IllegalArgumentException.class)
    public void of_withInvalidScale_throwsException() {
        // The parser logic for this case actually throws SVGParseException.
        // The of() method correctly wraps it in an IllegalArgumentException.
        PreserveAspectRatio.of("xMidYMid invalidScale");
    }

    //---------------------------------------------------------------------
    // Tests for instance methods (equals, toString)
    //---------------------------------------------------------------------

    @Test
    public void equals_behavesCorrectly() {
        PreserveAspectRatio pa1 = PreserveAspectRatio.of("xMidYMid meet");
        PreserveAspectRatio pa2 = PreserveAspectRatio.of("xMidYMid meet");
        PreserveAspectRatio pa3 = PreserveAspectRatio.of("xMidYMid slice");
        PreserveAspectRatio pa4 = PreserveAspectRatio.of("xMinYMin meet");

        assertEquals("An object should be equal to itself", pa1, pa1);
        assertEquals("Objects with the same values should be equal", pa1, pa2);
        assertNotEquals("Objects with different scales should not be equal", pa1, pa3);
        assertNotEquals("Objects with different alignments should not be equal", pa1, pa4);
        assertNotEquals("An object should not be equal to null", null, pa1);
        assertNotEquals("An object should not be equal to an object of a different class", new Object(), pa1);
    }

    @Test
    public void toString_returnsCorrectFormat() {
        PreserveAspectRatio paWithScale = PreserveAspectRatio.of("xMidYMid slice");
        assertEquals("xMidYMid slice", paWithScale.toString());

        PreserveAspectRatio paWithoutScale = PreserveAspectRatio.of("xMaxYMax");
        assertEquals("xMaxYMax null", paWithoutScale.toString());
    }

    @Test
    public void of_withExtraneousTokens_doesNotFail() {
        // This test verifies that the current parser ignores extra tokens at the end.
        // A stricter implementation might throw an exception here.
        PreserveAspectRatio result = PreserveAspectRatio.of("xMidYMid meet extra");
        assertNotNull(result);
        assertEquals(PreserveAspectRatio.Alignment.xMidYMid, result.getAlignment());
        assertEquals(PreserveAspectRatio.Scale.meet, result.getScale());
    }
}