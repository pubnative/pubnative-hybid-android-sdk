// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.bidstream;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

public class ImpressionVideoTest {

    @Test
    public void constructor_withParameters_initializesAllFieldsCorrectly() {
        Integer testPlacement = 2;
        Integer testPlacementSubtype = 1;
        int testPos = 1;
        List<Integer> testPlaybackMethods = new ArrayList<>(Arrays.asList(1, 3)); // Auto-play, sound on

        ImpressionVideo video = new ImpressionVideo(testPlacement, testPlacementSubtype, testPos, testPlaybackMethods);

        // Assert fields set from constructor parameters
        assertEquals(testPlacement, video.placement);
        assertEquals(testPlacementSubtype, video.plcmt);
        assertEquals(testPos, video.pos);
        assertEquals(testPlaybackMethods, video.playbackmethod);

        // Assert hardcoded field values
        assertEquals(1, video.linearity);
        assertEquals(0, video.boxingallowed);
        assertEquals(1, video.playbackend);
        assertTrue(video.mraidendcard);
        assertEquals(3, video.clktype);

        // Assert hardcoded list for `delivery`
        assertNotNull(video.delivery);
        assertEquals(1, video.delivery.size());
        assertEquals(Integer.valueOf(3), video.delivery.get(0));

        // Assert hardcoded list for `mimes`
        assertNotNull(video.mimes);
        assertEquals(5, video.mimes.size());
        MatcherAssert.assertThat(video.mimes, hasItems("video/mp4", "video/webm", "video/3gpp", "video/3gpp2", "video/x-m4v"));
    }
}
