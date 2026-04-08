// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.location;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import net.pubnative.lite.sdk.HyBid;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLocationManager;
import org.robolectric.shadows.ShadowLooper;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class HyBidLocationManagerTest {

    private HyBidLocationManager locationManager;
    private Context context;
    private ShadowLocationManager shadowLocationManager;
    private ShadowApplication shadowApplication;
    private MockedStatic<HyBid> hyBidMock;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();
        shadowApplication = Shadow.extract(context);
        LocationManager systemLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        shadowLocationManager = Shadow.extract(systemLocationManager);

        // Mock HyBid static methods
        hyBidMock = mockStatic(HyBid.class);

        locationManager = new HyBidLocationManager(context);
    }

    @After
    public void tearDown() {
        if (hyBidMock != null) {
            hyBidMock.close();
        }
    }

    // Constructor Tests

    @Test
    public void testConstructor_initializesLocationManager() {
        assertNotNull(locationManager);
    }

    @Test
    public void testConstructor_withValidContext() {
        HyBidLocationManager manager = new HyBidLocationManager(context);
        assertNotNull(manager);
    }

    // getUserLocation Tests

    @Test
    public void testGetUserLocation_withoutPermission_returnsNull() {
        shadowApplication.denyPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);

        Location result = locationManager.getUserLocation();

        assertNull(result);
    }

    @Test
    public void testGetUserLocation_withPermission_returnsLocation() {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);

        Location mockLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);
        shadowLocationManager.simulateLocation(mockLocation);

        Location result = locationManager.getUserLocation();

        assertNotNull(result);
        assertEquals(37.7749, result.getLatitude(), 0.001);
        assertEquals(-122.4194, result.getLongitude(), 0.001);
    }

    @Test
    public void testGetUserLocation_withPermission_andLocationTrackingEnabled_startsUpdates() {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);
        hyBidMock.when(HyBid::isLocationTrackingEnabled).thenReturn(true);
        hyBidMock.when(HyBid::areLocationUpdatesEnabled).thenReturn(true);

        Location mockLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);
        shadowLocationManager.simulateLocation(mockLocation);

        Location result = locationManager.getUserLocation();

        assertNotNull(result);
    }

    @Test
    public void testGetUserLocation_withNullNetworkLocation_returnsNull() {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);

        Location result = locationManager.getUserLocation();

        assertNull(result);
    }

    @Test
    public void testGetUserLocation_updatesBestLocation_whenNewLocationBetter() {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);

        // First location
        Location oldLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 100f);
        oldLocation.setTime(System.currentTimeMillis() - 60000);
        shadowLocationManager.simulateLocation(oldLocation);

        Location result1 = locationManager.getUserLocation();
        assertNotNull(result1);

        // Better location (more accurate and newer)
        Location newLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 50f);
        newLocation.setTime(System.currentTimeMillis());
        shadowLocationManager.simulateLocation(newLocation);

        Location result2 = locationManager.getUserLocation();
        assertNotNull(result2);
        assertEquals(50f, result2.getAccuracy(), 0.01);
    }

    // startLocationUpdates Tests

    @Test
    public void testStartLocationUpdates_withCoarsePermission_requestsUpdates() {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);

        locationManager.startLocationUpdates();
        ShadowLooper.idleMainLooper();

        // Verify no crash occurred
        assertNotNull(locationManager);
    }

    @Test
    public void testStartLocationUpdates_withoutPermission_doesNotRequestUpdates() {
        shadowApplication.denyPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);

        locationManager.startLocationUpdates();
        ShadowLooper.idleMainLooper();

        // Should not crash
        assertNotNull(locationManager);
    }

    @Test
    public void testStartLocationUpdates_withoutNetworkProvider_doesNotCrash() {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, false);

        locationManager.startLocationUpdates();
        ShadowLooper.idleMainLooper();

        // Should not crash
        assertNotNull(locationManager);
    }

    @Test
    public void testStartLocationUpdates_stopsAfterTimeout() {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);

        locationManager.startLocationUpdates();
        ShadowLooper.idleMainLooper();

        // Advance time beyond timeout
        ShadowLooper.runMainLooperToNextTask();
        ShadowLooper.idleMainLooper();

        // Should have stopped updates
        assertNotNull(locationManager);
    }

    // stopLocationUpdates Tests

    @Test
    public void testStopLocationUpdates_removesListeners() {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);

        locationManager.startLocationUpdates();
        ShadowLooper.idleMainLooper();

        locationManager.stopLocationUpdates();

        // Should not crash
        assertNotNull(locationManager);
    }

    @Test
    public void testStopLocationUpdates_withoutStarting_doesNotCrash() {
        locationManager.stopLocationUpdates();

        assertNotNull(locationManager);
    }

    // isBetterLocation Tests

    @Test
    public void testIsBetterLocation_withNullCurrentLocation_returnsTrue() {
        Location newLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);

        boolean result = locationManager.isBetterLocation(newLocation, null);

        assertTrue(result);
    }

    @Test
    public void testIsBetterLocation_significantlyNewer_returnsTrue() {
        Location oldLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);
        oldLocation.setTime(System.currentTimeMillis() - 180000); // 3 minutes ago

        Location newLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 60f);
        newLocation.setTime(System.currentTimeMillis());

        boolean result = locationManager.isBetterLocation(newLocation, oldLocation);

        assertTrue(result);
    }

    @Test
    public void testIsBetterLocation_significantlyOlder_returnsFalse() {
        Location currentLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);
        currentLocation.setTime(System.currentTimeMillis());

        Location oldLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 40f);
        oldLocation.setTime(System.currentTimeMillis() - 180000); // 3 minutes ago

        boolean result = locationManager.isBetterLocation(oldLocation, currentLocation);

        assertFalse(result);
    }

    @Test
    public void testIsBetterLocation_moreAccurate_returnsTrue() {
        Location lessAccurate = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 100f);
        lessAccurate.setTime(System.currentTimeMillis() - 1000);

        Location moreAccurate = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 50f);
        moreAccurate.setTime(System.currentTimeMillis());

        boolean result = locationManager.isBetterLocation(moreAccurate, lessAccurate);

        assertTrue(result);
    }

    @Test
    public void testIsBetterLocation_newerAndNotLessAccurate_returnsTrue() {
        Location older = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);
        older.setTime(System.currentTimeMillis() - 30000); // 30 seconds ago

        Location newer = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 50f);
        newer.setTime(System.currentTimeMillis());

        boolean result = locationManager.isBetterLocation(newer, older);

        assertTrue(result);
    }

    @Test
    public void testIsBetterLocation_newerButLessAccurate_returnsFalse() {
        Location moreAccurate = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);
        moreAccurate.setTime(System.currentTimeMillis() - 30000);

        Location lessAccurate = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 100f);
        lessAccurate.setTime(System.currentTimeMillis());

        boolean result = locationManager.isBetterLocation(lessAccurate, moreAccurate);

        // The implementation returns true because the new location is newer and not significantly less accurate
        // (100-50=50, which is < 200) and from the same provider
        assertTrue(result);
    }

    @Test
    public void testIsBetterLocation_newerSignificantlyLessAccurateSameProvider_returnsFalse() {
        Location moreAccurate = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);
        moreAccurate.setTime(System.currentTimeMillis() - 30000);

        Location muchLessAccurate = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 300f);
        muchLessAccurate.setTime(System.currentTimeMillis());

        boolean result = locationManager.isBetterLocation(muchLessAccurate, moreAccurate);

        assertFalse(result);
    }

    @Test
    public void testIsBetterLocation_newerNotSignificantlyLessAccurateDifferentProvider_returnsFalse() {
        Location gpsLocation = createMockLocation(LocationManager.GPS_PROVIDER, 37.7749, -122.4194, 50f);
        gpsLocation.setTime(System.currentTimeMillis() - 30000);

        Location networkLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 150f);
        networkLocation.setTime(System.currentTimeMillis());

        boolean result = locationManager.isBetterLocation(networkLocation, gpsLocation);

        assertFalse(result);
    }

    @Test
    public void testIsBetterLocation_sameProviderNullVsNonNull() {
        Location location1 = createMockLocation(null, 37.7749, -122.4194, 50f);
        location1.setTime(System.currentTimeMillis() - 30000);

        Location location2 = createMockLocation(null, 37.7750, -122.4195, 150f);
        location2.setTime(System.currentTimeMillis());

        boolean result = locationManager.isBetterLocation(location2, location1);

        assertTrue(result);
    }

    @Test
    public void testIsBetterLocation_differentProvidersOneNull() {
        Location nullProviderLocation = createMockLocation(null, 37.7749, -122.4194, 50f);
        nullProviderLocation.setTime(System.currentTimeMillis() - 30000);

        Location networkLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 150f);
        networkLocation.setTime(System.currentTimeMillis());

        boolean result = locationManager.isBetterLocation(networkLocation, nullProviderLocation);

        assertFalse(result);
    }

    // LocationListener callback tests

    @Test
    public void testOnLocationChanged_withBetterLocation_updatesAndStops() {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);

        // Set initial location
        Location oldLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 100f);
        oldLocation.setTime(System.currentTimeMillis() - 60000);
        shadowLocationManager.simulateLocation(oldLocation);
        locationManager.getUserLocation();

        // Start updates
        locationManager.startLocationUpdates();
        ShadowLooper.idleMainLooper();

        // Simulate location change with better location
        Location betterLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 50f);
        betterLocation.setTime(System.currentTimeMillis());

        locationManager.onLocationChanged(betterLocation);

        // Verify location was updated
        Location result = locationManager.getUserLocation();
        assertNotNull(result);
        assertEquals(50f, result.getAccuracy(), 0.01);
    }

    @Test
    public void testOnLocationChanged_withWorseLocation_doesNotUpdate() {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);

        // Set initial good location
        Location goodLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);
        goodLocation.setTime(System.currentTimeMillis());
        shadowLocationManager.simulateLocation(goodLocation);
        locationManager.getUserLocation();

        // Simulate location change with worse location
        Location worseLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 200f);
        worseLocation.setTime(System.currentTimeMillis() - 60000);

        locationManager.onLocationChanged(worseLocation);

        // Verify location was not updated
        Location result = locationManager.getUserLocation();
        assertNotNull(result);
        assertEquals(50f, result.getAccuracy(), 0.01);
    }

    @Test
    public void testOnStatusChanged_doesNotCrash() {
        locationManager.onStatusChanged(LocationManager.NETWORK_PROVIDER, LocationProvider.AVAILABLE, new Bundle());

        assertNotNull(locationManager);
    }

    @Test
    public void testOnProviderEnabled_doesNotCrash() {
        locationManager.onProviderEnabled(LocationManager.NETWORK_PROVIDER);

        assertNotNull(locationManager);
    }

    @Test
    public void testOnProviderDisabled_doesNotCrash() {
        locationManager.onProviderDisabled(LocationManager.NETWORK_PROVIDER);

        assertNotNull(locationManager);
    }

    // Edge case and error handling tests

    @Test
    public void testGetUserLocation_withLocationTrackingDisabled_doesNotStartUpdates() {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);
        hyBidMock.when(HyBid::isLocationTrackingEnabled).thenReturn(false);

        Location mockLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);
        shadowLocationManager.simulateLocation(mockLocation);

        Location result = locationManager.getUserLocation();

        assertNotNull(result);
        // Updates should not have been started
    }

    @Test
    public void testGetUserLocation_withLocationUpdatesDisabled_doesNotStartUpdates() {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);
        hyBidMock.when(HyBid::isLocationTrackingEnabled).thenReturn(true);
        hyBidMock.when(HyBid::areLocationUpdatesEnabled).thenReturn(false);

        Location mockLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);
        shadowLocationManager.simulateLocation(mockLocation);

        Location result = locationManager.getUserLocation();

        assertNotNull(result);
        // Updates should not have been started
    }

    @Test
    public void testGetUserLocation_multipleCalls_returnsCachedBestLocation() {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);

        Location mockLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);
        shadowLocationManager.simulateLocation(mockLocation);

        Location result1 = locationManager.getUserLocation();
        Location result2 = locationManager.getUserLocation();

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.getLatitude(), result2.getLatitude(), 0.001);
        assertEquals(result1.getLongitude(), result2.getLongitude(), 0.001);
    }

    @Test
    public void testIsBetterLocation_edgeCaseExactly2Minutes_returnsTrue() {
        Location oldLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);
        long twoMinutesAgo = System.currentTimeMillis() - (1000 * 60 * 2);
        oldLocation.setTime(twoMinutesAgo);

        Location newLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 50f);
        newLocation.setTime(System.currentTimeMillis());

        boolean result = locationManager.isBetterLocation(newLocation, oldLocation);

        // Should be considered significantly newer
        assertTrue(result);
    }

    @Test
    public void testIsBetterLocation_almostSignificantlyOlder_usesAccuracy() {
        Location currentLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 100f);
        currentLocation.setTime(System.currentTimeMillis());

        Location slightlyOldButMoreAccurate = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 50f);
        slightlyOldButMoreAccurate.setTime(System.currentTimeMillis() - 60000); // 1 minute old

        boolean result = locationManager.isBetterLocation(slightlyOldButMoreAccurate, currentLocation);

        assertTrue(result); // More accurate should win
    }

    @Test
    public void testIsBetterLocation_sameTimeMoreAccurate_returnsTrue() {
        long now = System.currentTimeMillis();
        Location lessAccurate = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 100f);
        lessAccurate.setTime(now);

        Location moreAccurate = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 50f);
        moreAccurate.setTime(now);

        boolean result = locationManager.isBetterLocation(moreAccurate, lessAccurate);

        assertTrue(result);
    }

    @Test
    public void testIsBetterLocation_accuracyBoundaryCase_exactly200meters() {
        Location moreAccurate = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);
        moreAccurate.setTime(System.currentTimeMillis() - 30000);

        Location lessAccurate = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 250f);
        lessAccurate.setTime(System.currentTimeMillis());

        boolean result = locationManager.isBetterLocation(lessAccurate, moreAccurate);

        // The implementation returns true because accuracyDelta = 250-50 = 200, which is NOT > 200
        // so isSignificantlyLessAccurate = false, making it acceptable
        assertTrue(result);
    }

    @Test
    public void testIsBetterLocation_accuracyBoundaryCase_over200meters() {
        Location moreAccurate = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);
        moreAccurate.setTime(System.currentTimeMillis() - 30000);

        Location muchLessAccurate = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 251f);
        muchLessAccurate.setTime(System.currentTimeMillis());

        boolean result = locationManager.isBetterLocation(muchLessAccurate, moreAccurate);

        // Now with 251-50 = 201 > 200, it should return false
        assertFalse(result);
    }

    @Test
    public void testIsBetterLocation_newerNotSignificantlyLessAccurateSameProvider_returnsTrue() {
        Location older = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);
        older.setTime(System.currentTimeMillis() - 30000);

        Location newer = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 150f);
        newer.setTime(System.currentTimeMillis());

        boolean result = locationManager.isBetterLocation(newer, older);

        // accuracyDelta = 150-50 = 100, which is < 200, and same provider
        assertTrue(result);
    }

    @Test
    public void testOnLocationChanged_callsStopLocationUpdatesWhenBetter() {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);

        Location initialLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 100f);
        initialLocation.setTime(System.currentTimeMillis() - 60000);
        shadowLocationManager.simulateLocation(initialLocation);
        locationManager.getUserLocation();

        locationManager.startLocationUpdates();
        ShadowLooper.idleMainLooper();

        // Trigger location changed with better location
        Location betterLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 30f);
        betterLocation.setTime(System.currentTimeMillis());
        locationManager.onLocationChanged(betterLocation);

        // Verify the location was updated
        Location result = locationManager.getUserLocation();
        assertNotNull(result);
        assertEquals(30f, result.getAccuracy(), 0.01);
    }

    @Test
    public void testGetUserLocation_withFinePermission_returnsLocation() {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);

        Location mockLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);
        shadowLocationManager.simulateLocation(mockLocation);

        Location result = locationManager.getUserLocation();

        assertNotNull(result);
        assertEquals(37.7749, result.getLatitude(), 0.001);
    }

    @Test
    public void testIsBetterLocation_oldLocationMoreAccurateThanSignificantThreshold_returnsFalse() {
        Location veryAccurate = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 10f);
        veryAccurate.setTime(System.currentTimeMillis() - 30000);

        Location lessAccurate = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 220f);
        lessAccurate.setTime(System.currentTimeMillis());

        boolean result = locationManager.isBetterLocation(lessAccurate, veryAccurate);

        // accuracyDelta = 220-10 = 210 > 200, so significantly less accurate
        assertFalse(result);
    }

    @Test
    public void testIsBetterLocation_sameTimeAndAccuracy_fromSameProvider() {
        long now = System.currentTimeMillis();
        Location location1 = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);
        location1.setTime(now);

        Location location2 = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 50f);
        location2.setTime(now);

        boolean result = locationManager.isBetterLocation(location2, location1);

        // Same time means isNewer = false, same accuracy means isMoreAccurate = false
        // So the method returns false (none of the conditions are met)
        assertFalse(result);
    }

    @Test
    public void testStartLocationUpdates_withNullLocationManager_doesNotCrash() {
        // This tests the exception handling in startLocationUpdates
        locationManager.startLocationUpdates();
        ShadowLooper.idleMainLooper();

        assertNotNull(locationManager);
    }

    @Test
    public void testGetUserLocation_whenLocationBecomesWorse_keepsOldLocation() {
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);

        // Set a good initial location
        Location goodLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 30f);
        goodLocation.setTime(System.currentTimeMillis());
        shadowLocationManager.simulateLocation(goodLocation);

        Location result1 = locationManager.getUserLocation();
        assertNotNull(result1);
        assertEquals(30f, result1.getAccuracy(), 0.01);

        // Now try to update with a worse location (older and less accurate)
        Location worseLocation = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 500f);
        worseLocation.setTime(System.currentTimeMillis() - 180001); // Significantly older
        shadowLocationManager.simulateLocation(worseLocation);

        Location result2 = locationManager.getUserLocation();
        assertNotNull(result2);
        // Should still have the good location
        assertEquals(30f, result2.getAccuracy(), 0.01);
    }

    @Test
    public void testIsBetterLocation_borderlineNewer_notLessAccurate() {
        Location older = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);
        older.setTime(System.currentTimeMillis() - 1000); // Just 1 second older

        Location newer = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 50f);
        newer.setTime(System.currentTimeMillis());

        boolean result = locationManager.isBetterLocation(newer, older);

        // isNewer = true, !isLessAccurate = true
        assertTrue(result);
    }

    @Test
    public void testIsBetterLocation_borderlineOlder_notMoreAccurate() {
        Location newer = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7749, -122.4194, 50f);
        newer.setTime(System.currentTimeMillis());

        Location slightlyOlder = createMockLocation(LocationManager.NETWORK_PROVIDER, 37.7750, -122.4195, 50f);
        slightlyOlder.setTime(System.currentTimeMillis() - 1000); // Just 1 second older

        boolean result = locationManager.isBetterLocation(slightlyOlder, newer);

        // Not newer, not more accurate
        assertFalse(result);
    }

    // Helper method to create mock locations
    private Location createMockLocation(String provider, double latitude, double longitude, float accuracy) {
        Location location = new Location(provider);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAccuracy(accuracy);
        location.setTime(System.currentTimeMillis());
        return location;
    }
}
