// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class OpenRTBResponseTest {

    @Test
    public void defaultConstructor_initializesFieldsToDefaults() {
        OpenRTBResponse response = new OpenRTBResponse();

        assertEquals("USD", response.getCurrency());
        assertNull(response.getId());
        assertNull(response.getSeatBids());
    }

    @Test
    public void jsonConstructor_withValidJson_populatesAllFields() throws Exception {
        JSONObject bidJson = new JSONObject();
        bidJson.put("id", "bid-123");
        bidJson.put("price", 1.5f);

        JSONObject seatBidJson = new JSONObject();
        seatBidJson.put("seat", "seat-abc");
        seatBidJson.put("bid", new org.json.JSONArray(List.of(bidJson)));

        JSONObject responseJson = new JSONObject();
        responseJson.put("id", "response-id");
        responseJson.put("bidid", "global-bid-id");
        responseJson.put("cur", "EUR"); // Override default
        responseJson.put("seatbid", new org.json.JSONArray(List.of(seatBidJson)));

        OpenRTBResponse response = new OpenRTBResponse(responseJson);

        assertEquals("response-id", response.getId());
        assertEquals("global-bid-id", response.getBidId());
        assertEquals("EUR", response.getCurrency());
        assertNotNull(response.getSeatBids());
        assertEquals(1, response.getSeatBids().size());

        SeatBid seatBid = response.getSeatBids().get(0);
        assertEquals("seat-abc", seatBid.getSeat());
        assertNotNull(seatBid.getBids());
        assertEquals(1, seatBid.getBids().size());

        Bid bid = seatBid.getBids().get(0);
        assertEquals("bid-123", bid.getId());
        assertEquals(Float.valueOf(1.5f), bid.getPrice());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create the source JSON and the original object
        JSONObject bidJson = new JSONObject();
        bidJson.put("id", "bid-123");

        JSONObject seatBidJson = new JSONObject();
        seatBidJson.put("seat", "seat-abc");
        seatBidJson.put("bid", new org.json.JSONArray(List.of(bidJson)));

        JSONObject originalJson = new JSONObject();
        originalJson.put("id", "response-xyz");
        originalJson.put("seatbid", new org.json.JSONArray(List.of(seatBidJson)));

        OpenRTBResponse originalResponse = new OpenRTBResponse(originalJson);

        // 2. Convert the object back to JSON
        JSONObject restoredJson = originalResponse.toJson();

        // 3. Convert the restored JSON back into a new object
        OpenRTBResponse restoredResponse = new OpenRTBResponse(restoredJson);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalResponse.getId(), restoredResponse.getId());
        assertEquals(originalResponse.getCurrency(), restoredResponse.getCurrency()); // Should be default "USD"

        assertNotNull(restoredResponse.getSeatBids());
        assertEquals(1, restoredResponse.getSeatBids().size());
        assertEquals(originalResponse.getSeatBids().get(0).getSeat(), restoredResponse.getSeatBids().get(0).getSeat());
        assertEquals(1, restoredResponse.getSeatBids().get(0).getBids().size());
        assertEquals(originalResponse.getSeatBids().get(0).getBids().get(0).getId(), restoredResponse.getSeatBids().get(0).getBids().get(0).getId());
    }
}
