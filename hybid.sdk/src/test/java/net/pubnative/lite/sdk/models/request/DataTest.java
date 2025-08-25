// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class DataTest {

    private Data data;

    @Before
    public void setUp() {
        data = new Data();
    }

    @Test
    public void testSettersAndGetters() {
        String id = "data-123";
        String name = "Test Data";
        DataExtension ext = new DataExtension(1L, "class-a");
        List<Segment> segments = Arrays.asList(new Segment());

        data.setId(id);
        data.setName(name);
        data.setExt(ext);
        data.setSegment(segments);

        assertEquals(id, data.getId());
        assertEquals(name, data.getName());
        assertEquals(ext, data.getExt());
        assertEquals(segments, data.getSegment());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This "round-trip" test ensures that a complex, nested object can be
        // serialized to JSON and then deserialized back into an identical object.

        // 1. Create the original object with all its nested dependencies
        Segment segment1 = new Segment();
        segment1.setId("seg-001");
        segment1.setName("Segment One");

        Segment segment2 = new Segment();
        segment2.setId("seg-002");
        segment2.setName("Segment Two");

        DataExtension dataExtension = new DataExtension(404L, "class-b");

        Data originalData = new Data();
        originalData.setId("data-abc");
        originalData.setName("Original Data");
        originalData.setSegment(Arrays.asList(segment1, segment2));
        originalData.setExt(dataExtension);

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalData.toJson();
        assertNotNull(jsonObject);
        assertEquals("data-abc", jsonObject.getString("id"));
        assertEquals("Segment One", jsonObject.getJSONArray("segment").getJSONObject(0).getString("name"));
        assertEquals(404L, jsonObject.getJSONObject("ext").getLong("segtax"));

        // 3. Convert the JSON back into a new object
        Data restoredData = new Data(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalData.getId(), restoredData.getId());
        assertEquals(originalData.getName(), restoredData.getName());

        // Assert nested DataExtension
        assertNotNull(restoredData.getExt());
        assertEquals(originalData.getExt().segtax, restoredData.getExt().segtax);
        assertEquals(originalData.getExt().segclass, restoredData.getExt().segclass);

        // Assert nested List<Segment>
        assertNotNull(restoredData.getSegment());
        assertEquals(2, restoredData.getSegment().size());
        assertEquals(originalData.getSegment().get(0).getId(), restoredData.getSegment().get(0).getId());
        assertEquals(originalData.getSegment().get(1).getName(), restoredData.getSegment().get(1).getName());
    }
}
