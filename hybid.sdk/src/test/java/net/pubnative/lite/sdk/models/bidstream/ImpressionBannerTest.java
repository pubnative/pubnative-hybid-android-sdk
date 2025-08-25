// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.bidstream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.hamcrest.MatcherAssert;
import static org.hamcrest.CoreMatchers.hasItems;


public class ImpressionBannerTest {

    @Test
    public void defaultConstructor_initializesFieldsCorrectly() {
        ImpressionBanner banner = new ImpressionBanner();

        assertEquals(1, banner.topframe);
        assertEquals(0, banner.pos);
        assertNotNull(banner.expdir);
        assertTrue(banner.expdir.isEmpty());
        assertNotNull(banner.mimes);
        assertEquals(2, banner.mimes.size());
        MatcherAssert.assertThat(banner.mimes, hasItems("text/html", "text/javascript"));
    }

    @Test
    public void constructorWithPosition_initializesFieldsCorrectly() {
        int testPosition = 7; // Ad Position: Below the Fold
        ImpressionBanner banner = new ImpressionBanner(testPosition);

        assertEquals(1, banner.topframe);
        assertEquals(testPosition, banner.pos);
        assertNotNull(banner.expdir);
        assertTrue(banner.expdir.isEmpty());
        assertNotNull(banner.mimes);
        assertEquals(2, banner.mimes.size());
        MatcherAssert.assertThat(banner.mimes, hasItems("text/html", "text/javascript"));
    }

    @Test
    public void constructorWithPositionAndExpdir_initializesFieldsCorrectly() {
        int testPosition = 3; // Ad Position: Header
        List<Integer> testExpdir = new ArrayList<>(Arrays.asList(1, 2)); // Expandable Directions

        ImpressionBanner banner = new ImpressionBanner(testPosition, testExpdir);

        assertEquals(1, banner.topframe);
        assertEquals(testPosition, banner.pos);
        assertEquals(testExpdir, banner.expdir);
        assertNotNull(banner.mimes);
        assertEquals(2, banner.mimes.size());
        MatcherAssert.assertThat(banner.mimes, hasItems("text/html", "text/javascript"));
    }
}
