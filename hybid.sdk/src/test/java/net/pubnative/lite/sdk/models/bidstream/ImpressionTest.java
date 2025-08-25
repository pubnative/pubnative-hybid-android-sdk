// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.bidstream;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ImpressionTest {

    @Test
    public void constructor_withParameters_assignsFieldsCorrectly() {
        Integer isInterstitial = 1;
        int clickBrowser = 0;

        Impression impression = new Impression(isInterstitial, clickBrowser);

        assertEquals(isInterstitial, impression.isInterstitial);
        assertEquals(clickBrowser, impression.clickBrowser);
    }

    @Test
    public void defaultConstructor_initializesFieldsToDefaultValues() {
        Impression impression = new Impression();

        // Verify that the fields are set to their default initialized values
        assertEquals(Integer.valueOf(0), impression.isInterstitial);
        assertEquals(1, impression.clickBrowser);
    }
}
