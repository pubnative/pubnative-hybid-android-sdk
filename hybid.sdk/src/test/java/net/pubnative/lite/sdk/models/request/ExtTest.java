// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ExtTest {

    @Test
    public void constructor_withParameters_setsFieldsCorrectly() throws Exception {
        Integer gdpr = 1;
        String gpp = "test-gpp";
        List<Integer> gppSid = Arrays.asList(1, 2, 3);
        String usPrivacy = "1YNY";

        Ext ext = new Ext(gdpr, gpp, gppSid, usPrivacy);

        // Verify state by serializing to JSON, since there are no getters
        JSONObject jsonResult = ext.toJson();

        assertNotNull(jsonResult);
        assertEquals(gdpr.intValue(), jsonResult.getInt("gdpr"));
        assertEquals(gpp, jsonResult.getString("gpp"));
        assertEquals(3, jsonResult.getJSONArray("gpp_sid").length());
        assertEquals(2, jsonResult.getJSONArray("gpp_sid").get(1));
        assertEquals(usPrivacy, jsonResult.getString("us_privacy"));
    }

    @Test
    public void setters_updateFieldsCorrectly() throws Exception {
        Ext ext = new Ext();

        Integer gdpr = 0;
        String gpp = "another-gpp";
        List<Integer> gppSid = Arrays.asList(5, 6);
        String usPrivacy = "1NNN";

        ext.setGdpr(gdpr);
        ext.setGpp(gpp);
        ext.setGppSid(gppSid);
        ext.setUsPrivacy(usPrivacy);

        // Verify state by serializing to JSON
        JSONObject jsonResult = ext.toJson();

        assertNotNull(jsonResult);
        assertEquals(gdpr.intValue(), jsonResult.getInt("gdpr"));
        assertEquals(gpp, jsonResult.getString("gpp"));
        assertEquals(2, jsonResult.getJSONArray("gpp_sid").length());
        assertEquals(5, jsonResult.getJSONArray("gpp_sid").get(0));
        assertEquals(usPrivacy, jsonResult.getString("us_privacy"));
    }

    @Test
    public void fromJson_withValidJson_populatesFieldsCorrectly() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gdpr", 1);
        jsonObject.put("us_privacy", "1YNY");

        Ext ext = new Ext();
        ext.fromJson(jsonObject);

        // Verify by converting back to JSON and checking the values
        JSONObject jsonResult = ext.toJson();
        assertEquals(1, jsonResult.getInt("gdpr"));
        assertEquals("1YNY", jsonResult.getString("us_privacy"));
        // gpp and gpp_sid were not in the source JSON, so they should not be in the output
        assertFalse(jsonResult.has("gpp"));
        assertFalse(jsonResult.has("gpp_sid"));
    }
}