package net.pubnative.lite.sdk.tracking;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static net.pubnative.lite.sdk.tracking.PNLiteCrashTrackerTestUtils.generateSessionTracker;
import static net.pubnative.lite.sdk.tracking.PNLiteCrashTrackerTestUtils.streamableToJson;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by erosgarciaponte on 13.02.18.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class AppDataTest {
    private Configuration config;

    @Before
    public void setUp() throws Exception {
        config = new Configuration("some-api-key");
    }

    @Test
    public void testManifestData() throws JSONException, IOException {
        AppData appData = new AppData(InstrumentationRegistry.getContext(), config, generateSessionTracker());
        JSONObject appDataJson = streamableToJson(appData);

        assertEquals("net.pubnative.lite.sdk.test", appDataJson.get("id"));
        assertEquals("net.pubnative.lite.sdk.test", appDataJson.get("packageName"));
        assertEquals("PNLite Android Tests", appDataJson.get("name"));
        assertEquals(1, appDataJson.get("versionCode"));
        assertEquals("1.0", appDataJson.get("versionName"));
        assertEquals("1.0", appDataJson.get("version"));
        assertEquals("development", appDataJson.get("releaseStage"));

        assertTrue(appDataJson.getLong("memoryUsage") > 0);
        assertNotNull(appDataJson.getBoolean("lowMemory"));
        assertTrue(appDataJson.getLong("duration") >= 0);
    }

    @Test
    public void testAppVersionOverride() throws JSONException, IOException {
        String appVersion = "1.2.3";
        config.setAppVersion(appVersion);

        AppData appData = new AppData(InstrumentationRegistry.getContext(), config, generateSessionTracker());
        JSONObject appDataJson = streamableToJson(appData);

        assertEquals(appVersion, appDataJson.get("version"));
    }

    @Test
    public void testReleaseStageOverride() throws JSONException, IOException {
        String releaseStage = "test-stage";
        config.setReleaseStage(releaseStage);

        AppData appData = new AppData(InstrumentationRegistry.getContext(), config, generateSessionTracker());
        JSONObject appDataJson = streamableToJson(appData);

        assertEquals(releaseStage, appDataJson.get("releaseStage"));
    }
}
