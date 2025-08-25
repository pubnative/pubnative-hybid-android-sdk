// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ContentInfoIconYPositionTest {

    @Test
    public void fromString_withExactValues_returnsCorrectEnum() {
        assertEquals(ContentInfoIconYPosition.TOP, ContentInfoIconYPosition.fromString("top"));
        assertEquals(ContentInfoIconYPosition.BOTTOM, ContentInfoIconYPosition.fromString("bottom"));
    }

    @Test
    public void fromString_withMixedCaseValues_returnsCorrectEnum() {
        assertEquals(ContentInfoIconYPosition.TOP, ContentInfoIconYPosition.fromString("Top"));
        assertEquals(ContentInfoIconYPosition.BOTTOM, ContentInfoIconYPosition.fromString("BOTTOM"));
    }

    @Test
    public void fromString_withNullOrEmptyValue_returnsTopAsDefault() {
        assertEquals(ContentInfoIconYPosition.TOP, ContentInfoIconYPosition.fromString(null));
        assertEquals(ContentInfoIconYPosition.TOP, ContentInfoIconYPosition.fromString(""));
    }

    @Test
    public void fromString_withUnknownValue_returnsTopAsDefault() {
        assertEquals(ContentInfoIconYPosition.TOP, ContentInfoIconYPosition.fromString("unknown_value"));
    }

    @Test
    public void getDefaultYPosition_returnsBottom() {
        assertEquals(ContentInfoIconYPosition.BOTTOM, ContentInfoIconYPosition.getDefaultYPosition());
    }

    @Test
    public void verticalPositionField_hasCorrectValue() {
        assertEquals("top", ContentInfoIconYPosition.TOP.verticalPosition);
        assertEquals("bottom", ContentInfoIconYPosition.BOTTOM.verticalPosition);
    }
}
