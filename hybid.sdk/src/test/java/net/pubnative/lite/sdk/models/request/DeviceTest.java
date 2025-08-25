// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import net.pubnative.lite.sdk.models.bidstream.DeviceExtension;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class DeviceTest {

    private Device device;

    @Before
    public void setUp() {
        device = new Device();
    }

    @Test
    public void testSettersAndGetters() {
        String userAgent = "Test User Agent";
        String make = "Google";
        String model = "Pixel Test";
        Integer width = 1080;
        Integer height = 1920;
        Geo geo = new Geo();
        geo.setCountry("USA");

        device.setUserAgent(userAgent);
        device.setMake(make);
        device.setModel(model);
        device.setW(width);
        device.setH(height);
        device.setGeo(geo);

        assertEquals(userAgent, device.getUserAgent());
        assertEquals(make, device.getMake());
        assertEquals(model, device.getModel());
        assertEquals(width, device.getW());
        assertEquals(height, device.getH());
        assertEquals(geo, device.getGeo());
        assertEquals("USA", device.getGeo().getCountry());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This test ensures that a complex, nested object can be
        // serialized to JSON and then deserialized back into an identical object.

        // 1. Create the original object with all its nested dependencies
        Geo geo = new Geo();
        geo.setLat(34.05f);
        geo.setCountry("USA");

        List<String> languages = Arrays.asList("en-US", "de-DE");
        DeviceExtension ext = new DeviceExtension(languages, 1, 85, 0, 51200, 122880, 1, 0, 0, 1, 0);

        UserAgent sua = new UserAgent();
        sua.setModel("Pixel");

        Device originalDevice = new Device();
        originalDevice.setUserAgent("Test UA");
        originalDevice.setMake("Google");
        originalDevice.setModel("Pixel Test");
        originalDevice.setW(1080);
        originalDevice.setH(1920);
        originalDevice.setGeo(geo);
        originalDevice.setExt(ext);
        originalDevice.setSua(sua);

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalDevice.toJson();
        assertNotNull(jsonObject);
        assertEquals("Google", jsonObject.getString("make"));
        assertEquals("USA", jsonObject.getJSONObject("geo").getString("country"));
        assertEquals(1, jsonObject.getJSONObject("ext").getInt("charging"));
        assertEquals("Pixel", jsonObject.getJSONObject("sua").getString("model"));

        // 3. Convert the JSON back into a new object
        Device restoredDevice = new Device(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalDevice.getUserAgent(), restoredDevice.getUserAgent());
        assertEquals(originalDevice.getMake(), restoredDevice.getMake());
        assertEquals(originalDevice.getModel(), restoredDevice.getModel());

        // Assert nested Geo object
        assertNotNull(restoredDevice.getGeo());
        assertEquals(originalDevice.getGeo().getCountry(), restoredDevice.getGeo().getCountry());

        // Assert nested DeviceExtension object
        assertNotNull(restoredDevice.getExt());
        assertEquals(originalDevice.getExt().charging, restoredDevice.getExt().charging);
        assertEquals(originalDevice.getExt().inputlanguages, restoredDevice.getExt().inputlanguages);

        // Assert nested UserAgent object
        assertNotNull(restoredDevice.getSua());
        assertEquals(originalDevice.getSua().getModel(), restoredDevice.getSua().getModel());
    }
}