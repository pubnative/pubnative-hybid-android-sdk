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
public class SignalDataTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        SignalData signalData = new SignalData();

        assertNull(signalData.status);
        assertNull(signalData.tagid);
        assertNull(signalData.admurl);
        assertNull(signalData.adm);
        assertNull(signalData.format);
    }

    @Test
    public void directFieldAccess_setsAndGetsValues() {
        SignalData signalData = new SignalData();
        String status = "ok";
        AdResponse adResponse = new AdResponse();
        adResponse.status = "ok";

        signalData.status = status;
        signalData.adm = adResponse;

        assertEquals(status, signalData.status);
        assertEquals(adResponse, signalData.adm);
        assertEquals("ok", signalData.adm.status);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create the original object with nested data
        Ad ad = new Ad();
        ad.assetgroupid = 1;

        AdResponse adResponse = new AdResponse();
        adResponse.status = "ok";
        adResponse.ads = List.of(ad);

        SignalData originalSignalData = new SignalData();
        originalSignalData.status = "ok";
        originalSignalData.tagid = "tag-123";
        originalSignalData.adm = adResponse;

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalSignalData.toJson();
        assertNotNull(jsonObject);
        assertEquals("ok", jsonObject.getString("status"));
        assertEquals(1, jsonObject.getJSONObject("adm").getJSONArray("ads").getJSONObject(0).getInt("assetgroupid"));

        // 3. Convert the JSON back into a new object
        SignalData restoredSignalData = new SignalData(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalSignalData.status, restoredSignalData.status);
        assertEquals(originalSignalData.tagid, restoredSignalData.tagid);

        assertNotNull(restoredSignalData.adm);
        assertEquals(originalSignalData.adm.status, restoredSignalData.adm.status);

        assertNotNull(restoredSignalData.adm.ads);
        assertEquals(1, restoredSignalData.adm.ads.size());
        assertEquals(originalSignalData.adm.ads.get(0).assetgroupid, restoredSignalData.adm.ads.get(0).assetgroupid);
    }
}