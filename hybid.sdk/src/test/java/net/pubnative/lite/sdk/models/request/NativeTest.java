// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class NativeTest {

    private Native aNative;

    @Before
    public void setUp() {
        aNative = new Native();
    }

    @Test
    public void testSettersAndGetters() {
        String request = "native_request_data";
        String version = "1.2";
        List<Integer> api = Arrays.asList(3, 5);
        List<Integer> blockedAttrs = Arrays.asList(1, 2);

        aNative.setRequest(request);
        aNative.setVer(version);
        aNative.setApi(api);
        aNative.setBlockedAttr(blockedAttrs);

        assertEquals(request, aNative.getRequest());
        assertEquals(version, aNative.getVer());
        assertEquals(api, aNative.getApi());
        assertEquals(blockedAttrs, aNative.getBlockedAttr());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This test ensures that an object serialized to JSON
        // can be deserialized back into an identical object.

        // 1. Create and populate the original object
        Native originalNative = new Native();
        originalNative.setRequest("native_request_data_v2");
        originalNative.setVer("1.2.1");
        originalNative.setApi(Arrays.asList(3, 5, 6));
        originalNative.setBlockedAttr(Arrays.asList(1, 2, 4));

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalNative.toJson();
        assertNotNull(jsonObject);
        assertEquals("1.2.1", jsonObject.getString("ver"));
        assertEquals(5, jsonObject.getJSONArray("api").get(1));

        // 3. Convert the JSON back into a new object
        Native restoredNative = new Native(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalNative.getRequest(), restoredNative.getRequest());
        assertEquals(originalNative.getVer(), restoredNative.getVer());
        assertEquals(originalNative.getApi(), restoredNative.getApi());
        assertEquals(originalNative.getBlockedAttr(), restoredNative.getBlockedAttr());
    }
}
