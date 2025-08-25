// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.os.Looper;

import net.pubnative.lite.sdk.utils.HyBidAdvertisingId;
import net.pubnative.lite.sdk.utils.reflection.MethodBuilderFactory;
import net.pubnative.lite.sdk.utils.reflection.ReflectionUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.CountDownLatch;

@RunWith(RobolectricTestRunner.class)
public class HyBidAdvertisingIdTest {

    private Context mockContext;

    @Before
    public void setup() {
        mockContext = mock(Context.class);
        if (Looper.myLooper() == null) Looper.prepare();
    }

    @Test
    public void testExecute() throws Exception {
        Object mockAdInfo = new Object();
        CountDownLatch latch = new CountDownLatch(1);
        ReflectionUtils.MethodBuilder getAdvertisingIdInfoBuilder = mock(ReflectionUtils.MethodBuilder.class);
        when(getAdvertisingIdInfoBuilder.execute()).thenReturn(mockAdInfo);
        ReflectionUtils.MethodBuilder getIdBuilder = mock(ReflectionUtils.MethodBuilder.class);
        when(getIdBuilder.execute()).thenReturn("mock-ad-id");
        ReflectionUtils.MethodBuilder isLimitTrackingBuilder = mock(ReflectionUtils.MethodBuilder.class);
        when(isLimitTrackingBuilder.execute()).thenReturn(true);
        try (MockedStatic<MethodBuilderFactory> factoryMock = mockStatic(MethodBuilderFactory.class)) {
            factoryMock.when(() ->
                            MethodBuilderFactory.create(
                                    null, "getAdvertisingIdInfo"))
                    .thenReturn(getAdvertisingIdInfoBuilder);
            factoryMock.when(() ->
                            MethodBuilderFactory.create(eq(mockAdInfo), eq("getId")))
                    .thenReturn(getIdBuilder);
            factoryMock.when(() ->
                            MethodBuilderFactory.create(eq(mockAdInfo), eq("isLimitAdTrackingEnabled")))
                    .thenReturn(isLimitTrackingBuilder);

            HyBidAdvertisingId advertisingIdExecutor = new HyBidAdvertisingId(mockContext);

            advertisingIdExecutor.execute((adId, isLimited) -> {
                assertEquals("mock-ad-id", adId);
                assertTrue(isLimited);
                latch.countDown();
            });
        }
    }
}