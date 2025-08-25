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
public class IdAudienceTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        IdAudience audience = new IdAudience();

        assertNull(audience.id);
        assertNull(audience.type);
        assertNull(audience.ts);
    }

    @Test
    public void directFieldAccess_setsAndGetsValues() {
        IdAudience audience = new IdAudience();
        String testId = "audience-123";
        String testType = "test-type";
        String testTs = "1672531200";

        audience.id = testId;
        audience.type = testType;
        audience.ts = testTs;

        assertEquals(testId, audience.id);
        assertEquals(testType, audience.type);
        assertEquals(testTs, audience.ts);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This test ensures that an object serialized to JSON can be
        // deserialized back into an object with identical values.

        // 1. Create and populate the original object
        IdAudience originalAudience = new IdAudience();
        originalAudience.id = "original-id";
        originalAudience.type = "original-type";
        originalAudience.ts = "original-ts";

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalAudience.toJson();
        assertNotNull(jsonObject);
        assertEquals("original-id", jsonObject.getString("id"));

        // 3. Convert the JSON back into a new object
        IdAudience restoredAudience = new IdAudience(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalAudience.id, restoredAudience.id);
        assertEquals(originalAudience.type, restoredAudience.type);
        assertEquals(originalAudience.ts, restoredAudience.ts);
    }
}