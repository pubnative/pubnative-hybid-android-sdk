// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ImpTest {

    private Imp imp;

    @Before
    public void setUp() {
        imp = new Imp();
    }

    @Test
    public void testDefaultConstructor_initializesFieldsToDefaultValues() {
        assertNull(imp.getId());
        assertEquals(Integer.valueOf(0), imp.getInstl());
        assertEquals(Float.valueOf(0.0f), imp.getBidfloor());
        assertEquals("USD", imp.getBidfloorcur());
    }

    @Test
    public void testSettersAndGetters() {
        String id = "imp-123";
        String tagId = "tag-abc";
        Banner banner = new Banner();
        banner.setW(320);
        banner.setH(50);
        Video video = new Video();
        video.setMinDuration(15);

        imp.setId(id);
        imp.setTagid(tagId);
        imp.setBanner(banner);
        imp.setVideo(video);

        assertEquals(id, imp.getId());
        assertEquals(tagId, imp.getTagid());
        assertEquals(banner, imp.getBanner());
        assertEquals(Integer.valueOf(320), imp.getBanner().getW());
        assertEquals(video, imp.getVideo());
        assertEquals(Integer.valueOf(15), imp.getVideo().getMinDuration());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create the original object with nested data
        Banner banner = new Banner();
        banner.setW(320);
        banner.setH(50);

        Video video = new Video();
        video.setMinDuration(5);
        video.setMaxDuration(30);

        Imp originalImp = new Imp();
        originalImp.setId("imp-xyz");
        originalImp.setInstl(1);
        originalImp.setBidfloor(2.5f);
        originalImp.setBidfloorcur("EUR");
        originalImp.setBanner(banner);
        originalImp.setVideo(video);

        // 2. Convert to JSON
        JSONObject jsonObject = originalImp.toJson();
        assertNotNull(jsonObject);
        assertEquals("imp-xyz", jsonObject.getString("id"));
        assertEquals(2.5, jsonObject.getDouble("bidfloor"), 0.001);
        assertNotNull(jsonObject.getJSONObject("banner"));
        assertEquals(30, jsonObject.getJSONObject("video").getInt("maxduration"));

        // 3. Convert back to an object
        Imp restoredImp = new Imp(jsonObject);

        // 4. Assert that the objects are identical
        assertEquals(originalImp.getId(), restoredImp.getId());
        assertEquals(originalImp.getInstl(), restoredImp.getInstl());
        assertEquals(originalImp.getBidfloor(), restoredImp.getBidfloor());
        assertEquals(originalImp.getBidfloorcur(), restoredImp.getBidfloorcur());

        // Assert nested banner
        assertNotNull(restoredImp.getBanner());
        assertEquals(originalImp.getBanner().getW(), restoredImp.getBanner().getW());
        assertEquals(originalImp.getBanner().getH(), restoredImp.getBanner().getH());

        // Assert nested video
        assertNotNull(restoredImp.getVideo());
        assertEquals(originalImp.getVideo().getMinDuration(), restoredImp.getVideo().getMinDuration());
        assertEquals(originalImp.getVideo().getMaxDuration(), restoredImp.getVideo().getMaxDuration());
    }
}
