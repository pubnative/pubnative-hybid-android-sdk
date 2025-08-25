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
public class IdOsTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        IdOs os = new IdOs();
        assertNull(os.name);
        assertNull(os.version);
        assertNull(os.build_signature);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        IdOs originalOs = new IdOs();
        originalOs.name = "Android";
        originalOs.version = "12.0";
        originalOs.build_signature = "ABC.123";

        JSONObject jsonObject = originalOs.toJson();
        assertNotNull(jsonObject);
        assertEquals("Android", jsonObject.getString("name"));

        IdOs restoredOs = new IdOs(jsonObject);

        assertEquals(originalOs.name, restoredOs.name);
        assertEquals(originalOs.version, restoredOs.version);
        assertEquals(originalOs.build_signature, restoredOs.build_signature);
    }
}
