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
public class RemoteConfigMetadataTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        RemoteConfigMetadata metadata = new RemoteConfigMetadata();
        assertNull(metadata.name);
        assertNull(metadata.last_version);
    }

    @Test
    public void directFieldAccess_setsAndGetsValues() {
        RemoteConfigMetadata metadata = new RemoteConfigMetadata();
        String name = "test-name";
        RemoteConfigLastVersion lastVersion = new RemoteConfigLastVersion();
        lastVersion.version_no = "1.0.0";

        metadata.name = name;
        metadata.last_version = lastVersion;

        assertEquals(name, metadata.name);
        assertEquals(lastVersion, metadata.last_version);
        assertEquals("1.0.0", metadata.last_version.version_no);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create the original object with nested data
        RemoteConfigLastVersion lastVersion = new RemoteConfigLastVersion();
        lastVersion.version_no = "2.1.0";
        lastVersion.publish_date = "2025-08-13";

        RemoteConfigMetadata originalMetadata = new RemoteConfigMetadata();
        originalMetadata.name = "Test Metadata";
        originalMetadata.last_version = lastVersion;

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalMetadata.toJson();
        assertNotNull(jsonObject);
        assertEquals("Test Metadata", jsonObject.getString("name"));
        assertEquals("2.1.0", jsonObject.getJSONObject("last_version").getString("version_no"));

        // 3. Convert the JSON back into a new object
        RemoteConfigMetadata restoredMetadata = new RemoteConfigMetadata(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalMetadata.name, restoredMetadata.name);
        assertNotNull(restoredMetadata.last_version);
        assertEquals(originalMetadata.last_version.version_no, restoredMetadata.last_version.version_no);
        assertEquals(originalMetadata.last_version.publish_date, restoredMetadata.last_version.publish_date);
    }
}