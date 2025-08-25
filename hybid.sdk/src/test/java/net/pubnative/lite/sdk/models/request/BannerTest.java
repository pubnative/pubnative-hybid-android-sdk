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
public class BannerTest {

    private Banner banner;

    @Before
    public void setUp() {
        banner = new Banner();
    }

    @Test
    public void testSettersAndGetters() {
        String id = "banner-123";
        Integer width = 320;
        Integer height = 50;
        List<Format> formats = new ArrayList<>();
        Format format = new Format();
        format.setWidth(320);
        format.setHeight(50);
        formats.add(format);

        banner.setId(id);
        banner.setW(width);
        banner.setH(height);
        banner.setFormat(formats);

        assertEquals(id, banner.getId());
        assertEquals(width, banner.getW());
        assertEquals(height, banner.getH());
        assertEquals(formats, banner.getFormat());
        assertEquals(Integer.valueOf(320), banner.getFormat().get(0).getWidth());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This "round-trip" test ensures that an object serialized to JSON
        // can be deserialized back into an identical object.

        // 1. Create the original object with nested data
        Format format1 = new Format();
        format1.setWidth(320);
        format1.setHeight(50);

        Format format2 = new Format();
        format2.setWidth(300);
        format2.setHeight(50);

        List<Format> formats = Arrays.asList(format1, format2);
        List<String> mimes = Arrays.asList("image/jpeg", "image/png");

        Banner originalBanner = new Banner();
        originalBanner.setId("banner-abc");
        originalBanner.setW(320);
        originalBanner.setH(50);
        originalBanner.setPos(1);
        originalBanner.setFormat(formats);
        originalBanner.setMimes(mimes);

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalBanner.toJson();
        assertNotNull(jsonObject);
        assertEquals("banner-abc", jsonObject.getString("id"));
        assertEquals(320, jsonObject.getJSONArray("format").getJSONObject(0).getInt("w"));

        // 3. Convert the JSON back into a new object
        Banner restoredBanner = new Banner(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalBanner.getId(), restoredBanner.getId());
        assertEquals(originalBanner.getW(), restoredBanner.getW());
        assertEquals(originalBanner.getH(), restoredBanner.getH());
        assertEquals(originalBanner.getPos(), restoredBanner.getPos());
        assertEquals(originalBanner.getMimes(), restoredBanner.getMimes());

        assertNotNull(restoredBanner.getFormat());
        assertEquals(2, restoredBanner.getFormat().size());

        Format originalFormat1 = originalBanner.getFormat().get(0);
        Format restoredFormat1 = restoredBanner.getFormat().get(0);

        assertEquals(originalFormat1.getWidth(), restoredFormat1.getWidth());
        assertEquals(originalFormat1.getHeight(), restoredFormat1.getHeight());
    }
}
