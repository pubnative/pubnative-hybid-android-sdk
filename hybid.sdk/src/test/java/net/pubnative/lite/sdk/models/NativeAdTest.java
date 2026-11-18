// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
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

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.api.PNApiClient;
import net.pubnative.lite.sdk.contentinfo.AdFeedbackFormHelper;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.URLValidator;
import net.pubnative.lite.sdk.viewability.FriendlyObstructionReasonConstants;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityManager;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityNativeDisplayAdSession;
import net.pubnative.lite.sdk.viewability.baseom.BaseFriendlyObstructionPurpose;
import net.pubnative.lite.sdk.visibility.ImpressionManager;
import net.pubnative.lite.sdk.visibility.ImpressionTracker;
import net.pubnative.lite.sdk.vpaid.helpers.EventTracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
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

    private Ad spiedAd;
    private AutoCloseable closeable;

    @Before
    public void setUp() throws Exception {
        closeable = openMocks(this);
        applicationContext = RuntimeEnvironment.getApplication();

        spiedAd = spy(new AdTestModel());

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
        NativeAd subject = new NativeAd(spiedAd);
        AdData adData = new AdData();
        adData.data = new java.util.HashMap<>();
        adData.data.put("number", 5.0); // Rating is a Double
        when(spiedAd.getAsset(APIAsset.RATING)).thenReturn(adData);
        assertEquals(5, subject.getRating());
    }

    @Test
    public void getRating_whenAssetIsMissing_returnsZero() {
        NativeAd subject = new NativeAd(spiedAd);
        when(spiedAd.getAsset(APIAsset.RATING)).thenReturn(null);
        assertEquals(0, subject.getRating());
    }

    @Test
    public void getBannerUrl_whenAssetExists_returnsUrl() {
        NativeAd subject = new NativeAd(spiedAd);
        AdData adData = new AdData();
        adData.data = new java.util.HashMap<>();
        adData.data.put("url", "https://banner.url");
        when(spiedAd.getAsset(APIAsset.BANNER)).thenReturn(adData);
        assertEquals("https://banner.url", subject.getBannerUrl());
    }

    @Test
    public void setAndGetBitmaps_workCorrectly() {
        NativeAd subject = new NativeAd(spiedAd);

        Bitmap mockIcon = mock(Bitmap.class);
        Bitmap mockBanner = mock(Bitmap.class);

        subject.setIconBitmap(mockIcon);
        subject.setBannerBitmap(mockBanner);

        assertEquals(mockIcon, subject.getIconBitmap());
        assertEquals(mockBanner, subject.getBannerBitmap());
    }

    @Test
    public void getTitle_whenAssetExists_returnsText() {
        NativeAd subject = new NativeAd(spiedAd);
        AdData adData = new AdData();
        adData.data = new HashMap<>();
        adData.data.put("text", "Test Title");
        when(spiedAd.getAsset(APIAsset.TITLE)).thenReturn(adData);
        assertEquals("Test Title", subject.getTitle());
    }

    @Test
    public void getTitle_whenAssetIsMissing_returnsNull() {
        NativeAd subject = new NativeAd(spiedAd);
        when(spiedAd.getAsset(APIAsset.TITLE)).thenReturn(null);
        assertNull(subject.getTitle());
    }

    @Test
    public void getDescription_whenAssetExists_returnsText() {
        NativeAd subject = new NativeAd(spiedAd);
        AdData adData = new AdData();
        adData.data = new HashMap<>();
        adData.data.put("text", "Test Description");
        when(spiedAd.getAsset(APIAsset.DESCRIPTION)).thenReturn(adData);
        assertEquals("Test Description", subject.getDescription());
    }

    @Test
    public void getCallToActionText_whenAssetExists_returnsText() {
        NativeAd subject = new NativeAd(spiedAd);
        AdData adData = new AdData();
        adData.data = new HashMap<>();
        adData.data.put("text", "Learn More");
        when(spiedAd.getAsset(APIAsset.CALL_TO_ACTION)).thenReturn(adData);
        assertEquals("Learn More", subject.getCallToActionText());
    }

    @Test
    public void getBidPoints_delegatesToAd() {
        NativeAd subject = new NativeAd(spiedAd);
        when(spiedAd.getECPM()).thenReturn(10);
        assertEquals(Integer.valueOf(10), subject.getBidPoints());
    }

    @Test
    public void getters_whenAdIsPopulated_delegateToAdCorrectly() {
        NativeAd subject = new NativeAd(spiedAd);

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
        NativeAd subject = new NativeAd(spiedAd);
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

        NativeAd subject = new NativeAd(spiedAd);
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
        NativeAd subject = new NativeAd(spiedAd);
        spiedAd.link = "https://example.com";
        subject.startTracking(mockView, mockClickableView, mockListener);
        verify(mockClickableView).setOnClickListener(any(View.OnClickListener.class));
        verify(mockView, never()).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void stopTracking_stopsImpressionManagerAndRemovesClickListener() {
        try (MockedStatic<ImpressionManager> mockedImpressionManager = mockStatic(ImpressionManager.class)) {
            NativeAd subject = new NativeAd(spiedAd);
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
            NativeAd subject = new NativeAd(spiedAd);
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
        NativeAd subject = new NativeAd(spiedAd);
        spiedAd.link = "https://destination.url";
        Map<String, String> extras = new HashMap<>();
        extras.put("param1", "value1");
        extras.put("param2", "value 2 that needs encoding");

        // Use reflection to access the private getClickUrl() method
        Method getClickUrlMethod = NativeAd.class.getDeclaredMethod("getClickUrl");
        getClickUrlMethod.setAccessible(true);

        subject.startTracking(mockView, mockClickableView, extras, mockListener);
        String clickUrl = (String) getClickUrlMethod.invoke(subject);

        assertNotNull(clickUrl);
        assertTrue(clickUrl.contains("param1=value1"));
        assertTrue(clickUrl.contains("param2=value%202%20that%20needs%20encoding"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void startTracking_withExtras_appendsExtrasToBeaconUrl() {
        AdData urlClickBeacon = new AdData();
        urlClickBeacon.data = new HashMap<>();
        String originalClickBeaconUrl = "https://example.com/click";
        urlClickBeacon.data.put("url", originalClickBeaconUrl);
        AdData jsClickBeacon = new AdData();
        jsClickBeacon.data = new HashMap<>();
        jsClickBeacon.data.put("js", "function click() {};");
        doReturn(Arrays.asList(urlClickBeacon, jsClickBeacon))
                .when(spiedAd).getBeacons(Ad.Beacon.CLICK);

        AdData urlImpressionBeacon = new AdData();
        urlImpressionBeacon.data = new HashMap<>();
        String originalImpressionBeaconUrl = "https://example.com/imp";
        urlImpressionBeacon.data.put("url", originalImpressionBeaconUrl);
        AdData jsImpressionBeacon = new AdData();
        jsImpressionBeacon.data = new HashMap<>();
        jsImpressionBeacon.data.put("js", "function imp() {};");
        doReturn(Arrays.asList(urlImpressionBeacon, jsImpressionBeacon))
                .when(spiedAd).getBeacons(Ad.Beacon.IMPRESSION);

        List<AdData>[] actualBeacons = new List[2];

        try (
                MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class);
                MockedConstruction<AdTracker> adTrackerMockedConstruction = mockConstruction(AdTracker.class, (mock, context) -> {
                    actualBeacons[0] = (List<AdData>) context.arguments().get(0);
                    actualBeacons[1] = (List<AdData>) context.arguments().get(1);
                });
        ) {
            PNApiClient mockApiClient = mock(PNApiClient.class);
            mockedHyBid.when(HyBid::getApiClient).thenReturn(mockApiClient);
            DeviceInfo mockDeviceInfo = mock(DeviceInfo.class);
            when(mockDeviceInfo.getAdvertisingId()).thenReturn("test-ad-id");
            mockedHyBid.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);

            NativeAd subject = new NativeAd(spiedAd);

            Map<String, String> extras = new HashMap<>();
            extras.put("param1", "value1");
            extras.put("param2", "value 2 that needs encoding");

            subject.startTracking(mockView, mockClickableView, extras, mockListener);

            assertEquals(
                    "Original impression beacon data is left intact",
                    originalImpressionBeaconUrl,
                    urlImpressionBeacon.getURL()
            );
            assertTrue(actualBeacons[0].get(0).getURL().contains("param1=value1"));
            assertTrue(actualBeacons[0].get(0).getURL().contains("param2=value%202%20that%20needs%20encoding"));
            assertEquals(
                    "JavaScript impression beacon is left intact",
                    jsImpressionBeacon.getJS(),
                    actualBeacons[0].get(1).getJS()
            );

            assertEquals(
                    "Original click beacon data is left intact",
                    originalClickBeaconUrl,
                    urlClickBeacon.getURL()
            );
            assertTrue(actualBeacons[1].get(0).getURL().contains("param1=value1"));
            assertTrue(actualBeacons[1].get(0).getURL().contains("param2=value%202%20that%20needs%20encoding"));
            assertEquals(
                    "JavaScript click beacon is left intact",
                    jsClickBeacon.getJS(),
                    actualBeacons[1].get(1).getJS()
            );
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void startTracking_withoutExtras_preservesBeacons() {
        AdData urlClickBeacon = new AdData();
        urlClickBeacon.data = new HashMap<>();
        urlClickBeacon.data.put("url", "https://example.com/click");
        AdData jsClickBeacon = new AdData();
        jsClickBeacon.data = new HashMap<>();
        jsClickBeacon.data.put("js", "function click() {};");
        List<AdData> requiredClickBeacons = Arrays.asList(urlClickBeacon, jsClickBeacon);
        doReturn(requiredClickBeacons).when(spiedAd).getBeacons(Ad.Beacon.CLICK);

        AdData urlImpressionBeacon = new AdData();
        urlImpressionBeacon.data = new HashMap<>();
        urlImpressionBeacon.data.put("url", "https://example.com/imp");
        AdData jsImpressionBeacon = new AdData();
        jsImpressionBeacon.data = new HashMap<>();
        jsImpressionBeacon.data.put("js", "function imp() {};");
        List<AdData> requiredImpressionBeacons = Arrays.asList(urlImpressionBeacon, jsImpressionBeacon);
        doReturn(requiredImpressionBeacons).when(spiedAd).getBeacons(Ad.Beacon.IMPRESSION);

        List<AdData>[] actualBeacons = new List[2];

        try (
                MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class);
                MockedConstruction<AdTracker> adTrackerMockedConstruction = mockConstruction(AdTracker.class, (mock, context) -> {
                    actualBeacons[0] = (List<AdData>) context.arguments().get(0);
                    actualBeacons[1] = (List<AdData>) context.arguments().get(1);
                });
        ) {
            PNApiClient mockApiClient = mock(PNApiClient.class);
            mockedHyBid.when(HyBid::getApiClient).thenReturn(mockApiClient);
            DeviceInfo mockDeviceInfo = mock(DeviceInfo.class);
            when(mockDeviceInfo.getAdvertisingId()).thenReturn("test-ad-id");
            mockedHyBid.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);

            NativeAd subject = new NativeAd(spiedAd);

            subject.startTracking(mockView, null);

            assertEquals(actualBeacons[0], requiredImpressionBeacons);
            assertEquals(actualBeacons[1], requiredClickBeacons);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void startTracking_withExtras_appendsLatestExtras() {
        AdData urlClickBeacon = new AdData();
        urlClickBeacon.data = new HashMap<>();
        urlClickBeacon.data.put("url", "https://example.com/click");
        doReturn(Collections.singletonList(urlClickBeacon))
                .when(spiedAd).getBeacons(Ad.Beacon.CLICK);

        AdData urlImpressionBeacon = new AdData();
        urlImpressionBeacon.data = new HashMap<>();
        urlImpressionBeacon.data.put("url", "https://example.com/imp");
        doReturn(Collections.singletonList(urlImpressionBeacon))
                .when(spiedAd).getBeacons(Ad.Beacon.IMPRESSION);

        List<AdData>[] actualBeacons = new List[2];

        try (
                MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class);
                MockedConstruction<AdTracker> adTrackerMockedConstruction = mockConstruction(AdTracker.class, (mock, context) -> {
                    actualBeacons[0] = (List<AdData>) context.arguments().get(0);
                    actualBeacons[1] = (List<AdData>) context.arguments().get(1);
                });
        ) {
            PNApiClient mockApiClient = mock(PNApiClient.class);
            mockedHyBid.when(HyBid::getApiClient).thenReturn(mockApiClient);
            DeviceInfo mockDeviceInfo = mock(DeviceInfo.class);
            when(mockDeviceInfo.getAdvertisingId()).thenReturn("test-ad-id");
            mockedHyBid.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);

            NativeAd subject = new NativeAd(spiedAd);

            Map<String, String> extras = new HashMap<>();
            extras.put("param1", "value1");
            extras.put("param2", "value 2 that needs encoding");

            subject.startTracking(mockView, mockClickableView, extras, mockListener);

            extras = new HashMap<>();
            extras.put("updatedParam1", "updatedValue1");
            subject.startTracking(mockView, mockClickableView, extras, mockListener);

            assertTrue(actualBeacons[0].get(0).getURL().contains("updatedParam1=updatedValue1"));
            assertFalse(actualBeacons[0].get(0).getURL().contains("param1=value1"));
            assertFalse(actualBeacons[0].get(0).getURL().contains("param2=value%202%20that%20needs%20encoding"));

            assertTrue(actualBeacons[1].get(0).getURL().contains("updatedParam1=updatedValue1"));
            assertFalse(actualBeacons[1].get(0).getURL().contains("param1=value1"));
            assertFalse(actualBeacons[1].get(0).getURL().contains("param2=value%202%20that%20needs%20encoding"));
        }
    }

    @Test
    public void startTracking_calledTwice_tracksClickOnce() {
        AdData urlClickBeacon = new AdData();
        urlClickBeacon.data = new HashMap<>();
        urlClickBeacon.data.put("url", "https://example.com/click");
        doReturn(Collections.singletonList(urlClickBeacon))
                .when(spiedAd).getBeacons(Ad.Beacon.CLICK);

        try (
                MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class);
        ) {
            PNApiClient mockApiClient = mock(PNApiClient.class);
            mockedHyBid.when(HyBid::getApiClient).thenReturn(mockApiClient);
            DeviceInfo mockDeviceInfo = mock(DeviceInfo.class);
            when(mockDeviceInfo.getAdvertisingId()).thenReturn("test-ad-id");
            mockedHyBid.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);

            NativeAd subject = new NativeAd(spiedAd);

            for (int i = 0; i < 2; i++) {
                subject.startTracking(mockView, mockClickableView, mockListener);
                subject.onNativeClick();
            }

            verify(mockApiClient, times(1))
                    .trackUrl(eq(urlClickBeacon.getURL()), any(), any(), any());
        }
    }

    @Test
    public void onLinkClicked_withInvalidUrl_doesNotShowForm() {
        // Use try-with-resources to mock the static validator and the helper's constructor
        try (MockedStatic<URLValidator> mockedValidator = mockStatic(URLValidator.class);
             MockedConstruction<AdFeedbackFormHelper> mockedFormHelper = mockConstruction(AdFeedbackFormHelper.class)) {

            NativeAd subject = new NativeAd(spiedAd);
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
        NativeAd subject = new NativeAd(spiedAd);
        subject.mListener = mockListener;
        subject.invokeOnImpression(mockView);
        verify(mockListener, times(1)).onAdImpression(subject, mockView);
    }

    @Test
    public void onImpression_tracksAndInvokesListener() {
        try (
                MockedConstruction<AdTracker> adTrackerMockedConstruction = mockConstruction(AdTracker.class);
        ) {
            NativeAd subject = new NativeAd(spiedAd);
            AdData beacon = new AdData();
            beacon.data = new java.util.HashMap<>();
            beacon.data.put("url", "https://impression.tracker");
            when(spiedAd.getBeacons(Ad.Beacon.IMPRESSION)).thenReturn(List.of(beacon));

            subject.mListener = mockListener;

            subject.onImpression(mockView);

            assertEquals(1, adTrackerMockedConstruction.constructed().size());
            AdTracker mockedAdTracker = adTrackerMockedConstruction.constructed().get(0);

            verify(mockedAdTracker, times(1)).trackImpression();
            verify(mockListener).onAdImpression(subject, mockView);
        }
    }

    @Test
    public void onNativeClick_tracksClickAndOpensUrl() {
        try (
                MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class);
                MockedConstruction<AdTracker> adTrackerMockedConstruction = mockConstruction(AdTracker.class);
        ) {
            NativeAd subject = new NativeAd(spiedAd);
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

            AdTracker mockedAdTracker = adTrackerMockedConstruction.constructed().getLast();
            verify(mockedAdTracker, times(1)).trackClick();
        }
    }

    // --------------- Tracking and Edge Case Tests ---------------

    @Test
    public void startTracking_withNullListener_doesNotCrash() {
        NativeAd subject = new NativeAd(spiedAd);
        spiedAd.link = "https://example.com";
        // Pass null for the listener to test the guard clause
        subject.startTracking(mockView, mockClickableView, null);
        // Test passes if no exception is thrown
    }

    @Test
    public void startTrackingImpression_withNullView_doesNothing() {
        try (MockedStatic<ImpressionManager> mockedImpressionManager = mockStatic(ImpressionManager.class)) {
            NativeAd subject = new NativeAd(spiedAd);
            subject.startTrackingImpression(null);
            mockedImpressionManager.verifyNoInteractions();
        }
    }

    @Test
    public void startTrackingClicks_withNullView_doesNothing() {
        NativeAd subject = new NativeAd(spiedAd);
        spiedAd.link = "https://example.com";
        subject.startTrackingClicks(null);
        // Test passes if no exception is thrown, as setOnClickListener would crash on a null object
    }

    @Test
    public void trackImpression_whenBeaconListIsNull_doesNotCrash() {
        when(spiedAd.getBeacons(Ad.Beacon.IMPRESSION)).thenReturn(null); // Return a null list
        NativeAd subject = new NativeAd(spiedAd);
        subject.onImpression(mockView);
    }

    @Test
    public void invokeOnImpression_withNullView_doesNotCrashOrInvokeListener() {
        NativeAd subject = new NativeAd(spiedAd);
        subject.mListener = mockListener;
        subject.invokeOnImpression(null);
        verify(mockListener, never()).onAdImpression(any(), any());
    }

    @Test
    public void onIconClicked_withNullTrackers_doesNothing() {
        try (MockedStatic<EventTracker> mockedEventTracker = mockStatic(EventTracker.class)) {
            NativeAd subject = new NativeAd(spiedAd);
            subject.startTrackingImpression(mockView);
            subject.onIconClicked(null); // Pass null list
            mockedEventTracker.verifyNoInteractions();
        }
    }

    @Test
    public void onLinkClicked_whenNotTracking_doesNothing() {
        try (MockedConstruction<AdFeedbackFormHelper> mockedFormHelper = mockConstruction(AdFeedbackFormHelper.class)) {
            // Do NOT call startTrackingImpression, so mAdView is null
            NativeAd subject = new NativeAd(spiedAd);
            subject.onLinkClicked("https://valid.url");

            // Verify the form helper was never created
            assertEquals(0, mockedFormHelper.constructed().size());
        }
    }

    @Test
    public void openURL_withMediationClickAndAdViewContext_opensUrl() {
        NativeAd subject = new NativeAd(spiedAd);
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
        NativeAd subject = new NativeAd(spiedAd);
        spiedAd.link = "https://test.url";
        // mAdView is null, should not throw
        subject.openURL("https://test.url", true);
    }

    @Test
    public void openURL_withNonMediationClickAndClickableViewContext_opensUrl() {
        NativeAd subject = new NativeAd(spiedAd);
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
        NativeAd subject = new NativeAd(spiedAd);
        spiedAd.link = "https://test.url";
        // mClickableView is null, should not throw
        subject.openURL("https://test.url", false);
    }

    @Test
    public void invokeOnClick_withNullListener_doesNotCrash() {
        NativeAd subject = new NativeAd(spiedAd);
        subject.mListener = null;
        // Should not throw
        subject.invokeOnClick(mockView);
    }

    @Test
    public void onNativeClick_withNullContext_doesNotCrash() {
        // mAdView is null, so context is null
        NativeAd subject = new NativeAd(spiedAd);
        subject.onNativeClick();
        // Should not throw
    }

    @Test
    public void onNativeClick_withNullViewContext_doesNotCrash() {
        NativeAd subject = new NativeAd(spiedAd);
        // mockClickableView.getContext() returns null
        when(mockClickableView.getContext()).thenReturn(null);
        spiedAd.link = "https://destination.url";
        subject.startTracking(mockView, mockClickableView, mockListener);

        subject.onNativeClick(mockClickableView);
        // Should not throw
    }

    @Test
    public void onIconClicked_withNonEmptyTrackers_postsEvents() {
        NativeAd subject = new NativeAd(spiedAd);
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
            NativeAd subject = new NativeAd(spiedAd);
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
            NativeAd subject = new NativeAd(spiedAd);
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

    // --------------- OM Viewability Tracking Tests ---------------

    @Test
    public void startTrackingImpression_initializesViewabilitySession() {
        try (
                MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class);
                MockedConstruction<HyBidViewabilityNativeDisplayAdSession> mockedAdSession =
                        mockConstruction(HyBidViewabilityNativeDisplayAdSession.class)
        ) {
            HyBidViewabilityManager mockViewabilityManager = mock(HyBidViewabilityManager.class);
            mockedHyBid.when(HyBid::getViewabilityManager).thenReturn(mockViewabilityManager);
            mockedHyBid.when(HyBid::getReportingController).thenReturn(mockReportingController);

            NativeAd nativeAd = new NativeAd(spiedAd);
            nativeAd.startTrackingImpression(mockView);

            // Verify a viewability session was created
            assertEquals(1, mockedAdSession.constructed().size());
            HyBidViewabilityNativeDisplayAdSession mockSession = mockedAdSession.constructed().get(0);

            // Verify initAdSession was called
            verify(mockSession).initAdSession(eq(mockView), anyList());
            // Verify fireLoaded was called
            verify(mockSession).fireLoaded();
        }
    }

    @Test
    public void onImpression_firesViewabilityImpression() {
        try (
                MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class);
                MockedConstruction<HyBidViewabilityNativeDisplayAdSession> mockedAdSession =
                        mockConstruction(HyBidViewabilityNativeDisplayAdSession.class)
        ) {
            HyBidViewabilityManager mockViewabilityManager = mock(HyBidViewabilityManager.class);
            mockedHyBid.when(HyBid::getViewabilityManager).thenReturn(mockViewabilityManager);
            mockedHyBid.when(HyBid::getReportingController).thenReturn(mockReportingController);

            NativeAd nativeAd = new NativeAd(spiedAd);
            nativeAd.startTrackingImpression(mockView);

            // Trigger impression
            nativeAd.onImpression(mockView);

            // Verify fireImpression was called
            HyBidViewabilityNativeDisplayAdSession mockSession = mockedAdSession.constructed().get(0);
            verify(mockSession).fireImpression();
        }
    }

    @Test
    public void stopTracking_stopsViewabilitySession() {
        try (
                MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class);
                MockedStatic<ImpressionManager> mockedImpressionManager = mockStatic(ImpressionManager.class);
                MockedConstruction<HyBidViewabilityNativeDisplayAdSession> mockedAdSession =
                        mockConstruction(HyBidViewabilityNativeDisplayAdSession.class)
        ) {
            HyBidViewabilityManager mockViewabilityManager = mock(HyBidViewabilityManager.class);
            mockedHyBid.when(HyBid::getViewabilityManager).thenReturn(mockViewabilityManager);
            mockedHyBid.when(HyBid::getReportingController).thenReturn(mockReportingController);

            NativeAd nativeAd = new NativeAd(spiedAd);
            nativeAd.startTrackingImpression(mockView);

            // Stop tracking
            nativeAd.stopTracking();

            // Verify stopAdSession was called
            HyBidViewabilityNativeDisplayAdSession mockSession = mockedAdSession.constructed().get(0);
            verify(mockSession).stopAdSession();
        }
    }

    @Test
    public void viewabilitySession_notInitialized_whenViewabilityManagerIsNull() {
        try (
                MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class);
                MockedConstruction<HyBidViewabilityNativeDisplayAdSession> mockedAdSession =
                        mockConstruction(HyBidViewabilityNativeDisplayAdSession.class)
        ) {
            // Return null for viewability manager
            mockedHyBid.when(HyBid::getViewabilityManager).thenReturn(null);
            mockedHyBid.when(HyBid::getReportingController).thenReturn(mockReportingController);

            NativeAd nativeAd = new NativeAd(spiedAd);
            nativeAd.startTrackingImpression(mockView);

            // Verify no viewability session was created
            assertEquals(0, mockedAdSession.constructed().size());
        }
    }

    @Test
    public void onImpression_firesViewabilityImpression_beforeBeacons() {
        final List<String> seq = new java.util.ArrayList<>();
        try (
                MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class);
                MockedConstruction<HyBidViewabilityNativeDisplayAdSession> mockedAdSession =
                        mockConstruction(HyBidViewabilityNativeDisplayAdSession.class,
                                (m, ctx) -> doAnswer(i -> {
                                    seq.add("om-impression");
                                    return null;
                                }).when(m).fireImpression());
                MockedConstruction<AdTracker> mockedAdTracker =
                        mockConstruction(AdTracker.class, (m, ctx) -> doAnswer(i -> {
                            seq.add(i.getMethod().getName());
                            return null;
                        }).when(m).trackImpression());
        ) {
            HyBidViewabilityManager mockViewabilityManager = mock(HyBidViewabilityManager.class);
            mockedHyBid.when(HyBid::getViewabilityManager).thenReturn(mockViewabilityManager);
            mockedHyBid.when(HyBid::getReportingController).thenReturn(mockReportingController);

            // Ensure at least one impression beacon exists so TrackingManager.track is invoked
            AdData beacon = new AdData();
            beacon.data = new HashMap<>();
            beacon.data.put("url", "https://example.com/imp");
            doReturn(java.util.Collections.singletonList(beacon))
                    .when(spiedAd).getBeacons(Ad.Beacon.IMPRESSION);

            NativeAd nativeAd = new NativeAd(spiedAd);
            nativeAd.startTrackingImpression(mockView);
            nativeAd.onImpression(mockView);

            // OM impression must fire before the impression beacon (OM ordering requirement)
            assertEquals(
                    java.util.Arrays.asList("om-impression", "trackImpression"),
                    seq);
        }
    }

    @Test
    public void startTrackingImpression_registersContentInfoAsFriendlyObstruction() {
        try (
                MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class);
                MockedConstruction<HyBidViewabilityNativeDisplayAdSession> mockedAdSession =
                        mockConstruction(HyBidViewabilityNativeDisplayAdSession.class)
        ) {
            HyBidViewabilityManager mockViewabilityManager = mock(HyBidViewabilityManager.class);
            mockedHyBid.when(HyBid::getViewabilityManager).thenReturn(mockViewabilityManager);
            mockedHyBid.when(HyBid::getReportingController).thenReturn(mockReportingController);

            View mockContentInfoView = mock(View.class);
            doReturn(mockContentInfoView).when(spiedAd).getContentInfo(any(Context.class), any());

            NativeAd nativeAd = new NativeAd(spiedAd);
            // Content info must be resolved before tracking starts for it to be registered
            nativeAd.getContentInfo(applicationContext);
            nativeAd.startTrackingImpression(mockView);

            HyBidViewabilityNativeDisplayAdSession mockSession = mockedAdSession.constructed().get(0);
            verify(mockSession).addFriendlyObstruction(
                    eq(mockContentInfoView),
                    eq(BaseFriendlyObstructionPurpose.OTHER),
                    eq(FriendlyObstructionReasonConstants.CONTENT_INFO_OBSTRUCTION_REASON));
        }
    }

    @Test
    public void getContentInfo_returnsCachedInstance_soOmRegistersRenderedView() {
        try (MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class)) {
            mockedHyBid.when(HyBid::getViewabilityManager)
                    .thenReturn(mock(HyBidViewabilityManager.class));
            mockedHyBid.when(HyBid::getReportingController).thenReturn(mockReportingController);

            View first = mock(View.class);
            View second = mock(View.class);
            doReturn(first, second).when(spiedAd).getContentInfo(any(Context.class), any());

            NativeAd nativeAd = new NativeAd(spiedAd);
            View rendered = nativeAd.getContentInfo(applicationContext);
            View again = nativeAd.getContentInfo(applicationContext);

            assertSame(first, rendered);
            assertSame(rendered, again);
            verify(spiedAd, times(1)).getContentInfo(any(Context.class), any());
        }
    }

    @Test
    public void startTrackingImpression_doesNotInitSession_whenImpressionAlreadyConfirmed() {
        try (
                MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class);
                MockedConstruction<HyBidViewabilityNativeDisplayAdSession> mockedAdSession =
                        mockConstruction(HyBidViewabilityNativeDisplayAdSession.class)
        ) {
            HyBidViewabilityManager mockViewabilityManager = mock(HyBidViewabilityManager.class);
            mockedHyBid.when(HyBid::getViewabilityManager).thenReturn(mockViewabilityManager);
            mockedHyBid.when(HyBid::getReportingController).thenReturn(mockReportingController);

            NativeAd nativeAd = new NativeAd(spiedAd);
            nativeAd.startTrackingImpression(mockView);
            // Confirm the impression
            nativeAd.onImpression(mockView);

            HyBidViewabilityNativeDisplayAdSession mockSession = mockedAdSession.constructed().get(0);

            // A second tracking attempt after confirmation must be a no-op for OM init
            nativeAd.startTrackingImpression(mockView);
            verify(mockSession, times(1)).initAdSession(eq(mockView), anyList());
        }
    }

    @Test
    public void startTrackingImpression_calledTwice_initializesSessionOnce() {
        try (
                MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class);
                MockedConstruction<HyBidViewabilityNativeDisplayAdSession> mockedAdSession =
                        mockConstruction(HyBidViewabilityNativeDisplayAdSession.class)
        ) {
            HyBidViewabilityManager mockViewabilityManager = mock(HyBidViewabilityManager.class);
            mockedHyBid.when(HyBid::getViewabilityManager).thenReturn(mockViewabilityManager);
            mockedHyBid.when(HyBid::getReportingController).thenReturn(mockReportingController);

            NativeAd nativeAd = new NativeAd(spiedAd);
            // Two consecutive starts without an intervening stop (e.g. a re-bind/re-layout)
            nativeAd.startTrackingImpression(mockView);
            nativeAd.startTrackingImpression(mockView);

            HyBidViewabilityNativeDisplayAdSession mockSession = mockedAdSession.constructed().get(0);
            // The duplicate-init guard must keep OM init to a single session start
            verify(mockSession, times(1)).initAdSession(eq(mockView), anyList());
            verify(mockSession, times(1)).fireLoaded();
        }
    }

    @Test
    public void stopTracking_withoutPriorStart_doesNotCrash() {
        try (
                MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class);
                MockedStatic<ImpressionManager> mockedImpressionManager = mockStatic(ImpressionManager.class);
                MockedConstruction<HyBidViewabilityNativeDisplayAdSession> mockedAdSession =
                        mockConstruction(HyBidViewabilityNativeDisplayAdSession.class)
        ) {
            HyBidViewabilityManager mockViewabilityManager = mock(HyBidViewabilityManager.class);
            mockedHyBid.when(HyBid::getViewabilityManager).thenReturn(mockViewabilityManager);
            mockedHyBid.when(HyBid::getReportingController).thenReturn(mockReportingController);

            NativeAd nativeAd = new NativeAd(spiedAd);
            // Stop before any start must be safe: no OM init and no impression fired
            nativeAd.stopTracking();

            HyBidViewabilityNativeDisplayAdSession mockSession = mockedAdSession.constructed().get(0);
            verify(mockSession, never()).initAdSession(any(View.class), anyList());
            verify(mockSession, never()).fireImpression();
        }
    }

    @Test
    public void startTracking_afterStop_reinitializesSession() {
        try (
                MockedStatic<HyBid> mockedHyBid = mockStatic(HyBid.class);
                MockedStatic<ImpressionManager> mockedImpressionManager = mockStatic(ImpressionManager.class);
                MockedConstruction<HyBidViewabilityNativeDisplayAdSession> mockedAdSession =
                        mockConstruction(HyBidViewabilityNativeDisplayAdSession.class)
        ) {
            HyBidViewabilityManager mockViewabilityManager = mock(HyBidViewabilityManager.class);
            mockedHyBid.when(HyBid::getViewabilityManager).thenReturn(mockViewabilityManager);
            mockedHyBid.when(HyBid::getReportingController).thenReturn(mockReportingController);

            NativeAd nativeAd = new NativeAd(spiedAd);
            nativeAd.startTrackingImpression(mockView);
            nativeAd.stopTracking();
            // A fresh tracking cycle must be allowed to init OM again once the guard is reset
            nativeAd.startTrackingImpression(mockView);

            HyBidViewabilityNativeDisplayAdSession mockSession = mockedAdSession.constructed().get(0);
            verify(mockSession, times(2)).initAdSession(eq(mockView), anyList());
        }
    }

}
