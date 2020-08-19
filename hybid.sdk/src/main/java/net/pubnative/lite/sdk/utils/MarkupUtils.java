package net.pubnative.lite.sdk.utils;

public class MarkupUtils {
    public static boolean isVastXml(String adValue) {
        return adValue.matches("(<VAST[\\s\\S]*?>)[\\s\\S]*<\\/VAST>") || adValue.matches("(<vast[\\s\\S]*?>)[\\s\\S]*<\\/vast>");
    }
}
