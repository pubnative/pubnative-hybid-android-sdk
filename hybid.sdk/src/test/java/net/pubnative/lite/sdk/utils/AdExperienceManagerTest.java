// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import net.pubnative.lite.sdk.models.AdExperience;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import org.junit.Test;

import static org.junit.Assert.*;

public class AdExperienceManagerTest {
    // isBrandAd tests
    @Test
    public void testIsBrandAd_True() {
        assertTrue(AdExperienceManager.isBrandAd(ApiAssetGroupType.VAST_INTERSTITIAL, AdExperience.BRAND));
    }

    @Test
    public void testIsBrandAd_True_CaseInsensitive() {
        assertTrue(AdExperienceManager.isBrandAd(ApiAssetGroupType.VAST_INTERSTITIAL, "BrAnD"));
    }

    @Test
    public void testIsBrandAd_False_WrongExperience() {
        assertFalse(AdExperienceManager.isBrandAd(ApiAssetGroupType.VAST_INTERSTITIAL, AdExperience.PERFORMANCE));
    }

    @Test
    public void testIsBrandAd_False_WrongAssetGroup() {
        assertFalse(AdExperienceManager.isBrandAd(ApiAssetGroupType.MRAID_320x480, AdExperience.BRAND));
    }

    @Test(expected = NullPointerException.class)
    public void testIsBrandAd_NullAssetGroup() {
        AdExperienceManager.isBrandAd(null, AdExperience.BRAND);
    }

    @Test(expected = NullPointerException.class)
    public void testIsBrandAd_NullExperience() {
        AdExperienceManager.isBrandAd(ApiAssetGroupType.VAST_INTERSTITIAL, null);
    }

    // isPerformanceAd tests
    @Test
    public void testIsPerformanceAd_True_VAST_INTERSTITIAL() {
        assertTrue(AdExperienceManager.isPerformanceAd(ApiAssetGroupType.VAST_INTERSTITIAL, AdExperience.PERFORMANCE));
    }

    @Test
    public void testIsPerformanceAd_True_MRAID_320x480() {
        assertTrue(AdExperienceManager.isPerformanceAd(ApiAssetGroupType.MRAID_320x480, AdExperience.PERFORMANCE));
    }

    @Test
    public void testIsPerformanceAd_True_MRAID_480x320() {
        assertTrue(AdExperienceManager.isPerformanceAd(ApiAssetGroupType.MRAID_480x320, AdExperience.PERFORMANCE));
    }

    @Test
    public void testIsPerformanceAd_True_MRAID_768x1024() {
        assertTrue(AdExperienceManager.isPerformanceAd(ApiAssetGroupType.MRAID_768x1024, AdExperience.PERFORMANCE));
    }

    @Test
    public void testIsPerformanceAd_True_MRAID_1024x768() {
        assertTrue(AdExperienceManager.isPerformanceAd(ApiAssetGroupType.MRAID_1024x768, AdExperience.PERFORMANCE));
    }

    @Test
    public void testIsPerformanceAd_True_MRAID_300x600() {
        assertTrue(AdExperienceManager.isPerformanceAd(ApiAssetGroupType.MRAID_300x600, AdExperience.PERFORMANCE));
    }

    @Test
    public void testIsPerformanceAd_True_CaseInsensitive() {
        assertTrue(AdExperienceManager.isPerformanceAd(ApiAssetGroupType.VAST_INTERSTITIAL, "PERFORMANCE"));
    }

    @Test
    public void testIsPerformanceAd_False_WrongExperience() {
        assertFalse(AdExperienceManager.isPerformanceAd(ApiAssetGroupType.VAST_INTERSTITIAL, AdExperience.BRAND));
    }

    @Test
    public void testIsPerformanceAd_False_WrongAssetGroup() {
        assertFalse(AdExperienceManager.isPerformanceAd(4, AdExperience.PERFORMANCE)); // 4 is not compatible
    }

    @Test(expected = NullPointerException.class)
    public void testIsPerformanceAd_NullAssetGroup() {
        AdExperienceManager.isPerformanceAd(null, AdExperience.PERFORMANCE);
    }

    @Test(expected = NullPointerException.class)
    public void testIsPerformanceAd_NullExperience() {
        AdExperienceManager.isPerformanceAd(ApiAssetGroupType.VAST_INTERSTITIAL, null);
    }
}
