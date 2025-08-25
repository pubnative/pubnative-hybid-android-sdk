// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class OpenRTBAdRequestTest {

    @Test
    public void constructor_withAppTokenAndZoneId_assignsParentFieldsAndDefaults() {
        String appToken = "test-app-token";
        String zoneId = "test-zone-id";

        OpenRTBAdRequest request = new OpenRTBAdRequest(appToken, zoneId);

        // Assert fields inherited from AdRequest
        assertEquals(appToken, request.appToken);
        assertEquals(zoneId, request.zoneId);

        // Assert fields with default initialization values
        assertEquals(Integer.valueOf(0), request.getTest());
        assertEquals(Integer.valueOf(2), request.getAt());
        assertEquals(Integer.valueOf(0), request.getAllimps());
    }

    @Test
    public void testSettersAndGetters() {
        OpenRTBAdRequest request = new OpenRTBAdRequest("token", "zone");

        String id = "request-id-123";
        App app = new App();
        app.setId("app-id-456");
        Device device = new Device();
        device.setModel("Pixel Test");

        request.setId(id);
        request.setApp(app);
        request.setDevice(device);

        assertEquals(id, request.getId());
        assertEquals(app, request.getApp());
        assertEquals("app-id-456", request.getApp().getId());
        assertEquals(device, request.getDevice());
        assertEquals("Pixel Test", request.getDevice().getModel());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This "round-trip" test ensures that a complex, nested object can be
        // serialized to JSON and then deserialized back into an identical object.

        // 1. Create the original object with all its nested dependencies
        App app = new App();
        app.setId("app-id-456");
        app.setBundle("com.test.app");

        Device device = new Device();
        device.setMake("Google");
        device.setModel("Pixel Test");

        Imp imp = new Imp();
        imp.setId("imp-id-789");

        OpenRTBAdRequest originalRequest = new OpenRTBAdRequest("test-token", "test-zone");
        originalRequest.setId("request-id-123");
        originalRequest.setApp(app);
        originalRequest.setDevice(device);
        originalRequest.setImp(Arrays.asList(imp));
        originalRequest.setAt(1); // Override default

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalRequest.toJson();
        assertNotNull(jsonObject);
        assertEquals("request-id-123", jsonObject.getString("id"));
        assertEquals("com.test.app", jsonObject.getJSONObject("app").getString("bundle"));
        assertEquals("imp-id-789", jsonObject.getJSONArray("imp").getJSONObject(0).getString("id"));

        // 3. Convert the JSON back into a new object
        OpenRTBAdRequest restoredRequest = new OpenRTBAdRequest(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalRequest.getId(), restoredRequest.getId());
        assertEquals(originalRequest.getAt(), restoredRequest.getAt());

        // Assert nested App
        assertNotNull(restoredRequest.getApp());
        assertEquals(originalRequest.getApp().getId(), restoredRequest.getApp().getId());
        assertEquals(originalRequest.getApp().getBundle(), restoredRequest.getApp().getBundle());

        // Assert nested Device
        assertNotNull(restoredRequest.getDevice());
        assertEquals(originalRequest.getDevice().getMake(), restoredRequest.getDevice().getMake());
        assertEquals(originalRequest.getDevice().getModel(), restoredRequest.getDevice().getModel());

        // Assert nested List<Imp>
        assertNotNull(restoredRequest.getImp());
        assertEquals(1, restoredRequest.getImp().size());
        assertEquals(originalRequest.getImp().get(0).getId(), restoredRequest.getImp().get(0).getId());
    }
}
