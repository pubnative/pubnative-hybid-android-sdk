// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import static junit.framework.Assert.assertEquals;
import static net.pubnative.lite.sdk.utils.SkipOffsetManager.getDefaultRewardedVideoSkipOffset;
import static net.pubnative.lite.sdk.utils.SkipOffsetManager.getHTMLSkipOffset;
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
    public void getVideoSkipOffset_whenAllValid_remoteConfigIsSmallest_returnsRemoteConfig() {
        // Resolution is a true minimum, not first-valid-wins: adParams (20) is largest despite being checked first.
        Integer resultSkipOffset = getVideoSkipOffset(5, 12, 20, false, false);
        assertEquals(Integer.valueOf(5), resultSkipOffset);
    }

    @Test
    public void getVideoSkipOffset_whenAllValid_adParamsIsSmallest_returnsAdParams() {
        Integer resultSkipOffset = getVideoSkipOffset(20, 12, 5, false, false);
        assertEquals(Integer.valueOf(5), resultSkipOffset);
    }

    @Test
    public void getVideoSkipOffset_whenAdParamsAbsent_returnsMinOfRemoteConfigAndPublisher() {
        Integer resultSkipOffset = getVideoSkipOffset(15, 12, null, false, false);
        assertEquals(Integer.valueOf(12), resultSkipOffset);
    }

    @Test
    public void getVideoSkipOffset_whenAdParamsInvalid_excludedFromMinimum() {
        // A negative adParams value must not participate in the minimum, same as if it were absent.
        Integer resultSkipOffset = getVideoSkipOffset(20, null, -1, false, false);
        assertEquals(Integer.valueOf(20), resultSkipOffset);
    }

    @Test
    public void getVideoSkipOffset_whenZeroIsSmallest_returnsZero() {
        // Guards the -1 sentinel used internally to mean "no valid value yet": a genuine 0 must not be mistaken for it.
        Integer resultSkipOffset = getVideoSkipOffset(20, null, 0, false, false);
        assertEquals(Integer.valueOf(0), resultSkipOffset);
    }

    @Test
    public void getVideoSkipOffset_whenOnlyPublisherValid_returnsPublisherValue() {
        // publisherSkipSeconds is unreachable from any current production call site, but the resolution
        // function itself must still honour it as a valid candidate.
        Integer resultSkipOffset = getVideoSkipOffset(null, 18, null, false, false);
        assertEquals(Integer.valueOf(18), resultSkipOffset);
    }

    @Test
    public void testGetRewardedSkipOffset_allNullValues() {
        Integer remoteConfigSkipOffset = null;
        Integer publisherSkipSeconds = null;
        Integer adParamsSkipSeconds = null;
        Integer defaultRewardedSkipOffset = getDefaultRewardedVideoSkipOffset();
        Integer resultSkipOffset = getVideoSkipOffset(remoteConfigSkipOffset, publisherSkipSeconds, adParamsSkipSeconds, false, false);
        assertEquals(defaultRewardedSkipOffset, resultSkipOffset);
    }

    @Test
    public void getVideoSkipOffset_forRewarded_negativeRemoteConfig_fallsBackToDefault() {
        Integer resultSkipOffset = getVideoSkipOffset(-1, null, null, false, false);
        assertEquals(Integer.valueOf(getDefaultRewardedVideoSkipOffset()), resultSkipOffset);
    }

    @Test
    public void getVideoSkipOffset_forRewarded_remoteConfigAboveOldCap_isHonoured() {
        Integer remoteConfigSkipOffset = 75;
        Integer resultSkipOffset = getVideoSkipOffset(remoteConfigSkipOffset, null, null, false, false);
        assertEquals(remoteConfigSkipOffset, resultSkipOffset);
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
    public void testGetRewardedHTMLSkipOffset_remoteConfigSkipOffsetAboveOldCap_isHonoured() {
        Integer remoteConfigSkipOffset = 35;
        Integer resultSkipOffset = getHTMLSkipOffset(remoteConfigSkipOffset, false);
        assertEquals(remoteConfigSkipOffset, resultSkipOffset);
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
    public void getVideoSkipOffset_forInterstitial_negativeRemoteConfig_fallsBackToDefault() {
        Integer result = getVideoSkipOffset(-1, null, null, false, true);
        assertEquals(Integer.valueOf(15), result);
    }

    @Test
    public void getVideoSkipOffset_forInterstitial_remoteConfigAboveOldCap_isHonoured() {
        Integer remoteConfigSkipOffset = 90;
        Integer result = getVideoSkipOffset(remoteConfigSkipOffset, null, null, false, true);
        assertEquals(remoteConfigSkipOffset, result);
    }

    @Test
    public void getVideoSkipOffset_forInterstitial_whenAdParamsAndRemoteConfigValid_returnsMinimum() {
        Integer result = getVideoSkipOffset(25, null, 10, false, true);
        assertEquals(Integer.valueOf(10), result);
    }

    @Test
    public void getHTMLSkipOffset_forInterstitialWithValueAboveOldCap_isHonoured() {
        Integer result = getHTMLSkipOffset(40, true);
        assertEquals(Integer.valueOf(40), result);
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
        assertEquals(Integer.valueOf(3), SkipOffsetManager.getDefaultEndcardCloseDelay());
        assertEquals(Integer.valueOf(3), SkipOffsetManager.getDefaultPCHTMLSkipOffset());
        assertEquals(Integer.valueOf(30), SkipOffsetManager.getDefaultPCRewardedHTMLSkipOffset());
        assertEquals(30, SkipOffsetManager.getDefaultRewardedVideoSkipOffset());
    }
}