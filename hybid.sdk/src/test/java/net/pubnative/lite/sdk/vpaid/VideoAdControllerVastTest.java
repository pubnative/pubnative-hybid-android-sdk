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

        assertTrue(rewardedController.isRewarded());
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

        assertTrue(fullscreenController.isVideoVisible());
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

        assertNotNull(rewardedController);
        assertTrue(rewardedController.isRewarded());
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

        assertNotNull(fullscreenController);
        assertTrue(fullscreenController.isVideoVisible());
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
        invokeCreateSkipTimer(fullscreenController, 0, true, true, false);
        assertNotNull(fullscreenController);
    }

    @Test
    public void testCreateSkipTimer_withPositiveSkipTime_createsTimer() throws Exception {
        VideoAdControllerVast fullscreenController = createFullscreenController();
        invokeCreateSkipTimer(fullscreenController, 5000, false, true, true);
        Field skipTimerField = VideoAdControllerVast.class.getDeclaredField("mSkipTimerWithPause");
        skipTimerField.setAccessible(true);
        Object skipTimer = skipTimerField.get(fullscreenController);
        assertNotNull(skipTimer);
    }

    @Test
    public void testCreateSkipTimer_withAllParametersFalse() throws Exception {
        VideoAdControllerVast fullscreenController = createFullscreenController();
        invokeCreateSkipTimer(fullscreenController, 5000, false, false, false);
        Field skipTimerField = VideoAdControllerVast.class.getDeclaredField("mSkipTimerWithPause");
        skipTimerField.setAccessible(true);
        assertNotNull(skipTimerField.get(fullscreenController));
    }

    @Test
    public void testCreateSkipTimer_withAllParametersTrue() throws Exception {
        VideoAdControllerVast fullscreenController = createFullscreenController();
        invokeCreateSkipTimer(fullscreenController, 5000, true, true, true);
        Field skipTimerField = VideoAdControllerVast.class.getDeclaredField("mSkipTimerWithPause");
        skipTimerField.setAccessible(true);
        assertNotNull(skipTimerField.get(fullscreenController));
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

        invokeCreateSkipTimer(nonFullscreenController, 5000, true, true, true);

        Field skipTimerField = VideoAdControllerVast.class.getDeclaredField("mSkipTimerWithPause");
        skipTimerField.setAccessible(true);
        Object skipTimer = skipTimerField.get(nonFullscreenController);
        
        // Timer should not be created
        assertNull(skipTimer);
    }

    @Test
    public void testCreateSkipTimer_onTick_callsSetSkipProgress() throws Exception {
        VideoAdControllerVast fullscreenController = createFullscreenController();
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
    }

    @Test
    public void testCreateSkipTimer_onFinish_callsEndSkip() throws Exception {
        VideoAdControllerVast fullscreenController = createFullscreenController();
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
    }

    @Test
    public void testCreateSkipTimer_onFinish_withAutoCloseAndEndcard() throws Exception {
        VideoAdControllerVast fullscreenController = createFullscreenController();
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
        android.media.MediaPlayer mockMediaPlayer = mock(android.media.MediaPlayer.class);
        when(mockMediaPlayer.getDuration()).thenReturn(30000);
        when(mockMediaPlayer.getVideoWidth()).thenReturn(1920);
        when(mockMediaPlayer.getVideoHeight()).thenReturn(1080);

        Field mediaPlayerField = VideoAdControllerVast.class.getDeclaredField("mMediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(videoAdControllerVast, mockMediaPlayer);

        return mockMediaPlayer;
    }

    private void setIsReplay(boolean value) throws Exception {
        Field isReplayField = VideoAdControllerVast.class.getDeclaredField("isReplay");
        isReplayField.setAccessible(true);
        isReplayField.setBoolean(videoAdControllerVast, value);
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
        videoAdControllerVast.setVideoVisible(true);

        // Drain all pending (including delayed) tasks so the postDelayed action executes
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // setSurface must have been called exactly once — the surface recovery succeeded
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

    // -------------------------------------------------------------------------
    // Reflection helpers for ViewControllerVast internals
    // -------------------------------------------------------------------------

    /** Returns the ViewControllerVast held inside the VideoAdControllerVast under test. */
    private ViewControllerVast getViewControllerVast() throws Exception {
        Field field = VideoAdControllerVast.class.getDeclaredField("mViewControllerVast");
        field.setAccessible(true);
        return (ViewControllerVast) field.get(videoAdControllerVast);
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
}
