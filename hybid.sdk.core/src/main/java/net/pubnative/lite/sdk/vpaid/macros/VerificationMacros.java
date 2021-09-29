package net.pubnative.lite.sdk.vpaid.macros;

public class VerificationMacros {
    private static final String MACRO_REASON = "[REASON]";

    public String processUrl(String url) {
        return url;
    }

    private static int getReason() {
        return MacroDefaultValues.VALUE_UNKNOWN;
    }
}
