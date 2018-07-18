package net.pubnative.lite.sdk.tracking;

import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by erosgarciaponte on 13.02.18.
 */

public class NullMetaDataTest {
    private static final String TAB_KEY = "tab";

    private Configuration config;
    private Throwable throwable;

    @Before
    public void setUp() throws Exception {
        config = new Configuration("api-key");
        HyBidCrashTracker.init(InstrumentationRegistry.getContext(), config);
        HyBidCrashTracker.setErrorReportApiClient(new ErrorReportApiClient() {
            @Override
            public void postReport(String urlString, Report report, Map<String, String> headers) throws NetworkException, BadResponseException {

            }
        });

        throwable = new RuntimeException("Test");
    }

    @Test
    public void testErrorDefaultMetaData() throws Exception {
        Error error = new Error.Builder(config, throwable, null).build();
        validateDefaultMetadata(error.getMetaData());
    }

    @Test
    public void testSecondErrorDefaultMetaData() throws Exception {
        Error error = new Error.Builder(config, "RuntimeException", "Something broke", new StackTraceElement[]{}, null).build();
        validateDefaultMetadata(error.getMetaData());
    }

    @Test
    public void testErrorSetMetadataRef() throws Exception {
        Error error = new Error.Builder(config, throwable, null).build();
        MetaData metaData = new MetaData();
        metaData.addToTab(TAB_KEY, "test", "data");
        error.setMetaData(metaData);
        assertNotNull(metaData.getTab(TAB_KEY));
    }

    @Test
    public void testErrorSetNullMetadata() throws Exception {
        Error error = new Error.Builder(config, throwable, null).build();
        error.setMetaData(null);
        validateDefaultMetadata(error.getMetaData());
    }

    @Test
    public void testConfigDefaultMetadata() throws Exception {
        validateDefaultMetadata(config.getMetaData());
    }

    @Test
    public void testConfigSetMetadataRef() throws Exception {
        Configuration configuration = new Configuration("test");
        configuration.setMetaData(new MetaData());
        validateDefaultMetadata(configuration.getMetaData());
    }

    @Test
    public void testConfigSetNullMetadata() throws Exception {
        Configuration configuration = new Configuration("test");
        configuration.setMetaData(null);
        validateDefaultMetadata(configuration.getMetaData());
    }

    @Test
    public void testNotify() throws Exception {
        HyBidCrashTracker.beforeNotify(new BeforeNotify() {
            @Override
            public boolean run(Error error) {
                validateDefaultMetadata(error.getMetaData());
                return false;
            }
        });
        Error error = new Error.Builder(config, new Throwable(), null).build();
        Client client = HyBidCrashTracker.getClient();
        client.notify(error, DeliveryStyle.SAME_THREAD, null);
    }

    private void validateDefaultMetadata(MetaData metaData) {
        assertNotNull(metaData);
        assertEquals(0, metaData.getTab(TAB_KEY).size());

        metaData.addToTab(TAB_KEY, "test", "data");
        assertEquals(1, metaData.getTab(TAB_KEY).size());
    }
}
