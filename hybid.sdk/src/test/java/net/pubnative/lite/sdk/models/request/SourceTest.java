// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SourceTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        Source source = new Source();

        assertNull(source.getFinalDecision());
        assertNull(source.getTransactionId());
        assertNull(source.getPaymentIdChain());
    }

    @Test
    public void jsonConstructor_withValidJson_populatesAllFields() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fd", 1);
        jsonObject.put("tid", "transaction-123");
        jsonObject.put("pchain", "payment-chain-abc");

        Source source = new Source(jsonObject);

        assertEquals(Integer.valueOf(1), source.getFinalDecision());
        assertEquals("transaction-123", source.getTransactionId());
        assertEquals("payment-chain-abc", source.getPaymentIdChain());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This test ensures that an object created from JSON can be serialized
        // back into an identical JSON structure.

        // 1. Create the source JSON and the original object
        JSONObject originalJson = new JSONObject();
        originalJson.put("fd", 0);
        originalJson.put("tid", "transaction-xyz");
        originalJson.put("pchain", "payment-chain-xyz");

        Source originalSource = new Source(originalJson);

        // 2. Convert the object back to JSON
        JSONObject restoredJson = originalSource.toJson();
        assertNotNull(restoredJson);

        // 3. Convert the restored JSON back into a new object
        Source restoredSource = new Source(restoredJson);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalSource.getFinalDecision(), restoredSource.getFinalDecision());
        assertEquals(originalSource.getTransactionId(), restoredSource.getTransactionId());
        assertEquals(originalSource.getPaymentIdChain(), restoredSource.getPaymentIdChain());
    }
}
