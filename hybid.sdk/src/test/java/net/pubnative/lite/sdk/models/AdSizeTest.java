// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class AdSizeTest {

    @Test
    public void getters_forBannerSize_returnCorrectValues() {
        AdSize banner = AdSize.SIZE_320x50;

        assertEquals(320, banner.getWidth());
        assertEquals(50, banner.getHeight());
        assertEquals("s", banner.getAdLayoutSize());
    }

    @Test
    public void getters_forMRectSize_returnCorrectValues() {
        AdSize mrect = AdSize.SIZE_300x250;

        assertEquals(300, mrect.getWidth());
        assertEquals(250, mrect.getHeight());
        assertEquals("m", mrect.getAdLayoutSize());
    }

    @Test
    public void getters_forInterstitialSize_returnCorrectValues() {
        AdSize interstitial = AdSize.SIZE_INTERSTITIAL;

        assertEquals(0, interstitial.getWidth());
        assertEquals(0, interstitial.getHeight());
        assertEquals("l", interstitial.getAdLayoutSize());
    }

    @Test
    public void toString_forStandardSize_returnsCorrectlyFormattedString() {
        AdSize banner = AdSize.SIZE_728x90;
        String expected = "(728 x 90)";

        assertEquals(expected, banner.toString());
    }

    @Test
    public void toString_forInterstitialSize_returnsCorrectlyFormattedString() {
        AdSize interstitial = AdSize.SIZE_INTERSTITIAL;
        String expected = "(0 x 0)";

        assertEquals(expected, interstitial.toString());
    }
}
