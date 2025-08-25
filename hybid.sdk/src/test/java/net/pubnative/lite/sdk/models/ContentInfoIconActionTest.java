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
public class ContentInfoIconActionTest {

    @Test
    public void fromString_withExactValues_returnsCorrectEnum() {
        assertEquals(ContentInfoIconAction.EXPAND, ContentInfoIconAction.fromString("expand"));
        assertEquals(ContentInfoIconAction.OPEN, ContentInfoIconAction.fromString("open"));
    }

    @Test
    public void fromString_withMixedCaseValues_returnsCorrectEnum() {
        assertEquals(ContentInfoIconAction.EXPAND, ContentInfoIconAction.fromString("Expand"));
        assertEquals(ContentInfoIconAction.OPEN, ContentInfoIconAction.fromString("OPEN"));
    }

    @Test
    public void fromString_withNullOrEmptyValue_returnsExpandAsDefault() {
        assertEquals(ContentInfoIconAction.EXPAND, ContentInfoIconAction.fromString(null));
        assertEquals(ContentInfoIconAction.EXPAND, ContentInfoIconAction.fromString(""));
    }

    @Test
    public void fromString_withUnknownValue_returnsExpandAsDefault() {
        assertEquals(ContentInfoIconAction.EXPAND, ContentInfoIconAction.fromString("unknown_value"));
    }

    @Test
    public void actionField_hasCorrectValue() {
        assertEquals("expand", ContentInfoIconAction.EXPAND.action);
        assertEquals("open", ContentInfoIconAction.OPEN.action);
    }
}
