package net.pubnative.lite.sdk.tracking;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.pubnative.lite.sdk.tracking.PNLiteCrashTrackerTestUtils.getSharedPrefs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by erosgarciaponte on 13.02.18.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ClientTest {
    private static final String USER_ID = "123456";
    private static final String USER_EMAIL = "mr.test@email.com";
    private static final String USER_NAME = "Mr Test";

    private Context context;
    private Configuration config;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getContext();
        clearSharedPrefs();
        config = new Configuration("api-key");
    }

    @After
    public void tearDown() throws Exception {
        clearSharedPrefs();
    }

    private void clearSharedPrefs() {
        // Make sure no user is stored
        SharedPreferences sharedPref = getSharedPrefs(context);
        sharedPref.edit()
                .remove("user.id")
                .remove("user.email")
                .remove("user.name")
                .commit();
    }

    @Test(expected = NullPointerException.class)
    public void testNullContext() {
        new Client(null, "api-key");
    }

    @Test
    public void testNotify() {
        // Notify should not crash
        Client client = PNLiteCrashTrackerTestUtils.generateClient();
        client.notify(new RuntimeException("Testing"));
    }

    @Test
    public void testConfig() {
        config.setEndpoint("new-endpoint");

        Client client = new Client(context, config);
        client.setErrorReportApiClient(PNLiteCrashTrackerTestUtils.generateErrorReportApiClient());
        client.setSessionTrackingApiClient(PNLiteCrashTrackerTestUtils.generateSessionTrackingApiClient());

        // Notify should not crash
        client.notify(new RuntimeException("Testing"));
    }

    @Test
    public void testStoreUserInPrefs() {
        config.setPersistUserBetweenSessions(true);
        Client client = new Client(context, config);
        client.setUser(USER_ID, USER_EMAIL, USER_NAME);

        // Check that the user was store in prefs
        SharedPreferences sharedPref = getSharedPrefs(context);
        assertEquals(USER_ID, sharedPref.getString("user.id", USER_ID));
        assertEquals(USER_EMAIL, sharedPref.getString("user.email", USER_EMAIL));
        assertEquals(USER_NAME, sharedPref.getString("user.name", USER_NAME));
    }

    @Test
    public void testStoreUserInPrefsDisabled() {
        config.setPersistUserBetweenSessions(false);
        Client client = new Client(context, config);
        client.setUser(USER_ID, USER_EMAIL, USER_NAME);

        // Check that the user was not stored in prefs
        SharedPreferences sharedPref = getSharedPrefs(context);
        assertFalse(sharedPref.contains("user.id"));
        assertFalse(sharedPref.contains("user.email"));
        assertFalse(sharedPref.contains("user.name"));
    }

    @Test
    public void testEmptyManifestConfig() {
        Bundle data = new Bundle();
        Configuration newConfig = Client.populateConfigFromManifest(new Configuration("api-key"), data);

        assertEquals(config.getApiKey(), newConfig.getApiKey());
        assertEquals(config.getBuildUUID(), newConfig.getBuildUUID());
        assertEquals(config.getAppVersion(), newConfig.getAppVersion());
        assertEquals(config.getReleaseStage(), newConfig.getReleaseStage());
        assertEquals(config.getEndpoint(), newConfig.getEndpoint());
        assertEquals(config.getSessionEndpoint(), newConfig.getSessionEndpoint());
        assertEquals(config.getSendThreads(), newConfig.getSendThreads());
        assertEquals(config.getEnableExceptionHandler(), newConfig.getEnableExceptionHandler());
        assertEquals(config.getPersistUserBetweenSessions(), newConfig.getPersistUserBetweenSessions());
    }

    @Test
    public void testFullManifestConfig() {
        String buildUuid = "123";
        String appVersion = "v1.0";
        String releaseStage = "debug";
        String endpoint = "http://example.com";
        String sessionEndpoint = "http://session-example.com";

        Bundle data = new Bundle();
        data.putString("net.pubnative.lite.tracking.BUILD_UUID", buildUuid);
        data.putString("net.pubnative.lite.tracking.APP_VERSION", appVersion);
        data.putString("net.pubnative.lite.tracking.RELEASE_STAGE", releaseStage);
        data.putString("net.pubnative.lite.tracking.SESSIONS_ENDPOINT", sessionEndpoint);
        data.putString("net.pubnative.lite.tracking.ENDPOINT", endpoint);
        data.putBoolean("net.pubnative.lite.tracking.SEND_THREADS", false);
        data.putBoolean("net.pubnative.lite.tracking.ENABLE_EXCEPTION_HANDLER", false);
        data.putBoolean("net.pubnative.lite.tracking.PERSIST_USER_BETWEEN_SESSIONS", true);
        data.putBoolean("net.pubnative.lite.tracking.AUTO_CAPTURE_SESSIONS", true);

        Configuration newConfig = Client.populateConfigFromManifest(new Configuration("api-key"), data);
        assertEquals(buildUuid, newConfig.getBuildUUID());
        assertEquals(appVersion, newConfig.getAppVersion());
        assertEquals(releaseStage, newConfig.getReleaseStage());
        assertEquals(endpoint, newConfig.getEndpoint());
        assertEquals(sessionEndpoint, newConfig.getSessionEndpoint());
        assertEquals(false, newConfig.getSendThreads());
        assertEquals(false, newConfig.getEnableExceptionHandler());
        assertEquals(true, newConfig.getPersistUserBetweenSessions());
        assertEquals(true, newConfig.shouldAutoCaptureSessions());
    }
}
