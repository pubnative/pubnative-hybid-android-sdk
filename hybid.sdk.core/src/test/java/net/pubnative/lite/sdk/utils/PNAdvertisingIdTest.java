package net.pubnative.lite.sdk.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by erosgarciaponte on 24.01.18.
 */
@RunWith(RobolectricTestRunner.class)
public class PNAdvertisingIdTest {
    @Test
    public void request_withContextNull_pass() {
        PNAdvertisingIdClient advertisingId = new PNAdvertisingIdClient();
        PNAdvertisingIdClient.Listener listener = mock(PNAdvertisingIdClient.Listener.class);
        advertisingId.request(null, listener);

        CountDownLatch latch = new CountDownLatch(1);
        try {
            latch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        verify(listener).onPNAdvertisingIdFinish((String) isNull(), (Boolean) isNotNull());
    }
}
