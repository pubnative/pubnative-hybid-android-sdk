// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class TrackingUrlModelTest {

    @Test
    public void defaultConstructor_initializesFieldsToDefaultValues() {
        TrackingUrlModel model = new TrackingUrlModel();

        assertNull(model.url);
        assertEquals(0, model.startTimestamp);
    }

    @Test
    public void directFieldAccess_setsAndGetsValues() {
        TrackingUrlModel model = new TrackingUrlModel();
        String testUrl = "https://example.com/track";
        long testTimestamp = System.currentTimeMillis();

        model.url = testUrl;
        model.startTimestamp = testTimestamp;

        assertEquals(testUrl, model.url);
        assertEquals(testTimestamp, model.startTimestamp);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create and populate the original object
        TrackingUrlModel originalModel = new TrackingUrlModel();
        originalModel.url = "https://example.com/tracker";
        originalModel.startTimestamp = 1672531200000L;

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalModel.toJson();
        assertNotNull(jsonObject);
        assertEquals("https://example.com/tracker", jsonObject.getString("url"));
        assertEquals(1672531200000L, jsonObject.getLong("startTimestamp"));

        // 3. Convert the JSON back into a new object
        TrackingUrlModel restoredModel = new TrackingUrlModel(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalModel.url, restoredModel.url);
        assertEquals(originalModel.startTimestamp, restoredModel.startTimestamp);
    }
}