// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class LearnMoreSizeTest {

    @Test
    public void fromString_withExactValues_returnsCorrectEnum() {
        assertEquals(LearnMoreSize.DEFAULT, LearnMoreSize.fromString("default"));
        assertEquals(LearnMoreSize.MEDIUM, LearnMoreSize.fromString("medium"));
        assertEquals(LearnMoreSize.LARGE, LearnMoreSize.fromString("large"));
    }

    @Test
    public void fromString_withMixedCaseValues_returnsCorrectEnum() {
        assertEquals(LearnMoreSize.MEDIUM, LearnMoreSize.fromString("Medium"));
        assertEquals(LearnMoreSize.LARGE, LearnMoreSize.fromString("LARGE"));
    }

    @Test
    public void fromString_withNullOrUnknownValue_returnsDefault() {
        assertEquals(LearnMoreSize.DEFAULT, LearnMoreSize.fromString(null));
        assertEquals(LearnMoreSize.DEFAULT, LearnMoreSize.fromString("unknown"));
    }
}
