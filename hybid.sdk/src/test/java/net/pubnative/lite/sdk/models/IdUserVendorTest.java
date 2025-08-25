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
public class IdUserVendorTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        IdUserVendor userVendor = new IdUserVendor();
        assertNull(userVendor.GGL);
        assertNull(userVendor.APL);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create the original object with nested data
        IdGgl ggl = new IdGgl();
        ggl.GAID = "test-gaid";

        IdApl apl = new IdApl();
        apl.IDFA = "test-idfa";

        IdUserVendor originalVendor = new IdUserVendor();
        originalVendor.GGL = ggl;
        originalVendor.APL = apl;

        // 2. Convert to JSON
        JSONObject jsonObject = originalVendor.toJson();
        assertNotNull(jsonObject);
        assertEquals("test-gaid", jsonObject.getJSONObject("GGL").getString("GAID"));
        assertEquals("test-idfa", jsonObject.getJSONObject("APL").getString("IDFA"));

        // 3. Convert back to object
        IdUserVendor restoredVendor = new IdUserVendor(jsonObject);

        // 4. Assert deep equality
        assertNotNull(restoredVendor.GGL);
        assertEquals(originalVendor.GGL.GAID, restoredVendor.GGL.GAID);
        assertNotNull(restoredVendor.APL);
        assertEquals(originalVendor.APL.IDFA, restoredVendor.APL.IDFA);
    }
}
