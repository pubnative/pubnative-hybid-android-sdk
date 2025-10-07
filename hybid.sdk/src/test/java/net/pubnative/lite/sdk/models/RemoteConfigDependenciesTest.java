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
public class RemoteConfigDependenciesTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        RemoteConfigDependencies dependencies = new RemoteConfigDependencies();
        assertNull(dependencies.models);
        assertNull(dependencies.metadata);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        RemoteConfigDependencies original = new RemoteConfigDependencies();
        original.models = List.of("model-a", "model-b");
        original.metadata = List.of("meta-1");

        JSONObject jsonObject = original.toJson();
        assertNotNull(jsonObject);
        assertEquals("model-b", jsonObject.getJSONArray("models").get(1));

        RemoteConfigDependencies restored = new RemoteConfigDependencies(jsonObject);
        assertEquals(original.models, restored.models);
        assertEquals(original.metadata, restored.metadata);
    }
}