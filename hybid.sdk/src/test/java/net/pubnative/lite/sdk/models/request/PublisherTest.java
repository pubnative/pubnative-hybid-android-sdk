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
public class PublisherTest {

    private Publisher publisher;

    @Before
    public void setUp() {
        publisher = new Publisher();
    }

    @Test
    public void testSettersAndGetters() {
        String id = "pub-123";
        String name = "Test Publisher";
        String domain = "publisher.com";
        List<String> categories = Arrays.asList("cat1", "cat2");

        publisher.setId(id);
        publisher.setName(name);
        publisher.setDomain(domain);
        publisher.setCategories(categories);

        assertEquals(id, publisher.getId());
        assertEquals(name, publisher.getName());
        assertEquals(domain, publisher.getDomain());
        assertEquals(categories, publisher.getCategories());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This test ensures that an object serialized to JSON
        // can be deserialized back into an identical object.

        // 1. Create and populate the original object
        Publisher originalPublisher = new Publisher();
        originalPublisher.setId("pub-abc");
        originalPublisher.setName("Original Publisher");
        originalPublisher.setDomain("original.com");
        originalPublisher.setCategories(Arrays.asList("IAB1", "IAB2-2"));

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalPublisher.toJson();
        assertNotNull(jsonObject);
        assertEquals("pub-abc", jsonObject.getString("id"));
        assertEquals("original.com", jsonObject.getString("domain"));

        // 3. Convert the JSON back into a new object
        Publisher restoredPublisher = new Publisher(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalPublisher.getId(), restoredPublisher.getId());
        assertEquals(originalPublisher.getName(), restoredPublisher.getName());
        assertEquals(originalPublisher.getDomain(), restoredPublisher.getDomain());
        assertEquals(originalPublisher.getCategories(), restoredPublisher.getCategories());
    }
}
