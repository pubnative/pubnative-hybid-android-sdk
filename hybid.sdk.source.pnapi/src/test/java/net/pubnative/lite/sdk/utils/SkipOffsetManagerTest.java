// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static net.pubnative.lite.sdk.utils.SkipOffsetManager.getDefaultRewardedHtmlSkipOffset;
import static net.pubnative.lite.sdk.utils.SkipOffsetManager.getHTMLSkipOffset;
import static net.pubnative.lite.sdk.utils.SkipOffsetManager.getMaximumRewardedSkipOffset;
import static net.pubnative.lite.sdk.utils.SkipOffsetManager.getNativeCloseButtonDelay;
import static net.pubnative.lite.sdk.utils.SkipOffsetManager.getVideoSkipOffset;

import org.junit.Test;


public class SkipOffsetManagerTest {

    @Test
    public void testGetNativeCloseButtonDelay_nullRemoteConfigDelay() {
        // Test with null remoteConfigDelay
        Integer nativeCloseButtonDelay = getNativeCloseButtonDelay(null);
        assertEquals(SkipOffsetManager.getDefaultNativeCloseButtonDelay(), nativeCloseButtonDelay);
    }

    @Test
    public void testGetNativeCloseButtonDelay_negativeRemoteConfigDelay() {
        // Test with negative remoteConfigDelay
        Integer nativeCloseButtonDelay = getNativeCloseButtonDelay(-1);
        assertEquals(SkipOffsetManager.getDefaultNativeCloseButtonDelay(), nativeCloseButtonDelay);
    }

    @Test
    public void testGetNativeCloseButtonDelay_remoteConfigDelayGreaterThanNATIVE_CLOSE_BUTTON_DELAY() {
        // Test with remoteConfigDelay greater than NATIVE_CLOSE_BUTTON_DELAY
        Integer nativeCloseButtonDelay = getNativeCloseButtonDelay(SkipOffsetManager.getDefaultNativeCloseButtonDelay() + 1);
        assertEquals(SkipOffsetManager.getDefaultNativeCloseButtonDelay(), nativeCloseButtonDelay);
    }

    @Test
    public void testGetNativeCloseButtonDelay_validRemoteConfigDelay() {
        // Test with valid remoteConfigDelay
        Integer nativeCloseButtonDelay = getNativeCloseButtonDelay(10);
        Integer desiredDelay = 10;
        assertEquals(desiredDelay, nativeCloseButtonDelay);
    }

    @Test
    public void testGetInterstitialHTMLSkipOffset_nullRemoteConfigSkipOffset() {
        // Test with null remoteConfigSkipOffset
        Integer remoteConfigSkipOffset = null;
        Integer resultSkipOffset = getHTMLSkipOffset(remoteConfigSkipOffset, true);
        assertEquals((int) resultSkipOffset, SkipOffsetManager.INTERSTITIAL_MRAID);
    }

    @Test
    public void testGetInterstitialHTMLSkipOffset_negativeRemoteConfigSkipOffset() {
        // Test with negative remoteConfigSkipOffset
        Integer remoteConfigSkipOffset = -1;
        Integer resultSkipOffset = getHTMLSkipOffset(remoteConfigSkipOffset, true);
        assertEquals((int) resultSkipOffset, SkipOffsetManager.INTERSTITIAL_MRAID);
    }

    @Test
    public void testGetInterstitialHTMLSkipOffset_validRemoteConfigSkipOffset() {
        // Test with valid remoteConfigSkipOffset
        Integer remoteConfigSkipOffset = 25;
        Integer resultSkipOffset = getHTMLSkipOffset(remoteConfigSkipOffset, true);
        assertEquals(resultSkipOffset, remoteConfigSkipOffset);
    }

    @Test
    public void testGetRewardedSkipOffset_nullRemoteConfigSkipOffset() {
        // Test with null remoteConfigSkipOffset
        Integer remoteConfigSkipOffset = null;
        Integer publisherSkipSeconds = 8;
        Integer adParamsSkipSeconds = 8;
        Boolean hasEndcard = false;

        Integer resultSkipOffset = getVideoSkipOffset(remoteConfigSkipOffset, publisherSkipSeconds, adParamsSkipSeconds, hasEndcard, false);
        assertEquals(resultSkipOffset, publisherSkipSeconds);
    }

    @Test
    public void testGetRewardedSkipOffset_negativeRemoteConfigSkipOffset() {
        // Test with negative remoteConfigSkipOffset
        Integer remoteConfigSkipOffset = -1;
        Integer publisherSkipSeconds = 12;
        Integer adParamsSkipSeconds = 8;
        Boolean hasEndcard = false;

        Integer resultSkipOffset = getVideoSkipOffset(remoteConfigSkipOffset, publisherSkipSeconds, adParamsSkipSeconds, hasEndcard, false);
        assertNotSame(resultSkipOffset, publisherSkipSeconds);
    }

    @Test
    public void testGetRewardedSkipOffset_remoteConfigSkipOffsetGreaterThanREWARDED_HTML_SKIP_OFFSET() {
        // Test with remoteConfigSkipOffset greater than REWARDED_HTML_SKIP_OFFSET
        Integer remoteConfigSkipOffset = 40;
        Integer publisherSkipSeconds = 12;
        Integer adParamsSkipSeconds = 8;
        Boolean hasEndcard = false;

        Integer correctSkipOffset = 8;

        Integer resultSkipOffset = getVideoSkipOffset(remoteConfigSkipOffset, publisherSkipSeconds, adParamsSkipSeconds, hasEndcard, false);
        assertEquals(resultSkipOffset, correctSkipOffset);
    }

    @Test
    public void testGetRewardedSkipOffset_validRemoteConfigSkipOffset() {
        // Test with valid remoteConfigSkipOffset
        Integer remoteConfigSkipOffset = 22;
        Integer publisherSkipSeconds = 12;
        Integer adParamsSkipSeconds = 8;
        Boolean hasEndcard = false;

        Integer resultSkipOffset = getVideoSkipOffset(remoteConfigSkipOffset, publisherSkipSeconds, adParamsSkipSeconds, hasEndcard, false);
        assertNotSame(resultSkipOffset, remoteConfigSkipOffset);
    }

    @Test
    public void testGetRewardedSkipOffset_allNullValues() {
        // Test with all null values
        Integer remoteConfigSkipOffset = null;
        Integer publisherSkipSeconds = null;
        Integer adParamsSkipSeconds = null;
        Boolean hasEndcard = false;
        Integer defaultRewardedSkipOffset = getMaximumRewardedSkipOffset();

        Integer resultSkipOffset = getVideoSkipOffset(remoteConfigSkipOffset, publisherSkipSeconds, adParamsSkipSeconds, hasEndcard, false);
        assertEquals(resultSkipOffset, defaultRewardedSkipOffset);
    }

    @Test
    public void testGetRewardedHTMLSkipOffset_nullRemoteConfigSkipOffset() {
        // Test with null remoteConfigSkipOffset
        Integer remoteConfigSkipOffset = null;
        Integer defaultRewardedSkipOffset = getDefaultRewardedHtmlSkipOffset();

        Integer resultSkipOffset = getHTMLSkipOffset(remoteConfigSkipOffset, false);
        assertEquals(resultSkipOffset, defaultRewardedSkipOffset);
    }

    @Test
    public void testGetRewardedHTMLSkipOffset_negativeRemoteConfigSkipOffset() {
        // Test with negative remoteConfigSkipOffset
        Integer remoteConfigSkipOffset = -1;
        Integer defaultRewardedSkipOffset = getDefaultRewardedHtmlSkipOffset();

        Integer resultSkipOffset = getHTMLSkipOffset(remoteConfigSkipOffset, false);
        assertEquals(resultSkipOffset, defaultRewardedSkipOffset);
    }

    @Test
    public void testGetRewardedHTMLSkipOffset_remoteConfigSkipOffsetGreaterThanREWARDED_HTML_SKIP_OFFSET() {
        // Test with remoteConfigSkipOffset greater than REWARDED_HTML_SKIP_OFFSET
        Integer remoteConfigSkipOffset = 35;
        Integer expectedSkipOffset = 30;

        Integer resultSkipOffset = getHTMLSkipOffset(remoteConfigSkipOffset, false);
        assertEquals(resultSkipOffset, expectedSkipOffset);
    }

    @Test
    public void testGetRewardedHTMLSkipOffset_validRemoteConfigSkipOffset() {
        // Test with valid remoteConfigSkipOffset
        Integer remoteConfigSkipOffset = 22;

        Integer resultSkipOffset = getHTMLSkipOffset(remoteConfigSkipOffset, false);
        assertEquals(resultSkipOffset, remoteConfigSkipOffset);
    }

}
