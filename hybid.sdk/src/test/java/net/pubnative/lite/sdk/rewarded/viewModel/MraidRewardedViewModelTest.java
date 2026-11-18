// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.viewModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedBroadcastReceiver;
import net.pubnative.lite.sdk.utils.PNLocalBroadcastManager;
import org.robolectric.Shadows;
import android.os.Looper;
import java.util.ArrayList;
import java.util.List;

import android.view.View;

import androidx.test.core.app.ApplicationProvider;

import net.pubnative.lite.sdk.AdCache;
import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.api.PNApiClient;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdData;
import net.pubnative.lite.sdk.models.AdTestModel;
import net.pubnative.lite.sdk.models.AuxiliaryAdEventType;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.views.endcard.HyBidEndCardView;
import net.pubnative.lite.sdk.mraid.model.HTMLAd;
import net.pubnative.lite.sdk.rewarded.RewardedActivityInteractor;
import net.pubnative.lite.sdk.rewarded.presenter.RewardedPresenterDecorator;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.UrlHandler;
import net.pubnative.lite.sdk.viewability.FriendlyObstructionReasonConstants;
import net.pubnative.lite.sdk.viewability.baseom.BaseFriendlyObstructionPurpose;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

@RunWith(RobolectricTestRunner.class)
public class MraidRewardedViewModelTest {

    private MraidRewardedViewModel viewModel;
    private Application application;
    private HyBidRewardedBroadcastReceiver broadcastReceiver;
    private AutoCloseable closeable;
    private Ad spiedAd;
    private UrlHandler mockUrlHandler;

    private MockedStatic<HyBid> hyBidMockedStatic;
    @Mock
    private RewardedActivityInteractor mockInteractor;
    @Mock
    private MRAIDBanner mockBanner;
    @Mock
    private MRAIDView mockMraidView;
    @Mock
    private ReportingController mockReportingController;
    @Mock
    private PNApiClient mockApiClient;

    @Before
    public void setUp() throws Exception {
        closeable = openMocks(this);
        application = ApplicationProvider.getApplicationContext();

        AdData clickBeacon = new AdData();
        clickBeacon.type = Ad.Beacon.CLICK;
        clickBeacon.data = new HashMap<>();
        clickBeacon.data.put("url", "https://click.com");

        AdData customEndCardClickBeacon = new AdData();
        customEndCardClickBeacon.type = Ad.Beacon.CUSTOM_END_CARD_CLICK;
        customEndCardClickBeacon.data = new HashMap<>();
        customEndCardClickBeacon.data.put("url", "https://customendcardclick.com");

        AdData customCtaClickBeacon = new AdData();
        customCtaClickBeacon.type = Ad.Beacon.CUSTOM_CTA_CLICK;
        customCtaClickBeacon.data = new HashMap<>();
        customCtaClickBeacon.data.put("url", "https://customctaclick.com");

        AdData customEndCardEventBeacon = new AdData();
        customEndCardEventBeacon.type = Ad.Beacon.CUSTOM_ENDCARD_EVENT;
        customEndCardEventBeacon.data = new HashMap<>();
        customEndCardEventBeacon.data.put("url", "https://customendcardevent.com?e=[EVENTTYPE]");

        spiedAd = spy(new AdTestModel());
        spiedAd.beacons = Arrays.asList(clickBeacon, customEndCardClickBeacon,
                customCtaClickBeacon, customEndCardEventBeacon);

        hyBidMockedStatic = mockStatic(HyBid.class);
        hyBidMockedStatic.when(HyBid::getReportingController).thenReturn(mockReportingController);
        hyBidMockedStatic.when(HyBid::isReportingEnabled).thenReturn(true);
        hyBidMockedStatic.when(() -> HyBid.getSDKVersionInfo(any(IntegrationType.class))).thenReturn("1.0.0");

        String zoneId = "zone1";
        AdCache adCache = new AdCache();
        adCache.put(zoneId, spiedAd);
        hyBidMockedStatic.when(HyBid::getAdCache).thenReturn(adCache);

        hyBidMockedStatic.when(HyBid::getApiClient).thenReturn(mockApiClient);
        DeviceInfo mockDeviceInfo = mock(DeviceInfo.class);
        when(mockDeviceInfo.getAdvertisingId()).thenReturn("test-ad-id");
        hyBidMockedStatic.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);

        RewardedPresenterDecorator presenter = new RewardedPresenterDecorator(
                mock(),
                new AdTracker(
                        spiedAd.getBeacons(Ad.Beacon.IMPRESSION),
                        spiedAd.getBeacons(Ad.Beacon.CLICK),
                        spiedAd.getBeacons(Ad.Beacon.SDK_EVENT),
                        spiedAd.getBeacons(Ad.Beacon.COMPANION_AD_EVENT),
                        spiedAd.getBeacons(Ad.Beacon.CUSTOM_ENDCARD_EVENT)
                ),
                new AdTracker(spiedAd.getBeacons(Ad.Beacon.CUSTOM_END_CARD_IMPRESSION), spiedAd.getBeacons(Ad.Beacon.CUSTOM_END_CARD_CLICK)),
                mockReportingController,
                mock(),
                mock()
        );
        broadcastReceiver = new HyBidRewardedBroadcastReceiver(application);
        broadcastReceiver.setListener((action, extras) -> {
            broadcastReceiver.handleAction(action, presenter, extras, presenter, presenter, presenter);
        });
        broadcastReceiver.register();

        try (MockedConstruction<UrlHandler> urlHandlerMockedConstruction =
                     mockConstruction(UrlHandler.class, (mock, context) -> {
                         mockUrlHandler = mock;
                     });
        ) {
            viewModel = new MraidRewardedViewModel(
                    application,
                    zoneId,
                    "integrationType",
                    5,
                    broadcastReceiver.getBroadcastId(),
                    mockInteractor
            );
        }
    }

    @After
    public void tearDown() throws Exception {
        broadcastReceiver.destroy();
        hyBidMockedStatic.close();
        closeable.close();
    }

    @Test
    public void testConstructorAndShouldShowContentInfo() {
        // Constructor already called in setup
        verify(mockInteractor).setContentLayout();
        assertFalse(viewModel.shouldShowContentInfo());
    }

    @Test
    public void testCloseAndSkipButtonClicked() {
        viewModel.closeButtonClicked();
        verify(mockInteractor, atLeastOnce()).finishActivity();

        // Inject mock banner and test skip
        replacePrivateVariableWithMock(MraidRewardedViewModel.class, "mView", mockBanner);
        viewModel.skipButtonClicked();
        verify(mockBanner).skipButtonClicked();
    }

    @Test
    public void testMraidResizeAndButtons() {
        assertFalse(viewModel.mraidViewResize(mockMraidView, 100, 100, 0, 0));

        viewModel.mraidShowCloseButton();
        verify(mockInteractor).showRewardedCloseButton(any());

        viewModel.mraidShowSkipButton();
        verify(mockInteractor).showRewardedSkipButton(any());

        viewModel.mraidHideSkipButton();
        verify(mockInteractor, atLeastOnce()).hideRewardedSkipButton();
    }

    @Test
    public void testReportingMethods() {
        // Load Success
        viewModel.onCustomEndCardLoadSuccess();
        ShadowLooper.idleMainLooper();
        verify(mockReportingController, atLeastOnce()).reportEvent(assertArg(event -> {
            assertEquals(Reporting.EventType.CUSTOM_END_CARD_LOAD_SUCCESS, event.getEventType());
        }));
        clearInvocations(mockReportingController);

        // Show
        viewModel.onCustomEndCardShow(Reporting.Key.END_CARD_HTML);
        ShadowLooper.idleMainLooper();
        verify(mockReportingController).reportEvent(assertArg(event -> {
            assertEquals(Reporting.EventType.CUSTOM_ENDCARD_IMPRESSION, event.getEventType());
            assertEquals(Reporting.Key.END_CARD_HTML, event.getCustomString(Reporting.Key.END_CARD_TYPE));
        }));
        clearInvocations(mockReportingController);

        // Load fail
        viewModel.onCustomEndCardLoadFail();
        ShadowLooper.idleMainLooper();
        verify(mockReportingController).reportEvent(assertArg(event -> {
            assertEquals(Reporting.EventType.CUSTOM_END_CARD_LOAD_FAILURE, event.getEventType());
        }));
        clearInvocations(mockReportingController);

        // Close
        viewModel.onCustomEndCardClosed();
        ShadowLooper.idleMainLooper();
        verify(mockReportingController).reportEvent(assertArg(event -> {
            assertEquals(Reporting.EventType.CUSTOM_ENDCARD_CLOSE, event.getEventType());
        }));
        clearInvocations(mockReportingController);

        // Click
        viewModel.onCustomEndCardClicked();
        ShadowLooper.idleMainLooper();
        verify(mockReportingController).reportEvent(assertArg(event -> {
            assertEquals(Reporting.EventType.CUSTOM_ENDCARD_CLICK, event.getEventType());
        }));
        clearInvocations(mockReportingController);

        // CTA Show
        viewModel.onCustomCTAShow();
        ShadowLooper.idleMainLooper();
        verify(mockReportingController).reportEvent(assertArg(event -> {
            assertEquals(Reporting.EventType.CUSTOM_CTA_SHOW, event.getEventType());
        }));
        clearInvocations(mockReportingController);

        // CTA Click
        viewModel.onCustomCTAClick();
        ShadowLooper.idleMainLooper();
        verify(mockReportingController).reportEvent(assertArg(event -> {
            assertEquals(Reporting.EventType.CUSTOM_CTA_CLICK, event.getEventType());
        }));
        clearInvocations(mockReportingController);

        // Click through
        viewModel.onClickThroughTriggered();
        ShadowLooper.idleMainLooper();
        verify(mockReportingController).reportEvent(assertArg(event -> {
            assertEquals(Reporting.EventType.CLICK, event.getEventType());
        }));
        clearInvocations(mockReportingController);
    }

    @Test
    public void testCloseLayoutCallbacks() {
        viewModel.onShowCloseLayout();
        verify(mockInteractor, atLeastOnce()).showRewardedCloseButton(any());

        viewModel.onRemoveCloseLayout();
        verify(mockInteractor, atLeastOnce()).hideRewardedCloseButton();

        viewModel.onClose();
        verify(mockInteractor, atLeastOnce()).finishActivity();
    }

    @Test
    public void testPauseResumeDestroy() {
        replacePrivateVariableWithMock(MraidRewardedViewModel.class, "mView", mockBanner);

        viewModel.pauseAd();
        verify(mockBanner).pause();

        replacePrivateVariableWithMock(MraidRewardedViewModel.class, "mIsFeedbackFormOpen", false);
        viewModel.resumeAd();
        verify(mockBanner).resume();

        viewModel.destroyAd();
        verify(mockBanner).stopAdSession();
        verify(mockBanner).destroy();
    }

    @Test
    public void testMraidHideCloseButton() {
        viewModel.mraidHideCloseButton();
        verify(mockInteractor, atLeastOnce()).hideRewardedCloseButton();
    }

    @Test
    public void testIsRewardEligible_followsSkipOffsetNotCloseability() {
        assertFalse(viewModel.isRewardEligible());

        // Closeable alone (e.g. antilock X) is not an earned reward
        replacePrivateVariableWithMock(RewardedViewModel.class, "mIsSkippable", true);
        assertFalse(viewModel.isRewardEligible());

        viewModel.mraidShowSkipButton();
        assertTrue(viewModel.isRewardEligible());
    }

    @Test
    public void testAddFriendlyObstruction() {
        replacePrivateVariableWithMock(MraidRewardedViewModel.class, "mView", mockBanner);
        View mockView = new View(application);
        viewModel.addFriendlyObstruction(mockView);
        verify(mockBanner).addViewabilityFriendlyObstruction(mockView, BaseFriendlyObstructionPurpose.OTHER, FriendlyObstructionReasonConstants.WATERMARK_OBSTRUCTION_REASON);
    }

    private void replacePrivateVariableWithMock(Class c, String fieldName, Object b) {
        try {
            Class<?> runnerClass = Class.forName("net.pubnative.lite.sdk.rewarded.viewModel.MraidRewardedViewModel");
            assertNotNull(runnerClass);

            Field field = c.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(viewModel, b);
        } catch (Exception ignore) {
        }
    }

    @Test
    public void mraidNativeFeatureCallTel_marksImpressionClicked_handlesUrl_reportsClick() {
        String adLink = "https://adurl.com";
        doReturn(adLink).when(spiedAd).getLink();
        String adNavigationMode = "nav";
        doReturn(adNavigationMode).when(spiedAd).getNavigationMode();

        String uri = "tel:+1-555-123-4567";
        viewModel.mraidNativeFeatureCallTel(uri);

        verify(spiedAd).setImpressionClicked();
        verify(mockUrlHandler).handleUrl(eq(uri), eq(adLink), eq(adNavigationMode));
        ShadowLooper.idleMainLooper();
        verify(mockReportingController).reportEvent(assertArg(event -> {
            assertEquals(Reporting.EventType.CLICK, event.getEventType());
        }));
    }

    @Test
    public void mraidNativeFeatureSendSms_marksImpressionClicked_handlesUrl_reportsClick() {
        String adLink = "https://adurl.com";
        doReturn(adLink).when(spiedAd).getLink();
        String adNavigationMode = "nav";
        doReturn(adNavigationMode).when(spiedAd).getNavigationMode();

        String uri = "sms:+15551234567?body=Hello%20there";
        viewModel.mraidNativeFeatureSendSms(uri);

        verify(spiedAd).setImpressionClicked();
        verify(mockUrlHandler).handleUrl(eq(uri), eq(adLink), eq(adNavigationMode));
        ShadowLooper.idleMainLooper();
        verify(mockReportingController).reportEvent(assertArg(event -> {
            assertEquals(Reporting.EventType.CLICK, event.getEventType());
        }));
    }

    @Test
    public void onClickThroughTriggered_marksImpressionClicked_reportsClick() {
        viewModel.onClickThroughTriggered();

        verify(spiedAd).setImpressionClicked();
        ShadowLooper.idleMainLooper();
        verify(mockReportingController).reportEvent(assertArg(event -> {
            assertEquals(Reporting.EventType.CLICK, event.getEventType());
        }));
    }

    @Test
    public void mraidNativeFeatureOpenBrowser_marksImpressionClicked_handlesUrl_reportsClick() {
        String adLink = "https://adurl.com";
        doReturn(adLink).when(spiedAd).getLink();
        String adNavigationMode = "nav";
        doReturn(adNavigationMode).when(spiedAd).getNavigationMode();

        String url = "https://example.com";
        viewModel.mraidNativeFeatureOpenBrowser(url);

        verify(spiedAd).setImpressionClicked();
        verify(mockUrlHandler).handleUrl(eq(url), eq(adLink), eq(adNavigationMode));
        ShadowLooper.idleMainLooper();
        verify(mockReportingController).reportEvent(assertArg(event -> {
            assertEquals(Reporting.EventType.CLICK, event.getEventType());
        }));
    }

    @Test
    public void onCustomCTAClick_marksImpressionClicked_reportsCustomCtaClick() {
        viewModel.onCustomCTAClick();

        verify(spiedAd).setImpressionClicked();
        ShadowLooper.idleMainLooper();
        verify(mockReportingController).reportEvent(assertArg(event -> {
            assertEquals(Reporting.EventType.CUSTOM_CTA_CLICK, event.getEventType());
        }));
    }

    @Test
    public void onCustomEndCardLoadFail_reportsCustomEndCardLoadFailure_firesEndCardEventBeacon() {
        viewModel.onCustomEndCardLoadFail();

        ShadowLooper.idleMainLooper();
        verify(mockReportingController).reportEvent(assertArg(event -> {
            assertEquals(Reporting.EventType.CUSTOM_END_CARD_LOAD_FAILURE, event.getEventType());
        }));
        verify(mockApiClient).trackUrl(eq("https://customendcardevent.com?e=" + AuxiliaryAdEventType.ERROR), any(), any(), any());
    }

    @Test
    public void onCustomEndCardClicked_marksImpressionClicked_reportsEndCardClick() {
        viewModel.onCustomEndCardClicked();

        verify(spiedAd).setImpressionClicked();
        ShadowLooper.idleMainLooper();
        verify(mockReportingController).reportEvent(assertArg(event -> {
            assertEquals(Reporting.EventType.CUSTOM_ENDCARD_CLICK, event.getEventType());
        }));
    }

    @Test
    public void getAdView_htmlAsset_reportsInterstitialPlacement() throws Exception {
        when(spiedAd.getAssetHtml(APIAsset.HTML_BANNER)).thenReturn("<html><body></body></html>");

        View adView = viewModel.getAdView();

        assertTrue(adView instanceof MRAIDBanner);
        assertEquals("interstitial", getPlacementType((MRAIDView) adView));
    }

    @Test
    public void getAdView_urlAsset_reportsInterstitialPlacement() throws Exception {
        Ad mockAd = mock(Ad.class);
        when(mockAd.getAssetUrl(APIAsset.HTML_BANNER)).thenReturn("https://example.com/ad.html");
        viewModel.mAd = mockAd;

        View adView = viewModel.getAdView();

        assertTrue(adView instanceof MRAIDBanner);
        assertEquals("interstitial", getPlacementType((MRAIDView) adView));
    }

    @Test
    public void whenManyClicksDone_clickBeaconsFireOnce() {
        viewModel.onCustomCTAClick();
        viewModel.onCustomCTAClick();
        viewModel.mraidNativeFeatureOpenBrowser("https://example.com");
        viewModel.mraidNativeFeatureSendSms("sms:+15551234567?body=Hello%20there");
        viewModel.mraidNativeFeatureCallTel("tel:+1-555-123-456");
        viewModel.onClickThroughTriggered();
        viewModel.onClickThroughTriggered();
        viewModel.onCustomEndCardClicked();
        viewModel.onCustomEndCardClicked();

        ShadowLooper.idleMainLooper();

        verify(mockApiClient, times(1))
                .trackUrl(eq(spiedAd.getBeacons(Ad.Beacon.CLICK).get(0).getURL()), any(), any(), any());
        verify(mockApiClient, times(1))
                .trackUrl(eq(spiedAd.getBeacons(Ad.Beacon.CUSTOM_CTA_CLICK).get(0).getURL()), any(), any(), any());
        verify(mockApiClient, times(1))
                .trackUrl(eq(spiedAd.getBeacons(Ad.Beacon.CUSTOM_END_CARD_CLICK).get(0).getURL()), any(), any(), any());
    }

    private static String getPlacementType(MRAIDView view) throws Exception {
        Method method = MRAIDView.class.getDeclaredMethod("getPlacementType");
        method.setAccessible(true);
        return (String) method.invoke(view);
    }

    private List<HyBidRewardedBroadcastReceiver.Action> captureBroadcasts() {
        final List<HyBidRewardedBroadcastReceiver.Action> actions = new ArrayList<>();
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                actions.add(HyBidRewardedBroadcastReceiver.Action.from(intent.getAction()));
            }
        };
        IntentFilter filter = new IntentFilter();
        for (HyBidRewardedBroadcastReceiver.Action action : HyBidRewardedBroadcastReceiver.Action.values()) {
            filter.addAction(action.getId());
        }
        broadcastReceiver.destroy();
        PNLocalBroadcastManager.getInstance(application).registerReceiver(receiver, filter);
        return actions;
    }

    @Test
    public void mraidViewError_beforeRewardIsEarned_closesWithoutReward() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();
        viewModel.mIsSkippable = false;

        viewModel.mraidViewError(mockMraidView);
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertTrue(actions.contains(HyBidRewardedBroadcastReceiver.Action.ERROR));
        assertTrue(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD));
        assertFalse(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE));
        verify(mockInteractor, atLeastOnce()).finishActivity();
    }

    @Test
    public void mraidViewError_afterRewardIsEarned_stillPaysReward() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();
        viewModel.mIsAdPresented = true;
        viewModel.mraidShowCloseButton(); // reward point reached: the countdown ended and the X is shown

        viewModel.mraidViewError(mockMraidView);
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertTrue(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE));
        assertFalse(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD));
    }

    @Test
    public void mraidViewError_zeroDelayAdFailingBeforePresentation_closesWithoutReward() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();
        viewModel.mIsSkippable = true; // zero skip delay: skippable before anything is shown
        viewModel.mIsAdPresented = false;

        viewModel.mraidViewError(mockMraidView);
        ShadowLooper.idleMainLooper();

        assertTrue(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD));
        assertFalse(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE));
    }

    @Test
    public void mraidViewLoaded_marksAdAsPresented() {
        assertFalse(viewModel.mIsAdPresented);
        viewModel.mraidViewLoaded(mockMraidView);
        assertTrue(viewModel.mIsAdPresented);
    }

    @Test
    public void mraidViewError_reportedTwice_closesOnlyOnce() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();
        clearInvocations(mockInteractor);

        viewModel.mraidViewError(mockMraidView);
        viewModel.mraidViewError(mockMraidView);
        ShadowLooper.idleMainLooper();

        int closes = 0;
        for (HyBidRewardedBroadcastReceiver.Action a : actions) {
            if (a == HyBidRewardedBroadcastReceiver.Action.CLOSE
                    || a == HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD) {
                closes++;
            }
        }
        assertEquals(1, closes);
        verify(mockInteractor, times(1)).finishActivity();
    }

    @Test
    public void closeAfterClose_isIgnored() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();

        viewModel.sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD);
        viewModel.sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLOSE); // must not pay the reward afterwards
        ShadowLooper.idleMainLooper();

        assertTrue(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD));
        assertFalse(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE));
    }

    @Test
    public void closeButtonClicked_normalClose_stillPaysReward() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();

        viewModel.closeButtonClicked();
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertTrue(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE));
    }

    @Test
    public void mraidViewError_afterSkipButtonShown_stillPaysReward() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();
        viewModel.mIsSkippable = false; // custom end card: skip button instead of close button
        viewModel.mIsAdPresented = true;

        viewModel.mraidShowSkipButton();
        viewModel.mraidViewError(mockMraidView);
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertTrue(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE));
        assertFalse(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD));
    }

    @Test
    public void mraidViewError_afterSkipButtonShownAndHidden_stillPaysReward() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();
        viewModel.mIsAdPresented = true;

        viewModel.mraidShowSkipButton();
        viewModel.mraidHideSkipButton();
        viewModel.mraidViewError(mockMraidView);
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertTrue(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE));
    }

    @Test
    public void mraidViewError_beforeSkipButtonShown_closesWithoutReward() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();
        viewModel.mIsSkippable = false;
        viewModel.mIsAdPresented = true;

        viewModel.mraidViewError(mockMraidView);
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertTrue(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD));
        assertFalse(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE));
    }

    @Test
    public void skipButtonShown_doesNotUnlockBackNavigation() {
        viewModel.mIsSkippable = false;

        viewModel.mraidShowSkipButton();

        assertFalse(viewModel.isAdSkippable());
        assertTrue(viewModel.isRewardEligible());
    }

    @Test
    public void realMraidSkipPath_endCardWithLink_rendererFailureAfterSkipShown_paysReward() throws Exception {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();
        viewModel.mIsSkippable = false;
        viewModel.mIsAdPresented = true;

        // Real MRAIDView reporting to the real view model, as in a rewarded ad with a custom end card
        MRAIDView realView = new MRAIDView(application, "https://example.com", null, false,
                new String[]{}, viewModel, null, null, false, true);
        HTMLAd htmlAd = mock(HTMLAd.class);
        when(htmlAd.getLink()).thenReturn("https://example.com");
        realView.setHtmlAd(htmlAd);
        Field endCardField = MRAIDView.class.getDeclaredField("mEndCardView");
        endCardField.setAccessible(true);
        endCardField.set(realView, mock(HyBidEndCardView.class));

        // The same method the skip timer calls when it finishes
        Method showSkipOrClose = MRAIDView.class.getDeclaredMethod("showSkipOrCloseButton");
        showSkipOrClose.setAccessible(true);
        showSkipOrClose.invoke(realView);

        assertFalse(viewModel.isAdSkippable()); // skip button shown, not the close button
        assertTrue(viewModel.isRewardEligible());

        viewModel.mraidViewError(realView);
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertTrue(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE));
        assertFalse(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD));
    }

    @Test
    public void antilockCloseButtonBeforePageLoaded_thenRendererFailure_doesNotPayReward() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();

        viewModel.mraidShowCloseButton(); // 5 s antilock X on a slow page, before mraidViewLoaded
        assertTrue(viewModel.isAdSkippable());
        assertFalse(viewModel.isRewardEligible());

        viewModel.mraidViewLoaded(mockMraidView);
        viewModel.mraidHideCloseButton(); // countdown starts, X hidden
        viewModel.mraidViewError(mockMraidView);
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertTrue(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD));
        assertFalse(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE));
    }

    @Test
    public void antilockCloseButton_thenCountdownEnds_paysRewardOnFailure() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();

        viewModel.mraidShowCloseButton(); // antilock, not earned
        viewModel.mraidViewLoaded(mockMraidView);
        viewModel.mraidHideCloseButton();
        viewModel.mraidShowCloseButton(); // countdown finished, now earned
        viewModel.mraidViewError(mockMraidView);
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertTrue(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE));
        assertFalse(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD));
    }

    @Test
    public void restoredRewardAfterActivityRecreation_stillPaysRewardOnFailure() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();

        viewModel.restoreRewardEarned(); // new view model after a relaunch, state from savedInstanceState
        assertTrue(viewModel.isRewardEligible());

        viewModel.mraidViewError(mockMraidView);
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertTrue(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE));
        assertFalse(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD));
    }

    @Test
    public void isRewardEarned_requiresPresentationAndEligibility() {
        viewModel.mraidShowSkipButton();
        viewModel.mIsAdPresented = false;
        assertFalse(viewModel.isRewardEarned());

        viewModel.mIsAdPresented = true;
        assertTrue(viewModel.isRewardEarned());
    }
}
