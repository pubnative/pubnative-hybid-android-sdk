package net.pubnative.tarantula.sdk.utils;

import net.pubnative.tarantula.sdk.models.Ad;

import java.util.Locale;

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

        Double eCPM = ad.getECPM().doubleValue() / ECPM_POINTS_DIVIDER;
        String bidECPM = String.format(Locale.ENGLISH, "%.3f", eCPM);

        stringBuilder.append(KEYS.PN_BID).append(':').append(bidECPM);

        return stringBuilder.toString();
    }
}
