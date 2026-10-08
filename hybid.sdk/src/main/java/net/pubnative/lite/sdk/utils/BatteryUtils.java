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

    public static final class BatteryStatus {

        public static final BatteryStatus UNAVAILABLE = new BatteryStatus(false, 0, false);

        public final boolean charging;
        public final int percentage;
        public final boolean percentageAvailable;

        BatteryStatus(boolean charging, int percentage, boolean percentageAvailable) {
            this.charging = charging;
            this.percentage = percentage;
            this.percentageAvailable = percentageAvailable;
        }
    }

    public static BatteryStatus readBatteryStatus(Context context) {
        if (context == null) {
            return BatteryStatus.UNAVAILABLE;
        }

        try {
            Intent batteryIntent = context.registerReceiver(null,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (batteryIntent != null) {
                int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean charging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL;

                int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                if (level >= 0 && scale > 0) {
                    return new BatteryStatus(charging, (level * 100) / scale, true);
                }
                return new BatteryStatus(charging, 0, false);
            }
        } catch (RuntimeException e) {
            Logger.e(TAG, "Could not retrieve battery status. The system may be unstable.", e);
        }
        return BatteryStatus.UNAVAILABLE;
    }

    @Deprecated
    public static synchronized int getBatteryPercentageSync(Context context) {
        BatteryStatus batteryStatus = readBatteryStatus(context);
        isBatteryPercentageValueFetched = batteryStatus.percentageAvailable;
        return batteryStatus.percentageAvailable ? batteryStatus.percentage : 0;
    }

    public static synchronized Boolean isBatteryPercentageValueFetched() {
        return isBatteryPercentageValueFetched;
    }

    @Deprecated
    public static boolean isChargingSync(Context context) {
        return readBatteryStatus(context).charging;
    }
}
