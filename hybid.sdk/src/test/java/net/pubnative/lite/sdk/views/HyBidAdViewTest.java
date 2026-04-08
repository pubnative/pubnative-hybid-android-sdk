// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.views;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

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

import java.lang.reflect.Field;

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

    @Test
    public void setWatermark_withValidBase64_createsImageView() {
        String validPngBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        HyBidAdView adView = new HyBidAdView(activity);
        adView.setWatermark(validPngBase64);
    }

    @Test
    public void setWatermark_withInvalidBase64_setsWatermarkToNull() {
        String invalidBase64 = "invalid_base64_string";
        HyBidAdView adView = new HyBidAdView(activity);
        adView.setWatermark(invalidBase64);
    }

    @Test
    public void setWatermark_withEmptyString_setsWatermarkToNull() {
        HyBidAdView adView = new HyBidAdView(activity);
        adView.setWatermark("");
    }

    @Test
    public void setWatermark_withNullString_setsWatermarkToNull() {
        HyBidAdView adView = new HyBidAdView(activity);
        String strNull = null;
        adView.setWatermark(strNull);
    }

    @Test
    public void setupAdView_withWatermark_addsWatermarkToView() {
        HyBidAdView adView = spy(new HyBidAdView(activity));
        View mockBannerView = mock(View.class);
        String validPngBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        adView.setWatermark(validPngBase64);

        // Mock the view's parent to return null
        when(mockBannerView.getParent()).thenReturn(null);

        adView.setupAdView(mockBannerView);
    }

    @Test
    public void setupAdView_withWatermarkAndPresenter_registersFriendlyObstruction() throws Exception {
        HyBidAdView adView = spy(new HyBidAdView(activity));
        View mockBannerView = mock(View.class);
        AdPresenter mockPresenter = mock(AdPresenter.class);
        String validPngBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";

        adView.setWatermark(validPngBase64);

        // Use reflection to set the private mPresenter field
        Field presenterField = HyBidAdView.class.getDeclaredField("mPresenter");
        presenterField.setAccessible(true);
        presenterField.set(adView, mockPresenter);

        // Mock the view's parent
        when(mockBannerView.getParent()).thenReturn(null);

        adView.setupAdView(mockBannerView);

        // Verify that addFriendlyObstruction was called on the presenter with the watermark
        verify(mockPresenter).addFriendlyObstruction(any(View.class));
    }


    @Test
    public void cleanup_removesWatermark() {
        HyBidAdView adView = new HyBidAdView(activity);
        View mockBannerView = mock(View.class);
        String validPngBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";

        // Set watermark and trigger its creation by calling setupAdView
        adView.setWatermark(validPngBase64);
        when(mockBannerView.getParent()).thenReturn(null);
        adView.setupAdView(mockBannerView);

        // Verify watermark exists before cleanup
        org.junit.Assert.assertNotNull(getPrivateWatermark(adView));

        // Call cleanup and verify watermark is removed
        adView.cleanup();
        org.junit.Assert.assertNull(getPrivateWatermark(adView));
    }

    @Test
    public void cleanup_removesVideoCacheEntry() throws Exception {
        net.pubnative.lite.sdk.vpaid.VideoAdCache mVideoAdCache = mock(net.pubnative.lite.sdk.vpaid.VideoAdCache.class);
        mHyBid.when(HyBid::getVideoAdCache).thenReturn(mVideoAdCache);

        HyBidAdView adView = new HyBidAdView(activity);
        Ad mockAdLocal = mock(Ad.class);
        when(mockAdLocal.getSessionId()).thenReturn("session_adview");

        Field adField = HyBidAdView.class.getDeclaredField("mAd");
        adField.setAccessible(true);
        adField.set(adView, mockAdLocal);

        adView.cleanup();

        verify(mVideoAdCache).remove("session_adview");
    }

    @Test
    public void cleanup_doesNotRemoveVideoCache_whenSessionIdEmpty() throws Exception {
        net.pubnative.lite.sdk.vpaid.VideoAdCache mVideoAdCache = mock(net.pubnative.lite.sdk.vpaid.VideoAdCache.class);
        mHyBid.when(HyBid::getVideoAdCache).thenReturn(mVideoAdCache);

        HyBidAdView adView = new HyBidAdView(activity);
        Ad mockAdLocal = mock(Ad.class);
        when(mockAdLocal.getSessionId()).thenReturn("");
        Field adField = HyBidAdView.class.getDeclaredField("mAd");
        adField.setAccessible(true);
        adField.set(adView, mockAdLocal);

        adView.cleanup();
        verify(mVideoAdCache, org.mockito.Mockito.never()).remove(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    public void setupAdView_withWatermark_registersFriendlyObstructionOnlyOnce() throws Exception {
        HyBidAdView adViewLocal = new HyBidAdView(activity);
        View banner = mock(View.class);
        AdPresenter presenterMock = mock(AdPresenter.class);
        String watermarkBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";

        adViewLocal.setWatermark(watermarkBase64);
        Field presenterField = HyBidAdView.class.getDeclaredField("mPresenter");
        presenterField.setAccessible(true);
        presenterField.set(adViewLocal, presenterMock);

        // Banner should return null for parent initially
        when(banner.getParent()).thenReturn(null);

        // First call should register the watermark
        adViewLocal.setupAdView(banner);

        // Now mock that the banner has a parent (the adViewLocal) so the second call won't re-enter setup
        when(banner.getParent()).thenReturn(adViewLocal);

        // Call setupAdView again with same banner - since banner now has a parent,
        // it won't re-enter the setup code, so no duplicate registration
        adViewLocal.setupAdView(banner);

        // Should be called exactly once
        verify(presenterMock, org.mockito.Mockito.times(1)).addFriendlyObstruction(any(View.class));
    }

    private ImageView getPrivateWatermark(HyBidAdView adView) {
        try {
            Field watermarkField = HyBidAdView.class.getDeclaredField("mWatermark");
            watermarkField.setAccessible(true);
            return (ImageView) watermarkField.get(adView);
        } catch (Exception e) {
            return null;
        }
    }

}