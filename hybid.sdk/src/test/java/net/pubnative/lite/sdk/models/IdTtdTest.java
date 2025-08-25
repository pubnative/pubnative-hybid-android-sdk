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
public class IdTtdTest {

    @Test
    public void defaultConstructor_initializesFieldToNull() {
        IdTtd idTtd = new IdTtd();
        assertNull(idTtd.IDL);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        IdTtd original = new IdTtd();
        original.IDL = "test-idl-value";

        JSONObject jsonObject = original.toJson();
        assertNotNull(jsonObject);
        assertEquals("test-idl-value", jsonObject.getString("IDL"));

        IdTtd restored = new IdTtd(jsonObject);
        assertEquals(original.IDL, restored.IDL);
    }
}
