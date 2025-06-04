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

        final CountDownLatch latch = new CountDownLatch(1);
        final int[] batteryPercentage = new int[1];

        new Thread(() -> {
            isBatteryPercentageValueFetched = false;
            BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            if (batteryManager != null) {
                batteryPercentage[0] = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                isBatteryPercentageValueFetched = true;
                latch.countDown();
            } else {
                latch.countDown();
                batteryPercentage[0] = 0;
            }
        }).start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            batteryPercentage[0] = 0;
        }
        return batteryPercentage[0];
    }
}