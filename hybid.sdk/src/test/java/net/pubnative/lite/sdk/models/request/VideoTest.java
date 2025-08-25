// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class VideoTest {

    private Video video;

    @Before
    public void setUp() {
        video = new Video();
    }

    @Test
    public void testDefaultConstructor_initializesFieldsToDefaultValues() {
        assertEquals(Integer.valueOf(0), video.getSkipMin());
        assertEquals(Integer.valueOf(0), video.getSkipAfter());
        assertEquals(Integer.valueOf(1), video.getBoxingAllowed());
        assertNull(video.getMimes());
        assertNull(video.getMinDuration());
    }

    @Test
    public void testSettersAndGetters() {
        Integer minDuration = 15;
        Integer maxDuration = 60;
        Integer skip = 1;
        Banner companionAd = new Banner();
        companionAd.setId("companion-123");

        video.setMinDuration(minDuration);
        video.setMaxDuration(maxDuration);
        video.setSkip(skip);
        video.setCompanionAds(Arrays.asList(companionAd));

        assertEquals(minDuration, video.getMinDuration());
        assertEquals(maxDuration, video.getMaxDuration());
        assertEquals(skip, video.getSkip());
        assertNotNull(video.getCompanionAds());
        assertEquals(1, video.getCompanionAds().size());
        assertEquals("companion-123", video.getCompanionAds().get(0).getId());
    }

    @Test
    public void testFromJson_withMissingFields_usesDefaults() throws Exception {
        // Create a JSON object with only a few fields, omitting the ones with defaults.
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("minduration", 10);

        Video videoFromJson = new Video(jsonObject);

        // Verify the parsed field.
        assertEquals(Integer.valueOf(10), videoFromJson.getMinDuration());

        // Verify that the other fields retained their default values.
        assertEquals(Integer.valueOf(0), videoFromJson.getSkipMin());
        assertEquals(Integer.valueOf(0), videoFromJson.getSkipAfter());
        assertEquals(Integer.valueOf(1), videoFromJson.getBoxingAllowed());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create the original object with nested data
        Banner companionAd = new Banner();
        companionAd.setW(300);
        companionAd.setH(60);

        Video originalVideo = new Video();
        originalVideo.setMimes(Arrays.asList("video/mp4"));
        originalVideo.setMinDuration(5);
        originalVideo.setMaxDuration(30);
        originalVideo.setLinearity(1);
        originalVideo.setSkip(1);
        originalVideo.setSkipMin(5); // Override default
        originalVideo.setCompanionAds(Arrays.asList(companionAd));

        // 2. Convert to JSON
        JSONObject jsonObject = originalVideo.toJson();
        assertNotNull(jsonObject);
        assertEquals(5, jsonObject.getInt("minduration"));
        assertEquals(1, jsonObject.getInt("linearity"));
        assertEquals(5, jsonObject.getInt("skipmin"));
        assertEquals(300, jsonObject.getJSONArray("companionad").getJSONObject(0).getInt("w"));

        // 3. Convert back to an object
        Video restoredVideo = new Video(jsonObject);

        // 4. Assert that the objects are identical
        assertEquals(originalVideo.getMimes(), restoredVideo.getMimes());
        assertEquals(originalVideo.getMinDuration(), restoredVideo.getMinDuration());
        assertEquals(originalVideo.getMaxDuration(), restoredVideo.getMaxDuration());
        assertEquals(originalVideo.getLinearity(), restoredVideo.getLinearity());
        assertEquals(originalVideo.getSkip(), restoredVideo.getSkip());
        assertEquals(originalVideo.getSkipMin(), restoredVideo.getSkipMin());

        // Assert nested companion ad
        assertNotNull(restoredVideo.getCompanionAds());
        assertEquals(1, restoredVideo.getCompanionAds().size());
        assertEquals(originalVideo.getCompanionAds().get(0).getW(), restoredVideo.getCompanionAds().get(0).getW());
        assertEquals(originalVideo.getCompanionAds().get(0).getH(), restoredVideo.getCompanionAds().get(0).getH());
    }
}
