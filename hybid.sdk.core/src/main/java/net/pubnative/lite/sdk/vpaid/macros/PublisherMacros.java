package net.pubnative.lite.sdk.vpaid.macros;

public class PublisherMacros {
    protected static final String MACRO_DOMAIN = "[DOMAIN]";
    protected static final String MACRO_PAGE_URL = "[PAGEURL]";
    protected static final String MACRO_APP_BUNDLE = "[APPBUNDLE]";

    public String processUrl(String url) {
        return url;
    }

    private static String getDomain() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private static String getPageUrl() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private static String getAppBundle() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }
}
