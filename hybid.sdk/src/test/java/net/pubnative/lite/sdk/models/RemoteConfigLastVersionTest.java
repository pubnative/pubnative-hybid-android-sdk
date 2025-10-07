// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class RemoteConfigLastVersionTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        RemoteConfigLastVersion lastVersion = new RemoteConfigLastVersion();

        assertNull(lastVersion.publish_date);
        assertNull(lastVersion.version_no);
        assertNull(lastVersion.input_size);
    }

    @Test
    public void directFieldAccess_setsAndGetsValues() {
        RemoteConfigLastVersion lastVersion = new RemoteConfigLastVersion();
        String publishDate = "2025-08-13";
        String versionNo = "1.2.3";
        Integer inputSize = 1024;

        lastVersion.publish_date = publishDate;
        lastVersion.version_no = versionNo;
        lastVersion.input_size = inputSize;

        assertEquals(publishDate, lastVersion.publish_date);
        assertEquals(versionNo, lastVersion.version_no);
        assertEquals(inputSize, lastVersion.input_size);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create and populate the original object
        RemoteConfigLastVersion originalVersion = new RemoteConfigLastVersion();
        originalVersion.publish_date = "2025-08-13T13:50:00Z";
        originalVersion.version_no = "2.0.0";
        originalVersion.input_size = 2048;

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalVersion.toJson();
        assertNotNull(jsonObject);
        assertEquals("2.0.0", jsonObject.getString("version_no"));
        assertEquals(2048, jsonObject.getInt("input_size"));

        // 3. Convert the JSON back into a new object
        RemoteConfigLastVersion restoredVersion = new RemoteConfigLastVersion(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalVersion.publish_date, restoredVersion.publish_date);
        assertEquals(originalVersion.version_no, restoredVersion.version_no);
        assertEquals(originalVersion.input_size, restoredVersion.input_size);
    }
}