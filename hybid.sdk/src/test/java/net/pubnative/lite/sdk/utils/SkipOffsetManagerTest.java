// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import static junit.framework.Assert.assertEquals;
import static net.pubnative.lite.sdk.utils.SkipOffsetManager.getHTMLSkipOffset;
import static net.pubnative.lite.sdk.utils.SkipOffsetManager.getMaximumRewardedSkipOffset;
import static net.pubnative.lite.sdk.utils.SkipOffsetManager.getNativeCloseButtonDelay;
import static net.pubnative.lite.sdk.utils.SkipOffsetManager.getVideoSkipOffset;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Field;


public class SkipOffsetManagerTest {

    @After
    public void tearDown() throws Exception {
        // Reset private static boolean flags after each test to ensure test independence
        SkipOffsetManager.reset();
    }

    private void setStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    @Test
    public void testGetNativeCloseButtonDelay_nullRemoteConfigDelay() {
        Integer nativeCloseButtonDelay = getNativeCloseButtonDelay(null);
        assertEquals(SkipOffsetManager.getDefaultNativeCloseButtonDelay(), nativeCloseButtonDelay);
    }

    @Test
    public void testGetNativeCloseButtonDelay_negativeRemoteConfigDelay() {
        Integer nativeCloseButtonDelay = getNativeCloseButtonDelay(-1);
        assertEquals(SkipOffsetManager.getDefaultNativeCloseButtonDelay(), nativeCloseButtonDelay);
    }

    @Test
    public void testGetNativeCloseButtonDelay_remoteConfigDelayGreaterThanNATIVE_CLOSE_BUTTON_DELAY() {
        Integer nativeCloseButtonDelay = getNativeCloseButtonDelay(SkipOffsetManager.getDefaultNativeCloseButtonDelay() + 1);
        assertEquals(SkipOffsetManager.getDefaultNativeCloseButtonDelay(), nativeCloseButtonDelay);
    }

    @Test
    public void testGetNativeCloseButtonDelay_validRemoteConfigDelay() {
        Integer nativeCloseButtonDelay = getNativeCloseButtonDelay(10);
        Integer desiredDelay = 10;
        assertEquals(desiredDelay, nativeCloseButtonDelay);
    }

    @Test
    public void testGetInterstitialHTMLSkipOffset_nullRemoteConfigSkipOffset() {
        Integer remoteConfigSkipOffset = null;
        Integer resultSkipOffset = getHTMLSkipOffset(remoteConfigSkipOffset, true);
        assertEquals(SkipOffsetManager.INTERSTITIAL_MRAID, (int) resultSkipOffset);
    }

    @Test
    public void testGetInterstitialHTMLSkipOffset_negativeRemoteConfigSkipOffset() {
        Integer remoteConfigSkipOffset = -1;
        Integer resultSkipOffset = getHTMLSkipOffset(remoteConfigSkipOffset, true);
        assertEquals(SkipOffsetManager.INTERSTITIAL_MRAID, (int) resultSkipOffset);
    }

    @Test
    public void testGetInterstitialHTMLSkipOffset_validRemoteConfigSkipOffset() {
        Integer remoteConfigSkipOffset = 25;
        Integer resultSkipOffset = getHTMLSkipOffset(remoteConfigSkipOffset, true);
        assertEquals(remoteConfigSkipOffset, resultSkipOffset);
    }

    @Test
    public void testGetRewardedSkipOffset_adParamsHasPrecedence() {
        // This test is refactored for clarity to confirm adParams has the highest precedence.
        Integer resultSkipOffset = getVideoSkipOffset(15, 12, 8, false, false);
        assertEquals(Integer.valueOf(8), resultSkipOffset);
    }

    @Test
    public void testGetRewardedSkipOffset_remoteConfigHasPrecedenceOverPublisher() {
        // This test confirms remoteConfig has precedence when adParams is null.
        Integer resultSkipOffset = getVideoSkipOffset(15, 12, null, false, false);
        assertEquals(Integer.valueOf(15), resultSkipOffset);
    }

    @Test
    public void testGetRewardedSkipOffset_allNullValues() {
        Integer remoteConfigSkipOffset = null;
        Integer publisherSkipSeconds = null;
        Integer adParamsSkipSeconds = null;
        Integer defaultRewardedSkipOffset = getMaximumRewardedSkipOffset();
        Integer resultSkipOffset = getVideoSkipOffset(remoteConfigSkipOffset, publisherSkipSeconds, adParamsSkipSeconds, false, false);
        assertEquals(defaultRewardedSkipOffset, resultSkipOffset);
    }

    @Test
    public void testGetRewardedHTMLSkipOffset_nullRemoteConfigSkipOffset() {
        Integer remoteConfigSkipOffset = null;
        Integer defaultRewardedSkipOffset = SkipOffsetManager.getDefaultRewardedHtmlSkipOffset();
        Integer resultSkipOffset = getHTMLSkipOffset(remoteConfigSkipOffset, false);
        assertEquals(defaultRewardedSkipOffset, resultSkipOffset);
    }

    @Test
    public void testGetRewardedHTMLSkipOffset_negativeRemoteConfigSkipOffset() {
        Integer remoteConfigSkipOffset = -1;
        Integer defaultRewardedSkipOffset = SkipOffsetManager.getDefaultRewardedHtmlSkipOffset();
        Integer resultSkipOffset = getHTMLSkipOffset(remoteConfigSkipOffset, false);
        assertEquals(defaultRewardedSkipOffset, resultSkipOffset);
    }

    @Test
    public void testGetRewardedHTMLSkipOffset_remoteConfigSkipOffsetGreaterThanREWARDED_HTML_SKIP_OFFSET() {
        Integer remoteConfigSkipOffset = 35;
        Integer expectedSkipOffset = 30;
        Integer resultSkipOffset = getHTMLSkipOffset(remoteConfigSkipOffset, false);
        assertEquals(expectedSkipOffset, resultSkipOffset);
    }

    @Test
    public void testGetRewardedHTMLSkipOffset_validRemoteConfigSkipOffset() {
        Integer remoteConfigSkipOffset = 22;
        Integer resultSkipOffset = getHTMLSkipOffset(remoteConfigSkipOffset, false);
        assertEquals(remoteConfigSkipOffset, resultSkipOffset);
    }

    // --- NEW TESTS (ADDED FOR COVERAGE) ---

    @Test
    public void getVideoSkipOffset_forInterstitialWithEndCard_usesCorrectDefault() {
        // Test interstitial video with an end card, expecting a default of 10.
        Integer result = getVideoSkipOffset(null, null, null, true, true);
        assertEquals(Integer.valueOf(10), result);
    }

    @Test
    public void getVideoSkipOffset_forInterstitialWithoutEndCard_usesCorrectDefault() {
        // Test interstitial video without an end card, expecting a default of 15.
        Integer result = getVideoSkipOffset(null, null, null, false, true);
        assertEquals(Integer.valueOf(15), result);
    }

    @Test
    public void getHTMLSkipOffset_forInterstitialWithValueGreaterThanMax_returnsCappedValue() {
        // Test the capping logic for values > 30.
        Integer result = getHTMLSkipOffset(40, true);
        assertEquals(Integer.valueOf(30), result);
    }

    @Test
    public void isCustomInterstitialHTMLSkipOffset_isSetCorrectly() {
        // Test that the static flag is correctly updated.
        assertFalse(SkipOffsetManager.isCustomInterstitialHTMLSkipOffset());
        getHTMLSkipOffset(5, true);
        assertTrue(SkipOffsetManager.isCustomInterstitialHTMLSkipOffset());
    }

    @Test
    public void isCustomInterstitialVideoSkipOffset_isSetCorrectly() {
        // Test that the static flag is correctly updated for video.
        assertFalse(SkipOffsetManager.isCustomInterstitialVideoSkipOffset());
        getVideoSkipOffset(20, null, null, true, true);
        assertTrue(SkipOffsetManager.isCustomInterstitialVideoSkipOffset());
    }

    @Test
    public void defaultGetters_returnCorrectConstants() {
        // Test all simple constant getters for 100% coverage.
        assertEquals(Integer.valueOf(15), SkipOffsetManager.getDefaultNativeCloseButtonDelay());
        assertEquals(Integer.valueOf(30), SkipOffsetManager.getDefaultRewardedHtmlSkipOffset());
        assertEquals(Integer.valueOf(3), SkipOffsetManager.getDefaultHtmlInterstitialSkipOffset());
        assertEquals(Integer.valueOf(10), SkipOffsetManager.getDefaultVideoWithEndCardSkipOffset());
        assertEquals(Integer.valueOf(15), SkipOffsetManager.getDefaultVideoWithoutEndCardSkipOffset());
        assertEquals(Integer.valueOf(4), SkipOffsetManager.getDefaultEndcardSkipOffset());
        assertEquals(Integer.valueOf(5), SkipOffsetManager.getDefaultPCEndcardSkipOffset());
        assertEquals(Integer.valueOf(0), SkipOffsetManager.getDefaultBCEndcardSkipOffset());
        assertEquals(Integer.valueOf(30), SkipOffsetManager.getMaximumEndcardCloseDelay());
        assertEquals(Integer.valueOf(3), SkipOffsetManager.getDefaultEndcardCloseDelay());
        assertEquals(Integer.valueOf(3), SkipOffsetManager.getDefaultPCHTMLSkipOffset());
        assertEquals(Integer.valueOf(30), SkipOffsetManager.getDefaultPCRewardedHTMLSkipOffset());
        assertEquals(30, SkipOffsetManager.getMaximumRewardedSkipOffset());
    }
}