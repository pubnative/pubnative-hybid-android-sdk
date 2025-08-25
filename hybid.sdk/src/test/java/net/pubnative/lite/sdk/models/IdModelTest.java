// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class IdModelTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        IdModel idModel = new IdModel();
        assertNull(idModel.apps);
        assertNull(idModel.device);
        assertNull(idModel.users);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create the full, multi-level nested object
        IdApp app = new IdApp();
        app.bundle_id = "com.test.app";

        IdDevice device = new IdDevice();
        device.model = "Pixel Test";

        IdUser user = new IdUser();
        user.SUID = "suid-123";

        IdModel originalModel = new IdModel();
        originalModel.apps = List.of(app);
        originalModel.device = device;
        originalModel.users = List.of(user);

        // 2. Convert to JSON
        JSONObject jsonObject = originalModel.toJson();
        assertNotNull(jsonObject);
        assertEquals("com.test.app", jsonObject.getJSONArray("apps").getJSONObject(0).getString("bundle_id"));
        assertEquals("Pixel Test", jsonObject.getJSONObject("device").getString("model"));
        assertEquals("suid-123", jsonObject.getJSONArray("users").getJSONObject(0).getString("SUID"));

        // 3. Convert back to object
        IdModel restoredModel = new IdModel(jsonObject);

        // 4. Assert deep equality
        assertNotNull(restoredModel.apps);
        assertEquals(1, restoredModel.apps.size());
        assertEquals(originalModel.apps.get(0).bundle_id, restoredModel.apps.get(0).bundle_id);

        assertNotNull(restoredModel.device);
        assertEquals(originalModel.device.model, restoredModel.device.model);

        assertNotNull(restoredModel.users);
        assertEquals(1, restoredModel.users.size());
        assertEquals(originalModel.users.get(0).SUID, restoredModel.users.get(0).SUID);
    }
}
