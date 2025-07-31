// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.models.request.Imp;


/**
 * Created by erosgarciaponte on 22.01.18.
 */

public final class OpenRTBAssetsGroup {

    public static Integer get(Imp imp, Integer width, Integer height, Ad.AdType adType, boolean isInterstitial) {
        if (adType == Ad.AdType.VIDEO) {
            return getVastGroupId(isInterstitial);
        } else {
            return getBannerGroupId(isInterstitial, imp, width, height);
        }
    }

    private static int getBannerGroupId(boolean isInterstitial, Imp adRequest, Integer customWidth, Integer customHeight) {

        Integer width;
        Integer height;
        if (adRequest != null) {
            width = adRequest.getBanner().getW();
            height = adRequest.getBanner().getH();
        } else if (customWidth != null && customHeight != null) {
            width = customWidth;
            height = customHeight;
        } else {
            return ApiAssetGroupType.MRAID_320x50;
        }

        if (isInterstitial) {
            return ApiAssetGroupType.MRAID_320x480;
        }

        int assetGroupId = ApiAssetGroupType.MRAID_320x50;

        if (width == 300 && height == 50)
            assetGroupId = ApiAssetGroupType.MRAID_300x50;
        if (width == 300 && height == 250)
            assetGroupId = ApiAssetGroupType.MRAID_300x250;
        if (width == 320 && height == 480)
            assetGroupId = ApiAssetGroupType.MRAID_320x480;
        if (width == 1024 && height == 768)
            assetGroupId = ApiAssetGroupType.MRAID_1024x768;
        if (width == 768 && height == 1024)
            assetGroupId = ApiAssetGroupType.MRAID_768x1024;
        if (width == 728 && height == 98)
            assetGroupId = ApiAssetGroupType.MRAID_728x90;
        if (width == 160 && height == 600)
            assetGroupId = ApiAssetGroupType.MRAID_160x600;
        if (width == 250 && height == 250)
            assetGroupId = ApiAssetGroupType.MRAID_250x250;
        if (width == 300 && height == 600)
            assetGroupId = ApiAssetGroupType.MRAID_300x600;
        if (width == 320 && height == 100)
            assetGroupId = ApiAssetGroupType.MRAID_320x100;
        if (width == 480 && height == 320)
            assetGroupId = ApiAssetGroupType.MRAID_480x320;

        return assetGroupId;
    }

    private static Integer getVastGroupId(boolean isInterstitial) {
        if (isInterstitial) {
            return ApiAssetGroupType.VAST_INTERSTITIAL;
        } else {
            return ApiAssetGroupType.VAST_MRECT;
        }
    }
}