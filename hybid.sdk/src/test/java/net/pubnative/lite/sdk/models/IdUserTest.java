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
public class IdUserTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        IdUser user = new IdUser();
        assertNull(user.SUID);
        assertNull(user.emails);
        assertNull(user.vendors);
        assertNull(user.locations);
        assertNull(user.audiences);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create the original object with nested data
        IdEmail email = new IdEmail();
        email.email = "test@example.com";

        IdUserVendor vendor = new IdUserVendor();
        IdGgl ggl = new IdGgl();
        ggl.GAID = "test-gaid";
        vendor.GGL = ggl;

        IdUser originalUser = new IdUser();
        originalUser.SUID = "suid-123";
        originalUser.emails = List.of(email);
        originalUser.vendors = vendor;

        // 2. Convert to JSON
        JSONObject jsonObject = originalUser.toJson();
        assertNotNull(jsonObject);
        assertEquals("suid-123", jsonObject.getString("SUID"));
        assertEquals("test@example.com", jsonObject.getJSONArray("emails").getJSONObject(0).getString("email"));
        assertEquals("test-gaid", jsonObject.getJSONObject("vendors").getJSONObject("GGL").getString("GAID"));

        // 3. Convert back to object
        IdUser restoredUser = new IdUser(jsonObject);

        // 4. Assert deep equality
        assertEquals(originalUser.SUID, restoredUser.SUID);
        assertNotNull(restoredUser.emails);
        assertEquals(1, restoredUser.emails.size());
        assertEquals(originalUser.emails.get(0).email, restoredUser.emails.get(0).email);
        assertNotNull(restoredUser.vendors);
        assertNotNull(restoredUser.vendors.GGL);
        assertEquals(originalUser.vendors.GGL.GAID, restoredUser.vendors.GGL.GAID);
    }
}