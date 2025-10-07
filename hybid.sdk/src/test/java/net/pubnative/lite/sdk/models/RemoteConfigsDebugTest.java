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
public class RemoteConfigsDebugTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        RemoteConfigsDebug debug = new RemoteConfigsDebug();

        assertNull(debug.configids);
        assertNull(debug.sliceids);
        assertNull(debug.getConfigIds());
        assertNull(debug.getSliceIds());
    }

    @Test
    public void getters_and_directFieldAccess_workCorrectly() {
        RemoteConfigsDebug debug = new RemoteConfigsDebug();
        List<Integer> configIds = List.of(101, 102);
        List<Integer> sliceIds = List.of(201, 202);

        debug.configids = configIds;
        debug.sliceids = sliceIds;

        assertEquals(configIds, debug.getConfigIds());
        assertEquals(sliceIds, debug.getSliceIds());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create and populate the original object
        RemoteConfigsDebug originalDebug = new RemoteConfigsDebug();
        originalDebug.configids = List.of(1, 2, 3);
        originalDebug.sliceids = List.of(4, 5, 6);

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalDebug.toJson();
        assertNotNull(jsonObject);
        assertEquals(2, jsonObject.getJSONArray("configids").get(1));
        assertEquals(6, jsonObject.getJSONArray("sliceids").get(2));

        // 3. Convert the JSON back into a new object
        RemoteConfigsDebug restoredDebug = new RemoteConfigsDebug(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalDebug.getConfigIds(), restoredDebug.getConfigIds());
        assertEquals(originalDebug.getSliceIds(), restoredDebug.getSliceIds());
    }
}