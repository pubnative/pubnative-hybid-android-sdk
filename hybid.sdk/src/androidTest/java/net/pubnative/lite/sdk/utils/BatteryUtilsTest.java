// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class BatteryUtilsTest {

    @Test
    public void testGetBatteryPercentage_withValidContext() {
        Context context = ApplicationProvider.getApplicationContext();
        int percentage = BatteryUtils.getBatteryPercentageSync(context);
        assertTrue("Battery percentage should be >= 0", percentage >= 0);
        assertTrue("Battery percentage should be <= 100", percentage <= 100);
        assertTrue("isBatteryPercentageValueFetched should be true after a successful call", BatteryUtils.isBatteryPercentageValueFetched());
    }

    @Test
    public void testGetBatteryPercentage_withNullContext() {
        int percentage = BatteryUtils.getBatteryPercentageSync(null);
        assertEquals("Percentage should be 0 when context is null", 0, percentage);
        assertFalse("isBatteryPercentageValueFetched should be false when context is null", BatteryUtils.isBatteryPercentageValueFetched());
    }

    @Test
    public void testGetBatteryPercentage() {
        Context context = ApplicationProvider.getApplicationContext();
        int percentage = BatteryUtils.getBatteryPercentageSync(context);
        assertTrue("Battery Value is valid", percentage >= 0 && percentage <= 100);
        assertTrue("isBatteryPercentageValueFetched should be true", BatteryUtils.isBatteryPercentageValueFetched());
    }
}
