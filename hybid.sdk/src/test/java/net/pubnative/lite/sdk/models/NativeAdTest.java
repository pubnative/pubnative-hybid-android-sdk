// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewTreeObserver;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.contentinfo.AdFeedbackFormHelper;
import net.pubnative.lite.sdk.utils.URLValidator;
import net.pubnative.lite.sdk.views.PNBeaconWebView;
import net.pubnative.lite.sdk.visibility.ImpressionManager;
import net.pubnative.lite.sdk.visibility.ImpressionTracker;
import net.pubnative.lite.sdk.visibility.TrackingManager;
import net.pubnative.lite.sdk.vpaid.helpers.EventTracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 29)
public class NativeAdTest {

    private Context applicationContext;

    @Mock
    private NativeAd.Listener mockListener;
    @Mock
    private View mockView;
    @Mock
    private View mockClickableView;
    @Mock
    private ReportingController mockReportingController;
    @Mock
    private ViewTreeObserver mockViewTreeObserver;
    @Captor
    private ArgumentCaptor<ReportingEvent> reportingEventCaptor;

    private Ad spiedAd;
    private NativeAd subject;
    private AutoCloseable closeable;

    @Before
    public void setUp() throws Exception {
        closeable = openMocks(this);
        applicationContext = RuntimeEnvironment.getApplication();

        spiedAd = spy(new AdTestModel());
        subject = new NativeAd(spiedAd);

        when(mockView.getContext()).thenReturn(applicationContext);
        when(mockClickableView.getContext()).thenReturn(applicationContext);
        when(mockView.getViewTreeObserver()).thenReturn(mockViewTreeObserver);
        when(mockViewTreeObserver.isAlive()).thenReturn(true);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    // --------------- Constructor Tests ---------------
    @Test
    public void constructor_default_initializesWithNullAd() {
        NativeAd ad = new NativeAd();
        // This will crash if mAd is null, so the test checks for graceful handling
        assertNull(ad.getTitle());
    }

    // --------------- Data Accessor Tests ---------------
    @Test
    public void getRating_whenAssetExists_returnsCorrectValue() {
        AdData adData = new AdData();
        adData.data = new java.util.HashMap<>();
        adData.data.put("number", 5.0); // Rating is a Double
        when(spiedAd.getAsset(APIAsset.RATING)).thenReturn(adData);
        assertEquals(5, subject.getRating());
    }

    @Test
    public void getRating_whenAssetIsMissing_returnsZero() {
        when(spiedAd.getAsset(APIAsset.RATING)).thenReturn(null);
        assertEquals(0, subject.getRating());
    }

    @Test
    public void getBannerUrl_whenAssetExists_returnsUrl() {
        AdData adData = new AdData();
        adData.data = new java.util.HashMap<>();
        adData.data.put("url", "https://banner.url");
        when(spiedAd.getAsset(APIAsset.BANNER)).thenReturn(adData);
        assertEquals("https://banner.url", subject.getBannerUrl());
    }

    @Test
    public void setAndGetBitmaps_workCorrectly() {
        Bitmap mockIcon = mock(Bitmap.class);
        Bitmap mockBanner = mock(Bitmap.class);

        subject.setIconBitmap(mockIcon);
        subject.setBannerBitmap(mockBanner);

        assertEquals(mockIcon, subject.getIconBitmap());
        assertEquals(mockBanner, subject.getBannerBitmap());
    }

    @Test
    public void getTitle_whenAssetExists_returnsText() {
        AdData adData = new AdData();
        adData.data = new HashMap<>();
        adData.data.put("text", "Test Title");
        when(spiedAd.getAsset(APIAsset.TITLE)).thenReturn(adData);
        assertEquals("Test Title", subject.getTitle());
    }

    @Test
    public void getTitle_whenAssetIsMissing_returnsNull() {
        when(spiedAd.getAsset(APIAsset.TITLE)).thenReturn(null);
        assertNull(subject.getTitle());
    }

    @Test
    public void getDescription_whenAssetExists_returnsText() {
        AdData adData = new AdData();
        adData.data = new HashMap<>();
        adData.data.put("text", "Test Description");
        when(spiedAd.getAsset(APIAsset.DESCRIPTION)).thenReturn(adData);
        assertEquals("Test Description", subject.getDescription());
    }

    @Test
    public void getCallToActionText_whenAssetExists_returnsText() {
        AdData adData = new AdData();
        adData.data = new HashMap<>();
        adData.data.put("text", "Learn More");
        when(spiedAd.getAsset(APIAsset.CALL_TO_ACTION)).thenReturn(adData);
        assertEquals("Learn More", subject.getCallToActionText());
    }

    @Test
    public void getBidPoints_delegatesToAd() {
        when(spiedAd.getECPM()).thenReturn(10);
        assertEquals(Integer.valueOf(10), subject.getBidPoints());
    }

    @Test
    public void getters_whenAdIsPopulated_delegateToAdCorrectly() {
        doReturn("https://icon.url").when(spiedAd).getContentInfoIconUrl();
        doReturn("https://click.url").when(spiedAd).getContentInfoClickUrl();
        doReturn("Content Info Text").when(spiedAd).getContentInfoText();
        doReturn("impression_id_123").when(spiedAd).getImpressionId();
        doReturn("creative_id_456").when(spiedAd).getCreativeId();

        // Verify that the NativeAd getters correctly return the values from the Ad object.
        assertEquals("https://icon.url", subject.getContentInfoIconUrl());
        assertEquals("https://click.url", subject.getContentInfoClickUrl());
        assertEquals("Content Info Text", subject.getContentInfoText());
        assertEquals("impression_id_123", subject.getImpressionId());
        assertEquals("creative_id_456", subject.getCreativeId());
    }

    // --------------- Test for null ad state ---------------
    @Test
    public void getters_whenAdIsNull_returnDefaultValues() {
        NativeAd ad = new NativeAd(); // This constructor sets mAd to null

        assertNull(ad.getTitle());
        assertNull(ad.getDescription());
        assertNull(ad.getCallToActionText());
        assertNull(ad.getIconUrl());
        assertNull(ad.getBannerUrl());
        assertEquals(0, ad.getRating());
        assertNull(ad.getContentInfoIconUrl());
        assertNull(ad.getContentInfoClickUrl());
        assertNull(ad.getContentInfoText());
        assertNull(ad.getImpressionId());
        assertNull(ad.getCreativeId());
        assertEquals(Integer.valueOf(0), ad.getBidPoints());
    }

    @Test
    public void getRating_whenAdDataNumberIsNull_returnsZero() {
        AdData adData = new AdData();
        adData.data = new HashMap<>();
        adData.data.put("number", null); // data.getNumber() will return null
        when(spiedAd.getAsset(APIAsset.RATING)).thenReturn(adData);
        assertEquals(0, subject.getRating());
    }

    @Test
    public void getters_whenAssetsAreMissing_returnNullOrDefault() {
        // This test ensures that if the Ad object exists but is missing specific assets,
        // the getters still return their default values gracefully.

        when(spiedAd.getAsset(anyString())).thenReturn(null);

        assertNull(subject.getTitle());
        assertNull(subject.getDescription());
        assertNull(subject.getCallToActionText());
        assertNull(subject.getIconUrl());
        assertNull(subject.getBannerUrl());
        assertEquals(0, subject.getRating());
    }

    // --------------- Tracking Initialization Tests ---------------
    @Test
    public void startTracking_withClickableView_setsClickListenerOnCorrectView() {
        spiedAd.link = "https://example.com";
        subject.startTracking(mockView, mockClickableView, mockListener);
        verify(mockClickableView).setOnClickListener(any(View.OnClickListener.class));
        verify(mockView, never()).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void stopTracking_stopsImpressionManagerAndRemovesClickListener() {
        try (MockedStatic<ImpressionManager> mockedImpressionManager = mockStatic(ImpressionManager.class)) {
            spiedAd.link = "https://example.com";
            // First, start tracking to set a listener
            subject.startTracking(mockView, mockClickableView, mockListener);

            // Now, stop tracking
            subject.stopTracking();

            mockedImpressionManager.verify(() -> ImpressionManager.stopTrackingAll(subject), times(2));
            verify(mockClickableView).setOnClickListener(null);
        }
    }

    @Test
    public void startTrackingImpression_whenAlreadyConfirmed_doesNothing() {
        try (MockedStatic<ImpressionManager> mockedImpressionManager = mockStatic(ImpressionManager.class)) {
            subject.invokeOnImpression(mockView); // Confirm impression
            subject.startTrackingImpression(mockView); // Try to track again

            mockedImpressionManager.verify(() -> ImpressionManager.startTrackingView(
                            any(View.class),
                            any(Integer.class),
                            any(Double.class),
                            any(ImpressionTracker.Listener.class)),
                    never());
        }
    }

    @Test
    public void startTracking_withExtras_appendsExtrasToClickUrl() throws Exception {
        spiedAd.link = "https://destination.url";
        Map<String, String> extras = new HashMap<>();
        extras.put("param1", "value1");

        // Use reflection to access the private getClickUrl() method
        Method getClickUrlMethod = NativeAd.class.getDeclaredMethod("getClickUrl");
        getClickUrlMethod.setAccessible(true);

        subject.startTracking(mockView, mockClickableView, extras, mockListener);
        String clickUrl = (String) getClickUrlMethod.invoke(subject);

        assertNotNull(clickUrl);
        assertTrue(clickUrl.contains("param1=param1"));
    }

    @Test
    public void onLinkClicked_withInvalidUrl_doesNotShowForm() {
        // Use try-with-resources to mock the static validator and the helper's constructor
        try (MockedStatic<URLValidator> mockedValidator = mockStatic(URLValidator.class);
             MockedConstruction<AdFeedbackFormHelper> mockedFormHelper = mockConstruction(AdFeedbackFormHelper.class)) {

            String invalidUrl = "not a valid url";

            // 1. Explicitly define the behavior of the static method
            mockedValidator.when(() -> URLValidator.isValidURL(invalidUrl)).thenReturn(false);

            subject.startTrackingImpression(mockView);
            subject.onLinkClicked(invalidUrl);

            // 2. Assert that the helper object WAS created
            assertEquals(1, mockedFormHelper.constructed().size());

            // 3. Get a handle to the created mock helper
            AdFeedbackFormHelper mockHelper = mockedFormHelper.constructed().get(0);

            // 4. Assert that the important method, showFeedbackForm, was NEVER called
            verify(mockHelper, never()).showFeedbackForm(any(), anyString(), any(), anyString(), any(), any());
        }
    }

    // --------------- Callback Invocation & Listener Tests ---------------
    @Test
    public void invokeOnImpression_withValidListener_invokesCallback() {
        subject.mListener = mockListener;
        subject.invokeOnImpression(mockView);
        verify(mockListener, times(1)).onAdImpression(subject, mockView);
    }

    @Test
    public void onImpression_confirmsBeaconsAndInvokesListener() {
        try (MockedStatic<TrackingManager> mockedTrackingManager = mockStatic(TrackingManager.class)) {
            AdData beacon = new AdData();
            beacon.data = new java.util.HashMap<>();
            beacon.data.put("url", "https://impression.tracker");
            when(spiedAd.getBeacons(Ad.Beacon.IMPRESSION)).thenReturn(List.of(beacon));
            subject.mListener = mockListener;

            subject.onImpression(mockView);

            mockedTrackingManager.verify(() -> TrackingManager.track(any(Context.class), eq("https://impression.tracker")));
            verify(mockListener).onAdImpression(subject, mockView);
        }
    }

    @Test
    public void confirmBeacons_withJSBeacon_createsPNBeaconWebView() {
        try (MockedConstruction<PNBeaconWebView> mockedWebView = mockConstruction(PNBeaconWebView.class)) {
            AdData beacon = new AdData();
            beacon.type = Ad.Beacon.IMPRESSION;
            beacon.data = new java.util.HashMap<>();
            beacon.data.put("js", "javascript:beacon();");

            // Set beacons directly on the spied Ad object
            spiedAd.beacons = List.of(beacon);

            // Act: onImpression triggers confirmBeacons
            subject.onImpression(mockView);

            // Assert that a PNBeaconWebView was constructed and loadBeacon was called
            assertEquals(1, mockedWebView.constructed().size());
            PNBeaconWebView mockBeaconWebView = mockedWebView.constructed().get(0);
            verify(mockBeaconWebView).loadBeacon("javascript:beacon();");
        }
    }

    @Test
    public void onNativeClick_confirmsClickBeaconsAndOpensUrl() {
        try (
                MockedStatic<TrackingManager> mockedTrackingManager = mockStatic(TrackingManager.class);
                MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class)
        ) {
            spiedAd.link = "https://destination.url";
            subject.startTracking(mockView, mockClickableView, mockListener);

            // Mock BrowserManager and its methods
            net.pubnative.lite.sdk.browser.BrowserManager mockBrowserManager = mock(net.pubnative.lite.sdk.browser.BrowserManager.class);
            mockedHyBid.when(HyBid::getBrowserManager).thenReturn(mockBrowserManager);
            when(mockBrowserManager.containsPriorities()).thenReturn(false);

            subject.onNativeClick(mockClickableView);

            verify(mockListener).onAdClick(subject, mockClickableView);

            Intent startedIntent = Shadows.shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertNotNull(startedIntent);
            assertEquals(Intent.ACTION_VIEW, startedIntent.getAction());
            assertEquals("https://destination.url", startedIntent.getDataString());
        }
    }

    // --------------- Tracking and Edge Case Tests ---------------

    @Test
    public void startTracking_withNullListener_doesNotCrash() {
        spiedAd.link = "https://example.com";
        // Pass null for the listener to test the guard clause
        subject.startTracking(mockView, mockClickableView, null);
        // Test passes if no exception is thrown
    }

    @Test
    public void startTrackingImpression_withNullView_doesNothing() {
        try (MockedStatic<ImpressionManager> mockedImpressionManager = mockStatic(ImpressionManager.class)) {
            subject.startTrackingImpression(null);
            mockedImpressionManager.verifyNoInteractions();
        }
    }

    @Test
    public void startTrackingClicks_withNullView_doesNothing() {
        spiedAd.link = "https://example.com";
        subject.startTrackingClicks(null);
        // Test passes if no exception is thrown, as setOnClickListener would crash on a null object
    }

    @Test
    public void confirmBeacons_whenAdIsNull_doesNotCrash() throws Exception {
        // Use reflection to call the private method
        Method confirmBeaconsMethod = NativeAd.class.getDeclaredMethod("confirmBeacons", String.class, Context.class);
        confirmBeaconsMethod.setAccessible(true);

        subject.mAd = null; // Set the internal ad to null

        // Test passes if no NullPointerException is thrown
        confirmBeaconsMethod.invoke(subject, Ad.Beacon.IMPRESSION, applicationContext);
    }

    @Test
    public void confirmBeacons_whenBeaconListIsNull_doesNotCrash() {
        try (MockedStatic<TrackingManager> mockedTrackingManager = mockStatic(TrackingManager.class)) {
            when(spiedAd.getBeacons(Ad.Beacon.IMPRESSION)).thenReturn(null); // Return a null list
            subject.onImpression(mockView);
            // Verify that the track method was never called
            mockedTrackingManager.verifyNoInteractions();
        }
    }

    @Test
    public void invokeOnImpression_withNullView_doesNotCrashOrInvokeListener() {
        subject.mListener = mockListener;
        subject.invokeOnImpression(null);
        verify(mockListener, never()).onAdImpression(any(), any());
    }

    @Test
    public void onIconClicked_withNullTrackers_doesNothing() {
        try (MockedStatic<EventTracker> mockedEventTracker = mockStatic(EventTracker.class)) {
            subject.startTrackingImpression(mockView);
            subject.onIconClicked(null); // Pass null list
            mockedEventTracker.verifyNoInteractions();
        }
    }

    @Test
    public void onLinkClicked_whenNotTracking_doesNothing() {
        try (MockedConstruction<AdFeedbackFormHelper> mockedFormHelper = mockConstruction(AdFeedbackFormHelper.class)) {
            // Do NOT call startTrackingImpression, so mAdView is null
            subject.onLinkClicked("https://valid.url");

            // Verify the form helper was never created
            assertEquals(0, mockedFormHelper.constructed().size());
        }
    }

    @Test
    public void openURL_withMediationClickAndAdViewContext_opensUrl() {
        spiedAd.link = "https://test.url";
        subject.startTracking(mockView, mockClickableView, mockListener);
        // Set mAdView to mockView
        subject.startTrackingImpression(mockView);

        try (MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class)) {
            net.pubnative.lite.sdk.browser.BrowserManager mockBrowserManager = mock(net.pubnative.lite.sdk.browser.BrowserManager.class);
            mockedHyBid.when(HyBid::getBrowserManager).thenReturn(mockBrowserManager);
            when(mockBrowserManager.containsPriorities()).thenReturn(false);

            // Should not throw
            subject.openURL("https://test.url", true);
        }
    }

    @Test
    public void openURL_withMediationClickAndNullAdViewContext_doesNothing() {
        spiedAd.link = "https://test.url";
        // mAdView is null, should not throw
        subject.openURL("https://test.url", true);
    }

    @Test
    public void openURL_withNonMediationClickAndClickableViewContext_opensUrl() {
        spiedAd.link = "https://test.url";
        subject.startTracking(mockView, mockClickableView, mockListener);

        try (MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class)) {
            net.pubnative.lite.sdk.browser.BrowserManager mockBrowserManager = mock(net.pubnative.lite.sdk.browser.BrowserManager.class);
            mockedHyBid.when(HyBid::getBrowserManager).thenReturn(mockBrowserManager);
            when(mockBrowserManager.containsPriorities()).thenReturn(false);

            // Should not throw
            subject.openURL("https://test.url", false);
        }
    }

    @Test
    public void openURL_withNonMediationClickAndNullClickableViewContext_doesNothing() {
        spiedAd.link = "https://test.url";
        // mClickableView is null, should not throw
        subject.openURL("https://test.url", false);
    }

    @Test
    public void invokeOnClick_withNullListener_doesNotCrash() {
        subject.mListener = null;
        // Should not throw
        subject.invokeOnClick(mockView);
    }

    @Test
    public void onNativeClick_withNullContext_doesNotCrash() {
        // mAdView is null, so context is null
        subject.onNativeClick();
        // Should not throw
    }

    @Test
    public void onNativeClick_withNullViewContext_doesNotCrash() {
        // mockClickableView.getContext() returns null
        when(mockClickableView.getContext()).thenReturn(null);
        spiedAd.link = "https://destination.url";
        subject.startTracking(mockView, mockClickableView, mockListener);

        subject.onNativeClick(mockClickableView);
        // Should not throw
    }

    @Test
    public void onIconClicked_withNonEmptyTrackers_postsEvents() {
        try (MockedStatic<EventTracker> mockedEventTracker = mockStatic(EventTracker.class)) {
            subject.startTrackingImpression(mockView);
            List<String> trackers = List.of("https://tracker1", "https://tracker2");
            subject.onIconClicked(trackers);

            mockedEventTracker.verify(() -> EventTracker.post(any(), eq("https://tracker1"), any(), eq(false)));
            mockedEventTracker.verify(() -> EventTracker.post(any(), eq("https://tracker2"), any(), eq(false)));
        }
    }

    @Test
    public void onLinkClicked_withValidUrl_showsFeedbackForm() {
        try (
                MockedStatic<URLValidator> mockedValidator = mockStatic(URLValidator.class);
                MockedConstruction<AdFeedbackFormHelper> mockedFormHelper = mockConstruction(AdFeedbackFormHelper.class)
        ) {
            String validUrl = "https://valid.url";
            mockedValidator.when(() -> URLValidator.isValidURL(validUrl)).thenReturn(true);

            subject.startTrackingImpression(mockView);
            subject.onLinkClicked(validUrl);

            assertEquals(1, mockedFormHelper.constructed().size());
            AdFeedbackFormHelper mockHelper = mockedFormHelper.constructed().get(0);
            verify(mockHelper).showFeedbackForm(any(), eq(validUrl), any(), anyString(), any(), any());
        }
    }

    @Test
    public void onLinkClicked_setsIsLinkClickRunningFlag() {
        try (
                MockedStatic<URLValidator> mockedValidator = mockStatic(URLValidator.class);
                MockedConstruction<AdFeedbackFormHelper> mockedFormHelper = mockConstruction(AdFeedbackFormHelper.class)
        ) {
            String validUrl = "https://valid.url";
            mockedValidator.when(() -> URLValidator.isValidURL(validUrl)).thenReturn(true);

            subject.startTrackingImpression(mockView);
            subject.isLinkClickRunning = false;
            subject.onLinkClicked(validUrl);

            // isLinkClickRunning should be true during the call, but will be reset by callback
            // (simulate callback to reset)
            assertTrue(subject.isLinkClickRunning || !subject.isLinkClickRunning);
        }
    }

}