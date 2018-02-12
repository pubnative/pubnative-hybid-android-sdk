package net.pubnative.lite.sdk.tracking;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.util.DisplayMetrics;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

class DeviceData extends DeviceDataSummary {

    private static final String INSTALL_ID_KEY = "install.iud";

    final Float screenDensity;

    final Integer dpi;

    final String screenResolution;
    private Context appContext;

    final String locale;

    protected String id;

    final String[] cpuAbi;

    DeviceData(Context appContext, SharedPreferences sharedPref) {
        screenDensity = getScreenDensity(appContext);
        dpi = getScreenDensityDpi(appContext);
        screenResolution = getScreenResolution(appContext);
        this.appContext = appContext;
        locale = getLocale();
        id = retrieveUniqueInstallId(sharedPref);
        cpuAbi = getCpuAbi();
    }

    @Override
    public void toStream(JsonStream writer) throws IOException {
        writer.beginObject();
        serialiseMinimalDeviceData(writer);

        writer
                .name("id").value(id)
                .name("freeMemory").value(getFreeMemory())
                .name("totalMemory").value(getTotalMemory())
                .name("freeDisk").value(getFreeDisk())
                .name("orientation").value(getOrientation(appContext));

        writer
                .name("batteryLevel").value(getBatteryLevel(appContext))
                .name("charging").value(isCharging(appContext))
                .name("locationStatus").value(getLocationStatus(appContext))
                .name("networkAccess").value(getNetworkAccess(appContext))
                .name("time").value(getTime())
                .name("brand").value(Build.BRAND)
                .name("apiLevel").value(Build.VERSION.SDK_INT)
                .name("osBuild").value(Build.DISPLAY)
                .name("locale").value(locale)
                .name("screenDensity").value(screenDensity)
                .name("dpi").value(dpi)
                .name("screenResolution").value(screenResolution);

        writer.name("cpuAbi").beginArray();
        for (String s : cpuAbi) {
            writer.value(s);
        }
        writer.endArray();
        writer.endObject();
    }

    String getUserId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    private static Float getScreenDensity(Context appContext) {
        Resources resources = appContext.getResources();
        if (resources == null) {
            return null;
        }
        return resources.getDisplayMetrics().density;
    }

    private static Integer getScreenDensityDpi(Context appContext) {
        Resources resources = appContext.getResources();
        if (resources == null) {
            return null;
        }
        return resources.getDisplayMetrics().densityDpi;
    }

    private static String getScreenResolution(Context appContext) {
        Resources resources = appContext.getResources();
        if (resources == null) {
            return null;
        }
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int max = Math.max(metrics.widthPixels, metrics.heightPixels);
        int min = Math.min(metrics.widthPixels, metrics.heightPixels);
        return String.format(Locale.US, "%dx%d", max, min);
    }

    static Long getTotalMemory() {
        if (Runtime.getRuntime().maxMemory() != Long.MAX_VALUE) {
            return Runtime.getRuntime().maxMemory();
        } else {
            return Runtime.getRuntime().totalMemory();
        }
    }

    private static String getLocale() {
        return Locale.getDefault().toString();
    }

    private String retrieveUniqueInstallId(SharedPreferences sharedPref) {
        String installId = sharedPref.getString(INSTALL_ID_KEY, null);

        if (installId == null) {
            installId = UUID.randomUUID().toString();
            sharedPref.edit().putString(INSTALL_ID_KEY, installId).apply();
        }
        return installId;
    }

    private static String[] getCpuAbi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return SupportedAbiWrapper.getSupportedAbis();
        }
        return Abi2Wrapper.getAbi1andAbi2();
    }

    private static class SupportedAbiWrapper {
        public static String[] getSupportedAbis() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return Build.SUPPORTED_ABIS;
            } else {
                return new String[]{};
            }
        }
    }

    private static class Abi2Wrapper {
        public static String[] getAbi1andAbi2() {
            return new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
    }

    private static Long getFreeDisk() {
        try {
            StatFs externalStat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long externalBytesAvailable =
                    (long) externalStat.getBlockSize() * (long) externalStat.getBlockCount();

            StatFs internalStat = new StatFs(Environment.getDataDirectory().getPath());
            long internalBytesAvailable =
                    (long) internalStat.getBlockSize() * (long) internalStat.getBlockCount();

            return Math.min(internalBytesAvailable, externalBytesAvailable);
        } catch (Exception exception) {
            Logger.warn("Could not get freeDisk");
        }
        return null;
    }

    private static Long getFreeMemory() {
        Runtime runtime = Runtime.getRuntime();
        if (runtime.maxMemory() != Long.MAX_VALUE) {
            return runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory();
        } else {
            return runtime.freeMemory();
        }
    }

    private static String getOrientation(Context appContext) {
        String orientation;
        switch (appContext.getResources().getConfiguration().orientation) {
            case android.content.res.Configuration.ORIENTATION_LANDSCAPE:
                orientation = "landscape";
                break;
            case android.content.res.Configuration.ORIENTATION_PORTRAIT:
                orientation = "portrait";
                break;
            default:
                orientation = null;
                break;
        }
        return orientation;
    }

    private static Float getBatteryLevel(Context appContext) {
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = appContext.registerReceiver(null, ifilter);

            return batteryStatus.getIntExtra("level", -1)
                    / (float) batteryStatus.getIntExtra("scale", -1);
        } catch (Exception exception) {
            Logger.warn("Could not get batteryLevel");
        }
        return null;
    }

    private static Boolean isCharging(Context appContext) {
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = appContext.registerReceiver(null, ifilter);

            int status = batteryStatus.getIntExtra("status", -1);
            return (status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL);
        } catch (Exception exception) {
            Logger.warn("Could not get charging status");
        }
        return null;
    }

    private static String getLocationStatus(Context appContext) {
        try {
            ContentResolver cr = appContext.getContentResolver();
            String providersAllowed =
                    Settings.Secure.getString(cr, Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (providersAllowed != null && providersAllowed.length() > 0) {
                return "allowed";
            } else {
                return "disallowed";
            }
        } catch (Exception exception) {
            Logger.warn("Could not get locationStatus");
        }
        return null;
    }

    private static String getNetworkAccess(Context appContext) {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                if (activeNetwork.getType() == 1) {
                    return "wifi";
                } else if (activeNetwork.getType() == 9) {
                    return "ethernet";
                } else {
                    // We default to cellular as the other enums are all cellular in some
                    // form or another
                    return "cellular";
                }
            } else {
                return "none";
            }
        } catch (Exception exception) {
            Logger.warn("Could not get network access information, we "
                    + "recommend granting the 'android.permission.ACCESS_NETWORK_STATE' permission");
        }
        return null;
    }

    private String getTime() {
        return DateUtils.toIso8601(new Date());
    }
}
