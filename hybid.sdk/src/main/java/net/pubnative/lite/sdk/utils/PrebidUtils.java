// The MIT License (MIT)
//
// Copyright (c) 2020 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.sdk.utils;

import android.os.Bundle;

import net.pubnative.lite.sdk.models.Ad;

import java.util.Set;

public class PrebidUtils {
    public enum KeywordMode {
        TWO_DECIMALS, THREE_DECIMALS
    }

    public static String getBidFromPoints(Integer points, PrebidUtils.KeywordMode mode) {
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
