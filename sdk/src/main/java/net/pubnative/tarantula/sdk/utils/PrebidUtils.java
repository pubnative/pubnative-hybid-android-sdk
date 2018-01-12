package net.pubnative.tarantula.sdk.utils;

import net.pubnative.tarantula.sdk.models.Ad;

/**
 * Created by erosgarciaponte on 11.01.18.
 */

public final class PrebidUtils {
    // Sample:
    // "m_max:true,max_adunit:ag9zfm1heGFkcy0xNTY1MTlyEwsSBkFkVW5pdBiAgICAvKGCCQw,max_bidX:000,max_bidXX:000,max_bidXXX:003"

    public interface KEYS {
        String PN = "m_pn";
        String PN_ZONE_ID = "pn_zone";
        String PN_BID_X = "pn_bidX";
        String PN_BID_XX = "pn_bidXX";
        String PN_BID_XXX = "pn_bidXXX";
    }

    public static String getPrebidKeywords(Ad ad) {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.toString();
    }


}
