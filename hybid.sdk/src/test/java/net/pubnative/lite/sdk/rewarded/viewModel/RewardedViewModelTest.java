// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.viewModel;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import net.pubnative.lite.sdk.interstitial.viewModel.InterstitialViewModelTest;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedBroadcastReceiver;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdExperience;
import net.pubnative.lite.sdk.rewarded.RewardedActivityInteractor;
import net.pubnative.lite.sdk.utils.UrlHandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class RewardedViewModelTest {

    @Mock Context mockContext;
    @Mock RewardedActivityInteractor mockListener;
    @Mock Ad mockAd;
    @Mock
    FrameLayout mockView;
    @Mock FrameLayout mockViewGroup;

    TestRewardedViewModel viewModel;

    static class TestRewardedViewModel extends RewardedViewModel {
        public TestRewardedViewModel(Context context, String zoneId, String integrationType, int skipOffset, long broadcastId, RewardedActivityInteractor listener) {
            super(context, zoneId, integrationType, skipOffset, broadcastId, listener);
        }
        @Override public boolean shouldShowContentInfo() { return true; }
        @Override public void resumeAd() {}
        @Override public void pauseAd() {}
        @Override public void closeButtonClicked() {}
        @Override public void skipButtonClicked() {}
        @Override public View getAdView() { return null; }
        @Override public void destroyAd() {}
        @Override public void resetVolumeChangeTracker() {}
        @Override public boolean hasReducedCloseSize() {return false;}
        @Override public void addFriendlyObstruction(View view) {}
    }

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        viewModel = new TestRewardedViewModel(
                RuntimeEnvironment.getApplication(), "zone1", "standalone", 5, 123L, mockListener
        );
        viewModel.mAd = mockAd;
    }

    @Test
    public void testIsValidAdToRender_true() {
        assertTrue(viewModel.isValidAdToRender());
    }

    @Test
    public void testSendBroadcast_callsSender() {
        viewModel.sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLOSE);
    }

    @Test
    public void testSendBroadcast_withExtras() {
        Bundle extras = new Bundle();
        viewModel.sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLOSE, extras);
    }

    @Test
    public void testIsFeedbackFormOpen_defaultFalse() {
        assertFalse(viewModel.isFeedbackFormOpen());
    }

    @Test
    public void testOnLinkClicked_invalidUrl() {
        viewModel.onLinkClicked("invalid-url");
        assertFalse(viewModel.isLinkClickRunning);
        assertFalse(viewModel.isFeedbackFormOpen());
    }

    @Test
    public void testOnLinkClicked_validUrl_feedbackForm() {
        viewModel.onLinkClicked("https://valid.url");
    }

    @Test
    public void testSetupContentInfo_withAd() {
        when(mockAd.getContentInfoContainer(any(), any())).thenReturn(mockView);
        viewModel.setupContentInfo(null);
    }

    @Test
    public void testGetContentInfoContainer_returnsAdContainer() {
        when(mockAd.getContentInfoContainer(any(), any())).thenReturn(mockViewGroup);
        assertNotNull(viewModel.getContentInfoContainer());
    }

    @Test
    public void testHideContentInfo_removesView() {
        when(mockAd.getContentInfoContainer(any(), any())).thenReturn(mockView);
        viewModel.setupContentInfo(null);
        viewModel.hideContentInfo();
    }

    @Test
    public void testProcessRewardedAd_invalidAd() {
        TestRewardedViewModel vm = new TestRewardedViewModel(mockContext, "", "standalone", 5, -1, mockListener);
        vm.processRewardedAd();
        verify(mockListener, atLeastOnce()).finishActivity();
    }

    @Test
    public void testIsAdSkippable_defaultFalse() {
        assertFalse(viewModel.isAdSkippable());
    }

    @Test
    public void testHandleURL_callsUrlHandlerWithCorrectParams() {
        when(mockAd.getLink()).thenReturn("http://test-link.com");
        when(mockAd.getNavigationMode()).thenReturn("0");
        // Spy on the viewModel to verify internal UrlHandler usage
        RewardedViewModel spyViewModel = spy(viewModel);
        spyViewModel.mAd = mockAd;

        // Use reflection to get the private mUrlHandlerDelegate
        try {
            java.lang.reflect.Field urlHandlerField = RewardedViewModel.class.getDeclaredField("mUrlHandlerDelegate");
            urlHandlerField.setAccessible(true);
            UrlHandler mockUrlHandler = mock(UrlHandler.class);
            urlHandlerField.set(spyViewModel, mockUrlHandler);

            spyViewModel.handleURL("http://test.com");

            verify(mockUrlHandler).handleUrl("http://test.com", "http://test-link.com", "0");
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }
}

