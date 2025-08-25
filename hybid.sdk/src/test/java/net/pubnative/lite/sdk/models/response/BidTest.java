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
public class BidTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        Bid bid = new Bid();

        assertNull(bid.getId());
        assertNull(bid.getPrice());
        assertNull(bid.getExt());
    }

    @Test
    public void jsonConstructor_withValidJson_populatesAllFields() throws Exception {
        JSONObject extJson = new JSONObject();
        extJson.put("crtype", "banner");

        JSONObject bidJson = new JSONObject();
        bidJson.put("id", "bid-123");
        bidJson.put("impid", "imp-456");
        bidJson.put("price", 1.25f);
        bidJson.put("adm", "<html>...</html>");
        bidJson.put("adomain", new org.json.JSONArray(List.of("advertiser.com")));
        bidJson.put("cid", "campaign-789");
        bidJson.put("crid", "creative-abc");
        bidJson.put("w", 320);
        bidJson.put("h", 50);
        bidJson.put("ext", extJson);

        Bid bid = new Bid(bidJson);

        assertEquals("bid-123", bid.getId());
        assertEquals("imp-456", bid.getImpressionid());
        assertEquals(Float.valueOf(1.25f), bid.getPrice());
        assertEquals("<html>...</html>", bid.getAdMarkup());
        assertEquals("advertiser.com", bid.getAdvertiserDomains().get(0));
        assertEquals("campaign-789", bid.getCampaignId());
        assertEquals("creative-abc", bid.getCreativeId());
        assertEquals(Integer.valueOf(320), bid.getWidth());
        assertEquals(Integer.valueOf(50), bid.getHeight());
        assertNotNull(bid.getExt());
        assertEquals("banner", bid.getExt().getCrtype());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create the source JSON and the original object
        JSONObject extJson = new JSONObject();
        extJson.put("crtype", "video");

        JSONObject originalJson = new JSONObject();
        originalJson.put("id", "bid-xyz");
        originalJson.put("price", 2.50);
        originalJson.put("ext", extJson);

        Bid originalBid = new Bid(originalJson);

        // 2. Convert the object back to JSON
        JSONObject restoredJson = originalBid.toJson();

        // 3. Convert the restored JSON back into a new object
        Bid restoredBid = new Bid(restoredJson);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalBid.getId(), restoredBid.getId());
        assertEquals(originalBid.getPrice(), restoredBid.getPrice());
        assertNotNull(restoredBid.getExt());
        assertEquals(originalBid.getExt().getCrtype(), restoredBid.getExt().getCrtype());
    }
}
