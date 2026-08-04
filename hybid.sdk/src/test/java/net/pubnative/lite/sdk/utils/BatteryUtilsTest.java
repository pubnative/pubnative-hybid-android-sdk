// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class BatteryUtilsTest {

    @Mock
    Context mContext;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getBatteryPercentageSync_nullContext_returnsZero() {
        int result = BatteryUtils.getBatteryPercentageSync(null);
        assertEquals(0, result);
        assertFalse(BatteryUtils.isBatteryPercentageValueFetched());
    }

    @Test
    public void getBatteryPercentageSync_nullIntent_returnsZero() {
        when(mContext.registerReceiver(isNull(), any())).thenReturn(null);
        int result = BatteryUtils.getBatteryPercentageSync(mContext);
        assertEquals(0, result);
        assertFalse(BatteryUtils.isBatteryPercentageValueFetched());
    }

    @Test
    public void getBatteryPercentageSync_validLevelAndScale_returnsCorrectPercentage() {
        Intent batteryIntent = new Intent();
        batteryIntent.putExtra(BatteryManager.EXTRA_LEVEL, 75);
        batteryIntent.putExtra(BatteryManager.EXTRA_SCALE, 100);
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        int result = BatteryUtils.getBatteryPercentageSync(mContext);

        assertEquals(75, result);
        assertTrue(BatteryUtils.isBatteryPercentageValueFetched());
    }

    @Test
    public void getBatteryPercentageSync_levelNegative_returnsZeroAndNotFetched() {
        Intent batteryIntent = new Intent();
        batteryIntent.putExtra(BatteryManager.EXTRA_LEVEL, -1);
        batteryIntent.putExtra(BatteryManager.EXTRA_SCALE, 100);
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        int result = BatteryUtils.getBatteryPercentageSync(mContext);

        assertEquals(0, result);
        assertFalse(BatteryUtils.isBatteryPercentageValueFetched());
    }

    @Test
    public void getBatteryPercentageSync_scaleZero_returnsZeroAndNotFetched() {
        Intent batteryIntent = new Intent();
        batteryIntent.putExtra(BatteryManager.EXTRA_LEVEL, 50);
        batteryIntent.putExtra(BatteryManager.EXTRA_SCALE, 0);
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        int result = BatteryUtils.getBatteryPercentageSync(mContext);

        assertEquals(0, result);
        assertFalse(BatteryUtils.isBatteryPercentageValueFetched());
    }

    @Test
    public void getBatteryPercentageSync_fullBattery_returns100() {
        Intent batteryIntent = new Intent();
        batteryIntent.putExtra(BatteryManager.EXTRA_LEVEL, 100);
        batteryIntent.putExtra(BatteryManager.EXTRA_SCALE, 100);
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        int result = BatteryUtils.getBatteryPercentageSync(mContext);

        assertEquals(100, result);
        assertTrue(BatteryUtils.isBatteryPercentageValueFetched());
    }

    @Test
    public void getBatteryPercentageSync_nonStandardScale_returnsCorrectPercentage() {
        Intent batteryIntent = new Intent();
        batteryIntent.putExtra(BatteryManager.EXTRA_LEVEL, 128);
        batteryIntent.putExtra(BatteryManager.EXTRA_SCALE, 256);
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        int result = BatteryUtils.getBatteryPercentageSync(mContext);

        assertEquals(50, result);
        assertTrue(BatteryUtils.isBatteryPercentageValueFetched());
    }

    @Test
    public void getBatteryPercentageSync_securityException_returnsZeroAndNotFetched() {
        when(mContext.registerReceiver(isNull(), any())).thenThrow(new SecurityException("no permission"));

        int result = BatteryUtils.getBatteryPercentageSync(mContext);

        assertEquals(0, result);
        assertFalse(BatteryUtils.isBatteryPercentageValueFetched());
    }

    @Test
    public void getBatteryPercentageSync_illegalArgumentException_returnsZeroAndNotFetched() {
        when(mContext.registerReceiver(isNull(), any())).thenThrow(new IllegalArgumentException("bad filter"));

        int result = BatteryUtils.getBatteryPercentageSync(mContext);

        assertEquals(0, result);
        assertFalse(BatteryUtils.isBatteryPercentageValueFetched());
    }

    @Test
    public void isChargingSync_nullContext_returnsFalse() {
        assertFalse(BatteryUtils.isChargingSync(null));
    }

    @Test
    public void isChargingSync_nullIntent_returnsFalse() {
        when(mContext.registerReceiver(isNull(), any())).thenReturn(null);

        assertFalse(BatteryUtils.isChargingSync(mContext));
    }

    @Test
    public void isChargingSync_statusCharging_returnsTrue() {
        Intent batteryIntent = new Intent();
        batteryIntent.putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_CHARGING);
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        assertTrue(BatteryUtils.isChargingSync(mContext));
    }

    @Test
    public void isChargingSync_statusFull_returnsTrue() {
        Intent batteryIntent = new Intent();
        batteryIntent.putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_FULL);
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        assertTrue(BatteryUtils.isChargingSync(mContext));
    }

    @Test
    public void isChargingSync_statusDischarging_returnsFalse() {
        Intent batteryIntent = new Intent();
        batteryIntent.putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_DISCHARGING);
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        assertFalse(BatteryUtils.isChargingSync(mContext));
    }

    @Test
    public void isChargingSync_statusNotCharging_returnsFalse() {
        Intent batteryIntent = new Intent();
        batteryIntent.putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_NOT_CHARGING);
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        assertFalse(BatteryUtils.isChargingSync(mContext));
    }

    @Test
    public void isChargingSync_statusUnknown_returnsFalse() {
        Intent batteryIntent = new Intent();
        batteryIntent.putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        assertFalse(BatteryUtils.isChargingSync(mContext));
    }

    @Test
    public void isChargingSync_missingStatusExtra_returnsFalse() {
        Intent batteryIntent = new Intent();
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        assertFalse(BatteryUtils.isChargingSync(mContext));
    }

    @Test
    public void isChargingSync_securityException_returnsFalse() {
        when(mContext.registerReceiver(isNull(), any())).thenThrow(new SecurityException("no permission"));

        assertFalse(BatteryUtils.isChargingSync(mContext));
    }

    @Test
    public void isChargingSync_illegalArgumentException_returnsFalse() {
        when(mContext.registerReceiver(isNull(), any())).thenThrow(new IllegalArgumentException("bad filter"));

        assertFalse(BatteryUtils.isChargingSync(mContext));
    }
}
