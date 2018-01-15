package net.pubnative.tarantula.sdk.utils;

import net.pubnative.tarantula.sdk.models.Ad;

/**
 * Created by erosgarciaponte on 11.01.18.
 */

public final class PrebidUtils {

    public interface KEYS {
        String PN = "m_pn";
        String PN_ZONE_ID = "pn_zone_id";
        String PN_BID = "pn_bid";
    }

    public static String getPrebidKeywords(Ad ad, String zoneId) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(KEYS.PN).append(':').append("true").append(',');
        stringBuilder.append(KEYS.PN_ZONE_ID).append(':').append(zoneId).append(',');

        String reversedECPM = new StringBuilder(ad.getECPM()).reverse().toString();
        StringBuilder bidECPM = new StringBuilder("0000").replace(0, reversedECPM.length(), reversedECPM).reverse();

        stringBuilder.append(KEYS.PN_BID).append(':').append(bidECPM);

        return stringBuilder.toString();
    }
}
