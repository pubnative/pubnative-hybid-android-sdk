// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class IdAppTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        IdApp idApp = new IdApp();
        assertNull(idApp.bundle_id);
        assertNull(idApp.users);
        assertNull(idApp.privacy);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create the full, multi-level nested object
        IdAppVendor vendor = new IdAppVendor();
        IdApl apl = new IdApl();
        apl.IDFA = "test-idfa";
        vendor.APL = apl;

        IdAppUser user = new IdAppUser();
        user.AUID = "auid-123";
        user.vendors = vendor;

        IdPrivacy privacy = new IdPrivacy();
        privacy.lat = false;

        IdApp originalApp = new IdApp();
        originalApp.bundle_id = "com.test.app";
        originalApp.users = List.of(user);
        originalApp.privacy = privacy;

        // 2. Convert to JSON
        JSONObject jsonObject = originalApp.toJson();
        assertNotNull(jsonObject);
        assertEquals("com.test.app", jsonObject.getString("bundle_id"));
        assertEquals("auid-123", jsonObject.getJSONArray("users").getJSONObject(0).getString("AUID"));
        assertEquals("test-idfa", jsonObject.getJSONArray("users").getJSONObject(0).getJSONObject("vendors").getJSONObject("APL").getString("IDFA"));
        assertFalse(jsonObject.getJSONObject("privacy").getBoolean("lat"));

        // 3. Convert back to object
        IdApp restoredApp = new IdApp(jsonObject);

        // 4. Assert deep equality
        assertEquals(originalApp.bundle_id, restoredApp.bundle_id);

        assertNotNull(restoredApp.privacy);
        assertEquals(originalApp.privacy.lat, restoredApp.privacy.lat);

        assertNotNull(restoredApp.users);
        assertEquals(1, restoredApp.users.size());
        IdAppUser restoredUser = restoredApp.users.get(0);
        assertEquals(user.AUID, restoredUser.AUID);

        assertNotNull(restoredUser.vendors);
        assertNotNull(restoredUser.vendors.APL);
        assertEquals(vendor.APL.IDFA, restoredUser.vendors.APL.IDFA);
    }
}
