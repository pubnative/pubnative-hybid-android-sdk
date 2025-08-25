// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class DataExtensionTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        DataExtension ext = new DataExtension();
        assertNotNull(ext); // Verifies the constructor ran without errors
        assertNull(ext.segtax);
        assertNull(ext.segclass);
    }

    @Test
    public void constructor_withParameters_assignsFieldsCorrectly() {
        Long segtax = 404L;
        String segclass = "test-class";

        DataExtension dataExtension = new DataExtension(segtax, segclass);

        assertEquals(segtax, dataExtension.segtax);
        assertEquals(segclass, dataExtension.segclass);
    }

    @Test
    public void toJson_withPopulatedObject_createsCorrectJsonObject() throws Exception {
        Long segtax = 123L;
        String segclass = "another-class";

        DataExtension dataExtension = new DataExtension(segtax, segclass);
        JSONObject jsonResult = dataExtension.toJson();

        assertNotNull(jsonResult);
        assertEquals(segtax.longValue(), jsonResult.getLong("segtax"));
        assertEquals(segclass, jsonResult.getString("segclass"));
    }
}