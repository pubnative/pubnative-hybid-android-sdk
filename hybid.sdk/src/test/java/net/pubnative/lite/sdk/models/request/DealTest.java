// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class DealTest {

    private Deal deal;

    @Before
    public void setUp() {
        deal = new Deal();
    }

    @Test
    public void testDefaultConstructor_initializesFieldsToDefaultValues() {
        assertNull(deal.getId());
        assertEquals(Float.valueOf(0.0f), deal.getBidFloor());
        assertEquals("USD", deal.getBidFloorCurrency());
        assertNull(deal.getAuctionType());
        assertNull(deal.getWSeat());
        assertNull(deal.getWAdomain());
    }

    @Test
    public void testSettersAndGetters() {
        String id = "deal-123";
        Float bidFloor = 2.5f;
        String bidFloorCur = "EUR";
        Integer auctionType = 1;
        List<String> wseat = List.of("seat1");
        List<String> wadomain = List.of("advertiser.com");

        deal.setId(id);
        deal.setBidFloor(bidFloor);
        deal.setBidFloorCurrency(bidFloorCur);
        deal.setAuctionType(auctionType);
        deal.setWSeat(wseat);
        deal.setWAdomain(wadomain);

        assertEquals(id, deal.getId());
        assertEquals(bidFloor, deal.getBidFloor());
        assertEquals(bidFloorCur, deal.getBidFloorCurrency());
        assertEquals(auctionType, deal.getAuctionType());
        assertEquals(wseat, deal.getWSeat());
        assertEquals(wadomain, deal.getWAdomain());
    }

    @Test
    public void testFromJson_withMissingFields_usesDefaults() throws Exception {
        // Create a JSON object with only an ID.
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "deal-only-id");

        Deal dealFromJson = new Deal(jsonObject);

        // Verify the ID was parsed.
        assertEquals("deal-only-id", dealFromJson.getId());

        // Verify that the other fields retained their default values.
        assertEquals(Float.valueOf(0.0f), dealFromJson.getBidFloor());
        assertEquals("USD", dealFromJson.getBidFloorCurrency());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create the original object
        Deal originalDeal = new Deal();
        originalDeal.setId("deal-abc");
        originalDeal.setBidFloor(5.0f);
        originalDeal.setBidFloorCurrency("GBP");
        originalDeal.setAuctionType(2);
        originalDeal.setWSeat(Arrays.asList("seat1", "seat2"));

        // 2. Convert to JSON
        JSONObject jsonObject = originalDeal.toJson();
        assertNotNull(jsonObject);
        assertEquals(5.0, jsonObject.getDouble("bidfloor"), 0.001);
        assertEquals("GBP", jsonObject.getString("bidfloorcur"));

        // 3. Convert back to an object
        Deal restoredDeal = new Deal(jsonObject);

        // 4. Assert that the objects are identical
        assertEquals(originalDeal.getId(), restoredDeal.getId());
        assertEquals(originalDeal.getBidFloor(), restoredDeal.getBidFloor());
        assertEquals(originalDeal.getBidFloorCurrency(), restoredDeal.getBidFloorCurrency());
        assertEquals(originalDeal.getAuctionType(), restoredDeal.getAuctionType());
        assertEquals(originalDeal.getWSeat(), restoredDeal.getWSeat());
    }
}