// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class LearnMoreLocationTest {

    @Test
    public void fromString_withExactValues_returnsCorrectEnum() {
        assertEquals(LearnMoreLocation.DEFAULT, LearnMoreLocation.fromString("default"));
        assertEquals(LearnMoreLocation.BOTTOM_DOWN, LearnMoreLocation.fromString("bottom_down"));
        assertEquals(LearnMoreLocation.BOTTOM_UP, LearnMoreLocation.fromString("bottom_up"));
    }

    @Test
    public void fromString_withMixedCaseValues_returnsCorrectEnum() {
        assertEquals(LearnMoreLocation.BOTTOM_DOWN, LearnMoreLocation.fromString("Bottom_Down"));
        assertEquals(LearnMoreLocation.BOTTOM_UP, LearnMoreLocation.fromString("BOTTOM_UP"));
    }

    @Test
    public void fromString_withNullOrUnknownValue_returnsDefault() {
        assertEquals(LearnMoreLocation.DEFAULT, LearnMoreLocation.fromString(null));
        assertEquals(LearnMoreLocation.DEFAULT, LearnMoreLocation.fromString("unknown"));
    }
}
