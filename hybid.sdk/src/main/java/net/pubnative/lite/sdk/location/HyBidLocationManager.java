// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNPermissionUtil;

/**
 * Created by erosgarciaponte on 14.02.18.
 */

@SuppressLint("MissingPermission")
public class HyBidLocationManager implements LocationListener {
    private static final String TAG = HyBidLocationManager.class.getSimpleName();

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int LOCATION_UPDATE_TIMEOUT = 10000;

    private final Context mContext;
    private final LocationManager mManager;

    private Location mCurrentBestLocation;

    public HyBidLocationManager(Context context) {
        mManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mContext = context;
    }

    private Location getLastKnownNetworkLocation() {
        return hasNetworkProvider() ? mManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) : null;
    }

    private boolean hasCoarsePermission() {
        return PNPermissionUtil.hasPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    private boolean hasPermission() {
        return hasCoarsePermission();
    }

    private boolean hasNetworkProvider() {
        return mManager != null && mManager.getProvider(LocationManager.NETWORK_PROVIDER) != null;
    }

    /**
     * Triggers a location update request and sets a timeout of 10 seconds to obtain the location
     */
    public void startLocationUpdates() {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        try {
            if (hasCoarsePermission()) {
                if (hasNetworkProvider()) {
                    mainHandler.post(() -> mManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, HyBidLocationManager.this));
                }
            }
        } catch (Exception exception) {
            Logger.e(TAG, "Can't request location updates: ".concat(String.valueOf(exception.getMessage())));
        }

        mainHandler.postDelayed(mStopUpdatesRunnable, LOCATION_UPDATE_TIMEOUT);
    }

    public void stopLocationUpdates() {
        if (mManager != null) {
            mManager.removeUpdates(this);
        }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else return isNewer && !isSignificantlyLessAccurate && isFromSameProvider;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private Location getLocationFromProviders() {
        Location result = null;

        Location networkLocation = hasCoarsePermission() ? getLastKnownNetworkLocation() : null;

        if (networkLocation != null) {
            result = networkLocation;
        }

        return result;
    }

    public Location getUserLocation() {
        Location result = null;
        if (hasPermission()) {
            Location locationFromProviders = getLocationFromProviders();
            if (locationFromProviders != null && isBetterLocation(locationFromProviders, mCurrentBestLocation)) {
                mCurrentBestLocation = locationFromProviders;
            }
            result = mCurrentBestLocation;

            if (HyBid.isLocationTrackingEnabled() && HyBid.areLocationUpdatesEnabled()) {
                startLocationUpdates();
            }
        }
        return result;
    }

    private final Runnable mStopUpdatesRunnable = this::stopLocationUpdates;

    @Override
    public void onLocationChanged(Location location) {
        if (isBetterLocation(location, mCurrentBestLocation)) {
            mCurrentBestLocation = location;
            stopLocationUpdates();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
