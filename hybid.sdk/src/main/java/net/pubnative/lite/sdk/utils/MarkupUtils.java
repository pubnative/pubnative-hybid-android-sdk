package net.pubnative.lite.sdk.utils;

import net.pubnative.lite.sdk.vpaid.models.vast.Vast;
import net.pubnative.lite.sdk.vpaid.xml.XmlParser;

public class MarkupUtils {
    public static boolean isVastXml(String adValue) {
        try {
            Vast vast = XmlParser.parse(adValue, Vast.class);
            return vast != null;
        } catch (Exception e) {
            return false;
        }
    }
}
