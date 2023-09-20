package net.pubnative.lite.sdk.utils;

import static net.pubnative.lite.sdk.utils.SkipOffsetManager.getBackButtonDelay;
import static net.pubnative.lite.sdk.utils.SkipOffsetManager.getDefaultRewardedHtmlSkipOffset;
import static net.pubnative.lite.sdk.utils.SkipOffsetManager.getInterstitialHTMLSkipOffset;
import static net.pubnative.lite.sdk.utils.SkipOffsetManager.getMaximumRewardedSkipOffset;
import static net.pubnative.lite.sdk.utils.SkipOffsetManager.getNativeCloseButtonDelay;
import static net.pubnative.lite.sdk.utils.SkipOffsetManager.getRewardedHTMLSkipOffset;
import static net.pubnative.lite.sdk.utils.SkipOffsetManager.getRewardedSkipOffset;

import org.junit.Test;
import static org.junit.Assert.*;

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
    public void testGetBackButtonDelay_nullRemoteConfigDelay() {
        // Test with null remoteConfigDelay
        Integer backButtonDelay = getBackButtonDelay(null);
        assertEquals(SkipOffsetManager.getDefaultBackButtonDelay(), backButtonDelay);
    }

    @Test
    public void testGetBackButtonDelay_negativeRemoteConfigDelay() {
        // Test with negative remoteConfigDelay
        Integer backButtonDelay = getBackButtonDelay(-1);
        assertEquals(SkipOffsetManager.getDefaultBackButtonDelay(), backButtonDelay);
    }

    @Test
    public void testGetBackButtonDelay_remoteConfigDelayGreaterThanBACK_BUTTON_DELAY_MAXIMUM() {
        // Test with remoteConfigDelay greater than BACK_BUTTON_DELAY_MAXIMUM
        Integer backButtonDelay = getBackButtonDelay(SkipOffsetManager.getMaximumBackButtonDelay() + 1);
        assertEquals(SkipOffsetManager.getDefaultBackButtonDelay(), backButtonDelay);
    }

    @Test
    public void testGetBackButtonDelay_validRemoteConfigDelay() {
        // Test with valid remoteConfigDelay
        Integer backButtonDelay = getBackButtonDelay(15);
        Integer desiredDelay = 15;
        assertEquals(desiredDelay, backButtonDelay);
    }


    @Test
    public void testGetInterstitialHTMLSkipOffset_nullRemoteConfigSkipOffset() {
        // Test with null remoteConfigSkipOffset
        Integer remoteConfigSkipOffset = null;
        Integer renderingSkipOffset = 10;
        Integer resultSkipOffset = getInterstitialHTMLSkipOffset(remoteConfigSkipOffset, renderingSkipOffset);
        assertEquals(resultSkipOffset, renderingSkipOffset);
    }

    @Test
    public void testGetInterstitialHTMLSkipOffset_negativeRemoteConfigSkipOffset() {
        // Test with negative remoteConfigSkipOffset
        Integer remoteConfigSkipOffset = -1;
        Integer renderingSkipOffset = 10;
        Integer resultSkipOffset = getInterstitialHTMLSkipOffset(remoteConfigSkipOffset, renderingSkipOffset);
        assertEquals(resultSkipOffset, renderingSkipOffset);
    }

    @Test
    public void testGetInterstitialHTMLSkipOffset_validRemoteConfigSkipOffset() {
        // Test with valid remoteConfigSkipOffset
        Integer remoteConfigSkipOffset = 25;
        Integer renderingSkipOffset = 10;
        Integer resultSkipOffset = getInterstitialHTMLSkipOffset(remoteConfigSkipOffset, renderingSkipOffset);
        assertEquals(resultSkipOffset, remoteConfigSkipOffset);
    }

    @Test
    public void testGetRewardedSkipOffset_nullRemoteConfigSkipOffset() {
        // Test with null remoteConfigSkipOffset
        Integer remoteConfigSkipOffset = null;
        Integer renderingSkipOffset = 10;
        Integer publisherSkipSeconds = 12;
        Integer adParamsSkipSeconds = 8;
        Boolean hasEndcard = false;

        Integer resultSkipOffset = getRewardedSkipOffset(remoteConfigSkipOffset, renderingSkipOffset, adParamsSkipSeconds, hasEndcard);
        assertEquals(resultSkipOffset, renderingSkipOffset);
    }

    @Test
    public void testGetRewardedSkipOffset_negativeRemoteConfigSkipOffset() {
        // Test with negative remoteConfigSkipOffset
        Integer remoteConfigSkipOffset = -1;
        Integer renderingSkipOffset = 10;
        Integer publisherSkipSeconds = 12;
        Integer adParamsSkipSeconds = 8;
        Boolean hasEndcard = false;

        Integer resultSkipOffset = getRewardedSkipOffset(remoteConfigSkipOffset, renderingSkipOffset, adParamsSkipSeconds, hasEndcard);
        assertEquals(resultSkipOffset, renderingSkipOffset);
    }

    @Test
    public void testGetRewardedSkipOffset_remoteConfigSkipOffsetGreaterThanREWARDED_HTML_SKIP_OFFSET() {
        // Test with remoteConfigSkipOffset greater than REWARDED_HTML_SKIP_OFFSET
        Integer remoteConfigSkipOffset = 40;
        Integer renderingSkipOffset = 10;
        Integer publisherSkipSeconds = 12;
        Integer adParamsSkipSeconds = 8;
        Boolean hasEndcard = false;

        Integer correctSkipOffset = 30;

        Integer resultSkipOffset = getRewardedSkipOffset(remoteConfigSkipOffset, renderingSkipOffset, adParamsSkipSeconds, hasEndcard);
        assertEquals(resultSkipOffset, correctSkipOffset);
    }

    @Test
    public void testGetRewardedSkipOffset_validRemoteConfigSkipOffset() {
        // Test with valid remoteConfigSkipOffset
        Integer remoteConfigSkipOffset = 22;
        Integer renderingSkipOffset = 10;
        Integer publisherSkipSeconds = 12;
        Integer adParamsSkipSeconds = 8;
        Boolean hasEndcard = false;

        Integer resultSkipOffset = getRewardedSkipOffset(remoteConfigSkipOffset, renderingSkipOffset, adParamsSkipSeconds, hasEndcard);
        assertEquals(resultSkipOffset, remoteConfigSkipOffset);
    }

    @Test
    public void testGetRewardedSkipOffset_allNullValues() {
        // Test with all null values
        Integer remoteConfigSkipOffset = null;
        Integer renderingSkipOffset = null;
        Integer publisherSkipSeconds = null;
        Integer adParamsSkipSeconds = null;
        Boolean hasEndcard = false;
        Integer defaultRewardedSkipOffset = getMaximumRewardedSkipOffset();

        Integer resultSkipOffset = getRewardedSkipOffset(remoteConfigSkipOffset, renderingSkipOffset, adParamsSkipSeconds, hasEndcard);
        assertEquals(resultSkipOffset, defaultRewardedSkipOffset);
    }

    @Test
    public void testGetRewardedHTMLSkipOffset_nullRemoteConfigSkipOffset() {
        // Test with null remoteConfigSkipOffset
        Integer remoteConfigSkipOffset = null;
        Integer defaultRewardedSkipOffset = getDefaultRewardedHtmlSkipOffset();

        Integer resultSkipOffset = getRewardedHTMLSkipOffset(remoteConfigSkipOffset);
        assertEquals(resultSkipOffset, defaultRewardedSkipOffset);
    }

    @Test
    public void testGetRewardedHTMLSkipOffset_negativeRemoteConfigSkipOffset() {
        // Test with negative remoteConfigSkipOffset
        Integer remoteConfigSkipOffset = -1;
        Integer defaultRewardedSkipOffset = getDefaultRewardedHtmlSkipOffset();

        Integer resultSkipOffset = getRewardedHTMLSkipOffset(remoteConfigSkipOffset);
        assertEquals(resultSkipOffset, defaultRewardedSkipOffset);
    }

    @Test
    public void testGetRewardedHTMLSkipOffset_remoteConfigSkipOffsetGreaterThanREWARDED_HTML_SKIP_OFFSET() {
        // Test with remoteConfigSkipOffset greater than REWARDED_HTML_SKIP_OFFSET
        Integer remoteConfigSkipOffset = 35;
        Integer expectedSkipOffset = 30;

        Integer resultSkipOffset = getRewardedHTMLSkipOffset(remoteConfigSkipOffset);
        assertEquals(resultSkipOffset, expectedSkipOffset);
    }

    @Test
    public void testGetRewardedHTMLSkipOffset_validRemoteConfigSkipOffset() {
        // Test with valid remoteConfigSkipOffset
        Integer remoteConfigSkipOffset = 22;

        Integer resultSkipOffset = getRewardedHTMLSkipOffset(remoteConfigSkipOffset);
        assertEquals(resultSkipOffset, remoteConfigSkipOffset);
    }

}
