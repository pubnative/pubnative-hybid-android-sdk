// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.List;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class BuyerSignalTest {

    private BuyerSignal buyerSignal;

    @Before
    public void setUp() {
        buyerSignal = new BuyerSignal();
    }

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        assertNull(buyerSignal.getOrigin());
        assertNull(buyerSignal.getBuyerData());
        assertNull(buyerSignal.getBuyerExperimentGroupId());
    }

    @Test
    public void getBuyerDataJson_withValidList_returnsCorrectJsonArrayString() {
        buyerSignal.buyerdata = List.of("signal1", "signal2", "{\"key\":\"value\"}");

        String expectedJsonString = "[\"signal1\",\"signal2\",\"{\\\"key\\\":\\\"value\\\"}\"]";
        assertEquals(expectedJsonString, buyerSignal.getBuyerDataJson());
    }

    @Test
    public void getBuyerDataJson_withNullOrEmptyList_returnsEmptyJsonArrayString() {
        // Test with a null list
        buyerSignal.buyerdata = null;
        assertEquals("[]", buyerSignal.getBuyerDataJson());

        // Test with an empty list
        buyerSignal.buyerdata = Collections.emptyList();
        assertEquals("[]", buyerSignal.getBuyerDataJson());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create and populate the original object
        List<String> data = List.of("data1", "data2");
        BuyerSignal originalSignal = new BuyerSignal();
        originalSignal.origin = "https://origin.com";
        originalSignal.buyer_experiment_group_id = "group-a";
        originalSignal.buyerdata = data;

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalSignal.toJson();
        assertNotNull(jsonObject);
        assertEquals("https://origin.com", jsonObject.getString("origin"));
        assertEquals("data2", jsonObject.getJSONArray("buyerdata").get(1));

        // 3. Convert the JSON back into a new object
        BuyerSignal restoredSignal = new BuyerSignal(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalSignal.getOrigin(), restoredSignal.getOrigin());
        assertEquals(originalSignal.getBuyerExperimentGroupId(), restoredSignal.getBuyerExperimentGroupId());
        assertEquals(originalSignal.getBuyerData(), restoredSignal.getBuyerData());
    }
}