// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.views;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import android.app.Activity;
import android.os.Looper;
import android.view.View;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.api.BannerRequestManager;
import net.pubnative.lite.sdk.api.LeaderboardRequestManager;
import net.pubnative.lite.sdk.api.MRectRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.utils.sdkmanager.SdkManager;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

import java.time.Duration;

@RunWith(RobolectricTestRunner.class)
public class HyBidAdViewTest {

    private Activity activity;
    private HyBidAdView adView;

    @Mock
    private HyBidAdView.Listener mockListener;
    @Mock
    private AdPresenter mockPresenter;
    @Mock
    private Ad mockAd;
    @Mock
    private View mockBannerView;
    @Mock
    private SdkManager mockSdkManager;

    private MockedStatic<HyBid> mHyBid;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        activity = Robolectric.buildActivity(Activity.class).create().get();

        mHyBid = mockStatic(HyBid.class);
        mHyBid.when(HyBid::getSdkManager).thenReturn(mockSdkManager);
        mHyBid.when(HyBid::isInitialized).thenReturn(true);
    }

    @After
    public void tearDown() {
        mHyBid.close();
        activity = null;
        adView = null;
    }


    @Test
    public void testCreateBannerView() {
        adView = new HyBidBannerAdView(activity);
        MatcherAssert.assertThat(adView.getRequestManager(), Matchers.instanceOf(BannerRequestManager.class));
    }

    @Test
    public void testCreateMRectView() {
        adView = new HyBidMRectAdView(activity);
        MatcherAssert.assertThat(adView.getRequestManager(), Matchers.instanceOf(MRectRequestManager.class));
    }

    @Test
    public void testCreateLeaderboardView() {
        adView = new HyBidLeaderboardAdView(activity);
        MatcherAssert.assertThat(adView.getRequestManager(), Matchers.instanceOf(LeaderboardRequestManager.class));
    }

    @Test
    public void load_withValidZoneId_requestsAd() {
        // Use mockConstruction to inject the mock RequestManager
        try (MockedConstruction<RequestManager> mockedRequestManager = mockConstruction(RequestManager.class)) {
            HyBidAdView adView = new HyBidAdView(activity);
            String zoneId = "test_zone_id";

            adView.load(zoneId, mockListener);

            // Get the mock that was created and verify interactions
            RequestManager manager = mockedRequestManager.constructed().get(0);
            verify(manager).setZoneId(zoneId);
            verify(manager).setRequestListener(adView);
            verify(manager).requestAd();
        }
    }

    @Test
    public void destroy_cleansUpDependencies() {
        try (MockedConstruction<RequestManager> mockedRequestManager = mockConstruction(RequestManager.class)) {
            HyBidAdView adView = new HyBidAdView(activity);

            adView.destroy();

            // Verify destroy() is called on the mock RequestManager
            RequestManager manager = mockedRequestManager.constructed().get(0);
            verify(manager).destroy();
        }
    }

    @Test
    public void renderAd_whenAdIsExpired_invokesLoadFailed() {
        // Use mockConstruction to ensure we have a mock RequestManager, preventing real network calls.
        try (MockedConstruction<RequestManager> mockedRequestManager = mockConstruction(RequestManager.class)) {
            HyBidAdView adView = new HyBidAdView(activity);
            adView.mListener = mockListener;

            // 1. Disable auto-showing to prevent renderAd() from being called inside onRequestSuccess().
            adView.setAutoShowOnLoad(false);

            // 2. Call load(). This will use the MOCK RequestManager and set the initial load time.
            adView.load("test_zone_id", mockListener);

            // 3. Simulate a successful ad request to set the internal mAd object.
            // This will now call invokeOnLoadFinished() instead of renderAd().
            adView.onRequestSuccess(mockAd);

            // 4. Advance Robolectric's clock past the expiration time.
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(1800001));

            // 5. Attempt to render the ad. This is now the *first and only* call to renderAd.
            adView.renderAd();

            // 6. Assert that it failed ONLY ONCE due to being expired.
            verify(mockListener).onAdLoadFailed(any(HyBidError.class));
        }
    }

    @Test
    public void renderAd_whenPresenterFailsToCreate_invokesLoadFailed() {
        // Here we also use a spy to override createPresenter
        HyBidAdView adViewSpy = spy(new HyBidAdView(activity));
        doReturn(null).when(adViewSpy).createPresenter();

        // Simulate a successful ad load first
        adViewSpy.onRequestSuccess(mockAd);
        adViewSpy.mListener = mockListener;

        adViewSpy.renderAd();

        // Since createPresenter() returns null, renderAd() should fail.
        verify(mockListener).onAdLoadFailed(any(HyBidError.class));
    }

    @Test
    public void onRequestSuccess_withValidAd_rendersAd() {
        HyBidAdView adViewSpy = spy(new HyBidAdView(activity));
        adViewSpy.onRequestSuccess(mockAd);
        verify(adViewSpy).renderAd();
    }

    @Test
    public void onAdLoaded_withValidBanner_setsUpAdView() {
        HyBidAdView adViewSpy = spy(new HyBidAdView(activity));
        adViewSpy.onAdLoaded(mockPresenter, mockBannerView);
        verify(adViewSpy).setupAdView(mockBannerView);
    }
}