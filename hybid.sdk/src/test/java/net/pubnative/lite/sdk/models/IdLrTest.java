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
public class IdLrTest {

    @Test
    public void defaultConstructor_initializesFieldToNull() {
        IdLr idLr = new IdLr();
        assertNull(idLr.IDL);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        IdLr original = new IdLr();
        original.IDL = "test-idl-value";

        JSONObject jsonObject = original.toJson();
        assertNotNull(jsonObject);
        assertEquals("test-idl-value", jsonObject.getString("IDL"));

        IdLr restored = new IdLr(jsonObject);
        assertEquals(original.IDL, restored.IDL);
    }
}
