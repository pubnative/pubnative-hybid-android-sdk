// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class BuyerSignalsTest {

    @Test
    public void defaultConstructor_initializesFieldToNull() {
        BuyerSignals buyerSignals = new BuyerSignals();

        assertNull(buyerSignals.igbuyer);
        assertNull(buyerSignals.getBuyerSignals());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This test ensures that an object serialized to JSON can be
        // deserialized back into an object with identical values.

        // 1. Create the original object with nested data
        BuyerSignal signal1 = new BuyerSignal();
        signal1.origin = "origin1.com";
        signal1.buyerdata = List.of("data1");

        BuyerSignal signal2 = new BuyerSignal();
        signal2.origin = "origin2.com";
        signal2.buyerdata = List.of("data2");

        BuyerSignals originalSignals = new BuyerSignals();
        originalSignals.igbuyer = List.of(signal1, signal2);

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalSignals.toJson();
        assertNotNull(jsonObject);
        assertEquals(2, jsonObject.getJSONArray("igbuyer").length());
        assertEquals("origin1.com", jsonObject.getJSONArray("igbuyer").getJSONObject(0).getString("origin"));

        // 3. Convert the JSON back into a new object
        BuyerSignals restoredSignals = new BuyerSignals(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertNotNull(restoredSignals.getBuyerSignals());
        assertEquals(2, restoredSignals.getBuyerSignals().size());

        BuyerSignal originalSignal1 = originalSignals.getBuyerSignals().get(0);
        BuyerSignal restoredSignal1 = restoredSignals.getBuyerSignals().get(0);

        assertEquals(originalSignal1.getOrigin(), restoredSignal1.getOrigin());
        assertEquals(originalSignal1.getBuyerData(), restoredSignal1.getBuyerData());

        BuyerSignal originalSignal2 = originalSignals.getBuyerSignals().get(1);
        BuyerSignal restoredSignal2 = restoredSignals.getBuyerSignals().get(1);

        assertEquals(originalSignal2.getOrigin(), restoredSignal2.getOrigin());
    }
}
