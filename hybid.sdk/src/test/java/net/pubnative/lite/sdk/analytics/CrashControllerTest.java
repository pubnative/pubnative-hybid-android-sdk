package net.pubnative.lite.sdk.analytics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class CrashControllerTest {

    private CrashController crashController;

    @Before
    public void setUp() {
        crashController = new CrashController();
    }

    // --- Tests for formatException(Exception) ---

    @Test
    public void formatException_withFullException_populatesAllFields() {
        Exception testException = createTestException("Test Message");

        ReportingEvent event = crashController.formatException(testException);

        assertNotNull(event);
        assertEquals(Reporting.EventType.ERROR, event.getEventType());
        assertEquals(Reporting.Platform.ANDROID, event.getPlatform());
        assertEquals("Test Message", event.getErrorMessage());
        assertEquals(testException.getLocalizedMessage(), event.getCustomString("LocalizedMessage"));
        assertTrue(event.getCustomString("Stacktrace").contains("createTestException"));
    }

    @Test
    public void formatException_withExceptionContainingNulls_handlesGracefully() {
        // Create an exception with null for its message and stack trace
        Exception testException = new Exception() {
            @Override
            public String getMessage() {
                return null;
            }

            @Override
            public StackTraceElement[] getStackTrace() {
                return null;
            }
        };

        ReportingEvent event = crashController.formatException(testException);

        assertNotNull(event);
        assertEquals(Reporting.EventType.ERROR, event.getEventType());
        assertEquals(Reporting.Platform.ANDROID, event.getPlatform());
        assertNull(event.getErrorMessage());
        assertNull(event.getCustomString("Stacktrace"));
    }

    @Test
    public void formatException_withNullException_returnsEventWithDefaults() {
        ReportingEvent event = crashController.formatException((Exception) null);

        assertNotNull(event);
        assertEquals(Reporting.EventType.ERROR, event.getEventType());
        assertEquals(Reporting.Platform.ANDROID, event.getPlatform());
        assertNull(event.getErrorMessage());
    }

    // --- Tests for formatException(Throwable) ---

    @Test
    public void formatException_withFullThrowable_populatesAllFields() {
        // Use an Error, which is a subclass of Throwable but not Exception
        Error testThrowable = new Error("Test Throwable Message");

        ReportingEvent event = crashController.formatException(testThrowable);

        assertNotNull(event);
        assertEquals(Reporting.EventType.ERROR, event.getEventType());
        assertEquals(Reporting.Platform.ANDROID, event.getPlatform());
        assertEquals("Test Throwable Message", event.getErrorMessage());
        assertEquals(testThrowable.getLocalizedMessage(), event.getCustomString("LocalizedMessage"));
        assertEquals(Arrays.toString(testThrowable.getStackTrace()), event.getCustomString("Stacktrace"));
    }

    @Test
    public void formatException_withNullThrowable_returnsEventWithDefaults() {
        ReportingEvent event = crashController.formatException((Throwable) null);

        assertNotNull(event);
        assertEquals(Reporting.EventType.ERROR, event.getEventType());
        assertEquals(Reporting.Platform.ANDROID, event.getPlatform());
        assertNull(event.getErrorMessage());
    }

    /**
     * Helper method to create an exception with a real stack trace.
     */
    private Exception createTestException(String message) {
        return new Exception(message);
    }
}
