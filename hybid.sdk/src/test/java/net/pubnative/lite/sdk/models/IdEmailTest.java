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
public class IdEmailTest {

    @Test
    public void defaultConstructor_initializesFieldToNull() {
        IdEmail idEmail = new IdEmail();
        assertNull(idEmail.email);
    }

    @Test
    public void directFieldAccess_setsAndGetsValue() {
        IdEmail idEmail = new IdEmail();
        String testEmail = "test@example.com";
        idEmail.email = testEmail;
        assertEquals(testEmail, idEmail.email);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create and populate the original object
        IdEmail originalIdEmail = new IdEmail();
        originalIdEmail.email = "original@example.com";

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalIdEmail.toJson();
        assertNotNull(jsonObject);
        assertEquals("original@example.com", jsonObject.getString("email"));

        // 3. Convert the JSON back into a new object
        IdEmail restoredIdEmail = new IdEmail(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalIdEmail.email, restoredIdEmail.email);
    }
}