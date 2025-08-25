// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AdDataTest {

    private AdData adData;

    @Before
    public void setup() {
        adData = new AdData();
        adData.data = new HashMap<>();
    }

    @Test
    public void convenienceConstructor_initializesFields() {
        AdData constructedData = new AdData("html", "htmlbanner", "<p>hello</p>");

        assertEquals("htmlbanner", constructedData.type);
        assertEquals("<p>hello</p>", constructedData.getStringField("html"));
    }

    @Test
    public void getStringField_whenFieldExistsAndIsString_returnsValue() {
        adData.data.put("text", "Hello World");
        assertEquals("Hello World", adData.getText());
    }

    @Test
    public void getStringField_whenFieldIsWrongType_returnsEmptyString() {
        adData.data.put("text", 123); // Put an Integer instead of a String
        assertEquals("", adData.getText());
    }

    @Test
    public void getStringField_whenFieldDoesNotExist_returnsNull() {
        // The underlying map returns null, but getStringField has a cast that can cause issues.
        // The implementation catches ClassCastException and returns "", but direct null should be handled.
        // Let's test the getText() wrapper which relies on getStringField
        assertNull(adData.getStringField("non_existent_field"));
    }

    @Test
    public void getIntField_whenFieldExists_returnsValue() {
        adData.data.put("w", 320);
        assertEquals(320, adData.getWidth());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        AdData original = new AdData();
        original.type = "test_type";
        original.data = new HashMap<>();
        original.data.put("key", "value");

        JSONObject json = original.toJson();
        AdData restored = new AdData(json);

        assertEquals(original.type, restored.type);
        assertEquals(original.getStringField("key"), restored.getStringField("key"));
    }
}