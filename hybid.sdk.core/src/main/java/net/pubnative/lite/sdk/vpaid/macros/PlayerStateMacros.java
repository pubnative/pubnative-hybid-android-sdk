package net.pubnative.lite.sdk.vpaid.macros;

public class PlayerStateMacros {
    private static final String MACRO_PLAYER_STATE = "[PLAYERSTATE]";
    private static final String MACRO_INVENTORY_STATE = "[INVENTORYSTATE]";
    private static final String MACRO_PLAYER_SIZE = "[PLAYERSIZE]";
    private static final String MACRO_AD_PLAY_HEAD = "[ADPLAYHEAD]";
    private static final String MACRO_ASSET_URI = "[ASSETURI]";
    private static final String MACRO_CONTENT_ID = "[CONTENTID]";
    private static final String MACRO_CONTENT_URI = "[CONTENTURI]";
    private static final String MACRO_POD_SEQUENCE = "[PODSEQUENCE]";
    private static final String MACRO_AD_SERVINGID = "[ADSERVINGID]";

    public String processUrl(String url) {
        return url;
    }

    private static String getPlayerState() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private static String getInventoryState() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private static String getPlayerSize() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private static String getAdPlayHead() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private static String getAssetUri() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private static String getContentId() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private static String getContentUri() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private static int getPodSequence() {
        return MacroDefaultValues.VALUE_UNKNOWN;
    }

    private static String getAdServingId() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }
}
