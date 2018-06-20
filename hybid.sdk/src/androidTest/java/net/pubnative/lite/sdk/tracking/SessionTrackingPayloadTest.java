package net.pubnative.lite.sdk.tracking;

import android.support.test.InstrumentationRegistry;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static net.pubnative.lite.sdk.tracking.PNLiteCrashTrackerTestUtils.generateSession;
import static net.pubnative.lite.sdk.tracking.PNLiteCrashTrackerTestUtils.generateSessionTracker;
import static net.pubnative.lite.sdk.tracking.PNLiteCrashTrackerTestUtils.streamableToJson;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by erosgarciaponte on 13.02.18.
 */
public class SessionTrackingPayloadTest {
    private JSONObject rootNode;
    private Session session;
    private AppData appData;

    private SessionStore sessionStore;
    private File storageDir;

    @Before
    public void setUp() throws Exception {
        Client client = new Client(InstrumentationRegistry.getContext(), "api-key");
        sessionStore = client.sessionStore;
        Assert.assertNotNull(sessionStore.storeDirectory);
        storageDir = new File(sessionStore.storeDirectory);
        FileUtils.clearFilesInDir(storageDir);

        session = generateSession();
        appData = new AppData(InstrumentationRegistry.getContext(), new Configuration("a"), generateSessionTracker());
        SessionTrackingPayload payload = new SessionTrackingPayload(session, appData);
        rootNode = streamableToJson(payload);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.clearFilesInDir(storageDir);
    }

    @Test
    public void testPayloadSerialisation() throws Exception {
        assertNotNull(rootNode);
        JSONArray sessions = rootNode.getJSONArray("sessions");

        JSONObject sessionNode = sessions.getJSONObject(0);
        assertNotNull(sessionNode);
        assertEquals("test", sessionNode.getString("id"));
        assertEquals(DateUtils.toIso8601(session.getStartedAt()), sessionNode.getString("startedAt"));
        assertNotNull(sessionNode.getJSONObject("user"));

        assertNotNull(rootNode.getJSONObject("notifier"));
        assertNotNull(rootNode.getJSONObject("device"));
        assertNotNull(rootNode.getJSONObject("app"));
    }

    /**
     * Serialises sessions from a file instead
     */
    @Test
    public void testMultipleSessionFiles() throws Exception {
        sessionStore.write(session);
        sessionStore.write(generateSession());
        List<File> storedFiles = sessionStore.findStoredFiles();

        SessionTrackingPayload payload = new SessionTrackingPayload(storedFiles, appData);
        rootNode = streamableToJson(payload);

        assertNotNull(rootNode);
        JSONArray sessions = rootNode.getJSONArray("sessions");
        assertNotNull(sessions);
        assertEquals(2, sessions.length());
    }
}
