// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import net.pubnative.lite.sdk.testing.TestUtil;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AdResponseTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        AdResponse response = new AdResponse();

        assertNull(response.status);
        assertNull(response.error_message);
        assertNull(response.ads);
        assertNull(response.ext);
    }

    @Test
    public void directFieldAccess_setsAndGetsValues() {
        AdResponse response = new AdResponse();
        List<Ad> adList = new ArrayList<>();
        Ad ad = new Ad();
        ad.link = "https://example.com";
        adList.add(ad);

        response.status = AdResponse.Status.OK;
        response.ads = adList;

        assertEquals(AdResponse.Status.OK, response.status);
        assertEquals(adList, response.ads);
        assertEquals("https://example.com", response.ads.get(0).link);
    }

    @Test
    public void validateAdResponse_withTestData_isConsistent() throws Exception {
        AdResponse originalResponse = TestUtil.createTestAdResponse();

        JSONObject json = originalResponse.toJson();
        AdResponse parsedResponse = new AdResponse(json);

        assertNotNull(parsedResponse);
        assertEquals("ok", parsedResponse.status);
        assertNotNull(parsedResponse.ads);
        assertEquals(1, parsedResponse.ads.size());

        Ad parsedAd = parsedResponse.ads.get(0);
        assertNotNull(parsedAd);
        assertEquals(ApiAssetGroupType.MRAID_320x50, parsedAd.assetgroupid);
        assertEquals(Integer.valueOf(9), parsedAd.getECPM());
        assertNotNull(parsedAd.getAssetHtml("htmlbanner"));
    }
}
