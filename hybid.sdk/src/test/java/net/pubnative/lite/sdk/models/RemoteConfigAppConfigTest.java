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
public class RemoteConfigAppConfigTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        RemoteConfigAppConfig config = new RemoteConfigAppConfig();
        assertNull(config.app_token);
        assertNull(config.api);
        assertNull(config.features);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create the original object with nested data
        RemoteConfigAppFeatures features = new RemoteConfigAppFeatures();
        features.ad_formats = List.of("banner", "video");

        RemoteConfigAppConfig originalConfig = new RemoteConfigAppConfig();
        originalConfig.app_token = "test-token";
        originalConfig.enabled_protocols = List.of("VAST_3_0");
        originalConfig.features = features;

        // 2. Convert to JSON
        JSONObject jsonObject = originalConfig.toJson();
        assertNotNull(jsonObject);
        assertEquals("test-token", jsonObject.getString("app_token"));
        assertEquals("banner", jsonObject.getJSONObject("features").getJSONArray("ad_formats").get(0));

        // 3. Convert back to an object
        RemoteConfigAppConfig restoredConfig = new RemoteConfigAppConfig(jsonObject);

        // 4. Assert deep equality
        assertEquals(originalConfig.app_token, restoredConfig.app_token);
        assertEquals(originalConfig.enabled_protocols, restoredConfig.enabled_protocols);

        assertNotNull(restoredConfig.features);
        assertEquals(originalConfig.features.ad_formats, restoredConfig.features.ad_formats);
        assertNull(restoredConfig.features.rendering);
    }
}
