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
package net.pubnative.lite.sdk;

import static android.content.Context.BATTERY_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import net.pubnative.lite.sdk.core.R;
import net.pubnative.lite.sdk.models.request.UserAgent;
import net.pubnative.lite.sdk.utils.HyBidAdvertisingId;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNAsyncUtils;
import net.pubnative.lite.sdk.utils.PNCrypto;
import net.pubnative.lite.sdk.utils.ScreenDimensionsUtils;
import net.pubnative.lite.sdk.utils.SoundUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class DeviceInfo {

    public interface Listener {
        void onInfoLoaded();
    }

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
    private final Context mContext;
    private final UserAgentProvider mUserAgentProvider;
    private String mAdvertisingId;
    private String mAdvertisingIdMd5;
    private String mAdvertisingIdSha1;
    private boolean mLimitTracking = false;
    private boolean mIsCharging = false;
    private Listener mListener;
    private String deviceHeight;
    private String deviceWidth;
    private float pxratio;

    public DeviceInfo(Context context) {
        mContext = context.getApplicationContext();
        mUserAgentProvider = new UserAgentProvider();
        getDeviceScreenDimensions();
    }

    public void initialize(Listener listener) {
        mListener = listener;
        fetchUserAgent();
        fetchAdvertisingId();
        updateChargingStatus();
    }

    public void updateChargingStatus() {
        BroadcastReceiver batteryStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                mIsCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
                context.unregisterReceiver(this);
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(batteryStatusReceiver, filter);
    }

    private void fetchAdvertisingId() {
        try {
            PNAsyncUtils.safeExecuteOnExecutor(new HyBidAdvertisingId(mContext, (advertisingId, limitTracking) -> {
                mLimitTracking = limitTracking;
                if (!TextUtils.isEmpty(advertisingId)) {
                    mAdvertisingId = advertisingId;
                    mAdvertisingIdMd5 = PNCrypto.md5(mAdvertisingId);
                    mAdvertisingIdSha1 = PNCrypto.sha1(mAdvertisingId);
                } else {
                    fetchFireOSAdvertisingId();
                }

                if (mListener != null) {
                    mListener.onInfoLoaded();
                }
            }));
        } catch (Exception exception) {
            Logger.e(TAG, "Error executing HyBidAdvertisingId AsyncTask");
            if (mListener != null) {
                mListener.onInfoLoaded();
            }
            HyBid.reportException(exception);
        }
    }

    private void fetchFireOSAdvertisingId() {
        String advertisingID;
        boolean limitAdTracking = false;

        try {
            if (mContext == null) {
                return;
            }
            ContentResolver cr = mContext.getContentResolver();

            if (cr != null) {
                // get user's tracking preference
                limitAdTracking = Settings.Secure.getInt(cr, "limit_ad_tracking") != 0;

                if (!limitAdTracking) {
                    // get advertising ID
                    advertisingID = Settings.Secure.getString(cr, "advertising_id");
                    if (advertisingID != null && !advertisingID.isEmpty()) {
                        mAdvertisingId = advertisingID;
                        mAdvertisingIdMd5 = PNCrypto.md5(mAdvertisingId);
                        mAdvertisingIdSha1 = PNCrypto.sha1(mAdvertisingId);
                    }
                }
            }

        } catch (Settings.SettingNotFoundException ex) {
            // no need to do anything
        }
    }

    public void fetchUserAgent() {
        mUserAgentProvider.initialise(mContext);
    }

    public void getDeviceScreenDimensions() {
        ScreenDimensionsUtils screenDimensionsUtils = new ScreenDimensionsUtils();
        Point point = screenDimensionsUtils.getScreenDimensionsToPoint(mContext);
        deviceWidth = Integer.toString(point.x);
        deviceHeight = Integer.toString(point.y);
        if (mContext != null) {
            pxratio = mContext.getResources().getDisplayMetrics().density;
        }
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

    public String getAdvertisingIdMd5() {
        return mAdvertisingIdMd5;
    }

    public String getAdvertisingIdSha1() {
        return mAdvertisingIdSha1;
    }

    public boolean limitTracking() {
        return mLimitTracking;
    }

    public Locale getLocale() {
        if (mContext != null) {
            return mContext.getResources().getConfiguration().locale;
        } else {
            return null;
        }
    }

    public Orientation getOrientation() {
        if (mContext != null) {
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
        } else {
            return Orientation.NONE;
        }
    }

    public String getModel() {
        return Build.MODEL;
    }

    public String getMake() {
        return Build.MANUFACTURER;
    }

    public int getDeviceType() {
        if (mContext != null) {
            boolean isTablet = mContext.getResources().getBoolean(R.bool.is_tablet);
            if (isTablet) {
                return 5;
            } else {
                return 4;
            }
        } else {
            return 1;
        }
    }

    @SuppressLint("MissingPermission")
    public Integer getConnectionType() {
        boolean networkStatePermission = hasPermission(Manifest.permission.ACCESS_NETWORK_STATE);
        if (networkStatePermission) {
            boolean readPhoneStatePermission = hasPermission(Manifest.permission.READ_PHONE_STATE);
            if (mContext != null && mContext.getSystemService(Context.CONNECTIVITY_SERVICE) != null) {
                ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && readPhoneStatePermission) {
                    Network nw = cm.getActiveNetwork();
                    if (nw != null) {
                        NetworkCapabilities nc = cm.getNetworkCapabilities(nw);
                        if (nc != null) {
                            if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                                return 2; // WIFI
                            } else if (nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                                return 1; // Ethernet; Wired Connection
                            } else if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                                TelephonyManager manager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                                if (manager != null) {
                                    switch (manager.getDataNetworkType()) {
                                        case TelephonyManager.NETWORK_TYPE_GPRS:
                                        case TelephonyManager.NETWORK_TYPE_EDGE:
                                        case TelephonyManager.NETWORK_TYPE_CDMA:
                                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                                        case TelephonyManager.NETWORK_TYPE_IDEN:
                                        case TelephonyManager.NETWORK_TYPE_GSM:
                                            return 4; //Cellular Network - 2G

                                        case TelephonyManager.NETWORK_TYPE_UMTS:
                                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                                        case TelephonyManager.NETWORK_TYPE_HSPA:
                                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                                        case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                                            return 5; // Cellular Network - 3G

                                        case TelephonyManager.NETWORK_TYPE_LTE:
                                        case TelephonyManager.NETWORK_TYPE_IWLAN:
                                        case 19:
                                            return 6; // Cellular Network - 4G

                                        case TelephonyManager.NETWORK_TYPE_NR:
                                            return 7; // Cellular Network - 5G

                                        default:
                                            return 3; // Cellular Network - Unknown Generation
                                    }
                                } else {
                                    return 3;
                                }
                            }
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                } else {
                    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                    if (networkInfo != null) {
                        switch (networkInfo.getType()) {
                            case ConnectivityManager.TYPE_WIFI:
                                return 2;
                            case ConnectivityManager.TYPE_ETHERNET:
                                return 1;
                            case ConnectivityManager.TYPE_MOBILE:
                                switch (networkInfo.getSubtype()) {
                                    case TelephonyManager.NETWORK_TYPE_GPRS:
                                    case TelephonyManager.NETWORK_TYPE_EDGE:
                                    case TelephonyManager.NETWORK_TYPE_CDMA:
                                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                                    case TelephonyManager.NETWORK_TYPE_IDEN:
                                    case TelephonyManager.NETWORK_TYPE_GSM:
                                        return 4; // Cellular Network - 2G

                                    case TelephonyManager.NETWORK_TYPE_UMTS:
                                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                                    case TelephonyManager.NETWORK_TYPE_HSPA:
                                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                                    case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                                        return 5; // Cellular Network - 3G

                                    case TelephonyManager.NETWORK_TYPE_LTE:
                                    case TelephonyManager.NETWORK_TYPE_IWLAN:
                                    case 19:
                                        return 6; // Cellular Network - 4G

                                    case TelephonyManager.NETWORK_TYPE_NR:
                                        return 7; // Cellular Network - 5G

                                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                                        return 3; // Cellular Network - Unknown Generation
                                    default:
                                        return null;
                                }
                            default:
                                return null;
                        }
                    } else {
                        return null;
                    }
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
        return null;
    }

    public String getCarrier() {
        TelephonyManager manager = getTelephonyManager();
        if (manager != null) {
            return manager.getNetworkOperatorName();
        } else {
            return "";
        }
    }

    public String getMccmnc() {
        TelephonyManager manager = getTelephonyManager();
        if (manager != null) {
            return manager.getNetworkOperator();
        } else {
            return "";
        }
    }

    public String getMccmncsim() {
        TelephonyManager manager = getTelephonyManager();
        if (manager != null) {
            return manager.getSimOperator();
        } else {
            return "";
        }
    }

    public String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public Context getContext() {
        return mContext;
    }

    public String getDeviceHeight() {
        return deviceHeight;
    }

    public String getDeviceWidth() {
        return deviceWidth;
    }

    public String getSoundSetting() {
        return SoundUtils.isSoundMuted(mContext) ? "0" : "1";
    }

    public String getUserAgent() {
        return mUserAgentProvider != null ? mUserAgentProvider.getUserAgent() : "";
    }

    public UserAgent getStructuredUserAgent() {
        return mUserAgentProvider != null ? mUserAgentProvider.getStructuredUserAgent() : null;
    }

    public String getPpi() {
        if (mContext != null) {
            float ppi = mContext.getResources().getDisplayMetrics().xdpi;
            int ppiInt = (int) ppi;
            return String.valueOf(ppiInt);
        } else {
            return "";
        }
    }

    public String getPxratio() {
        return String.valueOf(pxratio);
    }

    public boolean hasTrackingPermissions() {
        return hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) || hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private boolean hasPermission(String permission) {
        if (mContext != null) {
            int result = mContext.checkCallingOrSelfPermission(permission);
            return result == PackageManager.PERMISSION_GRANTED;
        } else {
            return false;
        }
    }

    private TelephonyManager getTelephonyManager() {
        if (mContext != null) {
            return (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        } else {
            return null;
        }
    }

    public String getLangb() {
        Locale loc = getLocale();
        if (loc != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return loc.toLanguageTag();
            }

            // we will use a dash as per BCP 47
            final char SEP = '-';
            String language = loc.getLanguage();
            String region = loc.getCountry();
            String variant = loc.getVariant();

            // special case for Norwegian Nynorsk since "NY" cannot be a variant as per BCP 47
            // this goes before the string matching since "NY" wont pass the variant checks
            if (language.equals("no") && region.equals("NO") && variant.equals("NY")) {
                language = "nn";
                region = "NO";
                variant = "";
            }

            if (language.isEmpty() || !language.matches("\\p{Alpha}{2,8}")) {
                language = "und";       // Follow the Locale#toLanguageTag() implementation
                // which says to return "und" for Undetermined
            } else if (language.equals("iw")) {
                language = "he";        // correct deprecated "Hebrew"
            } else if (language.equals("in")) {
                language = "id";        // correct deprecated "Indonesian"
            } else if (language.equals("ji")) {
                language = "yi";        // correct deprecated "Yiddish"
            }

            // ensure valid country code, if not well formed, it's omitted
            if (!region.matches("\\p{Alpha}{2}|\\p{Digit}{3}")) {
                region = "";
            }

            // variant subtags that begin with a letter must be at least 5 characters long
            if (!variant.matches("\\p{Alnum}{5,8}|\\p{Digit}\\p{Alnum}{3}")) {
                variant = "";
            }

            StringBuilder bcp47Tag = new StringBuilder(language);
            if (!region.isEmpty()) {
                bcp47Tag.append(SEP).append(region);
            }
            if (!variant.isEmpty()) {
                bcp47Tag.append(SEP).append(variant);
            }

            return bcp47Tag.toString();
        } else {
            return null;
        }
    }

    public List<String> getInputLanguages() {
        ArrayList<String> inputLanguages = new ArrayList<>();
        if (mContext != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && inputMethodManager.getEnabledInputMethodList() != null
                    && !inputMethodManager.getEnabledInputMethodList().isEmpty()) {
                List<InputMethodInfo> inputMethodInfoList = inputMethodManager.getEnabledInputMethodList();
                if (inputMethodInfoList != null) {
                    for (InputMethodInfo inputMethodInfo : inputMethodInfoList) {
                        List<InputMethodSubtype> subtypeList = inputMethodManager.getEnabledInputMethodSubtypeList(inputMethodInfo, true);
                        if (subtypeList != null) {
                            for (InputMethodSubtype subtype : subtypeList) {
                                if (subtype.getMode() != null && subtype.getMode().equals("keyboard")) {
                                    String currentLocale = subtype.getLocale();
                                    if (currentLocale == null || !currentLocale.isEmpty()) {
                                        inputLanguages.add(currentLocale);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return inputLanguages;
    }

    public Integer isBatteryCharging() {
        updateChargingStatus();
        return mIsCharging ? 1 : 0;
    }


    public Integer getBatteryLevel() {
        Integer batteryPercentage;
        if (Build.VERSION.SDK_INT >= 21) {
            BatteryManager batteryManager = (BatteryManager) mContext.getSystemService(BATTERY_SERVICE);
            batteryPercentage = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } else {
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = mContext.registerReceiver(null, intentFilter);
            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
            double batteryPct = level / (double) scale;
            batteryPercentage = (int) (batteryPct * 100);
        }

        if (batteryPercentage >= 85) {
            return 8;
        } else if (batteryPercentage >= 70) {
            return 7;
        } else if (batteryPercentage >= 55) {
            return 6;
        } else if (batteryPercentage >= 40) {
            return 5;
        } else if (batteryPercentage >= 25) {
            return 4;
        } else if (batteryPercentage >= 10) {
            return 3;
        } else if (batteryPercentage >= 5) {
            return 2;
        } else if (batteryPercentage >= 0) {
            return 1;
        } else {
            return null;
        }
    }

    public Integer isPowerSaveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                if (powerManager.isPowerSaveMode()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
        // For older versions return null
        return null;
    }

    public Integer getTotalMemoryMb() {
        long memoryBytes = getTotalInternalMemorySize();
        long memoryKb = memoryBytes / 1024;
        long memoryMb = memoryKb / 1024;

        return (int) memoryMb;
    }

    public Integer getFreeMemoryMb() {
        long memoryBytes = getAvailableInternalMemorySize();
        long memoryKb = memoryBytes / 1024;
        long memoryMb = memoryKb / 1024;

        return (int) memoryMb;
    }

    private Long getAvailableInternalMemorySize() {
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return (availableBlocks * blockSize);
    }

    private Long getTotalInternalMemorySize() {
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return (totalBlocks * blockSize);
    }

    public Integer isDarkMode() {
        Integer darkmode = null;
        if (mContext != null && mContext.getResources() != null && mContext.getResources().getConfiguration() != null) {
            switch (mContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_YES:
                    darkmode = 1;
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    darkmode = 0;
                    break;
            }
        }
        return darkmode;
    }

    public Integer isDndEnabled() {
        try {
            if (mContext == null || mContext.getContentResolver() == null) return null;
            int zenModeValue = Settings.Global.getInt(mContext.getContentResolver(), "zen_mode");
            switch (zenModeValue) {
                case 0:
                    Logger.d(TAG, "DnD : OFF");
                    return 0;
                case 1:
                    Logger.d(TAG, "DnD : ON - Priority Only");
                    return 1;
                case 2:
                    Logger.d(TAG, "DnD : ON - Total Silence");
                    return 1;
                case 3:
                    Logger.d(TAG, "DnD : ON - Alarms Only");
                    return 1;
                default:
                    return null;
            }
        } catch (Settings.SettingNotFoundException e) {
            return null;
        }
    }

    public Integer isAirplaneModeEnabled() {
        return checkAirplaneMode();
    }

    private Integer checkAirplaneMode() {
        if (isAirplaneModeOn()) {
            return 1;
        } else {
            return 0;
        }
    }

    private boolean isAirplaneModeOn() {
        if (mContext != null && mContext.getContentResolver() != null) {
            return Settings.System.getInt(mContext.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return false;
        }
    }

    public Integer isHeadsetOn() {
        boolean readPhoneStatePermission = hasPermission(Manifest.permission.READ_PHONE_STATE);
        if (readPhoneStatePermission) {
            AudioManager am = (AudioManager) this.mContext.getSystemService(Context.AUDIO_SERVICE);
            if (am == null)
                return null;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return (am.isWiredHeadsetOn() || am.isBluetoothScoOn() || am.isBluetoothA2dpOn()) ? 1 : 0;
            } else {
                AudioDeviceInfo[] devices = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
                if (devices == null) return null;
                for (AudioDeviceInfo device : devices) {
//                if (device.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET
//                        || device.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
//                        || device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
//                        || device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
//                    return 1;
//                }
                    if (device.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET
                            || device.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES) {
                        return 1;
                    }
                }
            }
            return 0;
        } else {
            return null;
        }
    }

    public Integer isBluetoothEnabled() {

        if (mContext == null)
            return null;

        PackageManager packageManager = mContext.getPackageManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
                || mContext.checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED)) {
            return null;
        }

        BluetoothAdapter bluetoothAdapter;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            BluetoothManager bluetoothManager = mContext.getSystemService(BluetoothManager.class);
            if (bluetoothManager != null)
                bluetoothAdapter = bluetoothManager.getAdapter();
            else
                return null;
        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        try {
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                return 0;
            } else {
                return 1;
            }
        } catch (Exception e){
            return null;
        }
    }
}