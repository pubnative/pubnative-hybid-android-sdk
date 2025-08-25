// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ContentInfoIconXPositionTest {

    @Test
    public void fromString_withExactValues_returnsCorrectEnum() {
        assertEquals(ContentInfoIconXPosition.LEFT, ContentInfoIconXPosition.fromString("left"));
        assertEquals(ContentInfoIconXPosition.RIGHT, ContentInfoIconXPosition.fromString("right"));
    }

    @Test
    public void fromString_withMixedCaseValues_returnsCorrectEnum() {
        assertEquals(ContentInfoIconXPosition.LEFT, ContentInfoIconXPosition.fromString("Left"));
        assertEquals(ContentInfoIconXPosition.RIGHT, ContentInfoIconXPosition.fromString("RIGHT"));
    }

    @Test
    public void fromString_withNullOrEmptyValue_returnsNull() {
        assertNull(ContentInfoIconXPosition.fromString(null));
        assertNull(ContentInfoIconXPosition.fromString(""));
    }

    @Test
    public void fromString_withUnknownValue_returnsLeftAsDefault() {
        assertEquals(ContentInfoIconXPosition.LEFT, ContentInfoIconXPosition.fromString("unknown_value"));
    }

    @Test
    public void getDefaultXPosition_returnsLeft() {
        assertEquals(ContentInfoIconXPosition.LEFT, ContentInfoIconXPosition.getDefaultXPosition());
    }

    @Test
    public void horizontalPositionField_hasCorrectValue() {
        assertEquals("left", ContentInfoIconXPosition.LEFT.horizontalPosition);
        assertEquals("right", ContentInfoIconXPosition.RIGHT.horizontalPosition);
    }
}
