package net.pubnative.tarantula.sdk.mrect.controller;

import android.view.View;
import android.widget.FrameLayout;

import net.pubnative.tarantula.sdk.api.MRectRequestManager;
import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.mrect.presenter.MRectPresenter;
import net.pubnative.tarantula.sdk.mrect.presenter.MRectPresenterFactory;
import net.pubnative.tarantula.sdk.mrect.view.MRectView;
import net.pubnative.tarantula.sdk.utils.TarantulaInitializationHelper;
import net.pubnative.tarantula.sdk.utils.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by erosgarciaponte on 24.01.18.
 */
@RunWith(RobolectricTestRunner.class)
public class MRectControllerTest {
    private static final String ZONE_ID = "2";

    @Mock
    private MRectPresenterFactory mMockPresenterFactory;
    @Mock
    private MRectRequestManager mMockRequestManager;
    @Mock
    private TarantulaInitializationHelper mMockInitializationHelper;
    @Mock
    private MRectView mMockMRectView;
    @Mock
    private MRectPresenter mMockPresenter;
    @Mock
    private View mMockView;
    @Mock
    private MRectView.Listener mMockListener;
    private Ad mTestAd;

    @InjectMocks
    private MRectController mSubject;

    @Before
    public void setup() {
        initMocks(this);
        mTestAd = TestUtil.createTestMRectAd();
    }

    @Test
    public void load() {
        when(mMockInitializationHelper.isInitialized()).thenReturn(true);

        mSubject.load(ZONE_ID, mMockMRectView);

        verify(mMockRequestManager).setZoneId(ZONE_ID);
        verify(mMockRequestManager).requestAd();
    }

    @Test
    public void load_withSDKUninitialized() {
        mSubject.load(ZONE_ID, mMockMRectView);

        verify(mMockRequestManager, never()).setZoneId(anyString());
        verify(mMockRequestManager, never()).requestAd();
    }

    @Test
    public void load_withNullZoneId() {
        mSubject.load(null, mMockMRectView);

        verify(mMockRequestManager, never()).setZoneId(anyString());
        verify(mMockRequestManager, never()).requestAd();
    }

    @Test
    public void load_withNullMRectView() {
        mSubject.load(ZONE_ID, null);

        verify(mMockRequestManager, never()).setZoneId(anyString());
        verify(mMockRequestManager, never()).requestAd();
    }

    @Test
    public void load_whenDestroyed() {
        mSubject.destroy();

        mSubject.load(ZONE_ID, mMockMRectView);

        verify(mMockRequestManager, never()).setZoneId(anyString());
        verify(mMockRequestManager, never()).requestAd();
    }

    @Test
    public void showAd() {
        when(mMockPresenterFactory.createMRectPresenter(mTestAd, mSubject))
                .thenReturn(mMockPresenter);

        mSubject.showAd(mTestAd);

        verify(mMockPresenter).load();
    }

    @Test
    public void showAd_withNullPresenter() {
        when(mMockInitializationHelper.isInitialized()).thenReturn(true);
        when(mMockPresenterFactory.createMRectPresenter(mTestAd, mSubject))
                .thenReturn(null);
        mSubject.setListener(mMockListener);
        mSubject.load(ZONE_ID, mMockMRectView);

        mSubject.showAd(mTestAd);

        verify(mMockListener).onMRectError(mMockMRectView);
        verify(mMockPresenter, never()).load();
    }

    @Test
    public void showAd_whenDestroyed() {
        mSubject.destroy();

        mSubject.showAd(mTestAd);

        verify(mMockPresenterFactory, never()).createMRectPresenter(any(Ad.class), any(MRectPresenter.Listener.class));
        verify(mMockListener, never()).onMRectError(mMockMRectView);
        verify(mMockPresenter, never()).load();
    }

    @Test
    public void destroy() {
        mSubject.destroy();

        verify(mMockRequestManager).destroy();
    }

    @Test
    public void onRequestFail() {
        when(mMockInitializationHelper.isInitialized()).thenReturn(true);
        mSubject.setListener(mMockListener);
        mSubject.load(ZONE_ID, mMockMRectView);

        mSubject.onRequestFail(new Exception());

        verify(mMockListener).onMRectError(mMockMRectView);
    }

    @Test
    public void onRequestFail_whenDestroyed() {
        mSubject.destroy();

        mSubject.onRequestFail(new Exception());

        verify(mMockListener, never()).onMRectError(any(MRectView.class));
    }

    @Test
    public void onMRectLoaded() {
        when(mMockInitializationHelper.isInitialized()).thenReturn(true);
        when(mMockPresenter.getAd()).thenReturn(mTestAd);
        mSubject.setListener(mMockListener);
        mSubject.load(ZONE_ID, mMockMRectView);

        mSubject.onMRectLoaded(mMockPresenter, mMockView);

        verify(mMockMRectView).removeAllViews();
        verify(mMockView).setLayoutParams(any(FrameLayout.LayoutParams.class));
        verify(mMockMRectView).addView(mMockView);
        verify(mMockListener).onMRectLoaded(mMockMRectView);
    }

    @Test
    public void onMRectLoaded_whenDestroyed() {
        mSubject.destroy();

        mSubject.onMRectLoaded(mMockPresenter, mMockView);

        verify(mMockMRectView, never()).removeAllViews();
        verify(mMockView, never()).setLayoutParams(any(FrameLayout.LayoutParams.class));
        verify(mMockMRectView, never()).addView(any(View.class));
        verify(mMockListener, never()).onMRectLoaded(any(MRectView.class));
    }

    @Test
    public void onMRectClicked() {
        when(mMockInitializationHelper.isInitialized()).thenReturn(true);
        mSubject.setListener(mMockListener);
        mSubject.load(ZONE_ID, mMockMRectView);

        mSubject.onMRectClicked(mMockPresenter);

        verify(mMockListener).onMRectClicked(mMockMRectView);
    }

    @Test
    public void onMRectClicked_whenDestroyed() {
        mSubject.destroy();

        mSubject.onMRectClicked(mMockPresenter);

        verify(mMockListener, never()).onMRectClicked(any(MRectView.class));
    }

    @Test
    public void onMRectError() {
        when(mMockInitializationHelper.isInitialized()).thenReturn(true);
        when(mMockPresenter.getAd()).thenReturn(mTestAd);
        mSubject.setListener(mMockListener);
        mSubject.load(ZONE_ID, mMockMRectView);

        mSubject.onMRectError(mMockPresenter);

        verify(mMockListener).onMRectError(mMockMRectView);
    }

    @Test
    public void onMRectError_whenDestroyed() {
        mSubject.destroy();

        mSubject.onMRectError(mMockPresenter);

        verify(mMockListener, never()).onMRectError(any(MRectView.class));
    }
}
