// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AudioTest {

    private Audio audio;

    @Before
    public void setUp() {
        audio = new Audio();
    }

    @Test
    public void testSettersAndGetters() {
        List<String> mimes = Arrays.asList("audio/mp4");
        Integer minDuration = 15;
        Integer maxDuration = 60;
        List<Banner> companionAds = new ArrayList<>();
        Banner banner = new Banner();
        banner.setId("companion_ad_1");
        companionAds.add(banner);

        audio.setMimes(mimes);
        audio.setMinDuration(minDuration);
        audio.setMaxDuration(maxDuration);
        audio.setCompanionAds(companionAds);

        assertEquals(mimes, audio.getMimes());
        assertEquals(minDuration, audio.getMinDuration());
        assertEquals(maxDuration, audio.getMaxDuration());
        assertEquals(companionAds, audio.getCompanionAds());
        assertEquals("companion_ad_1", audio.getCompanionAds().get(0).getId());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This "round-trip" test ensures that an object serialized to JSON
        // can be deserialized back into an identical object.

        // 1. Create the original object with nested data
        List<String> mimes = Arrays.asList("audio/mp4", "audio/ogg");
        List<Integer> protocols = Arrays.asList(2, 5);

        Banner companionBanner = new Banner();
        companionBanner.setId("companion_123");
        companionBanner.setW(300);
        companionBanner.setH(250);
        List<Banner> companionAds = Arrays.asList(companionBanner);

        Audio originalAudio = new Audio();
        originalAudio.setMimes(mimes);
        originalAudio.setProtocols(protocols);
        originalAudio.setMinDuration(5);
        originalAudio.setMaxDuration(30);
        originalAudio.setCompanionAds(companionAds);

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalAudio.toJson();
        assertNotNull(jsonObject);
        assertEquals(30, jsonObject.getInt("maxduration"));
        assertEquals("companion_123", jsonObject.getJSONArray("companionad").getJSONObject(0).getString("id"));

        // 3. Convert the JSON back into a new object
        Audio restoredAudio = new Audio(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalAudio.getMimes(), restoredAudio.getMimes());
        assertEquals(originalAudio.getProtocols(), restoredAudio.getProtocols());
        assertEquals(originalAudio.getMinDuration(), restoredAudio.getMinDuration());
        assertEquals(originalAudio.getMaxDuration(), restoredAudio.getMaxDuration());

        assertNotNull(restoredAudio.getCompanionAds());
        assertEquals(1, restoredAudio.getCompanionAds().size());

        Banner originalBanner = originalAudio.getCompanionAds().get(0);
        Banner restoredBanner = restoredAudio.getCompanionAds().get(0);

        assertEquals(originalBanner.getId(), restoredBanner.getId());
        assertEquals(originalBanner.getW(), restoredBanner.getW());
        assertEquals(originalBanner.getH(), restoredBanner.getH());
    }
}
