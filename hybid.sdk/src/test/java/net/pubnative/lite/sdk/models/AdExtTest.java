// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AdExtTest {

    @Test
    public void jsonConstructor_withValidJson_populatesMetaField() throws Exception {
        JSONObject metaJson = new JSONObject();
        metaJson.put("key_string", "value1");
        metaJson.put("key_int", 123);
        metaJson.put("key_bool", true);

        JSONObject rootJson = new JSONObject();
        rootJson.put("meta", metaJson);

        AdExt adExt = new AdExt(rootJson);

        assertNotNull(adExt.meta);
        assertEquals(3, adExt.meta.size());
        assertEquals("value1", adExt.meta.get("key_string"));
        assertEquals(123, adExt.meta.get("key_int"));
        assertEquals(Boolean.TRUE, adExt.meta.get("key_bool"));
    }

    @Test
    public void defaultConstructor_initializesMetaToNull() {
        AdExt adExt = new AdExt();
        assertNotNull(adExt);
    }
}
