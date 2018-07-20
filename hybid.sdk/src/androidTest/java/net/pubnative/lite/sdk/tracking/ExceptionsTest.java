package net.pubnative.lite.sdk.tracking;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static net.pubnative.lite.sdk.tracking.HyBidCrashTrackerTestUtils.streamableToJsonArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by erosgarciaponte on 13.02.18.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ExceptionsTest {
    private Configuration config;

    @Before
    public void setUp() throws Exception {
        config = new Configuration("api-key");
    }

    @Test
    public void testBasicException() throws JSONException, IOException {
        Exceptions exceptions = new Exceptions(config, new RuntimeException("oops"));
        JSONArray exceptionsJson = streamableToJsonArray(exceptions);

        assertEquals(1, exceptionsJson.length());

        JSONObject firstException = (JSONObject) exceptionsJson.get(0);
        assertEquals("java.lang.RuntimeException", firstException.get("errorClass"));
        assertEquals("oops", firstException.get("message"));
        assertNotNull(firstException.get("stacktrace"));
    }

    @Test
    public void testCauseException() throws JSONException, IOException {
        Throwable ex = new RuntimeException("oops", new Exception("cause"));
        Exceptions exceptions = new Exceptions(config, ex);
        JSONArray exceptionsJson = streamableToJsonArray(exceptions);

        assertEquals(2, exceptionsJson.length());

        JSONObject firstException = (JSONObject) exceptionsJson.get(0);
        assertEquals("java.lang.RuntimeException", firstException.get("errorClass"));
        assertEquals("oops", firstException.get("message"));
        assertNotNull(firstException.get("stacktrace"));

        JSONObject causeException = (JSONObject) exceptionsJson.get(1);
        assertEquals("java.lang.Exception", causeException.get("errorClass"));
        assertEquals("cause", causeException.get("message"));
        assertNotNull(causeException.get("stacktrace"));
    }

    @Test
    public void testNamedException() throws JSONException, IOException {
        StackTraceElement element = new StackTraceElement("Class", "method", "Class.java", 123);
        StackTraceElement[] frames = new StackTraceElement[]{element};
        Error error = new Error.Builder(config, "RuntimeException", "Example message", frames, null).build();
        Exceptions exceptions = new Exceptions(config, error.getException());

        JSONObject exceptionJson = streamableToJsonArray(exceptions).getJSONObject(0);
        assertEquals("RuntimeException", exceptionJson.get("errorClass"));
        assertEquals("Example message", exceptionJson.get("message"));

        JSONObject stackframeJson = exceptionJson.getJSONArray("stacktrace").getJSONObject(0);
        assertEquals("Class.method", stackframeJson.get("method"));
        assertEquals("Class.java", stackframeJson.get("file"));
        assertEquals(123, stackframeJson.get("lineNumber"));
    }

    @Test
    public void testCustomExceptionSerialization() throws JSONException, IOException {
        Exceptions exceptions = new Exceptions(config, new CustomException("Failed serialization"));

        JSONObject exceptionJson = streamableToJsonArray(exceptions).getJSONObject(0);
        assertEquals("CustomizedException", exceptionJson.get("errorClass"));
        assertEquals("Failed serialization", exceptionJson.get("message"));

        JSONObject stackframeJson = exceptionJson.getJSONArray("stacktrace").getJSONObject(0);
        assertEquals("MyFile.run", stackframeJson.get("method"));
        assertEquals("MyFile.java", stackframeJson.get("file"));
        assertEquals(408, stackframeJson.get("lineNumber"));
        assertEquals(18, stackframeJson.get("offset"));
    }
}
