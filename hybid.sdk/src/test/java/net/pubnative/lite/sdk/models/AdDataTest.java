// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

    //  --------------- Constructors & JSON ---------------
    @Test
    public void constructor_default_createsEmptyObject() {
        AdData emptyAdData = new AdData();
        assertNull(emptyAdData.type);
        assertNull(emptyAdData.data);
    }

    @Test
    public void convenienceConstructor_initializesFields() {
        AdData constructedData = new AdData("html", "htmlbanner", "<p>hello</p>");
        assertEquals("htmlbanner", constructedData.type);
        assertEquals("<p>hello</p>", constructedData.getStringField("html"));
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

    // --------------- String Getters ---------------
    @Test
    public void getStringField_whenFieldExists_returnsValue() {
        adData.data.put("text", "Hello World");
        assertEquals("Hello World", adData.getText());
    }

    @Test
    public void getStringField_whenFieldIsWrongType_returnsEmptyString() {
        adData.data.put("text", 123);
        assertEquals("", adData.getStringField("text"));
    }

    @Test
    public void getStringField_whenFieldIsMissing_returnsNull() {
        assertNull(adData.getStringField("text"));
    }

    // --------------- Integer Getters ---------------
    @Test
    public void getIntField_whenFieldExists_returnsValue() {
        adData.data.put("w", 320);
        assertEquals(Integer.valueOf(320), adData.getIntField("w"));
    }

    @Test
    public void getIntField_whenFieldIsMissing_returnsNull() {
        assertNull(adData.getIntField("w"));
    }

    @Test
    public void getIntField_whenFieldIsWrongType_returnsNull() {
        adData.data.put("w", "not an integer");
        assertNull(adData.getIntField("w"));
    }

    @Test
    public void getWidth_whenFieldExists_returnsWidth() {
        adData.data.put("w", 320);
        assertEquals(320, adData.getWidth());
    }

    @Test
    public void getWidth_whenFieldIsMissing_returnsZero() {
        assertEquals(0, adData.getWidth());
    }

    @Test
    public void getHeight_whenFieldExists_returnsHeight() {
        adData.data.put("h", 50);
        assertEquals(50, adData.getHeight());
    }

    @Test
    public void getHeight_whenFieldIsMissing_returnsZero() {
        assertEquals(0, adData.getHeight());
    }

    // --------------- Double Getters ---------------
    @Test
    public void getDoubleField_withValidDouble_returnsDouble() {
        adData.data.put("number", 123.45);
        assertEquals(Double.valueOf(123.45), adData.getNumber());
    }

    @Test
    public void getDoubleField_withValidInteger_returnsCastedDouble() {
        adData.data.put("number", 123);
        assertEquals(Double.valueOf(123.0), adData.getNumber());
    }

    @Test
    public void getDoubleField_whenFieldIsMissing_returnsNull() {
        assertNull(adData.getDoubleField("number"));
    }

    @Test
    public void getDoubleField_withWrongType_returnsNull() {
        adData.data.put("number", "not a number");
        assertNull(adData.getNumber());
    }

    // --------------- Boolean Getters ---------------
    @Test
    public void getBooleanField_withValidBoolean_returnsBoolean() {
        adData.data.put("boolean", true);
        assertEquals(Boolean.TRUE, adData.getBoolean());
    }

    @Test
    public void getBooleanField_whenFieldIsMissing_returnsNull() {
        assertNull(adData.getBooleanField("boolean"));
    }

    @Test
    public void getBooleanField_withWrongType_returnsNull() {
        adData.data.put("boolean", "not a boolean");
        assertNull(adData.getBoolean());
    }

    //  --------------- JSONObject Getters ---------------
    @Test
    public void getJSONObjectField_withValidObject_returnsObject() {
        JSONObject json = new JSONObject();
        adData.data.put("json", json);
        assertEquals(json, adData.getJSONObjectField("json"));
    }

    @Test
    public void getJSONObjectField_withWrongType_returnsNull() {
        // This test now verifies the corrected, safe behavior.
        adData.data.put("json", "not a json object");
        assertNull(adData.getJSONObjectField("json"));
    }

    @Test
    public void getJSONObjectField_whenFieldIsMissing_returnsNull() {
        assertNull(adData.getJSONObjectField("json"));
    }

    // --------------- Utility Methods ---------------
    @Test
    public void hasField_whenFieldExists_returnsTrue() {
        adData.data.put("url", "https://example.com");
        assertTrue(adData.hasField("url"));
    }

    @Test
    public void hasField_whenFieldDoesNotExist_returnsFalse() {
        assertFalse(adData.hasField("url"));
    }

    @Test
    public void hasField_whenDataMapIsNull_returnsFalse() {
        adData.data = null;
        assertFalse(adData.hasField("any_key"));
    }

    @Test
    public void getDataField_whenDataIsNull_returnsNull() {
        adData.data = null;
        assertNull(adData.getDataField("any_key"));
    }
}