// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.vpaid.enums.AudioState;
import net.pubnative.lite.sdk.HyBid;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class AdAudioStateManagerTest {
    @Test
    public void testFullscreen_AdWithValidAudioState_Muted() {
        Ad ad = mock(Ad.class);
        when(ad.getAudioState()).thenReturn("muted");
        AudioState result = AdAudioStateManager.getAudioState(ad, true);
        assertEquals(AudioState.MUTED, result);
    }

    @Test
    public void testFullscreen_AdWithValidAudioState_On() {
        Ad ad = mock(Ad.class);
        when(ad.getAudioState()).thenReturn("on");
        AudioState result = AdAudioStateManager.getAudioState(ad, true);
        assertEquals(AudioState.ON, result);
    }

    @Test
    public void testFullscreen_AdWithValidAudioState_Default() {
        Ad ad = mock(Ad.class);
        when(ad.getAudioState()).thenReturn("default");
        AudioState result = AdAudioStateManager.getAudioState(ad, true);
        assertEquals(AudioState.DEFAULT, result);
    }

    @Test
    public void testFullscreen_AdWithInvalidAudioState_FallbackToHyBid() {
        Ad ad = mock(Ad.class);
        when(ad.getAudioState()).thenReturn("invalid");
        try (var mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::getVideoAudioStatus).thenReturn(AudioState.ON);
            AudioState result = AdAudioStateManager.getAudioState(ad, true);
            assertEquals(AudioState.ON, result);
        }
    }

    @Test
    public void testFullscreen_AdWithNullAudioState_FallbackToHyBid() {
        Ad ad = mock(Ad.class);
        when(ad.getAudioState()).thenReturn(null);
        try (var mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::getVideoAudioStatus).thenReturn(AudioState.DEFAULT);
            AudioState result = AdAudioStateManager.getAudioState(ad, true);
            assertEquals(AudioState.DEFAULT, result);
        }
    }

    @Test
    public void testFullscreen_AdIsNull_FallbackToHyBid() {
        try (var mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::getVideoAudioStatus).thenReturn(AudioState.MUTED);
            AudioState result = AdAudioStateManager.getAudioState(null, true);
            assertEquals(AudioState.MUTED, result);
        }
    }

    @Test
    public void testNotFullscreen_AlwaysMuted() {
        Ad ad = mock(Ad.class);
        when(ad.getAudioState()).thenReturn("on"); // Should be ignored
        AudioState result = AdAudioStateManager.getAudioState(ad, false);
        assertEquals(AudioState.MUTED, result);

        result = AdAudioStateManager.getAudioState(null, false);
        assertEquals(AudioState.MUTED, result);
    }
}
