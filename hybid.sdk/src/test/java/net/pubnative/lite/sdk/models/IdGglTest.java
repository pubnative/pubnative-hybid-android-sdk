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
public class IdGglTest {

    @Test
    public void defaultConstructor_initializesFieldToNull() {
        IdGgl idGgl = new IdGgl();
        assertNull(idGgl.GAID);
    }

    @Test
    public void directFieldAccess_setsAndGetsValue() {
        IdGgl idGgl = new IdGgl();
        String testGaid = "test-gaid-123";
        idGgl.GAID = testGaid;
        assertEquals(testGaid, idGgl.GAID);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create and populate the original object
        IdGgl originalIdGgl = new IdGgl();
        originalIdGgl.GAID = "original-gaid";

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalIdGgl.toJson();
        assertNotNull(jsonObject);
        assertEquals("original-gaid", jsonObject.getString("GAID"));

        // 3. Convert the JSON back into a new object
        IdGgl restoredIdGgl = new IdGgl(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalIdGgl.GAID, restoredIdGgl.GAID);
    }
}
