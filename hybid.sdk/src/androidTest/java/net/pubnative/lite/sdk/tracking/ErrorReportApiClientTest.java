package net.pubnative.lite.sdk.tracking;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by erosgarciaponte on 13.02.18.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ErrorReportApiClientTest {
    private FakeApiClient apiClient;

    @Before
    public void setUp() throws Exception {
        apiClient = new FakeApiClient();
        HyBidCrashTracker.init(InstrumentationRegistry.getContext(), "123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApiClientNullValidation() {
        HyBidCrashTracker.setErrorReportApiClient(null);
    }

    @Test
    public void testPostReportCalled() {
        HyBidCrashTracker.setErrorReportApiClient(apiClient);

        assertNull(apiClient.report);
        Client client = HyBidCrashTracker.getClient();
        client.notifyBlocking(new Throwable());
        assertNotNull(apiClient.report);
    }

    private static class FakeApiClient implements ErrorReportApiClient {
        private Report report;

        @Override
        public void postReport(String urlString, Report report, Map<String, String> headers) throws NetworkException, BadResponseException {
            this.report = report;
        }
    }
}
