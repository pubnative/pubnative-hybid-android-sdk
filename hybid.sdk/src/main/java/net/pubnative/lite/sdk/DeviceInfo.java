// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
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

import net.pubnative.lite.sdk.models.request.UserAgent;
import net.pubnative.lite.sdk.utils.BatteryUtils;
import net.pubnative.lite.sdk.utils.HyBidAdvertisingId;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNCrypto;
import net.pubnative.lite.sdk.utils.ScreenDimensionsUtils;
import net.pubnative.lite.sdk.utils.SoundUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class DeviceInfo {

    public interface Listener {
        void onInfoLoaded();
    }

    public enum Orientation {
        PORTRAIT("portrait"), LANDSCAPE("landscape"), NONE("none");

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
        ETHERNET("ethernet"), WIFI("wifi"), WWAN("wwan"), NONE("none");

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
    private static final long SIGNALS_CACHE_DURATION_MS = 2000;
    private static final ExecutorService BACKGROUND_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "HyBid-DeviceInfo");
        thread.setDaemon(true);
        return thread;
    });

    private final Context mContext;
    private final UserAgentProvider mUserAgentProvider;
    private String mAdvertisingId;
    private String mAdvertisingIdMd5;
    private String mAdvertisingIdSha1;
    private boolean mLimitTracking = false;
    private Listener mListener;
    private String deviceHeight;
    private String deviceWidth;
    private float pxratio;
    private final AtomicReference<List<String>> mCachedInputLanguages = new AtomicReference<>(Collections.emptyList());
    private final AtomicReference<DeviceSignals> mCachedDeviceSignals = new AtomicReference<>(DeviceSignals.EMPTY);
    private final AtomicBoolean mSignalsRefreshInProgress = new AtomicBoolean(false);

    public DeviceInfo(Context context) {
        this(context, false);
    }

    /**
     * Reads the signal snapshot on the calling thread, for callers that consume the values
     * synchronously before a background refresh could land.
     *
     * <p>That read does binder and {@link StatFs} work inline, so this should not be called
     * from the main thread; {@link #DeviceInfo(Context)} refreshes the same snapshot instead.
     */
    public static DeviceInfo createForSynchronousRequest(Context context) {
        return new DeviceInfo(context, true);
    }

    private DeviceInfo(Context context, boolean primeSignalsSynchronously) {
        mContext = context.getApplicationContext();
        mUserAgentProvider = new UserAgentProvider();
        getDeviceScreenDimensions();
        if (primeSignalsSynchronously) {
            primeDeviceSignals();
        } else {
            fetchDeviceSignals();
        }
        fetchInputLanguages();
    }

    public void initialize(Listener listener) {
        mListener = listener;
        fetchUserAgent();
        fetchAdvertisingId();
        if (mCachedInputLanguages.get().isEmpty()) {
            fetchInputLanguages();
        }
    }

    /**
     * @deprecated Charging status is now served from a snapshot that is refreshed on a
     * background thread (see {@link #isBatteryCharging()}). This method is a no-op kept for
     * binary compatibility.
     */
    @Deprecated
    public void updateChargingStatus() {
        // no-op
    }

    private void fetchAdvertisingId() {
        try {
            HyBidAdvertisingId advertisingIdTask = new HyBidAdvertisingId(mContext);
            advertisingIdTask.execute((advertisingId, limitTracking) -> {
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
            });
        } catch (RejectedExecutionException exception) {
            Logger.e(TAG, "fetchAdvertisingId", exception);
            if (mListener != null) {
                mListener.onInfoLoaded();
            }
            HyBid.reportException(exception);
        } catch (Exception exception) {
            Logger.e(TAG, "Error executing HyBidAdvertisingId Executor");
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

    private void fetchInputLanguages() {
        try {
            BACKGROUND_EXECUTOR.execute(() -> {
                ArrayList<String> inputLanguages = new ArrayList<>();
                if (mContext != null) {
                    try {
                        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

                        if (inputMethodManager != null) {
                            List<InputMethodInfo> inputMethodInfoList = inputMethodManager.getEnabledInputMethodList();

                            if (inputMethodInfoList != null && !inputMethodInfoList.isEmpty()) {
                                for (InputMethodInfo inputMethodInfo : inputMethodInfoList) {
                                    if (inputMethodInfo == null) {
                                        continue;
                                    }

                                    List<InputMethodSubtype> subtypeList = inputMethodManager.getEnabledInputMethodSubtypeList(inputMethodInfo, true);
                                    if (subtypeList != null) {
                                        for (InputMethodSubtype subtype : subtypeList) {
                                            if (subtype == null) {
                                                continue;
                                            }
                                            String mode = subtype.getMode();
                                            if (mode != null && mode.equals("keyboard")) {
                                                String currentLocale = subtype.getLocale();
                                                if (currentLocale != null && !currentLocale.isEmpty()) {
                                                    inputLanguages.add(currentLocale);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception exception) {
                        Logger.e(TAG, "Error fetching input languages", exception);
                    }
                }
                mCachedInputLanguages.set(Collections.unmodifiableList(inputLanguages));
            });
        } catch (RejectedExecutionException exception) {
            Logger.e(TAG, "Failed to execute fetchInputLanguages", exception);
            HyBid.reportException(exception);
        }
    }

    static final class DeviceSignals {

        static final DeviceSignals EMPTY =
                new DeviceSignals(0, null, null, null, null, null, null, 0, 0L);

        final Integer charging;
        final Integer batteryPercentage;
        final Integer powerSaveMode;
        final Integer freeMemoryMb;
        final Integer totalMemoryMb;
        final Integer dndEnabled;
        final Integer headsetOn;
        final Integer airplaneMode;
        final long fetchedAtMillis;

        DeviceSignals(Integer charging, Integer batteryPercentage, Integer powerSaveMode,
                      Integer freeMemoryMb, Integer totalMemoryMb, Integer dndEnabled,
                      Integer headsetOn, Integer airplaneMode, long fetchedAtMillis) {
            this.charging = charging;
            this.batteryPercentage = batteryPercentage;
            this.powerSaveMode = powerSaveMode;
            this.freeMemoryMb = freeMemoryMb;
            this.totalMemoryMb = totalMemoryMb;
            this.dndEnabled = dndEnabled;
            this.headsetOn = headsetOn;
            this.airplaneMode = airplaneMode;
            this.fetchedAtMillis = fetchedAtMillis;
        }
    }

    private DeviceSignals currentSignals() {
        DeviceSignals signals = mCachedDeviceSignals.get();
        if (signals.fetchedAtMillis == 0
                || System.currentTimeMillis() - signals.fetchedAtMillis >= SIGNALS_CACHE_DURATION_MS) {
            fetchDeviceSignals();
        }
        return signals;
    }

    /**
     * Synchronous counterpart to {@link #fetchDeviceSignals()}; nothing is scheduled.
     */
    private void primeDeviceSignals() {
        try {
            mCachedDeviceSignals.set(readDeviceSignals());
        } catch (Exception exception) {
            Logger.e(TAG, "Error priming device signals", exception);
            HyBid.reportException(exception);
        }
    }

    private void fetchDeviceSignals() {
        if (!mSignalsRefreshInProgress.compareAndSet(false, true)) {
            return;
        }
        try {
            BACKGROUND_EXECUTOR.execute(() -> {
                try {
                    mCachedDeviceSignals.set(readDeviceSignals());
                } catch (Exception exception) {
                    Logger.e(TAG, "Error fetching device signals", exception);
                    HyBid.reportException(exception);
                } finally {
                    mSignalsRefreshInProgress.set(false);
                }
            });
        } catch (RejectedExecutionException exception) {
            mSignalsRefreshInProgress.set(false);
            Logger.e(TAG, "Failed to execute fetchDeviceSignals", exception);
            HyBid.reportException(exception);
        }
    }

    private static final class MemorySize {

        static final MemorySize UNAVAILABLE = new MemorySize(null, null);

        final Integer freeMb;
        final Integer totalMb;

        MemorySize(Integer freeMb, Integer totalMb) {
            this.freeMb = freeMb;
            this.totalMb = totalMb;
        }
    }

    private DeviceSignals readDeviceSignals() {
        BatteryUtils.BatteryStatus battery = readBatteryStatus();
        MemorySize memory = readInternalMemory();
        return new DeviceSignals(
                battery.charging ? 1 : 0,
                battery.percentageAvailable ? battery.percentage : null,
                readPowerSaveMode(),
                memory.freeMb,
                memory.totalMb,
                readDndEnabled(),
                readHeadsetOn(),
                readAirplaneMode(),
                System.currentTimeMillis());
    }

    private BatteryUtils.BatteryStatus readBatteryStatus() {
        try {
            return BatteryUtils.readBatteryStatus(mContext);
        } catch (Exception exception) {
            Logger.e(TAG, "Error reading battery status", exception);
            return BatteryUtils.BatteryStatus.UNAVAILABLE;
        }
    }

    private MemorySize readInternalMemory() {
        try {
            StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
            long blockSize = stat.getBlockSizeLong();
            return new MemorySize(
                    (int) ((stat.getAvailableBlocksLong() * blockSize) / 1024 / 1024),
                    (int) ((stat.getBlockCountLong() * blockSize) / 1024 / 1024));
        } catch (Exception exception) {
            Logger.e(TAG, "Error reading internal memory size", exception);
            return MemorySize.UNAVAILABLE;
        }
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
            return loc.toLanguageTag();
        } else {
            return null;
        }
    }

    public List<String> getInputLanguages() {
        List<String> cached = mCachedInputLanguages.get();
        if (cached.isEmpty()) {
            fetchInputLanguages();
        }
        return new ArrayList<>(cached);
    }

    public Integer isBatteryCharging() {
        return currentSignals().charging;
    }

    public Integer getBatteryLevel() {
        if (mContext == null) {
            return null;
        }
        Integer batteryPercentage = currentSignals().batteryPercentage;
        if (batteryPercentage == null) {
            return null;
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
        }
        return null;
    }

    public Integer isPowerSaveMode() {
        return currentSignals().powerSaveMode;
    }

    private Integer readPowerSaveMode() {
        try {
            if (mContext == null) {
                return null;
            }
            PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                if (powerManager.isPowerSaveMode()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } catch (Exception exception) {
            Logger.e(TAG, "Error reading power save mode", exception);
        }
        return null;
    }

    public Integer getTotalMemoryMb() {
        return currentSignals().totalMemoryMb;
    }

    public Integer getFreeMemoryMb() {
        return currentSignals().freeMemoryMb;
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
        if (mContext == null || mContext.getContentResolver() == null) return null;
        return currentSignals().dndEnabled;
    }

    private Integer readDndEnabled() {
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
        } catch (Exception exception) {
            Logger.e(TAG, "Error reading do not disturb setting", exception);
            return null;
        }
    }

    public Integer isAirplaneModeEnabled() {
        return currentSignals().airplaneMode;
    }

    private Integer readAirplaneMode() {
        try {
            return isAirplaneModeOn() ? 1 : 0;
        } catch (Exception exception) {
            Logger.e(TAG, "Error reading airplane mode setting", exception);
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
        return currentSignals().headsetOn;
    }

    private Integer readHeadsetOn() {
        try {
            boolean readPhoneStatePermission = hasPermission(Manifest.permission.READ_PHONE_STATE);
            if (readPhoneStatePermission) {
                AudioManager am = (AudioManager) this.mContext.getSystemService(Context.AUDIO_SERVICE);
                if (am == null) return null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    AudioDeviceInfo[] devices = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
                    if (devices == null) return null;
                    for (AudioDeviceInfo device : devices) {
                        int type = device.getType();
                        if (type == AudioDeviceInfo.TYPE_WIRED_HEADSET || type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES) {
                            return 1;
                        }
                    }
                    return 0;
                } else {
                    // For older versions, use deprecated methods with suppression
                    @SuppressWarnings("deprecation")
                    boolean isHeadsetConnected = am.isWiredHeadsetOn() || am.isBluetoothScoOn() || am.isBluetoothA2dpOn();
                    return isHeadsetConnected ? 1 : 0;
                }
            }
        } catch (Exception exception) {
            Logger.e(TAG, "Error reading headset state", exception);
        }
        return null;
    }
}