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
public class RemoteConfigMLModelTest {

    @Test
    public void defaultConstructor_initializesFieldsToNull() {
        RemoteConfigMLModel mlModel = new RemoteConfigMLModel();
        assertNull(mlModel.name);
        assertNull(mlModel.min_score);
        assertNull(mlModel.last_version);
    }

    @Test
    public void directFieldAccess_setsAndGetsValues() {
        RemoteConfigMLModel mlModel = new RemoteConfigMLModel();
        String name = "test-model";
        Double minScore = 0.95;
        RemoteConfigLastVersion lastVersion = new RemoteConfigLastVersion();
        lastVersion.version_no = "3.0";

        mlModel.name = name;
        mlModel.min_score = minScore;
        mlModel.last_version = lastVersion;

        assertEquals(name, mlModel.name);
        assertEquals(minScore, mlModel.min_score);
        assertEquals(lastVersion, mlModel.last_version);
        assertEquals("3.0", mlModel.last_version.version_no);
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // 1. Create the original object with nested data
        RemoteConfigLastVersion lastVersion = new RemoteConfigLastVersion();
        lastVersion.version_no = "4.2.0";
        lastVersion.publish_date = "2025-08-13";

        RemoteConfigMLModel originalModel = new RemoteConfigMLModel();
        originalModel.name = "Test ML Model";
        originalModel.min_score = 0.88;
        originalModel.last_version = lastVersion;

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalModel.toJson();
        assertNotNull(jsonObject);
        assertEquals("Test ML Model", jsonObject.getString("name"));
        assertEquals(0.88, jsonObject.getDouble("min_score"), 0.001);
        assertEquals("4.2.0", jsonObject.getJSONObject("last_version").getString("version_no"));

        // 3. Convert the JSON back into a new object
        RemoteConfigMLModel restoredModel = new RemoteConfigMLModel(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalModel.name, restoredModel.name);
        assertEquals(originalModel.min_score, restoredModel.min_score);
        assertNotNull(restoredModel.last_version);
        assertEquals(originalModel.last_version.version_no, restoredModel.last_version.version_no);
        assertEquals(originalModel.last_version.publish_date, restoredModel.last_version.publish_date);
    }
}