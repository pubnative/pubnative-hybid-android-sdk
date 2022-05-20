package net.pubnative.lite.sdk.utils;

import net.pubnative.lite.sdk.vpaid.models.vast.Vast;
import net.pubnative.lite.sdk.vpaid.xml.XmlParser;

public class MarkupUtils {
    public static boolean isVastXml(String adValue) {
        try {
            Vast vast = XmlParser.parse(adValue, Vast.class);
            if (vast != null) {
                return vast.getAds() != null || vast.getErrors() != null || vast.getStatus() != null || vast.getVersion() != null;
            } else {
                return false;
            }
        } catch (Exception e) {
            Logger.e("MarkupUtils", e.getMessage());
            return false;
        }
    }
}
