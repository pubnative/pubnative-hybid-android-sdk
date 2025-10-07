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
public class RemoteConfigAppFeaturesTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        RemoteConfigAppFeatures features = new RemoteConfigAppFeatures();
        assertNull(features.ad_formats);
        assertNull(features.rendering);
        assertNull(features.reporting);
        assertNull(features.user_consent);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create and populate the original object
        RemoteConfigAppFeatures originalFeatures = new RemoteConfigAppFeatures();
        originalFeatures.ad_formats = List.of("banner", "video");
        originalFeatures.reporting = List.of("enabled");

        // 2. Convert to JSON
        JSONObject jsonObject = originalFeatures.toJson();
        assertNotNull(jsonObject);
        assertEquals("banner", jsonObject.getJSONArray("ad_formats").get(0));

        // 3. Convert back to an object
        RemoteConfigAppFeatures restoredFeatures = new RemoteConfigAppFeatures(jsonObject);

        // 4. Assert that the objects are identical
        assertEquals(originalFeatures.ad_formats, restoredFeatures.ad_formats);
        assertEquals(originalFeatures.reporting, restoredFeatures.reporting);
        assertNull(restoredFeatures.rendering); // Verify unset field is null
    }
}
