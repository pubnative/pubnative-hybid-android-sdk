// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MetricTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        Metric metric = new Metric();

        assertNull(metric.getType());
        assertNull(metric.getValue());
        assertNull(metric.getVendor());
    }

    @Test
    public void jsonConstructor_withValidJson_populatesAllFields() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "viewable_impression");
        jsonObject.put("value", 0.75f);
        jsonObject.put("vendor", "HyBid");

        Metric metric = new Metric(jsonObject);

        assertEquals("viewable_impression", metric.getType());
        assertEquals(Float.valueOf(0.75f), metric.getValue());
        assertEquals("HyBid", metric.getVendor());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This test ensures that an object created from JSON can be serialized
        // back into an identical JSON structure.

        // 1. Create the source JSON and the original object
        JSONObject originalJson = new JSONObject();
        originalJson.put("type", "click_rate");
        originalJson.put("value", 0.15);
        originalJson.put("vendor", "TestVendor");

        Metric originalMetric = new Metric(originalJson);

        // 2. Convert the object back to JSON
        JSONObject restoredJson = originalMetric.toJson();
        assertNotNull(restoredJson);

        // 3. Convert the restored JSON back into a new object
        Metric restoredMetric = new Metric(restoredJson);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalMetric.getType(), restoredMetric.getType());
        assertEquals(originalMetric.getValue(), restoredMetric.getValue());
        assertEquals(originalMetric.getVendor(), restoredMetric.getVendor());
    }
}
