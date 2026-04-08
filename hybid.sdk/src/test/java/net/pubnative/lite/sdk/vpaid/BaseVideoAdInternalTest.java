// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.CustomCTAData;
import net.pubnative.lite.sdk.models.EndCardData;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.utils.AdCustomCTAManager;
import net.pubnative.lite.sdk.utils.AdEndCardManager;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityManager;
import net.pubnative.lite.sdk.vpaid.enums.AdState;
import net.pubnative.lite.sdk.vpaid.enums.VastError;
import net.pubnative.lite.sdk.vpaid.helpers.AssetsLoader;
import net.pubnative.lite.sdk.vpaid.helpers.ErrorLog;
import net.pubnative.lite.sdk.vpaid.helpers.SimpleTimer;
import net.pubnative.lite.sdk.vpaid.models.vpaid.AdSpotDimensions;
import net.pubnative.lite.sdk.vpaid.response.AdParams;
import net.pubnative.lite.sdk.vpaid.response.VastProcessor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;
import java.lang.reflect.Method;

@RunWith(RobolectricTestRunner.class)
public class BaseVideoAdInternalTest {

    @Mock private Context mockContext;
    @Mock private Ad mockAd;
    @Mock private AdPresenter.ImpressionListener mockImpressionListener;
    @Mock private AdCloseButtonListener mockAdCloseButtonListener;
    @Mock private VideoAdListener mockVideoAdListener;
    @Mock private CloseButtonListener mockCloseButtonListener;
    @Mock private VideoAdCacheItem mockCacheItem;
    @Mock private AdParams mockAdParams;
    @Mock private AdSpotDimensions mockAdSpotDimensions;
    @Mock private CustomCTAData mockCustomCTAData;
    @Mock private EndCardData mockEndCardData;
    @Mock private EndCardData mockCustomEndCardData;
    @Mock private Bitmap mockBitmap;
    @Mock private HyBidViewabilityManager mockViewabilityManager;

    private MockedStatic<TextUtils> mockedTextUtils;
    private MockedStatic<ErrorLog> mockedErrorLog;
    private MockedStatic<AdCustomCTAManager> mockedAdCustomCTAManager;
    private MockedStatic<AdEndCardManager> mockedAdEndCardManager;
    private MockedStatic<HyBid> mockedHyBid;
    private MockedStatic<Logger> mockedLogger;

    private TestVideoAd videoAd;

    // Concrete implementation for testing the abstract class
    private static class TestVideoAd extends BaseVideoAdInternal {
        TestVideoAd(Context context, Ad ad, boolean isInterstitial, boolean isFullscreen,
                    AdPresenter.ImpressionListener impressionListener, AdCloseButtonListener adCloseButtonListener) throws Exception {
            super(context, ad, isInterstitial, isFullscreen, impressionListener, adCloseButtonListener);
        }
        @Override void dismiss() {}
        @Override AdSpotDimensions getAdSpotDimensions() { return mock(AdSpotDimensions.class); }
        @Override int getAdFormat() { return 0; }
    }

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockedTextUtils = mockStatic(TextUtils.class);
        mockedErrorLog = mockStatic(ErrorLog.class);
        mockedAdCustomCTAManager = mockStatic(AdCustomCTAManager.class);
        mockedAdEndCardManager = mockStatic(AdEndCardManager.class);
        mockedHyBid = mockStatic(HyBid.class);
        mockedLogger = mockStatic(Logger.class);

        // Default mock behaviors
        when(mockAd.getVast()).thenReturn("<vast>valid</vast>");
        when(TextUtils.isEmpty(any())).thenAnswer(invocation -> {
            CharSequence s = invocation.getArgument(0);
            return s == null || s.length() == 0;
        });
        when(AdCustomCTAManager.isAbleShow(any())).thenReturn(false);
        when(AdCustomCTAManager.getCustomCtaDelay(any())).thenReturn(5);
        when(AdEndCardManager.shouldShowEndcard(any())).thenReturn(false);
        when(AdEndCardManager.shouldShowCustomEndcard(any())).thenReturn(false);
        when(HyBid.getViewabilityManager()).thenReturn(mockViewabilityManager);
    }

    @After
    public void tearDown() {
        mockedTextUtils.close();
        mockedErrorLog.close();
        mockedAdCustomCTAManager.close();
        mockedAdEndCardManager.close();
        mockedHyBid.close();
        mockedLogger.close();
    }

    // Constructor Tests
    @Test(expected = HyBidError.class)
    public void constructor_withNullContext_throwsError() throws Exception {
        new TestVideoAd(null, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
    }

    @Test(expected = HyBidError.class)
    public void constructor_withEmptyVast_throwsError() throws Exception {
        when(mockAd.getVast()).thenReturn("");
        new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
    }

    @Test(expected = HyBidError.class)
    public void constructor_withNullVast_throwsError() throws Exception {
        when(mockAd.getVast()).thenReturn(null);
        new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
    }

    @Test
    public void constructor_withValidParams_initializesCorrectly() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, true, true, mockImpressionListener, mockAdCloseButtonListener);

        assertEquals("Context should be stored", mockContext, videoAd.getContext());
        assertEquals("Ad should be stored", mockAd, videoAd.getAd());
        assertEquals("Should be interstitial", true, videoAd.isInterstitial());
        assertEquals("Initial state should be NONE", AdState.NONE, videoAd.getAdState());
        assertFalse("Should not be ready initially", videoAd.isReady());
        assertFalse("Should not be rewarded initially", videoAd.isRewarded());
    }

    @Test
    public void constructor_withRewardedAd_usesRewardedSkipOffset() throws Exception {
        when(mockAd.getVideoRewardedSkipOffset()).thenReturn(10);
        when(mockAd.getVideoSkipOffset()).thenReturn(5);

        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setRewarded(true);

        TestVideoAd rewardedAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener) {
            @Override
            boolean isRewarded() {
                return true;
            }
        };

        assertNotNull("Viewability session should be created", rewardedAd.getViewabilityAdSession());
        verify(mockAd).getVideoRewardedSkipOffset();
    }

    // Getter/Setter Tests
    @Test
    public void setAdListener_storesListener() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);

        videoAd.setAdListener(mockVideoAdListener);

        assertEquals("Video ad listener should be stored", mockVideoAdListener, videoAd.getAdListener());
    }

    @Test
    public void setAdState_updatesState() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);

        videoAd.setAdState(AdState.LOADING);

        assertEquals("Ad state should be updated", AdState.LOADING, videoAd.getAdState());
    }

    @Test
    public void setRewarded_updatesRewardedFlag() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);

        videoAd.setRewarded(true);

        assertTrue("Rewarded flag should be updated", videoAd.isRewarded());
    }

    @Test
    public void setAdCloseButtonListener_storesListener() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);

        videoAd.setAdCloseButtonListener(mockCloseButtonListener);

        videoAd.onAdCloseButtonVisible();
        verify(mockCloseButtonListener).onCloseButtonVisible();
    }

    @Test
    public void setVideoCacheItem_storesCacheItem() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);

        videoAd.setVideoCacheItem(mockCacheItem);

        when(mockCacheItem.getAdParams()).thenReturn(mockAdParams);
        when(mockAdParams.isVpaid()).thenReturn(false);

        try (MockedConstruction<VideoAdControllerVast> ignored = mockConstruction(VideoAdControllerVast.class)) {
            videoAd.proceedLoad(IntegrationType.HEADER_BIDDING);
        }
    }

    @Test
    public void setReady_setsFalse() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);

        videoAd.setReady();

        assertFalse("Ready should be set to false", videoAd.isReady());
    }

    @Test
    public void getViewabilityAdSession_returnsSession() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        Object session = videoAd.getViewabilityAdSession();
        assertNotNull("Viewability session should not be null", session);
    }

    @Test
    public void getCacheItem_returnsStoredCacheItem() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setVideoCacheItem(mockCacheItem);
        assertEquals("Cache item should be returned", mockCacheItem, videoAd.getCacheItem());
    }

    @Test
    public void startFetcherTimer_startsTimerAndHandlesTimeout() throws Exception {
        final SimpleTimer.Listener[] capturedListener = new SimpleTimer.Listener[1];
        try (MockedConstruction<AssetsLoader> mockedAssetsLoader = mockConstruction(AssetsLoader.class);
             MockedConstruction<SimpleTimer> mockedTimer = mockConstruction(SimpleTimer.class, (mock, context) -> {
                 capturedListener[0] = (SimpleTimer.Listener) context.arguments().get(1);
             })) {

            videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
            videoAd.setAdListener(mockVideoAdListener);

            videoAd.startFetcherTimer();

            SimpleTimer timer = mockedTimer.constructed().get(0);
            verify(timer).start();

            // Simulate the timer finishing (timeout)
            assertNotNull(capturedListener[0]);
            capturedListener[0].onFinish();

            mockedErrorLog.verify(() -> ErrorLog.postError(eq(mockContext), any(VastError.class)));
            ShadowLooper.runUiThreadTasks();
            verify(mockVideoAdListener).onAdLoadFail(any(PlayerInfo.class));
            verify(mockedAssetsLoader.constructed().get(0)).breakLoading();
        }
    }

    @Test
    public void onAdLoadSuccess_notifiesListener() throws Exception {
        // This test verifies the primary job of the onAdLoadSuccess method.
        TestVideoAd videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(mockVideoAdListener);

        videoAd.onAdLoadSuccessInternal();
        ShadowLooper.runUiThreadTasks();

        verify(mockVideoAdListener).onAdLoadSuccess();
    }

    @Test
    public void stopExpirationTimer_cancelsTimer() throws Exception {
        try (MockedConstruction<SimpleTimer> mockedTimer = mockConstruction(SimpleTimer.class);
             // We also need the mocks from the helper method here
             MockedConstruction<VideoAdControllerVast> mockedController = mockConstruction(VideoAdControllerVast.class);
             MockedConstruction<VastProcessor> mockedProcessor = mockConstruction(VastProcessor.class);
             MockedConstruction<AssetsLoader> mockedAssetsLoader = mockConstruction(AssetsLoader.class)) {

            videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
            videoAd.setAdListener(mockVideoAdListener);

            // Call a simplified helper that simulates the full ad load success path
            triggerAdLoadSuccess_forTimerTest(mockedProcessor, mockedAssetsLoader, mockedController);

            videoAd.stopExpirationTimer();

            // The expiration timer is the first one created
            SimpleTimer timer = mockedTimer.constructed().get(0);
            verify(timer).cancel();
        }
    }

    @Test
    public void stopExpirationTimer_whenTimerExists_logsMessage() throws Exception {
        try (MockedConstruction<VideoAdControllerVast> mockedController = mockConstruction(VideoAdControllerVast.class);
             MockedConstruction<VastProcessor> mockedProcessor = mockConstruction(VastProcessor.class);
             MockedConstruction<AssetsLoader> mockedAssetsLoader = mockConstruction(AssetsLoader.class)) {

            videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
            videoAd.setAdListener(mockVideoAdListener);

            triggerAdLoadSuccess_forTimerTest(mockedProcessor, mockedAssetsLoader, mockedController);
            videoAd.stopExpirationTimer();
            mockedLogger.verify(() -> Logger.d(eq("BaseVideoAdInternal"), eq("Stop schedule expiration")));
        }
    }

    @Test
    public void startExpirationTimer_startsTimerAndHandlesFinish() throws Exception {
        final SimpleTimer.Listener[] capturedListener = new SimpleTimer.Listener[1];
        try (MockedConstruction<SimpleTimer> mockedTimer = mockConstruction(SimpleTimer.class, (mock, context) -> {
            // Capture the listener from the constructor arguments
            capturedListener[0] = (SimpleTimer.Listener) context.arguments().get(1);
        })) {
            videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
            videoAd.setAdListener(mockVideoAdListener);

            // Directly trigger the method that starts the expiration timer
            videoAd.onAdLoadSuccessInternal();
            ShadowLooper.runUiThreadTasks();

            // Verify the timer was created and started
            assertEquals(1, mockedTimer.constructed().size());
            SimpleTimer timer = mockedTimer.constructed().get(0);
            verify(timer).start();

            // Simulate the timer finishing
            assertNotNull(capturedListener[0]);
            capturedListener[0].onFinish();

            // Verify the ad expired callback was called
            verify(mockVideoAdListener).onAdExpired();
        }
    }

    @Test
    public void stopFetcherTimer_cancelsTimer() throws Exception {
        try (MockedConstruction<SimpleTimer> mockedTimer = mockConstruction(SimpleTimer.class)) {
            videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);

            videoAd.startFetcherTimer();
            videoAd.stopFetcherTimer();

            SimpleTimer timer = mockedTimer.constructed().get(0);
            verify(timer).cancel();
        }
    }

    // ProceedLoad Tests
    @Test
    public void proceedLoad_withCacheItem_callsPrepareDirectly() throws Exception {
        try (MockedConstruction<VideoAdControllerVast> ignored = mockConstruction(VideoAdControllerVast.class)) {
            videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
            when(mockCacheItem.getAdParams()).thenReturn(mockAdParams);
            when(mockAdParams.isVpaid()).thenReturn(false);
            videoAd.setVideoCacheItem(mockCacheItem);

            videoAd.proceedLoad(IntegrationType.HEADER_BIDDING);

            verify(mockCacheItem).getAdParams();
        }
    }

    @Test
    public void proceedLoad_withoutCacheItem_callsFetchAd() throws Exception {
        ArgumentCaptor<VastProcessor.Listener> listenerCaptor = ArgumentCaptor.forClass(VastProcessor.Listener.class);

        try (MockedConstruction<VastProcessor> mockedProcessor = mockConstruction(VastProcessor.class)) {
            videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);

            videoAd.proceedLoad(IntegrationType.HEADER_BIDDING);

            VastProcessor processor = mockedProcessor.constructed().get(0);
            verify(processor).parseResponse(anyString(), listenerCaptor.capture());
        }
    }

    // VPAID Tests
    @Test
    public void prepare_withVpaidAd_callsOnAdLoadFail() throws Exception {
        ArgumentCaptor<VastProcessor.Listener> listenerCaptor = ArgumentCaptor.forClass(VastProcessor.Listener.class);

        try (MockedConstruction<VastProcessor> mockedProcessor = mockConstruction(VastProcessor.class)) {
            videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
            videoAd.setAdListener(mockVideoAdListener);
            when(mockAdParams.isVpaid()).thenReturn(true);

            videoAd.proceedLoad(IntegrationType.HEADER_BIDDING);

            VastProcessor processor = mockedProcessor.constructed().get(0);
            verify(processor).parseResponse(anyString(), listenerCaptor.capture());

            listenerCaptor.getValue().onParseSuccess(mockAdParams, "vast_content");

            ShadowLooper.runUiThreadTasks();
            verify(mockVideoAdListener).onAdLoadFail(any(PlayerInfo.class));
            mockedErrorLog.verify(() -> ErrorLog.postError(eq(mockContext), any(VastError.class)));
        }
    }

    // Event Callback Tests - Complete Coverage
    @Test
    public void onAdDidReachEnd_callsListener() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(mockVideoAdListener);

        videoAd.onAdDidReachEnd();

        verify(mockVideoAdListener).onAdDidReachEnd();
    }

    @Test
    public void onAdLeaveApp_callsListener() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(mockVideoAdListener);

        videoAd.onAdLeaveApp();

        verify(mockVideoAdListener).onLeaveApp();
    }

    @Test
    public void onAdClicked_callsListener() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(mockVideoAdListener);

        videoAd.onAdClicked();

        verify(mockVideoAdListener).onAdClicked();
    }

    @Test
    public void onAdSkipped_callsListener() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(mockVideoAdListener);

        videoAd.onAdSkipped();

        verify(mockVideoAdListener).onAdSkipped();
    }

    @Test
    public void onCustomEndCardShow_callsListener() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(mockVideoAdListener);

        videoAd.onCustomEndCardShow("test_type");

        verify(mockVideoAdListener).onCustomEndCardShow("test_type");
    }

    @Test
    public void onCustomCTAClick_callsListener() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(mockVideoAdListener);

        videoAd.onCustomCTAClick(true);

        verify(mockVideoAdListener).onCustomCTACLick(true);
    }

    @Test
    public void onEndCardLoadSuccess_callsListener() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(mockVideoAdListener);

        videoAd.onEndCardLoadSuccess(true);

        verify(mockVideoAdListener).onEndCardLoadSuccess(true);
    }

    @Test
    public void onAdReplaying_callsListener() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(mockVideoAdListener);

        videoAd.onAdReplaying();

        verify(mockVideoAdListener).onReplay();
    }

    // Additional callback method tests (continuing...)
    @Test
    public void allCallbackMethods_withNullListener_doNotCrash() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        // Don't set listener - should remain null

        // All these should not crash with null listener
        videoAd.onAdDidReachEnd();
        videoAd.onAdLeaveApp();
        videoAd.onAdClicked();
        videoAd.onAdSkipped();
        videoAd.onCustomEndCardShow("test");
        videoAd.onCustomCTAClick(true);
        videoAd.onEndCardLoadSuccess(true);
        videoAd.onAdReplaying();
    }

    @Test
    public void releaseAdController_destroysController() throws Exception {
        // Set up ALL necessary mocks for the helpers in one block
        try (MockedConstruction<VideoAdControllerVast> mockedController = mockConstruction(VideoAdControllerVast.class);
             MockedConstruction<VastProcessor> mockedProcessor = mockConstruction(VastProcessor.class);
             MockedConstruction<AssetsLoader> mockedAssetsLoader = mockConstruction(AssetsLoader.class)) {

            videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);

            // This helper now runs inside the active mock context
            triggerSuccessfulAssetLoading_forControllerTest(mockedProcessor, mockedAssetsLoader);

            VideoAdControllerVast controller = mockedController.constructed().get(0);
            videoAd.releaseAdController();

            verify(controller).destroy();
            assertNull("Controller should be null after release", videoAd.getAdController());
        }
    }

    @Test
    public void runOnUiThread_executesRunnable() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        Runnable mockRunnable = mock(Runnable.class);

        videoAd.runOnUiThread(mockRunnable);
        ShadowLooper.runUiThreadTasks();

        verify(mockRunnable).run();
    }

    // Helper Methods
    private void triggerSuccessfulAssetLoading_forControllerTest(
            MockedConstruction<VastProcessor> mockedProcessor,
            MockedConstruction<AssetsLoader> mockedAssetsLoader) {

        ArgumentCaptor<VastProcessor.Listener> vastListenerCaptor = ArgumentCaptor.forClass(VastProcessor.Listener.class);
        ArgumentCaptor<AssetsLoader.OnAssetsLoaded> assetsListenerCaptor = ArgumentCaptor.forClass(AssetsLoader.OnAssetsLoaded.class);
        when(mockAdParams.isVpaid()).thenReturn(false);

        videoAd.proceedLoad(IntegrationType.HEADER_BIDDING);

        VastProcessor processor = mockedProcessor.constructed().get(0);
        verify(processor).parseResponse(anyString(), vastListenerCaptor.capture());
        vastListenerCaptor.getValue().onParseSuccess(mockAdParams, "vast_content");

        AssetsLoader assetsLoader = mockedAssetsLoader.constructed().get(0);
        verify(assetsLoader).load(any(), any(), assetsListenerCaptor.capture());
        assetsListenerCaptor.getValue().onAssetsLoaded("video_path", mockEndCardData, "endcard_path");

        ShadowLooper.runUiThreadTasks();
    }

    private void triggerAdLoadSuccess_forTimerTest(
            MockedConstruction<VastProcessor> mockedProcessor,
            MockedConstruction<AssetsLoader> mockedAssetsLoader,
            MockedConstruction<VideoAdControllerVast> mockedController) {

        videoAd.initAdLoadingStartTime();
        triggerSuccessfulAssetLoading_forControllerTest(mockedProcessor, mockedAssetsLoader);

        ArgumentCaptor<VideoAdController.OnPreparedListener> preparedListenerCaptor = ArgumentCaptor.forClass(VideoAdController.OnPreparedListener.class);
        VideoAdControllerVast controller = mockedController.constructed().get(0);
        verify(controller, atLeastOnce()).prepare(preparedListenerCaptor.capture());
        preparedListenerCaptor.getValue().onPrepared();

        ShadowLooper.runUiThreadTasks();
    }

    @Test
    public void prepareAdController_withNullController_callsOnAdLoadFail() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(mockVideoAdListener);

        videoAd.releaseAdController();

        Method method = BaseVideoAdInternal.class.getDeclaredMethod(
                "prepareAdController", String.class, EndCardData.class, String.class);
        method.setAccessible(true);
        method.invoke(videoAd, "video_path", mockEndCardData, "endcard_path");

        ShadowLooper.runUiThreadTasks();

        verify(mockVideoAdListener).onAdLoadFail(any(PlayerInfo.class));
        mockedErrorLog.verify(() -> ErrorLog.postError(eq(mockContext), any(VastError.class)));
    }

    @Test
    public void prepareAdController_withNullCustomEndCard_doesNotCrash() throws Exception {
        try (MockedConstruction<VideoAdControllerVast> ignored = mockConstruction(VideoAdControllerVast.class);
             MockedConstruction<VastProcessor> mockedProcessor = mockConstruction(VastProcessor.class);
             MockedConstruction<AssetsLoader> mockedAssetsLoader = mockConstruction(AssetsLoader.class)) {

            videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
            videoAd.setAdListener(mockVideoAdListener);
            setupCustomEndCardTest(null);
            triggerAssetLoadingWithCustomEndCard(mockedProcessor, mockedAssetsLoader);
            mockedLogger.verify(() -> Logger.d(eq("BaseVideoAdInternal"), eq("Custom end card data is null or empty")));
        }
    }

    @Test
    public void prepareAdController_withNullCustomEndCardInExtensionMode_logsMessage() throws Exception {
        try (MockedConstruction<VideoAdControllerVast> ignored = mockConstruction(VideoAdControllerVast.class);
             MockedConstruction<VastProcessor> mockedProcessor = mockConstruction(VastProcessor.class);
             MockedConstruction<AssetsLoader> mockedAssetsLoader = mockConstruction(AssetsLoader.class)) {

            videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
            videoAd.setAdListener(mockVideoAdListener);

            when(mockAdParams.isVpaid()).thenReturn(false);
            when(AdEndCardManager.shouldShowEndcard(mockAd)).thenReturn(true);
            when(AdEndCardManager.shouldShowCustomEndcard(mockAd)).thenReturn(true);
            when(mockAd.getCustomEndCard()).thenReturn(null);
            when(mockAd.getCustomEndCardDisplay()).thenReturn(net.pubnative.lite.sdk.models.CustomEndCardDisplay.EXTENSION);
            triggerAssetLoadingWithCustomEndCard(mockedProcessor, mockedAssetsLoader);
            mockedLogger.verify(() -> Logger.d(eq("BaseVideoAdInternal"), eq("Custom end card data is null or empty")));
        }
    }

    @Test
    public void prepareAdController_withNullCustomEndCardContent_doesNotCrash() throws Exception {
        try (MockedConstruction<VideoAdControllerVast> ignored = mockConstruction(VideoAdControllerVast.class);
             MockedConstruction<VastProcessor> mockedProcessor = mockConstruction(VastProcessor.class);
             MockedConstruction<AssetsLoader> mockedAssetsLoader = mockConstruction(AssetsLoader.class)) {

            videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
            videoAd.setAdListener(mockVideoAdListener);
            when(mockCustomEndCardData.getContent()).thenReturn(null);
            setupCustomEndCardTest(mockCustomEndCardData);

            triggerAssetLoadingWithCustomEndCard(mockedProcessor, mockedAssetsLoader);

            mockedLogger.verify(() -> Logger.d(eq("BaseVideoAdInternal"), eq("Custom end card data is null or empty")));
        }
    }

    @Test
    public void prepareAdController_withValidCustomEndCardExtension_addsEndCard() throws Exception {
        try (MockedConstruction<VideoAdControllerVast> mockedController = mockConstruction(VideoAdControllerVast.class);
             MockedConstruction<VastProcessor> mockedProcessor = mockConstruction(VastProcessor.class);
             MockedConstruction<AssetsLoader> mockedAssetsLoader = mockConstruction(AssetsLoader.class)) {

            videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
            videoAd.setAdListener(mockVideoAdListener);

            when(mockCustomEndCardData.getContent()).thenReturn("custom_content");
            when(mockAdParams.isVpaid()).thenReturn(false);
            when(AdEndCardManager.shouldShowEndcard(mockAd)).thenReturn(true);
            when(AdEndCardManager.shouldShowCustomEndcard(mockAd)).thenReturn(true);
            when(mockAd.getCustomEndCard()).thenReturn(mockCustomEndCardData);
            when(mockAd.getCustomEndCardDisplay()).thenReturn(net.pubnative.lite.sdk.models.CustomEndCardDisplay.EXTENSION);

            triggerAssetLoadingWithCustomEndCard(mockedProcessor, mockedAssetsLoader);

            VideoAdControllerVast controller = mockedController.constructed().get(0);
            verify(controller, times(2)).addEndCardData(any(EndCardData.class));
            verify(mockVideoAdListener).onAdCustomEndCardFound();
        }
    }

    @Test
    public void prepareAdController_withValidCustomEndCardOnly_addsEndCard() throws Exception {
        try (MockedConstruction<VideoAdControllerVast> mockedController = mockConstruction(VideoAdControllerVast.class);
             MockedConstruction<VastProcessor> mockedProcessor = mockConstruction(VastProcessor.class);
             MockedConstruction<AssetsLoader> mockedAssetsLoader = mockConstruction(AssetsLoader.class)) {

            videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
            videoAd.setAdListener(mockVideoAdListener);

            when(mockCustomEndCardData.getContent()).thenReturn("custom_content");
            when(mockAdParams.isVpaid()).thenReturn(false);
            when(AdEndCardManager.shouldShowEndcard(mockAd)).thenReturn(false);
            when(AdEndCardManager.shouldShowCustomEndcard(mockAd)).thenReturn(true);
            when(mockAd.getCustomEndCard()).thenReturn(mockCustomEndCardData);

            triggerAssetLoadingWithCustomEndCard(mockedProcessor, mockedAssetsLoader);

            VideoAdControllerVast controller = mockedController.constructed().get(0);
            verify(controller, times(1)).addEndCardData(mockCustomEndCardData);
            verify(mockVideoAdListener).onAdCustomEndCardFound();
        }
    }

    @Test
    public void prepareAdController_withInvalidCustomEndCardOnly_logsMessage() throws Exception {
        try (MockedConstruction<VideoAdControllerVast> ignored = mockConstruction(VideoAdControllerVast.class);
             MockedConstruction<VastProcessor> mockedProcessor = mockConstruction(VastProcessor.class);
             MockedConstruction<AssetsLoader> mockedAssetsLoader = mockConstruction(AssetsLoader.class)) {

            videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
            videoAd.setAdListener(mockVideoAdListener);

            when(mockCustomEndCardData.getContent()).thenReturn("");
            when(mockAdParams.isVpaid()).thenReturn(false);
            when(AdEndCardManager.shouldShowEndcard(mockAd)).thenReturn(false);
            when(AdEndCardManager.shouldShowCustomEndcard(mockAd)).thenReturn(true);
            when(mockAd.getCustomEndCard()).thenReturn(mockCustomEndCardData);

            triggerAssetLoadingWithCustomEndCard(mockedProcessor, mockedAssetsLoader);
            mockedLogger.verify(() -> Logger.d(eq("BaseVideoAdInternal"), eq("Custom end card data is null or empty")));
        }
    }

    @Test
    public void isEndCardValid_withValidEndCard_returnsTrue() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        when(mockCustomEndCardData.getContent()).thenReturn("valid_content");
        Method method = BaseVideoAdInternal.class.getDeclaredMethod("isEndCardValid", EndCardData.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(videoAd, mockCustomEndCardData);
        assertTrue("Valid endcard should return true", result);
    }


    @Test
    public void isEndCardValid_withEmptyContent_returnsFalse() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        when(mockCustomEndCardData.getContent()).thenReturn("");
        Method method = BaseVideoAdInternal.class.getDeclaredMethod("isEndCardValid", EndCardData.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(videoAd, mockCustomEndCardData);
        assertFalse("Endcard with empty content should return false", result);
    }


    @Test
    public void onDefaultEndCardShow_notifiesListener() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(mockVideoAdListener);
        videoAd.onDefaultEndCardShow("standard");
        verify(mockVideoAdListener).onDefaultEndCardShow("standard");
    }

    @Test
    public void onDefaultEndCardShow_withNullListener_doesNotCrash() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(null);
        videoAd.onDefaultEndCardShow("standard");
    }

    @Test
    public void onCustomEndCardClick_notifiesListener() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(mockVideoAdListener);
        videoAd.onCustomEndCardClick("custom");
        verify(mockVideoAdListener).onCustomEndCardClick("custom");
    }

    @Test
    public void onCustomEndCardClick_withNullListener_doesNotCrash() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(null);
        videoAd.onCustomEndCardClick("custom");
    }

    @Test
    public void onDefaultEndCardClick_notifiesListener() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(mockVideoAdListener);
        videoAd.onDefaultEndCardClick("standard");
        verify(mockVideoAdListener).onDefaultEndCardClick("standard");
    }

    @Test
    public void onDefaultEndCardClick_withNullListener_doesNotCrash() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(null);
        videoAd.onDefaultEndCardClick("standard");
    }

    @Test
    public void onCustomCTAShow_notifiesListener() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(mockVideoAdListener);
        videoAd.onCustomCTAShow();
        verify(mockVideoAdListener).onCustomCTAShow();
    }

    @Test
    public void onCustomCTAShow_withNullListener_doesNotCrash() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(null);
        videoAd.onCustomCTAShow();
    }

    @Test
    public void onCustomCTALoadFail_notifiesListener() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(mockVideoAdListener);
        videoAd.onCustomCTALoadFail();
        verify(mockVideoAdListener).onCustomCTALoadFail();
    }

    @Test
    public void onCustomCTALoadFail_withNullListener_doesNotCrash() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(null);
        videoAd.onCustomCTALoadFail();
    }

    @Test
    public void onEndCardSkipped_notifiesListener() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(mockVideoAdListener);
        videoAd.onEndCardSkipped(true);
        verify(mockVideoAdListener).onEndCardSkipped(true);
    }

    @Test
    public void onEndCardSkipped_withNullListener_doesNotCrash() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(null);
        videoAd.onEndCardSkipped(false);
    }

    @Test
    public void onEndCardClosed_notifiesListener() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(mockVideoAdListener);
        videoAd.onEndCardClosed(true);
        verify(mockVideoAdListener).onEndCardClosed(true);
    }

    @Test
    public void onEndCardClosed_withNullListener_doesNotCrash() throws Exception {
        videoAd = new TestVideoAd(mockContext, mockAd, false, false, mockImpressionListener, mockAdCloseButtonListener);
        videoAd.setAdListener(null);
        videoAd.onEndCardClosed(false);
    }


    private void setupCustomEndCardTest(EndCardData customEndCard) {
        when(mockAdParams.isVpaid()).thenReturn(false);
        when(AdEndCardManager.shouldShowCustomEndcard(mockAd)).thenReturn(true);
        when(mockAd.getCustomEndCard()).thenReturn(customEndCard);
        when(mockAd.getCustomEndCardDisplay()).thenReturn(net.pubnative.lite.sdk.models.CustomEndCardDisplay.EXTENSION);
    }

    private void triggerAssetLoadingWithCustomEndCard(
            MockedConstruction<VastProcessor> mockedProcessor,
            MockedConstruction<AssetsLoader> mockedAssetsLoader) {

        ArgumentCaptor<VastProcessor.Listener> vastListenerCaptor = ArgumentCaptor.forClass(VastProcessor.Listener.class);
        ArgumentCaptor<AssetsLoader.OnAssetsLoaded> assetsListenerCaptor = ArgumentCaptor.forClass(AssetsLoader.OnAssetsLoaded.class);

        videoAd.proceedLoad(IntegrationType.HEADER_BIDDING);

        VastProcessor processor = mockedProcessor.constructed().get(0);
        verify(processor).parseResponse(anyString(), vastListenerCaptor.capture());
        vastListenerCaptor.getValue().onParseSuccess(mockAdParams, "vast_content");

        AssetsLoader assetsLoader = mockedAssetsLoader.constructed().get(0);
        verify(assetsLoader).load(any(), any(), assetsListenerCaptor.capture());
        assetsListenerCaptor.getValue().onAssetsLoaded("video_path", mockEndCardData, "endcard_path");
        ShadowLooper.runUiThreadTasks();
    }
}