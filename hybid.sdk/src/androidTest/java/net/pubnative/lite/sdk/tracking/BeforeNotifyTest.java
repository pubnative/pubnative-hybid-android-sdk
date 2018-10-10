package net.pubnative.lite.sdk.tracking;

import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Created by erosgarciaponte on 13.02.18.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class BeforeNotifyTest {
    private BeforeNotify beforeNotify = new BeforeNotify() {
        @Override
        public boolean run(Error error) {
            return true;
        }
    };

    private BeforeNotify beforeNotifySkip = new BeforeNotify() {
        @Override
        public boolean run(Error error) {
            return false;
        }
    };

    private Configuration config;

    @Before
    public void setUp() throws Exception {
        config = new Configuration("api-key");
    }

    @Test
    public void testRunModifiesError() {
        final String context = "new-context";

        BeforeNotify beforeNotify = new BeforeNotify() {
            @Override
            public boolean run(Error error) {
                error.setContext(context);
                return false;
            }
        };

        Error error = new Error.Builder(config, new RuntimeException("Test"), null).build();
        beforeNotify.run(error);

        assertEquals(context, error.getContext());
    }
}
