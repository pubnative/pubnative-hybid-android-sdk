// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
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
