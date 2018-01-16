package net.pubnative.tarantula.sdk;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;

import net.pubnative.tarantula.sdk.utils.Logger;
import net.pubnative.tarantula.sdk.utils.TarantulaAdvertisingIdClient;

import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class DeviceInfo {
    public enum Orientation {
        PORTRAIT("portrait"),
        LANDSCAPE("landscape"),
        NONE("none");

        @NonNull
        private final String mOrientation;

        Orientation(@NonNull String orientation) {
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

        @NonNull
        private final String mConnectivity;

        Connectivity(@NonNull String connectivity) {
            mConnectivity = connectivity;
        }

        @Override
        public String toString() {
            return mConnectivity;
        }
    }

    @NonNull
    private static final String TAG = DeviceInfo.class.getSimpleName();
    @NonNull
    private static final String UNKNOWN_APP_VERSION_IDENTIFIER = "UNKNOWN";
    @NonNull
    private final Context mContext;
    @NonNull
    private final String mUserAgent;
    @NonNull
    private String mAdvertisingId;
    @Nullable
    private final ConnectivityManager mConnectivityManager;
    @Nullable
    private final TelephonyManager mTelephonyManager;

    public DeviceInfo(@NonNull Context context) {
        mContext = context.getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mUserAgent = WebSettings.getDefaultUserAgent(context);
        } else if (Looper.myLooper() == Looper.getMainLooper()) {
            // Can only create WebViews on the main thread
            mUserAgent = new WebView(mContext).getSettings().getUserAgentString();
        } else {
            mUserAgent = System.getProperty("http.agent");
        }

        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        fetchAdvertisingId();
    }

    private void fetchAdvertisingId() {
        TarantulaAdvertisingIdClient client = new TarantulaAdvertisingIdClient();
        client.request(mContext, new TarantulaAdvertisingIdClient.Listener() {
            @Override
            public void onPNAdvertisingIdFinish(String advertisingId) {
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
    @NonNull
    public String getAdvertisingId() {
        return mAdvertisingId;
    }

    @NonNull
    public String getAppVersion() {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.d(TAG, "Could not determine app version", e);
            return UNKNOWN_APP_VERSION_IDENTIFIER;
        }
    }

    @NonNull
    public Locale getLocale() {
        return mContext.getResources().getConfiguration().locale;
    }

    @NonNull
    public String getTimeZoneShortDisplayName() {
        return TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);
    }

    @NonNull
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

    public int getScreenWidthPx() {
        return mContext.getResources().getDisplayMetrics().widthPixels;
    }

    public int getScreenHeightPx() {
        return mContext.getResources().getDisplayMetrics().heightPixels;
    }

    @NonNull
    public String getBrowserAgent() {
        return mUserAgent;
    }

    @NonNull
    public String getModel() {
        return Build.MODEL;
    }

    private boolean checkPermission(String permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }

        return mContext.checkPermission(permission, android.os.Process.myPid(), Process.myUid())
                != PackageManager.PERMISSION_GRANTED;
    }

    @NonNull
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

    @NonNull
    public String getCarrierName() {
        if (mTelephonyManager == null) {
            return "";
        }

        try {
            return mTelephonyManager.getNetworkOperatorName();
        } catch (Exception ignored) {
        }

        return "";
    }
}
