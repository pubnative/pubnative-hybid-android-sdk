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
public class RemoteConfigVoyagerTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        RemoteConfigVoyager voyager = new RemoteConfigVoyager();
        assertNull(voyager.audience_refresh_frequency);
        assertNull(voyager.app_info);
        assertNull(voyager.models);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create original object with nested data
        RemoteConfigAppInfo appInfo = new RemoteConfigAppInfo();
        appInfo.app_store_id = "12345";

        RemoteConfigMLModel mlModel = new RemoteConfigMLModel();
        mlModel.name = "test-model";

        RemoteConfigVoyager originalVoyager = new RemoteConfigVoyager();
        originalVoyager.audience_refresh_frequency = 60;
        originalVoyager.vg_targeting_key = "test_key";
        originalVoyager.app_info = appInfo;
        originalVoyager.models = List.of(mlModel);

        // 2. Convert to JSON
        JSONObject jsonObject = originalVoyager.toJson();
        assertNotNull(jsonObject);
        assertEquals(60, jsonObject.getInt("audience_refresh_frequency"));
        assertEquals("12345", jsonObject.getJSONObject("app_info").getString("app_store_id"));
        assertEquals("test-model", jsonObject.getJSONArray("models").getJSONObject(0).getString("name"));

        // 3. Convert back to object
        RemoteConfigVoyager restoredVoyager = new RemoteConfigVoyager(jsonObject);

        // 4. Assert deep equality
        assertEquals(originalVoyager.audience_refresh_frequency, restoredVoyager.audience_refresh_frequency);
        assertEquals(originalVoyager.vg_targeting_key, restoredVoyager.vg_targeting_key);

        assertNotNull(restoredVoyager.app_info);
        assertEquals(originalVoyager.app_info.app_store_id, restoredVoyager.app_info.app_store_id);

        assertNotNull(restoredVoyager.models);
        assertEquals(1, restoredVoyager.models.size());
        assertEquals(originalVoyager.models.get(0).name, restoredVoyager.models.get(0).name);
    }
}