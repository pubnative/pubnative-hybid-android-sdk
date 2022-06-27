// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
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
import net.pubnative.lite.sdk.utils.PNPermissionUtil;

/**
 * Created by erosgarciaponte on 14.02.18.
 */

@SuppressLint("MissingPermission")
public class HyBidLocationManager implements LocationListener {

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

    private Location getLastKnownGPSLocation() {
        return hasGPSProvider() ? mManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) : null;
    }

    private boolean hasFinePermission() {
        return PNPermissionUtil.hasPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private boolean hasCoarsePermission() {
        return PNPermissionUtil.hasPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    private boolean hasPermission() {
        return hasCoarsePermission() || hasFinePermission();
    }

    private boolean hasGPSProvider() {
        return mManager != null && mManager.getProvider(LocationManager.GPS_PROVIDER) != null;
    }

    private boolean hasNetworkProvider() {
        return mManager != null && mManager.getProvider(LocationManager.NETWORK_PROVIDER) != null;
    }

    /**
     * Triggers a location update request and sets a timeout of 10 seconds to obtain the location
     */
    public void startLocationUpdates() {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        if (hasFinePermission()) {
            if (hasGPSProvider()) {
                mainHandler.post(() -> mManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, HyBidLocationManager.this));
            }
            if (hasNetworkProvider()) {
                mainHandler.post(() -> mManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, HyBidLocationManager.this));
            }
        } else if (hasCoarsePermission()) {
            if (hasNetworkProvider()) {
                mainHandler.post(() -> mManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, HyBidLocationManager.this));
            }
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

        Location networkLocation = hasCoarsePermission() || hasFinePermission() ? getLastKnownNetworkLocation() : null;
        Location gpsLocation = hasFinePermission() ? getLastKnownGPSLocation() : null;

        if (gpsLocation != null && networkLocation != null) {
            if (isBetterLocation(gpsLocation, networkLocation)) {
                result = gpsLocation;
            } else {
                result = networkLocation;
            }
        } else if (gpsLocation != null) {
            result = gpsLocation;
        } else if (networkLocation != null) {
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
