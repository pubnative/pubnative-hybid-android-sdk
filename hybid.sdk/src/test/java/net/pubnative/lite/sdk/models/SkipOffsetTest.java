// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SkipOffsetTest {

    @Test
    public void constructor_whenIsCustomIsTrue_setsFieldsCorrectly() {
        int offset = 10;
        boolean isCustom = true;

        SkipOffset skipOffset = new SkipOffset(offset, isCustom);

        assertEquals(offset, skipOffset.getOffset());
        assertTrue(skipOffset.isCustom());
    }

    @Test
    public void constructor_whenIsCustomIsFalse_setsFieldsCorrectly() {
        int offset = 5;
        boolean isCustom = false;

        SkipOffset skipOffset = new SkipOffset(offset, isCustom);

        assertEquals(offset, skipOffset.getOffset());
        assertFalse(skipOffset.isCustom());
    }
}