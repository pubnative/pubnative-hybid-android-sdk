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
public class IdLocationTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        IdLocation location = new IdLocation();

        assertNull(location.lat);
        assertNull(location.lon);
        assertNull(location.type);
        assertNull(location.category);
        assertNull(location.accuracy);
        assertNull(location.ts);
    }

    @Test
    public void directFieldAccess_setsAndGetsValues() {
        IdLocation location = new IdLocation();
        String lat = "52.29";
        String lon = "7.42";
        String type = "gps";

        location.lat = lat;
        location.lon = lon;
        location.type = type;

        assertEquals(lat, location.lat);
        assertEquals(lon, location.lon);
        assertEquals(type, location.type);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create and populate the original object
        IdLocation originalLocation = new IdLocation();
        originalLocation.lat = "52.29";
        originalLocation.lon = "7.42";
        originalLocation.type = "gps";
        originalLocation.category = "test_category";
        originalLocation.accuracy = "10";
        originalLocation.ts = "1672531200";

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalLocation.toJson();
        assertNotNull(jsonObject);
        assertEquals("52.29", jsonObject.getString("lat"));
        assertEquals("10", jsonObject.getString("accuracy"));

        // 3. Convert the JSON back into a new object
        IdLocation restoredLocation = new IdLocation(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalLocation.lat, restoredLocation.lat);
        assertEquals(originalLocation.lon, restoredLocation.lon);
        assertEquals(originalLocation.type, restoredLocation.type);
        assertEquals(originalLocation.category, restoredLocation.category);
        assertEquals(originalLocation.accuracy, restoredLocation.accuracy);
        assertEquals(originalLocation.ts, restoredLocation.ts);
    }
}
