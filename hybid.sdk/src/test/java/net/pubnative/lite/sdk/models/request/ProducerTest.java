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
public class ProducerTest {

    private Producer producer;

    @Before
    public void setUp() {
        producer = new Producer();
    }

    @Test
    public void testSettersAndGetters() {
        String id = "prod-123";
        String name = "Test Producer";
        String domain = "producer.com";
        List<String> categories = Arrays.asList("cat1", "cat2");

        producer.setId(id);
        producer.setName(name);
        producer.setDomain(domain);
        producer.setCategories(categories);

        assertEquals(id, producer.getId());
        assertEquals(name, producer.getName());
        assertEquals(domain, producer.getDomain());
        assertEquals(categories, producer.getCategories());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This test ensures that an object serialized to JSON
        // can be deserialized back into an identical object.

        // 1. Create and populate the original object
        Producer originalProducer = new Producer();
        originalProducer.setId("prod-abc");
        originalProducer.setName("Original Producer");
        originalProducer.setDomain("original.com");
        originalProducer.setCategories(Arrays.asList("IAB1", "IAB2-2"));

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalProducer.toJson();
        assertNotNull(jsonObject);
        assertEquals("prod-abc", jsonObject.getString("id"));
        assertEquals("original.com", jsonObject.getString("domain"));

        // 3. Convert the JSON back into a new object
        Producer restoredProducer = new Producer(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalProducer.getId(), restoredProducer.getId());
        assertEquals(originalProducer.getName(), restoredProducer.getName());
        assertEquals(originalProducer.getDomain(), restoredProducer.getDomain());
        assertEquals(originalProducer.getCategories(), restoredProducer.getCategories());
    }
}
