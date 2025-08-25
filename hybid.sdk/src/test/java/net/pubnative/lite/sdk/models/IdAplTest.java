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
public class IdAplTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        IdApl idApl = new IdApl();

        assertNull(idApl.IDFA);
        assertNull(idApl.IDFV);
    }

    @Test
    public void directFieldAccess_setsAndGetsValues() {
        IdApl idApl = new IdApl();
        String testIdfa = "test-idfa-123";
        String testIdfv = "test-idfv-456";

        idApl.IDFA = testIdfa;
        idApl.IDFV = testIdfv;

        assertEquals(testIdfa, idApl.IDFA);
        assertEquals(testIdfv, idApl.IDFV);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This test ensures that an object serialized to JSON can be
        // deserialized back into an object with identical values.

        // 1. Create and populate the original object
        IdApl originalIdApl = new IdApl();
        originalIdApl.IDFA = "original-idfa";
        originalIdApl.IDFV = "original-idfv";

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalIdApl.toJson();
        assertNotNull(jsonObject);
        assertEquals("original-idfa", jsonObject.getString("IDFA"));

        // 3. Convert the JSON back into a new object
        IdApl restoredIdApl = new IdApl(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalIdApl.IDFA, restoredIdApl.IDFA);
        assertEquals(originalIdApl.IDFV, restoredIdApl.IDFV);
    }
}
