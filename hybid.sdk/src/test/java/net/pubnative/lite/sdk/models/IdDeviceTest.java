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
public class IdDeviceTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        IdDevice device = new IdDevice();

        assertNull(device.id);
        assertNull(device.os);
        assertNull(device.manufacture);
        assertNull(device.model);
        assertNull(device.brand);
        assertNull(device.battery);
    }

    @Test
    public void directFieldAccess_setsAndGetsValues() {
        IdDevice device = new IdDevice();
        String testId = "device-123";
        String testModel = "Pixel Test";
        IdOs os = new IdOs();
        os.name = "Android";

        device.id = testId;
        device.model = testModel;
        device.os = os;

        assertEquals(testId, device.id);
        assertEquals(testModel, device.model);
        assertEquals(os, device.os);
        assertEquals("Android", device.os.name);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create the original object with nested data
        IdOs os = new IdOs();
        os.name = "Android";
        os.version = "12";

        IdBattery battery = new IdBattery();
        battery.capacity = "90";
        battery.charging = false;

        IdDevice originalDevice = new IdDevice();
        originalDevice.id = "device-abc";
        originalDevice.manufacture = "Google";
        originalDevice.model = "Pixel";
        originalDevice.os = os;
        originalDevice.battery = battery;

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalDevice.toJson();
        assertNotNull(jsonObject);
        assertEquals("device-abc", jsonObject.getString("id"));
        assertEquals("Android", jsonObject.getJSONObject("os").getString("name"));
        assertEquals("90", jsonObject.getJSONObject("battery").getString("capacity"));

        // 3. Convert the JSON back into a new object
        IdDevice restoredDevice = new IdDevice(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalDevice.id, restoredDevice.id);
        assertEquals(originalDevice.manufacture, restoredDevice.manufacture);
        assertEquals(originalDevice.model, restoredDevice.model);

        // Assert nested IdOs
        assertNotNull(restoredDevice.os);
        assertEquals(originalDevice.os.name, restoredDevice.os.name);
        assertEquals(originalDevice.os.version, restoredDevice.os.version);

        // Assert nested IdBattery
        assertNotNull(restoredDevice.battery);
        assertEquals(originalDevice.battery.capacity, restoredDevice.battery.capacity);
        assertEquals(originalDevice.battery.charging, restoredDevice.battery.charging);
    }
}
