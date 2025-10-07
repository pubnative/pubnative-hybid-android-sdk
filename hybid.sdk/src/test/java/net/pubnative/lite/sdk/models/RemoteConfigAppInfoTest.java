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
public class RemoteConfigAppInfoTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        RemoteConfigAppInfo appInfo = new RemoteConfigAppInfo();

        assertNull(appInfo.app_store_id);
        assertNull(appInfo.iab_categories);
        assertNull(appInfo.pf);
        assertNull(appInfo.pm);
    }

    @Test
    public void directFieldAccess_setsAndGetsValues() {
        RemoteConfigAppInfo appInfo = new RemoteConfigAppInfo();
        String appId = "12345";
        List<String> categories = List.of("IAB1", "IAB2");
        Double pf = 0.5;
        Double pm = 0.7;

        appInfo.app_store_id = appId;
        appInfo.iab_categories = categories;
        appInfo.pf = pf;
        appInfo.pm = pm;

        assertEquals(appId, appInfo.app_store_id);
        assertEquals(categories, appInfo.iab_categories);
        assertEquals(pf, appInfo.pf);
        assertEquals(pm, appInfo.pm);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create and populate the original object
        RemoteConfigAppInfo originalInfo = new RemoteConfigAppInfo();
        originalInfo.app_store_id = "54321";
        originalInfo.iab_categories = List.of("IAB3", "IAB4");
        originalInfo.pf = 0.8;
        originalInfo.pm = 0.9;

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalInfo.toJson();
        assertNotNull(jsonObject);
        assertEquals("54321", jsonObject.getString("app_store_id"));
        assertEquals(0.8, jsonObject.getDouble("pf"), 0.001);

        // 3. Convert the JSON back into a new object
        RemoteConfigAppInfo restoredInfo = new RemoteConfigAppInfo(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalInfo.app_store_id, restoredInfo.app_store_id);
        assertEquals(originalInfo.iab_categories, restoredInfo.iab_categories);
        assertEquals(originalInfo.pf, restoredInfo.pf);
        assertEquals(originalInfo.pm, restoredInfo.pm);
    }
}
