package net.pubnative.lite.sdk.vpaid.macros;

public class AdBreakMacros {
    private static final String MACRO_CONTENT_PLAY_HEAD = "[CONTENTPLAYHEAD]";
    private static final String MACRO_MEDIA_PLAY_HEAD = "[MEDIAPLAYHEAD]";
    private static final String MACRO_BREAK_POSITION = "[BREAKPOSITION]";
    private static final String MACRO_BLOCKED_AD_CATEGORIES = "[BLOCKEDADCATEGORIES]";
    private static final String MACRO_AD_CATEGORIES = "[ADCATEGORIES]";
    private static final String MACRO_AD_COUNT = "[ADCOUNT]";
    private static final String MACRO_TRANSACTION_ID = "[TRANSACTIONID]";
    private static final String MACRO_PLACEMENT_TYPE = "[PLACEMENTTYPE]";
    private static final String MACRO_AD_TYPE = "[ADTYPE]";
    private static final String MACRO_UNIVERSAL_AD_ID = "[UNIVERSALADID]";
    private static final String MACRO_BREAK_MAX_DURATION = "[BREAKMAXDURATION]";
    private static final String MACRO_BREAK_MIN_DURATION = "[BREAKMINDURATION]";
    private static final String MACRO_BREAK_MAX_ADS = "[BREAKMAXADS]";
    private static final String MACRO_BREAK_MIN_AD_LENGTH = "[BREAKMINADLENGTH]";
    private static final String MACRO_BREAK_MAX_AD_LENGTH = "[BREAKMAXADLENGTH]";

    public String processUrl(String url) {
        return url;
    }

    private String getContentPlayHead() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private String getMediaPlayHead() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private String getBreakPosition() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private String getBlockedAdCategories() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private String getAdCategories() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private String getAdCount() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private String getTransactionId() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private String getPlacementType() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private String getAdType() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private String getUniversalAdId() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private String getBreakMaxDuration() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private String getBreakMinDuration() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private String getBreakMaxAds() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private String getBreakMinLength() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private String getBreakMaxLength() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }
}
