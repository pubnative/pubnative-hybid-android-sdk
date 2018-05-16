package net.pubnative.lite.sdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Looper;
import android.os.Process;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNAdvertisingIdClient;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class DeviceInfo {
    public enum Orientation {
        PORTRAIT("portrait"),
        LANDSCAPE("landscape"),
        NONE("none");

        private final String mOrientation;

        Orientation(String orientation) {
            mOrientation = orientation;
        }

        @Override
        public String toString() {
            return mOrientation;
        }
    }

    public enum Connectivity {
        ETHERNET("ethernet"),
        WIFI("wifi"),
        WWAN("wwan"),
        NONE("none");

        private final String mConnectivity;

        Connectivity(String connectivity) {
            mConnectivity = connectivity;
        }

        @Override
        public String toString() {
            return mConnectivity;
        }
    }

    private static final String TAG = DeviceInfo.class.getSimpleName();
    private static final String UNKNOWN_APP_VERSION_IDENTIFIER = "UNKNOWN";
    private final Context mContext;
    private String mAdvertisingId;
    private boolean mLimitTracking = false;
    private final ConnectivityManager mConnectivityManager;
    private final TelephonyManager mTelephonyManager;

    public DeviceInfo(Context context) {
        mContext = context.getApplicationContext();

        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        fetchAdvertisingId();
    }

    private void fetchAdvertisingId() {
        PNAdvertisingIdClient client = new PNAdvertisingIdClient();
        client.request(mContext, new PNAdvertisingIdClient.Listener() {
            @Override
            public void onPNAdvertisingIdFinish(String advertisingId, boolean limitTracking) {
                mLimitTracking = limitTracking;
                if (TextUtils.isEmpty(advertisingId)) {
                    mAdvertisingId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
                } else {
                    mAdvertisingId = advertisingId;
                }
            }
        });
    }

    /**
     * Attempt to use the play services advertising ID, but fall back on the old style Android ID.
     * https://developer.android.com/training/articles/user-data-ids.html
     * https://support.google.com/googleplay/android-developer/answer/6048248?hl=en
     * https://play.google.com/about/monetization-ads/ads/ad-id/
     *
     * @return
     */
    @SuppressLint("HardwareIds")
    public String getAdvertisingId() {
        return mAdvertisingId;
    }

    public boolean limitTracking() {
        return mLimitTracking;
    }

    public Locale getLocale() {
        return mContext.getResources().getConfiguration().locale;
    }

    public Orientation getOrientation() {
        switch (mContext.getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT: {
                return Orientation.PORTRAIT;
            }
            case Configuration.ORIENTATION_LANDSCAPE: {
                return Orientation.LANDSCAPE;
            }
            default: {
                return Orientation.NONE;
            }
        }
    }

    public String getModel() {
        return Build.MODEL;
    }

    public String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    private boolean checkPermission(String permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }

        return mContext.checkPermission(permission, android.os.Process.myPid(), Process.myUid())
                != PackageManager.PERMISSION_GRANTED;
    }

    public Connectivity getConnectivity() {
        if (mConnectivityManager == null || checkPermission(Manifest.permission.ACCESS_NETWORK_STATE)) {
            return Connectivity.NONE;
        }

        final NetworkInfo activeNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            return Connectivity.NONE;
        }

        switch (activeNetworkInfo.getType()) {
            case ConnectivityManager.TYPE_ETHERNET:
                return Connectivity.ETHERNET;
            case ConnectivityManager.TYPE_WIFI:
                return Connectivity.WIFI;
            case ConnectivityManager.TYPE_MOBILE:
            case ConnectivityManager.TYPE_MOBILE_DUN:
            case ConnectivityManager.TYPE_MOBILE_HIPRI:
            case ConnectivityManager.TYPE_MOBILE_MMS:
            case ConnectivityManager.TYPE_MOBILE_SUPL:
                return Connectivity.WWAN;
            default:
                return Connectivity.NONE;
        }
    }
}