// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AppTest {

    private App app;

    @Before
    public void setUp() {
        app = new App();
    }

    @Test
    public void testSettersAndGetters() {
        String id = "app-id-123";
        String name = "Test App";
        String bundle = "com.example.testapp";
        List<String> categories = Arrays.asList("IAB1", "IAB2");
        Publisher publisher = new Publisher();
        publisher.setId("pub-456");
        Content content = new Content();
        content.setTitle("Test Content");

        app.setId(id);
        app.setName(name);
        app.setBundle(bundle);
        app.setCategories(categories);
        app.setPublisher(publisher);
        app.setContent(content);

        assertEquals(id, app.getId());
        assertEquals(name, app.getName());
        assertEquals(bundle, app.getBundle());
        assertEquals(categories, app.getCategories());
        assertEquals(publisher, app.getPublisher());
        assertEquals(publisher.getId(), app.getPublisher().getId());
        assertEquals(content, app.getContent());
        assertEquals(content.getTitle(), app.getContent().getTitle());
    }

    @Test
    public void constructor_withValidJson_populatesAllFields() throws Exception {
        JSONObject publisherJson = new JSONObject();
        publisherJson.put("id", "pub-456");
        publisherJson.put("name", "Test Publisher");

        JSONObject contentJson = new JSONObject();
        contentJson.put("title", "Test Content");

        JSONObject appJson = new JSONObject();
        appJson.put("id", "app-id-123");
        appJson.put("name", "Test App from JSON");
        appJson.put("bundle", "com.example.json");
        appJson.put("cat", new JSONArray(Arrays.asList("IAB1", "IAB2")));
        appJson.put("paid", 1);
        appJson.put("publisher", publisherJson);
        appJson.put("content", contentJson);

        App appFromJson = new App(appJson);

        assertEquals("app-id-123", appFromJson.getId());
        assertEquals("Test App from JSON", appFromJson.getName());
        assertEquals("com.example.json", appFromJson.getBundle());
        assertEquals(Integer.valueOf(1), appFromJson.getPaid());
        assertNotNull(appFromJson.getCategories());
        assertTrue(appFromJson.getCategories().contains("IAB2"));
        assertNotNull(appFromJson.getPublisher());
        assertEquals("pub-456", appFromJson.getPublisher().getId());
        assertNotNull(appFromJson.getContent());
        assertEquals("Test Content", appFromJson.getContent().getTitle());
    }

    @Test
    public void toJson_withPopulatedObject_createsCorrectJson() throws Exception {
        Publisher publisher = new Publisher();
        publisher.setId("pub-456");
        publisher.setName("Test Publisher");

        Content content = new Content();
        content.setTitle("Test Content");

        app.setId("app-id-123");
        app.setName("Test App");
        app.setPaid(0);
        app.setPublisher(publisher);
        app.setContent(content);

        JSONObject jsonResult = app.toJson();

        assertNotNull(jsonResult);
        assertEquals("app-id-123", jsonResult.getString("id"));
        assertEquals("Test App", jsonResult.getString("name"));
        assertEquals(0, jsonResult.getInt("paid"));

        JSONObject publisherJson = jsonResult.getJSONObject("publisher");
        assertNotNull(publisherJson);
        assertEquals("pub-456", publisherJson.getString("id"));

        JSONObject contentJson = jsonResult.getJSONObject("content");
        assertNotNull(contentJson);
        assertEquals("Test Content", contentJson.getString("title"));
    }
}