// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class LearnMoreDataTest {

    @Test
    public void constructor_withValidStrings_initializesCorrectEnumValues() {
        LearnMoreData data = new LearnMoreData("medium", "bottom_up");

        assertEquals(LearnMoreSize.MEDIUM, data.getSize());
        assertEquals(LearnMoreLocation.BOTTOM_UP, data.getLocation());
    }

    @Test
    public void constructor_withInvalidStrings_initializesDefaultEnumValues() {
        LearnMoreData data = new LearnMoreData("unknown_size", "unknown_location");

        assertEquals(LearnMoreSize.DEFAULT, data.getSize());
        assertEquals(LearnMoreLocation.DEFAULT, data.getLocation());
    }

    @Test
    public void constructor_withNullStrings_initializesDefaultEnumValues() {
        LearnMoreData data = new LearnMoreData(null, null);

        assertEquals(LearnMoreSize.DEFAULT, data.getSize());
        assertEquals(LearnMoreLocation.DEFAULT, data.getLocation());
    }
}
