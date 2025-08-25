// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.bidstream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class DeviceExtensionTest {

    @Test
    public void constructor_withAllParameters_assignsFieldsCorrectly() {
        List<String> inputLanguages = Arrays.asList("en-US", "de-DE");
        Integer charging = 1;
        Integer batteryLevel = 85;
        Integer batterySaver = 0;
        Integer diskSpace = 51200; // 50 GB in MB
        Integer totalDisk = 122880; // 120 GB in MB
        Integer darkMode = 1;
        Integer dnd = 0;
        Integer airplane = 0;
        Integer headset = 1;
        Integer ringMute = 0;

        DeviceExtension deviceExtension = new DeviceExtension(inputLanguages, charging, batteryLevel,
                batterySaver, diskSpace, totalDisk, darkMode, dnd, airplane, headset, ringMute);

        assertEquals(inputLanguages, deviceExtension.inputlanguages);
        assertEquals(charging, deviceExtension.charging);
        assertEquals(batteryLevel, deviceExtension.batterylevel);
        assertEquals(batterySaver, deviceExtension.batterysaver);
        assertEquals(diskSpace, deviceExtension.diskspace);
        assertEquals(totalDisk, deviceExtension.totaldisk);
        assertEquals(darkMode, deviceExtension.darkmode);
        assertEquals(dnd, deviceExtension.dnd);
        assertEquals(airplane, deviceExtension.airplane);
        assertEquals(headset, deviceExtension.headset);
        assertEquals(ringMute, deviceExtension.ringmute);
    }

    @Test
    public void toJson_withPopulatedObject_createsCorrectJsonObject() throws Exception {
        List<String> inputLanguages = Arrays.asList("en-US", "fr-FR");
        Integer charging = 0;
        Integer batteryLevel = 50;

        // Create an object with some data
        DeviceExtension deviceExtension = new DeviceExtension(inputLanguages, charging, batteryLevel,
                null, null, null, null, null, null, null, null);

        JSONObject jsonResult = deviceExtension.toJson();

        assertNotNull(jsonResult);
        assertEquals(2, jsonResult.getJSONArray("inputlanguages").length());
        assertEquals("fr-FR", jsonResult.getJSONArray("inputlanguages").get(1));
        assertEquals(0, jsonResult.getInt("charging"));
        assertEquals(50, jsonResult.getInt("batterylevel"));
        // Fields that were null should not be present in the JSON
        assertFalse(jsonResult.has("batterysaver"));
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This test ensures that an object serialized to JSON can be deserialized back
        // into an object with the same values.

        List<String> inputLanguages = Arrays.asList("en-US", "es-ES");
        DeviceExtension originalExtension = new DeviceExtension(inputLanguages, 1, 99,
                0, 1024, 2048, 1, 0, 0, 1, 0);

        JSONObject jsonObject = originalExtension.toJson();

        DeviceExtension newExtension = new DeviceExtension(null, null, null, null, null, null, null, null, null, null, null);
        newExtension.fromJson(jsonObject);

        assertEquals(originalExtension.inputlanguages, newExtension.inputlanguages);
        assertEquals(originalExtension.charging, newExtension.charging);
        assertEquals(originalExtension.batterylevel, newExtension.batterylevel);
        assertEquals(originalExtension.batterysaver, newExtension.batterysaver);
        assertEquals(originalExtension.diskspace, newExtension.diskspace);
        assertEquals(originalExtension.totaldisk, newExtension.totaldisk);
        assertEquals(originalExtension.darkmode, newExtension.darkmode);
        assertEquals(originalExtension.dnd, newExtension.dnd);
        assertEquals(originalExtension.airplane, newExtension.airplane);
        assertEquals(originalExtension.headset, newExtension.headset);
        assertEquals(originalExtension.ringmute, newExtension.ringmute);
    }
}