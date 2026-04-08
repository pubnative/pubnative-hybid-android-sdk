// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryUtils {

    private static final String TAG = BatteryUtils.class.getSimpleName();
    static Boolean isBatteryPercentageValueFetched = false;

    public static synchronized int getBatteryPercentageSync(Context context) {
        isBatteryPercentageValueFetched = false;
        if (context == null) {
            return 0;
        }

        try {
            Intent batteryIntent = context.registerReceiver(null,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (batteryIntent != null) {
                int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                if (level >= 0 && scale > 0) {
                    isBatteryPercentageValueFetched = true;
                    return (level * 100) / scale;
                }
            }
        } catch (RuntimeException e) {
            Logger.e(TAG, "Could not retrieve battery status. The system may be unstable.", e);
        }
        return 0;
    }

    public static synchronized Boolean isBatteryPercentageValueFetched() {
        return isBatteryPercentageValueFetched;
    }
}