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
public class IdAppVendorTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        IdAppVendor vendor = new IdAppVendor();
        assertNull(vendor.APL);
        assertNull(vendor.LR);
        assertNull(vendor.TTD);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create original object with nested data
        IdApl apl = new IdApl();
        apl.IDFA = "test-idfa";

        IdLr lr = new IdLr();
        lr.IDL = "test-idl";

        IdTtd ttd = new IdTtd();
        ttd.IDL = "test-ttd-idl";

        IdAppVendor originalVendor = new IdAppVendor();
        originalVendor.APL = apl;
        originalVendor.LR = lr;
        originalVendor.TTD = ttd;

        // 2. Convert to JSON
        JSONObject jsonObject = originalVendor.toJson();
        assertNotNull(jsonObject);
        assertEquals("test-idfa", jsonObject.getJSONObject("APL").getString("IDFA"));
        assertEquals("test-idl", jsonObject.getJSONObject("LR").getString("IDL"));

        // 3. Convert back to object
        IdAppVendor restoredVendor = new IdAppVendor(jsonObject);

        // 4. Assert deep equality
        assertNotNull(restoredVendor.APL);
        assertEquals(originalVendor.APL.IDFA, restoredVendor.APL.IDFA);

        assertNotNull(restoredVendor.LR);
        assertEquals(originalVendor.LR.IDL, restoredVendor.LR.IDL);

        assertNotNull(restoredVendor.TTD);
        assertEquals(originalVendor.TTD.IDL, restoredVendor.TTD.IDL);
    }
}
