// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import net.pubnative.lite.sdk.models.Ad;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class AdEndCardManagerTest {
    @Test
    public void testIsEndCardEnabled_NullAd() {
        assertFalse(AdEndCardManager.isEndCardEnabled(null));
    }

    @Test
    public void testIsEndCardEnabled_ShouldShowEndcardTrue() {
        Ad ad = mock(Ad.class);
        when(ad.hasEndCard()).thenReturn(true);
        when(ad.isEndCardEnabled()).thenReturn(true);
        assertTrue(AdEndCardManager.isEndCardEnabled(ad));
    }

    @Test
    public void testIsEndCardEnabled_ShouldShowEndcardFalse_ShouldShowCustomEndcardTrue() {
        Ad ad = mock(Ad.class);
        when(ad.hasEndCard()).thenReturn(false);
        when(ad.hasCustomEndCard()).thenReturn(true);
        when(ad.isCustomEndCardEnabled()).thenReturn(true);
        assertTrue(AdEndCardManager.isEndCardEnabled(ad));
    }

    @Test
    public void testIsEndCardEnabled_BothFalse() {
        Ad ad = mock(Ad.class);
        when(ad.hasEndCard()).thenReturn(false);
        when(ad.hasCustomEndCard()).thenReturn(false);
        assertFalse(AdEndCardManager.isEndCardEnabled(ad));
    }

    @Test
    public void testShouldShowEndcard_HasEndCardFalse() {
        Ad ad = mock(Ad.class);
        when(ad.hasEndCard()).thenReturn(false);
        assertFalse(AdEndCardManager.shouldShowEndcard(ad));
    }

    @Test
    public void testShouldShowEndcard_HasEndCardTrue_HasRemoteConfigTrue_EnabledTrue() {
        Ad ad = mock(Ad.class);
        when(ad.hasEndCard()).thenReturn(true);
        when(ad.isEndCardEnabled()).thenReturn(true);
        assertTrue(AdEndCardManager.shouldShowEndcard(ad));
    }

    @Test
    public void testShouldShowEndcard_HasEndCardTrue_HasRemoteConfigTrue_EnabledFalse() {
        Ad ad = mock(Ad.class);
        when(ad.hasEndCard()).thenReturn(true);
        when(ad.isEndCardEnabled()).thenReturn(false);
        assertFalse(AdEndCardManager.shouldShowEndcard(ad));
    }

    @Test
    public void testShouldShowEndcard_HasEndCardTrue_HasRemoteConfigFalse() {
        Ad ad = mock(Ad.class);
        when(ad.hasEndCard()).thenReturn(true);
        when(ad.isEndCardEnabled()).thenReturn(null);
        assertTrue(AdEndCardManager.shouldShowEndcard(ad)); // END_CARD_ENABLED is true
    }

    @Test
    public void testShouldShowCustomEndcard_HasCustomEndCardFalse() {
        Ad ad = mock(Ad.class);
        when(ad.hasCustomEndCard()).thenReturn(false);
        assertFalse(AdEndCardManager.shouldShowCustomEndcard(ad));
    }

    @Test
    public void testShouldShowCustomEndcard_HasCustomEndCardTrue_EnabledTrue() {
        Ad ad = mock(Ad.class);
        when(ad.hasCustomEndCard()).thenReturn(true);
        when(ad.isCustomEndCardEnabled()).thenReturn(true);
        assertTrue(AdEndCardManager.shouldShowCustomEndcard(ad));
    }

    @Test
    public void testShouldShowCustomEndcard_HasCustomEndCardTrue_EnabledFalse() {
        Ad ad = mock(Ad.class);
        when(ad.hasCustomEndCard()).thenReturn(true);
        when(ad.isCustomEndCardEnabled()).thenReturn(false);
        assertFalse(AdEndCardManager.shouldShowCustomEndcard(ad));
    }

    @Test
    public void testShouldShowCustomEndcard_HasCustomEndCardTrue_EnabledNull() {
        Ad ad = mock(Ad.class);
        when(ad.hasCustomEndCard()).thenReturn(true);
        when(ad.isCustomEndCardEnabled()).thenReturn(null);
        assertFalse(AdEndCardManager.shouldShowCustomEndcard(ad)); // CUSTOM_END_CARD_ENABLED is false
    }

    @Test
    public void testGetDefaultEndCard() {
        assertTrue(AdEndCardManager.getDefaultEndCard());
    }

    @Test
    public void testHasEndcardRemoteConfig_True() {
        Ad ad = mock(Ad.class);
        when(ad.isEndCardEnabled()).thenReturn(true);
        assertTrue(invokeHasEndcardRemoteConfig(ad));
        when(ad.isEndCardEnabled()).thenReturn(false);
        assertTrue(invokeHasEndcardRemoteConfig(ad));
    }

    @Test
    public void testHasEndcardRemoteConfig_False() {
        Ad ad = mock(Ad.class);
        when(ad.isEndCardEnabled()).thenReturn(null);
        assertFalse(invokeHasEndcardRemoteConfig(ad));
    }

    // Helper to access private static method hasEndcardRemoteConfig
    private boolean invokeHasEndcardRemoteConfig(Ad ad) {
        try {
            java.lang.reflect.Method m = AdEndCardManager.class.getDeclaredMethod("hasEndcardRemoteConfig", Ad.class);
            m.setAccessible(true);
            Object result = m.invoke(null, ad);
            return result instanceof Boolean && (Boolean) result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
