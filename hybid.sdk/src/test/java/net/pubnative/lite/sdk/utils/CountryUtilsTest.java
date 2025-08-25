// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class CountryUtilsTest {

    @Test
    public void isGDPRCountry_shouldReturnTrue_forEUCountry() {
        assertTrue(CountryUtils.isGDPRCountry("DE"));  // Germany
        assertTrue(CountryUtils.isGDPRCountry("NL"));  // Netherlands
        assertTrue(CountryUtils.isGDPRCountry("EE"));  // Estonia
        assertTrue(CountryUtils.isGDPRCountry("SE"));  // Sweden
    }

    @Test
    public void isGDPRCountry_shouldReturnFalse_forNonGDPRCountry() {
        assertFalse(CountryUtils.isGDPRCountry("US"));
        assertFalse(CountryUtils.isGDPRCountry("CN"));
        assertFalse(CountryUtils.isGDPRCountry("IN"));
        assertFalse(CountryUtils.isGDPRCountry("EG"));
    }
}