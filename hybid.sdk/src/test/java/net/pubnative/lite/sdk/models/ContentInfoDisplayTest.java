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
public class ContentInfoDisplayTest {

    @Test
    public void fromString_withExactValues_returnsCorrectEnum() {
        assertEquals(ContentInfoDisplay.IN_APP, ContentInfoDisplay.fromString("inapp"));
        assertEquals(ContentInfoDisplay.SYSTEM_BROWSER, ContentInfoDisplay.fromString("system"));
    }

    @Test
    public void fromString_withMixedCaseValues_returnsCorrectEnum() {
        // Test case-insensitivity
        assertEquals(ContentInfoDisplay.IN_APP, ContentInfoDisplay.fromString("InApp"));
        assertEquals(ContentInfoDisplay.SYSTEM_BROWSER, ContentInfoDisplay.fromString("SYSTEM"));
    }

    @Test
    public void fromString_withNullOrEmptyValue_returnsSystemBrowserAsDefault() {
        assertEquals(ContentInfoDisplay.SYSTEM_BROWSER, ContentInfoDisplay.fromString(null));
        assertEquals(ContentInfoDisplay.SYSTEM_BROWSER, ContentInfoDisplay.fromString(""));
    }

    @Test
    public void fromString_withUnknownValue_returnsSystemBrowserAsDefault() {
        assertEquals(ContentInfoDisplay.SYSTEM_BROWSER, ContentInfoDisplay.fromString("unknown_value"));
    }

    @Test
    public void displayField_hasCorrectValue() {
        assertEquals("inapp", ContentInfoDisplay.IN_APP.display);
        assertEquals("system", ContentInfoDisplay.SYSTEM_BROWSER.display);
    }
}
