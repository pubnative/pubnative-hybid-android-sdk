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
public class IdBatteryTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        IdBattery battery = new IdBattery();

        assertNull(battery.capacity);
        assertNull(battery.charging);
    }

    @Test
    public void directFieldAccess_setsAndGetsValues() {
        IdBattery battery = new IdBattery();
        String testCapacity = "95";
        Boolean isCharging = true;

        battery.capacity = testCapacity;
        battery.charging = isCharging;

        assertEquals(testCapacity, battery.capacity);
        assertEquals(isCharging, battery.charging);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This test ensures that an object serialized to JSON can be
        // deserialized back into an object with identical values.

        // 1. Create and populate the original object
        IdBattery originalBattery = new IdBattery();
        originalBattery.capacity = "88";
        originalBattery.charging = false;

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalBattery.toJson();
        assertNotNull(jsonObject);
        assertEquals("88", jsonObject.getString("capacity"));
        assertFalse(jsonObject.getBoolean("charging"));

        // 3. Convert the JSON back into a new object
        IdBattery restoredBattery = new IdBattery(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalBattery.capacity, restoredBattery.capacity);
        assertEquals(originalBattery.charging, restoredBattery.charging);
    }
}
