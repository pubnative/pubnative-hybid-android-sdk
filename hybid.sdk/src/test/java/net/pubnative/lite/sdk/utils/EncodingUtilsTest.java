// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;


@RunWith(RobolectricTestRunner.class)
public class EncodingUtilsTest {

    @Test
    public void urlEncode_ValidValue_shouldPass() {
        String input = "hello world!";
        String expected = "hello+world%21";

        String result = EncodingUtils.urlEncode(input);

        assertEquals(expected, result);
    }

    @Test
    public void urlEncode_withNullValue_shouldReturnEmpty() {
        String result = EncodingUtils.urlEncode(null);
        assertEquals("", result);
    }
}