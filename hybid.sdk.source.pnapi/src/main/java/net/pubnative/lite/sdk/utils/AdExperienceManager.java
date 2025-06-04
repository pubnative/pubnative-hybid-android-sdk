// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import net.pubnative.lite.sdk.models.AdExperience;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;

public class AdExperienceManager {

    private static boolean isBrandCompatible(Integer assetgroupid) {
        return assetgroupid == ApiAssetGroupType.VAST_INTERSTITIAL;
    }

    private static boolean isPerformanceCompatible(Integer assetgroupid) {
        return assetgroupid == ApiAssetGroupType.VAST_INTERSTITIAL || assetgroupid == ApiAssetGroupType.MRAID_320x480
                || assetgroupid == ApiAssetGroupType.MRAID_480x320 || assetgroupid == ApiAssetGroupType.MRAID_768x1024
                || assetgroupid == ApiAssetGroupType.MRAID_1024x768 || assetgroupid == ApiAssetGroupType.MRAID_300x600;
    }

    public static boolean isBrandAd(Integer assetGroupId, String adExperience) {
        return isBrandCompatible(assetGroupId) && adExperience.equalsIgnoreCase(AdExperience.BRAND);
    }

    public static boolean isPerformanceAd(Integer assetGroupId, String adExperience) {
        return isPerformanceCompatible(assetGroupId) && adExperience.equalsIgnoreCase(AdExperience.PERFORMANCE);
    }

}
