package net.pubnative.lite.sdk.utils;

import android.os.Bundle;

import net.pubnative.lite.sdk.models.Ad;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by erosgarciaponte on 11.01.18.
 */

public final class PrebidUtils {
    private static final double ECPM_POINTS_DIVIDER = 1000.0;

    public interface KEYS {
        String PN = "m_pn";
        String PN_ZONE_ID = "pn_zone_id";
        String PN_BID = "pn_bid";
    }

    public static String getPrebidKeywords(Ad ad, String zoneId) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(KEYS.PN).append(':').append("true").append(',');
        stringBuilder.append(KEYS.PN_ZONE_ID).append(':').append(zoneId).append(',');
        stringBuilder.append(KEYS.PN_BID).append(':').append(getBidECPM(ad));

        return stringBuilder.toString();
    }

    public static Bundle getPrebidKeywordsBundle(Ad ad, String zoneid) {
        Bundle bundle = new Bundle();

        bundle.putBoolean(KEYS.PN, true);
        bundle.putString(KEYS.PN_ZONE_ID, zoneid);
        bundle.putString(KEYS.PN_BID, getBidECPM(ad));

        return bundle;
    }

    public static Set<String> getPrebidKeywordsSet(Ad ad, String zoneid) {
        Set<String> set = new LinkedHashSet<>(3);

        set.add(KEYS.PN.concat(":true"));
        set.add(KEYS.PN_ZONE_ID.concat(":").concat(zoneid));
        set.add(KEYS.PN_BID.concat(":").concat(getBidECPM(ad)));

        return set;
    }

    private static String getBidECPM(Ad ad) {
        Double eCPM = ad.getECPM().doubleValue() / ECPM_POINTS_DIVIDER;
        return String.format(Locale.ENGLISH, "%.3f", eCPM);
    }
}
