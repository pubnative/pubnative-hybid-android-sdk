
// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.view.View;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.utils.AdAudioStateManager;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityFriendlyObstruction;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityNativeVideoAdSession;
import net.pubnative.lite.sdk.viewability.FriendlyObstructionReasonConstants;
import net.pubnative.lite.sdk.viewability.baseom.BaseFriendlyObstructionPurpose;
import net.pubnative.lite.sdk.vpaid.enums.AdFormat;
import net.pubnative.lite.sdk.vpaid.enums.AdState;
import net.pubnative.lite.sdk.vpaid.enums.AudioState;
import net.pubnative.lite.sdk.vpaid.models.vpaid.AdSpotDimensions;
import net.pubnative.lite.sdk.vpaid.response.AdParams;
import net.pubnative.lite.sdk.vpaid.utils.Utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class VideoAdTest {

    @Mock private Context mockContext;
    @Mock private Ad mockAd;
    @Mock private AdPresenter.ImpressionListener mockImpressionListener;
    @Mock private AdCloseButtonListener mockAdCloseButtonListener;
    @Mock private VideoAdView mockBannerView;
    @Mock private VideoAdController mockAdController;
    @Mock private VideoAdListener mockVideoAdListener;
    @Mock private AdParams mockAdParams;
    @Mock private HyBidViewabilityNativeVideoAdSession mockViewabilityAdSession;
    @Mock private HyBidViewabilityFriendlyObstruction mockObstruction;

    private MockedStatic<Logger> mockedLogger;
    private MockedStatic<AdAudioStateManager> mockedAdAudioStateManager;
    private MockedStatic<Utils> mockedUtils;

    private VideoAd videoAd;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        mockedLogger = mockStatic(Logger.class);
        mockedAdAudioStateManager = mockStatic(AdAudioStateManager.class);
        mockedUtils = mockStatic(Utils.class);

        // Default mock behaviors
        when(mockAd.getVast()).thenReturn("<vast>valid</vast>");
        when(mockBannerView.getWidth()).thenReturn(320);
        when(mockBannerView.getHeight()).thenReturn(50);
        when(mockBannerView.getVisibility()).thenReturn(View.VISIBLE);
        when(mockAdController.getAdParams()).thenReturn(mockAdParams);
        when(mockAdController.getViewabilityFriendlyObstructions()).thenReturn(List.of(mockObstruction));
        when(mockObstruction.getView()).thenReturn(mock(View.class));
        when(mockObstruction.getPurpose()).thenReturn(BaseFriendlyObstructionPurpose.OTHER);
        when(mockObstruction.getReason()).thenReturn("reason");
        mockedAdAudioStateManager.when(() -> AdAudioStateManager.getAudioState(any(), anyBoolean())).thenReturn(AudioState.ON);
        mockedUtils.when(() -> Utils.isPhoneMuted(any())).thenReturn(false);

        videoAd = new VideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(mockVideoAdListener);

        // Set mock controller
        setPrivateField(videoAd, "mAdController", mockAdController);
        setPrivateField(videoAd, "mViewabilityAdSession", mockViewabilityAdSession);
    }

    @After
    public void tearDown() {
        mockedLogger.close();
        mockedAdAudioStateManager.close();
        mockedUtils.close();
    }

    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    public void testConstructor() throws Exception {
        VideoAd ad = new VideoAd(mockContext, mockAd, true, true, mockImpressionListener);
        assertNotNull(ad);
    }

    @Test
    public void testGetAdFormat() {
        assertEquals(AdFormat.BANNER, videoAd.getAdFormat());
    }

    @Test
    public void testGetAdSpotDimensions_withBannerView() {
        videoAd.bindView(mockBannerView);
        AdSpotDimensions dimensions = videoAd.getAdSpotDimensions();
        assertNotNull(dimensions);
        assertEquals(320, dimensions.getWidth());
        assertEquals(50, dimensions.getHeight());
    }

    @Test
    public void testGetAdSpotDimensions_withoutBannerView() {
        AdSpotDimensions dimensions = videoAd.getAdSpotDimensions();
        assertNull(dimensions);
    }

    @Test
    public void testBindView_withValidView() {
        videoAd.bindView(mockBannerView);
        mockedLogger.verify(() -> Logger.d(anyString(), anyString()));
    }

    @Test
    public void testBindView_withNullView() {
        videoAd.bindView(null);
        mockedLogger.verify(() -> Logger.e(anyString(), anyString()));
    }

    @Test
    public void testShow_whenNotReady() throws Exception {
        setPrivateField(videoAd, "mIsReady", false);
        videoAd.bindView(mockBannerView);
        videoAd.show();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        verify(mockAdController, never()).playAd();
    }

    @Test
    public void testShow_whenAlreadyShowing() throws Exception {
        setPrivateField(videoAd, "mAdState", AdState.SHOWING);
        setPrivateField(videoAd, "mIsReady", true);
        videoAd.bindView(mockBannerView);
        videoAd.show();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        mockedLogger.verify(() -> Logger.d(anyString(), eq("Banner already displays on screen")));
    }

    @Test
    public void testShow_success() throws Exception {
        setPrivateField(videoAd, "mAdState", AdState.NONE);
        setPrivateField(videoAd, "mIsReady", true);
        videoAd.bindView(mockBannerView);
        videoAd.show();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        verify(mockAdController).buildVideoAdView(mockBannerView);
        verify(mockAdController).playAd();
        verify(mockViewabilityAdSession).initAdSession(eq(mockBannerView), any());
        verify(mockViewabilityAdSession).addFriendlyObstruction(any(), eq(BaseFriendlyObstructionPurpose.OTHER), eq("reason"));
        verify(mockViewabilityAdSession).fireLoaded();
        assertTrue(videoAd.isAdStarted());
    }

    @Test
    public void testShow_controllerNull() throws Exception {
        setPrivateField(videoAd, "mAdController", null);
        setPrivateField(videoAd, "mIsReady", true);
        videoAd.bindView(mockBannerView);
        videoAd.show();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        verify(mockVideoAdListener).onAdLoadFail(any(PlayerInfo.class));
    }

    @Test
    public void testShow_withNullBannerView() throws Exception {
        setPrivateField(videoAd, "mAdState", AdState.NONE);
        setPrivateField(videoAd, "mIsReady", true);
        videoAd.show();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        mockedLogger.verify(() -> Logger.e(anyString(), eq("Banner is not ready")));
        assertTrue(videoAd.isAdStarted());
    }

    @Test
    public void testShow_withEmptyObstructions() throws Exception {
        when(mockAdController.getViewabilityFriendlyObstructions()).thenReturn(new ArrayList<>());
        setPrivateField(videoAd, "mAdState", AdState.NONE);
        setPrivateField(videoAd, "mIsReady", true);
        videoAd.bindView(mockBannerView);
        videoAd.show();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        verify(mockAdController).buildVideoAdView(mockBannerView);
        verify(mockAdController).playAd();
        verify(mockViewabilityAdSession, never()).addFriendlyObstruction(any(), any(), anyString());
    }

    @Test
    public void testShow_withNullAdParams() throws Exception {
        when(mockAdController.getAdParams()).thenReturn(null);
        setPrivateField(videoAd, "mAdState", AdState.NONE);
        setPrivateField(videoAd, "mIsReady", true);
        videoAd.bindView(mockBannerView);
        videoAd.show();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        verify(mockAdController, never()).playAd();
    }

    @Test
    public void testDismiss_whenNotShowing() throws Exception {
        setPrivateField(videoAd, "mAdState", AdState.NONE);
        videoAd.bindView(mockBannerView);
        videoAd.dismiss();
        ShadowLooper.runUiThreadTasks();
        verify(mockBannerView, never()).setVisibility(View.GONE);
    }

    @Test
    public void testDismiss_success() throws Exception {
        setPrivateField(videoAd, "mAdState", AdState.SHOWING);
        videoAd.bindView(mockBannerView);
        videoAd.dismiss();
        ShadowLooper.runUiThreadTasks();
        verify(mockBannerView).setVisibility(View.GONE);
        verify(mockBannerView).removeAllViews();
        verify(mockAdController).dismiss();
        verify(mockVideoAdListener).onAdDismissed(anyInt());
    }

    @Test
    public void testDismiss_withNullBannerView() throws Exception {
        setPrivateField(videoAd, "mAdState", AdState.SHOWING);
        videoAd.dismiss();
        ShadowLooper.runUiThreadTasks();
        verify(mockAdController).dismiss();
        verify(mockVideoAdListener).onAdDismissed(anyInt());
    }

    @Test
    public void testDismiss_withNullListener() throws Exception {
        videoAd.setAdListener(null);
        setPrivateField(videoAd, "mAdState", AdState.SHOWING);
        videoAd.bindView(mockBannerView);
        videoAd.dismiss();
        ShadowLooper.runUiThreadTasks();
        verify(mockBannerView).setVisibility(View.GONE);
        verify(mockAdController).dismiss();
    }

    @Test
    public void testResume() throws Exception {
        setPrivateField(videoAd, "mIsReady", true);
        videoAd.resume();
        verify(mockAdController).resume();
    }

    @Test
    public void testResume_whenNotReady() throws Exception {
        setPrivateField(videoAd, "mIsReady", false);
        videoAd.resume();
        verify(mockAdController, never()).resume();
    }

    @Test
    public void testResume_withNullController() throws Exception {
        setPrivateField(videoAd, "mAdController", null);
        setPrivateField(videoAd, "mIsReady", true);
        videoAd.resume();
        // Should not crash
    }

    @Test
    public void testResumeEndCardCloseButtonTimer() {
        videoAd.resumeEndCardCloseButtonTimer();
        verify(mockAdController).resumeEndCardCloseButtonTimer();
    }

    @Test
    public void testResumeEndCardCloseButtonTimer_withNullController() throws Exception {
        setPrivateField(videoAd, "mAdController", null);
        videoAd.resumeEndCardCloseButtonTimer();
        // Should not crash
    }

    @Test
    public void testPauseEndCardCloseButtonTimer() {
        videoAd.pauseEndCardCloseButtonTimer();
        verify(mockAdController).pauseEndCardCloseButtonTimer();
    }

    @Test
    public void testPauseEndCardCloseButtonTimer_withNullController() throws Exception {
        setPrivateField(videoAd, "mAdController", null);
        videoAd.pauseEndCardCloseButtonTimer();
        // Should not crash
    }

    @Test
    public void testPause() {
        videoAd.pause();
        verify(mockAdController).pause();
    }

    @Test
    public void testPause_withNullController() throws Exception {
        setPrivateField(videoAd, "mAdController", null);
        videoAd.pause();
        // Should not crash
    }

    @Test
    public void testIsAdStarted() throws Exception {
        assertFalse(videoAd.isAdStarted());
        Field field = videoAd.getClass().getDeclaredField("mIsAdStarted");
        field.setAccessible(true);
        field.set(videoAd, true);
        assertTrue(videoAd.isAdStarted());
    }

    @Test
    public void testSkip() {
        videoAd.skip();
        verify(mockAdController).skipVideo();
    }

    @Test
    public void testSkip_withNullController() throws Exception {
        setPrivateField(videoAd, "mAdController", null);
        try {
            videoAd.skip();
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    @Test
    public void testCloseVideo() {
        videoAd.closeVideo();
        verify(mockAdController).closeSelf();
    }

    @Test
    public void testCloseVideo_withNullController() throws Exception {
        setPrivateField(videoAd, "mAdController", null);
        try {
            videoAd.closeVideo();
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    @Test
    public void testOnVolumeChanged() {
        videoAd.onVolumeChanged();
        verify(mockAdController).onVolumeChanged();
    }

    @Test
    public void testOnVolumeChanged_withNullController() throws Exception {
        setPrivateField(videoAd, "mAdController", null);
        videoAd.onVolumeChanged();
        // Should not crash
    }

    @Test
    public void testValidateAudioState_muted() throws Exception {
        mockedAdAudioStateManager.when(() -> AdAudioStateManager.getAudioState(any(), anyBoolean())).thenReturn(AudioState.MUTED);
        setPrivateField(videoAd, "mIsReady", true);
        videoAd.bindView(mockBannerView);
        videoAd.show();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        verify(mockAdController).toggleMute();
    }

    @Test
    public void testValidateAudioState_default() throws Exception {
        mockedAdAudioStateManager.when(() -> AdAudioStateManager.getAudioState(any(), anyBoolean())).thenReturn(AudioState.DEFAULT);
        setPrivateField(videoAd, "mIsReady", true);
        videoAd.bindView(mockBannerView);
        videoAd.show();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        verify(mockAdController).toggleMute();
    }

    @Test
    public void testValidateAudioState_on_notMuted() throws Exception {
        mockedAdAudioStateManager.when(() -> AdAudioStateManager.getAudioState(any(), anyBoolean())).thenReturn(AudioState.ON);
        mockedUtils.when(() -> Utils.isPhoneMuted(any())).thenReturn(false);
        setPrivateField(videoAd, "mIsReady", true);
        videoAd.bindView(mockBannerView);
        videoAd.show();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        verify(mockAdController, never()).toggleMute();
    }

    @Test
    public void testValidateAudioState_on_muted() throws Exception {
        mockedAdAudioStateManager.when(() -> AdAudioStateManager.getAudioState(any(), anyBoolean())).thenReturn(AudioState.ON);
        mockedUtils.when(() -> Utils.isPhoneMuted(any())).thenReturn(true);
        setPrivateField(videoAd, "mIsReady", true);
        videoAd.bindView(mockBannerView);
        videoAd.show();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        verify(mockAdController).toggleMute();
    }

    @Test
    public void testAddFriendlyObstruction() {
        View mockView = mock(View.class);
        videoAd.addFriendlyObstruction(mockView);
        verify(mockViewabilityAdSession).addFriendlyObstruction(mockView, BaseFriendlyObstructionPurpose.OTHER, FriendlyObstructionReasonConstants.WATERMARK_OBSTRUCTION_REASON);
    }
}
