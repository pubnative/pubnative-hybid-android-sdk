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
public class PmpTest {

    private Pmp pmp;

    @Before
    public void setUp() {
        pmp = new Pmp();
    }

    @Test
    public void testDefaultConstructor_initializesFieldToDefaultValue() {
        assertEquals(Integer.valueOf(0), pmp.getPrivateAuction());
        assertNull(pmp.getDeals());
    }

    @Test
    public void testSettersAndGetters() {
        Integer privateAuction = 1;
        Deal deal = new Deal();
        deal.setId("deal-123");
        List<Deal> deals = Arrays.asList(deal);

        pmp.setPrivateAuction(privateAuction);
        pmp.setDeals(deals);

        assertEquals(privateAuction, pmp.getPrivateAuction());
        assertEquals(deals, pmp.getDeals());
        assertEquals("deal-123", pmp.getDeals().get(0).getId());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create the original object with nested data
        Deal deal1 = new Deal();
        deal1.setId("deal-abc");
        deal1.setBidFloor(2.5f);

        Deal deal2 = new Deal();
        deal2.setId("deal-xyz");
        deal2.setBidFloor(3.0f);

        Pmp originalPmp = new Pmp();
        originalPmp.setPrivateAuction(1);
        originalPmp.setDeals(Arrays.asList(deal1, deal2));

        // 2. Convert to JSON
        JSONObject jsonObject = originalPmp.toJson();
        assertNotNull(jsonObject);
        assertEquals(1, jsonObject.getInt("private_auction"));
        assertEquals("deal-xyz", jsonObject.getJSONArray("deals").getJSONObject(1).getString("id"));

        // 3. Convert back to an object
        Pmp restoredPmp = new Pmp(jsonObject);

        // 4. Assert that the objects are identical
        assertEquals(originalPmp.getPrivateAuction(), restoredPmp.getPrivateAuction());
        assertNotNull(restoredPmp.getDeals());
        assertEquals(2, restoredPmp.getDeals().size());

        assertEquals(originalPmp.getDeals().get(0).getId(), restoredPmp.getDeals().get(0).getId());
        assertEquals(originalPmp.getDeals().get(1).getBidFloor(), restoredPmp.getDeals().get(1).getBidFloor());
    }
}
