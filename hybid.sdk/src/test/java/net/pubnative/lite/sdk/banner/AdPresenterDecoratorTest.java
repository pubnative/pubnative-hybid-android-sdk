// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.banner;

import android.view.View;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.presenter.AdPresenterDecorator;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.testing.TestUtil;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.sdkmanager.SdkManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@RunWith(RobolectricTestRunner.class)
public class AdPresenterDecoratorTest {
    @Mock
    private AdPresenter mMockPresenter;
    @Mock
    private AdTracker mMockAdTracker;
    @Mock
    private View mMockView;
    @Mock
    private AdPresenter.Listener mMockListener;

    @InjectMocks
    private AdPresenterDecorator mSubject;

    private MockedStatic<HyBid> mHyBid;

    @Before
    public void setup() {
        openMocks(this);
        Ad mTestAd = TestUtil.createTestBannerAd();
        when(mMockPresenter.getAd()).thenReturn(mTestAd);

        mHyBid = mockStatic(HyBid.class);
        mHyBid.when(HyBid::getSdkManager).thenReturn(mock(SdkManager.class));
    }

    @After
    public void tearDown() {
        mHyBid.close();
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

        verify(mMockPresenter).startTracking(null);
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
    public void onBannerLoaded() {
        mSubject.onAdLoaded(mMockPresenter, mMockView);

        verify(mMockListener).onAdLoaded(mMockPresenter, mMockView);
    }

    @Test
    public void onBannerLoaded_whenDestroyed() {
        mSubject.destroy();

        mSubject.onAdLoaded(mMockPresenter, mMockView);

        verify(mMockListener, never()).onAdLoaded(any(AdPresenter.class), any(View.class));
    }

    @Test
    public void onBannerImpression() {
        mSubject.onImpression();

        verify(mMockAdTracker).trackImpression();
    }

    @Test
    public void onBannerImpression_whenDestroyed() {
        mSubject.destroy();

        mSubject.onImpression();

        verify(mMockAdTracker, never()).trackImpression();
    }

    @Test
    public void onBannerClicked() {
        mSubject.onAdClicked(mMockPresenter);

        verify(mMockAdTracker).trackClick();
        verify(mMockListener).onAdClicked(mMockPresenter);
    }

    @Test
    public void onBannerClicked_whenDestroyed() {
        mSubject.destroy();

        mSubject.onAdClicked(mMockPresenter);

        verify(mMockAdTracker, never()).trackClick();
        verify(mMockListener, never()).onAdClicked(any(AdPresenter.class));
    }

    @Test
    public void onBannerError() {
        mSubject.onAdError(mMockPresenter);

        verify(mMockListener).onAdError(mMockPresenter);
    }

    @Test
    public void onBannerError_whenDestroyed() {
        mSubject.destroy();

        mSubject.onAdError(mMockPresenter);

        verify(mMockListener, never()).onAdError(any(AdPresenter.class));
    }
}
