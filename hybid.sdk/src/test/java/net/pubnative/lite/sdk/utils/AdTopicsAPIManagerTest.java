// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.content.Context;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.prefs.HyBidPreferences;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class AdTopicsAPIManagerTest {
    @Test
    public void testSetTopicsAPIEnabled_ContextNull() {
        Ad ad = mock(Ad.class);
        // Should do nothing, no exception
        AdTopicsAPIManager.setTopicsAPIEnabled(null, ad);
    }

    @Test
    public void testSetTopicsAPIEnabled_AdNull() {
        Context context = mock(Context.class);
        // Should do nothing, no exception
        AdTopicsAPIManager.setTopicsAPIEnabled(context, null);
    }

    @Test
    public void testSetTopicsAPIEnabled_AdIsEnabledNull() {
        Context context = mock(Context.class);
        Ad ad = mock(Ad.class);
        when(ad.isTopicsAPIEnabled()).thenReturn(null);
        AdTopicsAPIManager.setTopicsAPIEnabled(context, ad);
        // Should do nothing, no exception
    }

    @Test
    public void testSetTopicsAPIEnabled_AdIsEnabledEqualsHyBid() {
        Context context = mock(Context.class);
        Ad ad = mock(Ad.class);
        when(ad.isTopicsAPIEnabled()).thenReturn(true);
        try (MockedStatic<HyBid> hyBidMock = Mockito.mockStatic(HyBid.class)) {
            hyBidMock.when(HyBid::isTopicsApiEnabled).thenReturn(true);
            AdTopicsAPIManager.setTopicsAPIEnabled(context, ad);
            hyBidMock.verify(HyBid::isTopicsApiEnabled);
            hyBidMock.verifyNoMoreInteractions();
        }
    }

    @Test
    public void testSetTopicsAPIEnabled_AdIsEnabledNotEqualsHyBid() {
        Context context = mock(Context.class);
        Ad ad = mock(Ad.class);
        when(ad.isTopicsAPIEnabled()).thenReturn(false);
        try (MockedStatic<HyBid> hyBidMock = Mockito.mockStatic(HyBid.class)) {
            hyBidMock.when(HyBid::isTopicsApiEnabled).thenReturn(true);
            try (MockedConstruction<HyBidPreferences> prefsMock = Mockito.mockConstruction(HyBidPreferences.class,
                    (mock, contextArgs) -> {
                        doNothing().when(mock).setTopicsAPIEnabled(false);
                    })) {
                AdTopicsAPIManager.setTopicsAPIEnabled(context, ad);
                // Should call setTopicsAPIEnabled on HyBidPreferences and HyBid
                verify(prefsMock.constructed().get(0)).setTopicsAPIEnabled(false);
                hyBidMock.verify(() -> HyBid.setTopicsApiEnabled(false));
            }
        }
    }

    @Test
    public void testIsTopicsAPIEnabled_ContextNull() {
        assertNull(AdTopicsAPIManager.isTopicsAPIEnabled(null));
    }

    @Test
    public void testIsTopicsAPIEnabled_ContextNotNull() {
        Context context = mock(Context.class);
        try (MockedConstruction<HyBidPreferences> prefsMock = Mockito.mockConstruction(HyBidPreferences.class,
                (mock, contextArgs) -> when(mock.isTopicsAPIEnabled()).thenReturn(true))) {
            Boolean result = AdTopicsAPIManager.isTopicsAPIEnabled(context);
            assertTrue(result);
            verify(prefsMock.constructed().get(0)).isTopicsAPIEnabled();
        }
    }

    @Test
    public void testIsTopicsAPIEnabled_ContextNotNull_ReturnsNull() {
        Context context = mock(Context.class);
        try (MockedConstruction<HyBidPreferences> prefsMock = Mockito.mockConstruction(HyBidPreferences.class,
                (mock, contextArgs) -> when(mock.isTopicsAPIEnabled()).thenReturn(null))) {
            Boolean result = AdTopicsAPIManager.isTopicsAPIEnabled(context);
            assertNull(result);
            verify(prefsMock.constructed().get(0)).isTopicsAPIEnabled();
        }
    }
}
