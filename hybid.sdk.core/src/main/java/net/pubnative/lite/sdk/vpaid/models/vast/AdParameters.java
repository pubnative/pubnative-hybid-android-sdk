package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Text;

public class AdParameters {
    @Attribute
    private String xmlEncoded;

    @Text
    private String text;

    public String getXmlEncoded() {
        return xmlEncoded;
    }

    public String getText() {
        return text;
    }

}
