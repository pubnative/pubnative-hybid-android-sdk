// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.interstitial.viewModel;

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
import android.view.View;

import androidx.test.core.app.ApplicationProvider;

import net.pubnative.lite.sdk.AdCache;
import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.api.PNApiClient;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastReceiver;
import net.pubnative.lite.sdk.interstitial.InterstitialActivityInteractor;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenterDecorator;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdData;
import net.pubnative.lite.sdk.models.AdTestModel;
import net.pubnative.lite.sdk.models.AuxiliaryAdEventType;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDView;
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

/**
 * Created by shubhamkeshri on 03.10.25.
 */

@RunWith(RobolectricTestRunner.class)
public class MraidInterstitialViewModelTest {

    private MraidInterstitialViewModel viewModel;
    private Application application;
    private HyBidInterstitialBroadcastReceiver broadcastReceiver;
    private AutoCloseable closeable;
    private Ad spiedAd;
    private UrlHandler mockUrlHandler;

    private MockedStatic<HyBid> hyBidMockedStatic;
    @Mock
    private InterstitialActivityInteractor mockInteractor;
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

        InterstitialPresenterDecorator presenter = new InterstitialPresenterDecorator(
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
        broadcastReceiver = new HyBidInterstitialBroadcastReceiver(application);
        broadcastReceiver.setListener((action, extras) -> {
            broadcastReceiver.handleAction(action, extras, presenter, presenter, presenter, presenter);
        });
        broadcastReceiver.register();

        try (MockedConstruction<UrlHandler> urlHandlerMockedConstruction =
                     mockConstruction(UrlHandler.class, (mock, context) -> {
                         mockUrlHandler = mock;
                     });
        ) {
            viewModel = new MraidInterstitialViewModel(
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
        replacePrivateVariableWithMock(MraidInterstitialViewModel.class, "mView", mockBanner);
        viewModel.skipButtonClicked();
        verify(mockBanner).skipButtonClicked();
    }

    @Test
    public void testMraidResizeAndButtons() {
        assertFalse(viewModel.mraidViewResize(mockMraidView, 100, 100, 0, 0));

        viewModel.mraidShowCloseButton();
        verify(mockInteractor).showInterstitialCloseButton(any());

        viewModel.mraidShowSkipButton();
        verify(mockInteractor).showInterstitialSkipButton(any());

        viewModel.mraidHideSkipButton();
        verify(mockInteractor, atLeastOnce()).hideInterstitialSkipButton();
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
        verify(mockInteractor, atLeastOnce()).showInterstitialCloseButton(any());

        viewModel.onRemoveCloseLayout();
        verify(mockInteractor, atLeastOnce()).hideInterstitialCloseButton();

        viewModel.onClose();
        verify(mockInteractor, atLeastOnce()).finishActivity();
    }

    @Test
    public void testPauseResumeDestroy() {
        replacePrivateVariableWithMock(MraidInterstitialViewModel.class, "mView", mockBanner);

        viewModel.pauseAd();
        verify(mockBanner).pause();

        replacePrivateVariableWithMock(MraidInterstitialViewModel.class, "mIsFeedbackFormOpen", false);
        viewModel.resumeAd();
        verify(mockBanner).resume();

        viewModel.destroyAd();
        verify(mockBanner).stopAdSession();
        verify(mockBanner).destroy();
    }

    @Test
    public void testMraidHideCloseButton() {
        viewModel.mraidHideCloseButton();
        verify(mockInteractor, atLeastOnce()).hideInterstitialCloseButton();
    }

    @Test
    public void testAddFriendlyObstruction() {
        replacePrivateVariableWithMock(MraidInterstitialViewModel.class, "mView", mockBanner);
        View mockView = new View(application);
        viewModel.addFriendlyObstruction(mockView);
        verify(mockBanner).addViewabilityFriendlyObstruction(mockView, BaseFriendlyObstructionPurpose.OTHER, FriendlyObstructionReasonConstants.WATERMARK_OBSTRUCTION_REASON);
    }

    private void replacePrivateVariableWithMock(Class c, String fieldName, Object b) {
        try {
            Class<?> runnerClass = Class.forName("net.pubnative.lite.sdk.interstitial.viewModel.MraidInterstitialViewModel");
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
        when(spiedAd.getAssetUrl(APIAsset.HTML_BANNER)).thenReturn("https://example.com/ad.html");

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
}
