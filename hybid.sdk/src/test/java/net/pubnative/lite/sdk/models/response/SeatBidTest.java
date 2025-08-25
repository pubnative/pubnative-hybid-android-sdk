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
public class SeatBidTest {

    @Test
    public void defaultConstructor_initializesFieldsToDefaults() {
        SeatBid seatBid = new SeatBid();

        assertEquals(Integer.valueOf(0), seatBid.getGroup());
        assertNull(seatBid.getSeat());
        assertNull(seatBid.getBids());
    }

    @Test
    public void jsonConstructor_withValidJson_populatesAllFields() throws Exception {
        JSONObject bidJson = new JSONObject();
        bidJson.put("id", "bid-123");

        JSONObject seatBidJson = new JSONObject();
        seatBidJson.put("seat", "seat-abc");
        seatBidJson.put("group", 1); // Override default
        seatBidJson.put("bid", new org.json.JSONArray(List.of(bidJson)));

        SeatBid seatBid = new SeatBid(seatBidJson);

        assertEquals("seat-abc", seatBid.getSeat());
        assertEquals(Integer.valueOf(1), seatBid.getGroup());
        assertNotNull(seatBid.getBids());
        assertEquals(1, seatBid.getBids().size());
        assertEquals("bid-123", seatBid.getBids().get(0).getId());
    }
}
