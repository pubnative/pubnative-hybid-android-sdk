// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.interstitial.viewModel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.app.Application;
import android.os.Handler;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;

import com.verve.atom.sdk.utils.Threads;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.interstitial.InterstitialActivityInteractor;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.viewability.FriendlyObstructionReasonConstants;
import net.pubnative.lite.sdk.viewability.baseom.BaseFriendlyObstructionPurpose;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;

/**
 * Created by shubhamkeshri on 03.10.25.
 */

@RunWith(RobolectricTestRunner.class)
public class MraidInterstitialViewModelTest {

    private MraidInterstitialViewModel viewModel;
    private Application application;

    @Mock
    private InterstitialActivityInteractor mockInteractor;
    @Mock
    private MRAIDBanner mockBanner;
    @Mock
    private MRAIDView mockMraidView;
    @Mock
    private ReportingController mockReportingController;
    @Mock
    private AdTracker mockAdTracker;
    @Mock
    private Handler mockedHandler;

    private MockedStatic<Threads> mockedThreads;

    @Before
    public void setUp() {
        openMocks(this);
        application = ApplicationProvider.getApplicationContext();
        mockedThreads = mockStatic(Threads.class);

        mockedThreads.when(Threads::newUiHandler)
                .thenReturn(mockedHandler);
        when(mockedHandler.postDelayed(any(Runnable.class), anyLong())).thenAnswer((Answer) invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        });


        viewModel = new MraidInterstitialViewModel(
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
        mockedThreads.close();
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
        assertTrue(viewModel.mraidViewResize(mockMraidView, 100, 100, 0, 0));

        viewModel.mraidShowCloseButton();
        verify(mockInteractor).showInterstitialCloseButton(any());

        viewModel.mraidShowSkipButton();
        verify(mockInteractor).showInterstitialSkipButton(any());

        viewModel.mraidHideSkipButton();
        verify(mockInteractor, atLeastOnce()).hideInterstitialSkipButton();
    }

    @Test
    public void testReportingMethods() {
        replacePrivateVariableWithMock(InterstitialViewModel.class, "mAdTracker", mockAdTracker);
        replacePrivateVariableWithMock(InterstitialViewModel.class, "mCustomCTATracker", mockAdTracker);
        replacePrivateVariableWithMock(InterstitialViewModel.class, "mAdEventTracker", mockAdTracker);
        replacePrivateVariableWithMock(InterstitialViewModel.class, "mCustomEndcardTracker", mockAdTracker);


        try (MockedStatic<HyBid> mockedStatic = mockStatic(HyBid.class)) {
            mockedStatic.when(HyBid::getReportingController).thenReturn(mockReportingController);
            mockedStatic.when(HyBid::isReportingEnabled).thenReturn(true);
            mockedStatic.when(() -> HyBid.getSDKVersionInfo(any(IntegrationType.class))).thenReturn("1.0.0");

            // Load Success
            replacePrivateVariableWithMock(MraidInterstitialViewModel.class, "mLoadCustomEndCardTracked", false);
            viewModel.onCustomEndCardLoadSuccess();
            verify(mockReportingController, atLeastOnce()).reportEvent(any(ReportingEvent.class));

            // Show
            replacePrivateVariableWithMock(MraidInterstitialViewModel.class, "mCustomEndcardTracker", true);
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
}
