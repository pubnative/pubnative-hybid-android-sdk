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

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by erosgarciaponte on 11.01.18.
 */

public class HeaderBiddingUtils {
    public enum KeywordMode {
        TWO_DECIMALS, THREE_DECIMALS
    }

    private static final double ECPM_POINTS_DIVIDER = 1000.0;

    public interface KEYS {
        String PN_BID = "pn_bid";
    }

    private static String getBidECPM(Ad ad, KeywordMode mode) {
        Double eCPM = ad.getECPM().doubleValue() / ECPM_POINTS_DIVIDER;
        String formatString = "%.3f";

        if (mode == KeywordMode.TWO_DECIMALS) {
            formatString = "%.2f";
        }

        return String.format(Locale.ENGLISH, formatString, eCPM);
    }

    public static String getBidFromPoints(Integer points, PrebidUtils.KeywordMode mode) {
        Double eCPM = points.doubleValue() / ECPM_POINTS_DIVIDER;
        String formatString = "%.3f";

        if (mode == PrebidUtils.KeywordMode.TWO_DECIMALS) {
            formatString = "%.2f";
        }

        return String.format(Locale.ENGLISH, formatString, eCPM);
    }

    //---------------------------------- String keywords -------------------------------------------
    public static String getHeaderBiddingKeywords(Ad ad) {
        return getHeaderBiddingKeywords(ad, "");
    }

    public static String getHeaderBiddingKeywords(Ad ad, KeywordMode mode) {
        return getHeaderBiddingKeywords(ad, "", mode);
    }

    public static String getHeaderBiddingKeywords(Ad ad, String zoneId) {
        return getHeaderBiddingKeywords(ad, zoneId, KeywordMode.THREE_DECIMALS);
    }

    public static String getHeaderBiddingKeywords(Ad ad, String zoneId, KeywordMode mode) {

        return KEYS.PN_BID + ':' + getBidECPM(ad, mode);
    }

    //---------------------------------- Bundle keywords -------------------------------------------
    public static Bundle getHeaderBiddingKeywordsBundle(Ad ad) {
        return getHeaderBiddingKeywordsBundle(ad, "");
    }

    public static Bundle getHeaderBiddingKeywordsBundle(Ad ad, KeywordMode mode) {
        return getHeaderBiddingKeywordsBundle(ad, "", mode);
    }

    public static Bundle getHeaderBiddingKeywordsBundle(Ad ad, String zoneid) {
        return getHeaderBiddingKeywordsBundle(ad, zoneid, KeywordMode.THREE_DECIMALS);
    }

    public static Bundle getHeaderBiddingKeywordsBundle(Ad ad, String zoneid, KeywordMode mode) {
        Bundle bundle = new Bundle();

        bundle.putString(KEYS.PN_BID, getBidECPM(ad, mode));

        return bundle;
    }

    //------------------------------------ Set keywords --------------------------------------------
    public static Set<String> getHeaderBiddingKeywordsSet(Ad ad) {
        return getHeaderBiddingKeywordsSet(ad, "");
    }

    public static Set<String> getHeaderBiddingKeywordsSet(Ad ad, KeywordMode mode) {
        return getHeaderBiddingKeywordsSet(ad, "", mode);
    }

    public static Set<String> getHeaderBiddingKeywordsSet(Ad ad, String zoneid) {
        return getHeaderBiddingKeywordsSet(ad, zoneid, KeywordMode.THREE_DECIMALS);
    }

    public static Set<String> getHeaderBiddingKeywordsSet(Ad ad, String zoneid, KeywordMode mode) {
        Set<String> set = new LinkedHashSet<>(3);

        set.add(KEYS.PN_BID.concat(":").concat(getBidECPM(ad, mode)));

        return set;
    }
}
