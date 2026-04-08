// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.media.AudioManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class SoundUtilsTest {

    @Mock
    Context mContext;

    @Mock
    Context mAppContext;

    @Mock
    AudioManager mAudioManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(mContext.getApplicationContext()).thenReturn(mAppContext);

        // Reset static state between tests
        setStaticField("lastCheckedTime", 0L);
        setStaticField("lastMutedState", true);
        setStaticField("isRefreshing", false);
    }

    // ===== Helper methods =====

    private void setStaticField(String fieldName, Object value) throws Exception {
        Field field = SoundUtils.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    private Object getStaticField(String fieldName) throws Exception {
        Field field = SoundUtils.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }

    private void waitForExecutor() throws Exception {
        Field executorField = SoundUtils.class.getDeclaredField("executor");
        executorField.setAccessible(true);
        ExecutorService executor = (ExecutorService) executorField.get(null);

        CountDownLatch latch = new CountDownLatch(1);
        executor.execute(latch::countDown);
        assertTrue("Executor did not finish in time", latch.await(2, TimeUnit.SECONDS));
    }

    // ===== isSoundMuted: null context =====

    @Test
    public void isSoundMuted_nullContext_returnsTrue() {
        assertTrue(SoundUtils.isSoundMuted(null));
    }

    // ===== checkMuteState: API 23+ uses isStreamMute =====

    @Test
    @Config(sdk = 28)
    public void isSoundMuted_streamMuted_api23Plus_returnsTrue() throws Exception {
        when(mAppContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mAudioManager);
        when(mAudioManager.isStreamMute(AudioManager.STREAM_RING)).thenReturn(true);

        SoundUtils.isSoundMuted(mContext);
        waitForExecutor();

        assertTrue(SoundUtils.isSoundMuted(mContext));
    }

    @Test
    @Config(sdk = 28)
    public void isSoundMuted_streamNotMuted_api23Plus_returnsFalse() throws Exception {
        when(mAppContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mAudioManager);
        when(mAudioManager.isStreamMute(AudioManager.STREAM_RING)).thenReturn(false);

        SoundUtils.isSoundMuted(mContext);
        waitForExecutor();

        assertFalse(SoundUtils.isSoundMuted(mContext));
    }

    // ===== checkMuteState: API < 23 uses getRingerMode =====

    @Test
    @Config(sdk = 22)
    public void isSoundMuted_ringerModeSilent_belowApi23_returnsTrue() throws Exception {
        when(mAppContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mAudioManager);
        when(mAudioManager.getRingerMode()).thenReturn(AudioManager.RINGER_MODE_SILENT);

        SoundUtils.isSoundMuted(mContext);
        waitForExecutor();

        assertTrue(SoundUtils.isSoundMuted(mContext));
    }

    @Test
    @Config(sdk = 22)
    public void isSoundMuted_ringerModeVibrate_belowApi23_returnsTrue() throws Exception {
        when(mAppContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mAudioManager);
        when(mAudioManager.getRingerMode()).thenReturn(AudioManager.RINGER_MODE_VIBRATE);

        SoundUtils.isSoundMuted(mContext);
        waitForExecutor();

        assertTrue(SoundUtils.isSoundMuted(mContext));
    }

    @Test
    @Config(sdk = 22)
    public void isSoundMuted_ringerModeNormal_belowApi23_returnsFalse() throws Exception {
        when(mAppContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mAudioManager);
        when(mAudioManager.getRingerMode()).thenReturn(AudioManager.RINGER_MODE_NORMAL);

        SoundUtils.isSoundMuted(mContext);
        waitForExecutor();

        assertFalse(SoundUtils.isSoundMuted(mContext));
    }

    // ===== Null AudioManager =====

    @Test
    @Config(sdk = 28)
    public void isSoundMuted_nullAudioManager_keepsPreviousState() throws Exception {
        when(mAppContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(null);

        // Default lastMutedState is true
        SoundUtils.isSoundMuted(mContext);
        waitForExecutor();

        assertTrue(SoundUtils.isSoundMuted(mContext));
    }

    // ===== Cache behavior =====

    @Test
    @Config(sdk = 28)
    public void isSoundMuted_withinCacheDuration_returnsCachedValue() throws Exception {
        when(mAppContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mAudioManager);
        when(mAudioManager.isStreamMute(AudioManager.STREAM_RING)).thenReturn(false);

        // First call triggers refresh
        SoundUtils.isSoundMuted(mContext);
        waitForExecutor();
        assertFalse(SoundUtils.isSoundMuted(mContext));

        // Change mock — but within cache window, should still return cached false
        when(mAudioManager.isStreamMute(AudioManager.STREAM_RING)).thenReturn(true);
        assertFalse(SoundUtils.isSoundMuted(mContext));
    }

    @Test
    @Config(sdk = 28)
    public void isSoundMuted_afterCacheExpiry_refreshesState() throws Exception {
        when(mAppContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mAudioManager);
        when(mAudioManager.isStreamMute(AudioManager.STREAM_RING)).thenReturn(false);

        SoundUtils.isSoundMuted(mContext);
        waitForExecutor();
        assertFalse(SoundUtils.isSoundMuted(mContext));

        // Force cache expiry
        setStaticField("lastCheckedTime", 0L);
        when(mAudioManager.isStreamMute(AudioManager.STREAM_RING)).thenReturn(true);

        SoundUtils.isSoundMuted(mContext);
        waitForExecutor();

        assertTrue(SoundUtils.isSoundMuted(mContext));
    }

    // ===== Exception handling =====

    @Test
    @Config(sdk = 28)
    public void isSoundMuted_securityException_handledGracefully() throws Exception {
        when(mAppContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mAudioManager);
        when(mAudioManager.isStreamMute(AudioManager.STREAM_RING))
                .thenThrow(new SecurityException("test"));

        // Should not throw; default muted state is preserved
        SoundUtils.isSoundMuted(mContext);
        waitForExecutor();

        assertTrue(SoundUtils.isSoundMuted(mContext));
    }

    @Test
    @Config(sdk = 28)
    public void isSoundMuted_runtimeException_handledGracefully() throws Exception {
        when(mAppContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mAudioManager);
        when(mAudioManager.isStreamMute(AudioManager.STREAM_RING))
                .thenThrow(new RuntimeException("test"));

        SoundUtils.isSoundMuted(mContext);
        waitForExecutor();

        assertTrue(SoundUtils.isSoundMuted(mContext));
    }

    @Test
    @Config(sdk = 28)
    public void isSoundMuted_outOfMemoryError_handledGracefully() throws Exception {
        when(mAppContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mAudioManager);
        when(mAudioManager.isStreamMute(AudioManager.STREAM_RING))
                .thenThrow(new OutOfMemoryError("test"));

        SoundUtils.isSoundMuted(mContext);
        waitForExecutor();

        assertTrue(SoundUtils.isSoundMuted(mContext));
    }

    // ===== Cache advances on failure (prevents tight retry loop) =====

    @Test
    @Config(sdk = 28)
    public void isSoundMuted_afterFailure_cacheWindowAdvances() throws Exception {
        when(mAppContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mAudioManager);
        when(mAudioManager.isStreamMute(AudioManager.STREAM_RING))
                .thenThrow(new RuntimeException("test"));

        SoundUtils.isSoundMuted(mContext);
        waitForExecutor();

        long lastChecked = (long) getStaticField("lastCheckedTime");
        assertTrue("lastCheckedTime should be updated even on failure", lastChecked > 0);
    }

    @Test
    @Config(sdk = 28)
    public void isSoundMuted_afterOomError_cacheWindowAdvances() throws Exception {
        when(mAppContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mAudioManager);
        when(mAudioManager.isStreamMute(AudioManager.STREAM_RING))
                .thenThrow(new OutOfMemoryError("test"));

        SoundUtils.isSoundMuted(mContext);
        waitForExecutor();

        long lastChecked = (long) getStaticField("lastCheckedTime");
        assertTrue("lastCheckedTime should be updated even on OOM", lastChecked > 0);
    }

    // ===== isRefreshing flag =====

    @Test
    @Config(sdk = 28)
    public void isSoundMuted_isRefreshingFlag_resetsAfterSuccess() throws Exception {
        when(mAppContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mAudioManager);
        when(mAudioManager.isStreamMute(AudioManager.STREAM_RING)).thenReturn(false);

        SoundUtils.isSoundMuted(mContext);
        waitForExecutor();

        assertFalse((boolean) getStaticField("isRefreshing"));
    }

    @Test
    @Config(sdk = 28)
    public void isSoundMuted_isRefreshingFlag_resetsAfterException() throws Exception {
        when(mAppContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mAudioManager);
        when(mAudioManager.isStreamMute(AudioManager.STREAM_RING))
                .thenThrow(new RuntimeException("test"));

        SoundUtils.isSoundMuted(mContext);
        waitForExecutor();

        assertFalse((boolean) getStaticField("isRefreshing"));
    }

    @Test
    @Config(sdk = 28)
    public void isSoundMuted_isRefreshingFlag_resetsAfterError() throws Exception {
        when(mAppContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mAudioManager);
        when(mAudioManager.isStreamMute(AudioManager.STREAM_RING))
                .thenThrow(new OutOfMemoryError("test"));

        SoundUtils.isSoundMuted(mContext);
        waitForExecutor();

        assertFalse((boolean) getStaticField("isRefreshing"));
    }
}

