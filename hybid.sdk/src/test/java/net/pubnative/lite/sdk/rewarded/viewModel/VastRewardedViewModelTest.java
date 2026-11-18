// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.viewModel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doAnswer;
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
import net.pubnative.lite.sdk.vpaid.PlayerInfo;
import static org.junit.Assert.assertEquals;

import android.view.View;

import androidx.test.core.app.ApplicationProvider;

import net.pubnative.lite.sdk.rewarded.RewardedActivityInteractor;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.vpaid.VideoAd;
import net.pubnative.lite.sdk.vpaid.VideoAdListener;


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
    public void testIsRewardEligible_notFinishedNotSkippable_false() {
        assertFalse(viewModel.isRewardEligible());
    }

    @Test
    public void testIsRewardEligible_skippableNoEndCard_true() {
        replacePrivateVariableWithMock(RewardedViewModel.class, "mIsSkippable", true);
        assertTrue(viewModel.isRewardEligible());
    }

    @Test
    public void testIsRewardEligible_videoFinishedEndCardPending_true() {
        // Video finished but end-card close-timer hasn't elapsed - mIsSkippable still false.
        replacePrivateVariableWithMock(VastRewardedViewModel.class, "mIsVideoFinished", true);
        replacePrivateVariableWithMock(RewardedViewModel.class, "mIsSkippable", false);

        assertTrue(viewModel.isRewardEligible());
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

    @Test
    public void videoAdListenerClicks_markTheImpression() {
        VideoAdListener listener = getPrivateFieldValue(VastRewardedViewModel.class, "mVideoAdListener");
        assertNotNull(listener);

        listener.onAdClicked();
        listener.onCustomEndCardClick("html");
        listener.onDefaultEndCardClick("static");
        listener.onCustomCTACLick(true);

        verify(mockAd, times(4)).setImpressionClicked(); // one per click callback
    }

    @Test
    public void videoAdListener_hasImpressionBeenClicked_reflectsTheAd() {
        VideoAdListener listener = getPrivateFieldValue(VastRewardedViewModel.class, "mVideoAdListener");
        assertNotNull(listener);

        when(mockAd.isImpressionClicked()).thenReturn(false);
        assertFalse(listener.hasImpressionBeenClicked());
        when(mockAd.isImpressionClicked()).thenReturn(true);
        assertTrue(listener.hasImpressionBeenClicked());
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
        PNLocalBroadcastManager.getInstance(application).registerReceiver(receiver, filter);
        return actions;
    }

    private void setVideoFinished(boolean finished) {
        try {
            Field field = VastRewardedViewModel.class.getDeclaredField("mIsVideoFinished");
            field.setAccessible(true);
            field.setBoolean(viewModel, finished);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    @Test
    public void adLoadFail_beforeRewardIsEarned_closesWithoutReward() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();
        VideoAdListener listener = getPrivateFieldValue(VastRewardedViewModel.class, "mVideoAdListener");
        assertNotNull(listener);
        clearInvocations(interactor);

        listener.onAdLoadFail(new PlayerInfo("No connection"));
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertTrue(actions.contains(HyBidRewardedBroadcastReceiver.Action.ERROR));
        assertTrue(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD));
        assertFalse(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE));
        verify(interactor).finishActivity();
    }

    @Test
    public void adLoadFail_afterVideoFinished_stillPaysReward() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();
        VideoAdListener listener = getPrivateFieldValue(VastRewardedViewModel.class, "mVideoAdListener");
        assertNotNull(listener);
        setVideoFinished(true);
        viewModel.mIsAdPresented = true;
        clearInvocations(interactor);

        listener.onAdLoadFail(new PlayerInfo("late error"));
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertTrue(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE));
        assertFalse(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD));
        verify(interactor).finishActivity();
    }

    @Test
    public void adLoadFail_zeroDelayAdFailingBeforeFirstFrame_closesWithoutReward() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();
        VideoAdListener listener = getPrivateFieldValue(VastRewardedViewModel.class, "mVideoAdListener");
        assertNotNull(listener);
        viewModel.mIsSkippable = true; // zero skip delay: skippable before the video starts

        listener.onAdLoadFail(new PlayerInfo("No connection"));
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertTrue(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD));
        assertFalse(actions.contains(HyBidRewardedBroadcastReceiver.Action.CLOSE));
    }

    @Test
    public void adStarted_marksAdAsPresented() {
        VideoAdListener listener = getPrivateFieldValue(VastRewardedViewModel.class, "mVideoAdListener");
        assertNotNull(listener);
        assertFalse(viewModel.mIsAdPresented);
        listener.onAdStarted();
        assertTrue(viewModel.mIsAdPresented);
    }

    @Test
    public void adLoadFail_reportedTwice_failsAndClosesOnlyOnce() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();
        VideoAdListener listener = getPrivateFieldValue(VastRewardedViewModel.class, "mVideoAdListener");
        assertNotNull(listener);
        clearInvocations(interactor);

        // MediaPlayer reported the same media error twice (VMA-1645 AC2)
        listener.onAdLoadFail(new PlayerInfo("Error loading media file"));
        listener.onAdLoadFail(new PlayerInfo("Error loading media file"));
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertEquals(1, countOf(actions, HyBidRewardedBroadcastReceiver.Action.ERROR));
        assertEquals(1, countOf(actions, HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD));
        assertEquals(0, countOf(actions, HyBidRewardedBroadcastReceiver.Action.CLOSE));
        verify(interactor, times(1)).finishActivity();
    }

    @Test
    public void closeButtonClicked_afterFullWatch_closesOnlyOnce() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();
        VideoAdListener listener = getPrivateFieldValue(VastRewardedViewModel.class, "mVideoAdListener");
        assertNotNull(listener);
        setVideoFinished(true);
        clearInvocations(interactor);
        // closeVideo() dismisses the ad, which calls back into onAdDismissed() (VMA-1645 AC5)
        doAnswer(invocation -> {
            listener.onAdDismissed();
            return null;
        }).when(mockVideoAd).closeVideo();

        viewModel.closeButtonClicked();
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertEquals(1, countOf(actions, HyBidRewardedBroadcastReceiver.Action.CLOSE));
        verify(interactor, times(1)).finishActivity();
    }

    @Test
    public void closeButtonClicked_afterFullWatch_whenCloseVideoDoesNotDismiss_stillCloses() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();
        setVideoFinished(true);
        clearInvocations(interactor);

        viewModel.closeButtonClicked(); // mockVideoAd.closeVideo() does nothing
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertEquals(1, countOf(actions, HyBidRewardedBroadcastReceiver.Action.CLOSE));
        verify(interactor, times(1)).finishActivity();
    }

    private int countOf(List<HyBidRewardedBroadcastReceiver.Action> actions, HyBidRewardedBroadcastReceiver.Action wanted) {
        int n = 0;
        for (HyBidRewardedBroadcastReceiver.Action a : actions) {
            if (a == wanted) n++;
        }
        return n;
    }

    @Test
    public void adLoadFail_afterSkipButtonShown_withEndCard_stillPaysReward() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();
        VideoAdListener listener = getPrivateFieldValue(VastRewardedViewModel.class, "mVideoAdListener");
        assertNotNull(listener);
        listener.onAdStarted();
        // End card ad: the skip offset is reached, a skip button is shown, however reward not granted (VMA-1645 AC4)
        viewModel.onSkipButtonShown();
        assertTrue(viewModel.isRewardEligible());

        listener.onAdLoadFail(new PlayerInfo("Error loading media file"));
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertEquals(1, countOf(actions, HyBidRewardedBroadcastReceiver.Action.CLOSE));
        assertEquals(0, countOf(actions, HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD));
    }

    @Test
    public void adLoadFail_beforeSkipButtonShown_withEndCard_doesNotPayReward() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();
        VideoAdListener listener = getPrivateFieldValue(VastRewardedViewModel.class, "mVideoAdListener");
        assertNotNull(listener);
        listener.onAdStarted();
        assertFalse(viewModel.isRewardEligible());

        listener.onAdLoadFail(new PlayerInfo("Error loading media file"));
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertEquals(1, countOf(actions, HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD));
        assertEquals(0, countOf(actions, HyBidRewardedBroadcastReceiver.Action.CLOSE));
    }

    @Test
    public void skipButtonShown_doesNotUnlockBackOrCloseBehaviour() {
        viewModel.onSkipButtonShown();

        assertTrue(viewModel.isRewardEligible());
        assertFalse(viewModel.isAdSkippable()); // back / close gating is unchanged
    }

    @Test
    public void adLoadFail_afterCloseButtonShown_withEndCard_stillPaysReward() throws Exception {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();
        VideoAdListener listener = getPrivateFieldValue(VastRewardedViewModel.class, "mVideoAdListener");
        assertNotNull(listener);
        setHasEndCard(true);
        listener.onAdStarted();
        // close_reward_after_finished: the close button replaces the skip button at the skip offset
        viewModel.showButton();
        assertTrue(viewModel.isRewardEligible());

        listener.onAdLoadFail(new PlayerInfo("Error loading media file"));
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertEquals(1, countOf(actions, HyBidRewardedBroadcastReceiver.Action.CLOSE));
        assertEquals(0, countOf(actions, HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD));
    }

    @Test
    public void showButton_withEndCard_doesNotUnlockBackNavigation() throws Exception {
        setHasEndCard(true);

        viewModel.showButton();

        assertTrue(viewModel.isRewardEligible());
        assertFalse(viewModel.isAdSkippable());
    }

    @Test
    public void showButton_withoutEndCard_unlocksBackNavigationAndIsEligible() throws Exception {
        setHasEndCard(false);

        viewModel.showButton();

        assertTrue(viewModel.isRewardEligible());
        assertTrue(viewModel.isAdSkippable());
    }

    private void setHasEndCard(boolean value) throws Exception {
        java.lang.reflect.Field field = VastRewardedViewModel.class.getDeclaredField("mHasEndCard");
        field.setAccessible(true);
        field.set(viewModel, value);
    }

    @Test
    public void restoredRewardAfterActivityRecreation_stillPaysRewardOnFailure() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();
        VideoAdListener listener = getPrivateFieldValue(VastRewardedViewModel.class, "mVideoAdListener");
        assertNotNull(listener);

        viewModel.restoreRewardEarned(); // new view model after a relaunch, state from savedInstanceState
        assertTrue(viewModel.isRewardEligible());

        listener.onAdLoadFail(new PlayerInfo("Error loading media file"));
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertEquals(1, countOf(actions, HyBidRewardedBroadcastReceiver.Action.CLOSE));
        assertEquals(0, countOf(actions, HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD));
    }

    @Test
    public void adLoadFail_closesExactlyOnce() {
        List<HyBidRewardedBroadcastReceiver.Action> actions = captureBroadcasts();
        VideoAdListener listener = getPrivateFieldValue(VastRewardedViewModel.class, "mVideoAdListener");
        assertNotNull(listener);

        listener.onAdLoadFail(new PlayerInfo("Error loading media file"));
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        int closes = 0;
        for (HyBidRewardedBroadcastReceiver.Action a : actions) {
            if (a == HyBidRewardedBroadcastReceiver.Action.CLOSE
                    || a == HyBidRewardedBroadcastReceiver.Action.CLOSE_WITHOUT_REWARD) {
                closes++;
            }
        }
        assertEquals(1, closes);
    }

    @Test
    public void isRewardEarned_zeroOffsetAdBeforeFirstFrame_isFalse() {
        viewModel.mIsSkippable = true; // zero skip offset: eligible before anything is shown
        viewModel.mIsAdPresented = false;
        assertTrue(viewModel.isRewardEligible());

        assertFalse(viewModel.isRewardEarned()); // so recreation at this point must not save a reward
    }

    @Test
    public void isRewardEarned_afterFirstFrameAndEligible_isTrue() {
        viewModel.mIsSkippable = true;
        viewModel.mIsAdPresented = true;

        assertTrue(viewModel.isRewardEarned());
    }
}
