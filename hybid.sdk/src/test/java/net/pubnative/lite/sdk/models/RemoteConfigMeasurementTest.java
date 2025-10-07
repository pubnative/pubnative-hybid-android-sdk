// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class RemoteConfigMeasurementTest {

    @Test
    public void defaultConstructor_initializesFieldToNull() {
        RemoteConfigMeasurement measurement = new RemoteConfigMeasurement();
        assertNull(measurement.viewability);
    }

    @Test
    public void directFieldAccess_setsAndGetsValue() {
        RemoteConfigMeasurement measurement = new RemoteConfigMeasurement();
        measurement.viewability = true;
        assertEquals(Boolean.TRUE, measurement.viewability);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create and populate the original object
        RemoteConfigMeasurement originalMeasurement = new RemoteConfigMeasurement();
        originalMeasurement.viewability = false;

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalMeasurement.toJson();
        assertNotNull(jsonObject);
        assertFalse(jsonObject.getBoolean("viewability"));

        // 3. Convert the JSON back into a new object
        RemoteConfigMeasurement restoredMeasurement = new RemoteConfigMeasurement(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalMeasurement.viewability, restoredMeasurement.viewability);
    }
}