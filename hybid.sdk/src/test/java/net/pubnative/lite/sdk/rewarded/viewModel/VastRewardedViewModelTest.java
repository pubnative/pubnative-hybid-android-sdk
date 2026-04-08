// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.viewModel;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.app.Application;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;

import net.pubnative.lite.sdk.rewarded.RewardedActivityInteractor;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.vpaid.VideoAd;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
public class VastRewardedViewModelTest {

    private VastRewardedViewModel viewModel;
    private Application application;
    @Mock
    RewardedActivityInteractor interactor;
    @Mock
    Ad mockAd;
    @Mock
    VideoAd mockVideoAd;
    @Mock
    AdTracker adTracker;


    @Before
    public void setUp() {
        openMocks(this);
        application = ApplicationProvider.getApplicationContext();

        when(mockAd.isEndCardEnabled()).thenReturn(false);

        viewModel = new VastRewardedViewModel(application, "zone1", "integration", 5, 123L, interactor);

        // Inject a fake Ad and VideoAd via reflection since they are private
        replacePrivateVariableWithMock(RewardedViewModel.class, "mAd", mockAd);

        replacePrivateVariableWithMock(VastRewardedViewModel.class, "mVideoAd", mockVideoAd);

        replacePrivateVariableWithMock(RewardedViewModel.class, "mAdEventTracker", adTracker);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testShouldShowContentInfo_returnsTrue() {
        assert (viewModel.shouldShowContentInfo());
    }

    @Test
    public void testGetAdView_returnsVideoView() {
        View v = viewModel.getAdView();
        assert (v != null);
    }

    @Test
    public void testPauseAndResumeAd_whenAdIsReady() {
        when(mockVideoAd.isAdStarted()).thenReturn(true);
        replacePrivateVariableWithMock(VastRewardedViewModel.class, "mReady", true);
        replacePrivateVariableWithMock(VastRewardedViewModel.class, "mIsVideoFinished", true);
        viewModel.pauseAd();
        viewModel.resumeAd();

        verify(mockVideoAd, atLeastOnce()).resumeEndCardCloseButtonTimer();
        verify(mockVideoAd, atLeastOnce()).pause();
        verify(mockVideoAd, atLeastOnce()).resume();
    }

    @Test
    public void testPauseAndResumeAd_whenAdIsNotReady() {
        when(mockVideoAd.isAdStarted()).thenReturn(true);
        replacePrivateVariableWithMock(VastRewardedViewModel.class, "mReady", false);
        replacePrivateVariableWithMock(VastRewardedViewModel.class, "mIsVideoFinished", false);
        viewModel.pauseAd();
        viewModel.resumeAd();

        verify(mockVideoAd, times(0)).resumeEndCardCloseButtonTimer();
        verify(mockVideoAd, times(0)).pause();
        verify(mockVideoAd, times(0)).resume();
    }

    @Test
    public void testDestroyAd() {
        viewModel.destroyAd();
        verify(mockVideoAd).destroy();
    }

    @Test
    public void testRenderVastAd_withoutAd_fallsBackToError() throws Exception {
        // mAd = null
        replacePrivateVariableWithMock(RewardedViewModel.class, "mAd", null);

        viewModel.renderVastAd();

        verify(interactor, atLeastOnce()).finishActivity();
    }

    @Test
    public void testRenderVastAd_withAd_callsVideoLoad() throws Exception {
        viewModel.getAdView(); // initialize mVideoPlayer
        viewModel.renderVastAd(); // with mockAd set
        // Expect no crash and broadcast
    }

    @Test
    public void testCloseButtonClicked_beforeFinish() throws Exception {
        // mIsVideoFinished = false
        replacePrivateVariableWithMock(VastRewardedViewModel.class, "mIsVideoFinished", false);

        viewModel.closeButtonClicked();
        verify(mockVideoAd).skip();

        // Now set finished
        replacePrivateVariableWithMock(VastRewardedViewModel.class, "mIsVideoFinished", true);

        viewModel.closeButtonClicked();
        verify(mockVideoAd).closeVideo();
        verify(interactor, atLeastOnce()).finishActivity();
    }

    @Test
    public void testOnImpressionAndShowButton() {
        viewModel.onImpression();
        viewModel.showButton();
        verify(interactor, atLeastOnce()).showRewardedCloseButton(any());
    }

    @Test
    public void testOnVolumeChanged_delegatesToVideoAd() {
        viewModel.onVolumeChanged();
        verify(mockVideoAd).onVolumeChanged();
    }

    @Test
    public void testAddFriendlyObstruction() {
        View mockView = new View(application);
        viewModel.addFriendlyObstruction(mockView);
        verify(mockVideoAd).addFriendlyObstruction(mockView);
    }

    private void replacePrivateVariableWithMock(Class c, String fieldName, Object b) {
        try {
            Class<?> runnerClass = Class.forName("net.pubnative.lite.sdk.rewarded.viewModel.VastRewardedViewModel");
            assertNotNull(runnerClass);

            Field field = c.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(viewModel, b);
        } catch (Exception ignore) {
        }
    }

    private <T> T getPrivateFieldValue(Class c, String fieldName) {
        try {
            Class<?> runnerClass = Class.forName("net.pubnative.lite.sdk.rewarded.viewModel.VastRewardedViewModel");
            assertNotNull(runnerClass);

            Field field = c.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(viewModel);
        } catch (Exception ignore) {
        }
        return null;
    }
}
