package net.pubnative.lite.sdk.banner.controller;

import android.view.View;
import android.widget.FrameLayout;

import net.pubnative.lite.sdk.api.BannerRequestManager;
import net.pubnative.lite.sdk.banner.presenter.BannerPresenter;
import net.pubnative.lite.sdk.banner.presenter.BannerPresenterFactory;
import net.pubnative.lite.sdk.banner.view.BannerView;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.PNInitializationHelper;
import net.pubnative.lite.sdk.utils.TestUtil;

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
public class BannerControllerTest {
    private static final String ZONE_ID = "2";

    @Mock
    private BannerPresenterFactory mMockPresenterFactory;
    @Mock
    private BannerRequestManager mMockRequestManager;
    @Mock
    private PNInitializationHelper mMockInitializationHelper;
    @Mock
    private BannerView mMockBannerView;
    @Mock
    private BannerPresenter mMockPresenter;
    @Mock
    private View mMockView;
    @Mock
    private BannerView.Listener mMockListener;
    private Ad mTestAd;

    @InjectMocks
    private BannerController mSubject;

    @Before
    public void setup() {
        initMocks(this);
        mTestAd = TestUtil.createTestBannerAd();
    }

    @Test
    public void load() {
        when(mMockInitializationHelper.isInitialized()).thenReturn(true);

        mSubject.load(ZONE_ID, mMockBannerView);

        verify(mMockRequestManager).setZoneId(ZONE_ID);
        verify(mMockRequestManager).requestAd();
    }

    @Test
    public void load_withSDKUninitialized() {
        mSubject.load(ZONE_ID, mMockBannerView);

        verify(mMockRequestManager, never()).setZoneId(anyString());
        verify(mMockRequestManager, never()).requestAd();
    }

    @Test
    public void load_withNullZoneId() {
        mSubject.load(null, mMockBannerView);

        verify(mMockRequestManager, never()).setZoneId(anyString());
        verify(mMockRequestManager, never()).requestAd();
    }

    @Test
    public void load_withNullBannerView() {
        mSubject.load(ZONE_ID, null);

        verify(mMockRequestManager, never()).setZoneId(anyString());
        verify(mMockRequestManager, never()).requestAd();
    }

    @Test
    public void load_whenDestroyed() {
        mSubject.destroy();

        mSubject.load(ZONE_ID, mMockBannerView);

        verify(mMockRequestManager, never()).setZoneId(anyString());
        verify(mMockRequestManager, never()).requestAd();
    }

    @Test
    public void showAd() {
        when(mMockPresenterFactory.createBannerPresenter(mTestAd, mSubject))
                .thenReturn(mMockPresenter);

        mSubject.showAd(mTestAd);

        verify(mMockPresenter).load();
    }

    @Test
    public void showAd_withNullPresenter() {
        when(mMockInitializationHelper.isInitialized()).thenReturn(true);
        when(mMockPresenterFactory.createBannerPresenter(mTestAd, mSubject))
                .thenReturn(null);
        mSubject.setListener(mMockListener);
        mSubject.load(ZONE_ID, mMockBannerView);

        mSubject.showAd(mTestAd);

        verify(mMockListener).onBannerError(mMockBannerView);
        verify(mMockPresenter, never()).load();
    }

    @Test
    public void showAd_whenDestroyed() {
        mSubject.destroy();

        mSubject.showAd(mTestAd);

        verify(mMockPresenterFactory, never()).createBannerPresenter(any(Ad.class), any(BannerPresenter.Listener.class));
        verify(mMockListener, never()).onBannerError(mMockBannerView);
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
        mSubject.load(ZONE_ID, mMockBannerView);

        mSubject.onRequestFail(new Exception());

        verify(mMockListener).onBannerError(mMockBannerView);
    }

    @Test
    public void onRequestFail_whenDestroyed() {
        mSubject.destroy();

        mSubject.onRequestFail(new Exception());

        verify(mMockListener, never()).onBannerError(any(BannerView.class));
    }

    @Test
    public void onBannerLoaded() {
        when(mMockInitializationHelper.isInitialized()).thenReturn(true);
        when(mMockPresenter.getAd()).thenReturn(mTestAd);
        mSubject.setListener(mMockListener);
        mSubject.load(ZONE_ID, mMockBannerView);

        mSubject.onBannerLoaded(mMockPresenter, mMockView);

        verify(mMockBannerView).removeAllViews();
        verify(mMockView).setLayoutParams(any(FrameLayout.LayoutParams.class));
        verify(mMockBannerView).addView(mMockView);
        verify(mMockListener).onBannerLoaded(mMockBannerView);
    }

    @Test
    public void onBannerLoaded_whenDestroyed() {
        mSubject.destroy();

        mSubject.onBannerLoaded(mMockPresenter, mMockView);

        verify(mMockBannerView, never()).removeAllViews();
        verify(mMockView, never()).setLayoutParams(any(FrameLayout.LayoutParams.class));
        verify(mMockBannerView, never()).addView(any(View.class));
        verify(mMockListener, never()).onBannerLoaded(any(BannerView.class));
    }

    @Test
    public void onBannerClicked() {
        when(mMockInitializationHelper.isInitialized()).thenReturn(true);
        mSubject.setListener(mMockListener);
        mSubject.load(ZONE_ID, mMockBannerView);

        mSubject.onBannerClicked(mMockPresenter);

        verify(mMockListener).onBannerClicked(mMockBannerView);
    }

    @Test
    public void onBannerClicked_whenDestroyed() {
        mSubject.destroy();

        mSubject.onBannerClicked(mMockPresenter);

        verify(mMockListener, never()).onBannerClicked(any(BannerView.class));
    }

    @Test
    public void onBannerError() {
        when(mMockInitializationHelper.isInitialized()).thenReturn(true);
        when(mMockPresenter.getAd()).thenReturn(mTestAd);
        mSubject.setListener(mMockListener);
        mSubject.load(ZONE_ID, mMockBannerView);

        mSubject.onBannerError(mMockPresenter);

        verify(mMockListener).onBannerError(mMockBannerView);
    }

    @Test
    public void onBannerError_whenDestroyed() {
        mSubject.destroy();

        mSubject.onBannerError(mMockPresenter);

        verify(mMockListener, never()).onBannerError(any(BannerView.class));
    }
}
