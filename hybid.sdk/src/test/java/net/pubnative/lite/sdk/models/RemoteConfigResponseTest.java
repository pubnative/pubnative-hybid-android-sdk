// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class RemoteConfigResponseTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        RemoteConfigResponse response = new RemoteConfigResponse();
        assertNull(response.status);
        assertNull(response.configs);
        assertNull(response.error_message);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create the original object with nested data
        AdData atomData = new AdData();
        atomData.type = ConfigAssets.ATOM_ENABLED;
        atomData.data = new HashMap<>();
        atomData.data.put("boolean", true);

        SdkConfig sdkConfig = new SdkConfig();
        sdkConfig.app_level = List.of(atomData);

        RemoteConfigResponse originalResponse = new RemoteConfigResponse();
        originalResponse.status = RemoteConfigResponse.Status.OK;
        originalResponse.configs = sdkConfig;

        // 2. Convert to JSON
        JSONObject jsonObject = originalResponse.toJson();
        assertNotNull(jsonObject);
        assertEquals(RemoteConfigResponse.Status.OK, jsonObject.getString("status"));
        assertTrue(jsonObject.getJSONObject("configs").getJSONArray("app_level").getJSONObject(0).getJSONObject("data").getBoolean("boolean"));

        // 3. Convert back to an object
        RemoteConfigResponse restoredResponse = new RemoteConfigResponse(jsonObject);

        // 4. Assert deep equality
        assertEquals(originalResponse.status, restoredResponse.status);
        assertNotNull(restoredResponse.configs);
        assertNotNull(restoredResponse.configs.app_level);
        assertEquals(1, restoredResponse.configs.app_level.size());
        assertTrue(restoredResponse.configs.isAtomEnabled());
    }
}