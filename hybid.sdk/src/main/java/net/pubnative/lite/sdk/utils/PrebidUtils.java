package net.pubnative.lite.sdk.utils;

import android.os.Bundle;

import net.pubnative.lite.sdk.models.Ad;

import java.util.LinkedHashSet;
import java.util.Set;

public class PrebidUtils extends HeaderBiddingUtils {
    public enum KeywordMode {
        TWO_DECIMALS, THREE_DECIMALS
    }

    //---------------------------------- String keywords -------------------------------------------
    public static String getPrebidKeywords(Ad ad, KeywordMode mode) {
        return getPrebidKeywords(ad, "", mapKeywordMode(mode));
    }

    public static String getPrebidKeywords(Ad ad, String zoneId, KeywordMode mode) {
        return getPrebidKeywords(ad, zoneId, mapKeywordMode(mode));
    }

    //---------------------------------- Bundle keywords -------------------------------------------

    public static Bundle getPrebidKeywordsBundle(Ad ad, KeywordMode mode) {
        return getPrebidKeywordsBundle(ad, "", mapKeywordMode(mode));
    }

    public static Bundle getPrebidKeywordsBundle(Ad ad, String zoneId, KeywordMode mode) {
        return getPrebidKeywordsBundle(ad, zoneId, mapKeywordMode(mode));
    }

    //------------------------------------ Set keywords --------------------------------------------
    public static Set<String> getPrebidKeywordsSet(Ad ad, KeywordMode mode) {
        return getPrebidKeywordsSet(ad, "", mapKeywordMode(mode));
    }

    public static Set<String> getPrebidKeywordsSet(Ad ad, String zoneId, KeywordMode mode) {
        return getPrebidKeywordsSet(ad, zoneId, mapKeywordMode(mode));
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
