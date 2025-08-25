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
public class SiteTest {

    private Site site;

    @Before
    public void setUp() {
        site = new Site();
    }

    @Test
    public void testSettersAndGetters() {
        String id = "site-123";
        String name = "Test Site";
        String domain = "example.com";
        Publisher publisher = new Publisher();
        publisher.setId("pub-456");

        site.setId(id);
        site.setName(name);
        site.setDomain(domain);
        site.setPublisher(publisher);

        assertEquals(id, site.getId());
        assertEquals(name, site.getName());
        assertEquals(domain, site.getDomain());
        assertEquals(publisher, site.getPublisher());
        assertEquals("pub-456", site.getPublisher().getId());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This test ensures that a complex, nested object can be
        // serialized to JSON and then deserialized back into an identical object.

        // 1. Create the original object with all its nested dependencies
        Publisher publisher = new Publisher();
        publisher.setId("pub-abc");
        publisher.setName("Test Publisher");

        Content content = new Content();
        content.setId("content-xyz");
        content.setTitle("Test Content");

        List<String> categories = Arrays.asList("IAB1", "IAB2");

        Site originalSite = new Site();
        originalSite.setId("site-456");
        originalSite.setName("Original Site");
        originalSite.setDomain("original.com");
        originalSite.setPage("https://original.com/page1");
        originalSite.setCategories(categories);
        originalSite.setPublisher(publisher);
        originalSite.setContent(content);

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalSite.toJson();
        assertNotNull(jsonObject);
        assertEquals("site-456", jsonObject.getString("id"));
        assertEquals("Test Publisher", jsonObject.getJSONObject("publisher").getString("name"));
        assertEquals("Test Content", jsonObject.getJSONObject("content").getString("title"));

        // 3. Convert the JSON back into a new object
        Site restoredSite = new Site(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalSite.getId(), restoredSite.getId());
        assertEquals(originalSite.getName(), restoredSite.getName());
        assertEquals(originalSite.getDomain(), restoredSite.getDomain());
        assertEquals(originalSite.getPage(), restoredSite.getPage());
        assertEquals(originalSite.getCategories(), restoredSite.getCategories());

        // Assert nested Publisher
        assertNotNull(restoredSite.getPublisher());
        assertEquals(originalSite.getPublisher().getId(), restoredSite.getPublisher().getId());
        assertEquals(originalSite.getPublisher().getName(), restoredSite.getPublisher().getName());

        // Assert nested Content
        assertNotNull(restoredSite.getContent());
        assertEquals(originalSite.getContent().getId(), restoredSite.getContent().getId());
        assertEquals(originalSite.getContent().getTitle(), restoredSite.getContent().getTitle());
    }
}
