// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.os.Bundle;

import net.pubnative.lite.sdk.models.Ad;

import java.util.Set;

public class PrebidUtils {
    public enum KeywordMode {
        TWO_DECIMALS, THREE_DECIMALS
    }

    public static String getBidFromPoints(Integer points, KeywordMode mode) {
        return HeaderBiddingUtils.getBidFromPoints(points, mode);
    }

    //---------------------------------- String keywords -------------------------------------------
    public static String getPrebidKeywords(Ad ad) {
        return HeaderBiddingUtils.getHeaderBiddingKeywords(ad);
    }

    public static String getPrebidKeywords(Ad ad, KeywordMode mode) {
        return HeaderBiddingUtils.getHeaderBiddingKeywords(ad, mapKeywordMode(mode));
    }

    public static String getPrebidKeywords(Ad ad, String zoneId) {
        return HeaderBiddingUtils.getHeaderBiddingKeywords(ad, zoneId);
    }

    public static String getPrebidKeywords(Ad ad, String zoneId, KeywordMode mode) {
        return HeaderBiddingUtils.getHeaderBiddingKeywords(ad, zoneId, mapKeywordMode(mode));
    }

    //---------------------------------- Bundle keywords -------------------------------------------
    public static Bundle getPrebidKeywordsBundle(Ad ad) {
        return HeaderBiddingUtils.getHeaderBiddingKeywordsBundle(ad);
    }

    public static Bundle getPrebidKeywordsBundle(Ad ad, KeywordMode mode) {
        return HeaderBiddingUtils.getHeaderBiddingKeywordsBundle(ad, mapKeywordMode(mode));
    }

    public static Bundle getPrebidKeywordsBundle(Ad ad, String zoneid) {
        return HeaderBiddingUtils.getHeaderBiddingKeywordsBundle(ad, zoneid);
    }

    public static Bundle getPrebidKeywordsBundle(Ad ad, String zoneid, KeywordMode mode) {
        return HeaderBiddingUtils.getHeaderBiddingKeywordsBundle(ad, zoneid, mapKeywordMode(mode));
    }

    //------------------------------------ Set keywords --------------------------------------------
    public static Set<String> getPrebidKeywordsSet(Ad ad) {
        return HeaderBiddingUtils.getHeaderBiddingKeywordsSet(ad);
    }

    public static Set<String> getPrebidKeywordsSet(Ad ad, KeywordMode mode) {
        return HeaderBiddingUtils.getHeaderBiddingKeywordsSet(ad, mapKeywordMode(mode));
    }

    public static Set<String> getPrebidKeywordsSet(Ad ad, String zoneid) {
        return HeaderBiddingUtils.getHeaderBiddingKeywordsSet(ad, zoneid);
    }

    public static Set<String> getPrebidKeywordsSet(Ad ad, String zoneid, KeywordMode mode) {
        return HeaderBiddingUtils.getHeaderBiddingKeywordsSet(ad, zoneid, mapKeywordMode(mode));
    }

    /*
     * This method is added for backward compatibility in the SDK
     */
    private static HeaderBiddingUtils.KeywordMode mapKeywordMode(KeywordMode mode) {
        if (mode == KeywordMode.TWO_DECIMALS) {
            return HeaderBiddingUtils.KeywordMode.TWO_DECIMALS;
        }
        return HeaderBiddingUtils.KeywordMode.THREE_DECIMALS;
    }
}
