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
public class RemoteConfigResolutionsTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        RemoteConfigResolutions resolutions = new RemoteConfigResolutions();
        assertNull(resolutions.audience_id);
        assertNull(resolutions.taxonomy_2_ids);
        assertNull(resolutions.start_time);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        RemoteConfigResolutions original = new RemoteConfigResolutions();
        original.audience_id = "audience-123";
        original.taxonomy_2_ids = List.of("tax2-a", "tax2-b");
        original.start_time = 1672531200;

        JSONObject jsonObject = original.toJson();
        assertNotNull(jsonObject);
        assertEquals("audience-123", jsonObject.getString("audience_id"));

        RemoteConfigResolutions restored = new RemoteConfigResolutions(jsonObject);
        assertEquals(original.audience_id, restored.audience_id);
        assertEquals(original.taxonomy_2_ids, restored.taxonomy_2_ids);
        assertEquals(original.start_time, restored.start_time);
    }
}