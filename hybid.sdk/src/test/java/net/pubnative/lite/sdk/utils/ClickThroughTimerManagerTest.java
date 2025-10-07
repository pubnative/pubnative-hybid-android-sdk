package net.pubnative.lite.sdk.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ClickThroughTimerManagerTest {

    @Test
    public void getClickThroughTimer_whenInputIsNull_returnsDefault() {
        // Test the "if (remoteConfigClickThroughTimer != null)" false path
        int result = ClickThroughTimerManager.getClickThroughTimer(null);
        assertEquals(10 * 1000, result);
    }

    @Test
    public void getClickThroughTimer_whenInputIsGreaterThanMax_returnsMax() {
        // Test the "if (remoteConfigClickThroughTimer > MAX_CLICK_THROUGH_TIMER)" path
        int result = ClickThroughTimerManager.getClickThroughTimer(40);
        assertEquals(35 * 1000, result);
    }

    @Test
    public void getClickThroughTimer_whenInputIsLessThanMin_returnsMin() {
        // Test the "else if (remoteConfigClickThroughTimer < MIN_CLICK_THROUGH_TIMER)" path
        int result = ClickThroughTimerManager.getClickThroughTimer(2);
        assertEquals(5 * 1000, result);
    }

    @Test
    public void getClickThroughTimer_whenInputIsWithinRange_returnsInput() {
        // Test the final "else" path
        int result = ClickThroughTimerManager.getClickThroughTimer(15);
        assertEquals(15 * 1000, result);
    }

    @Test
    public void getClickThroughTimer_whenInputIsEqualToBoundaries_returnsInput() {
        // Test the boundary conditions specifically
        int resultAtMin = ClickThroughTimerManager.getClickThroughTimer(5);
        assertEquals(5 * 1000, resultAtMin);

        int resultAtMax = ClickThroughTimerManager.getClickThroughTimer(25);
        assertEquals(25 * 1000, resultAtMax);
    }
}