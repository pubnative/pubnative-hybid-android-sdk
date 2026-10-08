// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.viewModel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.app.Application;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.rewarded.RewardedActivityInteractor;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.UrlHandler;
import net.pubnative.lite.sdk.viewability.FriendlyObstructionReasonConstants;
import net.pubnative.lite.sdk.viewability.baseom.BaseFriendlyObstructionPurpose;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@RunWith(RobolectricTestRunner.class)
public class MraidRewardedViewModelTest {

    private MraidRewardedViewModel viewModel;
    private Application application;

    @Mock
    private RewardedActivityInteractor mockInteractor;
    @Mock
    private MRAIDBanner mockBanner;
    @Mock
    private MRAIDView mockMraidView;
    @Mock
    private ReportingController mockReportingController;
    @Mock
    private AdTracker mockAdTracker;

    @Before
    public void setUp() {
        openMocks(this);
        application = ApplicationProvider.getApplicationContext();


        viewModel = new MraidRewardedViewModel(
                application,
                "zone1",
                "integrationType",
                5,
                12345L,
                mockInteractor
        );
    }

    @After
    public void tearDown() {

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
        replacePrivateVariableWithMock(RewardedViewModel.class, "mAdTracker", mockAdTracker);
        replacePrivateVariableWithMock(RewardedViewModel.class, "mCustomCTATracker", mockAdTracker);
        replacePrivateVariableWithMock(RewardedViewModel.class, "mAdEventTracker", mockAdTracker);
        replacePrivateVariableWithMock(RewardedViewModel.class, "mCustomEndcardTracker", mockAdTracker);

        try (MockedStatic<HyBid> mockedStatic = mockStatic(HyBid.class)) {
            mockedStatic.when(HyBid::getReportingController).thenReturn(mockReportingController);
            mockedStatic.when(HyBid::isReportingEnabled).thenReturn(true);
            mockedStatic.when(() -> HyBid.getSDKVersionInfo(any(IntegrationType.class))).thenReturn("1.0.0");

            // Load Success
            replacePrivateVariableWithMock(MraidRewardedViewModel.class, "mLoadCustomEndCardTracked", false);
            viewModel.onCustomEndCardLoadSuccess();
            verify(mockReportingController, atLeastOnce()).reportEvent(any(ReportingEvent.class));

            // Show
            replacePrivateVariableWithMock(MraidRewardedViewModel.class, "mCustomEndcardTracker", true);
            viewModel.onCustomEndCardShow("CUSTOM");
            verify(mockReportingController, atLeastOnce()).reportEvent(any());

            // Load fail
            viewModel.onCustomEndCardLoadFail();
            verify(mockReportingController, atLeastOnce()).reportEvent(any(ReportingEvent.class));

            // Close
            viewModel.onCustomEndCardClosed();
            verify(mockReportingController, atLeastOnce()).reportEvent(any(ReportingEvent.class));

            // Click
            viewModel.onCustomEndCardClicked();
            verify(mockReportingController, atLeastOnce()).reportEvent(any(ReportingEvent.class));

            // CTA Show and CTA Click
            viewModel.onCustomCTAShow();
            viewModel.onCustomCTAClick();
            verify(mockReportingController, atLeastOnce()).reportEvent(any(ReportingEvent.class));
        }
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
    public void testIsRewardEligible_usesBaseDefault() {
        // No override needed - MRAID has no video-completion concept distinct from skippability.
        assertFalse(viewModel.isRewardEligible());

        replacePrivateVariableWithMock(RewardedViewModel.class, "mIsSkippable", true);
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
    public void mraidNativeFeatureCallTel_marksImpressionClicked() {
        Ad mockAd = mock(Ad.class);
        viewModel.mAd = mockAd;

        viewModel.mraidNativeFeatureCallTel("tel:123");

        verify(mockAd).setImpressionClicked();
    }

    @Test
    public void mraidNativeFeatureSendSms_marksImpressionClicked() {
        Ad mockAd = mock(Ad.class);
        viewModel.mAd = mockAd;

        viewModel.mraidNativeFeatureSendSms("sms:123");

        verify(mockAd).setImpressionClicked();
    }

    @Test
    public void onClickThroughTriggered_marksImpressionClicked() {
        Ad mockAd = mock(Ad.class);
        viewModel.mAd = mockAd;
        replacePrivateVariableWithMock(RewardedViewModel.class, "mAdTracker", mockAdTracker);

        viewModel.onClickThroughTriggered();

        verify(mockAd).setImpressionClicked();
    }

    @Test
    public void mraidNativeFeatureOpenBrowser_marksImpressionClicked() {
        // Creative click -> switches the close button from the initial to the normal tap area.
        Ad mockAd = mock(Ad.class);
        viewModel.mAd = mockAd;
        replacePrivateVariableWithMock(RewardedViewModel.class, "mUrlHandlerDelegate", mock(UrlHandler.class));

        viewModel.mraidNativeFeatureOpenBrowser("https://example.com");

        verify(mockAd).setImpressionClicked();
    }

    @Test
    public void onCustomCTAClick_marksImpressionClicked() {
        Ad mockAd = mock(Ad.class);
        viewModel.mAd = mockAd;
        replacePrivateVariableWithMock(RewardedViewModel.class, "mCustomCTATracker", mockAdTracker);

        viewModel.onCustomCTAClick();

        verify(mockAd).setImpressionClicked();
    }

    @Test
    public void onCustomEndCardClicked_marksImpressionClicked() {
        Ad mockAd = mock(Ad.class);
        viewModel.mAd = mockAd;
        replacePrivateVariableWithMock(RewardedViewModel.class, "mAdTracker", mockAdTracker);
        replacePrivateVariableWithMock(RewardedViewModel.class, "mCustomEndcardTracker", mockAdTracker);
        replacePrivateVariableWithMock(RewardedViewModel.class, "mAdEventTracker", mockAdTracker);

        viewModel.onCustomEndCardClicked();

        verify(mockAd).setImpressionClicked();
    }

    @Test
    public void getAdView_htmlAsset_reportsInterstitialPlacement() throws Exception {
        Ad mockAd = mock(Ad.class);
        when(mockAd.getAssetHtml(APIAsset.HTML_BANNER)).thenReturn("<html><body></body></html>");
        viewModel.mAd = mockAd;

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

    private static String getPlacementType(MRAIDView view) throws Exception {
        Method method = MRAIDView.class.getDeclaredMethod("getPlacementType");
        method.setAccessible(true);
        return (String) method.invoke(view);
    }
}
