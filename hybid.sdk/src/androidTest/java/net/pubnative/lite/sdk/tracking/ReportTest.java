package net.pubnative.lite.sdk.tracking;

import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static net.pubnative.lite.sdk.tracking.HyBidCrashTrackerTestUtils.streamableToJson;
import static org.junit.Assert.assertEquals;

/**
 * Created by erosgarciaponte on 13.02.18.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ReportTest {
    private Report report;

    @Before
    public void setUp() throws Exception {
        Configuration config = new Configuration("example-api-key");
        Error error = new Error.Builder(config, new RuntimeException("Something broke"), null).build();
        report = new Report("api-key", error);
    }

    @Test
    public void testInMemoryError() throws JSONException, IOException {
        JSONObject reportJson = streamableToJson(report);
        assertEquals(1, reportJson.getJSONArray("events").length());
    }

    @Test
    public void testModifyingGroupingHash() throws JSONException, IOException {
        String groupingHash = "File.java:300429";
        report.getError().setGroupingHash(groupingHash);

        JSONObject reportJson = streamableToJson(report);
        JSONArray events = reportJson.getJSONArray("events");
        JSONObject event = events.getJSONObject(0);
        assertEquals(groupingHash, event.getString("groupingHash"));
    }
}
