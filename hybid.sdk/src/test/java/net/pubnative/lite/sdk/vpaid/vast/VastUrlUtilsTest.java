// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.vast;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import android.location.Location;
import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.UserDataManager;
import net.pubnative.lite.sdk.location.HyBidLocationManager;

/**
 * Unit tests for the refactored VastUrlUtils class.
 */
@RunWith(RobolectricTestRunner.class)
public class VastUrlUtilsTest {

    @Test
    public void formatURL_withAllParameters_buildsCorrectUrl() {
        // 1. The base URL with all placeholders.
        String baseUrl = "https://example.com/vast?adid={{adid}}&bundle={{bundle}}&dnt={{dnt}}" +
                "&lat={{lat}}&lon={{lon}}&ua={{user_agent}}&w={{width}}&h={{height}}" +
                "&gdpr={{gdpr}}&consent={{gdpr_consent}}&us_privacy={{us_privacy}}";

        // 2. Create a parameters object with specific test data.
        VastUrlParameters params = new VastUrlParameters.Builder()
                .advertisingId("test-ad-id")
                .bundleId("com.test.app")
                .dnt("1")
                .latitude("34.05")
                .longitude("-118.24")
                .userAgent("Test-User-Agent")
                .deviceWidth("1080")
                .deviceHeight("1920")
                .gdpr("1")
                .gdprConsent("test-consent-string")
                .usPrivacy("1YNY")
                .build();

        String actualUrl = VastUrlUtils.formatURL(baseUrl, params);

        // 3. Define the expected URL after all replacements.
        String expectedUrl = "https://example.com/vast?adid=test-ad-id&bundle=com.test.app&dnt=1" +
                "&lat=34.05&lon=-118.24&ua=Test-User-Agent&w=1080&h=1920" +
                "&gdpr=1&consent=test-consent-string&us_privacy=1YNY";

        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    public void formatURL_withSomeNullParameters_handlesGracefully() {
        // A URL where some placeholders will not be filled.
        String baseUrl = "https://example.com/vast?adid={{adid}}&dnt={{dnt}}&lat={{lat}}";

        // Only provide a subset of parameters.
        VastUrlParameters params = new VastUrlParameters.Builder()
                .advertisingId("test-ad-id")
                // dnt and lat are null
                .build();

        String actualUrl = VastUrlUtils.formatURL(baseUrl, params);

        // The VastTag class leaves placeholders if the value is null or empty.
        String expectedUrl = "https://example.com/vast?adid=test-ad-id&dnt={{dnt}}&lat={{lat}}";

        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    public void formatURL_withNullParams_doesNotCrash() {
        String baseUrl = "https://example.com/vast?adid={{adid}}";
        String actualUrl = VastUrlUtils.formatURL(baseUrl, null);
        assertEquals(baseUrl, actualUrl);
    }

    @Test
    public void buildParameters_whenCoppaIsEnabled_setsDntToOne() {
        // Use try-with-resources for static mocking
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            // Mock the chained calls
            UserDataManager mockUserDataManager = mock(UserDataManager.class);
            DeviceInfo mockDeviceInfo = mock(DeviceInfo.class);
            mockedHyBid.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            mockedHyBid.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);

            // This is the condition we are testing
            mockedHyBid.when(HyBid::isCoppaEnabled).thenReturn(true);

            VastUrlParameters params = VastUrlUtils.buildParameters();

            assertEquals("1", params.dnt);
        }
    }

    @Test
    public void buildParameters_whenLimitTrackingIsEnabled_setsDntToOne() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            UserDataManager mockUserDataManager = mock(UserDataManager.class);
            DeviceInfo mockDeviceInfo = mock(DeviceInfo.class);
            mockedHyBid.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            mockedHyBid.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            mockedHyBid.when(HyBid::isCoppaEnabled).thenReturn(false);

            // This is the condition we are testing
            when(mockDeviceInfo.limitTracking()).thenReturn(true);

            VastUrlParameters params = VastUrlUtils.buildParameters();

            assertEquals("1", params.dnt);
        }
    }

    @Test
    public void buildParameters_whenGdprApplies_setsGdprToOne() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            UserDataManager mockUserDataManager = mock(UserDataManager.class);
            DeviceInfo mockDeviceInfo = mock(DeviceInfo.class);
            mockedHyBid.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            mockedHyBid.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);

            // This is the condition we are testing
            when(mockUserDataManager.gdprApplies()).thenReturn(true);

            VastUrlParameters params = VastUrlUtils.buildParameters();

            assertEquals("1", params.gdpr);
        }
    }

    @Test
    public void buildParameters_whenLocationIsAvailable_setsLatLon() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            UserDataManager mockUserDataManager = mock(UserDataManager.class);
            DeviceInfo mockDeviceInfo = mock(DeviceInfo.class);
            HyBidLocationManager mockLocationManager = mock(HyBidLocationManager.class);
            Location mockLocation = mock(Location.class);

            mockedHyBid.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            mockedHyBid.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            mockedHyBid.when(HyBid::getLocationManager).thenReturn(mockLocationManager);
            when(mockLocationManager.getUserLocation()).thenReturn(mockLocation);

            // This is the condition we are testing
            when(mockLocation.getLatitude()).thenReturn(12.345);
            when(mockLocation.getLongitude()).thenReturn(67.891);

            VastUrlParameters params = VastUrlUtils.buildParameters();

            assertEquals("12.35", params.latitude);
            assertEquals("67.89", params.longitude);
        }
    }

    @Test
    public void buildParameters_whenLocationIsZero_setsLatLonToNull() {
        try (MockedStatic<HyBid> mockedHyBid = Mockito.mockStatic(HyBid.class)) {
            UserDataManager mockUserDataManager = mock(UserDataManager.class);
            DeviceInfo mockDeviceInfo = mock(DeviceInfo.class);
            HyBidLocationManager mockLocationManager = mock(HyBidLocationManager.class);
            Location mockLocation = mock(Location.class);

            mockedHyBid.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            mockedHyBid.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            mockedHyBid.when(HyBid::getLocationManager).thenReturn(mockLocationManager);
            when(mockLocationManager.getUserLocation()).thenReturn(mockLocation);

            // This is the condition we are testing
            when(mockLocation.getLatitude()).thenReturn(0.0);
            when(mockLocation.getLongitude()).thenReturn(0.0);

            VastUrlParameters params = VastUrlUtils.buildParameters();

            assertNull(params.latitude);
            assertNull(params.longitude);
        }
    }
}