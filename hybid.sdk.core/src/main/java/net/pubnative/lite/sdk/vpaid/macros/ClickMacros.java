package net.pubnative.lite.sdk.vpaid.macros;

public class ClickMacros {
    private static final String MACRO_CLICK_POS = "[CLICKPOS]";

    public String processUrl(String url) {
        return url;
    }

    private static String getClickPos() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }
}
