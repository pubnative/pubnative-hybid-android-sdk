// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class IdPrivacyTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        IdPrivacy privacy = new IdPrivacy();

        assertNull(privacy.lat);
        assertNull(privacy.tcfv1);
        assertNull(privacy.tcfv2);
        assertNull(privacy.iab_ccpa);
    }

    @Test
    public void directFieldAccess_setsAndGetsValues() {
        IdPrivacy privacy = new IdPrivacy();
        privacy.lat = true;
        privacy.iab_ccpa = "1YNY";

        assertEquals(Boolean.TRUE, privacy.lat);
        assertEquals("1YNY", privacy.iab_ccpa);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create and populate the original object
        IdPrivacy originalPrivacy = new IdPrivacy();
        originalPrivacy.lat = false;
        originalPrivacy.tcfv2 = "test-tcfv2-string";
        originalPrivacy.iab_ccpa = "1YNY";

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalPrivacy.toJson();
        assertNotNull(jsonObject);
        assertFalse(jsonObject.getBoolean("lat"));
        assertEquals("1YNY", jsonObject.getString("iab_ccpa"));

        // 3. Convert the JSON back into a new object
        IdPrivacy restoredPrivacy = new IdPrivacy(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalPrivacy.lat, restoredPrivacy.lat);
        assertEquals(originalPrivacy.tcfv1, restoredPrivacy.tcfv1);
        assertEquals(originalPrivacy.tcfv2, restoredPrivacy.tcfv2);
        assertEquals(originalPrivacy.iab_ccpa, restoredPrivacy.iab_ccpa);
    }
}