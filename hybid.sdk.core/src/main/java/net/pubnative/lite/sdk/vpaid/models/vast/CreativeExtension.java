package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Text;

public class CreativeExtension {
    @Attribute
    private String type;

    @Text
    private String text;

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }
}
