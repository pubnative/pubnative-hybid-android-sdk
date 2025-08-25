// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class RegsTest {

    private Regs regs;

    @Before
    public void setUp() {
        regs = new Regs();
    }

    @Test
    public void testSettersAndGetters() {
        // coppa has no setter, so it should be null initially
        assertNull(regs.getCOPPA());

        Ext ext = new Ext(1, "gpp", null, "1YNY");
        regs.setExt(ext);

        assertEquals(ext, regs.getExt());
    }

    @Test
    public void jsonConstructor_withValidJson_populatesAllFields() throws Exception {
        JSONObject extJson = new JSONObject();
        extJson.put("us_privacy", "1YNY");

        JSONObject regsJson = new JSONObject();
        regsJson.put("coppa", 1);
        regsJson.put("ext", extJson);

        Regs regsFromJson = new Regs(regsJson);

        assertEquals(Integer.valueOf(1), regsFromJson.getCOPPA());
        assertNotNull(regsFromJson.getExt());
        // To verify the content of the nested Ext object, we can serialize it back to JSON
        assertEquals("1YNY", regsFromJson.getExt().toJson().getString("us_privacy"));
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create the original object by first creating its source JSON,
        // since `coppa` has no setter.
        JSONObject extJson = new JSONObject();
        extJson.put("us_privacy", "1YNY");
        extJson.put("gdpr", 1);

        JSONObject originalJson = new JSONObject();
        originalJson.put("coppa", 1);
        originalJson.put("ext", extJson);

        Regs originalRegs = new Regs(originalJson);

        // 2. Convert the object back to JSON
        JSONObject restoredJson = originalRegs.toJson();
        assertNotNull(restoredJson);
        assertEquals(1, restoredJson.getInt("coppa"));
        assertNotNull(restoredJson.getJSONObject("ext"));
        assertEquals("1YNY", restoredJson.getJSONObject("ext").getString("us_privacy"));

        // 3. Convert the restored JSON back into a new object
        Regs restoredRegs = new Regs(restoredJson);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalRegs.getCOPPA(), restoredRegs.getCOPPA());

        // Since Ext has no getters, we compare by serializing both to JSON strings
        String originalExtJsonString = originalRegs.getExt().toJson().toString();
        String restoredExtJsonString = restoredRegs.getExt().toJson().toString();
        assertEquals(originalExtJsonString, restoredExtJsonString);
    }
}
