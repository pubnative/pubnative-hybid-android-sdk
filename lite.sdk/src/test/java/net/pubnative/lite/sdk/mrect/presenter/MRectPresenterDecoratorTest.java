package net.pubnative.lite.sdk.mrect.presenter;

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

/**
 * Created by erosgarciaponte on 24.01.18.
 */
@RunWith(RobolectricTestRunner.class)
public class MRectPresenterDecoratorTest {
    @Mock
    private MRectPresenter mMockPresenter;
    @Mock
    private AdTracker mMockAdTracker;
    @Mock
    private View mMockView;
    @Mock
    private MRectPresenter.Listener mMockListener;
    private Ad mTestAd;

    @InjectMocks
    private MRectPresenterDecorator mSubject;

    @Before
    public void setup() {
        initMocks(this);
        mTestAd = TestUtil.createTestMRectAd();
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
    public void onMRectLoaded() {
        mSubject.onMRectLoaded(mMockPresenter, mMockView);

        verify(mMockAdTracker).trackImpression();
        verify(mMockListener).onMRectLoaded(mMockPresenter, mMockView);
    }

    @Test
    public void onMRectLoaded_whenDestroyed() {
        mSubject.destroy();

        mSubject.onMRectLoaded(mMockPresenter, mMockView);

        verify(mMockAdTracker, never()).trackImpression();
        verify(mMockListener, never()).onMRectLoaded(any(MRectPresenter.class), any(View.class));
    }

    @Test
    public void onMRectClicked() {
        mSubject.onMRectClicked(mMockPresenter);

        verify(mMockAdTracker).trackClick();
        verify(mMockListener).onMRectClicked(mMockPresenter);
    }

    @Test
    public void onMRectClicked_whenDestroyed() {
        mSubject.destroy();

        mSubject.onMRectClicked(mMockPresenter);

        verify(mMockAdTracker, never()).trackClick();
        verify(mMockListener, never()).onMRectClicked(any(MRectPresenter.class));
    }

    @Test
    public void onMRectError() {
        mSubject.onMRectError(mMockPresenter);

        verify(mMockListener).onMRectError(mMockPresenter);
    }

    @Test
    public void onMRectError_whenDestroyed() {
        mSubject.destroy();

        mSubject.onMRectError(mMockPresenter);

        verify(mMockListener, never()).onMRectError(any(MRectPresenter.class));
    }
}
