package net.pubnative.tarantula.sdk.banner.presenter;

import android.view.View;

import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.utils.AdTracker;
import net.pubnative.tarantula.sdk.utils.TestUtil;

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

/**
 * Created by erosgarciaponte on 24.01.18.
 */
@RunWith(RobolectricTestRunner.class)
public class BannerPresenterDecoratorTest {
    @Mock
    private BannerPresenter mMockPresenter;
    @Mock
    private AdTracker mMockAdTracker;
    @Mock
    private View mMockView;
    @Mock
    private BannerPresenter.Listener mMockListener;
    private Ad mTestAd;

    @InjectMocks
    private BannerPresenterDecorator mSubject;

    @Before
    public void setup() {
        initMocks(this);
        mTestAd = TestUtil.createTestBannerAd();
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
    public void destroy() {
        mSubject.destroy();

        verify(mMockPresenter).destroy();
    }

    @Test
    public void onBannerLoaded() {
        mSubject.onBannerLoaded(mMockPresenter, mMockView);

        verify(mMockAdTracker).trackImpression();
        verify(mMockListener).onBannerLoaded(mMockPresenter, mMockView);
    }

    @Test
    public void onBannerLoaded_whenDestroyed() {
        mSubject.destroy();

        mSubject.onBannerLoaded(mMockPresenter, mMockView);

        verify(mMockAdTracker, never()).trackImpression();
        verify(mMockListener, never()).onBannerLoaded(any(BannerPresenter.class), any(View.class));
    }

    @Test
    public void onBannerClicked() {
        mSubject.onBannerClicked(mMockPresenter);

        verify(mMockAdTracker).trackClick();
        verify(mMockListener).onBannerClicked(mMockPresenter);
    }

    @Test
    public void onBannerClicked_whenDestroyed() {
        mSubject.destroy();

        mSubject.onBannerClicked(mMockPresenter);

        verify(mMockAdTracker, never()).trackClick();
        verify(mMockListener, never()).onBannerClicked(any(BannerPresenter.class));
    }

    @Test
    public void onBannerError() {
        mSubject.onBannerError(mMockPresenter);

        verify(mMockListener).onBannerError(mMockPresenter);
    }

    @Test
    public void onBannerError_whenDestroyed() {
        mSubject.destroy();

        mSubject.onBannerError(mMockPresenter);

        verify(mMockListener, never()).onBannerError(any(BannerPresenter.class));
    }
}
