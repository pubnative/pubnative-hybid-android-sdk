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
public class RemoteConfigParamsTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        RemoteConfigParams params = new RemoteConfigParams();
        assertNull(params.wl_taxonomy2);
        assertNull(params.resolutions);
        assertNull(params.distance_threshold);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        RemoteConfigResolutions resolutions = new RemoteConfigResolutions();
        resolutions.audience_id = "audience-123";

        RemoteConfigParams original = new RemoteConfigParams();
        original.wl_taxonomy2 = List.of("tax2-a");
        original.distance_threshold = 1000;
        original.resolutions = resolutions;

        JSONObject jsonObject = original.toJson();
        assertNotNull(jsonObject);
        assertEquals(1000, jsonObject.getInt("distance_threshold"));
        assertEquals("audience-123", jsonObject.getJSONObject("resolutions").getString("audience_id"));

        RemoteConfigParams restored = new RemoteConfigParams(jsonObject);

        assertEquals(original.wl_taxonomy2, restored.wl_taxonomy2);
        assertEquals(original.distance_threshold, restored.distance_threshold);
        assertNotNull(restored.resolutions);
        assertEquals(original.resolutions.audience_id, restored.resolutions.audience_id);
    }
}