// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.os.BatteryManager;

import java.util.concurrent.CountDownLatch;

public class BatteryUtils {

    static Boolean isBatteryPercentageValueFetched = false;

    public synchronized static int getBatteryPercentageSync(Context context) {
        isBatteryPercentageValueFetched = false;
        BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        if (batteryManager != null) {
            int percentage = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            isBatteryPercentageValueFetched = true;
            return (percentage != Integer.MIN_VALUE) ? percentage : 0;
        }
        return 0;
    }
}