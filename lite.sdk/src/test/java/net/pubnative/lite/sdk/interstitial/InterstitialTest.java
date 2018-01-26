package net.pubnative.lite.sdk.interstitial;

import net.pubnative.lite.sdk.api.InterstitialRequestManager;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenter;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenterFactory;
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
public class InterstitialTest {
    private static final String ZONE_ID = "2";
    @Mock
    private InterstitialPresenterFactory mMockPresenterFactory;
    @Mock
    private InterstitialRequestManager mMockRequestManager;
    @Mock
    private PNInitializationHelper mMockInitializationHelper;
    @Mock
    private InterstitialPresenter mMockPresenter;
    @Mock
    private Interstitial.Listener mMockListener;

    private Ad mTestAd;

    @InjectMocks
    private Interstitial mSubject;

    @Before
    public void setup() {
        initMocks(this);
        mTestAd = TestUtil.createTestInterstitialAd();
    }

    @Test
    public void load() {
        when(mMockInitializationHelper.isInitialized()).thenReturn(true);

        mSubject.load(ZONE_ID);

        verify(mMockRequestManager).setZoneId(ZONE_ID);
        verify(mMockRequestManager).requestAd();
    }

    @Test
    public void load_withSDKUninitialized() {
        mSubject.load(ZONE_ID);

        verify(mMockRequestManager, never()).setZoneId(anyString());
        verify(mMockRequestManager, never()).requestAd();
    }

    @Test
    public void load_withNullZoneId() {
        mSubject.load(null);

        verify(mMockRequestManager, never()).setZoneId(anyString());
        verify(mMockRequestManager, never()).requestAd();
    }

    @Test
    public void load_whenDestroyed() {
        mSubject.destroy();

        mSubject.load(ZONE_ID);

        verify(mMockRequestManager, never()).setZoneId(anyString());
        verify(mMockRequestManager, never()).requestAd();
    }

    @Test
    public void loadInterstitial() {
        when(mMockPresenterFactory.createInterstitialPresenter(mTestAd, mSubject))
                .thenReturn(mMockPresenter);

        mSubject.loadInterstitial(mTestAd);

        verify(mMockPresenter).load();
    }

    @Test
    public void loadInterstitial_withNullPresenter() {
        when(mMockInitializationHelper.isInitialized()).thenReturn(true);
        when(mMockPresenterFactory.createInterstitialPresenter(mTestAd, mSubject))
                .thenReturn(null);
        mSubject.setListener(mMockListener);
        mSubject.load(ZONE_ID);

        mSubject.loadInterstitial(mTestAd);

        verify(mMockListener).onInterstitialError(mSubject);
        verify(mMockPresenter, never()).load();
    }

    @Test
    public void loadInterstitial_whenDestroyed() {
        mSubject.destroy();

        mSubject.loadInterstitial(mTestAd);

        verify(mMockPresenterFactory, never())
                .createInterstitialPresenter(any(Ad.class), any(InterstitialPresenter.Listener.class));
        verify(mMockListener, never()).onInterstitialError(mSubject);
        verify(mMockPresenter, never()).load();
    }

    @Test
    public void destroy() {
        mSubject.destroy();

        verify(mMockRequestManager).destroy();
    }

    @Test
    public void onRequestFail() {
        mSubject.setListener(mMockListener);

        mSubject.onRequestFail(new Exception());

        verify(mMockListener).onInterstitialError(mSubject);
    }

    @Test
    public void onRequestFail_whenDestroyed() {
        mSubject.destroy();

        mSubject.onRequestFail(new Exception());

        verify(mMockListener, never()).onInterstitialError(any(Interstitial.class));
    }

    @Test
    public void onInterstitialLoaded() {
        mSubject.setListener(mMockListener);

        mSubject.onInterstitialLoaded(mMockPresenter);

        verify(mMockListener).onInterstitialLoaded(mSubject);
    }

    @Test
    public void onInterstitialLoaded_whenDestroyed() {
        mSubject.destroy();

        mSubject.onInterstitialLoaded(mMockPresenter);

        verify(mMockListener, never()).onInterstitialLoaded(any(Interstitial.class));
    }

    @Test
    public void onInterstitialClicked() {
        mSubject.setListener(mMockListener);

        mSubject.onInterstitialClicked(mMockPresenter);

        verify(mMockListener).onInterstitialClicked(mSubject);
    }

    @Test
    public void onInterstitialClicked_whenDestroyed() {
        mSubject.destroy();

        mSubject.onInterstitialClicked(mMockPresenter);

        verify(mMockListener, never()).onInterstitialClicked(any(Interstitial.class));
    }

    @Test
    public void onInterstitialDismissed() {
        when(mMockPresenterFactory.createInterstitialPresenter(mTestAd, mSubject))
                .thenReturn(mMockPresenter);
        mSubject.setListener(mMockListener);
        mSubject.loadInterstitial(mTestAd);

        mSubject.onInterstitialDismissed(mMockPresenter);

        verify(mMockPresenter).destroy();
        verify(mMockListener).onInterstitialDismissed(mSubject);
    }

    @Test
    public void onInterstitialDismissed_whenDestroyed() {
        mSubject.destroy();

        mSubject.onInterstitialDismissed(mMockPresenter);

        verify(mMockPresenter, never()).destroy();
        verify(mMockListener, never()).onInterstitialDismissed(any(Interstitial.class));
    }

    @Test
    public void onInterstitialError() {
        when(mMockPresenterFactory.createInterstitialPresenter(mTestAd, mSubject))
                .thenReturn(mMockPresenter);
        mSubject.setListener(mMockListener);
        mSubject.loadInterstitial(mTestAd);

        mSubject.onInterstitialError(mMockPresenter);

        verify(mMockPresenter).destroy();
        verify(mMockListener).onInterstitialError(mSubject);
    }

    @Test
    public void onInterstitialError_whenDestroyed() {
        mSubject.destroy();

        mSubject.onInterstitialError(mMockPresenter);

        verify(mMockPresenter, never()).destroy();
        verify(mMockListener, never()).onInterstitialError(any(Interstitial.class));
    }
}
