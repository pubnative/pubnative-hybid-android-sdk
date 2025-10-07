// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class RemoteConfigModelTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        RemoteConfigModel model = new RemoteConfigModel();
        assertNull(model.ttl);
        assertNull(model.app_config);
        assertNull(model.measurement);
        assertNull(model.voyager);
        assertNull(model.key);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create the original object with nested data
        RemoteConfigAppConfig appConfig = new RemoteConfigAppConfig();
        appConfig.app_token = "test-token";

        RemoteConfigMeasurement measurement = new RemoteConfigMeasurement();
        measurement.viewability = true;

        RemoteConfigVoyager voyager = new RemoteConfigVoyager();
        voyager.session_sample = 50;

        RemoteConfigModel originalModel = new RemoteConfigModel();
        originalModel.ttl = 3600;
        originalModel.key = "config_key";
        originalModel.app_config = appConfig;
        originalModel.measurement = measurement;
        originalModel.voyager = voyager;

        // 2. Convert to JSON
        JSONObject jsonObject = originalModel.toJson();
        assertNotNull(jsonObject);
        assertEquals(3600, jsonObject.getInt("ttl"));
        assertEquals("test-token", jsonObject.getJSONObject("app_config").getString("app_token"));
        assertTrue(jsonObject.getJSONObject("measurement").getBoolean("viewability"));
        assertEquals(50, jsonObject.getJSONObject("voyager").getInt("session_sample"));

        // 3. Convert back to an object
        RemoteConfigModel restoredModel = new RemoteConfigModel(jsonObject);

        // 4. Assert deep equality
        assertEquals(originalModel.ttl, restoredModel.ttl);
        assertEquals(originalModel.key, restoredModel.key);

        assertNotNull(restoredModel.app_config);
        assertEquals(originalModel.app_config.app_token, restoredModel.app_config.app_token);

        assertNotNull(restoredModel.measurement);
        assertEquals(originalModel.measurement.viewability, restoredModel.measurement.viewability);

        assertNotNull(restoredModel.voyager);
        assertEquals(originalModel.voyager.session_sample, restoredModel.voyager.session_sample);
    }
}