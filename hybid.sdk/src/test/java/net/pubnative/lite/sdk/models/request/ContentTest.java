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
public class ContentTest {

    private Content content;

    @Before
    public void setUp() {
        content = new Content();
    }

    @Test
    public void testSettersAndGetters() {
        String title = "Test Title";
        Integer episode = 5;
        Producer producer = new Producer();
        producer.setName("Test Producer");
        List<Data> dataList = new ArrayList<>();
        Data data = new Data();
        data.setId("data-1");
        dataList.add(data);

        content.setTitle(title);
        content.setEpisode(episode);
        content.setProducer(producer);
        content.setData(dataList);

        assertEquals(title, content.getTitle());
        assertEquals(episode, content.getEpisode());
        assertEquals(producer, content.getProducer());
        assertEquals("Test Producer", content.getProducer().getName());
        assertEquals(dataList, content.getData());
        assertEquals("data-1", content.getData().get(0).getId());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This "round-trip" test ensures that a complex, nested object can be
        // serialized to JSON and then deserialized back into an identical object.

        // 1. Create the original object with all its nested dependencies
        Producer producer = new Producer();
        producer.setId("prod-123");
        producer.setName("HyBid Productions");

        Segment segment = new Segment();
        segment.setId("seg-456");
        segment.setName("Test Segment");

        DataExtension dataExtension = new DataExtension(1L, "class-a");

        Data data = new Data();
        data.setId("data-789");
        data.setSegment(Arrays.asList(segment));
        data.setExt(dataExtension);

        Content originalContent = new Content();
        originalContent.setId("content-abc");
        originalContent.setTitle("Test Content Title");
        originalContent.setEpisode(101);
        originalContent.setProducer(producer);
        originalContent.setData(Arrays.asList(data));

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalContent.toJson();
        assertNotNull(jsonObject);
        assertEquals("Test Content Title", jsonObject.getString("title"));
        assertEquals("HyBid Productions", jsonObject.getJSONObject("producer").getString("name"));
        assertEquals("Test Segment", jsonObject.getJSONArray("data").getJSONObject(0).getJSONArray("segment").getJSONObject(0).getString("name"));

        // 3. Convert the JSON back into a new object
        Content restoredContent = new Content(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalContent.getId(), restoredContent.getId());
        assertEquals(originalContent.getTitle(), restoredContent.getTitle());
        assertEquals(originalContent.getEpisode(), restoredContent.getEpisode());

        // Assert nested Producer object
        assertNotNull(restoredContent.getProducer());
        assertEquals(originalContent.getProducer().getId(), restoredContent.getProducer().getId());
        assertEquals(originalContent.getProducer().getName(), restoredContent.getProducer().getName());

        // Assert nested List<Data> and its contents
        assertNotNull(restoredContent.getData());
        assertEquals(1, restoredContent.getData().size());
        Data restoredData = restoredContent.getData().get(0);
        assertEquals(data.getId(), restoredData.getId());

        // Assert doubly-nested Segment
        assertNotNull(restoredData.getSegment());
        assertEquals(1, restoredData.getSegment().size());
        assertEquals(segment.getName(), restoredData.getSegment().get(0).getName());

        // Assert doubly-nested DataExtension
        assertNotNull(restoredData.getExt());
        assertEquals(dataExtension.segtax, restoredData.getExt().segtax);
        assertEquals(dataExtension.segclass, restoredData.getExt().segclass);
    }
}
