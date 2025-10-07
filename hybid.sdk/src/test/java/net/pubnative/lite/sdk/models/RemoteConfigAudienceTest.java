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

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class RemoteConfigAudienceTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        RemoteConfigAudience audience = new RemoteConfigAudience();
        assertNull(audience.name);
        assertNull(audience.min_score);
        assertNull(audience.dependencies);
        assertNull(audience.params);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        RemoteConfigDependencies dependencies = new RemoteConfigDependencies();
        dependencies.models = List.of("model-a");

        RemoteConfigParams params = new RemoteConfigParams();
        params.distance_threshold = 500;

        RemoteConfigAudience original = new RemoteConfigAudience();
        original.name = "Test Audience";
        original.min_score = 0.75;
        original.requires_geolocation = true;
        original.dependencies = dependencies;
        original.params = params;

        JSONObject jsonObject = original.toJson();
        assertNotNull(jsonObject);
        assertEquals("Test Audience", jsonObject.getString("name"));
        assertEquals(0.75, jsonObject.getDouble("min_score"), 0.001);
        assertEquals("model-a", jsonObject.getJSONObject("dependencies").getJSONArray("models").get(0));
        assertEquals(500, jsonObject.getJSONObject("params").getInt("distance_threshold"));

        RemoteConfigAudience restored = new RemoteConfigAudience(jsonObject);

        assertEquals(original.name, restored.name);
        assertEquals(original.min_score, restored.min_score);
        assertEquals(original.requires_geolocation, restored.requires_geolocation);

        assertNotNull(restored.dependencies);
        assertEquals(original.dependencies.models, restored.dependencies.models);

        assertNotNull(restored.params);
        assertEquals(original.params.distance_threshold, restored.params.distance_threshold);
    }
}