package net.pubnative.lite.sdk.leaderboard.presenter;

import android.view.View;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class LeaderboardPresenterDecoratorTest {
    @Mock
    private LeaderboardPresenter mMockPresenter;
    @Mock
    private AdTracker mMockAdTracker;
    @Mock
    private View mMockView;
    @Mock
    private LeaderboardPresenter.Listener mMockListener;
    private Ad mTestAd;

    @InjectMocks
    private LeaderboardPresenterDecorator mSubject;

    @Before
    public void setup() {
        initMocks(this);
        mTestAd = TestUtil.createTestLeaderboardAd();
        when(mMockPresenter.getAd()).thenReturn(mTestAd);
    }

    @Test
    public void load() {
        mSubject.load();

        verify(mMockPresenter).load();
    }

    @Test
    public void load_whenDestroyed() {
        mSubject.destroy();

        mSubject.load();

        verify(mMockPresenter, never()).load();
    }

    @Test
    public void startTracking() {
        mSubject.startTracking();

        verify(mMockPresenter).startTracking();
    }

    @Test
    public void startTracking_whenDestroyed() {
        mSubject.destroy();

        mSubject.startTracking();

        verify(mMockPresenter, never()).startTracking();
    }

    @Test
    public void stopTracking() {
        mSubject.stopTracking();

        verify(mMockPresenter).stopTracking();
    }

    @Test
    public void stopTracking_whenDestroyed() {
        mSubject.destroy();

        mSubject.stopTracking();

        verify(mMockPresenter, never()).stopTracking();
    }

    @Test
    public void destroy() {
        mSubject.destroy();

        verify(mMockPresenter).destroy();
    }

    @Test
    public void onLeaderboardLoaded() {
        mSubject.onLeaderboardLoaded(mMockPresenter, mMockView);

        verify(mMockAdTracker).trackImpression();
        verify(mMockListener).onLeaderboardLoaded(mMockPresenter, mMockView);
    }

    @Test
    public void onLeaderboardLoaded_whenDestroyed() {
        mSubject.destroy();

        mSubject.onLeaderboardLoaded(mMockPresenter, mMockView);

        verify(mMockAdTracker, never()).trackImpression();
        verify(mMockListener, never()).onLeaderboardLoaded(any(LeaderboardPresenter.class), any(View.class));
    }

    @Test
    public void onLeaderboardClicked() {
        mSubject.onLeaderboardClicked(mMockPresenter);

        verify(mMockAdTracker).trackClick();
        verify(mMockListener).onLeaderboardClicked(mMockPresenter);
    }

    @Test
    public void onLeaderboardClicked_whenDestroyed() {
        mSubject.destroy();

        mSubject.onLeaderboardClicked(mMockPresenter);

        verify(mMockAdTracker, never()).trackClick();
        verify(mMockListener, never()).onLeaderboardClicked(any(LeaderboardPresenter.class));
    }

    @Test
    public void onLeaderboardError() {
        mSubject.onLeaderboardError(mMockPresenter);

        verify(mMockListener).onLeaderboardError(mMockPresenter);
    }

    @Test
    public void onLeaderboardError_whenDestroyed() {
        mSubject.destroy();

        mSubject.onLeaderboardError(mMockPresenter);

        verify(mMockListener, never()).onLeaderboardError(any(LeaderboardPresenter.class));
    }
}
