// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    // readBatteryStatus: the merged single-sticky-read entry point.

    @Test
    public void readBatteryStatus_readsChargingAndLevelFromASingleRegisterReceiver() {
        Intent batteryIntent = new Intent();
        batteryIntent.putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_CHARGING);
        batteryIntent.putExtra(BatteryManager.EXTRA_LEVEL, 75);
        batteryIntent.putExtra(BatteryManager.EXTRA_SCALE, 100);
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        BatteryUtils.BatteryStatus status = BatteryUtils.readBatteryStatus(mContext);

        assertTrue(status.charging);
        assertTrue(status.percentageAvailable);
        assertEquals(75, status.percentage);
        // The whole point of the merge: one binder round-trip, not two.
        verify(mContext, times(1)).registerReceiver(isNull(), any());
    }

    @Test
    public void readBatteryStatus_nullContext_returnsUnavailable() {
        BatteryUtils.BatteryStatus status = BatteryUtils.readBatteryStatus(null);

        assertSame(BatteryUtils.BatteryStatus.UNAVAILABLE, status);
        assertFalse(status.charging);
        assertFalse(status.percentageAvailable);
        assertEquals(0, status.percentage);
    }

    @Test
    public void readBatteryStatus_nullIntent_returnsUnavailable() {
        when(mContext.registerReceiver(isNull(), any())).thenReturn(null);

        assertSame(BatteryUtils.BatteryStatus.UNAVAILABLE, BatteryUtils.readBatteryStatus(mContext));
    }

    @Test
    public void readBatteryStatus_securityException_returnsUnavailable() {
        when(mContext.registerReceiver(isNull(), any())).thenThrow(new SecurityException("no permission"));

        assertSame(BatteryUtils.BatteryStatus.UNAVAILABLE, BatteryUtils.readBatteryStatus(mContext));
    }

    @Test
    public void readBatteryStatus_illegalArgumentException_returnsUnavailable() {
        when(mContext.registerReceiver(isNull(), any())).thenThrow(new IllegalArgumentException("bad filter"));

        assertSame(BatteryUtils.BatteryStatus.UNAVAILABLE, BatteryUtils.readBatteryStatus(mContext));
    }

    @Test
    public void readBatteryStatus_statusFull_reportsCharging() {
        Intent batteryIntent = new Intent();
        batteryIntent.putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_FULL);
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        assertTrue(BatteryUtils.readBatteryStatus(mContext).charging);
    }

    @Test
    public void readBatteryStatus_statusDischarging_reportsNotCharging() {
        Intent batteryIntent = new Intent();
        batteryIntent.putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_DISCHARGING);
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        assertFalse(BatteryUtils.readBatteryStatus(mContext).charging);
    }

    @Test
    public void readBatteryStatus_missingStatusExtra_reportsNotCharging() {
        Intent batteryIntent = new Intent();
        batteryIntent.putExtra(BatteryManager.EXTRA_LEVEL, 50);
        batteryIntent.putExtra(BatteryManager.EXTRA_SCALE, 100);
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        BatteryUtils.BatteryStatus status = BatteryUtils.readBatteryStatus(mContext);

        assertFalse(status.charging);
        assertTrue(status.percentageAvailable);
        assertEquals(50, status.percentage);
    }

    @Test
    public void readBatteryStatus_nonStandardScale_normalisesPercentageTo100() {
        Intent batteryIntent = new Intent();
        batteryIntent.putExtra(BatteryManager.EXTRA_LEVEL, 128);
        batteryIntent.putExtra(BatteryManager.EXTRA_SCALE, 256);
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        assertEquals(50, BatteryUtils.readBatteryStatus(mContext).percentage);
    }

    @Test
    public void readBatteryStatus_levelNegative_keepsChargingButMarksPercentageUnavailable() {
        Intent batteryIntent = new Intent();
        batteryIntent.putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_CHARGING);
        batteryIntent.putExtra(BatteryManager.EXTRA_LEVEL, -1);
        batteryIntent.putExtra(BatteryManager.EXTRA_SCALE, 100);
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        BatteryUtils.BatteryStatus status = BatteryUtils.readBatteryStatus(mContext);

        // An unusable level must not discard the charging flag read from the same intent.
        assertTrue(status.charging);
        assertFalse(status.percentageAvailable);
        assertEquals(0, status.percentage);
    }

    @Test
    public void readBatteryStatus_scaleZero_keepsChargingButMarksPercentageUnavailable() {
        Intent batteryIntent = new Intent();
        batteryIntent.putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_CHARGING);
        batteryIntent.putExtra(BatteryManager.EXTRA_LEVEL, 50);
        batteryIntent.putExtra(BatteryManager.EXTRA_SCALE, 0);
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        BatteryUtils.BatteryStatus status = BatteryUtils.readBatteryStatus(mContext);

        assertTrue(status.charging);
        assertFalse(status.percentageAvailable);
        assertEquals(0, status.percentage);
    }

    @Test
    public void getBatteryPercentageSync_delegates_performsASingleRegisterReceiver() {
        Intent batteryIntent = new Intent();
        batteryIntent.putExtra(BatteryManager.EXTRA_LEVEL, 40);
        batteryIntent.putExtra(BatteryManager.EXTRA_SCALE, 100);
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        assertEquals(40, BatteryUtils.getBatteryPercentageSync(mContext));
        verify(mContext, times(1)).registerReceiver(isNull(), any());
    }

    @Test
    public void isChargingSync_delegates_performsASingleRegisterReceiver() {
        Intent batteryIntent = new Intent();
        batteryIntent.putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_CHARGING);
        when(mContext.registerReceiver(isNull(), any())).thenReturn(batteryIntent);

        assertTrue(BatteryUtils.isChargingSync(mContext));
        verify(mContext, times(1)).registerReceiver(isNull(), any());
    }
}
