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
public class CustomEndCardDisplayTest {

    @Test
    public void fromString_withExactValues_returnsCorrectEnum() {
        assertEquals(CustomEndCardDisplay.EXTENSION, CustomEndCardDisplay.fromString("extension"));
        assertEquals(CustomEndCardDisplay.FALLBACK, CustomEndCardDisplay.fromString("fallback"));
    }

    @Test
    public void fromString_withMixedCaseValues_returnsCorrectEnum() {
        assertEquals(CustomEndCardDisplay.EXTENSION, CustomEndCardDisplay.fromString("Extension"));
        assertEquals(CustomEndCardDisplay.FALLBACK, CustomEndCardDisplay.fromString("FALLBACK"));
    }

    @Test
    public void fromString_withNullOrEmptyValue_returnsFallbackAsDefault() {
        assertEquals(CustomEndCardDisplay.FALLBACK, CustomEndCardDisplay.fromString(null));
        assertEquals(CustomEndCardDisplay.FALLBACK, CustomEndCardDisplay.fromString(""));
    }

    @Test
    public void fromString_withUnknownValue_returnsFallbackAsDefault() {
        assertEquals(CustomEndCardDisplay.FALLBACK, CustomEndCardDisplay.fromString("unknown_value"));
    }

    @Test
    public void displayField_hasCorrectValue() {
        assertEquals("extension", CustomEndCardDisplay.EXTENSION.display);
        assertEquals("fallback", CustomEndCardDisplay.FALLBACK.display);
    }
}
