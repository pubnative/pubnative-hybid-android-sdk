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

@RunWith(RobolectricTestRunner.class)
public class IdAppUserTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        IdAppUser user = new IdAppUser();
        assertNull(user.AUID);
        assertNull(user.SUID);
        assertNull(user.vendors);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create original object with nested data
        IdAppVendor vendor = new IdAppVendor();
        IdApl apl = new IdApl();
        apl.IDFA = "test-idfa";
        vendor.APL = apl;

        IdAppUser originalUser = new IdAppUser();
        originalUser.AUID = "auid-123";
        originalUser.SUID = "suid-456";
        originalUser.vendors = vendor;

        // 2. Convert to JSON
        JSONObject jsonObject = originalUser.toJson();
        assertNotNull(jsonObject);
        assertEquals("auid-123", jsonObject.getString("AUID"));
        assertEquals("test-idfa", jsonObject.getJSONObject("vendors").getJSONObject("APL").getString("IDFA"));

        // 3. Convert back to object
        IdAppUser restoredUser = new IdAppUser(jsonObject);

        // 4. Assert deep equality
        assertEquals(originalUser.AUID, restoredUser.AUID);
        assertEquals(originalUser.SUID, restoredUser.SUID);
        assertNotNull(restoredUser.vendors);
        assertNotNull(restoredUser.vendors.APL);
        assertEquals(originalUser.vendors.APL.IDFA, restoredUser.vendors.APL.IDFA);
    }
}
