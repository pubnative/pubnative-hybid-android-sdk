// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//

package net.pubnative.lite.sdk.utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class CheckUtilsTest {

    @Before
    public void setup() {
        CheckUtils.NoThrow.setStrictMode(false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkArgument_shouldThrow_whenFalse() {
        CheckUtils.checkArgument(false, "Expected exception");
    }

    @Test
    public void checkArgument_shouldNotThrow_whenTrue() {
        CheckUtils.checkArgument(true, "Should pass");
    }

    @Test
    public void noThrow_checkArgument_shouldReturnFalse_whenFalse_andStrictModeOff() {
        CheckUtils.NoThrow.setStrictMode(false);
        boolean result = CheckUtils.NoThrow.checkArgument(false, "Should log, not throw");
        assertFalse(result);
    }

    @Test
    public void noThrow_checkArgument_shouldReturnTrue_whenTrue() {
        CheckUtils.NoThrow.setStrictMode(false);
        boolean result = CheckUtils.NoThrow.checkArgument(true, "Should return true");
        assertTrue(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noThrow_checkArgument_shouldThrow_whenStrictModeOn() {
        CheckUtils.NoThrow.setStrictMode(true);
        CheckUtils.NoThrow.checkArgument(false, "Should throw in strict mode");
    }

    @Test
    public void noThrow_checkNotNull_shouldReturnTrue_whenNotNull() {
        Object obj = new Object();
        boolean result = CheckUtils.NoThrow.checkNotNull(obj, "Should be true");
        assertTrue(result);
    }

    @Test
    public void noThrow_checkNotNull_shouldReturnFalse_whenNull_andStrictModeOff() {
        CheckUtils.NoThrow.setStrictMode(false);
        boolean result = CheckUtils.NoThrow.checkNotNull(null, "Should log, not throw");
        assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void noThrow_checkNotNull_shouldThrow_whenNull_andStrictModeOn() {
        CheckUtils.NoThrow.setStrictMode(true);
        CheckUtils.NoThrow.checkNotNull(null, "Should throw in strict mode");
    }
}