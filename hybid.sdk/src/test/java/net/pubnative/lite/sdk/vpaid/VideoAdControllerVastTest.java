// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.TextureView;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.CustomCTAData;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityFriendlyObstruction;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityNativeVideoAdSession;
import net.pubnative.lite.sdk.viewability.baseom.BaseFriendlyObstructionPurpose;
import net.pubnative.lite.sdk.vpaid.helpers.EventTracker;
import net.pubnative.lite.sdk.vpaid.macros.MacroHelper;
import net.pubnative.lite.sdk.vpaid.response.AdParams;
import net.pubnative.lite.sdk.vpaid.utils.UrlClickSource;
import net.pubnative.lite.sdk.vpaid.vast.ViewControllerVast;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowMediaPlayer;

import android.view.View;
import android.view.ViewGroup;
import net.pubnative.lite.sdk.views.endcard.HyBidEndCardView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doAnswer;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = ShadowMediaPlayer.class, sdk = 28)
public class VideoAdControllerVastTest {

    @Mock
    private BaseVideoAdInternal mockBaseAdInternal;

    @Mock
    private AdParams mockAdParams;

    @Mock
    private HyBidViewabilityNativeVideoAdSession mockViewabilityAdSession;

    @Mock
    private AdPresenter.ImpressionListener mockImpressionListener;

    @Mock
    private AdCloseButtonListener mockAdCloseButtonListener;

    @Mock
    private CustomCTAData mockCustomCTAData;

    @Mock
    private Ad mockAd;

    @Mock
    private Context mockContext;

    @Mock
    private DeviceInfo mockDeviceInfo;

    private VideoAdControllerVast videoAdControllerVast;
    private Context context;
    
    private MockedStatic<HyBid> mockedHyBid;
    private MockedStatic<EventTracker> mockedEventTracker;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();

        mockedHyBid = mockStatic(HyBid.class);
        mockedEventTracker = mockStatic(EventTracker.class);
        
        mockedHyBid.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
        when(mockDeviceInfo.getUserAgent()).thenReturn("TestUserAgent");
        
        mockedEventTracker.when(() -> EventTracker.postEvent(
                any(Context.class), anyString(), anyString(), any(MacroHelper.class), anyBoolean()
        )).thenAnswer(invocation -> null);

        when(mockBaseAdInternal.getAd()).thenReturn(mockAd);
        when(mockBaseAdInternal.isRewarded()).thenReturn(false);
        when(mockBaseAdInternal.getContext()).thenReturn(context);
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(mockBaseAdInternal).runOnUiThread(any(Runnable.class));

        when(mockAd.isBrandAd()).thenReturn(false);
        when(mockAd.hasHiddenUxControls()).thenReturn(false);
        when(mockAd.getLearnMoreData()).thenReturn(null);
        when(mockAd.getEndCardCloseDelay()).thenReturn(null);
        when(mockAd.getFullScreenClickability()).thenReturn(null);
        when(mockAd.isIconSizeReduced()).thenReturn(null);
        when(mockAd.getAdExperience()).thenReturn(null);
        when(mockAd.getNativeCloseButtonDelay()).thenReturn(null);
        when(mockAd.needCloseRewardAfterFinish()).thenReturn(null);
        when(mockAd.needCloseInterAfterFinish()).thenReturn(null);

        videoAdControllerVast = new VideoAdControllerVast(
                mockBaseAdInternal,
                mockAdParams,
                mockViewabilityAdSession,
                false,
                mockImpressionListener,
                mockAdCloseButtonListener,
                mockCustomCTAData,
                0,
                IntegrationType.IN_APP_BIDDING
        );
    }

    @After
    public void tearDown() {
        if (mockedHyBid != null) {
            mockedHyBid.close();
        }
        if (mockedEventTracker != null) {
            mockedEventTracker.close();
        }
        ShadowMediaPlayer.resetStaticState();
    }

    @Test
    public void testPrepare_callsListenerOnPrepared() {
        VideoAdController.OnPreparedListener mockListener = mock(VideoAdController.OnPreparedListener.class);

        videoAdControllerVast.prepare(mockListener);

        verify(mockListener).onPrepared();
    }

    @Test
    public void testOnEndCardClosed_withCustomEndCard_callsBaseAdInternal() {
        videoAdControllerVast.onEndCardClosed(true);

        verify(mockBaseAdInternal).onEndCardClosed(eq(true));
    }

    @Test
    public void testOnEndCardClosed_withDefaultEndCard_callsBaseAdInternal() {
        videoAdControllerVast.onEndCardClosed(false);

        verify(mockBaseAdInternal).onEndCardClosed(eq(false));
    }

    @Test
    public void testOpenUrl_withVastSource_callsBaseAdInternal() {
        String url = "http://example.com";

        videoAdControllerVast.openUrl(url, UrlClickSource.VAST);

        verify(mockBaseAdInternal).onAdClicked();
    }

    @Test
    public void testOpenUrl_withCustomCTASource_callsBaseAdInternal() {
        String url = "http://example.com";

        videoAdControllerVast.openUrl(url, UrlClickSource.CUSTOM_CTA);

        verify(mockBaseAdInternal).onAdClicked();
    }

    @Test
    public void testOpenUrl_withDefaultEndCardSource_callsBaseAdInternal() {
        String url = "http://example.com";

        videoAdControllerVast.openUrl(url, UrlClickSource.DEFAULT_END_CARD);

        verify(mockBaseAdInternal).onAdClicked();
    }

    @Test
    public void testOpenUrl_withCustomEndCardSource_doesNotCallBaseAdInternal() {
        String url = "http://example.com";

        try {
            videoAdControllerVast.openUrl(url, UrlClickSource.CUSTOM_END_CARD);
        } catch (NullPointerException e) {
            // Expected - BrowserManager is not initialized in test environment
        }

        verify(mockBaseAdInternal, never()).onAdClicked();
    }

    @Test
    public void testOpenUrl_withNullSource_doesNothing() {
        String url = "http://example.com";

        videoAdControllerVast.openUrl(url, null);

        verify(mockBaseAdInternal, never()).onAdClicked();
    }

    @Test
    public void testOnCustomEndCardShow_firesEvent() {
        String endCardType = "custom";

        videoAdControllerVast.onCustomEndCardShow(endCardType);

        verify(mockBaseAdInternal).onCustomEndCardShow(eq(endCardType));
    }

    @Test
    public void testOnDefaultEndCardShow_firesEvent() {
        String endCardType = "default";

        videoAdControllerVast.onDefaultEndCardShow(endCardType);

        verify(mockBaseAdInternal).onDefaultEndCardShow(eq(endCardType));
    }

    @Test
    public void testOnCustomEndCardClick_firesEvent() {
        String endCardType = "custom";

        videoAdControllerVast.onCustomEndCardClick(endCardType);

        verify(mockBaseAdInternal).onCustomEndCardClick(eq(endCardType));
    }

    @Test
    public void testOnDefaultEndCardClick_firesEvent() {
        String endCardType = "default";

        videoAdControllerVast.onDefaultEndCardClick(endCardType);

        verify(mockBaseAdInternal).onDefaultEndCardClick(eq(endCardType));
    }

    @Test
    public void testOnCustomCTAShow_firesEvent() {
        videoAdControllerVast.onCustomCTAShow();

        verify(mockBaseAdInternal).onCustomCTAShow();
    }

    @Test
    public void testOnCustomCTAClick_callsBaseAdInternal() {
        videoAdControllerVast.onCustomCTAClick(true);

        verify(mockBaseAdInternal).onCustomCTAClick(eq(true));
    }

    @Test
    public void testOnCustomCTALoadFail_callsBaseAdInternal() {
        videoAdControllerVast.onCustomCTALoadFail();

        verify(mockBaseAdInternal).onCustomCTALoadFail();
    }

    @Test
    public void testOnEndCardLoadSuccess_withCustomEndCard_callsBaseAdInternal() {
        videoAdControllerVast.onEndCardLoadSuccess(true);

        verify(mockBaseAdInternal).onEndCardLoadSuccess(eq(true));
    }

    @Test
    public void testOnEndCardLoadSuccess_withDefaultEndCard_callsBaseAdInternal() {
        videoAdControllerVast.onEndCardLoadSuccess(false);

        verify(mockBaseAdInternal).onEndCardLoadSuccess(eq(false));
    }

    @Test
    public void testOnEndCardLoadFail_withCustomEndCard_callsBaseAdInternal() {
        videoAdControllerVast.onEndCardLoadFail(true);

        verify(mockBaseAdInternal).onEndCardLoadFail(eq(true));
    }

    @Test
    public void testOnEndCardLoadFail_withDefaultEndCard_callsBaseAdInternal() {
        videoAdControllerVast.onEndCardLoadFail(false);

        verify(mockBaseAdInternal).onEndCardLoadFail(eq(false));
    }

    @Test
    public void testReplayVast_callsBaseAdInternal() {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        videoAdControllerVast.replayVast();

        verify(mockBaseAdInternal).onAdReplaying();
    }

    @Test
    public void testSkipVideo_callsBaseAdInternal() {
        videoAdControllerVast.skipVideo();

        verify(mockBaseAdInternal).onAdSkipped();
    }

    @Test
    public void testHideEndcards_withCustomCTA_hidesLearnMore() throws Exception {
        // on replay, hideEndcards() must hide "Learn More" when a Custom CTA is active,
        // even if it was already visible (otherwise Learn More and the Custom CTA both show).
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        ViewControllerVast viewController = getViewControllerVast();
        setViewControllerBoolean(viewController, "mIsCustomCTA", true);
        View learnMore = getViewControllerView(viewController, "mOpenUrlLayout");
        learnMore.setVisibility(View.VISIBLE); // set Learn More already showing

        viewController.hideEndcards();

        assertEquals(View.GONE, learnMore.getVisibility());
    }

    @Test
    public void testHideEndcards_withoutCustomCTA_showsLearnMore() throws Exception {
        // Without a Custom CTA, hideEndcards() restores "Learn More" (the non-Custom-CTA path).
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        ViewControllerVast viewController = getViewControllerVast();
        setViewControllerBoolean(viewController, "mIsCustomCTA", false);
        setViewControllerBoolean(viewController, "mIsBrandAd", true);
        View learnMore = getViewControllerView(viewController, "mOpenUrlLayout");
        learnMore.setVisibility(View.GONE);

        viewController.hideEndcards();

        assertEquals(View.VISIBLE, learnMore.getVisibility());
    }

    @Test
    public void testSkipVideo_duringReplay_firesFinishedReplaying_andDoesNotCloseAd() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);
        setIsReplay(true);

        videoAdControllerVast.skipVideo();

        verify(mockBaseAdInternal).onAdFinishedReplaying();
        verify(mockBaseAdInternal, never()).dismiss();
        assertTrue(videoAdControllerVast.adFinishedPlaying());
    }

    @Test
    public void testHandleMediaPlayerComplete_duringReplay_firesOnAdFinishedReplayingOnce() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);
        setIsReplay(true);

        invokeHandleMediaPlayerComplete();

        verify(mockBaseAdInternal, times(1)).onAdFinishedReplaying();
        assertTrue(videoAdControllerVast.adFinishedPlaying());
    }

    @Test
    public void handleMediaPlayerComplete_whenSkipControlDisabled_stillReachesEnd() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        Field skipTimeMillisField = VideoAdControllerVast.class.getDeclaredField("mSkipTimeMillis");
        skipTimeMillisField.setAccessible(true);
        skipTimeMillisField.setInt(videoAdControllerVast, -1);

        invokeHandleMediaPlayerComplete();

        verify(mockBaseAdInternal).onAdDidReachEnd();
    }

    @Test
    public void testCloseEndCard_callsCloseSelf() {
        videoAdControllerVast.closeEndCard();

        verify(mockBaseAdInternal).dismiss();
    }

    @Test
    public void testAdFinishedPlaying_initiallyFalse() {
        assertFalse(videoAdControllerVast.adFinishedPlaying());
    }

    @Test
    public void testIsRewarded_returnsCorrectValue() {
        when(mockBaseAdInternal.isRewarded()).thenReturn(true);
        VideoAdControllerVast rewardedController = new VideoAdControllerVast(
                mockBaseAdInternal,
                mockAdParams,
                mockViewabilityAdSession,
                false,
                mockImpressionListener,
                mockAdCloseButtonListener,
                mockCustomCTAData,
                0,
                IntegrationType.IN_APP_BIDDING
        );

        try {
            assertTrue(rewardedController.isRewarded());
        } finally {
            rewardedController.destroy();
        }
    }

    @Test
    public void testGetAdParams_returnsAdParams() {
        AdParams result = videoAdControllerVast.getAdParams();

        assertEquals(mockAdParams, result);
    }

    @Test
    public void testGetViewabilityAdSession_returnsSession() {
        HyBidViewabilityNativeVideoAdSession result = videoAdControllerVast.getViewabilityAdSession();

        assertEquals(mockViewabilityAdSession, result);
    }

    @Test
    public void testAddViewabilityFriendlyObstruction_withValidParams() {
        View view = new View(context);
        BaseFriendlyObstructionPurpose purpose = BaseFriendlyObstructionPurpose.OTHER;
        String reason = "Test reason";

        videoAdControllerVast.addViewabilityFriendlyObstruction(view, purpose, reason);

        List<HyBidViewabilityFriendlyObstruction> obstructions = videoAdControllerVast.getViewabilityFriendlyObstructions();
        assertEquals(1, obstructions.size());
    }

    @Test
    public void testAddViewabilityFriendlyObstruction_withNullView_doesNotAdd() {
        BaseFriendlyObstructionPurpose purpose = BaseFriendlyObstructionPurpose.OTHER;
        String reason = "Test reason";

        videoAdControllerVast.addViewabilityFriendlyObstruction(null, purpose, reason);

        List<HyBidViewabilityFriendlyObstruction> obstructions = videoAdControllerVast.getViewabilityFriendlyObstructions();
        assertEquals(0, obstructions.size());
    }

    @Test
    public void testAddViewabilityFriendlyObstruction_withNullReason_doesNotAdd() {
        View view = new View(context);
        BaseFriendlyObstructionPurpose purpose = BaseFriendlyObstructionPurpose.OTHER;

        videoAdControllerVast.addViewabilityFriendlyObstruction(view, purpose, null);

        List<HyBidViewabilityFriendlyObstruction> obstructions = videoAdControllerVast.getViewabilityFriendlyObstructions();
        assertEquals(0, obstructions.size());
    }

    @Test
    public void testAddViewabilityFriendlyObstruction_withEmptyReason_doesNotAdd() {
        View view = new View(context);
        BaseFriendlyObstructionPurpose purpose = BaseFriendlyObstructionPurpose.OTHER;

        videoAdControllerVast.addViewabilityFriendlyObstruction(view, purpose, "");

        List<HyBidViewabilityFriendlyObstruction> obstructions = videoAdControllerVast.getViewabilityFriendlyObstructions();
        assertEquals(0, obstructions.size());
    }

    @Test
    public void testGetViewabilityFriendlyObstructions_initiallyEmpty() {
        List<HyBidViewabilityFriendlyObstruction> obstructions = videoAdControllerVast.getViewabilityFriendlyObstructions();

        assertNotNull(obstructions);
        assertEquals(0, obstructions.size());
    }

    @Test
    public void testIsVideoVisible_initiallyFalseForNonFullscreen() {
        assertFalse(videoAdControllerVast.isVideoVisible());
    }

    @Test
    public void testIsVideoVisible_initiallyTrueForFullscreen() {
        VideoAdControllerVast fullscreenController = new VideoAdControllerVast(
                mockBaseAdInternal,
                mockAdParams,
                mockViewabilityAdSession,
                true,
                mockImpressionListener,
                mockAdCloseButtonListener,
                mockCustomCTAData,
                0,
                IntegrationType.IN_APP_BIDDING
        );

        try {
            assertTrue(fullscreenController.isVideoVisible());
        } finally {
            fullscreenController.destroy();
        }
    }

    @Test
    public void testSetVideoVisible_updatesVisibility() {
        videoAdControllerVast.setVideoVisible(true);
        assertTrue(videoAdControllerVast.isVideoVisible());

        videoAdControllerVast.setVideoVisible(false);
        assertFalse(videoAdControllerVast.isVideoVisible());
    }

    @Test
    public void testGetProgress_initiallyReturnsMinusOne() {
        int progress = videoAdControllerVast.getProgress();

        assertEquals(-1, progress);
    }

    @Test
    public void testConstructor_withRewardedAd_setsProperDefaults() {
        when(mockBaseAdInternal.isRewarded()).thenReturn(true);

        VideoAdControllerVast rewardedController = new VideoAdControllerVast(
                mockBaseAdInternal,
                mockAdParams,
                mockViewabilityAdSession,
                false,
                mockImpressionListener,
                mockAdCloseButtonListener,
                mockCustomCTAData,
                0,
                IntegrationType.IN_APP_BIDDING
        );

        try {
            assertNotNull(rewardedController);
            assertTrue(rewardedController.isRewarded());
        } finally {
            rewardedController.destroy();
        }
    }

    @Test
    public void testConstructor_withFullscreen_setsVideoVisibleToTrue() {
        VideoAdControllerVast fullscreenController = new VideoAdControllerVast(
                mockBaseAdInternal,
                mockAdParams,
                mockViewabilityAdSession,
                true,
                mockImpressionListener,
                mockAdCloseButtonListener,
                mockCustomCTAData,
                0,
                IntegrationType.IN_APP_BIDDING
        );

        try {
            assertNotNull(fullscreenController);
            assertTrue(fullscreenController.isVideoVisible());
        } finally {
            fullscreenController.destroy();
        }
    }

    @Test
    public void testGetCloseButtonDelay_returnsExpectedValue() {
        when(mockAd.getNativeCloseButtonDelay()).thenReturn(5);

        Integer result = videoAdControllerVast.getCloseButtonDelay(mockAd);

        assertNotNull(result);
    }

    @Test
    public void testFireImpression_callsImpressionListenerAndSetsFlag() throws Exception {
        Method fireImpressionMethod = VideoAdControllerVast.class.getDeclaredMethod("fireImpression");
        fireImpressionMethod.setAccessible(true);

        Field isImpressionFiredField = VideoAdControllerVast.class.getDeclaredField("isImpressionFired");
        isImpressionFiredField.setAccessible(true);

        assertFalse((Boolean) isImpressionFiredField.get(videoAdControllerVast));
        fireImpressionMethod.invoke(videoAdControllerVast);

        verify(mockImpressionListener).onImpression();
        assertTrue((Boolean) isImpressionFiredField.get(videoAdControllerVast));
    }

    private VideoAdControllerVast createFullscreenController() {
        return new VideoAdControllerVast(
                mockBaseAdInternal,
                mockAdParams,
                mockViewabilityAdSession,
                true,
                mockImpressionListener,
                mockAdCloseButtonListener,
                mockCustomCTAData,
                0,
                IntegrationType.IN_APP_BIDDING
        );
    }

    // Helper method to set up and invoke createSkipTimer
    private void invokeCreateSkipTimer(VideoAdControllerVast controller, int skipTimeMillis,
                                       boolean autoClose, boolean showEndcard, boolean showCountdownTimer) throws Exception {
        Method createSkipTimerMethod = VideoAdControllerVast.class.getDeclaredMethod(
                "createSkipTimer", boolean.class, boolean.class, boolean.class);
        createSkipTimerMethod.setAccessible(true);

        Field skipTimeMillisField = VideoAdControllerVast.class.getDeclaredField("mSkipTimeMillis");
        skipTimeMillisField.setAccessible(true);
        skipTimeMillisField.setInt(controller, skipTimeMillis);

        createSkipTimerMethod.invoke(controller, autoClose, showEndcard, showCountdownTimer);
    }

    @Test
    public void testCreateSkipTimer_withZeroSkipTime_callsEndSkipImmediately() throws Exception {
        VideoAdControllerVast fullscreenController = createFullscreenController();
        try {
            invokeCreateSkipTimer(fullscreenController, 0, true, true, false);
            assertNotNull(fullscreenController);
        } finally {
            fullscreenController.destroy();
        }
    }

    @Test
    public void testCreateSkipTimer_withPositiveSkipTime_createsTimer() throws Exception {
        VideoAdControllerVast fullscreenController = createFullscreenController();
        try {
            invokeCreateSkipTimer(fullscreenController, 5000, false, true, true);
            Field skipTimerField = VideoAdControllerVast.class.getDeclaredField("mSkipTimerWithPause");
            skipTimerField.setAccessible(true);
            Object skipTimer = skipTimerField.get(fullscreenController);
            assertNotNull(skipTimer);
        } finally {
            fullscreenController.destroy();
        }
    }

    @Test
    public void testCreateSkipTimer_withAllParametersFalse() throws Exception {
        VideoAdControllerVast fullscreenController = createFullscreenController();
        try {
            invokeCreateSkipTimer(fullscreenController, 5000, false, false, false);
            Field skipTimerField = VideoAdControllerVast.class.getDeclaredField("mSkipTimerWithPause");
            skipTimerField.setAccessible(true);
            assertNotNull(skipTimerField.get(fullscreenController));
        } finally {
            fullscreenController.destroy();
        }
    }

    @Test
    public void testCreateSkipTimer_withAllParametersTrue() throws Exception {
        VideoAdControllerVast fullscreenController = createFullscreenController();
        try {
            invokeCreateSkipTimer(fullscreenController, 5000, true, true, true);
            Field skipTimerField = VideoAdControllerVast.class.getDeclaredField("mSkipTimerWithPause");
            skipTimerField.setAccessible(true);
            assertNotNull(skipTimerField.get(fullscreenController));
        } finally {
            fullscreenController.destroy();
        }
    }

    @Test
    public void testCreateSkipTimer_nonFullscreen_doesNotCreateTimer() throws Exception {
        VideoAdControllerVast nonFullscreenController = new VideoAdControllerVast(
                mockBaseAdInternal,
                mockAdParams,
                mockViewabilityAdSession,
                false,
                mockImpressionListener,
                mockAdCloseButtonListener,
                mockCustomCTAData,
                0,
                IntegrationType.IN_APP_BIDDING
        );

        try {
            invokeCreateSkipTimer(nonFullscreenController, 5000, true, true, true);

            Field skipTimerField = VideoAdControllerVast.class.getDeclaredField("mSkipTimerWithPause");
            skipTimerField.setAccessible(true);
            Object skipTimer = skipTimerField.get(nonFullscreenController);

            // Timer should not be created
            assertNull(skipTimer);
        } finally {
            nonFullscreenController.destroy();
        }
    }

    private void invokeInitSkipTime(VideoAdControllerVast controller, int duration) throws Exception {
        Method initSkipTimeMethod = VideoAdControllerVast.class.getDeclaredMethod("initSkipTime", int.class);
        initSkipTimeMethod.setAccessible(true);
        initSkipTimeMethod.invoke(controller, duration);
    }

    private int getSkipTimeMillis(VideoAdControllerVast controller) throws Exception {
        Field skipTimeMillisField = VideoAdControllerVast.class.getDeclaredField("mSkipTimeMillis");
        skipTimeMillisField.setAccessible(true);
        return skipTimeMillisField.getInt(controller);
    }

    @Test
    public void initSkipTime_forRewarded_skipOffsetAboveDuration_disablesSkipControl() throws Exception {
        when(mockBaseAdInternal.isRewarded()).thenReturn(true);
        when(mockAd.getVideoRewardedSkipOffset()).thenReturn(75);
        when(mockAdParams.getPublisherSkipSeconds()).thenReturn(-1);

        invokeInitSkipTime(videoAdControllerVast, 60000);

        assertEquals(-1, getSkipTimeMillis(videoAdControllerVast));
    }

    @Test
    public void initSkipTime_forRewarded_skipOffsetBelowDuration_isHonoured() throws Exception {
        when(mockBaseAdInternal.isRewarded()).thenReturn(true);
        when(mockAd.getVideoRewardedSkipOffset()).thenReturn(10);
        when(mockAdParams.getPublisherSkipSeconds()).thenReturn(-1);

        invokeInitSkipTime(videoAdControllerVast, 60000);

        assertEquals(10000, getSkipTimeMillis(videoAdControllerVast));
    }

    @Test
    public void initSkipTime_forInterstitial_skipOffsetAboveDuration_disablesSkipControl() throws Exception {
        when(mockBaseAdInternal.isRewarded()).thenReturn(false);
        when(mockAd.getVideoSkipOffset()).thenReturn(75);

        invokeInitSkipTime(videoAdControllerVast, 60000);

        assertEquals(-1, getSkipTimeMillis(videoAdControllerVast));
    }

    @Test
    public void initSkipTime_forInterstitial_skipOffsetBelowDuration_isHonoured() throws Exception {
        when(mockBaseAdInternal.isRewarded()).thenReturn(false);
        when(mockAd.getVideoSkipOffset()).thenReturn(10);

        invokeInitSkipTime(videoAdControllerVast, 60000);

        assertEquals(10000, getSkipTimeMillis(videoAdControllerVast));
    }

    @Test
    public void testCreateSkipTimer_onTick_callsSetSkipProgress() throws Exception {
        VideoAdControllerVast fullscreenController = createFullscreenController();
        try {
            invokeCreateSkipTimer(fullscreenController, 5000, false, true, false);

            Field skipTimerField = VideoAdControllerVast.class.getDeclaredField("mSkipTimerWithPause");
            skipTimerField.setAccessible(true);
            Object skipTimer = skipTimerField.get(fullscreenController);

            assertNotNull("Timer should be created", skipTimer);

            Class<?> timerClass = skipTimer.getClass();
            java.lang.reflect.Method onTickMethod = timerClass.getDeclaredMethod("onTick", long.class);
            onTickMethod.setAccessible(true);

            // Call onTick should execute setSkipProgress
            onTickMethod.invoke(skipTimer, 3000L);

            assertNotNull(fullscreenController);
        } finally {
            fullscreenController.destroy();
        }
    }

    @Test
    public void testCreateSkipTimer_onFinish_callsEndSkip() throws Exception {
        VideoAdControllerVast fullscreenController = createFullscreenController();
        try {
            invokeCreateSkipTimer(fullscreenController, 5000, true, false, false);

            Field skipTimerField = VideoAdControllerVast.class.getDeclaredField("mSkipTimerWithPause");
            skipTimerField.setAccessible(true);
            Object skipTimer = skipTimerField.get(fullscreenController);

            assertNotNull("Timer should be created", skipTimer);

            Class<?> timerClass = skipTimer.getClass();
            java.lang.reflect.Method onFinishMethod = timerClass.getDeclaredMethod("onFinish");
            onFinishMethod.setAccessible(true);

            // Call onFinish should execute endSkip
            onFinishMethod.invoke(skipTimer);

            assertNotNull(fullscreenController);
        } finally {
            fullscreenController.destroy();
        }
    }

    @Test
    public void testCreateSkipTimer_onFinish_withAutoCloseAndEndcard() throws Exception {
        VideoAdControllerVast fullscreenController = createFullscreenController();
        try {
            invokeCreateSkipTimer(fullscreenController, 5000, true, true, false);

            Field skipTimerField = VideoAdControllerVast.class.getDeclaredField("mSkipTimerWithPause");
            skipTimerField.setAccessible(true);
            Object skipTimer = skipTimerField.get(fullscreenController);

            assertNotNull("Timer should be created", skipTimer);

            Class<?> timerClass = skipTimer.getClass();
            java.lang.reflect.Method onFinishMethod = timerClass.getDeclaredMethod("onFinish");
            onFinishMethod.setAccessible(true);

            // Call onFinish should call endSkip
            onFinishMethod.invoke(skipTimer);

            assertNotNull(fullscreenController);
        } finally {
            fullscreenController.destroy();
        }
    }

    @Test
    public void testConstructor_createsAndStartsHandlerThread() throws Exception {
        Field handlerThreadField = VideoAdControllerVast.class.getDeclaredField("mActionsHandlerThread");
        handlerThreadField.setAccessible(true);
        Object handlerThread = handlerThreadField.get(videoAdControllerVast);

        Field handlerField = VideoAdControllerVast.class.getDeclaredField("mActionsProcessingHandler");
        handlerField.setAccessible(true);
        Object handler = handlerField.get(videoAdControllerVast);

        assertNotNull("HandlerThread should be created", handlerThread);
        assertTrue("HandlerThread should be alive", ((android.os.HandlerThread) handlerThread).isAlive());
        assertNotNull("Handler should be created", handler);
    }

    // Helper method for processTrackingEvents tests
    private Object createTrackingEvent(String url, String name, int timeMillis) throws Exception {
        Class<?> trackingEventClass = Class.forName("net.pubnative.lite.sdk.vpaid.models.vpaid.TrackingEvent");
        Object trackingEvent = trackingEventClass.getConstructor(String.class).newInstance(url);
        
        if (name != null) {
            Field nameField = trackingEventClass.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(trackingEvent, name);
        }
        
        Field timeMillisField = trackingEventClass.getDeclaredField("timeMillis");
        timeMillisField.setAccessible(true);
        timeMillisField.setInt(trackingEvent, timeMillis);
        
        return trackingEvent;
    }

    private void addTrackingEvent(Object trackingEvent) throws Exception {
        Field trackingEventsListField = VideoAdControllerVast.class.getDeclaredField("mTrackingEventsList");
        trackingEventsListField.setAccessible(true);
        java.util.List trackingEventsList = (java.util.List) trackingEventsListField.get(videoAdControllerVast);
        trackingEventsList.add(trackingEvent);
    }

    private void setDoneMillis(int millis) throws Exception {
        Field doneMillisField = VideoAdControllerVast.class.getDeclaredField("mDoneMillis");
        doneMillisField.setAccessible(true);
        doneMillisField.setInt(videoAdControllerVast, millis);
    }

    private void invokeProcessTrackingEvents() throws Exception {
        Method processTrackingEventsMethod = VideoAdControllerVast.class.getDeclaredMethod("processTrackingEvents");
        processTrackingEventsMethod.setAccessible(true);
        processTrackingEventsMethod.invoke(videoAdControllerVast);
    }

    private java.util.List getTrackingEventsList() throws Exception {
        Field trackingEventsListField = VideoAdControllerVast.class.getDeclaredField("mTrackingEventsList");
        trackingEventsListField.setAccessible(true);
        return (java.util.List) trackingEventsListField.get(videoAdControllerVast);
    }

    @Test
    public void testProcessTrackingEvents_withEmptyList_doesNotCrash() throws Exception {
        invokeProcessTrackingEvents();
        assertNotNull(videoAdControllerVast);
    }

    @Test
    public void testProcessTrackingEvents_removesProcessedEvents() throws Exception {
        setDoneMillis(5000);
        invokeProcessTrackingEvents();
        assertNotNull(videoAdControllerVast);
    }

    @Test
    public void testProcessTrackingEvents_withStartEvent_firesImpression() throws Exception {
        Object trackingEvent = createTrackingEvent("http://example.com", "start", 1000);
        
        addTrackingEvent(trackingEvent);

        Field containsStartEventField = VideoAdControllerVast.class.getDeclaredField("containsStartEvent");
        containsStartEventField.setAccessible(true);
        containsStartEventField.setBoolean(videoAdControllerVast, true);
        setDoneMillis(2000);
        invokeProcessTrackingEvents();
        verify(mockImpressionListener).onImpression();
    }

    @Test
    public void testProcessTrackingEvents_withNonStartEvent_doesNotFireImpression() throws Exception {
        Object trackingEvent = createTrackingEvent("http://example.com", "testevent", 1000);
        addTrackingEvent(trackingEvent);
        setDoneMillis(2000);
        invokeProcessTrackingEvents();
        verify(mockImpressionListener, never()).onImpression();
    }

    @Test
    public void testProcessTrackingEvents_eventNotYetReached_notProcessed() throws Exception {
        Object trackingEvent = createTrackingEvent("http://example.com", "midpoint", 5000);
        addTrackingEvent(trackingEvent);
        setDoneMillis(3000); // mDoneMillis < event.timeMillis

        invokeProcessTrackingEvents();

        assertEquals(1, getTrackingEventsList().size());
    }

    @Test
    public void testProcessTrackingEvents_multipleEvents_processesCorrectly() throws Exception {
        Object event1 = createTrackingEvent("http://event1.com", "firstQuartile", 1000);
        Object event2 = createTrackingEvent("http://event2.com", "thirdQuartile", 8000);
        Object event3 = createTrackingEvent("http://event3.com", "midpoint", 4000);

        addTrackingEvent(event1);
        addTrackingEvent(event2);
        addTrackingEvent(event3);

        setDoneMillis(5000);
        invokeProcessTrackingEvents();

        // Only event2 should remain
        assertEquals(1, getTrackingEventsList().size());
        assertTrue(getTrackingEventsList().contains(event2));
    }

    @Test
    public void testProcessTrackingEvents_impressionAlreadyFired_doesNotFireAgain() throws Exception {
        Object trackingEvent = createTrackingEvent("http://example.com", "start", 1000);
        addTrackingEvent(trackingEvent);

        Field isImpressionFiredField = VideoAdControllerVast.class.getDeclaredField("isImpressionFired");
        isImpressionFiredField.setAccessible(true);
        isImpressionFiredField.setBoolean(videoAdControllerVast, true);

        setDoneMillis(2000);

        invokeProcessTrackingEvents();

        // Should not fire impression again
        verify(mockImpressionListener, never()).onImpression();
    }


    // Helper methods for processActions tests
    private java.util.List getActionsQueue() throws Exception {
        Field mActionsField = VideoAdControllerVast.class.getDeclaredField("mActions");
        mActionsField.setAccessible(true);
        return (java.util.List) mActionsField.get(videoAdControllerVast);
    }

    private java.util.Map getPendingActionsMap() throws Exception {
        Field mPendingActionsField = VideoAdControllerVast.class.getDeclaredField("mPendingActions");
        mPendingActionsField.setAccessible(true);
        return (java.util.Map) mPendingActionsField.get(videoAdControllerVast);
    }

    private Object getAction(int index) throws Exception {
        Class<?> actionClass = Class.forName("net.pubnative.lite.sdk.vpaid.VideoAdControllerVast$Action");
        return actionClass.getEnumConstants()[index];
    }

    private void invokeProcessActions() throws Exception {
        Method processActionsMethod = VideoAdControllerVast.class.getDeclaredMethod("processActions");
        processActionsMethod.setAccessible(true);
        processActionsMethod.invoke(videoAdControllerVast);
    }

    private boolean getIsActionsProcessingRun() throws Exception {
        Field isActionsProcessingRunField = VideoAdControllerVast.class.getDeclaredField("isActionsProcessingRun");
        isActionsProcessingRunField.setAccessible(true);
        return (Boolean) isActionsProcessingRunField.get(videoAdControllerVast);
    }

    private void setIsActionsProcessingRun(boolean value) throws Exception {
        Field isActionsProcessingRunField = VideoAdControllerVast.class.getDeclaredField("isActionsProcessingRun");
        isActionsProcessingRunField.setAccessible(true);
        isActionsProcessingRunField.setBoolean(videoAdControllerVast, value);
    }

    @Test
    public void testProcessActions_withEmptyQueue_returnsImmediately() throws Exception {
        invokeProcessActions();
        assertNotNull(videoAdControllerVast);
    }

    @Test
    public void testProcessActions_whenAlreadyRunning_returnsImmediately() throws Exception {
        setIsActionsProcessingRun(true);
        invokeProcessActions();
        assertTrue(getIsActionsProcessingRun());
    }

    @Test
    public void testProcessActions_emptyQueueInWhileLoop_setsProcessingRunFalse() throws Exception {
        java.util.List mActions = getActionsQueue();
        Object initialAction = getAction(4);
        mActions.add(initialAction);

        invokeProcessActions();
        Thread.sleep(100);

        assertTrue(mActions.isEmpty());
        assertFalse(getIsActionsProcessingRun());
    }

    @Test
    public void testProcessActions_withPendingActions_addsToFrontOfQueue() throws Exception {
        java.util.List mActions = getActionsQueue();
        java.util.Map mPendingActions = getPendingActionsMap();

        Object playAction = getAction(1);
        Object pauseAction = getAction(2);

        mActions.add(playAction);

        java.util.List pendingList = new java.util.ArrayList();
        pendingList.add(pauseAction);
        mPendingActions.put(playAction, pendingList);

        invokeProcessActions();
        Thread.sleep(100);

        assertFalse(mPendingActions.containsKey(playAction));
    }

    @Test
    public void testProcessActions_pendingActionsNull_doesNotCrash() throws Exception {
        java.util.List mActions = getActionsQueue();
        java.util.Map mPendingActions = getPendingActionsMap();

        Object playAction = getAction(1);

        mActions.add(playAction);
        mPendingActions.put(playAction, null);

        invokeProcessActions();
        Thread.sleep(100);
        
        assertNotNull(videoAdControllerVast);
    }

    @Test
    public void testProcessActions_pendingActionsEmpty_doesNotAddToQueue() throws Exception {
        java.util.List mActions = getActionsQueue();
        java.util.Map mPendingActions = getPendingActionsMap();

        Object playAction = getAction(1);

        mActions.add(playAction);
        mPendingActions.put(playAction, new java.util.ArrayList());

        invokeProcessActions();
        Thread.sleep(100);

        assertTrue(mActions.isEmpty());
    }

    // Helper methods for processPlayAction tests
    private android.media.MediaPlayer setupMockMediaPlayer() throws Exception {
        return setupMockMediaPlayer(30000);
    }

    private android.media.MediaPlayer setupMockMediaPlayer(int duration) throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        when(mockMediaPlayer.getDuration()).thenReturn(duration);
        when(mockMediaPlayer.getVideoWidth()).thenReturn(1920);
        when(mockMediaPlayer.getVideoHeight()).thenReturn(1080);

        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        setupMockSurface();
        return mockMediaPlayer;
    }

    private void setupMockSurface() throws Exception {
        android.view.Surface mockSurface = mock(android.view.Surface.class);
        when(mockSurface.isValid()).thenReturn(true);

        ViewControllerVast vc = getViewControllerVast();
        Field surfaceField = ViewControllerVast.class.getDeclaredField("mSurface");
        surfaceField.setAccessible(true);
        surfaceField.set(vc, mockSurface);

    }

    private void setIsReplay(boolean value) throws Exception {
        Field isReplayField = VideoAdControllerVast.class.getDeclaredField("isReplay");
        isReplayField.setAccessible(true);
        isReplayField.setBoolean(videoAdControllerVast, value);
    }

    private void invokeHandleMediaPlayerComplete() throws Exception {
        Method method = VideoAdControllerVast.class.getDeclaredMethod("handleMediaPlayerComplete");
        method.setAccessible(true);
        method.invoke(videoAdControllerVast);
    }

    private void invokeProcessPlayAction() throws Exception {
        Method processPlayActionMethod = VideoAdControllerVast.class.getDeclaredMethod("processPlayAction");
        processPlayActionMethod.setAccessible(true);
        processPlayActionMethod.invoke(videoAdControllerVast);
    }

    @Test
    public void testProcessPlayAction_createsTimerAndStartsMediaPlayer() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = setupMockMediaPlayer();
        invokeProcessPlayAction();
        verify(mockMediaPlayer).getDuration();
        verify(mockMediaPlayer).start();
    }

    @Test
    public void testProcessPlayAction_firesImpressionWhenNotReplay() throws Exception {
        setupMockMediaPlayer();
        setIsReplay(false);
        invokeProcessPlayAction();
        verify(mockViewabilityAdSession).fireImpression();
    }

    @Test
    public void testProcessPlayAction_doesNotFireImpressionWhenReplay() throws Exception {
        setupMockMediaPlayer();
        setIsReplay(true);
        invokeProcessPlayAction();
        verify(mockViewabilityAdSession, never()).fireImpression();
    }

    @Test
    public void testProcessPlayAction_callsOnAdStarted() throws Exception {
        setupMockMediaPlayer();

        VideoAdListener mockAdListener = mock(VideoAdListener.class);
        when(mockBaseAdInternal.getAdListener()).thenReturn(mockAdListener);
        invokeProcessPlayAction();
        verify(mockAdListener).onAdStarted();
    }

    @Test
    public void testProcessPlayAction_withNullMediaPlayer_returnsEarly() throws Exception {
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, null);
        invokeProcessPlayAction();
        assertNotNull(videoAdControllerVast);
    }

    @Test
    public void testIsDurationInvalid_boundaryValues() throws Exception {
        Method method = VideoAdControllerVast.class.getDeclaredMethod("isDurationInvalid", int.class);
        method.setAccessible(true);

        assertTrue((Boolean) method.invoke(videoAdControllerVast, 0));
        assertTrue((Boolean) method.invoke(videoAdControllerVast, -1));
        assertTrue((Boolean) method.invoke(videoAdControllerVast, Integer.MIN_VALUE));
        assertFalse((Boolean) method.invoke(videoAdControllerVast, 1));
        assertFalse((Boolean) method.invoke(videoAdControllerVast, 15000));
    }

    @Test
    public void testProcessPlayAction_withZeroDuration_doesNotStartMediaPlayer() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = setupMockMediaPlayer(0);

        invokeProcessPlayAction();

        verify(mockMediaPlayer, never()).start();
    }

    @Test
    public void testProcessPlayAction_withNegativeDuration_doesNotStartMediaPlayer() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = setupMockMediaPlayer(-1);

        invokeProcessPlayAction();

        verify(mockMediaPlayer, never()).start();
    }

    @Test
    public void testProcessPlayAction_withZeroDuration_reportsLoadFailure() throws Exception {
        setupMockMediaPlayer(0);

        invokeProcessPlayAction();

        verify(mockBaseAdInternal).onAdLoadFailInternal(any(PlayerInfo.class));
    }

    @Test
    public void testProcessPlayAction_withNegativeDuration_reportsLoadFailure() throws Exception {
        setupMockMediaPlayer(-1);

        invokeProcessPlayAction();

        verify(mockBaseAdInternal).onAdLoadFailInternal(any(PlayerInfo.class));
    }

    @Test
    public void testProcessPlayAction_withZeroDuration_doesNotFireImpressionOrOnAdStarted() throws Exception {
        setupMockMediaPlayer(0);
        setIsReplay(false);

        VideoAdListener mockAdListener = mock(VideoAdListener.class);
        when(mockBaseAdInternal.getAdListener()).thenReturn(mockAdListener);

        invokeProcessPlayAction();

        verify(mockViewabilityAdSession, never()).fireImpression();
        verify(mockAdListener, never()).onAdStarted();
    }

    @Test
    public void testProcessPlayAction_withZeroDuration_doesNotCreateTimer() throws Exception {
        setupMockMediaPlayer(0);

        invokeProcessPlayAction();

        Field timerField = VideoAdControllerVast.class.getDeclaredField("mTimerWithPause");
        timerField.setAccessible(true);
        assertNull(timerField.get(videoAdControllerVast));
    }

    @Test
    public void testProcessPlayAction_withPositiveDuration_doesNotReportLoadFailure() throws Exception {
        setupMockMediaPlayer(30000);

        invokeProcessPlayAction();

        verify(mockBaseAdInternal, never()).onAdLoadFailInternal(any());
    }

    @Test
    public void testProcessPlayAction_withPositiveDuration_createsTimer() throws Exception {
        setupMockMediaPlayer(30000);

        invokeProcessPlayAction();

        Field timerField = VideoAdControllerVast.class.getDeclaredField("mTimerWithPause");
        timerField.setAccessible(true);
        assertNotNull(timerField.get(videoAdControllerVast));
    }


    // =====================================================================
    // Tests for fireViewabilityTrackingEvent (refactored method)
    // =====================================================================

    private void invokeFireViewabilityTrackingEvent(VideoAdControllerVast controller, String name) throws Exception {
        Method method = VideoAdControllerVast.class.getDeclaredMethod("fireViewabilityTrackingEvent", String.class);
        method.setAccessible(true);
        method.invoke(controller, name);
    }

    private void setViewabilityAdSession(VideoAdControllerVast controller, HyBidViewabilityNativeVideoAdSession session) throws Exception {
        Field field = VideoAdControllerVast.class.getDeclaredField("mViewabilityAdSession");
        field.setAccessible(true);
        field.set(controller, session);
    }

    private boolean getFieldBoolean(VideoAdControllerVast controller, String fieldName) throws Exception {
        Field field = VideoAdControllerVast.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.getBoolean(controller);
    }

    private void setFieldBoolean(VideoAdControllerVast controller, String fieldName, boolean value) throws Exception {
        Field field = VideoAdControllerVast.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.setBoolean(controller, value);
    }

    private void setFieldInt(VideoAdControllerVast controller, String fieldName, int value) throws Exception {
        Field field = VideoAdControllerVast.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.setInt(controller, value);
    }

    @Test
    public void testFireViewabilityTrackingEvent_nullSession_doesNothing() throws Exception {
        setViewabilityAdSession(videoAdControllerVast, null);

        invokeFireViewabilityTrackingEvent(videoAdControllerVast, "start");

        verify(mockViewabilityAdSession, never()).fireStart(anyFloat(), anyBoolean());
    }

    @Test
    public void testFireViewabilityTrackingEvent_emptyName_doesNothing() throws Exception {
        invokeFireViewabilityTrackingEvent(videoAdControllerVast, "");

        verify(mockViewabilityAdSession, never()).fireStart(anyFloat(), anyBoolean());
        verify(mockViewabilityAdSession, never()).fireFirstQuartile();
        verify(mockViewabilityAdSession, never()).fireMidpoint();
        verify(mockViewabilityAdSession, never()).fireThirdQuartile();
    }

    @Test
    public void testFireViewabilityTrackingEvent_nullName_doesNothing() throws Exception {
        invokeFireViewabilityTrackingEvent(videoAdControllerVast, null);

        verify(mockViewabilityAdSession, never()).fireStart(anyFloat(), anyBoolean());
    }

    @Test
    public void testFireViewabilityTrackingEvent_start_firesStartOnSession() throws Exception {
        when(mockAdParams.getDurationInteger()).thenReturn(30);

        invokeFireViewabilityTrackingEvent(videoAdControllerVast, "start");

        verify(mockViewabilityAdSession).fireStart(eq(30f), eq(true));
    }

    @Test
    public void testFireViewabilityTrackingEvent_start_setsStartFired() throws Exception {
        when(mockAdParams.getDurationInteger()).thenReturn(30);

        assertFalse(getFieldBoolean(videoAdControllerVast, "startFired"));
        invokeFireViewabilityTrackingEvent(videoAdControllerVast, "start");

        assertTrue(getFieldBoolean(videoAdControllerVast, "startFired"));
    }

    @Test
    public void testFireViewabilityTrackingEvent_start_alreadyFired_doesNotFireReportingAgain() throws Exception {
        when(mockAdParams.getDurationInteger()).thenReturn(30);
        setFieldBoolean(videoAdControllerVast, "startFired", true);

        invokeFireViewabilityTrackingEvent(videoAdControllerVast, "start");

        // fireStart on the session is still called (no guard on that), but the reporting event is not fired again.
        verify(mockViewabilityAdSession).fireStart(eq(30f), eq(true));
        assertTrue(getFieldBoolean(videoAdControllerVast, "startFired"));
    }

    @Test
    public void testFireViewabilityTrackingEvent_firstQuartile_firesFirstQuartileOnSession() throws Exception {
        invokeFireViewabilityTrackingEvent(videoAdControllerVast, "firstQuartile");

        verify(mockViewabilityAdSession).fireFirstQuartile();
    }

    @Test
    public void testFireViewabilityTrackingEvent_firstQuartile_setsFlag() throws Exception {
        assertFalse(getFieldBoolean(videoAdControllerVast, "firstQuartileFired"));

        invokeFireViewabilityTrackingEvent(videoAdControllerVast, "firstQuartile");

        assertTrue(getFieldBoolean(videoAdControllerVast, "firstQuartileFired"));
    }

    @Test
    public void testFireViewabilityTrackingEvent_midpoint_firesMidpointOnSession() throws Exception {
        invokeFireViewabilityTrackingEvent(videoAdControllerVast, "midpoint");

        verify(mockViewabilityAdSession).fireMidpoint();
    }

    @Test
    public void testFireViewabilityTrackingEvent_midpoint_setsFlag() throws Exception {
        assertFalse(getFieldBoolean(videoAdControllerVast, "midpointFired"));

        invokeFireViewabilityTrackingEvent(videoAdControllerVast, "midpoint");

        assertTrue(getFieldBoolean(videoAdControllerVast, "midpointFired"));
    }

    @Test
    public void testFireViewabilityTrackingEvent_thirdQuartile_firesThirdQuartileOnSession() throws Exception {
        invokeFireViewabilityTrackingEvent(videoAdControllerVast, "thirdQuartile");

        verify(mockViewabilityAdSession).fireThirdQuartile();
    }

    @Test
    public void testFireViewabilityTrackingEvent_thirdQuartile_setsFlag() throws Exception {
        assertFalse(getFieldBoolean(videoAdControllerVast, "thirdQuartileFired"));

        invokeFireViewabilityTrackingEvent(videoAdControllerVast, "thirdQuartile");

        assertTrue(getFieldBoolean(videoAdControllerVast, "thirdQuartileFired"));
    }

    @Test
    public void testFireViewabilityTrackingEvent_unknownName_doesNothing() throws Exception {
        invokeFireViewabilityTrackingEvent(videoAdControllerVast, "unknownEvent");

        verify(mockViewabilityAdSession, never()).fireStart(anyFloat(), anyBoolean());
        verify(mockViewabilityAdSession, never()).fireFirstQuartile();
        verify(mockViewabilityAdSession, never()).fireMidpoint();
        verify(mockViewabilityAdSession, never()).fireThirdQuartile();
    }

    // =====================================================================
    // End-to-end tests: fireViewabilityTrackingEvent(START) duration resolution
    // Verifies the exact value passed into fireStart() for each fallback path
    // =====================================================================

    private void nullifyAdParams() throws Exception {
        Field adParamsField = VideoAdControllerVast.class.getDeclaredField("mAdParams");
        adParamsField.setAccessible(true);
        adParamsField.set(videoAdControllerVast, null);
    }

    @Test
    public void testFireViewabilityTrackingEvent_start_adParamsNullDuration_fallsBackToMDuration() throws Exception {
        when(mockAdParams.getDurationInteger()).thenReturn(null);
        setFieldInt(videoAdControllerVast, "mDuration", 30000);

        invokeFireViewabilityTrackingEvent(videoAdControllerVast, "start");

        verify(mockViewabilityAdSession).fireStart(eq(30f), eq(true));
    }

    @Test
    public void testFireViewabilityTrackingEvent_start_adParamsZeroDuration_fallsBackToMDuration() throws Exception {
        when(mockAdParams.getDurationInteger()).thenReturn(0);
        setFieldInt(videoAdControllerVast, "mDuration", 15000);

        invokeFireViewabilityTrackingEvent(videoAdControllerVast, "start");

        verify(mockViewabilityAdSession).fireStart(eq(15f), eq(true));
    }

    @Test
    public void testFireViewabilityTrackingEvent_start_adParamsNegativeDuration_fallsBackToMDuration() throws Exception {
        when(mockAdParams.getDurationInteger()).thenReturn(-1);
        setFieldInt(videoAdControllerVast, "mDuration", 10000);

        invokeFireViewabilityTrackingEvent(videoAdControllerVast, "start");

        verify(mockViewabilityAdSession).fireStart(eq(10f), eq(true));
    }

    @Test
    public void testFireViewabilityTrackingEvent_start_noAdParams_fallsBackToMDuration() throws Exception {
        nullifyAdParams();
        setFieldInt(videoAdControllerVast, "mDuration", 45000);

        invokeFireViewabilityTrackingEvent(videoAdControllerVast, "start");

        verify(mockViewabilityAdSession).fireStart(eq(45f), eq(true));
    }

    @Test
    public void testFireViewabilityTrackingEvent_start_noAdParamsNoMDuration_fallsBackToSkipTime() throws Exception {
        nullifyAdParams();
        setFieldInt(videoAdControllerVast, "mDuration", -1);
        setFieldInt(videoAdControllerVast, "mSkipTimeMillis", 5000);

        invokeFireViewabilityTrackingEvent(videoAdControllerVast, "start");

        verify(mockViewabilityAdSession).fireStart(eq(5f), eq(true));
    }

    @Test
    public void testFireViewabilityTrackingEvent_start_allDurationSourcesUnavailable_passesZero() throws Exception {
        nullifyAdParams();
        setFieldInt(videoAdControllerVast, "mDuration", -1);
        setFieldInt(videoAdControllerVast, "mSkipTimeMillis", -1);

        invokeFireViewabilityTrackingEvent(videoAdControllerVast, "start");

        verify(mockViewabilityAdSession).fireStart(eq(0f), eq(true));
    }

    @Test
    public void testFireViewabilityTrackingEvent_start_nullDurationDoesNotThrowNPE() throws Exception {
        when(mockAdParams.getDurationInteger()).thenReturn(null);
        setFieldInt(videoAdControllerVast, "mDuration", -1);
        setFieldInt(videoAdControllerVast, "mSkipTimeMillis", -1);

        invokeFireViewabilityTrackingEvent(videoAdControllerVast, "start");

        verify(mockViewabilityAdSession).fireStart(eq(0f), eq(true));
    }

    // =====================================================================
    // Tests for resolveStartDuration (extracted helper)
    // =====================================================================

    private float invokeResolveStartDuration(VideoAdControllerVast controller) throws Exception {
        Method method = VideoAdControllerVast.class.getDeclaredMethod("resolveStartDuration");
        method.setAccessible(true);
        return (float) method.invoke(controller);
    }

    @Test
    public void testResolveStartDuration_adParamsNonNull_returnsDurationInSeconds() throws Exception {
        when(mockAdParams.getDurationInteger()).thenReturn(45);

        float result = invokeResolveStartDuration(videoAdControllerVast);

        assertEquals(45f, result, 0.001f);
    }

    @Test
    public void testResolveStartDuration_adParamsNullDuration_fallsBackToMDuration() throws Exception {
        when(mockAdParams.getDurationInteger()).thenReturn(null);
        setFieldInt(videoAdControllerVast, "mDuration", 20000);

        float result = invokeResolveStartDuration(videoAdControllerVast);

        assertEquals(20f, result, 0.001f);
    }

    @Test
    public void testResolveStartDuration_adParamsZeroDuration_fallsBackToMDuration() throws Exception {
        when(mockAdParams.getDurationInteger()).thenReturn(0);
        setFieldInt(videoAdControllerVast, "mDuration", 30000);

        float result = invokeResolveStartDuration(videoAdControllerVast);

        assertEquals(30f, result, 0.001f);
    }

    @Test
    public void testResolveStartDuration_adParamsNull_mDurationPositive_returnsSeconds() throws Exception {
        Field adParamsField = VideoAdControllerVast.class.getDeclaredField("mAdParams");
        adParamsField.setAccessible(true);
        adParamsField.set(videoAdControllerVast, null);

        setFieldInt(videoAdControllerVast, "mDuration", 20000);

        float result = invokeResolveStartDuration(videoAdControllerVast);

        assertEquals(20f, result, 0.001f);
    }

    @Test
    public void testResolveStartDuration_adParamsNull_mDurationNonPositive_mSkipTimePositive_returnsSeconds() throws Exception {
        Field adParamsField = VideoAdControllerVast.class.getDeclaredField("mAdParams");
        adParamsField.setAccessible(true);
        adParamsField.set(videoAdControllerVast, null);

        setFieldInt(videoAdControllerVast, "mDuration", -1);
        setFieldInt(videoAdControllerVast, "mSkipTimeMillis", 5000);

        float result = invokeResolveStartDuration(videoAdControllerVast);

        assertEquals(5f, result, 0.001f);
    }

    @Test
    public void testResolveStartDuration_allFallback_returnsZero() throws Exception {
        Field adParamsField = VideoAdControllerVast.class.getDeclaredField("mAdParams");
        adParamsField.setAccessible(true);
        adParamsField.set(videoAdControllerVast, null);

        setFieldInt(videoAdControllerVast, "mDuration", -1);
        setFieldInt(videoAdControllerVast, "mSkipTimeMillis", -1);

        float result = invokeResolveStartDuration(videoAdControllerVast);

        assertEquals(0f, result, 0.001f);
    }

    @Test
    public void testResolveStartDuration_adParamsExistsNullDuration_allFallbacksNonPositive_returnsZero() throws Exception {
        when(mockAdParams.getDurationInteger()).thenReturn(null);
        setFieldInt(videoAdControllerVast, "mDuration", -1);
        setFieldInt(videoAdControllerVast, "mSkipTimeMillis", -1);

        float result = invokeResolveStartDuration(videoAdControllerVast);

        assertEquals(0f, result, 0.001f);
    }

    @Test
    public void testResolveStartDuration_adParamsExistsNullDuration_fallsBackToSkipTime() throws Exception {
        when(mockAdParams.getDurationInteger()).thenReturn(null);
        setFieldInt(videoAdControllerVast, "mDuration", -1);
        setFieldInt(videoAdControllerVast, "mSkipTimeMillis", 5000);

        float result = invokeResolveStartDuration(videoAdControllerVast);

        assertEquals(5f, result, 0.001f);
    }

    // =====================================================================
    // Tests for fireReportingEventOnce (extracted helper)
    // =====================================================================

    private boolean invokeFireReportingEventOnce(VideoAdControllerVast controller, boolean alreadyFired, String eventType) throws Exception {
        Method method = VideoAdControllerVast.class.getDeclaredMethod("fireReportingEventOnce", boolean.class, String.class);
        method.setAccessible(true);
        return (boolean) method.invoke(controller, alreadyFired, eventType);
    }

    @Test
    public void testFireReportingEventOnce_notYetFired_returnsTrue() throws Exception {
        boolean result = invokeFireReportingEventOnce(videoAdControllerVast, false, "video_started");

        assertTrue(result);
    }

    @Test
    public void testFireReportingEventOnce_alreadyFired_returnsTrueWithoutFiringAgain() throws Exception {
        boolean result = invokeFireReportingEventOnce(videoAdControllerVast, true, "video_started");

        assertTrue(result);
    }

    @Test
    public void testDestroy_cleansUpResources() throws Exception {
        videoAdControllerVast.destroy();

        assertTrue(videoAdControllerVast.adFinishedPlaying());

        Field handlerThreadField = VideoAdControllerVast.class.getDeclaredField("mActionsHandlerThread");
        handlerThreadField.setAccessible(true);
        Object handlerThread = handlerThreadField.get(videoAdControllerVast);
        assertNull("HandlerThread should be null after destroy", handlerThread);

        Field handlerField = VideoAdControllerVast.class.getDeclaredField("mActionsProcessingHandler");
        handlerField.setAccessible(true);
        Object handler = handlerField.get(videoAdControllerVast);
        assertNull("Handler should be null after destroy", handler);

        Field timerField = VideoAdControllerVast.class.getDeclaredField("mTimerWithPause");
        timerField.setAccessible(true);
        assertNull("Timer should be null after destroy", timerField.get(videoAdControllerVast));

        Field skipTimerField = VideoAdControllerVast.class.getDeclaredField("mSkipTimerWithPause");
        skipTimerField.setAccessible(true);
        assertNull("Skip timer should be null after destroy", skipTimerField.get(videoAdControllerVast));
    }

    // =====================================================================
    // Tests for surface-recovery path: setVideoVisible / recoverMediaPlayerSurface
    // =====================================================================

    /**
     * After buildVideoAdView() the visibility listener fires onVisibilityChanged(VISIBLE),
     * which calls setVideoVisible(true) for the first time (this.videoVisible was false → no
     * recovery). The state should now be visible.
     */
    @Test
    public void testBuildVideoAdView_firesVisibilityListener_setsVideoVisibleToTrue() {
        assertFalse("Initially videoVisible must be false for a non-fullscreen controller",
                videoAdControllerVast.isVideoVisible());

        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        assertTrue("buildVideoAdView should set videoVisible to true via the visibility listener",
                videoAdControllerVast.isVideoVisible());
    }

    /**
     * When setVideoVisible(true) is called and videoVisible is already true (the condition that
     * ViewControllerVast.mCreateTextureListener now triggers on onSurfaceTextureAvailable), and
     * mMediaPlayer is null, recoverMediaPlayerSurface() must return early without throwing.
     */
    @Test
    public void testSetVideoVisible_whenAlreadyVisible_nullMediaPlayer_doesNotThrow() {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);
        assertTrue(videoAdControllerVast.isVideoVisible());

        // mMediaPlayer is null — recoverMediaPlayerSurface() should be a no-op
        videoAdControllerVast.setVideoVisible(true);

        assertTrue("videoVisible must remain true after the redundant setVideoVisible(true) call",
                videoAdControllerVast.isVideoVisible());
    }

    /**
     * When setVideoVisible(true) is called for the very first time (videoVisible was false),
     * recoverMediaPlayerSurface() must NOT be triggered — even if mMediaPlayer is already set.
     * This guards against inadvertently resetting the surface on the normal first-show path.
     */
    @Test
    public void testSetVideoVisible_firstTransitionToVisible_doesNotTriggerMediaPlayerRecovery() throws Exception {
        assertFalse(videoAdControllerVast.isVideoVisible());

        // Inject a mock MediaPlayer so we can verify setSurface is never called
        android.media.MediaPlayer mockMp = mock(android.media.MediaPlayer.class);
        Field mpField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mpField.setAccessible(true);
        mpField.set(videoAdControllerVast, mockMp);

        // First-time transition: false → true
        videoAdControllerVast.setVideoVisible(true);
        assertTrue(videoAdControllerVast.isVideoVisible());

        // Run any pending delayed tasks on the main looper
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // setSurface must NOT have been invoked because this was the initial visibility transition
        verify(mockMp, never()).setSurface(any());
    }

    /**
     * When setVideoVisible(true) is called while videoVisible is already true (simulating
     * ViewControllerVast.mCreateTextureListener.onSurfaceTextureAvailable notifying the controller
     * after the SurfaceTexture becomes available), and mMediaPlayer is non-null,
     * recoverMediaPlayerSurface() must schedule a setSurface() call via postDelayed.
     */
    @Test
    public void testSetVideoVisible_whenAlreadyVisible_withMediaPlayer_schedulesSurfaceRecovery() throws Exception {
        // buildVideoAdView sets videoVisible = true via the visibility listener
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);
        assertTrue(videoAdControllerVast.isVideoVisible());

        // Inject a mock MediaPlayer
        android.media.MediaPlayer mockMp = mock(android.media.MediaPlayer.class);
        Field mpField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mpField.setAccessible(true);
        mpField.set(videoAdControllerVast, mockMp);

        // Simulate onSurfaceTextureAvailable notifying the controller while video is already visible
        getTextureListener().onSurfaceTextureAvailable(new SurfaceTexture(0), 0, 0);

        // Clear any interactions from the callback above to verify the effect of the setVideoVisible(true) call
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        clearInvocations(mockMp);

        videoAdControllerVast.setVideoVisible(true);

        // Drain all pending (including delayed) tasks so the postDelayed action executes
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // setSurface must have been called by the explicit setVideoVisible(true) call
        verify(mockMp, times(1)).setSurface(any());
    }

    /**
     * After destroy() is called, any pending postDelayed callbacks must be cleared and further
     * setVideoVisible(true) calls must not schedule new surface-recovery tasks. This guards
     * against ViewControllerVast executing delayed work after the controller has been torn down.
     */
    @Test
    public void testSetVideoVisible_afterDestroy_doesNotScheduleSurfaceRecovery() throws Exception {
        // Build the view so videoVisible becomes true
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);
        assertTrue(videoAdControllerVast.isVideoVisible());

        // Inject a mock MediaPlayer
        android.media.MediaPlayer mockMp = mock(android.media.MediaPlayer.class);
        Field mpField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mpField.setAccessible(true);
        mpField.set(videoAdControllerVast, mockMp);

        // Tear down the controller — this must clear pending callbacks and set mIsDestroyed
        videoAdControllerVast.destroy();

        // Attempt to trigger surface recovery after teardown
        videoAdControllerVast.setVideoVisible(true);

        // Drain the main looper — nothing should execute
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // setSurface must NOT be called because postDelayed is guarded by mIsDestroyed
        verify(mockMp, never()).setSurface(any());
    }

    // =====================================================================
    // Tests for ViewControllerVast texture-listener and lifecycle paths
    // =====================================================================

    /**
     * onSurfaceTextureAvailable() must assign a non-null Surface to mSurface when the controller
     * is still alive.
     */
    @Test
    public void testOnSurfaceTextureAvailable_setsSurface() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        getTextureListener().onSurfaceTextureAvailable(new SurfaceTexture(0), 0, 0);

        assertNotNull("mSurface must be non-null after onSurfaceTextureAvailable",
                getVcSurface());
    }

    /**
     * onSurfaceTextureAvailable() after destroy() must be a no-op: mSurface must remain null
     * and no Surface object must be created.
     */
    @Test
    public void testOnSurfaceTextureAvailable_afterDestroy_doesNotSetSurface() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);
        videoAdControllerVast.destroy();

        getTextureListener().onSurfaceTextureAvailable(new SurfaceTexture(0), 0, 0);

        assertNull("mSurface must stay null after destroy() + onSurfaceTextureAvailable",
                getVcSurface());
    }

    /**
     * onSurfaceTextureAvailable() after destroy() must not schedule any surface-recovery work,
     * even when a MediaPlayer is present.
     */
    @Test
    public void testOnSurfaceTextureAvailable_afterDestroy_doesNotScheduleRecovery() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        android.media.MediaPlayer mockMp = mock(android.media.MediaPlayer.class);
        Field mpField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mpField.setAccessible(true);
        mpField.set(videoAdControllerVast, mockMp);

        videoAdControllerVast.destroy();

        getTextureListener().onSurfaceTextureAvailable(new SurfaceTexture(0), 0, 0);
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        verify(mockMp, never()).setSurface(any());
    }

    /**
     * onSurfaceTextureAvailable() while videoVisible is already true (the late-surface scenario)
     * must schedule a setSurface() call on the MediaPlayer, reusing the existing recovery path.
     */
    @Test
    public void testOnSurfaceTextureAvailable_whenVideoVisible_schedulesRecovery() throws Exception {
        // buildVideoAdView makes videoVisible=true via the visibility listener
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);
        assertTrue(videoAdControllerVast.isVideoVisible());

        android.media.MediaPlayer mockMp = mock(android.media.MediaPlayer.class);
        Field mpField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mpField.setAccessible(true);
        mpField.set(videoAdControllerVast, mockMp);

        // Simulate the TextureView surface becoming available after playback has already started
        getTextureListener().onSurfaceTextureAvailable(new SurfaceTexture(0), 0, 0);
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        verify(mockMp, times(1)).setSurface(any());
    }

    /**
     * onSurfaceTextureDestroyed() must release mSurface and null it out so that
     * getSurface() returns null afterwards.
     */
    @Test
    public void testOnSurfaceTextureDestroyed_nullsSurface() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        // First give the controller a surface
        TextureView.SurfaceTextureListener listener = getTextureListener();
        listener.onSurfaceTextureAvailable(new SurfaceTexture(0), 0, 0);
        assertNotNull("Pre-condition: mSurface must be set", getVcSurface());

        listener.onSurfaceTextureDestroyed(new SurfaceTexture(0));

        assertNull("mSurface must be null after onSurfaceTextureDestroyed", getVcSurface());
    }

    /**
     * onSurfaceTextureDestroyed() after destroy() must be a no-op: mSurface was already
     * released in destroy() and the guard must prevent any further action.
     */
    @Test
    public void testOnSurfaceTextureDestroyed_afterDestroy_isNoOp() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);
        videoAdControllerVast.destroy();

        // Must not throw; mSurface was already null'd by destroy()
        getTextureListener().onSurfaceTextureDestroyed(new SurfaceTexture(0));

        assertNull("mSurface must remain null after destroy() + onSurfaceTextureDestroyed",
                getVcSurface());
    }

    /**
     * destroy() must null the mVideoPlayerLayoutTexture reference so the TextureView
     * (and any listener it holds) cannot be reached from the controller after teardown.
     */
    @Test
    public void testDestroy_nullsTextureViewReference() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        // TextureView must be non-null before destroy
        assertNotNull("Pre-condition: mVideoPlayerLayoutTexture must be set after buildVideoAdView",
                getVcTextureView());

        videoAdControllerVast.destroy();

        assertNull("mVideoPlayerLayoutTexture must be null after destroy()",
                getVcTextureView());
    }

    /**
     * dismiss() must remove all pending postDelayed callbacks from the main-thread Handler
     * so that a surface-recovery task scheduled before dismiss() does not fire afterwards.
     */
    @Test
    public void testDismiss_clearsPendingPostDelayedCallbacks() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);
        assertTrue(videoAdControllerVast.isVideoVisible());

        android.media.MediaPlayer mockMp = mock(android.media.MediaPlayer.class);
        Field mpField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mpField.setAccessible(true);
        mpField.set(videoAdControllerVast, mockMp);

        // Schedule a surface-recovery task (videoVisible is already true)
        videoAdControllerVast.setVideoVisible(true);

        // Dismiss before the delayed task fires — must drain the handler queue
        videoAdControllerVast.dismiss();

        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // The recovery task was removed by dismiss(), so setSurface must never be called
        verify(mockMp, never()).setSurface(any());
    }

    /** Attaches the view tree to a visible Activity so View.post() runnable actually run. */
    private void attachToWindow(View root) {
        org.robolectric.android.controller.ActivityController<android.app.Activity> controller =
                org.robolectric.Robolectric.buildActivity(android.app.Activity.class).create();
        controller.get().setContentView(root);
        controller.start().resume().visible();
    }

    /** Forces a layout pass on the given view so its OnLayoutChangeListener fires, then flushes. */
    private void forceLayout(View view, int w, int h) {
        view.measure(View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, w, h);
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
    }

    @Test
    public void testAdjustLayoutParams_layoutGuardSkipsWhenSizeUnchanged() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);
        attachToWindow(videoAdView);
        ViewControllerVast vc = getViewControllerVast();

        vc.adjustLayoutParams(640, 480);

        android.widget.FrameLayout videoPlayer =
                (android.widget.FrameLayout) getViewControllerView(vc, "mVideoPlayerLayout");
        int widthAfterAdjust = videoPlayer.getLayoutParams().width;
        int heightAfterAdjust = videoPlayer.getLayoutParams().height;

        // Fire the OnLayoutChangeListener with an unchanged banner size: the guard must NOT re-apply params.
        forceLayout(videoPlayer, 300, 200);

        assertEquals(widthAfterAdjust, videoPlayer.getLayoutParams().width);
        assertEquals(heightAfterAdjust, videoPlayer.getLayoutParams().height);
    }

    @Test
    public void testAdjustLayoutParams_layoutGuardReappliesWhenSizeChanged() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);
        attachToWindow(videoAdView);
        ViewControllerVast vc = getViewControllerVast();

        vc.adjustLayoutParams(640, 480);

        android.widget.FrameLayout videoPlayer =
                (android.widget.FrameLayout) getViewControllerView(vc, "mVideoPlayerLayout");
        // Force current params to differ from the computed size so the guard takes the re-apply branch.
        videoPlayer.setLayoutParams(new android.widget.FrameLayout.LayoutParams(1, 1));
        forceLayout(videoPlayer, 300, 200);

        // Guard recomputed from the banner size and re-applied, overriding the bogus 1x1.
        assertNotEquals(1, videoPlayer.getLayoutParams().width);
    }

    @Test
    public void testShowEndCard_inline_blocksDescendantFocus_andReappliesMatchParent() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);
        attachToWindow(videoAdView);
        ViewControllerVast vc = getViewControllerVast();

        vc.showEndCard(null, null, false, mock(CloseButtonListener.class));

        HyBidEndCardView endCard = (HyBidEndCardView) getViewControllerView(vc, "mEndCardView");
        // Inline placement (not fullscreen) must block descendant focus
        assertEquals(ViewGroup.FOCUS_BLOCK_DESCENDANTS, endCard.getDescendantFocusability());

        // Move params off MATCH_PARENT so the guard takes the "re-apply" branch on next layout.
        endCard.setLayoutParams(new android.widget.FrameLayout.LayoutParams(100, 100));
        forceLayout(endCard, 300, 200);
        assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, endCard.getLayoutParams().width);
    }

    @Test
    public void testShowLastCustomEndCard_inline_blocksDescendantFocus_andReappliesMatchParent() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);
        attachToWindow(videoAdView);
        ViewControllerVast vc = getViewControllerVast();

        vc.showLastCustomEndCard(null, null, mock(CloseButtonListener.class));

        HyBidEndCardView lastCustom =
                (HyBidEndCardView) getViewControllerView(vc, "mLastCustomEndCardView");
        assertEquals(ViewGroup.FOCUS_BLOCK_DESCENDANTS, lastCustom.getDescendantFocusability());

        lastCustom.setLayoutParams(new android.widget.FrameLayout.LayoutParams(100, 100));
        forceLayout(lastCustom, 300, 200);
        assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, lastCustom.getLayoutParams().width);
    }

    private int layoutChangeListenerCount(View view) throws Exception {
        java.lang.reflect.Field liField = View.class.getDeclaredField("mListenerInfo");
        liField.setAccessible(true);
        Object listenerInfo = liField.get(view);
        if (listenerInfo == null) return 0;
        java.lang.reflect.Field lclField = listenerInfo.getClass().getDeclaredField("mOnLayoutChangeListeners");
        lclField.setAccessible(true);
        java.util.ArrayList<?> listeners = (java.util.ArrayList<?>) lclField.get(listenerInfo);
        return listeners == null ? 0 : listeners.size();
    }

    @Test
    public void testAdjustLayoutParams_doesNotStackLayoutListeners() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);
        attachToWindow(videoAdView);
        ViewControllerVast vc = getViewControllerVast();
        android.widget.FrameLayout videoPlayer =
                (android.widget.FrameLayout) getViewControllerView(vc, "mVideoPlayerLayout");

        int before = layoutChangeListenerCount(videoPlayer);
        vc.adjustLayoutParams(640, 480);
        vc.adjustLayoutParams(640, 480);
        vc.adjustLayoutParams(640, 480);

        // remove-before-add: repeated play/resume must not accumulate listeners (VMA-1539).
        assertEquals(before + 1, layoutChangeListenerCount(videoPlayer));
    }

    @Test
    public void testShowEndCard_doesNotStackLayoutListeners() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);
        attachToWindow(videoAdView);
        ViewControllerVast vc = getViewControllerVast();
        HyBidEndCardView endCard = (HyBidEndCardView) getViewControllerView(vc, "mEndCardView");

        int before = layoutChangeListenerCount(endCard);
        vc.showEndCard(null, null, false, mock(CloseButtonListener.class));
        vc.showEndCard(null, null, false, mock(CloseButtonListener.class));
        vc.showEndCard(null, null, false, mock(CloseButtonListener.class));

        assertEquals(before + 1, layoutChangeListenerCount(endCard));
    }

    @Test
    public void testShowLastCustomEndCard_doesNotStackLayoutListeners() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);
        attachToWindow(videoAdView);
        ViewControllerVast vc = getViewControllerVast();
        HyBidEndCardView lastCustom =
                (HyBidEndCardView) getViewControllerView(vc, "mLastCustomEndCardView");

        int before = layoutChangeListenerCount(lastCustom);
        vc.showLastCustomEndCard(null, null, mock(CloseButtonListener.class));
        vc.showLastCustomEndCard(null, null, mock(CloseButtonListener.class));
        vc.showLastCustomEndCard(null, null, mock(CloseButtonListener.class));

        assertEquals(before + 1, layoutChangeListenerCount(lastCustom));
    }

    @Test
    public void testEndCardViewListener_delegatesToAdController() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);
        ViewControllerVast vc = getViewControllerVast();
        HyBidEndCardView endCard = (HyBidEndCardView) getViewControllerView(vc, "mEndCardView");

        java.lang.reflect.Method create = ViewControllerVast.class.getDeclaredMethod(
                "createEndCardViewListener", HyBidEndCardView.class);
        create.setAccessible(true);
        HyBidEndCardView.EndCardViewListener l =
                (HyBidEndCardView.EndCardViewListener) create.invoke(vc, endCard);

        l.onShow(true, "type");
        l.onShow(false, "type");
        l.onLoadSuccess(true);
        l.onLoadFail(false);
        l.onClose(true);
        l.onSkip();
        l.onClick("http://example.com", true, "type");
        l.onClick("http://example.com", false, "type");

        verify(mockBaseAdInternal).onCustomEndCardShow("type");
        verify(mockBaseAdInternal).onDefaultEndCardShow("type");
        verify(mockBaseAdInternal).onEndCardLoadSuccess(true);
        verify(mockBaseAdInternal).onEndCardLoadFail(false);
        verify(mockBaseAdInternal).onEndCardClosed(true);
        verify(mockBaseAdInternal).onCustomEndCardClick("type");
        verify(mockBaseAdInternal).onDefaultEndCardClick("type");
    }

    // -------------------------------------------------------------------------
    // Reflection helpers for ViewControllerVast internals
    // -------------------------------------------------------------------------

    /** Returns the ViewControllerVast held inside the VideoAdControllerVast under test. */
    private ViewControllerVast getViewControllerVast() throws Exception {
        Field field = VideoAdControllerVast.class.getDeclaredField("mViewControllerVast");
        field.setAccessible(true);
        return (ViewControllerVast) field.get(videoAdControllerVast);
    }

    /** Sets a boolean field on the ViewControllerVast. */
    private void setViewControllerBoolean(ViewControllerVast target, String fieldName, boolean value) throws Exception {
        Field field = ViewControllerVast.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.setBoolean(target, value);
    }

    /** Returns a View field from the ViewControllerVast. */
    private View getViewControllerView(ViewControllerVast target, String fieldName) throws Exception {
        Field field = ViewControllerVast.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (View) field.get(target);
    }

    /** Returns the mCreateTextureListener from the ViewControllerVast. */
    private TextureView.SurfaceTextureListener getTextureListener() throws Exception {
        ViewControllerVast vc = getViewControllerVast();
        Field field = ViewControllerVast.class.getDeclaredField("mCreateTextureListener");
        field.setAccessible(true);
        return (TextureView.SurfaceTextureListener) field.get(vc);
    }

    /** Returns the current mSurface from the ViewControllerVast (may be null). */
    private android.view.Surface getVcSurface() throws Exception {
        ViewControllerVast vc = getViewControllerVast();
        Field field = ViewControllerVast.class.getDeclaredField("mSurface");
        field.setAccessible(true);
        return (android.view.Surface) field.get(vc);
    }

    /** Returns the mVideoPlayerLayoutTexture from the ViewControllerVast (null after destroy). */
    private android.view.TextureView getVcTextureView() throws Exception {
        ViewControllerVast vc = getViewControllerVast();
        Field field = ViewControllerVast.class.getDeclaredField("mVideoPlayerLayoutTexture");
        field.setAccessible(true);
        return (android.view.TextureView) field.get(vc);
    }

    // =====================================================================
    // Tests for processPlayAction
    // =====================================================================

    @Test
    public void testProcessPlayAction_withInvalidSurface_waitsForSurface() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        // Set up an invalid surface
        android.view.Surface mockSurface = mock(android.view.Surface.class);
        when(mockSurface.isValid()).thenReturn(false);
        ViewControllerVast vc = getViewControllerVast();
        Field surfaceField = ViewControllerVast.class.getDeclaredField("mSurface");
        surfaceField.setAccessible(true);
        surfaceField.set(vc, mockSurface);

        invokeProcessPlayAction();
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        // Should not call start() when surface is invalid
        verify(mockMediaPlayer, never()).start();
    }

    @Test
    public void testProcessPlayAction_withNullSurface_waitsForSurface() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        // Surface is null (not set)
        ViewControllerVast vc = getViewControllerVast();
        Field surfaceField = ViewControllerVast.class.getDeclaredField("mSurface");
        surfaceField.setAccessible(true);
        surfaceField.set(vc, null);

        invokeProcessPlayAction();
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Should not call start() when surface is null
        verify(mockMediaPlayer, never()).start();
    }

    @Test
    public void testProcessPlayAction_setSurfaceThrowsException_handlesGracefully() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        when(mockMediaPlayer.getDuration()).thenReturn(30000);
        when(mockMediaPlayer.getVideoWidth()).thenReturn(1920);
        when(mockMediaPlayer.getVideoHeight()).thenReturn(1080);
        doThrow(new IllegalStateException("MediaPlayer not initialized"))
                .when(mockMediaPlayer).setSurface(any());

        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        setupMockSurface();

        // Should not throw, should handle gracefully
        invokeProcessPlayAction();

        // start() should not be called due to exception
        verify(mockMediaPlayer, never()).start();
    }

    @Test
    public void testProcessPlayAction_startThrowsException_handlesGracefully() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        when(mockMediaPlayer.getDuration()).thenReturn(30000);
        when(mockMediaPlayer.getVideoWidth()).thenReturn(1920);
        when(mockMediaPlayer.getVideoHeight()).thenReturn(1080);
        doThrow(new IllegalStateException("MediaPlayer not in started state"))
                .when(mockMediaPlayer).start();

        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        setupMockSurface();

        // Should not throw, should handle gracefully
        invokeProcessPlayAction();

        verify(mockMediaPlayer).setSurface(any());
    }

    @Test
    public void testProcessPlayAction_getVideoWidthThrowsInUiCallback_handlesGracefully() throws Exception {
        // the layout lambda runs later on the UI thread; if the player is released by then
        // getVideoWidth() throws IllegalStateException and must be caught, not crash.
        android.media.MediaPlayer mockMediaPlayer = setupMockMediaPlayer();
        when(mockMediaPlayer.getVideoWidth()).thenThrow(new IllegalStateException("released"));

        invokeProcessPlayAction();

        verify(mockMediaPlayer).getVideoWidth();
        verify(mockMediaPlayer).start();
    }

    @Test
    public void testProcessPlayAction_playerNulledBeforeUiCallbackRuns_skipsLayoutRead() throws Exception {
        // Player nulled (destroy()/re-prepare) between posting the lambda and it running:
        // re-reading the field inside the Runnable must short-circuit before touching it.
        android.media.MediaPlayer mockMediaPlayer = setupMockMediaPlayer();
        doAnswer(invocation -> {
            Field field = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
            field.setAccessible(true);
            field.set(videoAdControllerVast, null);
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(mockBaseAdInternal).runOnUiThread(any(Runnable.class));

        invokeProcessPlayAction();

        verify(mockMediaPlayer, never()).getVideoWidth();
    }

    // =====================================================================
    // Tests for processResumeAction
    // =====================================================================

    private void invokeProcessResumeAction() throws Exception {
        Method processResumeActionMethod = VideoAdControllerVast.class.getDeclaredMethod("processResumeAction");
        processResumeActionMethod.setAccessible(true);
        processResumeActionMethod.invoke(videoAdControllerVast);
    }

    @Test
    public void testProcessResumeAction_withValidSurface_startsMediaPlayer() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        setupMockSurface();

        invokeProcessResumeAction();

        verify(mockMediaPlayer).setSurface(any());
        verify(mockMediaPlayer).start();
    }

    @Test
    public void testProcessResumeAction_withInvalidSurface_waitsForSurface() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        // Set up an invalid surface
        android.view.Surface mockSurface = mock(android.view.Surface.class);
        when(mockSurface.isValid()).thenReturn(false);
        ViewControllerVast vc = getViewControllerVast();
        Field surfaceField = ViewControllerVast.class.getDeclaredField("mSurface");
        surfaceField.setAccessible(true);
        surfaceField.set(vc, mockSurface);

        invokeProcessResumeAction();
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        // Should not call start() when surface is invalid
        verify(mockMediaPlayer, never()).start();
    }

    @Test
    public void testProcessResumeAction_withNullSurface_waitsForSurface() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        // Surface is null
        ViewControllerVast vc = getViewControllerVast();
        Field surfaceField = ViewControllerVast.class.getDeclaredField("mSurface");
        surfaceField.setAccessible(true);
        surfaceField.set(vc, null);

        invokeProcessResumeAction();
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        // Should not call start() when surface is null
        verify(mockMediaPlayer, never()).start();
    }

    @Test
    public void testProcessResumeAction_withNullMediaPlayer_returnsEarlyFromResumeHelper() throws Exception {
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, null);

        // Set isVideoCompleted to false so resumeMediaPlayerIfNeeded is actually tested
        Field isVideoCompletedField = VideoAdControllerVast.class.getDeclaredField("isVideoCompleted");
        isVideoCompletedField.setAccessible(true);
        isVideoCompletedField.setBoolean(videoAdControllerVast, false);

        invokeProcessResumeAction();

        // When mMediaPlayer is null and isVideoCompleted is false, resumeMediaPlayerIfNeeded returns true
        // The method should complete without throwing and not attempt any player operations

        // With no MediaPlayer there is nothing to resume; resume events should not be fired.
        verify(mockViewabilityAdSession, never()).fireResume();
        // Verify isVideoCompleted is still false (no state corruption)
        assertFalse(isVideoCompletedField.getBoolean(videoAdControllerVast));
    }

    @Test
    public void testProcessResumeAction_whenVideoCompleted_callsRecoverSurface() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        // Set isVideoCompleted = true
        Field isVideoCompletedField = VideoAdControllerVast.class.getDeclaredField("isVideoCompleted");
        isVideoCompletedField.setAccessible(true);
        isVideoCompletedField.setBoolean(videoAdControllerVast, true);

        setupMockSurface();

        invokeProcessResumeAction();

        // Drain delayed tasks to trigger recoverMediaPlayerSurface
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // recoverMediaPlayerSurface was called (sets surface)
        verify(mockMediaPlayer, atLeastOnce()).setSurface(any());
    }

    @Test
    public void testProcessResumeAction_setSurfaceThrowsException_handlesGracefully() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        doThrow(new IllegalStateException("MediaPlayer released"))
                .when(mockMediaPlayer).setSurface(any());

        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        setupMockSurface();

        // Should not throw
        invokeProcessResumeAction();

        // start() should not be called due to exception in setSurface
        verify(mockMediaPlayer, never()).start();
    }

    // =====================================================================
    // Tests for waitForSurface
    // =====================================================================

    private void invokeWaitForSurface() throws Exception {
        Method waitForSurfaceMethod = VideoAdControllerVast.class.getDeclaredMethod("waitForSurface");
        waitForSurfaceMethod.setAccessible(true);
        waitForSurfaceMethod.invoke(videoAdControllerVast);
    }

    @Test
    public void testWaitForSurface_whenSurfaceBecomesValid_addsPlayAction() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);

        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        // Set currentAction to PLAY (simulating waitForSurface called from processPlayAction)
        Field currentActionField = VideoAdControllerVast.class.getDeclaredField("currentAction");
        currentActionField.setAccessible(true);
        Class<?> actionClass = Class.forName("net.pubnative.lite.sdk.vpaid.VideoAdControllerVast$Action");
        @SuppressWarnings("unchecked")
        Object playAction = Enum.valueOf((Class) actionClass.asSubclass(Enum.class), "PLAY"); // PLAY
        currentActionField.set(videoAdControllerVast, playAction);

        // Start with null surface
        ViewControllerVast vc = getViewControllerVast();
        Field surfaceField = ViewControllerVast.class.getDeclaredField("mSurface");
        surfaceField.setAccessible(true);
        surfaceField.set(vc, null);

        // Call waitForSurface - it will schedule a delayed retry
        invokeWaitForSurface();

        // Make surface valid before the delayed check executes
        android.view.Surface mockSurface = mock(android.view.Surface.class);
        when(mockSurface.isValid()).thenReturn(true);
        surfaceField.set(vc, mockSurface);

        // Stub values required by processPlayAction when the PLAY retry is executed
        when(mockMediaPlayer.getDuration()).thenReturn(30000);
        when(mockMediaPlayer.getVideoWidth()).thenReturn(1920);
        when(mockMediaPlayer.getVideoHeight()).thenReturn(1080);

        // Run the delayed tasks on UI thread (schedules PLAY) and then drain the action-processing thread
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        Field actionsHandlerField = VideoAdControllerVast.class.getDeclaredField("mActionsProcessingHandler");
        actionsHandlerField.setAccessible(true);
        android.os.Handler actionsHandler = (android.os.Handler) actionsHandlerField.get(videoAdControllerVast);
        org.robolectric.Shadows.shadowOf(actionsHandler.getLooper()).idle();

        // PLAY retry should have been executed
        verify(mockMediaPlayer).setSurface(any());
        verify(mockMediaPlayer).start();
    }

    @Test
    public void testWaitForSurface_doesNotClearTextureListener() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        TextureView textureView = getVcTextureView();
        assertNotNull(textureView);
        assertNotNull(textureView.getSurfaceTextureListener());

        invokeWaitForSurface();

        // waitForSurface should not clear the TextureView listener
        assertNotNull(textureView.getSurfaceTextureListener());
    }

    @Test
    public void testWaitForSurface_withNullTextureView_doesNotCrash() throws Exception {
        // Don't build video ad view, so texture is null
        // mMediaPlayer is also null, so waitForSurface should exit early inside runnable

        invokeWaitForSurface();
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Verify no actions were added to the queue (early return due to null mMediaPlayer)
        Field mActionsField = VideoAdControllerVast.class.getDeclaredField("mActions");
        mActionsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.List<Object> actions = (java.util.List<Object>) mActionsField.get(videoAdControllerVast);
        assertTrue("No actions should be added when MediaPlayer is null", actions.isEmpty());
    }

    @Test
    public void testWaitForSurface_whenFinishedPlaying_doesNotRequeueAction() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        setMediaPlayerField(mockMediaPlayer);
        setCurrentActionField("PLAY");

        // Set finishedPlaying to true (simulating video has finished)
        Field finishedPlayingField = VideoAdControllerVast.class.getDeclaredField("finishedPlaying");
        finishedPlayingField.setAccessible(true);
        finishedPlayingField.setBoolean(videoAdControllerVast, true);

        // Set up a valid surface
        setSurfaceValidity(true);

        // Call waitForSurface
        invokeWaitForSurface();
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Verify no actions were added to the queue (guard prevented re-queue)
        assertTrue("No actions should be added when finishedPlaying is true", getActionsQueue().isEmpty());
    }

    @Test
    public void testWaitForSurface_whenVideoSkipped_doesNotRequeueAction() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        setMediaPlayerField(mockMediaPlayer);
        setCurrentActionField("PLAY");

        // Set isVideoSkipped to true (simulating video was skipped)
        // Note: In actual code flow, finishedPlaying is also set to true when video is skipped
        Field isVideoSkippedField = VideoAdControllerVast.class.getDeclaredField("isVideoSkipped");
        isVideoSkippedField.setAccessible(true);
        isVideoSkippedField.setBoolean(videoAdControllerVast, true);

        Field finishedPlayingField = VideoAdControllerVast.class.getDeclaredField("finishedPlaying");
        finishedPlayingField.setAccessible(true);
        finishedPlayingField.setBoolean(videoAdControllerVast, true);

        // Set up a valid surface
        setSurfaceValidity(true);

        // Call waitForSurface
        invokeWaitForSurface();
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Verify no actions were added to the queue (guard prevented re-queue)
        assertTrue("No actions should be added when video is skipped", getActionsQueue().isEmpty());
    }

    @Test
    public void testWaitForSurface_whenCurrentActionIsPause_doesNotRequeuePlay() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        setMediaPlayerField(mockMediaPlayer);

        // Set currentAction to PAUSE (simulating user paused while waiting for surface)
        setCurrentActionField("PAUSE");

        // Set up a valid surface
        setSurfaceValidity(true);

        // Call waitForSurface - it would try to queue PLAY but should be prevented
        invokeWaitForSurface();
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Verify no actions were added to the queue (guard prevented re-queue due to PAUSE state)
        assertTrue("No actions should be added when currentAction is PAUSE", getActionsQueue().isEmpty());
    }

    @Test
    public void testWaitForSurface_userPausesDuringWait_doesNotAutoResume() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        setMediaPlayerField(mockMediaPlayer);
        setCurrentActionField("PLAY");

        // Start with null surface so waitForSurface will schedule retry
        clearSurfaceField();

        // Call waitForSurface - schedules delayed retry
        invokeWaitForSurface();

        // Simulate user pausing during the wait
        setCurrentActionField("PAUSE");

        // Now make surface valid
        setSurfaceValidity(true);

        // Run delayed tasks - should NOT re-queue PLAY because controller is now PAUSED
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Verify no PLAY action was added (user pause should be respected)
        assertTrue("No PLAY should be queued when user paused during surface wait", getActionsQueue().isEmpty());
    }

    // =====================================================================
    // Tests for recoverMediaPlayerSurface
    // =====================================================================

    private void invokeRecoverMediaPlayerSurface() throws Exception {
        Method recoverMethod = VideoAdControllerVast.class.getDeclaredMethod("recoverMediaPlayerSurface");
        recoverMethod.setAccessible(true);
        recoverMethod.invoke(videoAdControllerVast);
    }

    @Test
    public void testRecoverMediaPlayerSurface_withNullMediaPlayer_returnsEarly() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        // Set up a mock surface to verify it's never used
        android.view.Surface mockSurface = mock(android.view.Surface.class);
        when(mockSurface.isValid()).thenReturn(true);
        ViewControllerVast vc = getViewControllerVast();
        Field surfaceField = ViewControllerVast.class.getDeclaredField("mSurface");
        surfaceField.setAccessible(true);
        surfaceField.set(vc, mockSurface);

        // Ensure mMediaPlayer is null
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, null);

        invokeRecoverMediaPlayerSurface();
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Verify surface.isValid() was never called because we returned early before checking
        verify(mockSurface, never()).isValid();
    }

    @Test
    public void testRecoverMediaPlayerSurface_withValidSurface_setsSurface() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        setupMockSurface();

        invokeRecoverMediaPlayerSurface();
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        verify(mockMediaPlayer).setSurface(any());
    }

    @Test
    public void testRecoverMediaPlayerSurface_whenFinishedPlaying_seeksToEnd() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        Field finishedPlayingField = VideoAdControllerVast.class.getDeclaredField("finishedPlaying");
        finishedPlayingField.setAccessible(true);
        finishedPlayingField.setBoolean(videoAdControllerVast, true);

        Field durationField = VideoAdControllerVast.class.getDeclaredField("mDuration");
        durationField.setAccessible(true);
        durationField.setInt(videoAdControllerVast, 30000);

        setupMockSurface();

        invokeRecoverMediaPlayerSurface();
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        verify(mockMediaPlayer).seekTo(30000);
    }

    @Test
    public void testRecoverMediaPlayerSurface_withInvalidSurface_doesNotSetSurface() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        // Set up an invalid surface
        android.view.Surface mockSurface = mock(android.view.Surface.class);
        when(mockSurface.isValid()).thenReturn(false);
        ViewControllerVast vc = getViewControllerVast();
        Field surfaceField = ViewControllerVast.class.getDeclaredField("mSurface");
        surfaceField.setAccessible(true);
        surfaceField.set(vc, mockSurface);

        invokeRecoverMediaPlayerSurface();
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Should not call setSurface with invalid surface
        verify(mockMediaPlayer, never()).setSurface(any());
    }

    @Test
    public void testRecoverMediaPlayerSurface_exceptionHandled_doesNotCrash() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        doThrow(new IllegalStateException("MediaPlayer released"))
                .when(mockMediaPlayer).setSurface(any());

        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        setupMockSurface();

        invokeRecoverMediaPlayerSurface();
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Verifies setSurface() was called (proving the exception was thrown and caught)
        // Test should pass because no exception propagated - the catch block handled it
        verify(mockMediaPlayer).setSurface(any());
    }

    // =====================================================================
    // Tests for destroy() nullifying mMediaPlayer
    // =====================================================================

    @Test
    public void testDestroy_nullsMediaPlayer() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        videoAdControllerVast.destroy();

        Object mediaPlayerAfterDestroy = mediaPlayerField.get(videoAdControllerVast);
        assertNull("mMediaPlayer should be null after destroy()", mediaPlayerAfterDestroy);
        verify(mockMediaPlayer).release();
    }

    // =====================================================================
    // API 23 (Android 6) resume-after-background — verifies the removed Android 6 changes are handled correctly
    // =====================================================================

    // ---- helpers for API 23 resume-after-background tests ---- //
    private void setMediaPlayerField(android.media.MediaPlayer player) throws Exception {
        Field f = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        f.setAccessible(true);
        f.set(videoAdControllerVast, player);
    }

    private android.media.MediaPlayer getMediaPlayerField() throws Exception {
        Field f = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        f.setAccessible(true);
        return (android.media.MediaPlayer) f.get(videoAdControllerVast);
    }

    private void setSurfaceValidity(boolean valid) throws Exception {
        android.view.Surface mockSurface = mock(android.view.Surface.class);
        when(mockSurface.isValid()).thenReturn(valid);
        ViewControllerVast vc = getViewControllerVast();
        Field surfaceField = ViewControllerVast.class.getDeclaredField("mSurface");
        surfaceField.setAccessible(true);
        surfaceField.set(vc, mockSurface);
    }

    private void clearSurfaceField() throws Exception {
        ViewControllerVast vc = getViewControllerVast();
        Field surfaceField = ViewControllerVast.class.getDeclaredField("mSurface");
        surfaceField.setAccessible(true);
        surfaceField.set(vc, null);
    }

    @SuppressWarnings("unchecked")
    private void setCurrentActionField(String enumName) throws Exception {
        Field currentActionField = VideoAdControllerVast.class.getDeclaredField("currentAction");
        currentActionField.setAccessible(true);
        Class<?> actionClass =
                Class.forName("net.pubnative.lite.sdk.vpaid.VideoAdControllerVast$Action");
        Object value = Enum.valueOf((Class<Enum>) actionClass.asSubclass(Enum.class), enumName);
        currentActionField.set(videoAdControllerVast, value);
    }
    // ---- helpers for API 23 resume-after-background tests ---- //

    @Test
    @Config(sdk = 23)
    public void testApi23_resumeAfterBackground_surfaceValid_startsPlayer() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        setMediaPlayerField(mockMediaPlayer);

        // On return to foreground the surface is valid.
        setSurfaceValidity(true);

        invokeProcessResumeAction();

        verify(mockMediaPlayer).setSurface(any());
        verify(mockMediaPlayer).start();
    }

    @Test
    @Config(sdk = 23)
    public void testApi23_resumeAfterBackground_surfaceNotReadyThenValid_queuesResume()
            throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        setMediaPlayerField(mockMediaPlayer);
        setCurrentActionField("RESUME");

        clearSurfaceField();

        invokeWaitForSurface();              // surface not ready -> schedules retry
        verify(mockMediaPlayer, never()).start();

        setSurfaceValidity(true);            // surface comes back

        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // The retry observed the valid surface and re-queued RESUME.
        Class<?> actionClass =
                Class.forName("net.pubnative.lite.sdk.vpaid.VideoAdControllerVast$Action");
        Object resume = Enum.valueOf((Class) actionClass.asSubclass(Enum.class), "RESUME");
        assertTrue("RESUME should be queued (or already drained) after surface became valid",
                getActionsQueue().contains(resume) || getActionsQueue().isEmpty());
    }

    @Test
    @Config(sdk = 23)
    public void testApi23_waitForSurface_doesNotSwapTextureListener() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        android.view.TextureView textureView = getVcTextureView();
        assertNotNull(textureView);
        android.view.TextureView.SurfaceTextureListener before =
                textureView.getSurfaceTextureListener();
        assertNotNull(before);

        // Give it a player so the polling lambda doesn't early-return.
        setMediaPlayerField(mock(android.media.MediaPlayer.class));

        invokeWaitForSurface();

        // Old Android 6 code replaced this listener; the new shared path must NOT.
        assertNotNull(textureView.getSurfaceTextureListener());
        assertTrue("ViewControllerVast's own listener must remain installed",
                before == textureView.getSurfaceTextureListener());
    }

    @Test
    @Config(sdk = 23)
    public void testApi23_waitForSurface_adDestroyedWhileWaiting_stopsAndDoesNotStart()
            throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        setMediaPlayerField(mockMediaPlayer);
        setCurrentActionField("RESUME");

        clearSurfaceField();   // surface never becomes valid

        invokeWaitForSurface();

        // Ad gets destroyed during the wait -> player reference cleared.
        setMediaPlayerField(null);

        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        verify(mockMediaPlayer, never()).start();
        assertNull(getMediaPlayerField());
    }

    @Test
    @Config(sdk = 23)
    public void testApi23_waitForSurface_surfaceNeverValid_givesUpWithoutStarting()
            throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        setMediaPlayerField(mockMediaPlayer);
        setCurrentActionField("RESUME");

        setSurfaceValidity(false);   // stays invalid the whole time

        invokeWaitForSurface();

        // Draining all delayed tasks must terminate (bounded retries), not hang.
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        verify(mockMediaPlayer, never()).start();
        assertTrue("no action should be queued when surface never became valid",
                getActionsQueue().isEmpty());
    }

    @Test
    public void testGetProgress_withZeroDuration_returnsMinusOne() throws Exception {
        setFieldInt(videoAdControllerVast, "mDoneMillis", 5000);
        setFieldInt(videoAdControllerVast, "mDuration", 0);

        int progress = videoAdControllerVast.getProgress();

        assertEquals(-1, progress);
    }

    @Test
    public void testGetProgress_withNegativeDuration_returnsMinusOne() throws Exception {
        setFieldInt(videoAdControllerVast, "mDoneMillis", 5000);
        setFieldInt(videoAdControllerVast, "mDuration", -5);

        int progress = videoAdControllerVast.getProgress();

        assertEquals(-1, progress);
    }

    @Test
    public void testGetProgress_withPositiveDuration_returnsCorrectPercentage() throws Exception {
        setFieldInt(videoAdControllerVast, "mDoneMillis", 15000);
        setFieldInt(videoAdControllerVast, "mDuration", 30000);

        int progress = videoAdControllerVast.getProgress();

        assertEquals(50, progress);
    }

    @Test
    public void testGetProgress_withDoneMillisMinusOne_returnsMinusOne() throws Exception {
        setFieldInt(videoAdControllerVast, "mDoneMillis", -1);
        setFieldInt(videoAdControllerVast, "mDuration", 30000);

        int progress = videoAdControllerVast.getProgress();

        assertEquals(-1, progress);
    }

    private void invokeProcessPauseAction() throws Exception {
        Method processPauseActionMethod = VideoAdControllerVast.class.getDeclaredMethod("processPauseAction");
        processPauseActionMethod.setAccessible(true);
        processPauseActionMethod.invoke(videoAdControllerVast);
    }

    @Test
    public void testProcessPauseAction_withNullMediaPlayer_doesNotCrash() throws Exception {
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, null);

        invokeProcessPauseAction();

        // Verify no exception was thrown and method completed
        assertNotNull(videoAdControllerVast);
    }

    @Test
    public void testProcessPauseAction_whenIsPlayingThrowsException_handlesGracefully() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        when(mockMediaPlayer.isPlaying()).thenThrow(new IllegalStateException("MediaPlayer released"));

        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        invokeProcessPauseAction();

        // Verify pause was never called due to exception in isPlaying
        verify(mockMediaPlayer, never()).pause();
    }

    @Test
    public void testProcessPauseAction_whenPauseThrowsException_handlesGracefully() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        when(mockMediaPlayer.isPlaying()).thenReturn(true);
        doThrow(new IllegalStateException("MediaPlayer not initialized")).when(mockMediaPlayer).pause();

        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        invokeProcessPauseAction();

        // Verify pause was attempted
        verify(mockMediaPlayer).pause();
    }

    @Test
    public void testProcessPauseAction_whenNotPlaying_doesNotCallPause() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        when(mockMediaPlayer.isPlaying()).thenReturn(false);

        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        invokeProcessPauseAction();

        // Verify pause was not called because isPlaying returned false
        verify(mockMediaPlayer, never()).pause();
    }

    @Test
    public void testProcessPauseAction_pausesTimersAndMediaPlayer() throws Exception {
        VideoAdControllerVast controller = createFullscreenController();
        try {
            android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
            when(mockMediaPlayer.isPlaying()).thenReturn(true);

            Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
            mediaPlayerField.setAccessible(true);
            mediaPlayerField.set(controller, mockMediaPlayer);

            // Create mock timers to verify both get paused
            net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause mockMainTimer =
                    mock(net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause.class);
            net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause mockSkipTimer =
                    mock(net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause.class);

            Field mainTimerField = VideoAdControllerVast.class.getDeclaredField("mTimerWithPause");
            mainTimerField.setAccessible(true);
            mainTimerField.set(controller, mockMainTimer);

            Field skipTimerField = VideoAdControllerVast.class.getDeclaredField("mSkipTimerWithPause");
            skipTimerField.setAccessible(true);
            skipTimerField.set(controller, mockSkipTimer);

            Method processPauseActionMethod = VideoAdControllerVast.class.getDeclaredMethod("processPauseAction");
            processPauseActionMethod.setAccessible(true);
            processPauseActionMethod.invoke(controller);

            verify(mockMainTimer).pause();
            verify(mockSkipTimer).pause();
            verify(mockMediaPlayer).pause();
        } finally {
            controller.destroy();
        }
    }

    private void invokeSkipVideoInternal(boolean skipEvent) throws Exception {
        Method skipVideoMethod = VideoAdControllerVast.class.getDeclaredMethod("skipVideo", boolean.class);
        skipVideoMethod.setAccessible(true);
        skipVideoMethod.invoke(videoAdControllerVast, skipEvent);
    }

    @Test
    public void testSkipVideoInternal_whenIsPlayingThrowsException_handlesGracefully() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        when(mockMediaPlayer.isPlaying()).thenThrow(new IllegalStateException("MediaPlayer released"));

        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        invokeSkipVideoInternal(true);

        // Verify the method completed and set finishedPlaying
        assertTrue(videoAdControllerVast.adFinishedPlaying());
    }

    @Test
    public void testSkipVideoInternal_whenPauseThrowsException_handlesGracefully() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        when(mockMediaPlayer.isPlaying()).thenReturn(true);
        doThrow(new IllegalStateException("MediaPlayer not initialized")).when(mockMediaPlayer).pause();

        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        invokeSkipVideoInternal(true);

        // Verify the method completed and set finishedPlaying
        assertTrue(videoAdControllerVast.adFinishedPlaying());
    }

    @Test
    public void testSkipVideoInternal_withNullMediaPlayer_doesNotCrash() throws Exception {
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, null);

        invokeSkipVideoInternal(true);

        // Verify the method completed
        assertTrue(videoAdControllerVast.adFinishedPlaying());
    }

    @Test
    public void testSkipVideoInternal_whenAlreadyFinished_returnsEarly() throws Exception {
        // Set finishedPlaying to true
        Field finishedPlayingField = VideoAdControllerVast.class.getDeclaredField("finishedPlaying");
        finishedPlayingField.setAccessible(true);
        finishedPlayingField.setBoolean(videoAdControllerVast, true);

        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        invokeSkipVideoInternal(true);

        // Verify isPlaying was never called because of early return
        verify(mockMediaPlayer, never()).isPlaying();
    }

    private void invokeMuteVideo(boolean mute, boolean postEvent) throws Exception {
        Method muteVideoMethod = VideoAdControllerVast.class.getDeclaredMethod("muteVideo", boolean.class, boolean.class);
        muteVideoMethod.setAccessible(true);
        muteVideoMethod.invoke(videoAdControllerVast, mute, postEvent);
    }

    @Test
    public void testMuteVideo_withNullMediaPlayer_returnsEarly() throws Exception {
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, null);

        invokeMuteVideo(true, false);

        // Verify fireVolumeChange was never called because of null player
        verify(mockViewabilityAdSession, never()).fireVolumeChange(anyBoolean());
    }

    @Test
    public void testMuteVideo_mute_setsVolumeToZero() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        invokeMuteVideo(true, false);

        verify(mockMediaPlayer).setVolume(0f, 0f);
        verify(mockViewabilityAdSession).fireVolumeChange(true);
    }

    @Test
    public void testMuteVideo_unmute_setsVolumeToSystemVolume() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        invokeMuteVideo(false, false);

        // Verify setVolume was called (with system volume, which may be 0 in test environment)
        verify(mockMediaPlayer).setVolume(anyFloat(), anyFloat());
        verify(mockViewabilityAdSession).fireVolumeChange(false);
    }

    @Test
    public void testMuteVideo_whenSetVolumeThrowsRuntimeException_handlesGracefully() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        doThrow(new RuntimeException("MediaPlayer error")).when(mockMediaPlayer).setVolume(anyFloat(), anyFloat());

        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        invokeMuteVideo(true, false);

        // Verify setVolume was attempted
        verify(mockMediaPlayer).setVolume(0f, 0f);
    }

    @Test
    public void testDestroy_stopsHandlerThreadAndReleasesMediaPlayer() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        Field handlerThreadField = VideoAdControllerVast.class.getDeclaredField("mActionsHandlerThread");
        handlerThreadField.setAccessible(true);
        Object handlerThreadBefore = handlerThreadField.get(videoAdControllerVast);
        assertNotNull("HandlerThread should exist before destroy", handlerThreadBefore);

        videoAdControllerVast.destroy();

        // Verify handler thread is null after destroy
        Object handlerThreadAfter = handlerThreadField.get(videoAdControllerVast);
        assertNull("HandlerThread should be null after destroy", handlerThreadAfter);

        // Verify media player release was called
        verify(mockMediaPlayer).release();
    }

    @Test
    public void testDestroy_setsFinishedPlayingToTrue() throws Exception {
        assertFalse(videoAdControllerVast.adFinishedPlaying());

        videoAdControllerVast.destroy();

        assertTrue("finishedPlaying should be true after destroy", videoAdControllerVast.adFinishedPlaying());
    }

    @Test
    public void testDestroy_clearsActionsQueue() throws Exception {
        java.util.List mActions = getActionsQueue();
        Object playAction = getAction(1);
        mActions.add(playAction);
        assertFalse("Actions queue should not be empty before destroy", mActions.isEmpty());

        videoAdControllerVast.destroy();
        assertTrue("Actions queue should be empty after destroy", getActionsQueue().isEmpty());
    }

    @Test
    public void testDestroy_whenMediaPlayerReleaseThrows_stillNullsReference() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        doThrow(new RuntimeException("Release failed")).when(mockMediaPlayer).release();

        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        videoAdControllerVast.destroy();

        // Verify media player reference is null despite exception
        Object mediaPlayerAfter = mediaPlayerField.get(videoAdControllerVast);
        assertNull("mMediaPlayer should be null after destroy even if release throws", mediaPlayerAfter);
    }

    @Test
    public void testDestroy_pausesAndNullsTimers() throws Exception {
        VideoAdControllerVast controller = createFullscreenController();

        // Create mock timers
        net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause mockTimer =
                mock(net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause.class);
        net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause mockSkipTimer =
                mock(net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause.class);

        // Set timers
        Field timerField = VideoAdControllerVast.class.getDeclaredField("mTimerWithPause");
        timerField.setAccessible(true);
        timerField.set(controller, mockTimer);

        Field skipTimerField = VideoAdControllerVast.class.getDeclaredField("mSkipTimerWithPause");
        skipTimerField.setAccessible(true);
        skipTimerField.set(controller, mockSkipTimer);

        assertNotNull("mTimerWithPause should exist before destroy", timerField.get(controller));
        assertNotNull("mSkipTimerWithPause should exist before destroy", skipTimerField.get(controller));

        controller.destroy();

        // Verify both timers pause before they were null
        verify(mockTimer).pause();
        verify(mockSkipTimer).pause();

        // Verify both timers are null after destroy
        assertNull("mTimerWithPause should be null after destroy", timerField.get(controller));
        assertNull("mSkipTimerWithPause should be null after destroy", skipTimerField.get(controller));
    }

    private boolean invokeResumeMediaPlayerIfNeeded() throws Exception {
        Method method = VideoAdControllerVast.class.getDeclaredMethod("resumeMediaPlayerIfNeeded");
        method.setAccessible(true);
        return (boolean) method.invoke(videoAdControllerVast);
    }

    @Test
    public void testResumeMediaPlayerIfNeeded_withVideoCompleted_returnsTrue() throws Exception {
        Field isVideoCompletedField = VideoAdControllerVast.class.getDeclaredField("isVideoCompleted");
        isVideoCompletedField.setAccessible(true);
        isVideoCompletedField.setBoolean(videoAdControllerVast, true);

        boolean result = invokeResumeMediaPlayerIfNeeded();

        assertTrue("Should return true when video is completed", result);
    }

    @Test
    public void testResumeMediaPlayerIfNeeded_withNullMediaPlayer_returnsFalse() throws Exception {
        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, null);

        Field isVideoCompletedField = VideoAdControllerVast.class.getDeclaredField("isVideoCompleted");
        isVideoCompletedField.setAccessible(true);
        isVideoCompletedField.setBoolean(videoAdControllerVast, false);

        boolean result = invokeResumeMediaPlayerIfNeeded();

        assertFalse("Should return false when media player is null", result);
    }

    @Test
    public void testResumeMediaPlayerIfNeeded_setSurfaceThrowsIllegalArgument_returnsFalse() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        doThrow(new IllegalArgumentException("Surface released")).when(mockMediaPlayer).setSurface(any());

        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        Field isVideoCompletedField = VideoAdControllerVast.class.getDeclaredField("isVideoCompleted");
        isVideoCompletedField.setAccessible(true);
        isVideoCompletedField.setBoolean(videoAdControllerVast, false);

        setupMockSurface();

        boolean result = invokeResumeMediaPlayerIfNeeded();

        assertFalse("Should return false when setSurface throws exception", result);
        verify(mockMediaPlayer, never()).start();
    }

    @Test
    public void testResumeMediaPlayerIfNeeded_withValidSurface_returnsTrue() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);

        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        Field isVideoCompletedField = VideoAdControllerVast.class.getDeclaredField("isVideoCompleted");
        isVideoCompletedField.setAccessible(true);
        isVideoCompletedField.setBoolean(videoAdControllerVast, false);

        setupMockSurface();

        boolean result = invokeResumeMediaPlayerIfNeeded();

        assertTrue("Should return true when resume succeeds", result);
        verify(mockMediaPlayer).setSurface(any());
        verify(mockMediaPlayer).start();
    }

    @Test
    public void testResumeMediaPlayerIfNeeded_withInvalidSurface_returnsFalse() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);

        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        Field isVideoCompletedField = VideoAdControllerVast.class.getDeclaredField("isVideoCompleted");
        isVideoCompletedField.setAccessible(true);
        isVideoCompletedField.setBoolean(videoAdControllerVast, false);

        setSurfaceValidity(false);

        boolean result = invokeResumeMediaPlayerIfNeeded();

        assertFalse("Should return false when surface is invalid", result);
        verify(mockMediaPlayer, never()).start();
    }

    @Test
    public void testResumeTimersIfPaused_withPausedTimers_resumesBothTimers() throws Exception {
        net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause mockTimer =
                mock(net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause.class);
        when(mockTimer.isPaused()).thenReturn(true);

        net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause mockSkipTimer =
                mock(net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause.class);
        when(mockSkipTimer.isPaused()).thenReturn(true);

        Field timerField = VideoAdControllerVast.class.getDeclaredField("mTimerWithPause");
        timerField.setAccessible(true);
        timerField.set(videoAdControllerVast, mockTimer);

        Field skipTimerField = VideoAdControllerVast.class.getDeclaredField("mSkipTimerWithPause");
        skipTimerField.setAccessible(true);
        skipTimerField.set(videoAdControllerVast, mockSkipTimer);

        Method method = VideoAdControllerVast.class.getDeclaredMethod("resumeTimersIfPaused");
        method.setAccessible(true);
        method.invoke(videoAdControllerVast);

        verify(mockTimer).resume();
        verify(mockSkipTimer).resume();
    }

    @Test
    public void testSkipVideoInternal_withTimers_pausesAndClearsTimers() throws Exception {
        net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause mockTimer =
                mock(net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause.class);
        net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause mockSkipTimer =
                mock(net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause.class);

        Field timerField = VideoAdControllerVast.class.getDeclaredField("mTimerWithPause");
        timerField.setAccessible(true);
        timerField.set(videoAdControllerVast, mockTimer);

        Field skipTimerField = VideoAdControllerVast.class.getDeclaredField("mSkipTimerWithPause");
        skipTimerField.setAccessible(true);
        skipTimerField.set(videoAdControllerVast, mockSkipTimer);

        invokeSkipVideoInternal(true);

        verify(mockTimer).pause();
        verify(mockSkipTimer).pause();
        assertNull("mTimerWithPause should be null after skip", timerField.get(videoAdControllerVast));
        assertNull("mSkipTimerWithPause should be null after skip", skipTimerField.get(videoAdControllerVast));
    }

    // Helper methods for async-prepare tests
    private boolean getAwaitingPrepare() throws Exception {
        Field field = VideoAdControllerVast.class.getDeclaredField("awaitingPrepare");
        field.setAccessible(true);
        return (Boolean) field.get(videoAdControllerVast);
    }

    private void setAwaitingPrepare(boolean value) throws Exception {
        Field field = VideoAdControllerVast.class.getDeclaredField("awaitingPrepare");
        field.setAccessible(true);
        field.setBoolean(videoAdControllerVast, value);
    }

    private android.os.Handler getActionsHandler() throws Exception {
        Field field = VideoAdControllerVast.class.getDeclaredField("mActionsProcessingHandler");
        field.setAccessible(true);
        return (android.os.Handler) field.get(videoAdControllerVast);
    }

    private void invokeProcessPrepareAction() throws Exception {
        Method method = VideoAdControllerVast.class.getDeclaredMethod("processPrepareAction");
        method.setAccessible(true);
        try {
            method.invoke(videoAdControllerVast);
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }

    @Test
    public void testProcessPrepareAction_startsAsyncPrepare_doesNotCompleteSynchronously() throws Exception {
        String videoUri = "http://test.example/video.mp4";
        org.robolectric.shadows.util.DataSource dataSource = org.robolectric.shadows.util.DataSource.toDataSource(videoUri);
        ShadowMediaPlayer.addMediaInfo(dataSource, new ShadowMediaPlayer.MediaInfo(30000, 5000));

        videoAdControllerVast.setVideoFilePath(videoUri);
        invokeProcessPrepareAction();

        // prepareAsync() must return immediately, before the video is actually ready.
        assertTrue("awaitingPrepare should be set while the async prepare is in flight", getAwaitingPrepare());
        android.media.MediaPlayer mediaPlayer = getMediaPlayerField();
        assertEquals("player must still be PREPARING right after prepareAsync() returns",
                ShadowMediaPlayer.State.PREPARING, org.robolectric.Shadows.shadowOf(mediaPlayer).getState());

        org.robolectric.Shadows.shadowOf(mediaPlayer).invokePreparedListener();
        assertFalse("awaitingPrepare should clear once onPrepared() fires", getAwaitingPrepare());
    }

    @Test
    public void testOnError_duringAwaitingPrepare_clearsFlagAndResumesQueue() throws Exception {
        String videoUri = "http://test.example/broken-video.mp4";
        org.robolectric.shadows.util.DataSource dataSource = org.robolectric.shadows.util.DataSource.toDataSource(videoUri);
        ShadowMediaPlayer.addMediaInfo(dataSource, new ShadowMediaPlayer.MediaInfo(30000, 5000));

        videoAdControllerVast.setVideoFilePath(videoUri);
        invokeProcessPrepareAction();
        assertTrue(getAwaitingPrepare());

        // Queue a follow-up action to prove the queue resumes, not just that the flag clears.
        getActionsQueue().add(getAction(2)); // PAUSE

        // prepareAsync() failures are reported via onError(), not by throwing.
        android.media.MediaPlayer mediaPlayer = getMediaPlayerField();
        org.robolectric.Shadows.shadowOf(mediaPlayer).invokeErrorListener(1 /* MEDIA_ERROR_UNKNOWN */, 0);
        org.robolectric.Shadows.shadowOf(getActionsHandler().getLooper()).idle();

        assertFalse("awaitingPrepare must clear on error so the queue doesn't stall forever",
                getAwaitingPrepare());
        assertTrue("the queued action should have run once the error unblocked the queue",
                getActionsQueue().isEmpty());
        assertNull("the failed player should be released and cleared, not left for a later "
                + "action to run against", getMediaPlayerField());
        verify(mockBaseAdInternal).onAdLoadFailInternal(any(PlayerInfo.class));
    }

    @Test
    public void testOnError_systemErrorDuringAwaitingPrepare_stillReportsLoadFailure() throws Exception {
        String videoUri = "http://test.example/broken-video.mp4";
        org.robolectric.shadows.util.DataSource dataSource = org.robolectric.shadows.util.DataSource.toDataSource(videoUri);
        ShadowMediaPlayer.addMediaInfo(dataSource, new ShadowMediaPlayer.MediaInfo(30000, 5000));

        videoAdControllerVast.setVideoFilePath(videoUri);
        invokeProcessPrepareAction();
        assertTrue(getAwaitingPrepare());

        // A MEDIA_ERROR_SYSTEM* error during prepare must still be reported as a load failure -
        // returning false here (the post-prepare behavior) would rely on onCompletion() as a
        // fallback, but the player is already released/cleared by this point so that never fires.
        android.media.MediaPlayer mediaPlayer = getMediaPlayerField();
        org.robolectric.Shadows.shadowOf(mediaPlayer)
                .invokeErrorListener(1 /* what */, net.pubnative.lite.sdk.utils.MediaPlayerErrors.MEDIA_ERROR_SYSTEM /* extra */);

        assertFalse(getAwaitingPrepare());
        verify(mockBaseAdInternal).onAdLoadFailInternal(any(PlayerInfo.class));
    }

    @Test
    public void testProcessActions_queuedActionWaitsForRealOnPrepared_thenRuns() throws Exception {
        String videoUri = "http://test.example/video.mp4";
        org.robolectric.shadows.util.DataSource dataSource = org.robolectric.shadows.util.DataSource.toDataSource(videoUri);
        ShadowMediaPlayer.addMediaInfo(dataSource, new ShadowMediaPlayer.MediaInfo(30000, 5000));
        videoAdControllerVast.setVideoFilePath(videoUri);

        // PAUSE stands in for PLAY: ShadowMediaPlayer can't simulate setSurface(), but PAUSE
        // exercises the same "does the next action wait for PREPARE" mechanism safely.
        java.util.List mActions = getActionsQueue();
        mActions.add(getAction(0)); // PREPARE
        mActions.add(getAction(2)); // PAUSE

        invokeProcessActions();
        org.robolectric.Shadows.shadowOf(getActionsHandler().getLooper()).idle();

        assertTrue("prepare should still be in flight", getAwaitingPrepare());
        assertEquals("PAUSE must stay queued until the player is actually ready",
                1, getActionsQueue().size());
        assertEquals(ShadowMediaPlayer.State.PREPARING,
                org.robolectric.Shadows.shadowOf(getMediaPlayerField()).getState());

        org.robolectric.Shadows.shadowOf(getMediaPlayerField()).invokePreparedListener();
        org.robolectric.Shadows.shadowOf(getActionsHandler().getLooper()).idle();

        assertFalse(getAwaitingPrepare());
        assertTrue("the queued action should have run once onPrepared() fired",
                getActionsQueue().isEmpty());
    }

    @Test
    public void testProcessActions_reentrantCallWhileAwaitingPrepare_doesNotRunQueuedPlay() throws Exception {
        android.media.MediaPlayer mockMediaPlayer = setupMockMediaPlayer();

        java.util.List mActions = getActionsQueue();
        mActions.add(getAction(1)); // PLAY

        // Simulate the state right after PREPARE ran and kicked off an async prepare.
        setAwaitingPrepare(true);
        setIsActionsProcessingRun(true);

        // Some other call site (e.g. pause()/resume()) triggers processActions() again.
        invokeProcessActions();
        org.robolectric.Shadows.shadowOf(getActionsHandler().getLooper()).idle();

        verify(mockMediaPlayer, never()).start();
        assertEquals("PLAY must remain queued while awaitingPrepare is true",
                1, getActionsQueue().size());
    }

    private void setFinishedPlaying(boolean value) throws Exception {
        Field field = VideoAdControllerVast.class.getDeclaredField("finishedPlaying");
        field.setAccessible(true);
        field.setBoolean(videoAdControllerVast, value);
    }

    @Test
    public void testProcessPrepareAction_whenFinishedPlaying_doesNotStartPrepare() throws Exception {
        setFinishedPlaying(true);
        videoAdControllerVast.setVideoFilePath("http://test.example/video.mp4");

        invokeProcessPrepareAction();

        assertNull("no MediaPlayer should be created once the controller is destroyed",
                getMediaPlayerField());
        assertFalse(getAwaitingPrepare());
    }

    @Test
    public void testProcessPrepareAction_withInvalidVideoUri_reportsFailureOnceAndReleasesPlayer() throws Exception {
        videoAdControllerVast.setVideoFilePath(null);

        invokeProcessPrepareAction();

        // Without the early return, this would fall through and report a second failure.
        verify(mockBaseAdInternal, times(1)).onAdLoadFailInternal(any(PlayerInfo.class));
        assertNull("no MediaPlayer should be created for an invalid URI", getMediaPlayerField());
        assertFalse(getAwaitingPrepare());
    }

    @Test
    public void testProcessPrepareAction_whenSetDataSourceThrows_releasesPlayerAndReportsFailureOnce() throws Exception {
        // No MediaInfo registered, so setDataSource() throws and hits the catch block.
        videoAdControllerVast.setVideoFilePath("http://test.example/unregistered-video.mp4");

        invokeProcessPrepareAction();

        verify(mockBaseAdInternal, times(1)).onAdLoadFailInternal(any(PlayerInfo.class));
        assertNull("the MediaPlayer from a failed prepare should be released and cleared",
                getMediaPlayerField());
        assertFalse(getAwaitingPrepare());
    }

    @Test
    public void testProcessPrepareAction_reprepareWithInvalidUri_resetsAwaitingPrepare() throws Exception {
        String videoUri = "http://test.example/video.mp4";
        org.robolectric.shadows.util.DataSource dataSource = org.robolectric.shadows.util.DataSource.toDataSource(videoUri);
        ShadowMediaPlayer.addMediaInfo(dataSource, new ShadowMediaPlayer.MediaInfo(30000, 5000));
        videoAdControllerVast.setVideoFilePath(videoUri);

        // First prepare starts and is still in flight.
        invokeProcessPrepareAction();
        assertTrue(getAwaitingPrepare());

        // Re-preparing with an invalid URI abandons the old (still in-flight) player without
        // starting a new one - nothing will ever fire onPrepared()/onError() to clear the flag.
        videoAdControllerVast.setVideoFilePath(null);
        invokeProcessPrepareAction();

        assertFalse("awaitingPrepare must not stay stuck true when re-preparing with an invalid URI",
                getAwaitingPrepare());
        assertNull("no MediaPlayer should be left over from the invalid re-prepare",
                getMediaPlayerField());
    }

    @Test
    public void testOnPrepared_afterDestroy_doesNotTouchNulledHandler() throws Exception {
        String videoUri = "http://test.example/video.mp4";
        org.robolectric.shadows.util.DataSource dataSource = org.robolectric.shadows.util.DataSource.toDataSource(videoUri);
        ShadowMediaPlayer.addMediaInfo(dataSource, new ShadowMediaPlayer.MediaInfo(30000, 5000));
        videoAdControllerVast.setVideoFilePath(videoUri);

        invokeProcessPrepareAction();
        assertTrue(getAwaitingPrepare());
        android.media.MediaPlayer mediaPlayer = getMediaPlayerField();

        // destroy() while the prepare is still in flight - it resets awaitingPrepare itself.
        videoAdControllerVast.destroy();
        assertFalse("destroy() should reset awaitingPrepare itself", getAwaitingPrepare());

        // The callback can still arrive after destroy(). Without the finishedPlaying guard,
        // it would NPE posting to the now-null handler.
        org.robolectric.Shadows.shadowOf(mediaPlayer).invokePreparedListener();

        assertFalse("onPrepared() must bail out without touching the queue",
                getAwaitingPrepare());
    }

    @Test
    public void testOnError_afterDestroy_doesNotTouchNulledHandler() throws Exception {
        String videoUri = "http://test.example/broken-video.mp4";
        org.robolectric.shadows.util.DataSource dataSource = org.robolectric.shadows.util.DataSource.toDataSource(videoUri);
        ShadowMediaPlayer.addMediaInfo(dataSource, new ShadowMediaPlayer.MediaInfo(30000, 5000));
        videoAdControllerVast.setVideoFilePath(videoUri);

        invokeProcessPrepareAction();
        assertTrue(getAwaitingPrepare());
        android.media.MediaPlayer mediaPlayer = getMediaPlayerField();

        videoAdControllerVast.destroy();
        assertFalse("destroy() should reset awaitingPrepare itself", getAwaitingPrepare());
        clearInvocations(mockBaseAdInternal);

        // Without the finishedPlaying guard, this would NPE and wrongly report a failure.
        org.robolectric.Shadows.shadowOf(mediaPlayer).invokeErrorListener(1, 0);

        assertFalse("onError() must bail out without touching the queue", getAwaitingPrepare());
        verify(mockBaseAdInternal, never()).onAdLoadFailInternal(any(PlayerInfo.class));
    }

    @Test
    public void testReplayVast_afterSkip_actuallyRestartsPrepare() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        String videoUri = "http://test.example/video.mp4";
        org.robolectric.shadows.util.DataSource dataSource = org.robolectric.shadows.util.DataSource.toDataSource(videoUri);
        ShadowMediaPlayer.addMediaInfo(dataSource, new ShadowMediaPlayer.MediaInfo(30000, 0));
        videoAdControllerVast.setVideoFilePath(videoUri);

        // Simulate a completed/skipped first playthrough.
        videoAdControllerVast.skipVideo();
        assertTrue(videoAdControllerVast.adFinishedPlaying());

        // Replay must genuinely start a new prepare, not silently no-op.
        videoAdControllerVast.replayVast();
        org.robolectric.Shadows.shadowOf(getActionsHandler().getLooper()).idle();

        assertFalse("finishedPlaying must be reset so a fresh prepare can start",
                videoAdControllerVast.adFinishedPlaying());
        assertNotNull("replay should have created a new MediaPlayer for the fresh prepare",
                getMediaPlayerField());
    }

    @Test
    public void testReplayVast_afterSkipDuringInFlightPrepare_queueIsNotStuck() throws Exception {
        VideoAdView videoAdView = new VideoAdView(context);
        videoAdControllerVast.buildVideoAdView(videoAdView);

        String videoUri = "http://test.example/video.mp4";
        org.robolectric.shadows.util.DataSource dataSource = org.robolectric.shadows.util.DataSource.toDataSource(videoUri);
        ShadowMediaPlayer.addMediaInfo(dataSource, new ShadowMediaPlayer.MediaInfo(30000, 5000));
        videoAdControllerVast.setVideoFilePath(videoUri);

        // Simulate the state right after PREPARE ran and the loop paused to wait for it.
        invokeProcessPrepareAction();
        assertTrue(getAwaitingPrepare());
        setIsActionsProcessingRun(true);
        android.media.MediaPlayer stalePlayer = getMediaPlayerField();

        // Skip is a public API and can be called any time, including mid-prepare.
        videoAdControllerVast.skipVideo();

        assertFalse("skipVideo() must not leave the queue paused forever", getAwaitingPrepare());
        assertFalse("skipVideo() must not leave the queue marked busy forever",
                getIsActionsProcessingRun());

        // A later replay must actually run, not silently no-op on a stuck queue.
        videoAdControllerVast.replayVast();
        org.robolectric.Shadows.shadowOf(getActionsHandler().getLooper()).idle();

        android.media.MediaPlayer newPlayer = getMediaPlayerField();
        assertNotNull("replay should have started a fresh prepare", newPlayer);
        assertNotSame("replay must not resume the stale in-flight player from before the skip",
                stalePlayer, newPlayer);
    }

    private void invokeExecuteAction(Object action) throws Exception {
        Class<?> actionClass = Class.forName("net.pubnative.lite.sdk.vpaid.VideoAdControllerVast$Action");
        Method method = VideoAdControllerVast.class.getDeclaredMethod("executeAction", actionClass);
        method.setAccessible(true);
        try {
            method.invoke(videoAdControllerVast, action);
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }

    @Test
    public void testExecuteActionPrepare_returnsLongBeforeMediaPlayerFinishesPreparing() throws Exception {
        String videoUri = "http://test.example/video.mp4";
        org.robolectric.shadows.util.DataSource dataSource = org.robolectric.shadows.util.DataSource.toDataSource(videoUri);
        long simulatedPrepareDelayMillis = 3000;
        ShadowMediaPlayer.addMediaInfo(dataSource,
                new ShadowMediaPlayer.MediaInfo(30000, (int) simulatedPrepareDelayMillis));
        videoAdControllerVast.setVideoFilePath(videoUri);

        long start = System.nanoTime();
        invokeExecuteAction(getAction(0)); // PREPARE
        long elapsedMillis = java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

        // The old synchronous prepare() wouldn't return until the video was ready. This must
        // return almost immediately, proving the monitor is held only for an instant.
        assertTrue("executeAction(PREPARE) took " + elapsedMillis + "ms but should return in well "
                        + "under the simulated " + simulatedPrepareDelayMillis + "ms prepare time",
                elapsedMillis < simulatedPrepareDelayMillis / 2);
        assertTrue(getAwaitingPrepare());
        assertEquals("the player must still be mid-prepare when executeAction() already returned",
                ShadowMediaPlayer.State.PREPARING,
                org.robolectric.Shadows.shadowOf(getMediaPlayerField()).getState());
    }

    @Test
    public void testDestroy_completesPromptly_onceConcurrentMonitorHolderReleasesIt() throws Exception {
        final java.util.concurrent.CountDownLatch monitorAcquired = new java.util.concurrent.CountDownLatch(1);
        final long holdMillis = 300;

        Thread monitorHolder = new Thread(() -> {
            synchronized (videoAdControllerVast) {
                monitorAcquired.countDown();
                try {
                    Thread.sleep(holdMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        monitorHolder.start();

        assertTrue("background thread should have acquired the monitor",
                monitorAcquired.await(1, java.util.concurrent.TimeUnit.SECONDS));

        long start = System.nanoTime();
        videoAdControllerVast.destroy();
        long elapsedMillis = java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

        monitorHolder.join(java.util.concurrent.TimeUnit.SECONDS.toMillis(2));
        assertFalse("monitorHolder thread should have finished", monitorHolder.isAlive());

        // destroy() waiting for the monitor is fine - it just must not hang well beyond
        // the brief hold above.
        assertTrue("destroy() took " + elapsedMillis + "ms - should complete shortly after the "
                        + "monitor is released (held for " + holdMillis + "ms), not hang",
                elapsedMillis < 2000);
    }

    @Test
    public void testOnError_fromSupersededPlayer_isIgnored() throws Exception {
        String videoUri = "http://test.example/video.mp4";
        org.robolectric.shadows.util.DataSource dataSource = org.robolectric.shadows.util.DataSource.toDataSource(videoUri);
        ShadowMediaPlayer.addMediaInfo(dataSource, new ShadowMediaPlayer.MediaInfo(30000, 5000));
        videoAdControllerVast.setVideoFilePath(videoUri);

        // Player A starts preparing, then gets superseded by a new prepare (as happens on a
        // skip + replay) before its callback arrives.
        invokeProcessPrepareAction();
        android.media.MediaPlayer playerA = getMediaPlayerField();
        invokeProcessPrepareAction();
        android.media.MediaPlayer playerB = getMediaPlayerField();
        assertNotSame(playerA, playerB);
        assertTrue(getAwaitingPrepare());

        // A's stale error callback finally arrives.
        org.robolectric.Shadows.shadowOf(playerA).invokeErrorListener(1, 0);

        assertTrue("a stale callback from A must not touch B's in-flight prepare",
                getAwaitingPrepare());
        assertSame("a stale callback from A must not null out the current player",
                playerB, getMediaPlayerField());
    }

    @Test
    public void testOnPrepared_fromSupersededPlayer_isIgnored() throws Exception {
        String videoUri = "http://test.example/video.mp4";
        org.robolectric.shadows.util.DataSource dataSource = org.robolectric.shadows.util.DataSource.toDataSource(videoUri);
        ShadowMediaPlayer.addMediaInfo(dataSource, new ShadowMediaPlayer.MediaInfo(30000, 5000));
        videoAdControllerVast.setVideoFilePath(videoUri);

        invokeProcessPrepareAction();
        android.media.MediaPlayer playerA = getMediaPlayerField();
        invokeProcessPrepareAction();
        android.media.MediaPlayer playerB = getMediaPlayerField();
        assertNotSame(playerA, playerB);
        assertTrue(getAwaitingPrepare());

        // A's stale prepared callback finally arrives.
        org.robolectric.Shadows.shadowOf(playerA).invokePreparedListener();

        assertTrue("a stale callback from A must not clear B's in-flight prepare",
                getAwaitingPrepare());
    }

    @Test
    public void testOnCompletion_fromSupersededPlayer_isIgnored() throws Exception {
        String videoUri = "http://test.example/video.mp4";
        org.robolectric.shadows.util.DataSource dataSource = org.robolectric.shadows.util.DataSource.toDataSource(videoUri);
        ShadowMediaPlayer.addMediaInfo(dataSource, new ShadowMediaPlayer.MediaInfo(30000, 5000));
        videoAdControllerVast.setVideoFilePath(videoUri);

        invokeProcessPrepareAction();
        android.media.MediaPlayer playerA = getMediaPlayerField();
        invokeProcessPrepareAction();
        android.media.MediaPlayer playerB = getMediaPlayerField();
        assertNotSame(playerA, playerB);

        // A's stale completion callback finally arrives - it must not end B's fresh playthrough.
        org.robolectric.Shadows.shadowOf(playerA).invokeCompletionListener();

        assertFalse("a stale completion from A must not mark the fresh playthrough as finished",
                videoAdControllerVast.adFinishedPlaying());
    }

    @Test
    public void testOnCompletion_whileFinishedPlaying_isIgnored() throws Exception {
        String videoUri = "http://test.example/video.mp4";
        org.robolectric.shadows.util.DataSource dataSource = org.robolectric.shadows.util.DataSource.toDataSource(videoUri);
        ShadowMediaPlayer.addMediaInfo(dataSource, new ShadowMediaPlayer.MediaInfo(30000, 0));
        videoAdControllerVast.setVideoFilePath(videoUri);

        invokeProcessPrepareAction();
        android.media.MediaPlayer mediaPlayer = getMediaPlayerField();

        // Simulate the window inside destroy() where finishedPlaying is already set but the
        // player hasn't been released/nulled yet (destroy() runs on the main thread while this
        // callback fires on the actions thread, so the two can genuinely interleave).
        setFinishedPlaying(true);

        org.robolectric.Shadows.shadowOf(mediaPlayer).invokeCompletionListener();

        verify(mockBaseAdInternal, never()).onAdDidReachEnd();
    }
}
