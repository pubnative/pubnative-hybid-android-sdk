package net.pubnative.lite.sdk.interstitial.presenter;

import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.testing.TestUtil;
import net.pubnative.lite.sdk.utils.AdTracker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by erosgarciaponte on 24.01.18.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 29)
public class InterstitialPresenterDecoratorTest {
    @Mock
    private InterstitialPresenter mMockPresenter;
    @Mock
    private AdTracker mMockAdTracker;
    @Mock
    private ReportingController mReportingController;
    @Mock
    private InterstitialPresenter.Listener mMockListener;

    @InjectMocks
    private InterstitialPresenterDecorator mSubject;

    @Before
    public void setup() {
        initMocks(this);
        Ad mTestAd = TestUtil.createTestInterstitialAd();
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
    public void show() {
        mSubject.show();

        verify(mMockPresenter).show();
    }

    @Test
    public void show_whenDestroyed() {
        mSubject.destroy();

        mSubject.show();

        verify(mMockPresenter, never()).show();
    }

    @Test
    public void destroy() {
        mSubject.destroy();

        verify(mMockPresenter).destroy();
    }

    @Test
    public void onInterstitialLoaded() {
        mSubject.onInterstitialLoaded(mMockPresenter);

        verify(mMockListener).onInterstitialLoaded(mMockPresenter);
    }

    @Test
    public void onInterstitialLoaded_whenDestroyed() {
        mSubject.destroy();

        mSubject.onInterstitialLoaded(mMockPresenter);

        verify(mMockListener, never()).onInterstitialLoaded(any(InterstitialPresenter.class));
    }

    @Test
    public void onInterstitialShown() {
        mSubject.onInterstitialShown(mMockPresenter);

        verify(mMockAdTracker).trackImpression();
        verify(mMockListener).onInterstitialShown(mMockPresenter);
    }

    @Test
    public void onInterstitialShown_whenDestroyed() {
        mSubject.destroy();

        mSubject.onInterstitialShown(mMockPresenter);

        verify(mMockAdTracker, never()).trackImpression();
        verify(mMockListener, never()).onInterstitialShown(any(InterstitialPresenter.class));
    }

    @Test
    public void onInterstitialClicked() {
        mSubject.onInterstitialClicked(mMockPresenter);

        verify(mMockAdTracker).trackClick();
        verify(mMockListener).onInterstitialClicked(mMockPresenter);
    }

    @Test
    public void onInterstitialClicked_whenDestroyed() {
        mSubject.destroy();

        mSubject.onInterstitialClicked(mMockPresenter);

        verify(mMockAdTracker, never()).trackClick();
        verify(mMockListener, never()).onInterstitialClicked(any(InterstitialPresenter.class));
    }

    @Test
    public void onInterstitialDismissed() {
        mSubject.onInterstitialDismissed(mMockPresenter);

        verify(mMockListener).onInterstitialDismissed(mMockPresenter);
    }

    @Test
    public void onInterstitialDismissed_whenDestroyed() {
        mSubject.destroy();

        mSubject.onInterstitialDismissed(mMockPresenter);

        verify(mMockListener, never()).onInterstitialDismissed(any(InterstitialPresenter.class));
    }

    @Test
    public void onInterstitialError() {
        mSubject.onInterstitialError(mMockPresenter);

        verify(mMockListener).onInterstitialError(mMockPresenter);
    }

    @Test
    public void onInterstitialError_whenDestroyed() {
        mSubject.destroy();

        mSubject.onInterstitialError(mMockPresenter);

        verify(mMockListener, never()).onInterstitialError(any(InterstitialPresenter.class));
    }
}
