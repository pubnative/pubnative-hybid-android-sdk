package net.pubnative.lite.demo;

import java.util.Arrays;
import java.util.List;

/**
 * Created by erosgarciaponte on 11.01.18.
 */

public class Constants {
    public static final String BANNER_MRAID_ZONE_ID = "2";
    public static final String INTERSTITIAL_MRAID_ZONE_ID = "3";
    public static final String MEDIUM_MRAID_ZONE_ID = "5";
    public static final String INTERSTITIAL_VIDEO_ZONE_ID = "4";
    public static final String MEDIUM_VIDEO_ZONE_ID = "6";

    public static final String MOPUB_MRAID_BANNER_AD_UNIT = "b8b82260e1b84a9ba361e03c21ce4caf";
    public static final String MOPUB_MRAID_INTERSTITIAL_AD_UNIT = "0bd7ea20185547f2bd29a9574bfce917";
    public static final String MOPUB_MRAID_MEDIUM_AD_UNIT = "1fafef6b872a4e10ba9fc573ca347e55";

    public static final String DFP_MRAID_BANNER_AD_UNIT = "/219576711/pnlite_dfp_banner";
    public static final String DFP_MRAID_INTERSTITIAL_AD_UNIT = "/219576711/pnlite_dfp_interstitial";
    public static final String DFP_MRAID_MEDIUM_AD_UNIT = "/219576711/pnlite_dfp_mrect";

    public static final String APP_TOKEN = "dde3c298b47648459f8ada4a982fa92d";
    public static final List<String> ZONE_ID_LIST = Arrays.asList(BANNER_MRAID_ZONE_ID, MEDIUM_MRAID_ZONE_ID, INTERSTITIAL_MRAID_ZONE_ID, INTERSTITIAL_VIDEO_ZONE_ID);

    public final class IntentParams {
        public static final String ZONE_ID = "zone_id";
    }
}
