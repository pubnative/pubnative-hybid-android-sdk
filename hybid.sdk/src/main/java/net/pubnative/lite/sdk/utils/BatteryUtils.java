// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.content.Context;
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
            BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            if (batteryManager != null) {
                int percentage = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                isBatteryPercentageValueFetched = true;
                return (percentage != Integer.MIN_VALUE) ? percentage : 0;
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